package com.yourcompany.incometax.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yourcompany.incometax.data.dao.EntitlementDao
import com.yourcompany.incometax.data.dao.ProfileDao
import com.yourcompany.incometax.data.entity.EntitlementEntity
import com.yourcompany.incometax.data.entity.ProfileEntity

@Database(
    entities = [ProfileEntity::class, EntitlementEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TaxDatabase : RoomDatabase() {
    abstract fun profileDao(): ProfileDao
    abstract fun entitlementDao(): EntitlementDao

    companion object {
        @Volatile
        private var INSTANCE: TaxDatabase? = null

        fun getDatabase(context: Context): TaxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaxDatabase::class.java,
                    "tax_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
