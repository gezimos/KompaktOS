package com.kompakt.service

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast

/** Switch slot 2 between the physical SIM tray and the soldered eUICC. */
class SimModeActivity : Activity() {

    private companion object {
        /** The muxed slot. Slot 0 is SIM 1 and has nothing to do with this. */
        const val SLOT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!ESim.available()) {
            toast(R.string.sim_mode_unavailable)
            finish()
            return
        }

        val current = ESim.state() ?: Prefs.card(this)
        val next = if (current == ESim.ESIM) ESim.SIM else ESim.ESIM

        // Gate here as well as in the Settings row.
        if (next == ESim.ESIM && traySimIsOn()) {
            AlertDialog.Builder(this)
                .setTitle(R.string.sim_mode_title)
                .setMessage(R.string.sim_mode_needs_sim_off)
                .setPositiveButton(android.R.string.ok, null)
                .setOnDismissListener { finish() }
                .show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.sim_mode_title)
            .setMessage(
                if (next == ESim.ESIM) getString(R.string.sim_mode_to_esim)
                else getString(R.string.sim_mode_to_sim)
            )
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(R.string.sim_mode_restart) { _, _ -> apply(next) }
            .setOnDismissListener { finish() }
            .show()
    }

    /** True while a card in the tray still holds slot 2. */
    private fun traySimIsOn(): Boolean {
        val sm = getSystemService(SubscriptionManager::class.java) ?: return false
        return try {
            sm.activeSubscriptionInfoList
                ?.any { it.simSlotIndex == SLOT && !it.isEmbedded } == true
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "cannot read subscriptions (${t.javaClass.simpleName})")
            false
        }
    }

    /** Write the mux and restart. */
    private fun apply(next: String) {
        val power = getSystemService(Context.POWER_SERVICE) as PowerManager
        Sysfs.onIo {
            val ok = ESim.selectBlocking(next)
            Log.i(Sysfs.TAG, "sim mux -> $next (ok=$ok)")
            if (ok) {
                Prefs.setCard(this@SimModeActivity, next)
                // Picked up by OfflineWatcherService after the reboot.
                Prefs.setPendingEsim(this@SimModeActivity, next == ESim.ESIM)
                power.reboot(null)
            } else {
                Handler(Looper.getMainLooper()).post { toast(R.string.sim_mode_failed) }
            }
        }
    }

    private fun toast(res: Int) =
        Toast.makeText(applicationContext, res, Toast.LENGTH_LONG).show()
}
