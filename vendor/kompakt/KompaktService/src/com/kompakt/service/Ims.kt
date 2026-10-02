package com.kompakt.service

import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SubscriptionManager
import android.telephony.ims.ImsMmTelManager
import android.telephony.ims.RegistrationManager
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/** Calls over 4G for Diagnostics: IMS app, 4G Calling switch, carrier registration. */
object Ims {
    const val PACKAGE = "com.mediatek.ims"

    fun installed(c: Context): Boolean = try {
        c.packageManager.getPackageInfo(PACKAGE, 0)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    /** Rows for Diagnostics: the app, the switch, the registration. Blocks for up to two seconds. */
    fun diagnose(c: Context): List<Pair<String, String>> {
        val rows = ArrayList<Pair<String, String>>()
        rows.add(c.getString(R.string.diag_ims_app) to c.getString(
            if (installed(c)) R.string.diag_installed else R.string.diag_not_installed))
        val sub = SubscriptionManager.getDefaultVoiceSubscriptionId()
        if (!SubscriptionManager.isValidSubscriptionId(sub)) {
            rows.add(c.getString(R.string.diag_ims_switch) to c.getString(R.string.diag_no_sim))
            return rows
        }
        val mmtel = ImsMmTelManager.createForSubscriptionId(sub)
        val on = runCatching { mmtel.isAdvancedCallingSettingEnabled }.getOrNull()
        rows.add(c.getString(R.string.diag_ims_switch) to when (on) {
            true -> c.getString(R.string.diag_on)
            false -> c.getString(R.string.diag_off)
            null -> "?"
        })
        rows.add(c.getString(R.string.diag_ims_registered) to when (registration(mmtel)) {
            RegistrationManager.REGISTRATION_STATE_REGISTERED -> c.getString(R.string.diag_yes)
            RegistrationManager.REGISTRATION_STATE_REGISTERING -> c.getString(R.string.diag_ims_registering)
            RegistrationManager.REGISTRATION_STATE_NOT_REGISTERED -> c.getString(R.string.diag_no)
            else -> "?"
        })
        return rows
    }

    /** One of RegistrationManager's states, or -1 when it cannot be asked. */
    private fun registration(mmtel: ImsMmTelManager): Int {
        var state = -1
        val done = CountDownLatch(1)
        runCatching {
            mmtel.getRegistrationState(Runnable::run) { s ->
                state = s
                done.countDown()
            }
            done.await(2, TimeUnit.SECONDS)
        }
        return state
    }
}
