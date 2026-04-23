package com.kobby.hymnal.core.config

interface RemoteConfigManager {
    suspend fun fetchAndActivate(): Boolean
    fun getString(key: String, defaultValue: String): String
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun getLong(key: String, defaultValue: Long): Long

    companion object {
        const val KEY_WEEKLY_ENABLED = "notifications_weekly_enabled"
        const val KEY_WEEKLY_TITLE = "notifications_weekly_title"
        const val KEY_WEEKLY_BODY = "notifications_weekly_body"
        
        const val KEY_INACTIVITY_ENABLED = "notifications_inactivity_enabled"
        const val KEY_INACTIVITY_TITLE = "notifications_inactivity_title"
        const val KEY_INACTIVITY_BODY = "notifications_inactivity_body"
        const val KEY_INACTIVITY_DAYS = "notifications_inactivity_days"
        
        const val KEY_SEASONAL_ENABLED = "notifications_seasonal_enabled"
    }
}
