package com.kobby.hymnal.di

import com.kobby.hymnal.core.iap.IosPurchaseManager
import com.kobby.hymnal.core.iap.PurchaseManager
import com.kobby.hymnal.core.iap.PurchaseStorage
import org.koin.dsl.module

val subscriptionModule = module {
    single { PurchaseStorage(get()) }
    single<PurchaseManager> { IosPurchaseManager(get()) }
}

