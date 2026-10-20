package com.kompakt.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

/** GMS's "uncertified" notice is permanent and undismissable here, so drop it. */
class UncertifiedNotice : NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        val n = sbn?.notification ?: return
        if (sbn.packageName != "com.google.android.gms") return
        if (n.channelId != "uncertified_device") return
        runCatching { cancelNotification(sbn.key) }
    }
}
