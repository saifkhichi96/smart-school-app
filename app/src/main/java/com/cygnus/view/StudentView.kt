package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.bumptech.glide.Glide
import com.cygnus.R
import com.cygnus.model.Student
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class StudentView : BaseView<Student> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private var studentImageView: ImageView
    private var studentNameView: TextView
    private var studentRollNoView: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_student, this)
        studentImageView = findViewById(R.id.userImage)
        studentNameView = findViewById(R.id.nameField)
        studentRollNoView = findViewById(R.id.rollNo)
    }

    override fun updateView(model: Student) {
        val photoRef = Firebase.storage.getReference("users/${model.id}/photo.png")
        photoRef.downloadUrl.addOnSuccessListener {
            if (it != null) Glide.with(this)
                    .load(photoRef)
                    .into(studentImageView)
        }

        studentNameView.text = model.name
        studentRollNoView.text = "Roll # ${model.rollNo}"
    }

}