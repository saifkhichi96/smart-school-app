package com.cygnus

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

/**
 * An [Application] subclass represents this application.
 *
 * Purpose of this class is to define default behaviours, perform
 * SDK initializations and declare any shared data.
 */
class CygnusApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // Enabling persistence speeds up app by caching data locally
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }

    companion object {
        // the EXTRA_* strings are used as tags to pass
        // data between activities using Intents
        const val EXTRA_USER = "user"
        const val EXTRA_ACCOUNT_TYPE = "account_type"
        const val EXTRA_REFERRAL_CODE = "referral_code"
        const val EXTRA_INVITE_STATUS = "invite_status"
        const val EXTRA_INVITES = "invites"

        // the PARAM_* strings are used to define parameters
        // used in dynamic links
        const val PARAM_LINK_TARGET = "continueUrl"
        const val PARAM_REFERRAL_CODE = "referral"
        const val PARAM_ACCOUNT_TYPE = "type"

        // the refTo* functions return a reference to resources
        // in the Firebase database
        private val db get() = FirebaseDatabase.getInstance()
        fun refToInvites(schoolId: String) = db.getReference("${schoolId}/invites/")
    }

}