package com.ronda.app.detection

/**
 * Result of evaluating a package's risk level.
 *
 * Contains the metadata RONDA reads from PackageManager:
 * install source, declared permissions, and the computed risk level.
 */
data class RiskResult(
    val packageName: String,
    val appLabel: String,
    val installSource: String,
    val flaggedPermissions: List<String>,
    val riskLevel: RiskLevel
)

enum class RiskLevel {
    /** Sideloaded AND declares READ_SMS or RECEIVE_SMS */
    HIGH,
    /** One signal or none — no alert */
    LOW
}
