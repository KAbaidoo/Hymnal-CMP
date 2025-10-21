package com.kobby.hymnal

import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class ShowOnboarding(private val settings: Settings) {
    private val showOnboarding = settings.getBoolean("onboarding", true)

    private val flow = MutableStateFlow(showOnboarding)

    fun execute(): Flow<Boolean> {
        return flow
    }

}

