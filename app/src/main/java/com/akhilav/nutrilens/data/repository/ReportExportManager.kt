package com.akhilav.nutrilens.data.repository

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import coil3.Bitmap
import com.akhilav.nutrilens.data.local.entity.FoodConsumptionEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReportExportManager @Inject constructor(
    @ApplicationContext private val context: Context
){
    fun exportPDFWithCharts(title:String, content: String,charts: List<Pair<String, Bitmap>>): File{
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        var pageNumber = 1
        // First page: report summary
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth,pageHeight,pageNumber++).create()

        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 20f
        }
        canvas.drawText(title,40f,60f,paint)
        paint.textSize = 13f
        var yPosition = 100f

        content.lines().forEach { line ->
            if(yPosition > 790f) return@forEach
            canvas.drawText(line.take(75),40f,yPosition,paint)
            yPosition += 24f
        }
        pdfDocument.finishPage(page)

        // Following pages: charts
        charts.forEach { (chartTitle,bitmap) ->
            val chartPageInfo = PdfDocument.PageInfo.Builder(
                pageWidth, pageHeight, pageNumber++
            ).create()

            val chartPage = pdfDocument.startPage(chartPageInfo)
            val chartCanvas = chartPage.canvas

            val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.BLACK
                textSize = 20f
            }

            chartCanvas.drawText(chartTitle,40f,60f,titlePaint)
            val availableWidth = pageWidth - 60f
            val availableHeight = 550f

            val scale = minOf(
                availableWidth / bitmap.width,
                availableHeight / bitmap.height
            )

            val scaledWidth = bitmap.width * scale
            val scaledHeight = bitmap.height * scale

            val left = (pageWidth - scaledWidth) / 2f
            val top = 75f

            val destination = RectF(
                left,
                top,
                left + scaledWidth,
                top + scaledHeight
            )

            chartCanvas.drawBitmap(bitmap,null,destination,Paint(Paint.ANTI_ALIAS_FLAG))

            pdfDocument.finishPage(chartPage)

        }

        val reportDirectory = File(context.cacheDir,"reports")
        reportDirectory.mkdirs()

        val file = File(reportDirectory,"nutrition_report_${System.currentTimeMillis()}.pdf")

        file.outputStream().use {
            pdfDocument.writeTo(it)
        }
        pdfDocument.close()
        return file
    }

    fun exportCsv(consumption: List<FoodConsumptionEntity?>): File {
        val reportDirectory = File(context.cacheDir,"reports")
        if(!reportDirectory.exists()){
            reportDirectory.mkdirs()
        }
        val file = File(reportDirectory,"nutrition_report_${System.currentTimeMillis()}.csv")

        fun csvValue(value: String): String {
            return "\"${value.replace("\"", "\"\"")}\""
        }

        val csvContent = buildString {
            appendLine("Date,Food Name,Quantity (g),Calories (kcal),Protein (g),Carbohydrates (g),Fat (g)")

            consumption.filterNotNull().forEach { item ->
                val date = java.text.SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    java.util.Locale.getDefault()
                ).format(java.util.Date(item.consumedAt))
                appendLine(
                    listOf(
                        csvValue(date),
                        csvValue(item.productName),
                        item.quantityGrams.toString(),
                        item.calories?.toString() ?: "",
                        item.protein?.toString() ?: "",
                        item.carbohydrates?.toString() ?: "",
                        item.fat?.toString() ?: ""
                    ).joinToString(",")
                )
            }
        }
        file.writeText(csvContent, Charsets.UTF_8)
        return file
    }

}