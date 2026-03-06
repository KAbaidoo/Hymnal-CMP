package com.kobby.hymnal.core.iap

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * Tracks app usage to determine when to show donation prompts.
 * Uses exponential backoff to show prompts less frequently over time.
 *
 * Non-supporters: 10, 25, 50, 100, 200, 400 (capped) hymns
 */
class UsageTrackingManager(private val storage: PurchaseStorage) {

    private val _usageStats = MutableStateFlow(UsageStats())
    val usageStats: StateFlow<UsageStats> = _usageStats.asStateFlow()

    /**
     * Record that a hymn was read.
     * Returns true if we should show the donation prompt.
     */
    fun recordHymnRead(isSupporter: Boolean): Boolean {
        val currentCount = storage.hymnsReadCount
        val newCount = currentCount + 1
        storage.hymnsReadCount = newCount

        _usageStats.value = _usageStats.value.copy(hymnsRead = newCount)

        // Check if we should show donation prompt
        return shouldShowDonationPrompt(isSupporter)
    }

    /**
     * Check if donation prompt should be shown based on exponential backoff logic.
     * Updated behavior: supporters NEVER see the paywall again (no yearly reminders).
     */
    fun shouldShowDonationPrompt(isSupporter: Boolean): Boolean {
        // Supporters should not be shown donation prompts anymore
        if (isSupporter) {
            return false
        }

        // For non-supporters, use regular exponential backoff
        val hymnsRead = storage.hymnsReadCount
        val nextThreshold = storage.nextPromptThreshold
        return hymnsRead >= nextThreshold
    }

    /**
     * Record that the donation prompt was shown.
     * Increments prompt counter and calculates next threshold.
     */
    fun recordPromptShown() {
        storage.lastDonationPromptTimestamp = Clock.System.now().toEpochMilliseconds()
        storage.donationPromptCount += 1

        // Calculate and store next threshold based on new prompt count (no supporter branch)
        val nextThreshold = storage.calculateNextThreshold()
        storage.nextPromptThreshold = nextThreshold
    }

    /**
     * Record that a donation was made.
     * Resets counters and sets up reminder schedule for internal tracking.
     */
    fun recordDonationMade() {
        storage.recordDonation()
        // Reset hymn count for fresh start
        storage.hymnsReadCount = 0

        _usageStats.value = _usageStats.value.copy(
            hymnsRead = 0,
            promptCount = 0
        )
    }

    /**
     * Get the next prompt threshold for display purposes.
     */
    fun getNextPromptThreshold(): Int {
        return storage.nextPromptThreshold
    }

    /**
     * Initialize usage stats from storage.
     */
    fun initialize() {
        _usageStats.value = UsageStats(
            hymnsRead = storage.hymnsReadCount,
            promptCount = storage.donationPromptCount,
            lastDonationDate = null
        )
    }
}

/**
 * Usage statistics for the app.
 */
data class UsageStats(
    val hymnsRead: Int = 0,
    val promptCount: Int = 0,
    val lastDonationDate: Long? = null
)
