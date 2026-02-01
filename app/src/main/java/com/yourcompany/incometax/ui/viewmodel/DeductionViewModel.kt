package com.yourcompany.incometax.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yourcompany.incometax.data.model.TaxInputs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DeductionViewModel : ViewModel() {
    
    private val _section80C = MutableStateFlow("")
    val section80C: StateFlow<String> = _section80C.asStateFlow()
    
    private val _section80D = MutableStateFlow("")
    val section80D: StateFlow<String> = _section80D.asStateFlow()
    
    fun updateSection80C(value: String) {
        _section80C.value = value
    }
    
    fun updateSection80D(value: String) {
        _section80D.value = value
    }
    
    fun applyDeductions(inputs: TaxInputs): TaxInputs {
        return inputs.copy(
            section80C = _section80C.value.toDoubleOrNull() ?: 0.0,
            section80D = _section80D.value.toDoubleOrNull() ?: 0.0
        )
    }
    
    fun setInputs(inputs: TaxInputs) {
        _section80C.value = if (inputs.section80C > 0.0) inputs.section80C.toString() else ""
        _section80D.value = if (inputs.section80D > 0.0) inputs.section80D.toString() else ""
    }
}
