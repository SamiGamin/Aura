package com.stokia.aura

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.memoryCacheSettings
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class AuraApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        // Disable Firestore persistence — memory-only cache (zero local storage)
         // Firebase Firestore optimizations
        val settings = firestoreSettings {
            // Disable offline persistence to guarantee zero local storage of sensitive data
            setLocalCacheSettings(memoryCacheSettings {})
        }
        FirebaseFirestore.getInstance().firestoreSettings = settings

        // Inicializar Cloudinary
        val cloudinaryConfig = mapOf(
            "cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME,
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to BuildConfig.CLOUDINARY_API_SECRET
        )
        MediaManager.init(this, cloudinaryConfig)
    }
}
