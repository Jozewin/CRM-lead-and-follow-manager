package com.techpuram.leadandfollowmanagement.util

import android.content.Context
import android.content.SharedPreferences
import java.util.concurrent.TimeUnit

/**
 * Manages app preferences and first-time usage tracking
 */
object PreferenceManager {
    private const val PREF_NAME = "leadAndFollowup"
    private const val KEY_FIRST_TIME_USER = "first_time_user"
    private const val KEY_FIRST_LAUNCH_DATE = "first_launch_date"
    private const val KEY_SHOW_ADS = "show_ads"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Checks if this is the first time the user has opened the app
     */
    fun isFirstTimeUser(context: Context): Boolean {
        val prefs = getPrefs(context)
        // Default to true if preference doesn't exist yet
        return prefs.getBoolean(KEY_FIRST_TIME_USER, true)
    }

    /**
     * Marks that the user has completed the first-time setup
     */
    fun setFirstTimeDone(context: Context) {
        val prefs = getPrefs(context)
        val editor = prefs.edit()

        // Mark first time as done
        editor.putBoolean(KEY_FIRST_TIME_USER, false)

        // Set the first launch date if not already set
        if (!prefs.contains(KEY_FIRST_LAUNCH_DATE)) {
            editor.putLong(KEY_FIRST_LAUNCH_DATE, System.currentTimeMillis())
        }

        editor.apply()
    }

    /**
     * Determines if ads should be shown based on the elapsed time since first launch
     * Returns true if more than 1 day has passed since first launch (2nd day onwards)
     */
    fun shouldShowAds(context: Context): Boolean {
        val prefs = getPrefs(context)

        // Get the first launch date
        val firstLaunchDate = prefs.getLong(KEY_FIRST_LAUNCH_DATE, 0)
        if (firstLaunchDate == 0L) {
            // No first launch date recorded yet, record it now and don't show ads
            prefs.edit().putLong(KEY_FIRST_LAUNCH_DATE, System.currentTimeMillis()).apply()
            return false
        }

        // Calculate days since first launch
        val currentTime = System.currentTimeMillis()
        val daysSinceFirstLaunch = TimeUnit.MILLISECONDS.toDays(currentTime - firstLaunchDate)

        // Show ads if more than 1 day has passed (2nd day onwards)
        val shouldShow = daysSinceFirstLaunch >= 1

        // Cache the decision to avoid recalculating
        if (shouldShow && !prefs.contains(KEY_SHOW_ADS)) {
            prefs.edit().putBoolean(KEY_SHOW_ADS, true).apply()
        }

        return shouldShow
    }

    /**
     * Resets all preferences (for testing purposes)
     */
    fun resetPreferences(context: Context) {
        val prefs = getPrefs(context)
        prefs.edit().clear().apply()
    }
}