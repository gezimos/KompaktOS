package com.kompakt.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** A slider release with auto brightness on (frameworks_base 0167): the point goes into the curve. */
class LearnReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION) return
        LightCurve.learn(context, intent.getFloatExtra("lux", Float.NaN),
            intent.getFloatExtra("brightness", Float.NaN))
    }

    companion object {
        const val ACTION = "com.kompakt.service.action.BRIGHTNESS_LEARN"
    }
}
