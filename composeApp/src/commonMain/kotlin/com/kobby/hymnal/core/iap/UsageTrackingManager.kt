package com.kobby.hymnal.core.iap

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

/**
 * Tracks app usage to determine when to show donation prompts.
 * Uses a milestone-based backoff to show prompts at regular intervals.
 *
 * Non-supporters: 10, 30, 60, 100, 150 (capped) hymns
 */
class UsageTrackingManager(private val storage: PurchaseStorage) {

    private val _usageStats = MutableStateFlow(UsageStats())
    val usageStats: StateFlow<UsageStats> = _usageStats.asStateFlow()

    companion object {
        private const val YEAR_IN_MS = 365L * 24 * 60 * 60 * 1000
        private const val REVIEW_COOLDOWN_MS = 90L * 24 * 60 * 60 * 1000 // 90 days
        private const val REVIEW_HYMN_THRESHOLD = 5
        private const val REVIEW_SEARCH_THRESHOLD = 3
    }

    /**
     * Record that a hymn was read.
     * Returns true if we should show the donation prompt.
     */
    fun recordHymnRead(isSupporter: Boolean): Boolean {
        // Check for yearly reset before processing
        checkYearlyReset(isSupporter)

        // Update donation tracking
        val currentCount = storage.hymnsReadCount
        val newCount = currentCount + 1
        storage.hymnsReadCount = newCount

        // Update review tracking (independent of donation logic)
        val currentReviewCount = storage.reviewHymnsReadCount
        val newReviewCount = currentReviewCount + 1
        storage.reviewHymnsReadCount = newReviewCount

        println("DEBUG: UsageTrackingManager - Hymn read recorded. Total: $newCount, Review count: $newReviewCount")

        _usageStats.value = _usageStats.value.copy(hymnsRead = newCount)

        // Check if we should show donation prompt
        return shouldShowDonationPrompt(isSupporter)
    }

    /**
     * Record that a successful search was performed.
     */
    fun recordSearchSuccess() {
        // Update donation tracking
        val currentCount = storage.searchCount
        storage.searchCount = currentCount + 1

        // Update review tracking
        val currentReviewCount = storage.reviewSearchCount
        val newReviewCount = currentReviewCount + 1
        storage.reviewSearchCount = newReviewCount
        
        println("DEBUG: UsageTrackingManager - Search success recorded. Review search count: $newReviewCount")
    }

    /**
     * Check if we should show the in-app review prompt.
     * Strategy:
     * 1. Check Cooldown: 90 days since last prompt.
     * 2. Check Engagement: 5 fresh reads OR 3 fresh searches.
     */
    fun shouldShowReviewPrompt(): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        val lastPrompt = storage.lastReviewPromptTimestamp

        // 1. Check Cooldown (90 days)
        if (now - lastPrompt < REVIEW_COOLDOWN_MS) {
            println("DEBUG: UsageTrackingManager - Cooldown active. Last prompt: $lastPrompt, Current: $now")
            return false
        }

        // 2. Check Fresh Engagement
        val freshReads = storage.reviewHymnsReadCount
        val freshSearches = storage.reviewSearchCount

        val shouldShow = freshReads >= REVIEW_HYMN_THRESHOLD || freshSearches >= REVIEW_SEARCH_THRESHOLD
        println("DEBUG: UsageTrackingManager - Review prompt check: $shouldShow (Reads: $freshReads, Searches: $freshSearches)")
        return shouldShow
    }

    /**
     * Record that the in-app review prompt was shown.
     * Resets engagement counters to require fresh usage for the next prompt.
     */
    fun recordReviewPromptShown() {
        storage.lastReviewPromptTimestamp = Clock.System.now().toEpochMilliseconds()
        // Reset counters so user must "earn" the next prompt (after cooldown)
        storage.reviewHymnsReadCount = 0
        storage.reviewSearchCount = 0
        
        // Update legacy flag for backward compatibility (optional, but good practice)
        storage.hasShownReviewPrompt = true
    }

    /**
     * Check if donation prompt should be shown based on milestone-based logic.
     * Updated behavior: supporters NEVER see the paywall again (no yearly reminders).
     * Non-supporters: 10, 30, 60, 100, 150 hymns read (capped).
     */
    fun shouldShowDonationPrompt(isSupporter: Boolean): Boolean {
        // Supporters should not be shown donation prompts anymore
        if (isSupporter) {
            return false
        }

        // For non-supporters, use milestones (10, 30, 60, 100, 150) with a hard cap at 150
        val hymnsRead = storage.hymnsReadCount
        if (hymnsRead > PurchaseStorage.PROMPT_CAP_THRESHOLD) {
            return false
        }

        val nextThreshold = storage.nextPromptThreshold
        return hymnsRead >= nextThreshold
    }

    /**
     * Check if a year has passed since the last reset and reset counters if so.
     */
    private fun checkYearlyReset(isSupporter: Boolean) {
        if (isSupporter) return

        val now = Clock.System.now().toEpochMilliseconds()
        val lastReset = storage.lastResetTimestamp

        // Initialize lastReset if it's 0 (first use)
        if (lastReset == 0L) {
            storage.lastResetTimestamp = now
            return
        }

        if (now - lastReset >= YEAR_IN_MS) {
            resetYearlyCounters()
        }
    }

    /**
     * Reset counters for a new year of usage tracking.
     */
    private fun resetYearlyCounters() {
        storage.hymnsReadCount = 0
        storage.donationPromptCount = 0
        storage.nextPromptThreshold = 10
        storage.lastResetTimestamp = Clock.System.now().toEpochMilliseconds()

        _usageStats.value = UsageStats(
            hymnsRead = 0,
            promptCount = 0,
            lastDonationDate = null
        )
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
