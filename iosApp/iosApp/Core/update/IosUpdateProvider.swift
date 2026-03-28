//
//  IosUpdateProvider.swift
//  iosApp
//
//  Created by kobby on 28/03/2026.
//  Copyright © 2026 orgName. All rights reserved.
//

import ComposeApp
import Foundation
import FirebaseRemoteConfig



class IosUpdateProvider: NativeUpdateProvider {
    
    private let remoteConfig: RemoteConfig
    
    init(){
        self.remoteConfig = RemoteConfig.remoteConfig()
        
        let settings = RemoteConfigSettings()
        #if DEBUG
        settings.minimumFetchInterval = 0
        #else
        settings.minimumFetchInterval = 43200
        #endif
        RemoteConfig.remoteConfig().configSettings = settings
        
        let currentVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0.0.0"
        let defaults: [String: NSObject] = [
            "latest_version": currentVersion as NSString,
            "min_required_version": currentVersion as NSString
            ]
        
        self.remoteConfig.setDefaults(defaults)
    }
    
    func checkForUpdates() async throws -> UpdateResult {
        
        return try await withCheckedThrowingContinuation { continuation in
            remoteConfig.fetchAndActivate { [weak self] status, error in
                guard let self = self else { return }
                
                if let error = error {
                    continuation.resume(returning: UpdateResult.Error(message: error.localizedDescription))
                    return
                }
                
                // 1. Fetch values from Firebase
                let latestVersion = self.remoteConfig.configValue(forKey: "latest_version").stringValue ?? "0.0.0"
                let minRequiredVersion = self.remoteConfig.configValue(forKey: "min_required_version").stringValue ?? "0.0.0"
                
                // 2. Get current version
                let currentVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0.0.0"
                
                print("Checking for updates (iOS): current=\(currentVersion), latest=\(latestVersion), minRequired=\(minRequiredVersion)")
                
                // 3. Use shared VersionUtils logic
                let isUpdateAvailable = VersionUtils.shared.isUpdateAvailable(current: currentVersion, latest: latestVersion)
                let isMandatory = VersionUtils.shared.isUpdateAvailable(current: currentVersion, latest: minRequiredVersion)
                
                // 4. Map back to Kotlin Sealed Class types
                if isUpdateAvailable {
                    continuation.resume(returning: UpdateResult.UpdateAvailable(latestVersion: latestVersion, isMandatory: isMandatory))
                } else {
                    continuation.resume(returning: UpdateResult.UpToDate())
                }
            }
        }
    }

    
}

