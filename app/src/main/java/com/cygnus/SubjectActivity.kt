package com.cygnus

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import co.aspirasoft.adapter.ModelViewAdapter
import co.aspirasoft.util.PermissionUtils
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.SubjectsDao
import com.cygnus.dao.UsersDao
import com.cygnus.model.CourseFile
import com.cygnus.model.Lecture
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.storage.FileManager
import com.cygnus.storage.FileUtils.getLastPathSegmentOnly
import com.cygnus.storage.MaterialAdapter
import com.cygnus.view.AddLectureDialog
import com.cygnus.view.LectureView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
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

    private var materialAdapter: MaterialAdapter? = null
    private val material = ArrayList<CourseFile>()

    private lateinit var mFileManager: FileManager

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

        addMaterialButton.setOnClickListener {
            if (PermissionUtils.requestPermissionIfNeeded(
                            this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            getString(R.string.permission_storage),
                            RC_WRITE_PERMISSION
                    )) {
                pickFile()
            }
        }

        mFileManager = FileManager.newInstance(this, "$schoolId/${subject.classId}/${subject.name}/")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_ACTION_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.getLastPathSegmentOnly(this)?.let { filename ->
                val status = Snackbar.make(contentList, "Uploading...", Snackbar.LENGTH_INDEFINITE)
                status.show()
                mFileManager.upload(filename, data.data!!)
                        .addOnSuccessListener {
                            it.metadata?.let { metadata ->
                                material.add(CourseFile(filename, metadata))
                                materialAdapter?.notifyDataSetChanged()
                            }

                            status.setText("File uploaded.")
                            Handler().postDelayed({ status.dismiss() }, 2500L)
                        }
                        .addOnFailureListener {
                            status.setText(it.message ?: "Failed to upload file.")
                            Handler().postDelayed({ status.dismiss() }, 2500L)
                        }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_WRITE_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickFile()
            }
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
        showCourseMaterial()
    }

    private fun showCourseMaterial() {
        if (materialAdapter == null) {
            materialAdapter = MaterialAdapter(this, material, mFileManager)
            contentList.adapter = materialAdapter
        }

        mFileManager.listAll().addOnSuccessListener { result ->
            material.clear()
            result?.items?.forEach { reference ->
                reference.metadata.addOnSuccessListener { metadata ->
                    material.add(CourseFile(reference.name, metadata))
                    materialAdapter?.notifyDataSetChanged()
                }
            }
        }
    }

    private fun pickFile() {
        val i = Intent(Intent.ACTION_GET_CONTENT)
        i.type = "application/*"
        i.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(i, RESULT_ACTION_PICK)
    }

    private inner class AppointmentsAdapter(context: Context, lectures: List<Lecture>)
        : ModelViewAdapter<Lecture>(context, lectures, LectureView::class)

    companion object {
        private const val RESULT_ACTION_PICK = 100
        private const val RC_WRITE_PERMISSION = 200
    }

}