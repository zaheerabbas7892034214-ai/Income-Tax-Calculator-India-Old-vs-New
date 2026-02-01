package com.yourcompany.incometax.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entitlement")
data class EntitlementEntity(
    @PrimaryKey
    val id: Int = 1,
    val isPro: Boolean = false,
    val purchaseToken: String? = null,
    val purchaseTime: Long? = null
)
