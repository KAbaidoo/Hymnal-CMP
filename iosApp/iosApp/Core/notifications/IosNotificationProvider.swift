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

    func scheduleSeasonal() {
        let currentYear = Calendar.current.component(.year, from: Date())
        let years = [currentYear, currentYear + 1]

        years.forEach { year in
            seasonalEvents(for: year).forEach { event in
                let content = UNMutableNotificationContent()
                content.title = event.title
                content.body = event.body

                var components = DateComponents()
                components.year = event.year
                components.month = event.month
                components.day = event.day
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

    private func seasonalEvents(for year: Int) -> [(id: String, title: String, body: String, year: Int, month: Int, day: Int)] {
        let easter = easterSunday(year: year)
        let palmSunday = Calendar.current.date(byAdding: .day, value: -7, to: easter)!
        let goodFriday = Calendar.current.date(byAdding: .day, value: -2, to: easter)!
        let ascension = Calendar.current.date(byAdding: .day, value: 39, to: easter)!
        let pentecost = Calendar.current.date(byAdding: .day, value: 49, to: easter)!

        return [
            buildEvent(id: "seasonal_\(year)_palm_sunday", title: "Palm Sunday is here", body: "Open Hymnal for Palm Sunday hymns.", date: palmSunday),
            buildEvent(id: "seasonal_\(year)_good_friday", title: "Good Friday hymns", body: "Prepare for Good Friday with the right hymns.", date: goodFriday),
            buildEvent(id: "seasonal_\(year)_easter", title: "He is risen", body: "Find Easter hymns for today’s service.", date: easter),
            buildEvent(id: "seasonal_\(year)_ascension", title: "Ascension Day hymns", body: "Open Hymnal and prepare for Ascension worship.", date: ascension),
            buildEvent(id: "seasonal_\(year)_pentecost", title: "Pentecost hymns", body: "Prepare your Pentecost hymn set.", date: pentecost),
            (id: "seasonal_\(year)_christmas", title: "Christmas hymns ready", body: "Celebrate with your Christmas hymn selections.", year: year, month: 12, day: 25)
        ]
    }

    private func buildEvent(id: String, title: String, body: String, date: Date) -> (id: String, title: String, body: String, year: Int, month: Int, day: Int) {
        let components = Calendar.current.dateComponents([.year, .month, .day], from: date)
        return (id: id, title: title, body: body, year: components.year ?? 0, month: components.month ?? 1, day: components.day ?? 1)
    }

    private func easterSunday(year: Int) -> Date {
        let a = year % 19
        let b = year / 100
        let c = year % 100
        let d = b / 4
        let e = b % 4
        let f = (b + 8) / 25
        let g = (b - f + 1) / 3
        let h = (19 * a + b - d - g + 15) % 30
        let i = c / 4
        let k = c % 4
        let l = (32 + 2 * e + 2 * i - h - k) % 7
        let m = (a + 11 * h + 22 * l) / 451
        let month = (h + l - 7 * m + 114) / 31
        let day = ((h + l - 7 * m + 114) % 31) + 1

        var components = DateComponents()
        components.year = year
        components.month = month
        components.day = day
        components.hour = 12
        return Calendar.current.date(from: components) ?? Date()
    }
}
