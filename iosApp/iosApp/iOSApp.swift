import SwiftUI
import FirebaseCore
import FirebaseCrashlytics

@main
struct iOSApp: App {
    
    init() {
           FirebaseApp.configure()
           
           // Enable Crashlytics only in release builds
           #if DEBUG
           Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(false)
           #else
           Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
           #endif
       }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
