package com.yourcompany.incometax.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    
    val availableYears = listOf("FY 2023-24", "FY 2024-25", "FY 2025-26")
    
    private val _selectedFinancialYear = MutableStateFlow("FY 2024-25")
    val selectedFinancialYear: StateFlow<String> = _selectedFinancialYear.asStateFlow()
    
    fun updateSelectedYear(year: String) {
        if (year in availableYears) {
            _selectedFinancialYear.value = year
        }
    }
}
