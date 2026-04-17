import Foundation
import UIKit
import UserNotifications
import ComposeApp

#if canImport(FirebaseMessaging)
import FirebaseMessaging
#endif

final class IosPushNotificationCoordinator: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {

    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        UNUserNotificationCenter.current().delegate = self
        application.registerForRemoteNotifications()

        #if canImport(FirebaseMessaging)
        Messaging.messaging().delegate = self
        Self.syncCampaignTopicSubscriptionFromUserDefaults()
        #endif

        return true
    }

    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        #if canImport(FirebaseMessaging)
        Messaging.messaging().apnsToken = deviceToken
        #endif
    }

    func application(
        _ application: UIApplication,
        didFailToRegisterForRemoteNotificationsWithError error: Error
    ) {
        print("APNs registration failed: \(error.localizedDescription)")
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        let request = notification.request
        let category = resolveCategory(identifier: request.identifier, userInfo: request.content.userInfo)
        let messageId = request.content.userInfo["gcm.message_id"] as? String
        let source = request.content.userInfo["from"] as? String
        TraceManager_iosKt.trackIosNotificationReceived(
            category: category,
            messageId: messageId,
            source: source
        )

        completionHandler([.badge, .sound, .banner, .list])
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        didReceive response: UNNotificationResponse,
        withCompletionHandler completionHandler: @escaping () -> Void
    ) {
        let request = response.notification.request
        let category = resolveCategory(identifier: request.identifier, userInfo: request.content.userInfo)
        TraceManager_iosKt.trackIosNotificationOpened(
            category: category,
            notificationId: request.identifier
        )

        completionHandler()
    }

    private func resolveCategory(identifier: String, userInfo: [AnyHashable: Any]) -> String {
        if identifier == "weekly_reminders" {
            return "weekly"
        }
        if identifier == "inactivity_nudges" {
            return "inactivity"
        }
        if identifier.hasPrefix("seasonal_") {
            return "seasonal"
        }

        if let category = userInfo["notification_category"] as? String, !category.isEmpty {
            return category.lowercased()
        }

        if userInfo["gcm.message_id"] != nil || userInfo["google.c.a.c_id"] != nil {
            return "campaign"
        }

        return "campaign"
    }

    #if canImport(FirebaseMessaging)
    static func syncCampaignTopicSubscriptionFromUserDefaults() {
        // Campaign delivery is externally controlled. Keep topic subscription active.
        Messaging.messaging().subscribe(toTopic: "hymnal_campaigns") { error in
            if let error {
                print("Topic subscribe failed: \(error.localizedDescription)")
            }
        }
    }
    #endif
}

#if canImport(FirebaseMessaging)
extension IosPushNotificationCoordinator: MessagingDelegate {
    func messaging(_ messaging: Messaging, didReceiveRegistrationToken fcmToken: String?) {
        guard let token = fcmToken, !token.isEmpty else { return }
        UserDefaults.standard.set(token, forKey: "fcm_registration_token")
        Self.syncCampaignTopicSubscriptionFromUserDefaults()
        print("FCM token refreshed")
    }
}
#endif
