package com.cygnus.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.Teacher
import com.google.android.material.card.MaterialCardView

class TeacherView : BaseView<Teacher> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val defaultElevation: Float

    private val teacherCard: MaterialCardView
    private val teacherName: TextView
    private val teacherEmail: TextView
    private val teacherAddress: Button
    private val teacherPhone: Button
    private val revokeInviteButton: Button

    init {
        LayoutInflater.from(context).inflate(R.layout.view_teacher, this)
        teacherCard = findViewById(R.id.teacherCard)
        teacherName = findViewById(R.id.teacherName)
        teacherEmail = findViewById(R.id.teacherEmail)
        teacherAddress = findViewById(R.id.teacherAddress)
        teacherPhone = findViewById(R.id.teacherPhone)
        revokeInviteButton = findViewById(R.id.revokeInviteButton)

        defaultElevation = teacherCard.cardElevation
    }

    /**
     * Displays teacher details.
     */
    override fun updateView(model: Teacher) {
        // if the Teacher has not completed sign up yet, show pending view
        if (model.name.isBlank()) {
            teacherCard.apply {
                setCardBackgroundColor(Color.TRANSPARENT)
                cardElevation = 0F
            }

            teacherName.text = model.email
            teacherEmail.visibility = View.GONE
            teacherAddress.visibility = View.GONE
            teacherPhone.visibility = View.GONE
            revokeInviteButton.visibility = View.VISIBLE
        }

        // if the Teacher details are completed, show accepted view
        else {
            teacherCard.apply {
                setCardBackgroundColor(Color.WHITE)
                cardElevation = defaultElevation
            }
            teacherEmail.visibility = View.VISIBLE
            teacherAddress.visibility = View.VISIBLE
            teacherPhone.visibility = View.VISIBLE
            revokeInviteButton.visibility = View.GONE

            teacherName.text = model.name
            teacherEmail.text = model.email
            teacherAddress.text = model.address
            teacherPhone.text = model.phone
        }
    }

}