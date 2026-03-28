package com.kobby.hymnal.core.update

/**
 * Utility for comparing semantic version strings.
 */
object VersionUtils {

    /**
     * Compares two semantic version strings.
     * @param current The current app version (e.g., "1.2.3")
     * @param latest The latest available version (e.g., "1.2.4")
     * @return Positive if current > latest, Negative if current < latest, 0 if equal.
     */
    fun compareVersions(current: String, latest: String): Int {
        val currentParts = current.split(".").map { it.toIntOrNull() ?: 0 }
        val latestParts = latest.split(".").map { it.toIntOrNull() ?: 0 }

        val maxLength = maxOf(currentParts.size, latestParts.size)

        for (i in 0 until maxLength) {
            val currentVal = currentParts.getOrElse(i) { 0 }
            val latestVal = latestParts.getOrElse(i) { 0 }

            if (currentVal != latestVal) {
                return currentVal - latestVal
            }
        }

        return 0
    }

    /**
     * Checks if an update is available.
     * @param current The current app version.
     * @param latest The latest available version.
     * @return True if [latest] is newer than [current].
     */
    fun isUpdateAvailable(current: String, latest: String): Boolean {
        return compareVersions(current, latest) < 0
    }
}
