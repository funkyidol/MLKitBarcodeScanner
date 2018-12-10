package com.funkyidol.mlkitbarcodescanner

import android.app.Application
import com.google.firebase.FirebaseApp

class BarcodeScannerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}