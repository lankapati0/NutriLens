package com.akhilav.nutrilens.utils

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import androidx.core.graphics.createBitmap

class ReportChartRenderer {

    fun drawBarChart(title: String, labels:List<String>, values:List<Double>, width:Int = 1000,height:Int=500)
    : Bitmap{
        val bitmap = createBitmap(width, height)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.rgb(244,247,243))

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 36f
        }
        if(title.isNotBlank()){
            canvas.drawText(title,50f,55f,paint)
        }

        val chartLeft = 80f
        val chartTop = 20f
        val chartBottom = height - 70f
        val chartRight = width - 50f

        paint.strokeWidth = 3f
        canvas.drawLine(chartLeft,chartBottom,chartRight,chartBottom,paint)

        if(values.isEmpty()){
            paint.textSize = 28f
            canvas.drawText("No data available",chartLeft,chartTop+50f,paint)
            return bitmap
        }
        val maxValue = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
        val slotWidth = (chartRight - chartLeft) / values.size
        val barWidth = slotWidth * 0.55f

        values.forEachIndexed { index, value ->
            val barHeight = ((value/maxValue) * (chartBottom - chartTop)).toFloat()
            val left = chartLeft+index*slotWidth - (slotWidth-barWidth)/2

            val top = chartBottom - barHeight

            paint.color = Color.rgb(36,158,149)

            canvas.drawRoundRect(left,top,left+barWidth,chartBottom,12f,12f,paint)

            paint.color = Color.BLACK
            paint.textSize = 20f

            canvas.drawText(labels.getOrElse(index){""},
                left,chartBottom + 35f,paint)
        }
        return bitmap
    }

    fun drawCircularNutritionChart(
        title: String,
        value: Double?,
        target: Double,
        unit: String,
        color: Int,
        width: Int = 700,
        height: Int = 500
    ): Bitmap {
        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        val backgroundColor = Color.rgb(244, 247, 243)
        val textColor = Color.rgb(25, 49, 57)
        val trackColor = Color.rgb(220, 231, 224)

        canvas.drawColor(backgroundColor)

        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = textColor
            textSize = 38f
            typeface = android.graphics.Typeface.create(
                android.graphics.Typeface.DEFAULT,
                android.graphics.Typeface.BOLD
            )
        }

        canvas.drawText(title, 45f, 65f, titlePaint)

        val centerX = width / 2f
        val centerY = 245f
        val radius = 115f
        val strokeWidth = 24f

        val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            this.strokeWidth = strokeWidth
            strokeCap = Paint.Cap.ROUND
        }

        val ringBounds = RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        )

        ringPaint.color = trackColor
        canvas.drawArc(ringBounds, -90f, 360f, false, ringPaint)

        if (value != null && target > 0) {
            val progress = (value / target).toFloat().coerceIn(0f, 1f)

            ringPaint.color = color
            canvas.drawArc(
                ringBounds,
                -90f,
                progress * 360f,
                false,
                ringPaint
            )
        }

        val valuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = textColor
            textSize = 42f
            textAlign = Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
        }

        val displayValue = value?.let {
            if (unit == "kcal" || unit == "ml") {
                it.toInt().toString()
            } else {
                String.format(java.util.Locale.getDefault(), "%.1f", it)
            }
        } ?: "N/A"

        canvas.drawText(displayValue, centerX, centerY + 8f, valuePaint)

        val unitPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = textColor
            textSize = 23f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(unit, centerX, centerY + 42f, unitPaint)

        val targetPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = textColor
            textSize = 25f
            textAlign = Paint.Align.CENTER
        }

        canvas.drawText(
            "Daily Goal: ${target.toInt()} $unit",
            centerX,
            430f,
            targetPaint
        )

        return bitmap
    }

}