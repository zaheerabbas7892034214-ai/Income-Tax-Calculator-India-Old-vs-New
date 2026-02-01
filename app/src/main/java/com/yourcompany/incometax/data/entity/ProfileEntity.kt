package com.yourcompany.incometax.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val inputsJson: String,
    val createdAt: Long = System.currentTimeMillis()
)
