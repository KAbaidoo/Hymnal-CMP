//
//  IosTraceProvider.swift
//  iosApp
//
//  Created by kobby on 28/03/2026.
//  Copyright © 2026 orgName. All rights reserved.
//

import Foundation
import ComposeApp
import FirebaseAnalytics


class IosTraceProvider: NativeTraceProvider {
    func track(event: String, params: [String : String]) {
        Analytics.logEvent(event, parameters: params)
    }
}
