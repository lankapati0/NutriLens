package com.akhilav.nutrilens.ui.screens.fasting

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.theme.BrandSage
import com.akhilav.nutrilens.viewmodels.fasting.FastingTrackerViewModel
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FastingTrackerScreen(
    viewModel: FastingTrackerViewModel = hiltViewModel()
){
    val colorScheme = MaterialTheme.colorScheme

    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle(initialValue = null)

    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(activeSession?.startTime) {
        if(activeSession != null){
            while(true){
                currentTimeMillis = System.currentTimeMillis()
                delay(1000L.milliseconds)
            }
        }
    }

    val elapsedSeconds:Long = activeSession?.let {
        ((currentTimeMillis - it.startTime)/1000L).coerceAtLeast(0L)
    } ?: 0L
    var selectedTargetHours by rememberSaveable { mutableStateOf("16") }
    val targetHours = activeSession?.targetHours ?: 16
    val targetSeconds = targetHours * 3600L
    val fastingProgress = (elapsedSeconds.toFloat()/targetSeconds.toFloat()).coerceIn(0f, 1f)

    val remainingSeconds:Long = (targetSeconds - elapsedSeconds).coerceAtLeast(0L)

    Column(modifier = Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header
        Column {
            Text(
                text = "Fasting Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onBackground
            )

            Text(
                text = "Build healthier eating habits",
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant
            )
        }
        // Fasting status card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                Text(
                    text = "YOUR FASTING JOURNEY",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Ready to start?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface
                )
                OutlinedTextField(
                    value = selectedTargetHours,
                    onValueChange = { value ->
                        if (value.length <= 2 && value.all { it.isDigit() }) {
                            selectedTargetHours = value
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Fasting duration")
                    },
                    suffix = {
                        Text("hours")
                    },
                    singleLine = true,
                    enabled = activeSession == null,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                FastingProgressRing(
                    progress = fastingProgress,
                    elapsedTime = formatFastingTime(elapsedSeconds),
                    targetTime = "$targetHours Hours",
                    remainingTime = "${formatFastingTime(remainingSeconds)} remaining"
                )

                Button(
                    onClick = {
                        if (activeSession == null) {
                            val hours = selectedTargetHours.toIntOrNull()

                            if (hours != null && hours in 1..23) {
                                viewModel.startFast(hours)
                            }
                        } else {
                            viewModel.endFast(activeSession!!)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = activeSession != null ||
                            (selectedTargetHours.toIntOrNull() in 1..23)
                ) {
                    Text(
                        if (activeSession == null) {
                            "Start Fast"
                        } else {
                            "End Fast"
                        }
                    )
                }
            }
        }

        // Fasting plan
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            )
        ){
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                Text(
                    text = "Fasting Plan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "16:8 Intermittent Fasting",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.primary
                )
                Text(
                    text = "16 hours fasting • 8 hours eating window",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
        // Today's summary
        Text(
            text = "Today's Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ){
            SummaryCard(
                title = "Total Fasts",
                value = "0",
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Fasting Hours",
                value = "0h",
                modifier = Modifier.weight(1f)
            )
        }

    }



}

@Composable
private fun SummaryCard(title:String, value:String, modifier: Modifier = Modifier){
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun FastingProgressRing(
    progress: Float,
    elapsedTime: String,
    targetTime: String,
    remainingTime: String
) {
    val colorScheme = MaterialTheme.colorScheme

    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        label = "fasting progress"
    )

    val remainingDisplay = remainingTime
        .substringBefore(" remaining")
        .substringBeforeLast(":")

    Box(
        modifier = Modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)

            val outerRadius = size.minDimension * 0.43f
            val innerRadius = size.minDimension * 0.34f

            val outerStroke = 11.dp.toPx()
            val innerStroke = 9.dp.toPx()

            val primaryColor = colorScheme.primary
            val secondaryColor = colorScheme.secondary
            val trackColor = colorScheme.surfaceVariant

            // Outer background track
            drawArc(
                color = trackColor.copy(alpha = 0.75f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(
                    center.x - outerRadius,
                    center.y - outerRadius
                ),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(
                    width = outerStroke,
                    cap = StrokeCap.Round
                )
            )

            // Inner background track
            drawArc(
                color = trackColor.copy(alpha = 0.55f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(
                    center.x - innerRadius,
                    center.y - innerRadius
                ),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(
                    width = innerStroke,
                    cap = StrokeCap.Round
                )
            )

            // Outer soft glow
            drawArc(
                color = BrandOceanTeal.copy(alpha = 0.22f),
                startAngle = -90f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = Offset(
                    center.x - outerRadius,
                    center.y - outerRadius
                ),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(width = 22.dp.toPx())
            )

            // Outer progress arc
            val outerSweep = 270f * animatedProgress

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        BrandDeepTeal,
                        BrandOceanTeal,
                        BrandMint,
                        BrandDeepTeal
                    )
                ),
                startAngle = -90f,
                sweepAngle = outerSweep,
                useCenter = false,
                topLeft = Offset(
                    center.x - outerRadius,
                    center.y - outerRadius
                ),
                size = Size(outerRadius * 2, outerRadius * 2),
                style = Stroke(
                    width = outerStroke,
                    cap = StrokeCap.Round
                )
            )

            // Inner progress arc, starting at a different position
            drawArc(
                color = BrandMint.copy(alpha = 0.20f),
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = Offset(
                    center.x - innerRadius,
                    center.y - innerRadius
                ),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(width = 18.dp.toPx())
            )

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        BrandSage,
                        BrandMint,
                        BrandOceanTeal,
                        BrandSage
                    )
                ),
                startAngle = 135f,
                sweepAngle = 270f * animatedProgress,
                useCenter = false,
                topLeft = Offset(
                    center.x - innerRadius,
                    center.y - innerRadius
                ),
                size = Size(innerRadius * 2, innerRadius * 2),
                style = Stroke(
                    width = innerStroke,
                    cap = StrokeCap.Round
                )
            )

            // Small endpoint indicator on the outer arc
            val endAngle = Math.toRadians(
                (-90f + outerSweep).toDouble()
            )

            val dotX = center.x + outerRadius * cos(endAngle).toFloat()
            val dotY = center.y + outerRadius * sin(endAngle).toFloat()

            drawCircle(
                color = BrandMint.copy(alpha = 0.35f),
                radius = 11.dp.toPx(),
                center = Offset(dotX, dotY)
            )

            drawCircle(
                color = BrandMint,
                radius = 5.dp.toPx(),
                center = Offset(dotX, dotY)
            )

            drawCircle(
                color = colorScheme.surface,
                radius = 2.dp.toPx(),
                center = Offset(dotX, dotY)
            )
        }

        // Centre countdown
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        color = BrandDeepTeal.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "◷",
                    color = BrandOceanTeal,
                    fontSize = 29.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Ends in",
                color = colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = remainingDisplay,
                    color = colorScheme.onSurface,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = "HRS",
                    color = colorScheme.onSurfaceVariant,
                    fontSize = 10.sp,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
        }
    }
}


private fun formatFastingTime(seconds:Long): String{
    val hours = seconds / 3600
    val minutes = (seconds%3600) / 60
    val remainingSeconds = seconds % 60

    return "%02d:%02d:%02d".format(hours,minutes,remainingSeconds)
}
