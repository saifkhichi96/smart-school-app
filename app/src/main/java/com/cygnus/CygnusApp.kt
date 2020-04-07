package com.cygnus

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * An [Application] subclass represents this application.
 *
 * Purpose of this class is to define default behaviours, perform
 * SDK initializations and declare any shared data.
 */
class CygnusApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Enabling persistence speeds up app by caching data locally
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    companion object {
        // the EXTRA_* strings are used as tags to pass
        // data between activities using Intents
        const val EXTRA_USER = "user"
    }

}