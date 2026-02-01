package com.yourcompany.incometax.domain.calculator

import com.yourcompany.incometax.data.model.TaxInputs
import com.yourcompany.incometax.data.model.TaxResult
import com.yourcompany.incometax.data.model.TaxSlab
import kotlin.math.max

object TaxCalculator {
    
    private const val CESS_RATE = 0.04
    
    private val oldRegimeSlabs2023_24 = listOf(
        TaxSlab(250000.0, 0.0),
        TaxSlab(500000.0, 0.05),
        TaxSlab(1000000.0, 0.20),
        TaxSlab(Double.MAX_VALUE, 0.30)
    )
    
    private val oldRegimeSlabs2024_25 = listOf(
        TaxSlab(250000.0, 0.0),
        TaxSlab(500000.0, 0.05),
        TaxSlab(1000000.0, 0.20),
        TaxSlab(Double.MAX_VALUE, 0.30)
    )
    
    private val oldRegimeSlabs2025_26 = listOf(
        TaxSlab(250000.0, 0.0),
        TaxSlab(500000.0, 0.05),
        TaxSlab(1000000.0, 0.20),
        TaxSlab(Double.MAX_VALUE, 0.30)
    )
    
    private val newRegimeSlabs2023_24 = listOf(
        TaxSlab(300000.0, 0.0),
        TaxSlab(600000.0, 0.05),
        TaxSlab(900000.0, 0.10),
        TaxSlab(1200000.0, 0.15),
        TaxSlab(1500000.0, 0.20),
        TaxSlab(Double.MAX_VALUE, 0.30)
    )
    
    private val newRegimeSlabs2024_25 = listOf(
        TaxSlab(300000.0, 0.0),
        TaxSlab(700000.0, 0.05),
        TaxSlab(1000000.0, 0.10),
        TaxSlab(1200000.0, 0.15),
        TaxSlab(1500000.0, 0.20),
        TaxSlab(Double.MAX_VALUE, 0.30)
    )
    
    private val newRegimeSlabs2025_26 = listOf(
        TaxSlab(400000.0, 0.0),
        TaxSlab(800000.0, 0.05),
        TaxSlab(1200000.0, 0.10),
        TaxSlab(1600000.0, 0.15),
        TaxSlab(2000000.0, 0.20),
        TaxSlab(Double.MAX_VALUE, 0.30)
    )
    
    fun calculateOldRegime(inputs: TaxInputs): TaxResult {
        val slabs = when (inputs.financialYear) {
            "FY 2023-24" -> oldRegimeSlabs2023_24
            "FY 2024-25" -> oldRegimeSlabs2024_25
            "FY 2025-26" -> oldRegimeSlabs2025_26
            else -> oldRegimeSlabs2024_25
        }
        
        val grossIncome = inputs.basicSalary + inputs.hra + inputs.otherAllowances + inputs.otherIncome
        
        val standardDeduction = 50000.0
        
        val hraExemption = calculateHraExemption(
            hra = inputs.hra,
            basicSalary = inputs.basicSalary,
            rentPaid = 0.0
        )
        
        val totalDeductions = standardDeduction + 
                            inputs.section80C.coerceAtMost(150000.0) + 
                            inputs.section80D.coerceAtMost(25000.0) +
                            hraExemption
        
        val taxableIncome = max(0.0, grossIncome - totalDeductions)
        
        val taxBeforeCess = calculateTaxFromSlabs(taxableIncome, slabs)
        val cess = taxBeforeCess * CESS_RATE
        val totalTax = taxBeforeCess + cess
        
        val effectiveRate = if (grossIncome > 0) (totalTax / grossIncome) * 100 else 0.0
        
        return TaxResult(
            taxPayable = totalTax,
            effectiveTaxRate = effectiveRate,
            regime = "Old Regime"
        )
    }
    
    fun calculateNewRegime(inputs: TaxInputs): TaxResult {
        val slabs = when (inputs.financialYear) {
            "FY 2023-24" -> newRegimeSlabs2023_24
            "FY 2024-25" -> newRegimeSlabs2024_25
            "FY 2025-26" -> newRegimeSlabs2025_26
            else -> newRegimeSlabs2024_25
        }
        
        val grossIncome = inputs.basicSalary + inputs.hra + inputs.otherAllowances + inputs.otherIncome
        
        val standardDeduction = when (inputs.financialYear) {
            "FY 2023-24" -> 0.0
            "FY 2024-25" -> 50000.0
            "FY 2025-26" -> 75000.0
            else -> 50000.0
        }
        
        val taxableIncome = max(0.0, grossIncome - standardDeduction)
        
        val taxBeforeCess = calculateTaxFromSlabs(taxableIncome, slabs)
        
        val rebate = if (taxableIncome <= getRebateLimit(inputs.financialYear)) {
            taxBeforeCess.coerceAtMost(getRebateAmount(inputs.financialYear))
        } else {
            0.0
        }
        
        val taxAfterRebate = max(0.0, taxBeforeCess - rebate)
        val cess = taxAfterRebate * CESS_RATE
        val totalTax = taxAfterRebate + cess
        
        val effectiveRate = if (grossIncome > 0) (totalTax / grossIncome) * 100 else 0.0
        
        return TaxResult(
            taxPayable = totalTax,
            effectiveTaxRate = effectiveRate,
            regime = "New Regime"
        )
    }
    
    fun getRecommendedRegime(oldResult: TaxResult, newResult: TaxResult): String {
        return if (newResult.taxPayable < oldResult.taxPayable) {
            "New Regime (Lower tax of ₹${String.format("%.2f", newResult.taxPayable)})"
        } else if (oldResult.taxPayable < newResult.taxPayable) {
            "Old Regime (Lower tax of ₹${String.format("%.2f", oldResult.taxPayable)})"
        } else {
            "Both regimes have equal tax (₹${String.format("%.2f", oldResult.taxPayable)})"
        }
    }
    
    private fun calculateTaxFromSlabs(income: Double, slabs: List<TaxSlab>): Double {
        var tax = 0.0
        var previousLimit = 0.0
        
        for (slab in slabs) {
            if (income <= previousLimit) break
            
            val taxableInThisSlab = if (income > slab.upTo) {
                slab.upTo - previousLimit
            } else {
                income - previousLimit
            }
            
            tax += taxableInThisSlab * slab.rate
            previousLimit = slab.upTo
        }
        
        return tax
    }
    
    private fun calculateHraExemption(hra: Double, basicSalary: Double, rentPaid: Double): Double {
        if (hra == 0.0) return 0.0
        
        val actual = hra
        val rentMinusTenPercent = max(0.0, rentPaid - (basicSalary * 0.10))
        val fiftyPercent = basicSalary * 0.50
        
        return minOf(actual, rentMinusTenPercent, fiftyPercent)
    }
    
    private fun getRebateLimit(financialYear: String): Double {
        return when (financialYear) {
            "FY 2023-24" -> 700000.0
            "FY 2024-25" -> 700000.0
            "FY 2025-26" -> 800000.0
            else -> 700000.0
        }
    }
    
    private fun getRebateAmount(financialYear: String): Double {
        return when (financialYear) {
            "FY 2023-24" -> 25000.0
            "FY 2024-25" -> 25000.0
            "FY 2025-26" -> 60000.0
            else -> 25000.0
        }
    }
}
