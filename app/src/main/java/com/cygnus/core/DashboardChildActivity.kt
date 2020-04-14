package com.cygnus.core

import androidx.appcompat.widget.Toolbar
import com.cygnus.R

abstract class DashboardChildActivity : SecureActivity() {

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        findViewById<Toolbar?>(R.id.toolbar)?.let { setSupportActionBar(it) }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}