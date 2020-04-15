package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.model.Student

class StudentView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseView<Student>(context, attrs, defStyleAttr) {

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