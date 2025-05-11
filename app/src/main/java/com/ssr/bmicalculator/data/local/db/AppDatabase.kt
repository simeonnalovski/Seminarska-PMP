package com.ssr.bmicalculator.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ssr.bmicalculator.data.local.dao.LanguagePreferenceDao // Important import
import com.ssr.bmicalculator.data.local.dao.UserDataDao // Important import
import com.ssr.bmicalculator.data.local.entity.LanguagePreference
import com.ssr.bmicalculator.data.local.entity.UserData

@Database(entities = [LanguagePreference::class, UserData::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun languagePreferenceDao(): LanguagePreferenceDao // This line is crucial
    abstract fun userDataDao(): UserDataDao // This line is crucial

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: android.content.Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = androidx.room.Room.databaseBuilder(
                                        context.applicationContext,
                                        AppDatabase::class.java,
                                        "app_database"
                                    ).fallbackToDestructiveMigration(false).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}