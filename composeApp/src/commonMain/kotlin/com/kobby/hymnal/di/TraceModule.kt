package com.kobby.hymnal.di

import com.kobby.hymnal.core.trace.TraceManager
import com.kobby.hymnal.core.trace.createTraceManager
import org.koin.dsl.module

val traceModule = module {
    single<TraceManager> { createTraceManager() }
}
