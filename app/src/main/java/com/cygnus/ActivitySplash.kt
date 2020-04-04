package com.cygnus

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase

/**
 * SplashActivity is first called when the app starts.
 *
 * Purpose of this activity is to display the splash screen of
 * the Cygnus app and to prepare the app for launch.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class ActivitySplash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        Handler().postDelayed({
            startApp()
        }, 1500L)
    }

    private fun startApp() {
        // TODO: Start the application
    }

}