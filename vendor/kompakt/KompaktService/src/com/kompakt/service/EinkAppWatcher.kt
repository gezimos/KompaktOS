package com.kompakt.service

import android.accessibilityservice.AccessibilityService
import android.app.ActivityManager
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent

/** Per-app e-ink modes. */
class EinkAppWatcher : AccessibilityService() {

    companion object {
        /** The app in front, for re-applying its mode after an unlock; the watcher sees no switch then. */
        @Volatile
        var foreground: String? = null
            private set
    }

    private var lastPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(Sysfs.TAG, "eink: per-app watcher connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val eventPkg = event.packageName?.toString() ?: return

        // Windows belonging to the shade and to our own flash overlay are not app switches.
        if (eventPkg == "com.android.systemui" && lastPackage != null) return
        // Kompakt: the refresh flash is a bare View window of ours; the app under it never reports itself again.
        if (eventPkg == packageName && (Flash.active || event.className == View::class.java.name)) return

        // The app in front is the top activity: the keyboard's window, or a dialog of another app, goes over it without being an app switch.
        val pkg = topPackage() ?: eventPkg
        if (pkg != eventPkg) Log.i(Sysfs.TAG, "eink: window of $eventPkg, app in front $pkg")

        if (pkg == lastPackage) return
        lastPackage = pkg
        foreground = pkg

        AppInvert.apply(this, pkg)

        if (!Prefs.perAppEnabled(this)) return
        val mode = Prefs.appMode(this, pkg)
        // Skip an unchanged mode: writing waveform_mode forces a full repaint.
        if (mode == Eink.applied) {
            Log.i(Sysfs.TAG, "eink: $pkg -> $mode (unchanged)")
            return
        }
        Log.i(Sysfs.TAG, "eink: $pkg -> $mode")
        Eink.setMode(mode)
    }

    private fun topPackage(): String? = try {
        getSystemService(ActivityManager::class.java)
            ?.getRunningTasks(1)?.firstOrNull()?.topActivity?.packageName
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "eink: top activity unavailable (${t.javaClass.simpleName})")
        null
    }

    override fun onInterrupt() = Unit
}
