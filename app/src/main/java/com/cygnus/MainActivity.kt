package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
                // TODO: Open attendance activity
            }

            classAnnouncementsButton.setOnClickListener {
                // TODO: Open announcements activity
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
                        // TODO: Open an activity where class students can be added/edited
                    }
                }
            }
        } else finish()
    }

    class SubjectAdapter(context: Context, private val subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class)

}