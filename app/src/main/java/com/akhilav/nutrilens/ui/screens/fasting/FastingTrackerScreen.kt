package com.akhilav.nutrilens.ui.screens.fasting

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.data.local.entity.FastingSessionEntity
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.theme.BrandSage
import com.akhilav.nutrilens.viewmodels.fasting.FastingTrackerViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FastingTrackerScreen(
    viewModel: FastingTrackerViewModel = hiltViewModel()
){
    val colorScheme = MaterialTheme.colorScheme

    val activeSession by viewModel.activeSession.collectAsStateWithLifecycle(initialValue = null)
    val allSessions by viewModel.allSessions.collectAsStateWithLifecycle(initialValue = emptyList())
    var selectedHistoryFilter by rememberSaveable {mutableStateOf("All")}

    val sevenDaysAgo = System.currentTimeMillis() - (7L *24*60*60*1000)
    val weeklySessions = allSessions.filter { session ->
        session.endTime != null && session.endTime >= sevenDaysAgo
    }

    val weeklyTotalSeconds: Long = weeklySessions.sumOf { session ->
        ((session.endTime ?: session.startTime) - session.startTime).coerceAtLeast(0L) / 1000L
    }

    val averangeFastSeconds:Long = if(weeklySessions.isNotEmpty()){
        weeklyTotalSeconds/weeklySessions.size
    } else {0L}

    val longestFastSeconds:Long = weeklySessions.maxOfOrNull { session ->
        ((session.endTime ?: session.startTime) - session.startTime).coerceAtLeast(0L)/1000L
    } ?: 0L

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
    val targetHours = activeSession?.targetHours ?: selectedTargetHours.toIntOrNull() ?: 16
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
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            ),
            border = BorderStroke(1.dp,BrandMint.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(6.dp)
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
                    color = BrandOceanTeal,    letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                )

                Text(
                    text = if(activeSession == null) "Ready to start?" else "You're fasting!",
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
                    },shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandOceanTeal,
                        unfocusedBorderColor = colorScheme.outlineVariant,
                        focusedLabelColor = BrandOceanTeal,
                        cursorColor = BrandOceanTeal
                    ),
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
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepTeal,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(24.dp,16.dp),
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
                    text = "$targetHours:${24-targetHours} Intermittent fasting",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorScheme.primary
                )
                Text(
                    text = "$targetHours hours fasting • ${24 - targetHours} hours eating window",
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
                value = allSessions.count { session ->
                    session.endTime != null &&
                            java.text.SimpleDateFormat(
                                "yyyyMMdd",
                                java.util.Locale.getDefault()
                            ).format(java.util.Date(session.startTime)) ==
                            java.text.SimpleDateFormat(
                                "yyyyMMdd",
                                java.util.Locale.getDefault()
                            ).format(java.util.Date())
                }.toString(),
                modifier = Modifier.weight(1f)
            )

            SummaryCard(
                title = "Fasting Hours",
                value = run {
                    val today = java.text.SimpleDateFormat(
                        "yyyyMMdd",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date())

                    val totalSeconds = allSessions
                        .filter { session ->
                            session.endTime != null &&
                                    java.text.SimpleDateFormat(
                                        "yyyyMMdd",
                                        java.util.Locale.getDefault()
                                    ).format(java.util.Date(session.startTime)) == today
                        }
                        .sumOf { session ->
                            (session.endTime!! - session.startTime) / 1000L
                        }

                    "${totalSeconds / 3600}h ${(totalSeconds % 3600) / 60}m"
                },
                modifier = Modifier.weight(1f)
            )
        }
        FastingHistorySection(allSessions,selectedHistoryFilter,{selectedHistoryFilter = it})

        WeeklyAnalyticsSection(weeklyTotalSeconds, averangeFastSeconds, longestFastSeconds)
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

@Composable
fun FastingHistorySection(sessions: List<FastingSessionEntity>,
                          selectedFilter:String, onFilterChange:(String)-> Unit){
    val colorScheme = MaterialTheme.colorScheme
    val completedSessions = sessions.filter { session ->
        session.endTime != null
    }

    val filteredSessions = completedSessions.filter { session ->
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = session.startTime

        when (selectedFilter) {
            "This Week" -> {
                val startOfWeek = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.DAY_OF_WEEK, firstDayOfWeek)
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }

                session.startTime >= startOfWeek.timeInMillis
            }

            "This Month" -> {
                val now = java.util.Calendar.getInstance()

                calendar.get(java.util.Calendar.YEAR) ==
                        now.get(java.util.Calendar.YEAR) &&
                        calendar.get(java.util.Calendar.MONTH) ==
                        now.get(java.util.Calendar.MONTH)
            }

            else -> true
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text(
            text = "Fasting History",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            listOf("All", "This Week", "This Month").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterChange(filter) },
                    label = { Text(filter) }
                )
                }
        }

        if(filteredSessions.isEmpty()){
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "◷",
                        fontSize = 32.sp,
                        color = BrandOceanTeal
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (selectedFilter) {
                            "This Week" -> "No fasts completed this week"
                            "This Month" -> "No fasts completed this month"
                            else -> "No completed fasts yet"
                        },
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = "Try another filter or complete a fasting session to see your history.",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                }
        } else {
            filteredSessions.groupBy { session ->
                java.text.SimpleDateFormat(
                    "dd MMM yyyy",
                    java.util.Locale.getDefault()
                ).format(java.util.Date(session.startTime))
            }
                .forEach { (date, dateSessions) ->
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = BrandOceanTeal
                    )
                    dateSessions.forEach { session ->
                val durationSeconds =
                    ((session.endTime!! - session.startTime) / 1000L)
                        .coerceAtLeast(0L)

                val durationHours = durationSeconds / 3600
                val durationMinutes = (durationSeconds % 3600) / 60

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = colorScheme.surface
                    ),
                    shape = RoundedCornerShape(18.dp)
                ){
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column {
                                Text(
                                    text = "Completed Fast",
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )

                                Text(
                                    text = if (durationSeconds >= session.targetHours * 3600L) {
                                        "Target achieved"
                                    } else {
                                        "Fast completed"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandOceanTeal
                                )
                            }

                            Text(
                                text = "${durationHours}h ${durationMinutes}m",
                                fontWeight = FontWeight.Bold,
                                color = BrandOceanTeal,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Text(
                            text = "Target: ${session.targetHours} hours",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                        val timeFormat = java.text.SimpleDateFormat(
                            "hh:mm a",
                            java.util.Locale.getDefault()
                        )

                        Text(
                            text = "Started: ${timeFormat.format(java.util.Date(session.startTime))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "Ended: ${timeFormat.format(java.util.Date(session.endTime!!))}",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )

                        val sessionDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                            .format(Date(session.startTime))

                        Text(
                            text = sessionDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }

                }
            }
        }
    }
}

@Composable
fun WeeklyAnalyticsSection(totalSeconds:Long, averageSeconds:Long, longestSeconds:Long){
    val colorScheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Weekly Insights",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = colorScheme.onBackground
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorScheme.surface
            )
        ){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ){
                WeeklyStatItem(
                    title = "Total Fasting Hours",
                    value = "${totalSeconds / 3600}h ${(totalSeconds % 3600) / 60}m"
                )

                HorizontalDivider()

                WeeklyStatItem(
                    title = "Average Fast",
                    value = "${averageSeconds / 3600}h ${(averageSeconds % 3600) / 60}m"
                )

                HorizontalDivider()

                WeeklyStatItem(
                    title = "Longest Fast",
                    value = "${longestSeconds / 3600}h ${(longestSeconds % 3600) / 60}m"
                )
            }
        }
    }
}

@Composable
fun WeeklyStatItem(title:String, value: String){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value,
            color = BrandOceanTeal,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}