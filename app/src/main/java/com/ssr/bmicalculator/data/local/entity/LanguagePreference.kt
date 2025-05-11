package com.ssr.bmicalculator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "language_preferences")
data class LanguagePreference(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val languageCode: String
)