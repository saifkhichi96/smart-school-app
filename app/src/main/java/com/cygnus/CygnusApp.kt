package com.cygnus

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class CygnusApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Enabling persistence speeds up app by caching data locally
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

}