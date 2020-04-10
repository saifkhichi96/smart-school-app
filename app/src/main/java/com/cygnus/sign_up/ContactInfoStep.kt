package com.cygnus.sign_up

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.google.android.material.textfield.TextInputEditText

class ContactInfoStep : WizardViewStep("Create Profile") {

    private lateinit var emailField: TextInputEditText
    private lateinit var streetField: TextInputEditText
    private lateinit var cityField: TextInputEditText
    private lateinit var stateField: TextInputEditText
    private lateinit var postalCodeField: TextInputEditText
    private lateinit var countryField: TextInputEditText
    private lateinit var phoneField: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_contact_info, container, false)

        emailField = v.findViewById(R.id.emailField)
        emailField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                emailField.showError(null)
                if (!s.toString().trim().isEmail()) {
                    emailField.showError(getString(R.string.error_email_invalid))
                }
            }
        })

        streetField = v.findViewById(R.id.streetField)
        cityField = v.findViewById(R.id.cityField)
        stateField = v.findViewById(R.id.stateField)
        postalCodeField = v.findViewById(R.id.postalCodeField)
        countryField = v.findViewById(R.id.countryField)

        phoneField = v.findViewById(R.id.phoneField)

        return v
    }

    override fun isDataValid(): Boolean {
        return if (emailField.isNotBlank(true) &&
                streetField.isNotBlank(true) &&
                cityField.isNotBlank(true) &&
                stateField.isNotBlank(true) &&
                postalCodeField.isNotBlank(true) &&
                countryField.isNotBlank(true) &&
                phoneField.isNotBlank(true)) {

            val inputEmail = emailField.text.toString().trim()
            if (inputEmail.isEmail()) {
                data.put(R.id.emailField, inputEmail)

                data.put(R.id.streetField, streetField.text.toString().trim())
                data.put(R.id.cityField, cityField.text.toString().trim())
                data.put(R.id.stateField, stateField.text.toString().trim())
                data.put(R.id.postalCodeField, postalCodeField.text.toString().trim())
                data.put(R.id.countryField, countryField.text.toString().trim())

                data.put(R.id.phoneField, phoneField.text.toString().trim())
                true
            } else {
                emailField.showError(getString(R.string.error_email_invalid))
                false
            }
        } else {
            false
        }
    }

}