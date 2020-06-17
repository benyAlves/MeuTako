package com.udacity.maluleque.meutako.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
class User {
    lateinit var uid: String
    var name: String? = null
    var phone: String? = null

    constructor() {}

    constructor(uid: String, name: String?, phone: String?) {
        this.uid = uid
        this.name = name
        this.phone = phone
    }

}