package com.test.test_karim.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.galee.core.Constant
import com.test.test_karim.data.model.GueestsDAO
import com.test.test_karim.data.model.Guests

@Database(entities = [Guests::class],
     version = 5, exportSchema = true)

abstract class AppDatabase : RoomDatabase() {

    abstract fun guestDAO(): GueestsDAO

    companion object {
        var database: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (database == null) {
                synchronized(AppDatabase::class.java) {
                    if (database == null) {
                        database = buildDatabase(context)
                    }
                }
            }
            return database
        }

        fun buildDatabase(applicationContext: Context): AppDatabase? {
            return Room.databaseBuilder(applicationContext.applicationContext, AppDatabase::class.java, Constant.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                //.allowMainThreadQueries()
                .build()
        }
    }
}