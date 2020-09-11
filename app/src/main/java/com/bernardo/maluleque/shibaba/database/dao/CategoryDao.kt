package com.bernardo.maluleque.shibaba.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.bernardo.maluleque.shibaba.model.Category
import com.bernardo.maluleque.shibaba.model.CategoryType

@Dao
interface CategoryDao {

    @Query("SELECT * from category where type = :type ORDER BY name ASC")
    fun getCategoriesByType(type: CategoryType): LiveData<List<Category>>

    @Query("SELECT * from category where uid = :uid")
    fun getCategoryById(uid: String): LiveData<Category>

    @Insert
    suspend fun insert(category: Category)

    @Delete
    fun delete(category: Category)

    /* @Transaction
     @Query("SELECT * FROM Category")
     fun getCategoryWithTransactions(): LiveData<List<CategoryWithTransactions>>*/

}