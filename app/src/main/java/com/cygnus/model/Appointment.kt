package com.cygnus.model

import co.aspirasoft.model.BaseModel
import java.sql.Timestamp

/**
 * Appointment is a model class which represents a weekly appointment.
 *
 * An appointment is a weekly meeting held on a particular day, at a defined time
 * and a location.
 *
 * @constructor Creates a new appointment
 * @property dayOfWeek day of week when appointment is scheduled
 * @property startTime time of day when appointment start
 * @property endTime time of day when appointment ends
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class Appointment(var dayOfWeek: Int, var startTime: Timestamp, var endTime: Timestamp, var location: String) : BaseModel() {

    // no-arg constructor required for Firebase
    constructor() : this(1, Timestamp(System.currentTimeMillis()), Timestamp(System.currentTimeMillis()), "")

    init {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw IllegalStateException("Day of week must be in range 1-7 inclusive")
        }
    }

}
