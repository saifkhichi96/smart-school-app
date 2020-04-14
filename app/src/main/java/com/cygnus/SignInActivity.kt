package com.cygnus

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.util.InputUtils.isNotBlank
import com.cygnus.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_in.*

/**
 * SignInActivity is first displayed to all new users.
 *
 * Purpose of this activity is to let users log into the app
 * using their credentials.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SignInActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signInButton.setOnClickListener { signIn() }
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
     * Callback for when Firebase authentication succeeds.
     *
     * After user has been successfully authenticated, we need to fetch their
     * details from Firebase database to complete the sign-in process.
     */
    private fun onAuthSuccess(firebaseUser: FirebaseUser) {
        val db = FirebaseDatabase.getInstance()
        db.getReference("${firebaseUser.uid}/name/")
                .addListenerForSingleValueEvent(object : SignInEventListener() {
                    override fun onDataReceived(snapshot: DataSnapshot) {
                        when {
                            // when a `School` signs in
                            snapshot.exists() -> onSignedInAsSchool(snapshot.value.toString(), firebaseUser)

                            // when a regular user signs in, we first need to find out which school
                            // they are associated with by retrieving their school's id
                            else -> db.getReference("user_schools/${firebaseUser.uid}")
                                    .addListenerForSingleValueEvent(object : SignInEventListener() {
                                        override fun onDataReceived(snapshot: DataSnapshot) {
                                            snapshot.getValue(String::class.java)?.let { schoolId ->
                                                onSchoolDetailsReceived(schoolId, firebaseUser)
                                            }
                                        }
                                    })
                        }
                    }
                })
    }

    /**
     * Callback for when details of [firebaseUser]'s school are received.
     */
    private fun onSchoolDetailsReceived(schoolId: String, firebaseUser: FirebaseUser) {
        FirebaseDatabase.getInstance()
                .getReference("$schoolId/users/${firebaseUser.uid}/")
                .addListenerForSingleValueEvent(object : SignInEventListener() {
                    override fun onDataReceived(snapshot: DataSnapshot) {
                        snapshot.getValue(when ((snapshot.value as HashMap<*, *>)["type"]) {
                            Student::class.simpleName -> Student::class.java
                            Teacher::class.simpleName -> Teacher::class.java
                            else -> School::class.java
                        })?.let { user -> onSignedIn(user, schoolId) }
                    }
                })
    }

    /**
     * Callback for when the sign-in completes.
     *
     * User is automatically redirected to the appropriate screen in the app.
     */
    private fun onSignedIn(user: User, schoolId: String) {
        startActivity(Intent(
                applicationContext,
                when (user) {
                    is School -> SchoolDashboardActivity::class.java
                    is Teacher -> TeacherDashboardActivity::class.java
                    else -> StudentDashboardActivity::class.java
                }
        ).apply {
            putExtra(CygnusApp.EXTRA_USER, user)
            putExtra(CygnusApp.EXTRA_SCHOOL, schoolId)
        })
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }

    /**
     * Callback for a `School` signs in.
     *
     * @param name name of the school
     * @param account Firebase account of the school
     */
    private fun onSignedInAsSchool(name: String, account: FirebaseUser) {
        onSignedIn(School(
                account.uid,
                name,
                Credentials(account.email!!, "")
        ), account.uid)
    }

    /**
     * Callback for when an error occurs during the sign-in process.
     *
     * This method is called if authentication fails or user's details
     * could not be fetched from the database.
     */
    private fun onFailure(ex: Exception?) {
        Toast.makeText(this, ex?.message ?: "Sign in failed", Toast.LENGTH_LONG).show()
        auth.signOut()
    }

    private abstract inner class SignInEventListener : ValueEventListener {
        abstract fun onDataReceived(snapshot: DataSnapshot)

        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                onDataReceived(snapshot)
            } catch (ex: Exception) {
                onFailure(ex)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            onFailure(error.toException())
        }
    }

}