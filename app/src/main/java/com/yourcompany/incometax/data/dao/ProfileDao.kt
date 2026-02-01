package com.yourcompany.incometax.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yourcompany.incometax.data.entity.ProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Query("SELECT * FROM profiles ORDER BY createdAt DESC")
    fun getAllProfiles(): Flow<List<ProfileEntity>>

    @Insert
    suspend fun insertProfile(profile: ProfileEntity)

    @Query("DELETE FROM profiles WHERE id = :id")
    suspend fun deleteProfile(id: Long)

    @Query("DELETE FROM profiles")
    suspend fun deleteAllProfiles()
}
