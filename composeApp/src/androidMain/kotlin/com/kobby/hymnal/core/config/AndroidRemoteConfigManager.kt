package com.kobby.hymnal.core.config

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kobby.hymnal.BuildConfig
import kotlinx.coroutines.tasks.await
import android.util.Log

class AndroidRemoteConfigManager : RemoteConfigManager {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {
            val minFetchInterval = if (BuildConfig.DEBUG) 0L else 43200L // 12 hours
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(minFetchInterval)
                .build()
            setConfigSettingsAsync(configSettings)
        }
    }

    override suspend fun fetchAndActivate(): Boolean {
        return try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            Log.w("RemoteConfigManager", "Remote Config fetch failed", e)
            false
        }
    }

    override fun getString(key: String, defaultValue: String): String {
        val value = remoteConfig.getString(key)
        return if (value.isNotEmpty()) value else defaultValue
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        // Firebase Remote Config returns false if key doesn't exist, which might not be our default.
        // We can check if the key exists or just use the value if it's explicitly set.
        // However, standard RC behavior is to use defaults set via setDefaultsAsync.
        return remoteConfig.getBoolean(key)
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return remoteConfig.getLong(key)
    }
}
