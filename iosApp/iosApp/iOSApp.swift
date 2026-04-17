import SwiftUI
import FirebaseCore
import FirebaseCrashlytics
import ComposeApp

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(IosPushNotificationCoordinator.self)
    var pushNotificationCoordinator

    init() {
        FirebaseApp.configure()

        #if DEBUG
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(false)
        #else
        Crashlytics.crashlytics().setCrashlyticsCollectionEnabled(true)
        #endif

        CrashlyticsManager_iosKt.initializeNativeCrashlyticsProvider(provider: IosCrashlyticsProvider())
        #if DEBUG
        TraceManager_iosKt.initializeNativeTraceProvider(
            provider: IosTraceProvider(),
            debugLoggingEnabled: true
        )
        #else
        TraceManager_iosKt.initializeNativeTraceProvider(
            provider: IosTraceProvider(),
            debugLoggingEnabled: false
        )
        #endif

        PurchaseManager_iosKt.initializeNativePurchaseProvider(provider: IosPurchaseProvider())
        IosUpdateManager_iosKt.initializeNativeUpdateProvider(provider: IosUpdateProvider())
        NotificationManager_iosKt.initializeNativeNotificationProvider(provider: IosNotificationProvider())
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
