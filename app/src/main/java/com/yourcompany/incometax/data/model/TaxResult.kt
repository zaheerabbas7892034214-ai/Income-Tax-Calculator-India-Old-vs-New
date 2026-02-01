package com.yourcompany.incometax.data.model

data class TaxResult(
    val taxPayable: Double,
    val effectiveTaxRate: Double,
    val regime: String
)
