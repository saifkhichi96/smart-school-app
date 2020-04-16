package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.ClassesDao
import com.cygnus.dao.Invite
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.view.AddSubjectDialog
import com.cygnus.view.SubjectView
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_list.*

class SchoolSubjectsActivity : DashboardChildActivity() {

    private val classes: ArrayList<String> = ArrayList()
    private val teachers: ArrayList<String> = ArrayList()
    private val subjects: ArrayList<Subject> = ArrayList()

    private lateinit var adapter: SubjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Read staff list from intent
        val invites = intent.getParcelableArrayListExtra<Invite>(CygnusApp.EXTRA_INVITES)
        if (invites == null) {
            finish()
            return
        }
        invites.forEach { this.teachers.add(it.invitee) }

        adapter = SubjectAdapter(this, subjects)
        contentList.adapter = adapter

        addButton.setOnClickListener { onAddClassClicked() }
    }

    override fun updateUI(currentUser: User) {
        ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener {
            classes.clear()
            it?.forEach { schoolClass ->
                classes.add(schoolClass.name)
                schoolClass.subjects?.values?.forEach { subject ->
                    if (!subjects.contains(subject)) subjects.add(subject)
                }
            }
            adapter.notifyDataSetChanged()
        })
    }

    private fun onAddClassClicked() {
        if (classes.isNotEmpty()) {
            AddSubjectDialog.newInstance(classes, teachers, currentUser.id).show(
                    supportFragmentManager,
                    "add_class_dialog"
            )
        } else {
            Toast.makeText(this, "You must add your school classes first.", Toast.LENGTH_LONG).show()
        }
    }

    private inner class SubjectAdapter(context: Context, val subjects: List<Subject>)
        : ModelViewAdapter<Subject>(context, subjects, SubjectView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                AddSubjectDialog.newInstance(classes, teachers, currentUser.id, subjects[position])
                        .show(supportFragmentManager, "add_subject_dialog")
            }
            return v
        }

    }

}