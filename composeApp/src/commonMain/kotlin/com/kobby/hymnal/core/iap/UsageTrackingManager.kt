package com.kobby.hymnal.core.iap

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Tracks app usage to determine when to show support prompts.
 * In the generous freemium model, we track hymn reads to show support sheet
 * at natural interruption points (e.g., after 10th hymn).
 */
class UsageTrackingManager(private val storage: PurchaseStorage) {

    companion object {
        private const val HYMNS_READ_THRESHOLD = 10
    }

    private val _usageStats = MutableStateFlow(UsageStats())
    val usageStats: StateFlow<UsageStats> = _usageStats.asStateFlow()

    /**
     * Record that a hymn was read.
     * Returns true if we should show the support prompt.
     */
    fun recordHymnRead(): Boolean {
        val currentCount = storage.hymnsReadCount
        val newCount = currentCount + 1
        storage.hymnsReadCount = newCount

        _usageStats.value = _usageStats.value.copy(hymnsRead = newCount)

        // Show support prompt on exactly the threshold hymn
        return newCount == HYMNS_READ_THRESHOLD
    }

    /**
     * Record that a premium feature was accessed (for analytics).
     */
    fun recordFeatureAccessAttempt(feature: PremiumFeature) {
        val attempts = storage.getFeatureAccessAttempts(feature)
        storage.setFeatureAccessAttempts(feature, attempts + 1)

        _usageStats.value = _usageStats.value.copy(
            featureAccessAttempts = storage.getAllFeatureAccessAttempts()
        )
    }

    /**
     * Reset the hymn read counter (e.g., after user supports).
     */
    fun resetHymnReadCount() {
        storage.hymnsReadCount = 0
        _usageStats.value = _usageStats.value.copy(hymnsRead = 0)
    }

    /**
     * Check if we should show the support prompt based on usage.
     */
    fun shouldShowSupportPrompt(): Boolean {
        val hymnsRead = storage.hymnsReadCount
        return hymnsRead >= HYMNS_READ_THRESHOLD
    }

    /**
     * Initialize usage stats from storage.
     */
    fun initialize() {
        _usageStats.value = UsageStats(
            hymnsRead = storage.hymnsReadCount,
            featureAccessAttempts = storage.getAllFeatureAccessAttempts()
        )
    }
}

/**
 * Usage statistics for the app.
 */
data class UsageStats(
    val hymnsRead: Int = 0,
    val featureAccessAttempts: Map<PremiumFeature, Int> = emptyMap()
)

