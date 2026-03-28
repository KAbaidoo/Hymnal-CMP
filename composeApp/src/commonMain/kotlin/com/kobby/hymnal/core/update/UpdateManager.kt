package com.kobby.hymnal.core.update

/**
 * Represents the result of an app update check.
 */
sealed class UpdateResult {
    /**
     * App is up to date.
     */
    object UpToDate : UpdateResult()

    /**
     * A new version is available.
     * @property latestVersion The version name of the latest update.
     * @property isMandatory True if the user must update to continue using the app.
     */
    data class UpdateAvailable(
        val latestVersion: String,
        val isMandatory: Boolean
    ) : UpdateResult()

    /**
     * An error occurred while checking for updates.
     */
    data class Error(val message: String) : UpdateResult()
}

/**
 * Interface for managing app update checks across platforms.
 */
interface UpdateManager {
    /**
     * Checks if a new version of the app is available.
     * @return [UpdateResult] indicating the status of the update check.
     */
    suspend fun checkForUpdates(): UpdateResult

    /**
     * Returns the platform-specific store URL for the app.
     */
    fun getUpdateUrl(): String
}

/**
 * Expect declaration for platform-specific UpdateManager implementation.
 */
expect fun createUpdateManager(): UpdateManager
