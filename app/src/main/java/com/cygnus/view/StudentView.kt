package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.model.Student

class StudentView : BaseView<Student> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var studentNameView: TextView
    private var studentRollNoView: TextView

    init {
        LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_2, this)
        studentNameView = findViewById(android.R.id.text1)
        studentRollNoView = findViewById(android.R.id.text2)
    }

    override fun updateView(model: Student) {
        studentNameView.text = model.name
        studentRollNoView.text = "Roll # ${model.rollNo}"
    }

}