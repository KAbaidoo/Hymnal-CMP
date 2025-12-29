package com.kobby.hymnal.di

import com.kobby.hymnal.core.iap.IosSubscriptionManager
import com.kobby.hymnal.core.iap.SubscriptionManager
import com.kobby.hymnal.core.iap.SubscriptionStorage
import org.koin.dsl.module

val subscriptionModule = module {
    single { SubscriptionStorage(get()) }
    single<SubscriptionManager> { IosSubscriptionManager(get()) }
}

