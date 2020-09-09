package com.bernardo.maluleque.shibaba.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bernardo.maluleque.shibaba.model.Category

@Dao
interface CategoryDao {

    @Query("SELECT * from category where type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: String): LiveData<List<Category>>

    @Query("SELECT * from category where uid = :uid")
    fun getCategoryById(uid: String): LiveData<Category>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Delete
    fun delete(category: Category)

    /* @Transaction
     @Query("SELECT * FROM Category")
     fun getCategoryWithTransactions(): LiveData<List<CategoryWithTransactions>>*/

}