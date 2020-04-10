package com.cygnus.model

import co.aspirasoft.model.BaseModel
import java.sql.Timestamp

/**
 * Subject is a course of study.
 *
 * Each subject is taught to a [SchoolClass] by a particular [Teacher].
 *
 * @constructor Creates a new subject.
 * @param name Name of the course.
 * @param teacherId The instructor's id who teaches the course.
 * @param classId The [SchoolClass]'s id to which this is taught.
 *
 * @property appointments List of appointments (i.e. daily class times).
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Subject(name: String, teacherId: String, classId: String) : BaseModel() {

    // no-arg constructor required for Firebase
    constructor() : this("", "", "")

    var appointments = ArrayList<Appointment>()
        private set

    var name = name
        set(value) {
            field = value
            setChanged()
        }

    var teacherId = teacherId
        set(value) {
            field = value
            setChanged()
        }

    var classId = classId
        set(value) {
            field = value
            setChanged()
        }

    fun addAppointment(dayOfWeek: Int, startTime: Timestamp, endTime: Timestamp, location: String) {
        appointments.add(Appointment(dayOfWeek, startTime, endTime, location))
        setChanged()
    }

}