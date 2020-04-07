package com.cygnus.utils

import com.cygnus.CygnusApp
import com.google.firebase.auth.ActionCodeSettings

object DynamicLinksUtils {

    private const val domain = "https://cygnus.page.link"
    private const val androidPackageName = "com.cygnus"
    private const val iOSBundleId = "com.cygnus"

    const val ACTION_REGISTRATION = "/finishSignUp"

    /**
     * Returns a valid [ActionCodeSettings] to register a new user with [accountType] and [referralCode].
     */
    fun createSignUpAction(accountType: String, referralCode: String): ActionCodeSettings {
        val url = "$domain$ACTION_REGISTRATION?" +
                "${CygnusApp.PARAM_ACCOUNT_TYPE}=$accountType&" +
                "${CygnusApp.PARAM_REFERRAL_CODE}=$referralCode"

        return ActionCodeSettings.newBuilder()
                .setUrl(url)
                .setHandleCodeInApp(true)
                .setIOSBundleId(iOSBundleId)
                .setAndroidPackageName(androidPackageName, true, "1")
                .build()
    }

}