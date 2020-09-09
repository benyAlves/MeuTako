package com.bernardo.maluleque.shibaba.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.bernardo.maluleque.shibaba.database.dao.CategoryDao
import com.bernardo.maluleque.shibaba.model.Category
import com.bernardo.maluleque.shibaba.repository.CategoryRepository

class CategoryViewModel(categoryDao: CategoryDao) : ViewModel() {

    private val repository: CategoryRepository = CategoryRepository(categoryDao)

    fun getCategoriesByType(type: String): LiveData<List<Category>> {
        return repository.getCategories(type)

    }
}