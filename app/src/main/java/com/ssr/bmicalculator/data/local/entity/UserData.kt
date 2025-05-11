package com.ssr.bmicalculator.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_data")
data class UserData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val gender: String? = null,
    val age: Int? = null,
    val height: String? = null,
    val weight: Int? = null
)