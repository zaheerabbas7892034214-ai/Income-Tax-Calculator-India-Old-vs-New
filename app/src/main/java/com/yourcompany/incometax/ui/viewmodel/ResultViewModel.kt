package com.yourcompany.incometax.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.incometax.data.model.TaxInputs
import com.yourcompany.incometax.data.model.TaxResult
import com.yourcompany.incometax.domain.calculator.TaxCalculator
import com.yourcompany.incometax.domain.export.PdfExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ResultViewModel : ViewModel() {
    
    private val _oldRegimeResult = MutableStateFlow<TaxResult?>(null)
    val oldRegimeResult: StateFlow<TaxResult?> = _oldRegimeResult.asStateFlow()
    
    private val _newRegimeResult = MutableStateFlow<TaxResult?>(null)
    val newRegimeResult: StateFlow<TaxResult?> = _newRegimeResult.asStateFlow()
    
    private val _recommendedRegime = MutableStateFlow<String?>(null)
    val recommendedRegime: StateFlow<String?> = _recommendedRegime.asStateFlow()
    
    fun calculateTax(inputs: TaxInputs) {
        viewModelScope.launch {
            val (oldResult, newResult, recommendation) = withContext(Dispatchers.Default) {
                val oldResult = TaxCalculator.calculateOldRegime(inputs)
                val newResult = TaxCalculator.calculateNewRegime(inputs)
                val recommendation = TaxCalculator.getRecommendedRegime(oldResult, newResult)
                Triple(oldResult, newResult, recommendation)
            }
            
            _oldRegimeResult.value = oldResult
            _newRegimeResult.value = newResult
            _recommendedRegime.value = recommendation
        }
    }
    
    fun exportPdf(context: Context, inputs: TaxInputs, financialYear: String) {
        viewModelScope.launch {
            val oldResult = _oldRegimeResult.value
            val newResult = _newRegimeResult.value
            
            if (oldResult != null && newResult != null) {
                val uri = withContext(Dispatchers.IO) {
                    PdfExporter.exportToPdf(
                        context = context,
                        inputs = inputs,
                        oldResult = oldResult,
                        newResult = newResult,
                        financialYear = financialYear
                    )
                }
                
                if (uri != null) {
                    PdfExporter.sharePdf(context, uri)
                }
            }
        }
    }
    
    fun clearResults() {
        _oldRegimeResult.value = null
        _newRegimeResult.value = null
        _recommendedRegime.value = null
    }
}
