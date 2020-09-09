package com.bernardo.maluleque.shibaba.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bernardo.maluleque.shibaba.utils.DateUtils.getDataDayMonth
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity
class Transaction : Parcelable {
    @PrimaryKey
    lateinit var uid: String
    var type: String? = null
    var amount = 0.0
    var category: String? = null
    var description: String? = null
    var date: Long = 0
    var image: String? = null

    constructor() {}

/*
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
*/

    override fun describeContents(): Int {
        return 0
    }

    val formattedDate: String
        get() = getDataDayMonth(date)

    constructor(parcel: Parcel) : this() {
        uid = parcel.readString()!!
        type = parcel.readString()
        amount = parcel.readDouble()
        category = parcel.readString()
        description = parcel.readString()
        date = parcel.readLong()
        image = parcel.readString()
    }

    /* companion object {
         val CREATOR: Parcelable.Creator<Transaction?> = object : Parcelable.Creator<Transaction?> {
             override fun createFromParcel(`in`: Parcel): Transaction? {
                 return Transaction(`in`)
             }

             override fun newArray(size: Int): Array<Transaction?> {
                 return arrayOfNulls(size)
             }
         }
     }*/
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(type)
        parcel.writeDouble(amount)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeLong(date)
        parcel.writeString(image)
    }

    companion object CREATOR : Parcelable.Creator<Transaction> {
        override fun createFromParcel(parcel: Parcel): Transaction {
            return Transaction(parcel)
        }

        override fun newArray(size: Int): Array<Transaction?> {
            return arrayOfNulls(size)
        }
    }
}