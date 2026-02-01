package com.yourcompany.incometax.data.model

data class TaxInputs(
    val financialYear: String = "",
    val basicSalary: Double = 0.0,
    val hra: Double = 0.0,
    val otherAllowances: Double = 0.0,
    val otherIncome: Double = 0.0,
    val section80C: Double = 0.0,
    val section80D: Double = 0.0
) {
    val totalIncome: Double
        get() = basicSalary + hra + otherAllowances + otherIncome
    
    val standardDeduction: Double
        get() = 50000.0
    
    val section80Deductions: Double
        get() = section80C + section80D
}
