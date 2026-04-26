import ComposeApp
import Foundation
@preconcurrency import FirebaseRemoteConfig

class IosRemoteConfigProvider: NativeRemoteConfigProvider {
    
    private let remoteConfig: RemoteConfig
    
    init() {
        self.remoteConfig = RemoteConfig.remoteConfig()
        
        let settings = RemoteConfigSettings()
        #if DEBUG
        settings.minimumFetchInterval = 0
        #else
        settings.minimumFetchInterval = 43200 // 12 hours
        #endif
        self.remoteConfig.configSettings = settings
    }
    
    func fetchAndActivate() async throws -> KotlinBoolean {
        return try await withCheckedThrowingContinuation { continuation in
            self.remoteConfig.fetchAndActivate { status, error in
                if let error = error {
                    print("Remote Config fetch failed (iOS): \(error.localizedDescription)")
                    continuation.resume(returning: KotlinBoolean(value: false))
                } else {
                    continuation.resume(returning: KotlinBoolean(value: status == .successFetchedFromRemote || status == .successUsingPreFetchedData))
                }
            }
        }
    }
    
    func getString(key: String, defaultValue: String) -> String {
        let value = self.remoteConfig.configValue(forKey: key).stringValue ?? ""
        return value.isEmpty ? defaultValue : value
    }
    
    func getBoolean(key: String, defaultValue: Bool) -> Bool {
        let value = self.remoteConfig.configValue(forKey: key)
        return value.source == .static ? defaultValue : value.boolValue
    }
    
    func getLong(key: String, defaultValue: Int64) -> Int64 {
        let value = self.remoteConfig.configValue(forKey: key)
        return value.source == .static ? defaultValue : value.numberValue.int64Value
    }
}
