package com.kobby.hymnal.core.update

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kobby.hymnal.BuildKonfig
import com.kobby.hymnal.core.sharing.ShareConstants
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.kobby.hymnal.BuildConfig

class AndroidUpdateManager : UpdateManager {

    private val remoteConfig: FirebaseRemoteConfig by lazy {
        FirebaseRemoteConfig.getInstance().apply {

            val minFetchInterval = if (BuildConfig.DEBUG) 0L else  43200L // 12 hours
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(minFetchInterval)
                .build()
            setConfigSettingsAsync(configSettings)
            // Default values
            setDefaultsAsync(mapOf(
                KEY_LATEST_VERSION to BuildKonfig.VERSION_NAME,
                KEY_MIN_REQUIRED_VERSION to BuildKonfig.VERSION_NAME
            ))
        }
    }

    override suspend fun checkForUpdates(): UpdateResult {
        return try {
            // Fetch and activate remote config values
            remoteConfig.fetchAndActivate().await()

            val latestVersion = remoteConfig.getString(KEY_LATEST_VERSION)
            val minRequiredVersion = remoteConfig.getString(KEY_MIN_REQUIRED_VERSION)
            val currentVersion = BuildKonfig.VERSION_NAME

            Log.d("UpdateManager", "Checking for updates: current=$currentVersion, latest=$latestVersion, minRequired=$minRequiredVersion")

            val isUpdateAvailable = VersionUtils.isUpdateAvailable(currentVersion, latestVersion)
            val isMandatory = VersionUtils.isUpdateAvailable(currentVersion, minRequiredVersion)

            if (isUpdateAvailable) {
                UpdateResult.UpdateAvailable(latestVersion, isMandatory)
            } else {
                UpdateResult.UpToDate
            }
        } catch (e: Exception) {
            Log.e("UpdateManager", "Error checking for updates", e)
            UpdateResult.Error(e.message ?: "Unknown error")
        }
    }

    override fun getUpdateUrl(): String {
        return ShareConstants.ANDROID_PLAY_STORE_URL
    }

    companion object {
        private const val KEY_LATEST_VERSION = "latest_version"
        private const val KEY_MIN_REQUIRED_VERSION = "min_required_version"
    }
}

actual fun createUpdateManager(): UpdateManager = AndroidUpdateManager()
