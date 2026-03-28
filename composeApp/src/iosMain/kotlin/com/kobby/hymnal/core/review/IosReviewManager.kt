package com.kobby.hymnal.core.review

import platform.StoreKit.SKStoreReviewController
import platform.UIKit.UIApplication
import platform.UIKit.UIWindowScene

/**
 * iOS implementation of [ReviewManager] using SKStoreReviewController.
 */
class IosReviewManager : ReviewManager {

    override fun requestReview() {
        val scene = UIApplication.sharedApplication.connectedScenes.firstOrNull { 
            it is UIWindowScene 
        } as? UIWindowScene
        
        if (scene != null) {
            SKStoreReviewController.requestReviewInScene(scene)
        } else {
            // Fallback for older iOS versions if needed, though most KMP targets are newer
            SKStoreReviewController.requestReview()
        }
    }
}
