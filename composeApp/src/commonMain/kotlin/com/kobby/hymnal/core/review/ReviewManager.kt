package com.kobby.hymnal.core.review

/**
 * Interface for requesting in-app reviews on different platforms.
 */
interface ReviewManager {
    /**
     * Request an in-app review.
     * This will trigger the platform's native review prompt if appropriate.
     */
    fun requestReview()
}
