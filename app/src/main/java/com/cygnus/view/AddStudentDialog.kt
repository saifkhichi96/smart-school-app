package com.cygnus.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import co.aspirasoft.util.InputUtils.isEmail
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import com.cygnus.R
import com.cygnus.tasks.InvitationTask
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.dialog_add_student.*

class AddStudentDialog(context: Context, private val teacherId: String, private val classId: String) : Dialog(context) {

    private lateinit var schoolId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_add_student)
        okButton.setOnClickListener { onOk() }

        okButton.isEnabled = false
        FirebaseDatabase.getInstance()
                .getReference("user_schools/${teacherId}")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val schoolId = snapshot.getValue(String::class.java)
                        if (schoolId != null) {
                            this@AddStudentDialog.schoolId = schoolId
                            okButton.isEnabled = true
                        } else {
                            dismiss()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        dismiss()
                    }
                })
    }

    private fun onOk() {
        if (emailField.isNotBlank(true) && rollNoField.isNotBlank(true)) {
            setCancelable(false)
            okButton.isEnabled = false

            val rollNo = rollNoField.text.toString().trim()
            // TODO: Roll number must be unique
//            FirebaseDatabase.getInstance().getReference("$referralCode/users/")
//                    .orderByValue()
//                    .equalTo(user.rollNo, "rollNo")
//                    .addListenerForSingleValueEvent(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            if (snapshot.exists()) {
//                                onFailure("")
//                            }
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//                    })


            val inputEmail = emailField.text.toString().trim()
            if (inputEmail.isEmail()) {
                InvitationTask(context, schoolId, inputEmail, classId, rollNo).start { task ->
                    setCancelable(true)
                    okButton.isEnabled = true

                    if (task.isSuccessful) {
                        Snackbar.make(
                                emailField,
                                context.getString(R.string.status_invitation_sent),
                                Snackbar.LENGTH_LONG
                        ).show()
                        emailField.setText("")
                    } else {
                        emailField.showError("Email ${task.exception?.message ?: "could not be sent"}.")
                    }
                }
            } else {
                emailField.showError(context.getString(R.string.error_email_invalid))
            }

            dismiss()
        }
    }

}