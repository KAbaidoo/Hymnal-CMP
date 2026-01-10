package com.kobby.hymnal.di

import com.kobby.hymnal.core.iap.AndroidPurchaseManager
import com.kobby.hymnal.core.iap.BillingHelper
import com.kobby.hymnal.core.iap.PurchaseManager
import com.kobby.hymnal.core.iap.PurchaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val subscriptionModule = module {
    single { BillingHelper(androidContext()) }
    single { PurchaseStorage(get()) }
    single<PurchaseManager> { AndroidPurchaseManager(androidContext(), get(), get()) }
}

