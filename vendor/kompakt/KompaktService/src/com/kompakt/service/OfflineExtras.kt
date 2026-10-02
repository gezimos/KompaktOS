package com.kompakt.service

import android.bluetooth.BluetoothManager
import android.content.Context
import android.hardware.SensorPrivacyManager
import android.net.wifi.WifiManager
import android.util.Log

/** What the offline switch turns off besides the modem. */
object OfflineExtras {

    enum class Extra(val key: String, val default: Boolean, val locked: Boolean) {
        WIFI("wifi", true, false),
        BLUETOOTH("bt", false, false),
        CAMERA("camera", false, false),
    }

    private fun prefs(c: Context) = c.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun enabled(c: Context, e: Extra): Boolean =
        e.locked || prefs(c).getBoolean("offline_" + e.key, e.default)

    /** Store the choice and apply it at once if the switch is already engaged. */
    fun setEnabled(c: Context, e: Extra, on: Boolean) {
        if (e.locked) return
        prefs(c).edit().putBoolean("offline_" + e.key, on).apply()
        val app = c.applicationContext
        Sysfs.onIo { apply(app, OfflineSwitch.engaged()) }
    }

    /** Bring every extra in line with the switch. Idempotent. Call off the main thread. */
    fun apply(context: Context, offline: Boolean) {
        val c = context.applicationContext
        for (e in Extra.values()) {
            if (offline && enabled(c, e)) turnOff(c, e) else restore(c, e)
        }
    }

    private fun markKey(e: Extra) = "offline_off_" + e.key

    private fun turnOff(c: Context, e: Extra) {
        if (prefs(c).getBoolean(markKey(e), false)) return
        if (!isOn(c, e)) return
        if (setOn(c, e, false)) {
            prefs(c).edit().putBoolean(markKey(e), true).apply()
            Log.i(Sysfs.TAG, "offline: ${e.key} off")
        }
    }

    private fun restore(c: Context, e: Extra) {
        if (!prefs(c).getBoolean(markKey(e), false)) return
        setOn(c, e, true)
        prefs(c).edit().putBoolean(markKey(e), false).apply()
        Log.i(Sysfs.TAG, "offline: ${e.key} restored")
    }

    private fun isOn(c: Context, e: Extra): Boolean = try {
        when (e) {
            Extra.WIFI -> c.getSystemService(WifiManager::class.java)?.isWifiEnabled == true
            Extra.BLUETOOTH ->
                c.getSystemService(BluetoothManager::class.java)?.adapter?.isEnabled == true
            Extra.CAMERA -> !privacy(c, SensorPrivacyManager.Sensors.CAMERA)
        }
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "offline: cannot read ${e.key} (${t.javaClass.simpleName})")
        false
    }

    @Suppress("DEPRECATION")
    private fun setOn(c: Context, e: Extra, on: Boolean): Boolean = try {
        when (e) {
            Extra.WIFI ->
                c.getSystemService(WifiManager::class.java)?.setWifiEnabled(on) == true
            Extra.BLUETOOTH -> {
                val adapter = c.getSystemService(BluetoothManager::class.java)?.adapter
                (if (on) adapter?.enable() else adapter?.disable()) == true
            }
            Extra.CAMERA -> setPrivacy(c, SensorPrivacyManager.Sensors.CAMERA, !on)
        }
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "offline: cannot switch ${e.key} (${t.javaClass.simpleName})")
        false
    }

    private fun privacy(c: Context, sensor: Int): Boolean =
        c.getSystemService(SensorPrivacyManager::class.java)
            ?.isSensorPrivacyEnabled(SensorPrivacyManager.TOGGLE_TYPE_SOFTWARE, sensor) == true

    /** True only if the block really changed: the service ignores unsupported sensors. */
    private fun setPrivacy(c: Context, sensor: Int, blocked: Boolean): Boolean {
        val spm = c.getSystemService(SensorPrivacyManager::class.java) ?: return false
        spm.setSensorPrivacy(sensor, blocked)
        return privacy(c, sensor) == blocked
    }
}
