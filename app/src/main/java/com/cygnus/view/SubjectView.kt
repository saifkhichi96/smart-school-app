package com.cygnus.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.Subject
import kotlinx.android.synthetic.main.view_subject.view.*

class SubjectView : BaseView<Subject> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val subjectColor: View
    private val subjectName: TextView
    private val subjectTeacher: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_subject, this)
        subjectColor = findViewById(R.id.subjectColor)
        subjectName = findViewById(R.id.subjectName)
        subjectTeacher = findViewById(R.id.subjectTeacher)
    }

    override fun updateView(model: Subject) {
        subjectName.text = model.name
        subjectClass.text = model.classId
        subjectTeacher.text = "Teacher: " + model.teacherId
        try {
            subjectColor.setBackgroundColor(Color.parseColor(convertToColor(model)))
        } catch (ignored: Exception) {

        }
    }

    fun showTeacher() {
        subjectTeacher.visibility = View.VISIBLE
    }

    private fun convertToColor(o: Any): String? {
        val i = o.hashCode()
        return "#FF" + Integer.toHexString(i shr 16 and 0xFF) +
                Integer.toHexString(i shr 8 and 0xFF) +
                Integer.toHexString(i and 0xFF)
    }

}