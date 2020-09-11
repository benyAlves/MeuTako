package com.bernardo.maluleque.shibaba.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bernardo.maluleque.shibaba.database.ShibabaDatabase
import com.bernardo.maluleque.shibaba.di.testModule
import com.bernardo.maluleque.shibaba.model.Category
import com.bernardo.maluleque.shibaba.model.CategoryType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject

@RunWith(AndroidJUnit4::class)
class CategoryRepositoryTest : KoinTest {


    @Before
    fun setUp() {
        startKoin {
            modules(listOf(testModule))
        }
    }

    val database: ShibabaDatabase by inject()

    @Test
    fun `insert a new category save it in database`() = runBlocking {
        val category = Category(type = CategoryType.EXPENSE, name = "This is category")
        database.categoryDao().insert(category)
    }

    @After
    fun after() {
        database.close()
        stopKoin()
    }

}