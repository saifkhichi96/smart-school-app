package com.cygnus

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cygnus.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * SecureActivity is an abstract activity which restricts usage to signed in users.
 *
 * Extend this class to make an activity secure. A `secured` activity will only be
 * allowed to open if a [FirebaseUser] is authenticated and the [User] instance of
 * signed in user is passed to the activity intent with [CygnusApp.EXTRA_USER] tag.
 */
abstract class SecureActivity : AppCompatActivity() {

    protected lateinit var currentUser: User

    /**
     * Overrides the onCreate activity lifecycle method.
     *
     * All authentication checks are performed here, and activity is terminated
     * if the checks fail.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val signedInUser = FirebaseAuth.getInstance().currentUser       // firebase auth -> (1)
        val user = intent.getSerializableExtra(CygnusApp.EXTRA_USER) as User? // account details -> (2)
        currentUser = when {
            signedInUser == null || user == null -> return finish() // both (1) and (2) must exist
            user.id == signedInUser.uid -> user                     // and both must belong to same user
            else -> return finish()                                 // else finish activity
        }
    }

    /**
     * Overrides the onStart activity lifecycle method.
     *
     * This is only called if all authentication checks passed. We can safely
     * use our [currentUser] object here.
     */
    override fun onStart() {
        super.onStart()
        updateUI(currentUser)
    }

    /**
     * Implement this method to update UI for signed in user, if needed.
     */
    abstract fun updateUI(currentUser: User)

}