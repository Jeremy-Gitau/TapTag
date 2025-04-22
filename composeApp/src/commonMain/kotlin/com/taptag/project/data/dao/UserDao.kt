package com.taptag.project.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.taptag.project.sources.local.room.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(authResponse: UserEntity)

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    @Transaction
    @Query("SELECT * FROM user WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    @Transaction
    @Query("DELETE FROM user WHERE id = :userId")
    suspend fun clearUserById(userId: String)

}