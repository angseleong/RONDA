package com.ronda.app.detect

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

/**
 * The only Android-aware half of detection: reads an installed package through
 * [PackageManager] and reduces it to a set of signal keys for
 * [com.ronda.app.core.RiskEvaluator].
 *
 * `PackageManager` is the *entire* detection surface. RONDA never uses an
 * AccessibilityService — that is the banking-trojan abuse vector, and RONDA
 * detecting apps that request it while requesting it itself would be
 * indistinguishable from the malware. Likewise RONDA reads that other apps
 * declare SMS permissions; it never declares one.
 *
 * Everything here reads *declared* manifest entries, not granted permissions,
 * and makes no network call.
 */
class SignalExtractor(private val pm: PackageManager) {

    fun extract(packageName: String): Set<String> {
        val info = try {
            pm.getPackageInfo(packageName, FLAGS)
        } catch (e: PackageManager.NameNotFoundException) {
            return emptySet()
        }

        val keys = mutableSetOf<String>()

        // --- Declared permissions ---
        info.requestedPermissions?.forEach { perm ->
            PERMISSION_KEYS[perm]?.let { keys += it }
        }

        // --- Declared components that bind privileged system services ---
        info.services?.forEach { s ->
            when (s.permission) {
                "android.permission.BIND_ACCESSIBILITY_SERVICE" -> keys += "ACCESSIBILITY"
                "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" -> keys += "NOTIF_LISTENER"
            }
        }
        info.receivers?.forEach { r ->
            if (r.permission == "android.permission.BIND_DEVICE_ADMIN") keys += "DEVICE_ADMIN"
        }

        // --- Provenance ---
        keys += installSourceKey(packageName)
        if (isSelfSigned(info)) keys += "CERT_SELF_SIGNED"
        if (pm.getLaunchIntentForPackage(packageName) == null) keys += "NO_LAUNCHER"

        val appInfo = info.applicationInfo
        if (appInfo != null && appInfo.targetSdkVersion < Build.VERSION_CODES.M) keys += "LEGACY_SDK"

        val label = appInfo?.let { pm.getApplicationLabel(it).toString() } ?: packageName
        if (mimicsBrand(packageName, label)) keys += "NAME_MIMIC"

        return keys
    }

    /** Label as PackageManager reports it, falling back to the package name. */
    fun labelOf(packageName: String): String = try {
        pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        packageName
    }

    /**
     * `getInstallSourceInfo()` (API 30+), never the deprecated
     * `getInstallerPackageName()`. A sideloaded app usually reports a null
     * installer, so we fall back to the initiating package (`com.android.shell`
     * for adb) before concluding it was sideloaded.
     */
    private fun installSourceKey(packageName: String): String {
        val installer = try {
            val src = pm.getInstallSourceInfo(packageName)
            src.installingPackageName ?: src.initiatingPackageName
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
        return when {
            installer == PLAY_STORE -> "SRC_PLAY"
            installer in KNOWN_STORES -> "SRC_KNOWN_STORE"
            else -> "SRC_SIDELOAD"
        }
    }

    /** Self-signed means the certificate vouches only for itself: issuer == subject. */
    private fun isSelfSigned(info: PackageInfo): Boolean {
        val signers = info.signingInfo?.apkContentsSigners ?: return false
        val factory = CertificateFactory.getInstance("X.509")
        return signers.any { sig ->
            try {
                val cert = factory.generateCertificate(sig.toByteArray().inputStream()) as X509Certificate
                cert.issuerX500Principal == cert.subjectX500Principal
            } catch (e: Exception) {
                false
            }
        }
    }

    /**
     * Matches whole tokens, not substrings — "bri" must not fire on "fabric".
     * Legitimate apps from Play Store do match here (real WhatsApp trips it), but
     * the 0.45 Play multiplier keeps their score well under the alert threshold;
     * the signal only bites in combination with sideloading.
     */
    private fun mimicsBrand(packageName: String, label: String): Boolean {
        val tokens = label.lowercase().split(Regex("[^a-z0-9]+")) +
                packageName.lowercase().split('.')
        return tokens.any { it in BRANDS }
    }

    private companion object {
        const val PLAY_STORE = "com.android.vending"

        val KNOWN_STORES = setOf(
            "com.sec.android.app.samsungapps",  // Samsung Galaxy Store
            "org.fdroid.fdroid",                // F-Droid
            "com.amazon.venezia"                // Amazon Appstore
        )

        val BRANDS = setOf(
            "bca", "bri", "mandiri", "bni", "dana", "ovo",
            "gopay", "gojek", "shopee", "whatsapp", "tokopedia"
        )

        const val FLAGS = PackageManager.GET_PERMISSIONS or
                PackageManager.GET_SERVICES or
                PackageManager.GET_RECEIVERS or
                PackageManager.GET_SIGNING_CERTIFICATES

        val PERMISSION_KEYS: Map<String, String> = mapOf(
            // READ_SMS and RECEIVE_SMS both collapse to one SMS_READ signal —
            // an app declaring both must count 40, not 80.
            "android.permission.READ_SMS" to "SMS_READ",
            "android.permission.RECEIVE_SMS" to "SMS_READ",
            "android.permission.REQUEST_INSTALL_PACKAGES" to "INSTALL_PKG",
            "android.permission.SYSTEM_ALERT_WINDOW" to "OVERLAY",
            "android.permission.RECORD_AUDIO" to "AUDIO",
            "android.permission.CALL_PHONE" to "CALL",
            "android.permission.ANSWER_PHONE_CALLS" to "CALL",
            "android.permission.READ_CONTACTS" to "CONTACTS",
            "android.permission.CAMERA" to "CAMERA",
            "android.permission.ACCESS_FINE_LOCATION" to "LOCATION",
            "android.permission.READ_PHONE_STATE" to "PHONE_STATE",
            "android.permission.QUERY_ALL_PACKAGES" to "QUERY_PKGS",
            "android.permission.RECEIVE_BOOT_COMPLETED" to "BOOT",
            "android.permission.INTERNET" to "INTERNET",
            "android.permission.FOREGROUND_SERVICE" to "FG_SERVICE"
        )
    }
}
