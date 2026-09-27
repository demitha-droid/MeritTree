package com.example.analytics

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics

/**
 * Helper object for Firebase Analytics event tracking.
 * Safely guards against missing Firebase configuration during development.
 */
object BodhiAnalytics {
    private const val TAG = "BodhiAnalytics"
    private var firebaseAnalytics: FirebaseAnalytics? = null

    fun init(context: Context) {
        try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                firebaseAnalytics = FirebaseAnalytics.getInstance(context)
                Log.d(TAG, "Firebase Analytics initialized successfully.")
            } else {
                Log.w(TAG, "FirebaseApp is not initialized. Ensure google-services.json is present in app/ folder.")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to initialize FirebaseAnalytics: ${e.message}")
        }
    }

    fun logEvent(eventName: String, params: (Bundle.() -> Unit)? = null) {
        try {
            val bundle = if (params != null) Bundle().apply(params) else null
            firebaseAnalytics?.logEvent(eventName, bundle)
            Log.d(TAG, "Event logged: $eventName")
        } catch (e: Exception) {
            Log.w(TAG, "Error logging event $eventName: ${e.message}")
        }
    }

    fun logScreenView(screenName: String, screenClass: String = "MainActivity") {
        logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, screenClass)
        }
    }

    fun logMeritCreated(category: String, hasPhoto: Boolean, hasAudio: Boolean, hasVideo: Boolean) {
        logEvent("merit_created") {
            putString("category", category)
            putBoolean("has_photo", hasPhoto)
            putBoolean("has_audio", hasAudio)
            putBoolean("has_video", hasVideo)
        }
    }

    fun logMeritDeleted(category: String) {
        logEvent("merit_deleted") {
            putString("category", category)
        }
    }

    fun logThemeChanged(theme: String) {
        logEvent("theme_changed") {
            putString("theme", theme)
        }
    }

    fun logLanguageChanged(language: String) {
        logEvent("language_changed") {
            putString("language", language)
        }
    }

    fun logBackupExported(meritCount: Int) {
        logEvent("backup_exported") {
            putInt("merit_count", meritCount)
        }
    }

    fun logBackupRestored(meritCount: Int) {
        logEvent("backup_restored") {
            putInt("merit_count", meritCount)
        }
    }
}
