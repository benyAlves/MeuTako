package com.bernardo.maluleque.shibaba.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bernardo.maluleque.shibaba.database.dao.CategoryDao
import com.bernardo.maluleque.shibaba.model.Category
import com.bernardo.maluleque.shibaba.model.CategoryType
import com.bernardo.maluleque.shibaba.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryViewModel(categoryDao: CategoryDao) : ViewModel() {


    private val repository: CategoryRepository = CategoryRepository(categoryDao)

    fun getCategoriesByType(type: CategoryType): LiveData<List<Category>> {
        return repository.getCategories(type)
    }

    fun saveCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(category)
    }
}