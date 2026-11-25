package com.akhilav.nutrilens.utils.parser

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabel
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class FoodImageAnalyzer(
    private val onFoodDetected: (List<ImageLabel>) -> Unit
){


    private val labeler = ImageLabeling.getClient(
        ImageLabelerOptions.DEFAULT_OPTIONS
    )

    fun analyze(bitmap: Bitmap){
        val image = InputImage.fromBitmap(bitmap,0)
        labeler.process(image)
            .addOnSuccessListener{ labels ->
                val confidenceLabels = labels.filter { it.confidence >= 0.60f }
                onFoodDetected(labels)
            }.addOnFailureListener {
                exception -> exception.printStackTrace()
            }
    }

    fun analyze(imageProxy: ImageProxy){
        val mediaImage = imageProxy.image
        if(mediaImage == null){
            imageProxy.close()
            return
        }
        val image = InputImage.fromMediaImage(mediaImage,imageProxy.imageInfo.rotationDegrees)

        labeler.process(image)
            .addOnSuccessListener { labels ->
                val confidenceLabels = labels.filter { it.confidence >= 0.60f }
                onFoodDetected(confidenceLabels)
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }.addOnCompleteListener { imageProxy.close() }
    }

    fun close(){
        labeler.close()
    }
}