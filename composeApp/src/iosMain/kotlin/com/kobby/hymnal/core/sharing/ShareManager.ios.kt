package com.kobby.hymnal.core.sharing

import com.kobby.hymnal.composeApp.database.Hymn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication

actual class ShareManager {
    
    @OptIn(ExperimentalForeignApi::class)
    actual fun shareHymn(hymn: Hymn) {
        val shareContent = ShareContentFormatter.formatHymnForSharing(hymn)
        
        // Get the current root view controller
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
            ?: return
        
        // Create the share content as NSString
        val nsShareContent = NSString.create(string = shareContent)
        val activityItems = listOf(nsShareContent)
        
        // Create UIActivityViewController
        val activityViewController = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        )
        
        // Present the share sheet
        rootViewController.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }
}