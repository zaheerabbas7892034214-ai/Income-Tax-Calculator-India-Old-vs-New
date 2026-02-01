package com.yourcompany.incometax.domain.repository

import com.yourcompany.incometax.data.dao.EntitlementDao
import com.yourcompany.incometax.data.entity.EntitlementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EntitlementRepository(
    private val entitlementDao: EntitlementDao
) {
    
    fun isPro(): Flow<Boolean> {
        return entitlementDao.getEntitlement().map { entity ->
            entity?.isPro ?: false
        }
    }
    
    suspend fun updateProStatus(
        isPro: Boolean,
        purchaseToken: String? = null,
        purchaseTime: Long? = null
    ) {
        val entitlement = EntitlementEntity(
            id = 1,
            isPro = isPro,
            purchaseToken = purchaseToken,
            purchaseTime = purchaseTime
        )
        entitlementDao.updateEntitlement(entitlement)
    }
}
