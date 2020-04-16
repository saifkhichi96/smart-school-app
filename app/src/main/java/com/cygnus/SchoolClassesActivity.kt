package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.ClassesDao
import com.cygnus.dao.Invite
import com.cygnus.model.SchoolClass
import com.cygnus.model.User
import com.cygnus.view.AddClassDialog
import com.cygnus.view.SchoolClassView
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_list.*

class SchoolClassesActivity : DashboardChildActivity() {

    private val teachers: ArrayList<String> = ArrayList()
    private val classes: ArrayList<SchoolClass> = ArrayList()

    private lateinit var adapter: SchoolClassAdapter

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

        adapter = SchoolClassAdapter(this, classes)
        contentList.adapter = adapter

        addButton.setOnClickListener { onAddClassClicked() }
    }

    override fun updateUI(currentUser: User) {
        ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener {
            it?.let {
                classes.clear()
                classes.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun onAddClassClicked() {
        AddClassDialog.newInstance(teachers, currentUser.id).show(
                supportFragmentManager,
                "add_class_dialog"
        )
    }

    private inner class SchoolClassAdapter(context: Context, val classes: List<SchoolClass>)
        : ModelViewAdapter<SchoolClass>(context, classes, SchoolClassView::class) {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val v = super.getView(position, convertView, parent)
            v.setOnClickListener {
                AddClassDialog.newInstance(teachers, currentUser.id, classes[position])
                        .show(supportFragmentManager, "add_class_dialog")
            }
            return v
        }

    }

}