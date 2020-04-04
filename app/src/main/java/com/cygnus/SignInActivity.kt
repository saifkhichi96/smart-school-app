package com.cygnus

import android.app.ProgressDialog
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.util.InputUtils.isNotBlank
import com.cygnus.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton.setOnClickListener { signIn() }
        signUpButton.setOnClickListener { signUp() }
    }

    /**
     * Overrides the activity lifecycle [AppCompatActivity.onStart] method
     * to check if an existing user signed in at activity startup.
     */
    override fun onStart() {
        super.onStart()
        auth.currentUser?.let { onAuthSuccess(it) }
    }

    /**
     * Starts the sign-in sequence using credentials provided by the user.
     *
     * Authentication is asynchronous, and happens only if all required fields
     * are provided. A blocking UI is displayed while the request is being
     * processed, disallowing all user actions.
     */
    private fun signIn() {
        if (emailField.isNotBlank(true) && passwordField.isNotBlank(true)) {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            val progressDialog = ProgressDialog.show(
                    this,
                    "Please Wait",
                    "Signing you in...",
                    true
            )
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        progressDialog.cancel()
                        if (it.isSuccessful) {
                            it.result?.user?.let { firebaseUser -> onAuthSuccess(firebaseUser) }
                        } else {
                            it.exception?.let { ex -> onFailure(ex) }
                        }
                    }
        }
    }

    /**
     * Redirects to SignUpActivity where user can register for a new account.
     */
    private fun signUp() {
        // TODO: Open sign up activity
    }

    /**
     * Callback for when Firebase authentication succeeds.
     *
     * After user has been successfully authenticated, we need to fetch their
     * details from Firebase database to complete the sign-in process.
     */
    private fun onAuthSuccess(firebaseUser: FirebaseUser) {
        FirebaseDatabase.getInstance()
                .getReference("users/${firebaseUser.uid}/")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            snapshot.getValue(User::class.java)?.let {
                                onSignedIn(it)
                            }
                        } catch (ex: Exception) {
                            onFailure(ex)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onFailure(error.toException())
                    }
                })
    }

    /**
     * Callback for when the sign-in completes.
     *
     * User is automatically redirected to the main screen in the app.
     */
    private fun onSignedIn(user: User) {
        // TODO: Open main activity
    }

    /**
     * Callback for when an error occurs during the sign-in process.
     *
     * This method is called if authentication fails or user's details
     * could not be fetched from the database.
     */
    private fun onFailure(ex: Exception) {
        Toast.makeText(this, ex.message ?: "Sign in failed", Toast.LENGTH_LONG).show()
        auth.signOut()
    }

}