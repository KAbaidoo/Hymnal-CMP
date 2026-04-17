package com.kobby.hymnal.core.update

import com.kobby.hymnal.core.sharing.ShareConstants
import platform.Foundation.NSLog

class IosUpdateManager : UpdateManager {

    override suspend fun checkForUpdates(): UpdateResult {
        NSLog("Checking for updates on iOS via NativeUpdateProvider")
        return nativeUpdateProvider?.checkForUpdates() ?: UpdateResult.UpToDate
    }

    override fun getUpdateUrl(): String {
        return ShareConstants.IOS_APP_STORE_URL
    }
}

interface NativeUpdateProvider {
    suspend fun checkForUpdates(): UpdateResult
}

private var nativeUpdateProvider: NativeUpdateProvider? = null

fun initializeNativeUpdateProvider(provider: NativeUpdateProvider) {
    nativeUpdateProvider = provider
}
