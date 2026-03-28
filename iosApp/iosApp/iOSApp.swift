import SwiftUI
import FirebaseCore
import FirebaseCrashlytics
import ComposeApp


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
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
