package com.akhilav.nutrilens.utils.parser

import android.graphics.Bitmap
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.tracing.AtomicBoolean
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

data class IngredientTextLine(val text: String, val left:Int, val top:Int, val right:Int, val bottom:Int)

class IngredientLabelAnalyzer(
    private val onTextDetected:(List<IngredientTextLine>)-> Unit
): ImageAnalysis.Analyzer{

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val isProcessing = AtomicBoolean(false)

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        imageProxy.close()
    }

    @OptIn(ExperimentalGetImage::class)
    fun processCaptureImage(imageProxy: ImageProxy){
        if(!isProcessing.compareAndSet(false,true)){
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if(mediaImage == null){
            imageProxy.close()
            isProcessing.set(false)
            return
        }
        val image = InputImage.fromMediaImage(mediaImage,imageProxy.imageInfo.rotationDegrees)
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val rotation = imageProxy.imageInfo.rotationDegrees

                val imageWidth = if(rotation ==90 || rotation == 270){
                    mediaImage.height
                } else {
                    mediaImage.width
                }
                val imageHeight = if(rotation ==90 || rotation == 270){
                    mediaImage.width
                } else {
                    mediaImage.height
                }
                val textLines = extractTextLines(visionText,imageWidth, imageHeight)
                if(textLines.isNotEmpty()){
                    onTextDetected(textLines)
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }.addOnCompleteListener {
                imageProxy.close()
                isProcessing.set(false)
            }
    }

    fun analyze(bitmap: Bitmap) {
        if (!isProcessing.compareAndSet(false, true)) {
            return
        }

        val image = InputImage.fromBitmap(bitmap, 0)

        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                val textLines = extractTextLines(
                    visionText,
                    bitmap.width,
                    bitmap.height,
                    restrictToScanArea = false
                )

                if (textLines.isNotEmpty()) {
                    onTextDetected(textLines)
                }
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace()
            }
            .addOnCompleteListener {
                isProcessing.set(false)
            }
    }

    private fun extractTextLines(
        visionText: Text,
        imageWidth: Int,
        imageHeight: Int,
        restrictToScanArea: Boolean = true
    ): List<IngredientTextLine> {

        val leftLimit = imageWidth * 0.10f
        val rightLimit = imageWidth * 0.90f
        val topLimit = imageHeight * 0.10f
        val bottomLimit = imageHeight * 0.90f

        return visionText.textBlocks.flatMap { block ->
            block.lines.mapNotNull { line ->

                val bounds = line.boundingBox
                    ?: return@mapNotNull null

                val insideScanArea =
                    bounds.left >= leftLimit &&
                            bounds.right <= rightLimit &&
                            bounds.top >= topLimit &&
                            bounds.bottom <= bottomLimit

                if (restrictToScanArea && !insideScanArea) {
                    return@mapNotNull null
                }

                IngredientTextLine(
                    text = line.text,
                    left = bounds.left,
                    top = bounds.top,
                    right = bounds.right,
                    bottom = bounds.bottom
                )
            }
        }
    }

    fun close(){
        textRecognizer.close()
    }

}