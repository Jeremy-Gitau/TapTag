package com.taptag.project.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.taptag.project.sources.local.room.entities.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(userProfile: UserProfileEntity)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfileEntity)

    @Transaction
    @Query("SELECT * FROM userProfile WHERE id = :profileId")
    suspend fun getUserProfileById(profileId: String): UserProfileEntity?

    @Transaction
    @Query("SELECT * FROM userProfile WHERE id = :profileId")
    fun getUserProfileByIdFlow(profileId: String): Flow<UserProfileEntity?>

    @Transaction
    @Query("SELECT * FROM userProfile")
    suspend fun getAllUserProfiles(): List<UserProfileEntity>

    @Transaction
    @Query("SELECT * FROM userProfile")
    fun getAllUserProfilesFlow(): Flow<List<UserProfileEntity>>

    @Transaction
    @Query("DELETE FROM userProfile WHERE id = :profileId")
    suspend fun deleteUserProfileById(profileId: String)

    @Transaction
    @Query("DELETE FROM userProfile")
    suspend fun deleteAllUserProfiles()

}