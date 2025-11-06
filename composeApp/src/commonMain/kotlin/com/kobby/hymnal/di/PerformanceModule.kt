package com.kobby.hymnal.di

import com.kobby.hymnal.core.performance.PerformanceManager
import com.kobby.hymnal.core.performance.createPerformanceManager
import org.koin.dsl.module

/**
 * Koin module for Performance Monitoring dependencies
 */
val performanceModule = module {
    single<PerformanceManager> { createPerformanceManager() }
}
