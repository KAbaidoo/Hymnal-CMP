package com.kobby.hymnal.core.config

import platform.Foundation.NSLog

interface NativeRemoteConfigProvider {
    suspend fun fetchAndActivate(): Boolean
    fun getString(key: String, defaultValue: String): String
    fun getBoolean(key: String, defaultValue: Boolean): Boolean
    fun getLong(key: String, defaultValue: Long): Long
}

private var nativeProvider: NativeRemoteConfigProvider? = null

fun initializeNativeRemoteConfigProvider(provider: NativeRemoteConfigProvider) {
    nativeProvider = provider
}

class IosRemoteConfigManager : RemoteConfigManager {
    override suspend fun fetchAndActivate(): Boolean {
        return nativeProvider?.fetchAndActivate() ?: false
    }

    override fun getString(key: String, defaultValue: String): String {
        return nativeProvider?.getString(key, defaultValue) ?: defaultValue
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return nativeProvider?.getBoolean(key, defaultValue) ?: defaultValue
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return nativeProvider?.getLong(key, defaultValue) ?: defaultValue
    }
}
