package com.bernardo.maluleque.shibaba.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity
data class Category(@PrimaryKey val uid: Int,
                    @ColumnInfo(name = "name") val type: String?,
                    @ColumnInfo(name = "type") val name: String?

)