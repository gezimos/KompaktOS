package com.kompakt.service

import android.app.ActivityManager
import android.app.KeyguardManager
import android.app.ActivityManager.RunningTaskInfo
import android.app.ActivityTaskManager
import android.app.TaskStackListener
import android.app.WindowConfiguration
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.PowerManager
import android.os.SystemClock
import android.util.Log

/** Per-app e-ink modes, driven by the system's own task stack callback. */
object EinkAppWatcher {

    /** The app in front, for re-applying its mode after an unlock. */
    @Volatile
    var foreground: String? = null
        private set

    /** A burst of stack changes is one check. */
    private const val SETTLE_MS = 40L

    /** The new app gets this long to draw before the flash. */
    private const val REFRESH_DELAY_MS = 600L

    /** Two app-change flashes never come closer than this. */
    private const val REFRESH_GAP_MS = 2000L

    private var lastRefresh = 0L
    private val refreshNow = Runnable { refreshAfterSwitch() }

    private var registered = false
    private var last: String? = null
    @Volatile
    private var pending = false
    private lateinit var context: Context
    private var handler: Handler? = null

    @Synchronized
    fun start(c: Context) {
        if (registered) return
        context = c.applicationContext
        val h = handler ?: Handler(HandlerThread("eink-top-app").apply { start() }.looper)
            .also { handler = it }
        try {
            ActivityTaskManager.getInstance().registerTaskStackListener(object : TaskStackListener() {
                override fun onTaskStackChanged() = poke()
                override fun onTaskMovedToFront(taskInfo: RunningTaskInfo) = poke()
            })
            registered = true
            Log.i(Sysfs.TAG, "eink: per-app watcher registered")
            h.post { check() }
        } catch (t: Throwable) {
            Log.e(Sysfs.TAG, "eink: cannot register the per-app watcher", t)
        }
    }

    /** The app in front now, read from the system rather than from the last event. */
    fun topNow(): String? = top()

    private fun poke() {
        if (pending) return
        pending = true
        handler?.postDelayed({ check() }, SETTLE_MS)
    }

    private fun check() {
        pending = false
        val pkg = top() ?: return
        if (pkg == last) return
        last = pkg
        foreground = pkg

        // A newer switch replaces a flash still waiting.
        handler?.removeCallbacks(refreshNow)
        if (Prefs.refreshOnApp(context)) handler?.postDelayed(refreshNow, REFRESH_DELAY_MS)

        AppInvert.apply(context, pkg)
        // Before the mode: a mode change repaints the whole screen, in the new pattern.
        Eink.usePattern(context, pkg)

        if (!Prefs.perAppEnabled(context)) return
        val mode = Prefs.appMode(context, pkg)
        // Skip an unchanged mode: writing waveform_mode forces a full repaint.
        if (mode == Eink.applied) {
            Log.i(Sysfs.TAG, "eink: $pkg -> $mode (unchanged)")
            return
        }
        Log.i(Sysfs.TAG, "eink: $pkg -> $mode")
        Eink.setMode(mode)
    }

    /** The app-change flash: only if that screen is still in front and the screen is on and unlocked. */
    private fun refreshAfterSwitch() {
        val now = SystemClock.elapsedRealtime()
        if (Flash.active || now - lastRefresh < REFRESH_GAP_MS) return
        val task = topTask() ?: return
        if (task.activityType == WindowConfiguration.ACTIVITY_TYPE_RECENTS ||
            task.topActivity?.packageName != last) return
        if (context.getSystemService(PowerManager::class.java)?.isInteractive != true) return
        if (context.getSystemService(KeyguardManager::class.java)?.isKeyguardLocked == true) return
        lastRefresh = now
        Log.i(Sysfs.TAG, "eink: app changed to $last, refreshing")
        Flash.show(context)
    }

    private fun topTask(): RunningTaskInfo? = try {
        context.getSystemService(ActivityManager::class.java)?.getRunningTasks(1)?.firstOrNull()
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "eink: top task unavailable (${t.javaClass.simpleName})")
        null
    }

    /** The top task's app; Recents draws over an app and is not one. */
    private fun top(): String? {
        val task = topTask()
        return if (task == null || task.activityType == WindowConfiguration.ACTIVITY_TYPE_RECENTS) null
        else task.topActivity?.packageName
    }
}
