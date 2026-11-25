package com.akhilav.nutrilens.utils

import android.graphics.Bitmap
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.atomic.AtomicBoolean

class BarcodeAnalyzer(private val onBarcodeDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient()
    private val hasScanned = AtomicBoolean(false)

    @androidx.annotation.OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if (hasScanned.get()) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image

        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        val image = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstNotNullOfOrNull {
                    it.rawValue
                }

                if (barcode != null &&
                    hasScanned.compareAndSet(false, true)
                ) {
                    onBarcodeDetected(barcode)
                }
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    fun analyze(bitmap: Bitmap, onResult:(String?)-> Unit){
        val image = InputImage.fromBitmap(bitmap, 0)

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstNotNullOfOrNull {
                    it.rawValue
                }
                onResult(barcode)
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun reset(){
        hasScanned.set(false)
    }

    fun close() {
        hasScanned.set(false)
    }
}
