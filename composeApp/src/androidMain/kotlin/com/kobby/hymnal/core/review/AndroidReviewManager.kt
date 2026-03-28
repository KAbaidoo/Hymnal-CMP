package com.kobby.hymnal.core.review

import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import com.kobby.hymnal.core.util.ActivityProvider

/**
 * Android implementation of [ReviewManager] using Google Play In-App Review API.
 */
class AndroidReviewManager(
    private val context: Context,
    private val activityProvider: ActivityProvider
) : ReviewManager {

    override fun requestReview() {
        val activity = activityProvider.currentActivity
        if (activity == null) {
            println("DEBUG: AndroidReviewManager - No current activity available to launch review.")
            return
        }
        
        println("DEBUG: AndroidReviewManager - Initializing ReviewManagerFactory.")
        val manager = ReviewManagerFactory.create(context)
        val request = manager.requestReviewFlow()

        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                println("DEBUG: AndroidReviewManager - Review flow request SUCCESS. Launching review.")
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            } else {
                val exception = task.exception
                println("DEBUG: AndroidReviewManager - Review flow request FAILED. Exception: ${exception?.message}")
            }
        }
    }
}

