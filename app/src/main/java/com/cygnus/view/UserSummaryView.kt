package com.cygnus.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import co.aspirasoft.view.BaseView
import com.cygnus.R
import com.cygnus.model.School
import com.cygnus.model.Student
import com.cygnus.model.Teacher
import com.cygnus.model.User

/**
 * UserSummaryView is custom view for displaying a summary of user account.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class UserSummaryView : BaseView<User> {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private val userImage: ImageView
    private val userNameLabel: TextView
    private val userSchoolLabel: TextView
    private val addressLabel: TextView
    private val phoneLabel: TextView
    private val birthdayLabel: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_user_summary, this)
        userImage = findViewById(R.id.userImage)
        userNameLabel = findViewById(R.id.userNameLabel)
        userSchoolLabel = findViewById(R.id.userEmailLabel)
        addressLabel = findViewById(R.id.addressLabel)
        phoneLabel = findViewById(R.id.phoneLabel)
        birthdayLabel = findViewById(R.id.birthdayLabel)

        findViewById<Button>(R.id.profileButton).setOnClickListener {
            // TODO: Open profile activity
        }
    }

    /**
     * Displays user details.
     */
    override fun updateView(model: User) {
        userNameLabel.text = model.name
        userSchoolLabel.text = model.credentials.email
        addressLabel.text = model.address ?: "Not Set"
        phoneLabel.text = model.phone ?: "Not Set"

        when (model) {
            is Student -> {
                birthdayLabel.visibility = View.VISIBLE
                birthdayLabel.text = model.dateOfBirth ?: "Not Set"
                userImage.setImageResource(R.drawable.ph_student)
            }
            is Teacher -> {
                userImage.setImageResource(R.drawable.ph_teacher)
            }

            // Not customisations for `School` users needed
            // as this view will not be used for them
            is School -> return
        }
    }

}