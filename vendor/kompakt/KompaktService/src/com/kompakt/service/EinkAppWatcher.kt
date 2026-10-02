package com.kompakt.service

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

/** Per-app e-ink modes. */
class EinkAppWatcher : AccessibilityService() {

    private var lastPackage: String? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        Log.i(Sysfs.TAG, "eink: per-app watcher connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        // Windows belonging to the shade and to our own flash overlay are not app switches.
        if (pkg == "com.android.systemui" && lastPackage != null) return

        if (pkg == lastPackage) return
        lastPackage = pkg

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

    override fun onInterrupt() = Unit
}
