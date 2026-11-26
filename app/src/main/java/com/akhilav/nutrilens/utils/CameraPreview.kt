package com.akhilav.nutrilens.utils

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.akhilav.nutrilens.utils.parser.IngredientLabelAnalyzer
import com.akhilav.nutrilens.utils.parser.IngredientTextLine
import java.util.concurrent.Executors
import kotlin.time.Duration.Companion.milliseconds

@SuppressLint("RememberReturnType")
@Composable
fun CameraPreview(modifier: Modifier = Modifier,
                  captureRequest:Int,
                  onBarcodeDetected: (String) -> Unit,
                  scanMode:String, onTextDetected:(List<IngredientTextLine>) -> Unit,
                  onFoodImageCaptured:(ImageProxy) -> Unit,
                  barcodeResetRequest: Int,isFlashOn: Boolean,
){
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val currentOnBarcodeDetected by rememberUpdatedState(onBarcodeDetected)
    val currentOnTextDetected by rememberUpdatedState(onTextDetected)
    val currentOnFoodImageCaptured by rememberUpdatedState(onFoodImageCaptured)
    val previewView = remember { PreviewView(context).apply {
        scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    val imageCapture = remember { ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build() }
    var camera by remember { mutableStateOf<androidx.camera.core.Camera?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    LaunchedEffect(isFlashOn,camera) {
        camera?.let { activeCamera ->
            if (activeCamera.cameraInfo.hasFlashUnit()) {
                activeCamera.cameraControl.enableTorch(isFlashOn)
            }
        }
    }
    DisposableEffect(cameraExecutor) {
        onDispose { cameraExecutor.shutdown() }
    }

    val barcodeAnalyzer = remember(scanMode){
        BarcodeAnalyzer {barcode ->
            currentOnBarcodeDetected(barcode)
        }
    }

    LaunchedEffect(barcodeResetRequest) {
        if (barcodeResetRequest > 0) {
            barcodeAnalyzer.reset()
        }
    }

    val ingredientAnalyzer = remember(scanMode){
        IngredientLabelAnalyzer { text ->
            currentOnTextDetected(text)
        }
    }

    val captureAnalyzer = remember {
        IngredientLabelAnalyzer { text ->
            currentOnTextDetected(text)
        }
    }

    LaunchedEffect(captureRequest,scanMode) {
        if ((scanMode == "ingredient" || scanMode == "freshFood") && captureRequest > 0) {
            kotlinx.coroutines.delay(1000.milliseconds)

            imageCapture.takePicture(
                cameraExecutor,
                object : ImageCapture.OnImageCapturedCallback() {
                    override fun onCaptureSuccess(image: ImageProxy) {

                        if(scanMode == "ingredient"){
                            captureAnalyzer.processCaptureImage(image)
                        } else if(scanMode == "freshFood") {
                            currentOnFoodImageCaptured(image)
                        }

                    }

                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                    }
                }
            )
        }
    }


    DisposableEffect(lifecycleOwner,scanMode) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build().also {
                    it.setAnalyzer(cameraExecutor,
                        when(scanMode){
                            "barcode" -> barcodeAnalyzer
                            "ingredient" -> ingredientAnalyzer
                            else -> ImageAnalysis.Analyzer {image -> image.close()}
                        })
                }

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis,
                    imageCapture
                )
            }catch (e: Exception){
                e.printStackTrace()
            }
        },executor)

        onDispose {
            if(cameraProviderFuture.isDone){
                cameraProviderFuture.get().unbindAll()
            }
            barcodeAnalyzer.close()
            ingredientAnalyzer.close()
        }
    }
    AndroidView(factory = {previewView},modifier=modifier)
}