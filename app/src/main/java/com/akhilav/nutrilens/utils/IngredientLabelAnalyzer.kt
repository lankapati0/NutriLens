package com.akhilav.nutrilens.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.tracing.AtomicBoolean
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class IngredientLabelAnalyzer(
    private val onTextDetected:(String)-> Unit
): ImageAnalysis.Analyzer{

    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val hasRecognized = AtomicBoolean(false)

    override fun analyze(image: ImageProxy) {

    }


}