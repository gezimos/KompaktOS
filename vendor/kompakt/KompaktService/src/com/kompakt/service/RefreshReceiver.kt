package com.kompakt.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Long press home arrives here. */
class RefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Flash.show(context.applicationContext)
    }
}
