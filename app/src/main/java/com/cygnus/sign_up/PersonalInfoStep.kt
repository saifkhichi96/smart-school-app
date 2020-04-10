package com.cygnus.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.google.android.material.textfield.TextInputEditText

class PersonalInfoStep : WizardViewStep("Complete Profile") {

    private lateinit var fatherField: TextInputEditText
    private lateinit var motherField: TextInputEditText
    private lateinit var bloodGroupField: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_personal_info, container, false)

        fatherField = v.findViewById(R.id.fatherNameField)
        motherField = v.findViewById(R.id.motherNameField)
        bloodGroupField = v.findViewById(R.id.bloodGroupField)

        return v
    }

    override fun isDataValid(): Boolean {
        data.put(R.id.fatherNameField, fatherField.text.toString().trim())
        data.put(R.id.motherNameField, motherField.text.toString().trim())
        data.put(R.id.bloodGroupField, bloodGroupField.text.toString().trim())
        return true
    }

}