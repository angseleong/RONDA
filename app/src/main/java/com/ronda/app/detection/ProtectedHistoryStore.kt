package com.ronda.app.detection

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ProtectedHistoryItem(
    val packageName: String,
    val appLabel: String,
    val timestamp: Long,
    val action: String // "uninstalled" or "safe"
)

class ProtectedHistoryStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("ronda_protected_history", Context.MODE_PRIVATE)

    fun record(packageName: String, appLabel: String, action: String) {
        val current = history()
        val newItem = ProtectedHistoryItem(
            packageName = packageName,
            appLabel = appLabel,
            timestamp = System.currentTimeMillis(),
            action = action
        )
        val updated = listOf(newItem) + current.take(49)
        save(updated)
    }

    fun history(): List<ProtectedHistoryItem> {
        val raw = prefs.getString("items", null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).map { i ->
                val obj = array.getJSONObject(i)
                ProtectedHistoryItem(
                    packageName = obj.getString("packageName"),
                    appLabel = obj.getString("appLabel"),
                    timestamp = obj.getLong("timestamp"),
                    action = obj.getString("action")
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun save(items: List<ProtectedHistoryItem>) {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject().apply {
                put("packageName", item.packageName)
                put("appLabel", item.appLabel)
                put("timestamp", item.timestamp)
                put("action", item.action)
            }
            array.put(obj)
        }
        prefs.edit().putString("items", array.toString()).apply()
    }
}
