package com.bernardo.maluleque.shibaba.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
@Entity
data class Category(@PrimaryKey(autoGenerate = true) val uid: Int = 0,
                    @ColumnInfo(name = "name") val type: CategoryType,
                    @ColumnInfo(name = "type") val name: String

)