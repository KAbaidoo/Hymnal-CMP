package com.kobby.hymnal.core.trace

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.kobby.hymnal.BuildConfig

/**
 * Android tracing implementation backed by Firebase Analytics.
 */
class AndroidTraceManager : TraceManager {
    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }

    override fun track(event: String, params: Map<String, String>) {
        val safeEvent = TraceSanitizer.sanitizeEventName(event)
        val safeParams = TraceSanitizer.sanitizeParams(params)

        if (BuildConfig.DEBUG) {
            Log.d("TraceManager", "event=$safeEvent params=$safeParams")
        }

        analytics.logEvent(safeEvent) {
            safeParams.forEach { (key, value) ->
                param(key, value)
            }
        }
    }
}

actual fun createTraceManager(): TraceManager = AndroidTraceManager()
