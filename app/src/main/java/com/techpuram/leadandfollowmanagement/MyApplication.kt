package com.techpuram.leadandfollowmanagement

import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        Log.d("MyApplication", "Firebase Analytics initialized")
    }

    companion object {
        @JvmStatic
        lateinit var mFirebaseAnalytics: FirebaseAnalytics

        @JvmStatic // Optional, if you want it callable as a static method from Java
        fun logEvent(event: String, name: String, value: String?) {
            try {
                if (!::mFirebaseAnalytics.isInitialized) {
                    Log.w("MyApplication", "Firebase Analytics not initialized, cannot log event")
                    return
                }

                val bundle = Bundle()
                bundle.putString(if (name.isEmpty()) FirebaseAnalytics.Param.ITEM_NAME else name, value)
                mFirebaseAnalytics.logEvent(
                    if (event.isEmpty()) FirebaseAnalytics.Event.SELECT_CONTENT else event,
                    bundle
                )
                Log.d("MyApplication", "Firebase event logged: $event")
            } catch (e: Exception) {
                Log.e("MyApplication", "Error logging Firebase event: ${e.message}", e)
            }
        }
    }
}