package com.yourcompany.incometax.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yourcompany.incometax.data.entity.EntitlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntitlementDao {
    @Query("SELECT * FROM entitlement WHERE id = 1")
    fun getEntitlement(): Flow<EntitlementEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateEntitlement(entitlement: EntitlementEntity)
}
