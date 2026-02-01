package com.yourcompany.incometax.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yourcompany.incometax.data.database.TaxDatabase
import com.yourcompany.incometax.domain.billing.BillingManager
import com.yourcompany.incometax.domain.repository.EntitlementRepository
import com.yourcompany.incometax.domain.repository.ProfileRepository

class ViewModelFactory(
    private val application: Application,
    private val billingManager: BillingManager
) : ViewModelProvider.Factory {

    private val database by lazy { TaxDatabase.getDatabase(application) }
    private val profileRepository by lazy { ProfileRepository(database.profileDao()) }
    private val entitlementRepository by lazy { EntitlementRepository(database.entitlementDao()) }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(IncomeViewModel::class.java) -> {
                IncomeViewModel() as T
            }
            modelClass.isAssignableFrom(DeductionViewModel::class.java) -> {
                DeductionViewModel() as T
            }
            modelClass.isAssignableFrom(ResultViewModel::class.java) -> {
                ResultViewModel() as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel() as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(profileRepository, entitlementRepository) as T
            }
            modelClass.isAssignableFrom(BillingViewModel::class.java) -> {
                BillingViewModel(billingManager, entitlementRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
