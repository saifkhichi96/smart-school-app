package com.cygnus.sign_up

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.cygnus.model.Teacher
import com.google.android.material.textfield.TextInputEditText

class CreateAccountStep : WizardViewStep("Welcome to Cygnus") {

    private lateinit var passwordField: TextInputEditText
    private lateinit var passwordRepeatField: TextInputEditText
    private lateinit var signUpWelcomeMessage: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_create_account, container, false)

        passwordField = v.findViewById(R.id.passwordField)
        passwordField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordField.isNotBlank(true)
            }
        })

        passwordRepeatField = v.findViewById(R.id.passwordRepeatField)
        passwordRepeatField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                passwordRepeatField.isNotBlank(true)
                checkPasswordValid()
            }
        })

        signUpWelcomeMessage = v.findViewById(R.id.signUpMessage)

        return v
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it as SignUpActivity
            signUpWelcomeMessage.text = String.format(
                    it.getString(R.string.sign_up_welcome_msg),
                    if (it.accountType == Teacher::class) "Respected" else "Dear", // Greeting
                    it.accountType.simpleName, // Account Type
                    if (it.accountType == Teacher::class) "school" else "class teacher" // Greeting
            )
        }
    }

    private fun checkPasswordValid(): Boolean {
        passwordField.showError(null)
        if (passwordField.text.isNullOrBlank() || passwordField.text.toString() != passwordRepeatField.text.toString()) {
            passwordField.showError(getString(R.string.error_password_mismatch))
            return false
        }

        return true
    }

    override fun isDataValid(): Boolean {
        return if (passwordField.isNotBlank(true) &&
                passwordRepeatField.isNotBlank(true) &&
                checkPasswordValid()) {
            data.put(R.id.passwordField, passwordField.text.toString().trim())
            true
        } else {
            false
        }
    }

}