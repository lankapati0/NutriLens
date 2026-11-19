package com.akhilav.nutrilens.utils

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import androidx.core.graphics.createBitmap

class ReportChartRenderer {

    fun drawBarChart(title: String, labels:List<String>, values:List<Double>, width:Int = 1000,height:Int=500)
    : Bitmap{
        val bitmap = createBitmap(width, height)

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

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

            paint.color = Color.rgb(21,101,192)

            canvas.drawRoundRect(left,top,left+barWidth,chartBottom,12f,12f,paint)

            paint.color = Color.BLACK
            paint.textSize = 20f

            canvas.drawText(labels.getOrElse(index){""},
                left,chartBottom + 35f,paint)
        }
        return bitmap
    }
}