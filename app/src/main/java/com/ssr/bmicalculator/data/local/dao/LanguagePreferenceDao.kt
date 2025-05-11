package com.ssr.bmicalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ssr.bmicalculator.data.local.entity.LanguagePreference

@Dao
interface LanguagePreferenceDao {
    @Query("SELECT * FROM language_preferences LIMIT 1")
    suspend fun getPreferredLanguage(): LanguagePreference?

    @Insert
    suspend fun insert(preference: LanguagePreference)

    @Update
    suspend fun update(preference: LanguagePreference)
}