package com.yourcompany.incometax.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.incometax.domain.billing.BillingManager
import com.yourcompany.incometax.domain.repository.EntitlementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BillingViewModel(
    private val billingManager: BillingManager,
    private val entitlementRepository: EntitlementRepository
) : ViewModel() {
    
    enum class PurchaseState {
        Idle,
        Loading,
        Success,
        Failed
    }
    
    val isPro: StateFlow<Boolean> = entitlementRepository.isPro()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    private val _purchaseState = MutableStateFlow(PurchaseState.Idle)
    val purchaseState: StateFlow<PurchaseState> = _purchaseState.asStateFlow()
    
    init {
        observeBillingState()
    }
    
    private fun observeBillingState() {
        viewModelScope.launch {
            billingManager.billingState.collect { state ->
                when (state) {
                    is BillingManager.BillingState.PurchaseSuccess -> {
                        _purchaseState.value = PurchaseState.Success
                    }
                    is BillingManager.BillingState.Error -> {
                        _purchaseState.value = PurchaseState.Failed
                    }
                    is BillingManager.BillingState.Restoring -> {
                        _purchaseState.value = PurchaseState.Loading
                    }
                    else -> {
                        if (_purchaseState.value != PurchaseState.Success) {
                            _purchaseState.value = PurchaseState.Idle
                        }
                    }
                }
            }
        }
    }
    
    fun launchPurchase(activity: Activity) {
        _purchaseState.value = PurchaseState.Loading
        billingManager.launchPurchaseFlow(activity)
    }
    
    fun restorePurchase() {
        _purchaseState.value = PurchaseState.Loading
        billingManager.restorePurchases()
    }
    
    fun resetPurchaseState() {
        _purchaseState.value = PurchaseState.Idle
    }
    
    override fun onCleared() {
        super.onCleared()
        billingManager.disconnect()
    }
}
