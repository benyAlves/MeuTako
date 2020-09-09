package com.bernardo.maluleque.shibaba.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bernardo.maluleque.shibaba.model.Transaction

@Dao
interface TransactionDao {

    @Query("SELECT * from `transaction` where date between :initialDate and :endDate")
    fun getTransactionByDate(initialDate: Long, endDate: Long): LiveData<List<Transaction>>

    @Query("SELECT * from `transaction` where uid = :uid")
    fun getCategoryById(uid: String): LiveData<Transaction>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transaction: Transaction)

    @Delete
    fun delete(transaction: Transaction)
}