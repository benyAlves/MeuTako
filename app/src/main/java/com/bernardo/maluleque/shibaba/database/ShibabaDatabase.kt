package com.bernardo.maluleque.shibaba.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.bernardo.maluleque.shibaba.database.dao.CategoryDao
import com.bernardo.maluleque.shibaba.database.dao.TransactionDao
import com.bernardo.maluleque.shibaba.model.Category
import com.bernardo.maluleque.shibaba.model.Transaction

@Database(entities = [Category::class, Transaction::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class ShibabaDatabase : RoomDatabase() {

    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: ShibabaDatabase? = null

        fun getDatabase(context: Context): ShibabaDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                        context.applicationContext,
                        ShibabaDatabase::class.java,
                        "word_database"
                ).fallbackToDestructiveMigration()
                        .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}