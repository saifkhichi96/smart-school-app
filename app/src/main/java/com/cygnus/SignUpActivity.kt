package com.cygnus

import android.os.Bundle
import android.util.SparseArray
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import co.aspirasoft.view.WizardViewStep
import kotlinx.android.synthetic.main.activity_sign_up.*


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

    // TODO: Create sign up steps
    private val steps: List<WizardViewStep> = listOf()

    lateinit var referralCode: String
    lateinit var accountType: String
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
            // TODO: Handle form data
        }

        // Read sign up parameters from intent
        referralCode = intent.getStringExtra(CygnusApp.EXTRA_REFERRAL_CODE) ?: ""
        accountType = intent.getStringExtra(CygnusApp.EXTRA_ACCOUNT_TYPE) ?: ""
        emailLink = intent.data.toString()

        // Sign up cannot proceed if no referral code or account type provided
        if (referralCode.isBlank() || accountType.isBlank()) {
            finish()
        }

        // Referral code must be still valid
        verifyReferralCode()

    }

    /**
     * Checks the validity of referral code.
     *
     * Validity checks include confirming existence of school in database,
     * checking that the invitation still exists and has a `Pending` state.
     */
    private fun verifyReferralCode() {
        // TODO: Verify the invitation reference
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
            super.onBackPressed()
        }
    }

}