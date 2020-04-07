package com.cygnus.tasks

import co.aspirasoft.tasks.DummyTask
import co.aspirasoft.tasks.FirebaseTask
import com.cygnus.utils.DynamicLinksUtils
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query

/**
 * InvitationTask is a [FirebaseTask] for sending invites.
 *
 * Purpose of this task is to asynchronously send a sign up invite
 * to [inviteeEmail] by generating a magic link.
 *
 * @param inviteeType Type of the invited user (teacher, student, etc).
 * @param referral Unique referral code for generating invitation.
 */
class InvitationTask(
        private val inviteeEmail: String,
        private val inviteeType: String,
        private val referral: String
) : FirebaseTask() {

    /**
     * Location of invitations in Firebase database.
     */
    private val refPath = "${referral}/invites/"

    /**
     * State of the new invitation.
     *
     * All new invitations are in `Pending` state.
     */
    private val state = "Pending"

    /**
     * Checks if the invitee has previously been invited.
     *
     * Return `true` if an invitation for this invitee already exists.
     *
     * @param snapshot Snapshot of current database state.
     */
    private fun checkAlreadyInvited(snapshot: DataSnapshot): Boolean {
        var exists = false
        for (child in snapshot.children) {
            if (child.value.toString().startsWith(inviteeEmail, true)) {
                exists = true
                break
            }
        }
        return exists
    }

    /**
     * Requests list of existing users.
     */
    override fun init(): Query {
        return FirebaseDatabase.getInstance()
                .getReference(refPath)
                .orderByValue()
    }

    /**
     * Generates and sends an invitation.
     */
    override fun onQuerySuccess(): Task<Void?> {
        return FirebaseAuth.getInstance()
                .sendSignInLinkToEmail(inviteeEmail, DynamicLinksUtils.createSignUpAction(inviteeType, referral))
                .addOnSuccessListener {
                    FirebaseDatabase.getInstance()
                            .getReference(refPath)
                            .push()
                            .setValue("$inviteeEmail:$state")
                }
    }

    /**
     * Callback for when invitee already exists in database.
     */
    override fun onQueryFailure(): Task<Void?> {
        return DummyTask(Exception("already exists"))
    }

    /**
     * Checks the precondition for sending the invite.
     *
     * A new invitation is only sent if the invitee is not already a member
     * and has not previously been invited.
     *
     * Return `true` if conditions satisfied or no initial query.
     */
    override fun checkCriteria(snapshot: DataSnapshot): Boolean {
        return checkAlreadyInvited(snapshot)
    }

}
