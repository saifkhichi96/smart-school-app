package com.cygnus

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.OpenableColumns
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import androidx.core.content.FileProvider.getUriForFile
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_subjects.*
import java.io.File


/**
 * SubjectActivity shows details of a [Subject].
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SubjectActivity : DashboardChildActivity() {

    private inner class CourseFile(val name: String, val metadata: StorageMetadata) {
        override fun toString(): String {
            return name
        }
    }

    private lateinit var localMaterialPath: String
    private lateinit var materialRef: StorageReference
    private lateinit var materialAdapter: ArrayAdapter<CourseFile>
    private val material = ArrayList<CourseFile>()

    private lateinit var subject: Subject
    private var editable: Boolean = false

    private var appointmentsAdapter: AppointmentsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subjects)

        subject = intent.getSerializableExtra(CygnusApp.EXTRA_SCHOOL_SUBJECT) as Subject? ?: return finish()
        localMaterialPath = "$schoolId/${subject.classId}/${subject.name}/"
        materialRef = Firebase.storage.getReference(localMaterialPath)
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
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.type = "application/*"
            i.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(i, RESULT_ACTION_PICK)
        }

        materialAdapter = object : ArrayAdapter<CourseFile>(
                this,
                android.R.layout.simple_list_item_1,
                material
        ) {

            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val v = super.getView(position, convertView, parent)
                v.setOnClickListener {
                    // Download and open file
                    val item = material[position]
                    val root = File(getExternalFilesDir(null), localMaterialPath)
                    if (!root.exists()) root.mkdirs()

                    val file = File(root, item.name)
                    if (file.exists()) openFile(file) else {
                        file.createNewFile()
                        materialRef.child(item.name).getFile(file)
                                .addOnSuccessListener {
                                    openFile(file)
                                }.addOnFailureListener {
                                    file.delete()
                                }
                    }
                }

                v.setOnLongClickListener {
                    // TODO: Delete file in editable mode
                    false
                }
                return v
            }

        }
    }

    fun openFile(file: File) {
        try {
            val extension = file.path.split(".").last()
            val mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            val uri = getUriForFile(this, "com.cygnus.fileprovider", file)

            val i = Intent(Intent.ACTION_VIEW)
            i.setDataAndType(uri, mime)
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(i)
        } catch (ex: Exception) {
            Snackbar.make(contentList, ex.message ?: "Could not open file.", Snackbar.LENGTH_INDEFINITE).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RESULT_ACTION_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val filename = uri.getLastPathSegment(this)
                if (filename != null) {
                    val status = Snackbar.make(contentList, "Uploading...", Snackbar.LENGTH_INDEFINITE)
                    status.show()
                    materialRef.child(filename)
                            .putFile(uri)
                            .addOnSuccessListener {
                                it.metadata?.let { metadata ->
                                    material.add(CourseFile(filename, metadata))
                                    materialAdapter.notifyDataSetChanged()
                                    materialAdapter.sort { o1, o2 ->
                                        o1.metadata.creationTimeMillis.compareTo(o2.metadata.creationTimeMillis)
                                    }
                                }

                                status.setText("File uploaded.")
                                Handler().postDelayed({ status.dismiss() }, 2500L)
                            }
                            .addOnFailureListener {
                                status.setText("Failed to upload file.")
                                Handler().postDelayed({ status.dismiss() }, 2500L)
                            }
                }
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
        contentList.adapter = materialAdapter

        // Show course material
        materialRef.listAll()
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        material.clear()
                        it.result?.items?.forEach { reference ->
                            reference.metadata.addOnSuccessListener { metadata ->
                                material.add(CourseFile(reference.name, metadata))
                                materialAdapter.sort { o1, o2 ->
                                    o1.metadata.creationTimeMillis.compareTo(o2.metadata.creationTimeMillis)
                                }
                            }
                        }
                        materialAdapter.notifyDataSetChanged()
                    }
                }
    }


    private inner class AppointmentsAdapter(context: Context, lectures: List<Lecture>)
        : ModelViewAdapter<Lecture>(context, lectures, LectureView::class)

    companion object {
        private const val RESULT_ACTION_PICK = 100
    }

    fun Uri.getLastPathSegment(context: Context): String? {
        var name: String? = null
        when {
            ContentResolver.SCHEME_FILE == this.scheme -> name = this.lastPathSegment
            ContentResolver.SCHEME_CONTENT == this.scheme -> {
                val returnCursor: Cursor? = context.contentResolver.query(this, null, null, null, null)
                if (returnCursor != null && returnCursor.moveToFirst()) {
                    val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    name = returnCursor.getString(nameIndex)
                    returnCursor.close()
                }
            }
        }
        return name
    }

}