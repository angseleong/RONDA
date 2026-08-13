package com.ronda.app.detection

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

/**
 * Reads install source and declared permissions for a given package,
 * then applies the risk rule:
 *   sideloaded (non-Play Store) AND declares READ_SMS/RECEIVE_SMS → HIGH RISK
 */
class RiskEvaluator(private val context: Context) {

    companion object {
        private const val TAG = "RiskEvaluator"

        /** Play Store installer package name */
        private const val PLAY_STORE = "com.android.vending"

        /** SMS permissions that signal malicious intent when sideloaded */
        private val DANGEROUS_PERMISSIONS = setOf(
            "android.permission.READ_SMS",
            "android.permission.RECEIVE_SMS"
        )
    }

    /**
     * Evaluate a package's risk level.
     *
     * @param packageName The package to evaluate (e.g. "com.test.undangan")
     * @return [RiskResult] with install source, flagged permissions, and risk level
     */
    fun evaluate(packageName: String): RiskResult {
        val pm = context.packageManager

        // --- Read app label ---
        val appLabel = try {
            val appInfo = pm.getApplicationInfo(packageName, 0)
            pm.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "Package not found: $packageName")
            return RiskResult(
                packageName = packageName,
                appLabel = packageName,
                installSource = "unknown",
                flaggedPermissions = emptyList(),
                riskLevel = RiskLevel.LOW
            )
        }

        // --- Read install source via getInstallSourceInfo() (API 30+) ---
        val installSource = try {
            val sourceInfo = pm.getInstallSourceInfo(packageName)
            Log.d(TAG, "InstallSourceInfo for $packageName: " +
                    "initiating=${sourceInfo.initiatingPackageName}, " +
                    "installing=${sourceInfo.installingPackageName}, " +
                    "originating=${sourceInfo.originatingPackageName}")
            // installingPackageName is often null for sideloaded apps;
            // fall back to initiatingPackageName (e.g. "com.android.shell" for adb)
            sourceInfo.installingPackageName
                ?: sourceInfo.initiatingPackageName
                ?: "manual"
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "Install source not found for: $packageName")
            "unknown"
        }

        // --- Read declared permissions via getPackageInfo(GET_PERMISSIONS) ---
        val flaggedPermissions = try {
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)
            val requested = packageInfo.requestedPermissions
            Log.d(TAG, "Raw requestedPermissions for $packageName: ${requested?.toList()}")
            requested?.filter { it in DANGEROUS_PERMISSIONS } ?: emptyList()
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "Could not read permissions for: $packageName")
            emptyList()
        }

        // --- Apply risk rule ---
        val isSideloaded = installSource != PLAY_STORE
        val declaresSms = flaggedPermissions.isNotEmpty()
        val riskLevel = if (isSideloaded && declaresSms) RiskLevel.HIGH else RiskLevel.LOW

        Log.d(TAG, "RESULT for $packageName: source=$installSource, " +
                "isSideloaded=$isSideloaded, declaresSms=$declaresSms, " +
                "flagged=$flaggedPermissions, risk=$riskLevel")

        return RiskResult(
            packageName = packageName,
            appLabel = appLabel,
            installSource = installSource,
            flaggedPermissions = flaggedPermissions,
            riskLevel = riskLevel
        )
    }
}
