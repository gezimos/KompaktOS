package com.kompakt.service

import android.app.AppOpsManager
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PermissionInfo
import android.os.Process
import android.provider.Settings
import android.util.Log

/** Kompakt: the two bundled launchers get what they ask for, once. */
private const val GRANTS_KEY = "kompakt_launcher_grants"
private const val GRANTS_VERSION = 1

private data class Launcher(
    val pkg: String,
    val listener: String,
    val accessibility: String,
)

private val LAUNCHERS = listOf(
    Launcher(
        pkg = "app.inkos",
        listener = "com.github.gezimos.inkos.services.NotificationService",
        accessibility = "com.github.gezimos.inkos.services.ActionService",
    ),
    Launcher(
        pkg = "com.gezimos.katapult",
        listener = "com.gezimos.katapult.service.NotificationListener",
        accessibility = "com.gezimos.katapult.lockscreen.LockscreenWidgetService",
    ),
)

/** usage access, modify system settings, install unknown apps */
private val APP_OPS = listOf(
    AppOpsManager.OPSTR_GET_USAGE_STATS,
    AppOpsManager.OPSTR_WRITE_SETTINGS,
    AppOpsManager.OPSTR_REQUEST_INSTALL_PACKAGES,
)

fun grantLauncherDefaultsOnce(context: Context) {
    val cr = context.contentResolver
    try {
        if (Settings.Secure.getInt(cr, GRANTS_KEY, 0) >= GRANTS_VERSION) return
    } catch (_: Throwable) {
    }
    for (l in LAUNCHERS) {
        val uid = try {
            context.packageManager.getPackageUid(l.pkg, 0)
        } catch (_: PackageManager.NameNotFoundException) {
            Log.i(Sysfs.TAG, "grants: ${l.pkg} not installed, skipped")
            continue
        }
        grantRuntime(context, l.pkg)
        grantAppOps(context, l.pkg, uid)
        grantListener(context, ComponentName(l.pkg, l.listener))
        enableAccessibility(context, ComponentName(l.pkg, l.accessibility))
    }
    try {
        Settings.Secure.putInt(cr, GRANTS_KEY, GRANTS_VERSION)
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "grants: marker write failed", t)
    }
}

private fun grantRuntime(context: Context, pkg: String) {
    val pm = context.packageManager
    val requested = try {
        pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS).requestedPermissions
    } catch (t: Throwable) {
        null
    } ?: return
    val user = Process.myUserHandle()
    for (perm in requested) {
        try {
            val info = pm.getPermissionInfo(perm, 0)
            if (info.protection != PermissionInfo.PROTECTION_DANGEROUS) continue
            pm.grantRuntimePermission(pkg, perm, user)
            Log.i(Sysfs.TAG, "grants: $pkg $perm")
        } catch (t: Throwable) {
            // Unknown or non-grantable permission; the rest still apply.
        }
    }
}

private fun grantAppOps(context: Context, pkg: String, uid: Int) {
    val ops = context.getSystemService(AppOpsManager::class.java) ?: return
    for (op in APP_OPS) {
        try {
            ops.setMode(op, uid, pkg, AppOpsManager.MODE_ALLOWED)
            Log.i(Sysfs.TAG, "grants: $pkg $op allowed")
        } catch (t: Throwable) {
            Log.w(Sysfs.TAG, "grants: $pkg $op failed (${t.javaClass.simpleName})")
        }
    }
}

/** Through the API that owns the setting; a direct settings write is reconciled away. */
private fun grantListener(context: Context, component: ComponentName) {
    try {
        context.getSystemService(NotificationManager::class.java)
            ?.setNotificationListenerAccessGranted(component, true)
        Log.i(Sysfs.TAG, "grants: listener ${component.flattenToShortString()}")
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "grants: listener failed (${t.javaClass.simpleName})")
    }
}

private fun enableAccessibility(context: Context, component: ComponentName) {
    try {
        val cr = context.contentResolver
        val flat = component.flattenToString()
        val current = Settings.Secure.getString(
            cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES).orEmpty()
        if (!current.split(':').contains(flat)) {
            val updated = if (current.isEmpty()) flat else "$current:$flat"
            Settings.Secure.putString(cr, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, updated)
        }
        Settings.Secure.putInt(cr, Settings.Secure.ACCESSIBILITY_ENABLED, 1)
        Log.i(Sysfs.TAG, "grants: accessibility $flat")
    } catch (t: Throwable) {
        Log.w(Sysfs.TAG, "grants: accessibility failed (${t.javaClass.simpleName})")
    }
}
