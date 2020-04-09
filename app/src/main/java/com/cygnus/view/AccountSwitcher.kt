package com.cygnus.view

import android.app.Activity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import com.cygnus.R
import com.cygnus.model.User
import kotlinx.android.synthetic.main.dialog_account_switcher.*

/**
 * AccountSwitcher allows switching between user accounts.
 *
 * This dialog box shows information of currently signed in user, and
 * allows users to sign out, along with some other high level controls.
 * It follows Google's material account switcher design.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class AccountSwitcher private constructor(private val activity: Activity) : AlertDialog(activity) {

    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_account_switcher)
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Position the dialog box just under the action bar
        window?.attributes = window?.attributes?.apply {
            gravity = Gravity.TOP

            val a = TypedValue()
            y += if (context.theme.resolveAttribute(android.R.attr.actionBarSize, a, true)) {
                TypedValue.complexToDimensionPixelSize(a.data, context.resources.displayMetrics)
            } else {
                172
            }
        }

        // Bind the user to view object (this ensures all updates
        // to user object are correctly reflected in the view)
        userSummaryView.bindWithModel(user)

        // Show sign out confirmation when sign out button is clicked
        signOutButton.setOnClickListener {
            LogoutConfirmationDialog.show(activity)
            dismiss()
        }

        // TODO: Handle clicks on `Privacy` and `ToS` buttons.
        privacyButton.setOnClickListener {

        }

        tosButton.setOnClickListener {

        }
    }

    /**
     * Builder to create an [AccountSwitcher] dialog box.
     *
     * @constructor Creates a new AccountSwitcher Builder.
     * @param context The activity where this will be used.
     *
     * @property dialog The AccountSwitcher instance being built.
     */
    class Builder(context: Activity) {

        private val dialog = AccountSwitcher(context)

        /**
         * Sets the [user] to display in the dialog box.
         *
         * This will normally be the currently signed in user.
         */
        fun setUser(user: User): Builder {
            dialog.user = user
            return this
        }

        /**
         * Returns a new instance of [AccountSwitcher] with defined properties.
         */
        fun create(): AccountSwitcher {
            return dialog
        }

        /**
         * Shows the [AccountSwitcher] dialog box.
         */
        fun show() {
            dialog.show()
        }

    }

}