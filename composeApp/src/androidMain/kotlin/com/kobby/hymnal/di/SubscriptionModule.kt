package com.kobby.hymnal.di

import com.kobby.hymnal.core.iap.AndroidSubscriptionManager
import com.kobby.hymnal.core.iap.BillingHelper
import com.kobby.hymnal.core.iap.SubscriptionManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val subscriptionModule = module {
    single { BillingHelper(androidContext()) }
    single<SubscriptionManager> { AndroidSubscriptionManager(androidContext(), get()) }
}

