package com.udacity.maluleque.meutako.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.firestore.IgnoreExtraProperties
import com.udacity.maluleque.meutako.utils.DateUtils.getDataDayMonth

@IgnoreExtraProperties
class Transaction : Parcelable {
    var uid: String? = null
    var type: String? = null
    var amount = 0.0
    var category: String? = null
    var description: String? = null
    var date: Long = 0
    var image: String? = null

    constructor() {}

    protected constructor(`in`: Parcel) {
        uid = `in`.readString()
        type = `in`.readString()
        amount = `in`.readDouble()
        category = `in`.readString()
        description = `in`.readString()
        date = `in`.readLong()
        image = `in`.readString()
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(uid)
        dest.writeString(type)
        dest.writeDouble(amount)
        dest.writeString(category)
        dest.writeString(description)
        dest.writeLong(date)
        dest.writeString(image)
    }

    override fun describeContents(): Int {
        return 0
    }

    val formattedDate: String
        get() = getDataDayMonth(date)

    companion object {
        val CREATOR: Parcelable.Creator<Transaction?> = object : Parcelable.Creator<Transaction?> {
            override fun createFromParcel(`in`: Parcel): Transaction? {
                return Transaction(`in`)
            }

            override fun newArray(size: Int): Array<Transaction?> {
                return arrayOfNulls(size)
            }
        }
    }
}