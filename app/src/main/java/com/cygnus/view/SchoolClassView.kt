package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.model.SchoolClass

class SchoolClassView : BaseView<SchoolClass> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

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