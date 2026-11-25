package com.akhilav.nutrilens.ui.screens.scanner

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.remote.creation.dsl.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.screens.nutrition.ProductNutritionScreen
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.utils.BarcodeAnalyzer
import com.akhilav.nutrilens.utils.CameraPreview
import com.akhilav.nutrilens.utils.parser.FoodImageAnalyzer
import com.akhilav.nutrilens.utils.parser.FoodRecognitionResult
import com.akhilav.nutrilens.utils.parser.IngredientLabelAnalyzer
import com.akhilav.nutrilens.utils.parser.IngredientTextLine
import com.akhilav.nutrilens.utils.parser.IngredientLabelData
import com.akhilav.nutrilens.utils.parser.IngredientLabelParser
import com.akhilav.nutrilens.utils.parser.NutritionLabelData
import com.akhilav.nutrilens.utils.parser.NutritionLabelParser
import com.akhilav.nutrilens.viewmodels.ScannerViewModel
import com.akhilav.nutrilens.viewmodels.favorites.FavoritesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var scannerBarcode by remember { mutableStateOf<String?>(null) }
    val favoritesViewModel: FavoritesViewModel = hiltViewModel()

    var scanMode by remember {mutableStateOf("idle")}
    var captureRequest by remember { mutableIntStateOf(0) }
    var barcodeResetRequest by remember { mutableIntStateOf(0) }
    var recognizedText by remember { mutableStateOf<List<IngredientTextLine>?>(null) }
    var foodResults by remember {mutableStateOf<List<FoodRecognitionResult>>(emptyList())}
    var showScanModes by remember { mutableStateOf(false) }
    val foodImageAnalyzer = remember {
        FoodImageAnalyzer {labels ->
            foodResults = labels.map { labels ->
                FoodRecognitionResult(labels.text,labels.confidence)
            }
        }
    }

    DisposableEffect(foodImageAnalyzer) {
        onDispose { foodImageAnalyzer.close() }
    }

    var nutrientRows by remember {mutableStateOf<List<NutritionLabelData>>(emptyList())}
    var isSingleColumnNutrition by remember { mutableStateOf(false) }
    var ingredientData by remember {mutableStateOf<IngredientLabelData?>(null)}
    val product by viewModel.product.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    val galleryBarcodeAnalyzer = remember {
        BarcodeAnalyzer { barcode ->
            if (barcode != null) {
                scannerBarcode = barcode
                viewModel.loadProduct(barcode)
            }
        }
    }

    val galleryIngredientAnalyzer = remember {
        IngredientLabelAnalyzer { text ->
            recognizedText = text
            ingredientData = IngredientLabelParser.parse(text)

            isSingleColumnNutrition = text.any { line ->
                val value = line.text.uppercase().replace(" ", "")
                value.contains("NUTRITIONFACTS100G") ||
                        value.contains("PER100G")
            }

            nutrientRows = NutritionLabelParser.parse(text)
        }
    }

    val galleryScope = rememberCoroutineScope()
    val galleryPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            galleryScope.launch {
                val bitmap = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use {
                        BitmapFactory.decodeStream(it)
                    }
                }

                if (bitmap != null) {
                    when (scanMode) {
                        "barcode" -> {
                            scannerBarcode = null
                            galleryBarcodeAnalyzer.analyze(bitmap) { barcode ->
                                if (barcode != null) {
                                    scannerBarcode = barcode
                                    viewModel.loadProduct(barcode)
                                }
                            }
                        }

                        "ingredient" -> {
                            nutrientRows = emptyList()
                            ingredientData = null
                            galleryIngredientAnalyzer.analyze(bitmap)
                        }

                        "freshFood" -> {
                            foodResults = emptyList()
                            foodImageAnalyzer.analyze(bitmap)
                        }
                    }
                }
            }
        }
    }

    DisposableEffect(galleryBarcodeAnalyzer,galleryIngredientAnalyzer) {
        onDispose {
            galleryBarcodeAnalyzer.close()
            galleryIngredientAnalyzer.close()
        }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val infiniteTransition = rememberInfiniteTransition(
        label = "loading"
    )

    val loadingRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (isLoading) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "loadingRotation"
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    if (product != null) {
        ProductNutritionScreen(
            product = product!!,
            onSaveConsumption = { quantity,mealType ->
                viewModel.saveConsumption(quantityGrams = quantity,mealType= mealType)
            },
            onToggleFavorite = { barcode, isFavorite ->
                favoritesViewModel.updateFavorite(barcode, isFavorite)
            }
        )
    } else if (hasCameraPermission) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {

            // Camera preview
            CameraPreview(
                barcodeResetRequest = barcodeResetRequest,
                onBarcodeDetected = { barcode ->
                    scannerBarcode = barcode
                    viewModel.loadProduct(barcode)
                },
                onFoodImageCaptured = {image ->
                    foodImageAnalyzer.analyze(image)
                },
                captureRequest = captureRequest,
                onTextDetected = { text ->
                    recognizedText = text
                    ingredientData = IngredientLabelParser.parse(text)
                    isSingleColumnNutrition = text.any{ line ->
                        val value = line.text.uppercase().replace(" ","")
                        value.contains("NUTRITIONFACTS100G") ||
                                value.contains("PER100G")
                    }
                    nutrientRows = NutritionLabelParser.parse(text)
                },
                scanMode = scanMode,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NutriScan",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = when (scanMode) {
                        "barcode" -> "Align the barcode inside the frame"
                        "ingredient" -> "Position the nutrition label inside the frame"
                        "freshFood" -> "Place your food inside the frame"
                        else -> "Choose a scan mode to get started"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }

            if(scanMode != "idle"){
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(width = 280.dp, height = 300.dp)
                ) {
                    val cornerColor = Color.White.copy(alpha = 0.9f)

                    // Top-left
                    Box(
                        Modifier
                            .align(Alignment.TopStart)
                            .size(36.dp, 4.dp)
                            .background(cornerColor, CircleShape)
                    )
                    Box(
                        Modifier
                            .align(Alignment.TopStart)
                            .size(4.dp, 36.dp)
                            .background(cornerColor, CircleShape)
                    )
                    // Top-right
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(36.dp, 4.dp)
                            .background(cornerColor, CircleShape)
                    )
                    Box(
                        Modifier
                            .align(Alignment.TopEnd)
                            .size(4.dp, 36.dp)
                            .background(cornerColor, CircleShape)
                    )

                    // Bottom-left
                    Box(
                        Modifier
                            .align(Alignment.BottomStart)
                            .size(36.dp, 4.dp)
                            .background(cornerColor, CircleShape)
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomStart)
                            .size(4.dp, 36.dp)
                            .background(cornerColor, CircleShape)
                    )

                    // Bottom-right
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp, 4.dp)
                            .background(cornerColor, CircleShape)
                    )
                    Box(
                        Modifier
                            .align(Alignment.BottomEnd)
                            .size(4.dp, 36.dp)
                            .background(cornerColor, CircleShape)
                    )
                }
            }


            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.65f)
                            )
                        )
                    )
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            listOf(
                                "freshFood" to "Scan Food",
                                "barcode" to "Barcode",
                                "ingredient" to "Food Label"
                            ).forEach { (mode, label) ->
                                Surface(
                                    onClick = {
                                        scanMode = mode
                                        scannerBarcode = null
                                        recognizedText = null
                                        foodResults = emptyList()
                                        nutrientRows = emptyList()
                                        ingredientData = null

                                        if (mode == "barcode") {
                                            barcodeResetRequest++
                                        }
                                    },
                                    shape = RoundedCornerShape(20.dp),
                                    color = if (scanMode == mode) {
                                        Color.White.copy(alpha = 0.18f)
                                    } else {
                                        Color.Transparent
                                    },
                                    border = if (scanMode == mode) {
                                        BorderStroke(1.dp, BrandOceanTeal)
                                    } else {
                                        null
                                    }
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 7.dp
                                        ),
                                        color = if (scanMode == mode) {
                                            Color.White
                                        } else {
                                            Color.White.copy(alpha = 0.75f)
                                        },
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        LargeFloatingActionButton(
                            onClick = {
                                when (scanMode) {
                                    "freshFood", "ingredient" -> {
                                        foodResults = emptyList()
                                        nutrientRows = emptyList()
                                        ingredientData = null
                                        captureRequest++
                                    }

                                    "barcode" -> {
                                        barcodeResetRequest++
                                    }
                                }
                            },
                            containerColor = Color.White,
                            contentColor = Color(0xFF171D20),
                            shape = CircleShape
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(18.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                LargeFloatingActionButton(
                                    onClick = {
                                        when (scanMode) {
                                            "freshFood", "ingredient" -> {
                                                foodResults = emptyList()
                                                nutrientRows = emptyList()
                                                ingredientData = null
                                                captureRequest++
                                            }

                                            "barcode" -> {
                                                barcodeResetRequest++
                                            }
                                        }
                                    },
                                    containerColor = Color.White,
                                    contentColor = Color(0xFF171D20),
                                    shape = CircleShape
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(64.dp)
                                            .border(
                                                width = 2.dp,
                                                color = Color(0xFF171D20).copy(alpha = 0.15f),
                                                shape = CircleShape
                                            )
                                            .padding(5.dp)
                                            .background(Color.White, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = "Capture food",
                                            modifier = Modifier.size(30.dp)
                                        )
                                    }
                                }
                                Surface(
                                    onClick = {
                                        if (scanMode != "idle") {
                                            galleryPickerLauncher.launch("image/*")
                                        }
                                    },
                                    modifier = Modifier.size(56.dp),
                                    shape = CircleShape,
                                    color = Color.Black,
                                    border = BorderStroke(
                                        1.dp,
                                        Color.White.copy(alpha = 0.35f)
                                    )
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = "Choose image from gallery",
                                            tint = Color.White,
                                            modifier = Modifier.size(26.dp)
                                        )
                                    }
                                }

                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = when (scanMode) {
                                "barcode" -> "Point camera at barcode"
                                "ingredient" -> "Capture nutrition label"
                                "freshFood" -> "Capture food"
                                else -> "Select a scan mode"
                            },
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            if(scanMode == "freshFood" && foodResults.isNotEmpty()){
                GlassCard(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.9f)
                        .padding(horizontal = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Food Recognition",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        HorizontalDivider(
                            color = BrandOceanTeal.copy(alpha = 0.25f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "We identified these items in your image",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        foodResults.take(5).forEach { result ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                BrandOceanTeal.copy(alpha = 0.12f),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Restaurant,
                                            contentDescription = null,
                                            tint = BrandOceanTeal,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }

                                    Text(
                                        text = result.foodName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Text(
                                    text = "${(result.confidence * 100).toInt()}%",
                                    color = BrandOceanTeal,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }
                    }
                }
            }
            // Barcode label
            scannerBarcode?.let { barcode ->
                GlassCard(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Barcode: $barcode",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (nutrientRows.isNotEmpty() || ingredientData != null) {
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(
                            Alignment.Center
                        )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Label Analysis",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(280.dp)
                                .verticalScroll(rememberScrollState())
                        ) {

                            // Nutrition section
                            if (nutrientRows.isNotEmpty()) {

                                Text(
                                    text = "Nutrition Information",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Nutrient",
                                        modifier = Modifier.weight(1.4f),
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                    if (!isSingleColumnNutrition) {

                                        Text(
                                            text = "Serving",
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    Text(
                                        text = "100g",
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.labelMedium
                                    )

                                    if(!isSingleColumnNutrition){
                                        Text(
                                            text = "RDA",
                                            modifier = Modifier.weight(0.8f),
                                            style = MaterialTheme.typography.labelMedium
                                        )

                                    }
                                }

                                HorizontalDivider()

                                nutrientRows.forEach { row ->

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = row.nutrient,
                                            modifier = Modifier.weight(1.4f),
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        if (!isSingleColumnNutrition) {
                                            Text(
                                                text = row.perServing ?: "-",
                                                modifier = Modifier.weight(1f),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }

                                        Text(
                                            text = row.per100g ?: "-",
                                            modifier = Modifier.weight(1f),
                                            style = MaterialTheme.typography.bodySmall
                                        )

                                        if (!isSingleColumnNutrition) {
                                            Text(
                                                text = row.rda ?: "-",
                                                modifier = Modifier.weight(0.8f),
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    HorizontalDivider()
                                }
                            }

                            // Ingredients section
                            ingredientData?.let { data ->

                                if (nutrientRows.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    HorizontalDivider()
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                Text(
                                    text = "Ingredients",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = data.ingredientsText.ifBlank {
                                        "Not detected"
                                    },
                                    color = MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                data.containsStatement?.let {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                                data.mayContainStatement?.let {
                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = it,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = {
                                nutrientRows = emptyList()
                                ingredientData = null
                                recognizedText = null
                                isSingleColumnNutrition = false
                                captureRequest++
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Scan Again")
                        }
                    }
                }
            }

            // Loading indicator
            if (isLoading) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 100.dp)
                        .fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    shadowElevation = 12.dp,
                    border = BorderStroke(
                        1.dp,
                        BrandOceanTeal.copy(alpha = 0.35f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .background(
                                        BrandOceanTeal.copy(alpha = 0.12f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = BrandOceanTeal,
                                    modifier = Modifier
                                        .size(26.dp)
                                        .graphicsLayer {
                                            rotationZ = loadingRotation
                                        }
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Analyzing product",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(3.dp))

                                Text(
                                    text = "Checking barcode and nutrition data",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(CircleShape),
                            color = BrandOceanTeal,
                            trackColor = BrandOceanTeal.copy(alpha = 0.12f)
                        )

                        Text(
                            text = "Please wait a moment...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }

            // Error message
            error?.let {
                GlassCard(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        modifier = Modifier.padding(18.dp),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

    } else {
        // Camera permission screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                BrandOceanTeal.copy(alpha = 0.12f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "📷",
                            fontSize = 32.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Camera permission required",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Allow camera access to scan food product barcodes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    Button(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandDeepTeal
                        )
                    ) {
                        Text("Grant Camera Permission")
                    }
                }
            }
        }
    }
}
