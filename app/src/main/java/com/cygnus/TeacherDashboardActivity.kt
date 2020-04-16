package com.cygnus

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardActivity
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.Subject
import com.cygnus.model.Teacher
import com.cygnus.model.User
import com.cygnus.view.SubjectView
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_dashboard_teacher.*

/**
 * TeacherDashboardActivity is the teachers' homepage.
 *
 * This is the dashboard which is first displayed when a [Teacher]
 * user signs into the app. All actions for class teachers and
 * other teachers are defined in this activity.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class TeacherDashboardActivity : DashboardActivity() {

    private lateinit var currentTeacher: Teacher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_teacher)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // Only allow a signed in teacher to access this page
        currentTeacher = when (currentUser) {
            is Teacher -> currentUser as Teacher
            else -> {
                finish()
                return
            }
        }

        // Set up click listeners
        attendanceButton.setOnClickListener { startSecurely(AttendanceActivity::class.java) }
        classAnnouncementsButton.setOnClickListener { startSecurely(NoticeActivity::class.java) }
        manageStudentsButton.setOnClickListener { startSecurely(StudentsActivity::class.java) }
    }

    /**
     * Displays the signed in user's details.
     */
    override fun updateUI(currentUser: User) {
        getSubjectsList()
        if (currentTeacher.isClassTeacher()) {
            classTeacherCard.visibility = View.VISIBLE
            className.text = currentTeacher.classId
            getStudentCount()
        } else {
            classTeacherCard.visibility = View.GONE
        }
    }

    private fun getStudentCount() {
        onStudentCountReceived(0)
        currentTeacher.classId?.let { classId ->
            UsersDao.getStudentsInClass(schoolId, classId, OnSuccessListener {
                onStudentCountReceived(it.size)
            })
        }
    }

    private fun onStudentCountReceived(count: Int) {
        studentCount.text = String.format(getString(R.string.ph_student_count), count)
    }

    /**
     * Gets a list of courses from database taught by [currentTeacher].
     */
    private fun getSubjectsList() {
        SubjectsDao.getSubjectsByTeacher(schoolId, currentTeacher.email, OnSuccessListener {
            onSubjectsReceived(it)
        })
    }

    private fun onSubjectsReceived(subjects: List<Subject>) {
        coursesList.adapter = SubjectAdapter(this, subjects)
    }

    private inner class SubjectAdapter(context: Context, val subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                val subject = subjects[position]
                startSecurely(SubjectActivity::class.java, Intent().apply {
                    putExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT, subject)
                    putExtra(CygnusApp.EXTRA_EDITABLE_MODE, true)
                })
            }
            return v
        }

    }

}