import Foundation
import UserNotifications
import ComposeApp

class IosNotificationProvider: NativeNotificationProvider {

    private let weeklyIdentifier = "weekly_reminders"
    private let inactivityIdentifier = "inactivity_nudges"

    func scheduleWeekly() {
        let content = UNMutableNotificationContent()
        content.title = "Your hymns are right here in your pocket"
        content.body = "Open Hymnal and prepare for Sunday worship."

        var dateComponents = DateComponents()
        dateComponents.weekday = 1 // Sunday
        dateComponents.hour = 7
        dateComponents.minute = 30

        let trigger = UNCalendarNotificationTrigger(dateMatching: dateComponents, repeats: true)
        let request = UNNotificationRequest(identifier: weeklyIdentifier, content: content, trigger: trigger)

        UNUserNotificationCenter.current().add(request) { error in
            if let error {
                print("Error scheduling weekly reminder: \(error.localizedDescription)")
            }
        }
    }

    func scheduleInactivity() {
        let content = UNMutableNotificationContent()
        content.title = "Start your morning with a hymn"
        content.body = "Discover a hymn for today."

        let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 3 * 24 * 60 * 60, repeats: false)
        let request = UNNotificationRequest(identifier: inactivityIdentifier, content: content, trigger: trigger)

        UNUserNotificationCenter.current().add(request) { error in
            if let error {
                print("Error scheduling inactivity nudge: \(error.localizedDescription)")
            }
        }
    }

    func scheduleSeasonal(events: [SeasonalNotificationEvent]) {
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
                if let error {
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
}
