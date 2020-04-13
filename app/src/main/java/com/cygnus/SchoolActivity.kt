package com.cygnus

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import com.cygnus.model.School
import com.cygnus.model.User
import com.cygnus.tasks.InvitationTask
import com.cygnus.view.EmailsInputDialog
import com.cygnus.view.LogoutConfirmationDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_school.*

/**
 * SchoolActivity is the homepage of `School` users.
 *
 * Purpose of this activity is to provide a UI to the schools
 * which allows them to perform certain administrative tasks.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SchoolActivity : SecureActivity() {

    private val invitedStaff = ArrayList<Invite>()
    private val joinedStaff = ArrayList<Invite>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""
    }

    override fun onStart() {
        super.onStart()
        trackSentInvites() // start the live counters
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.school_action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                LogoutConfirmationDialog.show(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Displays the signed in school's details.
     */
    override fun updateUI(currentUser: User) {
        if (currentUser is School) {
            schoolName.text = currentUser.name
            schoolEmail.text = currentUser.email
            schoolCode.setImageBitmap(QRGEncoder(currentUser.id, null, QRGContents.Type.TEXT, 512).bitmap)

            showStaffStats(0, 0)
        } else finish()
    }

    fun onManageClassesClicked(v: View) {
        startActivity(Intent(this, SchoolClassesActivity::class.java).apply {
            putExtra(CygnusApp.EXTRA_USER, currentUser)
            putExtra(CygnusApp.EXTRA_INVITES, joinedStaff)
        })
    }

    fun onManageSubjectsClicked(v: View) {
        // TODO: Open subject management activity
    }

    /**
     * Handles clicks on single invite button.
     *
     * Reads an email address from [emailField] and tries generating an invite
     * for this address. A blocking progress box is displayed while the invite
     * is being sent.
     */
    fun onInviteSingleClicked(v: View) {
        if (emailField.isNotBlank()) {
            val email = emailField.text.toString().trim()
            val progressDialog = ProgressDialog.show(
                    this,
                    getString(R.string.status_invitation_sending),
                    String.format(getString(R.string.status_invitation_progress), email),
                    true
            )

            inviteSingleEmail(email, OnCompleteListener {
                progressDialog.dismiss()
                if (!it.isSuccessful) {
                    emailField.showError(it.exception?.message ?: getString(R.string.error_invitation_failure))
                }
            })
        }
    }

    /**
     * Handles clicks on multiple invites button.
     *
     * Opens a [EmailsInputDialog] where the user can input multiple email
     * addresses in a form. Invites for all valid emails are generated on
     * successful input.
     *
     * A blocking progress box is displayed while the invites are being
     * sent. Status of each sent invite is displayed.
     */
    fun onInviteMultipleClicked(v: View) {
        EmailsInputDialog(this)
                .setOnEmailsReceivedListener { emails ->
                    inviteMultipleEmails(emails)
                }
                .show()
    }

    /**
     * Handles clicks on invited staff button.
     *
     * Opens the [TeachersActivity] with a list of staff members who have pending
     * invitations.
     */
    fun onInvitedStaffClicked(v: View) {
        if (invitedStaff.size > 0) {
            val i = Intent(this, TeachersActivity::class.java)
            i.putExtra(CygnusApp.EXTRA_USER, currentUser)
            i.putExtra(CygnusApp.EXTRA_INVITE_STATUS, getString(R.string.status_invite_pending))
            i.putExtra(CygnusApp.EXTRA_INVITES, invitedStaff)
            startActivity(i)
        } else {
            Snackbar.make(joinedStaffButton, "No staff members invited yet.", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles clicks on joined staff button.
     *
     * Opens the [TeachersActivity] with a list of staff members who have accepted
     * their invitations and joined the app.
     */
    fun onJoinedStaffClicked(v: View) {
        if (joinedStaff.size > 0) {
            val i = Intent(this, TeachersActivity::class.java)
            i.putExtra(CygnusApp.EXTRA_USER, currentUser)
            i.putExtra(CygnusApp.EXTRA_INVITE_STATUS, getString(R.string.status_invite_accepted))
            i.putExtra(CygnusApp.EXTRA_INVITES, joinedStaff)
            startActivity(i)
        } else {
            Snackbar.make(joinedStaffButton, "No staff members have joined yet.", Snackbar.LENGTH_SHORT).show()
        }
    }

    /**
     * Sends a link to [email] which can be used to create a Teacher account.
     *
     * @param listener Optional callback to listen for completion of invitation task.
     */
    private fun inviteSingleEmail(email: String, listener: OnCompleteListener<Void?>? = null) {
        InvitationTask(this, currentUser.id, email)
                .start { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SchoolActivity, getString(R.string.status_invitation_sent), Toast.LENGTH_LONG).show()
                        emailField.setText("")
                    } else {
                        emailField.showError("Email ${task.exception?.message ?: "could not be sent"}.")
                    }

                    listener?.let { task.addOnCompleteListener(it) }
                }
    }

    /**
     * Sends links to [emails] which can be used to create Teacher accounts.
     */
    private fun inviteMultipleEmails(emails: List<String>) {
        val invitees = emails.size
        var invited = 1

        val progressDialog = ProgressDialog.show(
                this,
                String.format(getString(R.string.status_invitations_sending), invited, invitees),
                String.format(getString(R.string.status_invitations_progress), invited, invitees),
                true
        )

        var status = ""
        for (email in emails) {
            inviteSingleEmail(email, OnCompleteListener {
                progressDialog.setTitle(String.format(getString(R.string.status_invitations_sending), invited, invitees))

                synchronized(status) {
                    status += if (it.isSuccessful) {
                        "$email invited.\n\n"
                    } else {
                        "$email ${it.exception?.message ?: "already exists"}.\n\n"
                    }
                    synchronized(progressDialog) {
                        progressDialog.setMessage(status)
                    }
                }

                invited += 1
                if (invited > invitees) {
                    progressDialog.setTitle(getString(R.string.status_invitations_sent))
                    progressDialog.setCancelable(true)
                    Handler().postDelayed({ progressDialog.dismiss() }, 5000L)
                }
            })
        }
    }

    /**
     * Displays number of invited and joined staff members.
     */
    private fun showStaffStats(invited: Int, joined: Int) {
        invitedStaffButton.text = String.format(getString(R.string.label_staff_invited), invited)
        joinedStaffButton.text = String.format(getString(R.string.label_staff_joined), joined)
    }

    /**
     * Listens for changes in status of sent invites.
     *
     * Monitors all sent invites and displays the number of accepted
     * and pending invites in realtime.
     */
    private fun trackSentInvites() {
        CygnusApp.refToInvites(currentUser.id)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, String>>() {}
                        snapshot.getValue(t)?.let { invites ->
                            val invitedStaff = ArrayList<Invite>()
                            val joinedStaff = ArrayList<Invite>()
                            invites.forEach {
                                val invite = Invite(
                                        it.key,
                                        it.value.split(":")[0],
                                        it.value.split(":")[1]
                                )

                                when (invite.status) {
                                    getString(R.string.status_invite_pending) -> invitedStaff.add(invite)
                                    getString(R.string.status_invite_accepted) -> joinedStaff.add(invite)
                                }
                            }

                            this@SchoolActivity.invitedStaff.apply {
                                clear()
                                addAll(invitedStaff)
                            }

                            this@SchoolActivity.joinedStaff.apply {
                                clear()
                                addAll(joinedStaff)
                            }
                        }

                        showStaffStats(invitedStaff.size, joinedStaff.size)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back button twice to exit.", Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    @Parcelize
    data class Invite(val id: String, val invitee: String, val status: String) : Parcelable {
        override fun equals(other: Any?): Boolean {
            return if (other is Invite?) other?.id == this.id else super.equals(other)
        }

        override fun hashCode(): Int {
            return id.hashCode()
        }
    }

}