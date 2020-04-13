package com.cygnus

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.model.SchoolClass
import com.cygnus.model.User
import com.cygnus.view.AddClassDialog
import com.cygnus.view.SchoolClassView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_list.*

class SchoolClassesActivity : SecureActivity() {

    private val teachers: ArrayList<String> = ArrayList()
    private val classes: ArrayList<SchoolClass> = ArrayList()

    private lateinit var adapter: SchoolClassAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Read staff list from intent
        val invites = intent.getParcelableArrayListExtra<SchoolActivity.Invite>(CygnusApp.EXTRA_INVITES)
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
        FirebaseDatabase.getInstance()
                .getReference("${currentUser.id}/classes/")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, SchoolClass>>() {}
                        snapshot.getValue(t)?.values?.let {
                            classes.clear()
                            classes.addAll(it)
                            adapter.notifyDataSetChanged()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
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