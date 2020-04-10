package com.cygnus

import android.os.Bundle
import android.view.MenuItem
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.AddStudentDialog
import kotlinx.android.synthetic.main.activity_attendance.toolbar
import kotlinx.android.synthetic.main.activity_students.*

class StudentsActivity : SecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}
