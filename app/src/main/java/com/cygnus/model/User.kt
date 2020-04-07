package com.cygnus.model

import co.aspirasoft.model.BaseModel

class User(var id: String, var name: String, var type: String, var credentials: Credentials) : BaseModel() {

    // no-arg constructor required for Firebase
    constructor() : this("", "", "", Credentials())

    val email: String
        get() = credentials.email

}