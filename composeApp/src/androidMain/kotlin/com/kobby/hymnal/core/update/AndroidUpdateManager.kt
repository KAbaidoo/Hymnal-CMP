package com.kobby.hymnal.core.update

import android.content.Context
import com.kobby.hymnal.BuildKonfig
import com.kobby.hymnal.core.config.RemoteConfigManager
import com.kobby.hymnal.core.sharing.ShareConstants
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.tasks.await

class AndroidUpdateManager(
    private val context: Context,
    private val remoteConfigManager: RemoteConfigManager
) : UpdateManager {

    override suspend fun checkForUpdates(): UpdateResult {
        val currentVersion = BuildKonfig.VERSION_NAME

        remoteConfigManager.fetchAndActivate()

        val minRequiredVersion = remoteConfigManager.getString(KEY_MIN_REQUIRED_VERSION, BuildKonfig.VERSION_NAME)

        val isMandatory = VersionUtils.isUpdateAvailable(currentVersion, minRequiredVersion)
        val playStoreUpdate = getPlayStoreUpdateInfo()
        val isUpdateAvailable = isMandatory || playStoreUpdate.isUpdateAvailable

        Log.d(
            "UpdateManager",
            "Checking for updates: current=$currentVersion, minRequired=$minRequiredVersion, " +
                "playStoreUpdate=${playStoreUpdate.isUpdateAvailable}, mandatory=$isMandatory"
        )

        return if (isUpdateAvailable) {
            UpdateResult.UpdateAvailable(isMandatory)
        } else {
            UpdateResult.UpToDate
        }
    }

    override fun getUpdateUrl(): String {
        return ShareConstants.ANDROID_PLAY_STORE_URL
    }

    private suspend fun getPlayStoreUpdateInfo(): PlayStoreUpdateInfo {
        return try {
            val appUpdateManager = AppUpdateManagerFactory.create(context)
            val appUpdateInfo = appUpdateManager.appUpdateInfo.await()

            val availability = appUpdateInfo.updateAvailability()
            val isUpdateAvailable = availability == UpdateAvailability.UPDATE_AVAILABLE ||
                availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            val availableVersionCode = if (isUpdateAvailable) appUpdateInfo.availableVersionCode() else null

            PlayStoreUpdateInfo(
                isUpdateAvailable = isUpdateAvailable,
                availableVersionCode = availableVersionCode
            )
        } catch (e: Exception) {
            Log.w("UpdateManager", "Play Store update check failed", e)
            PlayStoreUpdateInfo(isUpdateAvailable = false, availableVersionCode = null)
        }
    }

    private data class PlayStoreUpdateInfo(
        val isUpdateAvailable: Boolean,
        val availableVersionCode: Int?
    )

    companion object {
        private const val KEY_MIN_REQUIRED_VERSION = "min_required_version"
    }
}
