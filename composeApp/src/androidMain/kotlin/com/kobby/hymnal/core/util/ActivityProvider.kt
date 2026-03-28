package com.kobby.hymnal.core.util

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * Utility to provide the current top-most [Activity] to classes that need it (e.g., IAP, ReviewManager).
 */
class ActivityProvider : Application.ActivityLifecycleCallbacks {
    private var currentActivityReference: WeakReference<Activity>? = null

    val currentActivity: Activity?
        get() = currentActivityReference?.get()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivityReference = WeakReference(activity)
    }

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivityReference?.get() == activity) {
            currentActivityReference = null
        }
    }
}
