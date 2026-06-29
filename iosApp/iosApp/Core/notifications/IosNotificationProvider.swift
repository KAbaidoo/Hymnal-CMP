import Foundation
import UserNotifications
import ComposeApp

class IosNotificationProvider: NativeNotificationProvider {

    private let weeklyIdentifier = "weekly_reminders"
    private let inactivityIdentifier = "inactivity_nudges"

    func scheduleWeekly(settings: NotificationSettings) {
        guard settings.weeklyEnabled else {
            UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [weeklyIdentifier])
            return
        }
        
        let content = UNMutableNotificationContent()
        content.title = settings.weeklyTitle
        content.body = settings.weeklyBody

        var dateComponents = DateComponents()
        dateComponents.weekday = 1 // Sunday
        dateComponents.hour = 7
        dateComponents.minute = 30

        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        let request = UNNotificationRequest(identifier: weeklyIdentifier, content: content, trigger: trigger)

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error scheduling weekly reminder: \(error.localizedDescription)")
            }
        }
    }

    func scheduleInactivity(settings: NotificationSettings) {
        guard settings.inactivityEnabled else {
            UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [inactivityIdentifier])
            return
        }
        
        let content = UNMutableNotificationContent()
        content.title = settings.inactivityTitle
        content.body = settings.inactivityBody

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: TimeInterval(settings.inactivityDays * 24 * 60 * 60), repeats: false)
        let request = UNNotificationRequest(identifier: inactivityIdentifier, content: content, trigger: trigger)

        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error scheduling inactivity nudge: \(error.localizedDescription)")
            }
        }
    }

    func scheduleSeasonal(settings: NotificationSettings) {
        guard settings.seasonalEnabled else {
            UNUserNotificationCenter.current().getPendingNotificationRequests { requests in
                let seasonalIds = requests
                    .map { $0.identifier }
                    .filter { $0.hasPrefix("seasonal_") }
                UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: seasonalIds)
            }
            return
        }
        
        let currentYear = Calendar.current.component(.year, from: Date())
        let events = SeasonalEventCalculator.shared.eventsForYear(year: Int32(currentYear)) +
                     SeasonalEventCalculator.shared.eventsForYear(year: Int32(currentYear + 1))
        
        events.forEach { event in
            let content = UNMutableNotificationContent()
            content.title = event.title
            content.body = event.body

            var components = DateComponents()
            components.year = Int(event.date.year)
            components.month = Int(event.date.monthNumber)
            components.day = Int(event.date.dayOfMonth)
            components.hour = 8
            components.minute = 0

            guard let date = Calendar.current.date(from: components), date > Date() else {
                return
            }

            let trigger = UNCalendarNotificationTrigger(dateMatching: components, repeats: false)
            let request = UNNotificationRequest(identifier: event.id, content: content, trigger: trigger)

            UNUserNotificationCenter.current().add(request) { error in
                if let error = error {
                    print("Error scheduling seasonal reminder \(event.id): \(error.localizedDescription)")
                }
            }
        }
    }

    func syncCampaignSubscription(enabled: Bool) {
        _ = enabled
        IosPushNotificationCoordinator.syncCampaignTopicSubscriptionFromUserDefaults()
    }

    func cancelAll() {
        UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: [
            weeklyIdentifier,
            inactivityIdentifier
        ])
        UNUserNotificationCenter.current().getPendingNotificationRequests { requests in
            let seasonalIds = requests
                .map { $0.identifier }
                .filter { $0.hasPrefix("seasonal_") }
            UNUserNotificationCenter.current().removePendingNotificationRequests(withIdentifiers: seasonalIds)
        }
    }

    func requestPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                print("Notification permission granted")
            } else if let error {
                print("Notification permission error: \(error.localizedDescription)")
            }
        }
    }

    func sendTestNotification() {
        let content = UNMutableNotificationContent()
        content.title = "Hymnal Test Notification"
        content.body = "This is a test notification to verify reminders are working!"
        content.sound = UNNotificationSound.default
        
        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1.0, repeats: false)
        let request = UNNotificationRequest(identifier: "test_notification", content: content, trigger: trigger)
        
        UNUserNotificationCenter.current().add(request) { error in
            if let error = error {
                print("Error scheduling test notification: \(error.localizedDescription)")
            }
        }
    }
}
