package com.funkyidol.mlkitbarcodescanner

import android.app.Application
import com.google.firebase.FirebaseApp
import timber.log.Timber

class BarcodeScannerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        Timber.plant(Timber.DebugTree())
    }
}