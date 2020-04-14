package com.cygnus.dao

import android.os.Parcelable
import com.cygnus.CygnusApp
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import kotlinx.android.parcel.Parcelize

object InvitesDao {

    fun checkInviteStatus(schoolId: String, invitee: String, callback: OnSuccessListener<Invite?>) {
        getInvites(
                schoolId,
                OnSuccessListener { invites ->
                    val thisInvite = Invite("", invitee, "")
                    if (invites != null && invites.contains(thisInvite)) {
                        callback.onSuccess(invites.find { it.invitee == invitee })
                    } else {
                        callback.onSuccess(null)
                    }
                }
        )
    }

    fun getInvites(schoolId: String, callback: OnSuccessListener<List<Invite>?>) {
        CygnusApp.refToInvites(schoolId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        callback.onSuccess(null)
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        val t = object : GenericTypeIndicator<HashMap<String, String>>() {}
                        snapshot.getValue(t)?.let { map ->
                            val invites = ArrayList<Invite>()
                            map.forEach { entry ->
                                invites.add(Invite(
                                        entry.key,
                                        entry.value.split(":")[0],
                                        entry.value.split(":")[1]
                                ))
                            }
                            callback.onSuccess(invites)
                        }
                    }

                })
    }

}

@Parcelize
data class Invite(val id: String, val invitee: String, val status: String) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Invite

        if (invitee != other.invitee) return false

        return true
    }

    override fun hashCode(): Int {
        return invitee.hashCode()
    }
}