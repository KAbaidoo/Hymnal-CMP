package com.kobby.hymnal.core.sharing

import com.kobby.hymnal.composeApp.database.Hymn
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.UIKit.UINavigationController
import platform.UIKit.UITabBarController
import platform.UIKit.UIWindow
import platform.UIKit.UIPopoverPresentationController
import platform.UIKit.popoverPresentationController

actual class ShareManager {
    
    @OptIn(ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)
    actual fun shareHymn(hymn: Hymn) {
        val shareContent = ShareContentFormatter.formatHymnForSharing(hymn)

        // Resolve a safe presenter (top-most visible view controller)
        val presenter = findTopViewController() ?: return

        // Create the share content as NSString
        val nsShareContent = NSString.create(string = shareContent)
        val activityItems = listOf(nsShareContent)

        // Create UIActivityViewController
        val activityViewController = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null
        )

        // On iPad the activity controller uses a popover; ensure it has a valid anchor
        val popover = activityViewController.popoverPresentationController as? UIPopoverPresentationController
        if (popover != null) {
            popover.sourceView = presenter.view
            // sourceRect left unset; sourceView provides a valid anchor
        }

        // Present the share sheet from the resolved presenter
        presenter.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }

    // Find the top-most view controller to present from.
    @OptIn(kotlinx.cinterop.BetaInteropApi::class)
    private fun findTopViewController(): UIViewController? {
        val app = UIApplication.sharedApplication

        // Get windows and filter to UIWindow instances where available
        val windowsList = app.windows.filterIsInstance<UIWindow>() ?: emptyList()
        val keyWindow = app.keyWindow ?: windowsList.firstOrNull { it.isKeyWindow() } ?: windowsList.firstOrNull()
        val rootVC = keyWindow?.rootViewController ?: return null

        var top: UIViewController = rootVC

        // Walk through presented controllers to get the visible one
        while (true) {
            val presented = top.presentedViewController ?: break
            top = presented
        }

        // Drill into common container controllers
        if (top is UINavigationController) {
            top = top.visibleViewController ?: top
        } else if (top is UITabBarController) {
            top = top.selectedViewController ?: top
        }

        return top
    }
}