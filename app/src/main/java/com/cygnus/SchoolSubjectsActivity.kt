package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.Invite
import com.cygnus.model.Subject
import com.cygnus.model.User
import com.cygnus.view.AddSubjectDialog
import com.cygnus.view.SubjectView
import com.google.firebase.database.*
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
        FirebaseDatabase.getInstance()
                .getReference("${currentUser.id}/classes/")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, Subject>>() {}
                        classes.clear()
                        snapshot.getValue(t)?.values?.forEach {
                            classes.add(it.name)
                            FirebaseDatabase.getInstance()
                                    .getReference("${currentUser.id}/classes/${it.name}/subjects/")
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            val t2 = object : GenericTypeIndicator<HashMap<String, Subject>>() {}
                                            snapshot.getValue(t2)?.values?.forEach { subject ->
                                                if (!subjects.contains(subject))
                                                    subjects.add(subject)
                                            }
                                            adapter.notifyDataSetChanged()
                                        }

                                        override fun onCancelled(error: DatabaseError) {

                                        }
                                    })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
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