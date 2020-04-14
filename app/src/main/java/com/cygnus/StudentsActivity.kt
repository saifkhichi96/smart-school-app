package com.cygnus

import android.os.Bundle
import com.cygnus.core.DashboardChildActivity
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.AddStudentDialog
import kotlinx.android.synthetic.main.activity_students.*

class StudentsActivity : DashboardChildActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        try {
            val classId = (currentUser as Teacher).classId!!
            addStudentButton.setOnClickListener {
                AddStudentDialog(this, currentUser.id, classId).show()
            }
        } catch (ex: Exception) {
            finish()
        }
    }

    override fun updateUI(currentUser: User) {

    }

}