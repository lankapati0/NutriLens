package com.akhilav.nutrilens.ui.screens.scanner

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.akhilav.nutriscan.ui.screens.scanner.BarcodeAnalyzer
import java.util.concurrent.Executors

@Composable
fun CameraPreview(modifier: Modifier = Modifier,
                  onBarcodeDetected: (String) -> Unit,
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnBarcodeDetected by rememberUpdatedState(onBarcodeDetected)
    val previewView = remember { PreviewView(context).apply {
        scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val barcodeAnalyzer = remember { BarcodeAnalyzer{ barcode ->
        currentOnBarcodeDetected(barcode)
        }
    }


    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                    it.setAnalyzer(cameraExecutor,barcodeAnalyzer)
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA,preview,imageAnalysis)
            }catch (e: Exception){
                e.printStackTrace()
            }
        },executor)

        onDispose {
            if(cameraProviderFuture.isDone){
                cameraProviderFuture.get().unbindAll()
            }
            barcodeAnalyzer.close()
            cameraExecutor.shutdown()
        }
    }
    AndroidView(factory = {previewView},modifier=modifier)
}