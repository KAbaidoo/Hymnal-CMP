package com.kobby.hymnal.di

import com.kobby.hymnal.core.iap.IosSubscriptionManager
import com.kobby.hymnal.core.iap.SubscriptionManager
import org.koin.dsl.module

val subscriptionModule = module {
    single<SubscriptionManager> { IosSubscriptionManager() }
}

