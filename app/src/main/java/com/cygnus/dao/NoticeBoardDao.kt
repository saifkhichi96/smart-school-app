package com.cygnus.dao

import com.cygnus.CygnusApp
import com.cygnus.model.NoticeBoardPost
import com.cygnus.model.Subject
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener

/**
 * A data access class to read posts on notice boards.
 *
 * Purpose of this class is to provide methods for communicating with the
 * Firebase backend to access data related to notice boards.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
object NoticeBoardDao {

    /**
     * Adds a new post to a class notice board.
     *
     * @param schoolId The id of the school.
     * @param classId The id of the class.
     * @param post The post to add.
     * @param listener A listener for receiving response of the request.
     */
    fun add(schoolId: String, classId: String, post: NoticeBoardPost, listener: OnCompleteListener<Void?>) {
        CygnusApp.refToClassNoticeBoard(schoolId, classId)
                .push()
                .setValue(post)
                .addOnCompleteListener(listener)
    }

    /**
     * Retrieves a list of subjects taught by a teacher.
     *
     * @param schoolId The id of the school.
     * @param teacherId The email address of the teacher.
     * @param listener A listener for receiving response of the request.
     */
    fun getSubjectsByTeacher(schoolId: String, teacherId: String, listener: OnSuccessListener<List<Subject>>) {
        ClassesDao.getClassesAtSchool(schoolId, OnSuccessListener {
            val subjects = ArrayList<Subject>()
            it?.forEach { schoolClass ->
                schoolClass.subjects?.values?.forEach { subject ->
                    if (subject.teacherId == teacherId) {
                        subjects += subject
                    }
                }
            }
            listener.onSuccess(subjects)
        })
    }

    /**
     * Retrieves a list of subjects taught in a school class.
     *
     * @param schoolId The id of the school.
     * @param classId The id of the school class
     * @param listener A listener for receiving response of the request.
     */
    fun getPostsByClass(schoolId: String, classId: String, listener: OnSuccessListener<ArrayList<NoticeBoardPost>>) {
        CygnusApp.refToClassNoticeBoard(schoolId, classId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val posts = ArrayList<NoticeBoardPost>()

                        val t = object : GenericTypeIndicator<HashMap<String, NoticeBoardPost>>() {}
                        snapshot.getValue(t)?.values?.toList()?.forEach { posts.add(it) }
                        listener.onSuccess(posts)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        listener.onSuccess(ArrayList())
                    }
                })
    }

}