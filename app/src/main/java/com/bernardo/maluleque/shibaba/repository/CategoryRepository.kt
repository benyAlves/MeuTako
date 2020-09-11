package com.bernardo.maluleque.shibaba.repository

import androidx.lifecycle.LiveData
import com.bernardo.maluleque.shibaba.database.dao.CategoryDao
import com.bernardo.maluleque.shibaba.model.Category
import com.bernardo.maluleque.shibaba.model.CategoryType

class CategoryRepository(private val categoryDao: CategoryDao) {


    fun getCategories(type: CategoryType): LiveData<List<Category>> {
        return categoryDao.getCategoriesByType(type)
    }

    suspend fun insert(category: Category) {
        return categoryDao.insert(category)
    }
}