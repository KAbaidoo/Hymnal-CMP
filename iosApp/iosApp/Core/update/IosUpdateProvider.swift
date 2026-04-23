//
//  IosUpdateProvider.swift
//  iosApp
//
//  Created by kobby on 28/03/2026.
//  Copyright © 2026 orgName. All rights reserved.
//

import ComposeApp
import Foundation
@preconcurrency import FirebaseRemoteConfig

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
            "min_required_version": currentVersion as NSString
            ]
        
        self.remoteConfig.setDefaults(defaults)
    }
    
    func checkForUpdates() async throws -> UpdateResult {
        
        return try await withCheckedThrowingContinuation { continuation in
            let remoteConfig = self.remoteConfig
            remoteConfig.fetchAndActivate { _, error in
                if let error = error {
                    print("Remote Config fetch failed (iOS), proceeding with defaults: \(error.localizedDescription)")
                }

                // 1. Resolve versions from Remote Config + App Store
                let currentVersion = Bundle.main.infoDictionary?["CFBundleShortVersionString"] as? String ?? "0.0.0"
                let minRequiredVersion = remoteConfig.configValue(forKey: "min_required_version").stringValue ?? currentVersion
                let bundleId = Bundle.main.bundleIdentifier ?? ""

                Self.fetchLatestAppStoreVersion(bundleId: bundleId) { appStoreVersion in
                    // 2. Compute update state
                    let isMandatory = VersionUtils.shared.isUpdateAvailable(current: currentVersion, latest: minRequiredVersion)
                    let isStoreUpdateAvailable = appStoreVersion.map {
                        VersionUtils.shared.isUpdateAvailable(current: currentVersion, latest: $0)
                    } ?? false

                    print(
                        "Checking for updates (iOS): current=\(currentVersion), appStore=\(appStoreVersion ?? "n/a"), " +
                        "minRequired=\(minRequiredVersion), mandatory=\(isMandatory)"
                    )

                    if (isMandatory || isStoreUpdateAvailable) {
                        continuation.resume(
                            returning: UpdateResult.UpdateAvailable(
                                isMandatory: isMandatory
                            )
                        )
                    } else {
                        continuation.resume(returning: UpdateResult.UpToDate())
                    }
                }
            }
        }
    }

    private static func fetchLatestAppStoreVersion(bundleId: String, completion: @escaping (String?) -> Void) {
        guard !bundleId.isEmpty else {
            completion(nil)
            return
        }

        let endpoint = "https://itunes.apple.com/lookup?bundleId=\(bundleId)"
        guard let encoded = endpoint.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed),
              let url = URL(string: encoded) else {
            completion(nil)
            return
        }

        URLSession.shared.dataTask(with: url) { data, _, error in
            if let error = error {
                print("App Store lookup failed (iOS): \(error.localizedDescription)")
                completion(nil)
                return
            }

            guard let data = data else {
                completion(nil)
                return
            }

            do {
                let response = try JSONDecoder().decode(AppStoreLookupResponse.self, from: data)
                completion(response.results.first?.version)
            } catch {
                print("App Store lookup decode failed (iOS): \(error.localizedDescription)")
                completion(nil)
            }
        }.resume()
    }
    
    private struct AppStoreLookupResponse: Decodable {
        let results: [AppStoreLookupResult]
    }
    
    private struct AppStoreLookupResult: Decodable {
        let version: String
    }
    
}
