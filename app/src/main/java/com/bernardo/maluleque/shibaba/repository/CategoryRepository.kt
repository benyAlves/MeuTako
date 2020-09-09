package com.bernardo.maluleque.shibaba.repository

import androidx.lifecycle.LiveData
import com.bernardo.maluleque.shibaba.database.dao.CategoryDao
import com.bernardo.maluleque.shibaba.model.Category

class CategoryRepository(private val categoryDao: CategoryDao) {


    fun getCategories(type: String): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }
}