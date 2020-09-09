package com.bernardo.maluleque.shibaba.model

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithTransactions(
        @Embedded val category: Category,
        @Relation(
                parentColumn = "uid",
                entityColumn = "category"
        )
        val transactions: List<Transaction>
)
