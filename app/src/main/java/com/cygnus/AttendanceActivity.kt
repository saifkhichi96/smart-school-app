package com.cygnus

import android.content.Context
import android.os.Bundle
import co.aspirasoft.adapter.ModelViewAdapter
import com.cygnus.core.DashboardChildActivity
import com.cygnus.dao.AttendanceDao
import com.cygnus.model.AttendanceRecord
import com.cygnus.model.Student
import com.cygnus.model.User
import com.cygnus.view.AttendanceView
import com.google.android.gms.tasks.OnSuccessListener
import kotlinx.android.synthetic.main.activity_list.*

class AttendanceActivity : DashboardChildActivity() {

    private val attendanceRecords = ArrayList<AttendanceRecord>()

    private lateinit var currentStudent: Student
    private lateinit var adapter: AttendanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        // Only allow a student to access this page
        currentStudent = when (currentUser) {
            is Student -> currentUser as Student
            else -> {
                finish()
                return
            }
        }

        adapter = AttendanceAdapter(this, attendanceRecords)
        contentList.adapter = adapter
    }

    override fun updateUI(currentUser: User) {
        AttendanceDao.getByStudent(schoolId, currentStudent, OnSuccessListener { savedRecords ->
            attendanceRecords.clear()
            savedRecords?.let { attendanceRecords.addAll(it) }
            adapter.notifyDataSetChanged()
        })
    }

    private inner class AttendanceAdapter(context: Context, val records: List<AttendanceRecord>)
        : ModelViewAdapter<AttendanceRecord>(context, records, AttendanceView::class) {

        override fun notifyDataSetChanged() {
            records.sortedBy { it.date }
            super.notifyDataSetChanged()
        }

    }

}