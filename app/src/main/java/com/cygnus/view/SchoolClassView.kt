package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.model.SchoolClass

class SchoolClassView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseView<SchoolClass>(context, attrs, defStyleAttr) {

    private var classNameView: TextView
    private var classTeacherView: TextView

    init {
        LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, this)
        classNameView = findViewById(android.R.id.text1)
        classTeacherView = findViewById(android.R.id.text2)
    }

    override fun updateView(model: SchoolClass) {
        classNameView.text = model.name
        classTeacherView.text = if (model.teacherId.isBlank()) "No Teacher Assigned" else "Teacher: " + model.teacherId
    }

}