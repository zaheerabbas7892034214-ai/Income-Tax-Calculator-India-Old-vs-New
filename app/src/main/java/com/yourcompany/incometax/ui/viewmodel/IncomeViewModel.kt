package com.yourcompany.incometax.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yourcompany.incometax.data.model.TaxInputs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class IncomeViewModel : ViewModel() {
    
    private val _basicSalary = MutableStateFlow("")
    val basicSalary: StateFlow<String> = _basicSalary.asStateFlow()
    
    private val _hra = MutableStateFlow("")
    val hra: StateFlow<String> = _hra.asStateFlow()
    
    private val _otherAllowances = MutableStateFlow("")
    val otherAllowances: StateFlow<String> = _otherAllowances.asStateFlow()
    
    private val _otherIncome = MutableStateFlow("")
    val otherIncome: StateFlow<String> = _otherIncome.asStateFlow()
    
    fun updateBasicSalary(value: String) {
        _basicSalary.value = value
    }
    
    fun updateHra(value: String) {
        _hra.value = value
    }
    
    fun updateOtherAllowances(value: String) {
        _otherAllowances.value = value
    }
    
    fun updateOtherIncome(value: String) {
        _otherIncome.value = value
    }
    
    fun getTaxInputs(): TaxInputs {
        return TaxInputs(
            basicSalary = _basicSalary.value.toDoubleOrNull() ?: 0.0,
            hra = _hra.value.toDoubleOrNull() ?: 0.0,
            otherAllowances = _otherAllowances.value.toDoubleOrNull() ?: 0.0,
            otherIncome = _otherIncome.value.toDoubleOrNull() ?: 0.0
        )
    }
    
    fun setInputs(inputs: TaxInputs) {
        _basicSalary.value = if (inputs.basicSalary > 0.0) inputs.basicSalary.toString() else ""
        _hra.value = if (inputs.hra > 0.0) inputs.hra.toString() else ""
        _otherAllowances.value = if (inputs.otherAllowances > 0.0) inputs.otherAllowances.toString() else ""
        _otherIncome.value = if (inputs.otherIncome > 0.0) inputs.otherIncome.toString() else ""
    }
}
