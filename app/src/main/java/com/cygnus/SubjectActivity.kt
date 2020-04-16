package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.View
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.Lecture
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.view.AddLectureDialog
import com.cygnus.view.LectureView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_subjects.*

/**
 * SubjectActivity shows details of a [Subject].
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SubjectActivity : DashboardChildActivity() {

    private lateinit var subject: Subject
    private var editable: Boolean = false

    private var appointmentsAdapter: AppointmentsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subjects)

        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()
        editable = intent.getBooleanExtra(CygnusApp.EXTRA_EDITABLE_MODE, editable)
        if (editable) {
            addAppointmentButton.visibility = View.VISIBLE
            addMaterialButton.visibility = View.VISIBLE
        }

        addAppointmentButton.setOnClickListener {
            AddLectureDialog.newInstance(schoolId, subject)
                    .apply {
                        onDismissListener = {
                            appointmentsAdapter?.notifyDataSetChanged()
                            SubjectsDao.add(schoolId, subject, OnCompleteListener { })
                        }
                    }
                    .show(supportFragmentManager, "add_lecture_dialog")
        }
    }

    override fun updateUI(currentUser: User) {
        // Show subject details
        supportActionBar?.title = subject.name
        className.text = subject.classId
        UsersDao.getUserByEmail(schoolId, subject.teacherId, OnSuccessListener {
            teacherName.text = "Teacher: " + (it?.name ?: subject.teacherId)
        })

        // Show lecture times
        appointmentsAdapter = AppointmentsAdapter(this, subject.appointments)

        appointmentsList.adapter = appointmentsAdapter

        // Show course material
    }


    private inner class AppointmentsAdapter(context: Context, lectures: List<Lecture>)
        : ModelViewAdapter<Lecture>(context, lectures, LectureView::class)

}