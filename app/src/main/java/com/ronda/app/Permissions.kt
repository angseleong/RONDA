package com.ronda.app

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.content.ContextCompat

/**
 * The three permissions RONDA needs, and how to ask for each one.
 *
 * SYSTEM_ALERT_WINDOW and PACKAGE_USAGE_STATS cannot be requested through the
 * normal runtime permission dialog — the user must grant them in system Settings.
 *
 * Note what is deliberately absent: no SMS permission, no AccessibilityService.
 * PACKAGE_USAGE_STATS reveals only the foreground package name, and
 * SYSTEM_ALERT_WINDOW only draws on top. Neither can read screen content or input.
 */
object Permissions {

    /** Draw the warning overlay on top of a flagged app. */
    fun hasOverlay(context: Context): Boolean = Settings.canDrawOverlays(context)

    /** See which package is in the foreground. */
    // unsafeCheckOpNoThrow is deprecated in favour of checkOpNoThrow(String, int, String),
    // but that replacement is @FlaggedApi and does not exist on API 30-35 devices.
    // Keep the deprecated call until minSdk can be raised.
    @Suppress("DEPRECATION")
    fun hasUsageStats(context: Context): Boolean {
        val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    /** Show detection notifications. Implicitly granted below API 33. */
    fun hasNotifications(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** True when RONDA can both detect and block. */
    fun canBlock(context: Context): Boolean = hasOverlay(context) && hasUsageStats(context)

    fun overlaySettingsIntent(context: Context): Intent = Intent(
        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
        Uri.parse("package:${context.packageName}")
    )

    fun usageStatsSettingsIntent(): Intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
}
