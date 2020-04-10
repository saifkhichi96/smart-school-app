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

class SubjectView(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : BaseView<Subject>(context, attrs, defStyleAttr) {

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
        subjectTeacher.text = model.classId
        try {
            subjectColor.setBackgroundColor(Color.parseColor(convertToColor(model.name)))
        } catch (ignored: Exception) {

        }
    }

    private fun convertToColor(s: String): String? {
        val i = s.hashCode()
        return "#FF" + Integer.toHexString(i shr 16 and 0xFF) +
                Integer.toHexString(i shr 8 and 0xFF) +
                Integer.toHexString(i and 0xFF)
    }

}