package com.yourcompany.incometax.domain.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import com.yourcompany.incometax.domain.repository.EntitlementRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BillingManager(
    private val context: Context,
    private val entitlementRepository: EntitlementRepository
) {
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    
    private val _purchaseStatus = MutableStateFlow(false)
    val purchaseStatus: StateFlow<Boolean> = _purchaseStatus.asStateFlow()
    
    private val _billingState = MutableStateFlow<BillingState>(BillingState.Idle)
    val billingState: StateFlow<BillingState> = _billingState.asStateFlow()
    
    private var billingClient: BillingClient? = null
    
    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _billingState.value = BillingState.Error("Purchase cancelled")
        } else {
            _billingState.value = BillingState.Error("Error: ${billingResult.debugMessage}")
        }
    }
    
    init {
        initializeBillingClient()
    }
    
    private fun initializeBillingClient() {
        billingClient = BillingClient.newBuilder(context)
            .setListener(purchasesUpdatedListener)
            .enablePendingPurchases()
            .build()
        
        connectToBillingService()
    }
    
    private fun connectToBillingService() {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _billingState.value = BillingState.Connected
                    queryPurchases()
                } else {
                    _billingState.value = BillingState.Error("Connection failed: ${billingResult.debugMessage}")
                }
            }
            
            override fun onBillingServiceDisconnected() {
                _billingState.value = BillingState.Disconnected
                connectToBillingService()
            }
        })
    }
    
    fun launchPurchaseFlow(activity: Activity) {
        if (billingClient?.isReady != true) {
            _billingState.value = BillingState.Error("Billing client not ready")
            return
        }
        
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(PRODUCT_ID)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
                )
            )
            .build()
        
        billingClient?.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                val productDetails = productDetailsList[0]
                
                val productDetailsParamsList = listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build()
                )
                
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(productDetailsParamsList)
                    .build()
                
                billingClient?.launchBillingFlow(activity, billingFlowParams)
            } else {
                _billingState.value = BillingState.Error("Product not found: ${billingResult.debugMessage}")
            }
        }
    }
    
    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                acknowledgePurchase(purchase)
            } else {
                updateProStatus(purchase)
            }
        }
    }
    
    private fun acknowledgePurchase(purchase: Purchase) {
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
            .build()
        
        billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                updateProStatus(purchase)
            }
        }
    }
    
    private fun updateProStatus(purchase: Purchase) {
        scope.launch {
            try {
                entitlementRepository.updateProStatus(
                    isPro = true,
                    purchaseToken = purchase.purchaseToken,
                    purchaseTime = purchase.purchaseTime
                )
                _purchaseStatus.value = true
                _billingState.value = BillingState.PurchaseSuccess
            } catch (e: Exception) {
                Log.e(TAG, "Error updating pro status", e)
                _billingState.value = BillingState.Error("Failed to update status: ${e.message}")
            }
        }
    }
    
    fun processPurchases() {
        queryPurchases()
    }
    
    private fun queryPurchases() {
        if (billingClient?.isReady != true) {
            return
        }
        
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        
        billingClient?.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                var hasPro = false
                for (purchase in purchases) {
                    if (purchase.products.contains(PRODUCT_ID) && 
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        hasPro = true
                        handlePurchase(purchase)
                        break
                    }
                }
                if (!hasPro) {
                    scope.launch {
                        entitlementRepository.updateProStatus(isPro = false)
                        _purchaseStatus.value = false
                    }
                }
            }
        }
    }
    
    fun restorePurchases() {
        _billingState.value = BillingState.Restoring
        queryPurchases()
    }
    
    fun getPurchaseStatus(): StateFlow<Boolean> {
        return purchaseStatus
    }
    
    fun disconnect() {
        billingClient?.endConnection()
    }
    
    sealed class BillingState {
        object Idle : BillingState()
        object Connected : BillingState()
        object Disconnected : BillingState()
        object Restoring : BillingState()
        object PurchaseSuccess : BillingState()
        data class Error(val message: String) : BillingState()
    }
    
    companion object {
        private const val TAG = "BillingManager"
        const val PRODUCT_ID = "tax_pro_unlock"
    }
}
