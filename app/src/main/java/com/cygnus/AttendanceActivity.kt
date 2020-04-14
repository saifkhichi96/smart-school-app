package com.cygnus

import android.os.Bundle
import com.cygnus.core.DashboardChildActivity
import com.cygnus.model.User

class AttendanceActivity : DashboardChildActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
    }

    override fun updateUI(currentUser: User) {

    }

}