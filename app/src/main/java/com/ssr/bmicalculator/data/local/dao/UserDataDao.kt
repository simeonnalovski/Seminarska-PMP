package com.ssr.bmicalculator.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.ssr.bmicalculator.data.local.entity.UserData

@Dao
interface UserDataDao {
    @Query("SELECT * FROM user_data LIMIT 1")
    suspend fun getUserData(): UserData?

    @Insert
    suspend fun insert(userData: UserData)

    @Update
    suspend fun update(userData: UserData)
}