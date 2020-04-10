package com.cygnus.sign_up

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.view.WizardViewStep
import com.cygnus.R
import com.cygnus.model.Student
import com.cygnus.model.Teacher
import com.google.android.material.textfield.TextInputEditText

class IntroductionStep : WizardViewStep("Say Hi") {

    private lateinit var userImage: ImageView
    private lateinit var nameField: TextInputEditText
    private lateinit var dateOfBirth: TextInputEditText

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.signup_step_introduction, container, false)

        userImage = v.findViewById(R.id.userImage)
        // TODO: Allow user to upload an image

        nameField = v.findViewById(R.id.nameField)
        dateOfBirth = v.findViewById(R.id.dateOfBirthField)
        // TODO: Allow selection of gender

        return v
    }

    override fun onStart() {
        super.onStart()
        activity?.let {
            it as SignUpActivity
            when (it.accountType) {
                Student::class -> userImage.setImageResource(R.drawable.ph_student)
                Teacher::class -> userImage.setImageResource(R.drawable.ph_teacher)
            }
        }
    }

    override fun isDataValid(): Boolean {
        return if (nameField.isNotBlank(true)) {
            data.put(R.id.dateOfBirthField, dateOfBirth.text.toString().trim())
            data.put(R.id.nameField, nameField.text.toString().trim())
            true
        } else {
            false
        }
    }

}