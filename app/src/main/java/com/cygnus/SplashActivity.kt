package com.cygnus

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

/**
 * SplashActivity is first called when the app starts.
 *
 * Purpose of this activity is to display the splash screen of
 * the Cygnus app and to prepare the app for launch.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // auto-start app after 1.5 seconds
        Handler().postDelayed({ startApp() }, 1500L)
    }

    /**
     * Redirect to the first screen in the app.
     */
    private fun startApp() {
        startActivity(Intent(applicationContext, SignInActivity::class.java))
        finish()
    }

}