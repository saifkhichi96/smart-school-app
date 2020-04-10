package com.cygnus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.model.*
import com.cygnus.view.AccountSwitcher
import com.cygnus.view.SubjectView
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : SecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // TODO: Replace these placeholder courses with actual courses from database
        val formatter = SimpleDateFormat("hh:mma", Locale.getDefault())
        val ds = Timestamp(formatter.parse("10:00am")!!.time)
        val de = Timestamp(formatter.parse("11:30am")!!.time)

        if (currentUser is Teacher) {
            val teacher = currentUser as Teacher
            coursesList.adapter = SubjectAdapter(this, listOf(
                    Subject("Mathematics", currentUser.name, teacher.classId ?: "Class").apply {
                        addAppointment(1, ds, de, "48-110")
                        addAppointment(4, ds, de, "46-280")
                    },
                    Subject("English", currentUser.name, teacher.classId ?: "Class").apply {
                        addAppointment(3, ds, de, "42-220")
                    }
            ))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_open_account -> {
                AccountSwitcher.Builder(this)
                        .setUser(currentUser)
                        .show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {
        if (currentUser !is School) {
            attendanceButton.setOnClickListener {
                startActivity(Intent(this, AttendanceActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

            classAnnouncementsButton.setOnClickListener {
                startActivity(Intent(this, NoticeActivity::class.java))
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            }

            when (currentUser) {
                is Student -> {
                    headline.text = "You are enrolled in"
                    className.text = currentUser.classId
                    attendanceButton.text = "See Attendance"
                }

                is Teacher -> {
                    headline.text = "You are assigned to"
                    className.text = currentUser.classId
                    attendanceButton.text = "Mark Attendance"

                    studentCount.visibility = View.VISIBLE
                    // TODO: Update student count from db

                    manageStudentsButton.visibility = View.VISIBLE
                    manageStudentsButton.setOnClickListener {
                        startActivity(Intent(this, StudentsActivity::class.java).apply {
                            putExtra(CygnusApp.EXTRA_USER, currentUser)
                        })
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    }
                }
            }
        } else finish()
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back button twice to exit.", Toast.LENGTH_SHORT).show()
        Handler().postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    class SubjectAdapter(context: Context, private val subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class)

}