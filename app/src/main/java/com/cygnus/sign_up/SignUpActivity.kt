package com.cygnus.sign_up

import android.os.Bundle
import android.util.SparseArray
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.view.WizardViewStep
import com.cygnus.CygnusApp
import com.cygnus.R
import com.cygnus.model.Credentials
import com.cygnus.model.Student
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlin.reflect.KClass


/**
 * SignUpActivity allows a user to complete their registration.
 *
 * Purpose of this activity is to let users sign up for new accounts.
 *
 * Users are only allowed to sign up if they have a valid [referralCode]
 * Referrals are issued by `School` users for creating `Teacher` account,
 * and by `Teacher` users for creating `Student` accounts.
 *
 * @property accountType The type of user account to be created.
 * @property emailLink The magic link to use for this sign up.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SignUpActivity : AppCompatActivity() {

    private val steps: List<WizardViewStep> = listOf(
            CreateAccountStep(),
            IntroductionStep(),
            ContactInfoStep(),
            PersonalInfoStep()
    )

    lateinit var referralCode: String
    lateinit var accountType: KClass<out User>
    lateinit var emailLink: String

    /**
     * Overrides the onCreate activity lifecycle method.
     *
     * Sign up parameters are received and checked here. Account creation
     * only proceeds if all parameters are correct.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        wizardView.setupWithWizardSteps(supportFragmentManager, steps)
        wizardView.setOnSubmitListener {
            onSubmit(it)
        }

        // Read sign up parameters from intent
        referralCode = intent.getStringExtra(CygnusApp.EXTRA_REFERRAL_CODE) ?: ""
        val accountType = User.valueOf(intent.getStringExtra(CygnusApp.EXTRA_ACCOUNT_TYPE))
        emailLink = intent.data.toString()

        // Sign up cannot proceed if no referral code or account type provided
        if (referralCode.isBlank() || accountType == null) {
            finish()
            return
        }
        this.accountType = accountType

        // Referral code must be still valid
        verifyReferralCode()

    }

    private fun onSubmit(data: SparseArray<Any>) {
        val name = data[R.id.nameField].toString()
        val address = data[R.id.streetField].toString()
        val phone = data[R.id.phoneField].toString()
        val classId = "" // todo: data[R.id.classIdField].toString()

        val email = data[R.id.emailField].toString()
        val password = data[R.id.passwordField].toString()

        val rollNo = "" // todo: data[R.id.rollNoField].toString()
        val dateOfBirth = data[R.id.dateOfBirthField].toString()
        val fatherName = data[R.id.fatherNameField].toString()
        val motherName = data[R.id.motherNameField].toString()

        when (accountType) {
            Student::class -> Student()
            Teacher::class -> Teacher()
            else -> null
        }?.let {
            it.name = name
            it.credentials = Credentials(email, password)
            it.address = address
            it.phone = phone

            when (it) {
                is Student -> {
                    it.classId = classId
                    it.rollNo = rollNo
                    it.dateOfBirth = dateOfBirth
                    it.fatherName = fatherName
                    it.motherName = motherName
                }
                is Teacher -> {
                    it.classId = classId
                }
            }

            onUserCreated(it)
        }
    }

    private fun onUserCreated(user: User) {
        val auth = FirebaseAuth.getInstance()
        if (auth.isSignInWithEmailLink(emailLink)) {
            auth.signInWithEmailLink(user.email, emailLink)
                    .addOnSuccessListener { result ->
                        result.user?.let { firebaseUser ->
                            user.id = firebaseUser.uid
                            FirebaseDatabase.getInstance().reference
                                    .updateChildren(mapOf(
                                            "$referralCode/users/${user.id}/" to user,
                                            "user_schools/${user.id}/" to referralCode)
                                    )
                                    .addOnSuccessListener {
                                        firebaseUser.updatePassword(user.credentials.password)
                                                .addOnSuccessListener {
                                                    Toast.makeText(this, "You are now registered", Toast.LENGTH_LONG).show()
                                                    finish()
                                                }
                                                .addOnFailureListener { ex ->
                                                    Toast.makeText(this, ex.message
                                                            ?: "Sign up failed", Toast.LENGTH_LONG).show()
                                                    result.credential?.let { credential ->
                                                        firebaseUser.reauthenticate(credential)
                                                                .addOnCompleteListener {
                                                                    firebaseUser.delete()
                                                                }
                                                    }
                                                }
                                    }
                                    .addOnFailureListener { ex ->
                                        Toast.makeText(this, ex.message ?: "Sign up failed", Toast.LENGTH_LONG).show()
                                        result.credential?.let { credential ->
                                            firebaseUser.reauthenticate(credential)
                                                    .addOnCompleteListener {
                                                        firebaseUser.delete()
                                                    }
                                        }
                                    }
                        }
                    }
                    .addOnFailureListener { ex ->
                        Toast.makeText(this, ex.message ?: "Sign up failed", Toast.LENGTH_LONG).show()
                    }
        }
    }

    /**
     * Checks the validity of referral code.
     *
     * Validity checks include confirming existence of school in database,
     * checking that the invitation still exists and has a `Pending` state.
     */
    private fun verifyReferralCode() {
        // TODO: Verify the invitation reference
        // FIXME: Sign up is being allowed even if invitation withdrawn
//        CygnusApp.refToInvites(referralCode)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (!snapshot.exists()) {
//                            onVerificationFailure()
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        onVerificationFailure(error.toException().message)
//                    }
//                })
    }

    /**
     * Called when the referral code could not be verified.
     *
     * An error message is displayed and the activity terminates.
     *
     * @param error An (optional) description of cause of failure.
     */
    private fun onVerificationFailure(error: String? = null) {
        Toast.makeText(
                this,
                error ?: "Link verification failed. Please ask your admin to issue a new invitation.",
                Toast.LENGTH_LONG
        ).show()
        finish()
    }

    override fun onBackPressed() {
        if (!wizardView.onBackPressed()) {
            MaterialAlertDialogBuilder(this)
                    .setMessage(getString(R.string.prompt_cancel_signup))
                    .setPositiveButton(android.R.string.yes) { dialog, _ ->
                        super.onBackPressed()
                        dialog.dismiss()
                    }
                    .setNegativeButton(android.R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }
                    .show()
        }
    }

}