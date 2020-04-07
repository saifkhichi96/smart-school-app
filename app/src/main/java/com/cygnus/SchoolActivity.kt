package com.cygnus

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import co.aspirasoft.util.InputUtils.isNotBlank
import co.aspirasoft.util.InputUtils.showError
import com.cygnus.model.User
import com.cygnus.tasks.InvitationTask
import com.cygnus.view.EmailsInputDialog
import com.cygnus.view.LogoutConfirmationDialog
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school)
        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        // set up click handlers
        multipleInviteButton.setOnClickListener { onMultipleInvitesClicked() }
        singleInviteButton.setOnClickListener { onSingleInviteClicked() }
    }

    override fun onStart() {
        super.onStart()
        trackSentInvites() // start the live counters
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
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
        if (currentUser.type == "School") {
            schoolName.text = currentUser.name
            schoolEmail.text = currentUser.email
            schoolCode.setImageBitmap(QRGEncoder(currentUser.id, null, QRGContents.Type.TEXT, 512).bitmap)
        } else finish()
    }

    /**
     * Listens for changes in status of sent invites.
     *
     * Monitors all sent invites and displays the number of accepted
     * and pending invites in realtime.
     */
    private fun trackSentInvites() {
        FirebaseDatabase.getInstance()
                .getReference("${currentUser.id}/invites/")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var pending = 0
                        var accepted = 0
                        val t = object : GenericTypeIndicator<HashMap<String, String>>() {}
                        snapshot.getValue(t)?.let { invites ->
                            for (invite in invites.values) {
                                if (invite.endsWith("Pending")) {
                                    pending += 1
                                } else if (invite.endsWith("Accepted")) {
                                    accepted += 1
                                }
                            }
                        }

                        pendingInvites.text = "$pending"
                        acceptedInvites.text = "$accepted"
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

    /**
     * Handles clicks on single invite button.
     *
     * Reads an email address from [emailField] and tries generating an invite
     * for this address. A blocking progress box is displayed while the invite
     * is being sent.
     */
    private fun onSingleInviteClicked() {
        if (emailField.isNotBlank()) {
            val email = emailField.text.toString().trim()
            val progressDialog = ProgressDialog.show(
                    this,
                    "Sending Invitation",
                    "A magic link is being emailed to $email...",
                    true
            )

            inviteSingleEmail(email, OnCompleteListener {
                progressDialog.dismiss()
                if (!it.isSuccessful) {
                    emailField.showError(it.exception?.message ?: "Failed to invite.")
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
    private fun onMultipleInvitesClicked() {
        EmailsInputDialog(this)
                .setOnEmailsReceivedListener { emails ->
                    inviteMultipleEmails(emails)
                }
                .show()
    }

    /**
     * Sends a link to [email] which can be used to create a Teacher account.
     *
     * @param listener Optional callback to listen for completion of invitation task.
     */
    private fun inviteSingleEmail(email: String, listener: OnCompleteListener<Void?>? = null) {
        InvitationTask(email, "Teacher", currentUser.id)
                .start { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@SchoolActivity, "Email sent.", Toast.LENGTH_LONG).show()
                        emailField.setText("")
                    } else {
                        emailField.showError(task.exception?.message ?: "Email could not be sent.")
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
                "Sending $invited/$invitees Invitation...",
                "Emailed $invited/$invitees magic links...",
                true
        )

        var status = ""
        for (email in emails) {
            inviteSingleEmail(email, OnCompleteListener {
                progressDialog.setTitle("Sending $invited/$invitees Invitation...")

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
                    progressDialog.setTitle("Invitations Sent")
                    progressDialog.setCancelable(true)
                    Handler().postDelayed({ progressDialog.dismiss() }, 5000L)
                }
            })
        }
    }

}
