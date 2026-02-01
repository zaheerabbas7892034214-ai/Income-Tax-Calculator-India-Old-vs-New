package com.yourcompany.incometax.domain.export

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.yourcompany.incometax.data.model.TaxInputs
import com.yourcompany.incometax.data.model.TaxResult
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {
    
    private const val TAG = "PdfExporter"
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 40
    private const val LINE_HEIGHT = 20
    
    fun exportToPdf(
        context: Context,
        inputs: TaxInputs,
        oldResult: TaxResult,
        newResult: TaxResult,
        financialYear: String
    ): Uri? {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            
            val canvas = page.canvas
            var yPosition = MARGIN + 20
            
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            val headerPaint = Paint().apply {
                textSize = 16f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            val normalPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.BLACK
            }
            
            val boldPaint = Paint().apply {
                textSize = 12f
                isFakeBoldText = true
                color = android.graphics.Color.BLACK
            }
            
            canvas.drawText("Income Tax Calculator Report", MARGIN.toFloat(), yPosition.toFloat(), titlePaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawText("Financial Year: $financialYear", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            canvas.drawText("Generated on: ${dateFormat.format(Date())}", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawLine(
                MARGIN.toFloat(), 
                yPosition.toFloat(), 
                (PAGE_WIDTH - MARGIN).toFloat(), 
                yPosition.toFloat(), 
                normalPaint
            )
            yPosition += LINE_HEIGHT
            
            canvas.drawText("Income Details", MARGIN.toFloat(), yPosition.toFloat(), headerPaint)
            yPosition += LINE_HEIGHT + 5
            
            canvas.drawText("Basic Salary:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(inputs.basicSalary)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            canvas.drawText("HRA:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(inputs.hra)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            canvas.drawText("Other Allowances:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(inputs.otherAllowances)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            canvas.drawText("Other Income:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(inputs.otherIncome)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            val grossIncome = inputs.basicSalary + inputs.hra + inputs.otherAllowances + inputs.otherIncome
            canvas.drawText("Gross Income:", MARGIN.toFloat(), yPosition.toFloat(), boldPaint)
            canvas.drawText("₹${formatAmount(grossIncome)}", 300f, yPosition.toFloat(), boldPaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawText("Deductions (Old Regime)", MARGIN.toFloat(), yPosition.toFloat(), headerPaint)
            yPosition += LINE_HEIGHT + 5
            
            canvas.drawText("Section 80C:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(inputs.section80C)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            canvas.drawText("Section 80D:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(inputs.section80D)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawLine(
                MARGIN.toFloat(), 
                yPosition.toFloat(), 
                (PAGE_WIDTH - MARGIN).toFloat(), 
                yPosition.toFloat(), 
                normalPaint
            )
            yPosition += LINE_HEIGHT
            
            canvas.drawText("Tax Calculation Results", MARGIN.toFloat(), yPosition.toFloat(), headerPaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawText("Old Regime", MARGIN.toFloat(), yPosition.toFloat(), boldPaint)
            yPosition += LINE_HEIGHT + 5
            
            canvas.drawText("Tax Payable:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(oldResult.taxPayable)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            canvas.drawText("Effective Tax Rate:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("${String.format("%.2f", oldResult.effectiveTaxRate)}%", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawText("New Regime", MARGIN.toFloat(), yPosition.toFloat(), boldPaint)
            yPosition += LINE_HEIGHT + 5
            
            canvas.drawText("Tax Payable:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("₹${formatAmount(newResult.taxPayable)}", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT
            
            canvas.drawText("Effective Tax Rate:", MARGIN.toFloat(), yPosition.toFloat(), normalPaint)
            canvas.drawText("${String.format("%.2f", newResult.effectiveTaxRate)}%", 300f, yPosition.toFloat(), normalPaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawLine(
                MARGIN.toFloat(), 
                yPosition.toFloat(), 
                (PAGE_WIDTH - MARGIN).toFloat(), 
                yPosition.toFloat(), 
                normalPaint
            )
            yPosition += LINE_HEIGHT
            
            val recommendation = if (newResult.taxPayable < oldResult.taxPayable) {
                "New Regime (Saves ₹${formatAmount(oldResult.taxPayable - newResult.taxPayable)})"
            } else if (oldResult.taxPayable < newResult.taxPayable) {
                "Old Regime (Saves ₹${formatAmount(newResult.taxPayable - oldResult.taxPayable)})"
            } else {
                "Both regimes have equal tax"
            }
            
            canvas.drawText("Recommendation", MARGIN.toFloat(), yPosition.toFloat(), headerPaint)
            yPosition += LINE_HEIGHT + 5
            
            canvas.drawText(recommendation, MARGIN.toFloat(), yPosition.toFloat(), boldPaint)
            yPosition += LINE_HEIGHT * 2
            
            canvas.drawText(
                "Note: This is a simplified calculation. Please consult a tax professional for accurate assessment.",
                MARGIN.toFloat(),
                yPosition.toFloat(),
                normalPaint.apply { textSize = 10f }
            )
            
            pdfDocument.finishPage(page)
            
            val fileName = "TaxReport_${financialYear.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
            val file = File(context.cacheDir, fileName)
            
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            pdfDocument.close()
            
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            
            Log.d(TAG, "PDF exported successfully: $uri")
            uri
            
        } catch (e: Exception) {
            Log.e(TAG, "Error exporting PDF", e)
            null
        }
    }
    
    fun sharePdf(context: Context, uri: Uri) {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Share Tax Report"))
    }
    
    private fun formatAmount(amount: Double): String {
        return String.format("%,.2f", amount)
    }
}
