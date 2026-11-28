package com.akhilav.nutrilens.ui.screens.sleep_track

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.theme.BrandCoral
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandGold
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.viewmodels.sleep_tracker.SleepViewModel

@Composable
fun SleepDetailScreen(
    sleepViewModel: SleepViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val colorScheme = MaterialTheme.colorScheme
    val sleepRecords by sleepViewModel.sleepRecords.collectAsStateWithLifecycle()
    val latestSleep = sleepRecords.firstOrNull()
    val totalStageMinutes = latestSleep?.let {
        it.awakeMinutes + it.remMinutes + it.lightMinutes + it.deepMinutes
    } ?: 456
    val sleepShortfall = (latestSleep?.sleepGoalMinutes ?: 480) -
            (latestSleep?.totalSleepMinutes ?: 432)

    val sleepGoalMessage = when {
        sleepShortfall > 0 ->
            "Your total sleep was ${formatSleepDuration(sleepShortfall)} below your goal."

        sleepShortfall < 0 ->
            "You exceeded your sleep goal by ${formatSleepDuration(-sleepShortfall)}."

        else -> "You achieved your sleep goal!"
    }

    fun stagePercentage(minutes: Int): Float {
        return if (totalStageMinutes > 0) {
            minutes.toFloat() / totalStageMinutes
        } else {
            0f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        Text(
            text = latestSleep?.let {
                formatSleepDate(it.date)
            } ?: "No Sleep Record",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurfaceVariant
        )

        if (latestSleep == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Nightlight,
                        contentDescription = null,
                        tint = BrandOceanTeal,
                        modifier = Modifier.size(42.dp)
                    )

                    Text(
                        text = "No sleep records yet",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = "Add your first sleep record to see your sleep score, stages and insights here.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if(latestSleep != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Nightlight,
                            contentDescription = null,
                            tint = BrandOceanTeal
                        )

                        Text(
                            text = "Sleep Insight",
                            style = MaterialTheme.typography.labelLarge,
                            color = BrandOceanTeal
                        )
                    }

                    Text(
                        text = "Short night, strong deep sleep",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Divider(color = colorScheme.surfaceVariant)
                    val currentScore = latestSleep?.sleepScore ?: 82
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Sleep summary",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = latestSleep?.let {
                                    formatSleepDuration(it.totalSleepMinutes)
                                } ?: "7h 12m",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                            Text(
                                text = when {
                                    currentScore >= 85 -> "Excellent"
                                    currentScore >= 70 -> "Good"
                                    currentScore >= 50 -> "Fair"
                                    else -> "Needs Improvement"
                                },
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = BrandOceanTeal
                            )
                        }
                        SleepDetailScoreRing(score = currentScore)

                    }

                    HorizontalDivider(color = colorScheme.surfaceVariant)
                    SleepStageDetailRow(
                        title = "Awake",
                        duration = "${(stagePercentage(latestSleep?.awakeMinutes ?: 24) * 100).toInt()}%",
                        color = BrandCoral,
                        progress = stagePercentage(latestSleep?.awakeMinutes ?: 24)
                    )

                    SleepStageDetailRow(
                        title = "REM",
                        duration = formatSleepDuration(latestSleep?.remMinutes ?: 94),
                        color = BrandMint,
                        progress = stagePercentage(latestSleep?.remMinutes ?: 94)
                    )

                    SleepStageDetailRow(
                        title = "Light",
                        duration = formatSleepDuration(latestSleep?.lightMinutes ?: 270),
                        color = BrandOceanTeal,
                        progress = stagePercentage(latestSleep?.lightMinutes ?: 270)
                    )

                    SleepStageDetailRow(
                        title = "Deep",
                        duration = formatSleepDuration(latestSleep?.deepMinutes ?: 68),
                        color = BrandDeepTeal,
                        progress = stagePercentage(latestSleep?.deepMinutes ?: 68)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = latestSleep?.bedtime ?: "11:48 PM",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = latestSleep?.wakeUpTime ?: "7:24 AM",
                            style = MaterialTheme.typography.labelSmall,
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if(latestSleep != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Sleep Consistency",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = "Your sleep schedule this week",
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )

                    val sleepHours = if (sleepRecords.isEmpty()) {
                        listOf(6.5f, 7.2f, 7.8f, 6.9f, 8f, 7.2f, 7.5f)
                    } else {
                        sleepRecords.take(7).reversed()
                            .map { it.totalSleepMinutes / 60f }
                    }

                    val days = if (sleepRecords.isEmpty()) {
                        listOf("M", "T", "W", "T", "F", "S", "S")
                    } else {
                        sleepRecords
                            .take(7)
                            .reversed()
                            .map { it.date.takeLast(2) }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        sleepHours.forEachIndexed { index, hours ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height((hours * 12).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(
                                            if (index == 6) BrandOceanTeal
                                            else colorScheme.surfaceVariant
                                        )
                                )

                                Text(
                                    text = days[index],
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Text(
                        text = "Average sleep: ${
                            formatSleepDuration((sleepHours.average() * 60).toInt())
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandOceanTeal
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Sleep Timeline",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            latestSleep?.bedtime ?: "11:48 PM",
                            color = colorScheme.onSurfaceVariant
                        )
                        Text(
                            latestSleep?.wakeUpTime ?: "7:24 AM",
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(50))
                            .background(colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(
                                    if ((latestSleep?.sleepGoalMinutes ?: 480) > 0) {
                                        ((latestSleep?.totalSleepMinutes ?: 432).toFloat() /
                                                (latestSleep?.sleepGoalMinutes ?: 480)).coerceIn(
                                            0f,
                                            1f
                                        )
                                    } else {
                                        0f
                                    }
                                )
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(50))
                                .background(BrandOceanTeal)
                        )
                    }

                    val timelineProgress =
                        if ((latestSleep?.sleepGoalMinutes ?: 480) > 0) {
                            ((latestSleep?.totalSleepMinutes ?: 432).toFloat() /
                                    (latestSleep?.sleepGoalMinutes ?: 480))
                                .coerceIn(0f, 1f)
                        } else {
                            0f
                        }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Asleep for ${
                                formatSleepDuration(latestSleep?.totalSleepMinutes ?: 432)
                            }",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "${(timelineProgress * 100).toInt()}% of goal",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = BrandOceanTeal
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Sleep Quality Breakdown",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SleepStageDonutChart(
                            awake = latestSleep?.awakeMinutes ?: 24,
                            rem = latestSleep?.remMinutes ?: 94,
                            light = latestSleep?.lightMinutes ?: 270,
                            deep = latestSleep?.deepMinutes ?: 68
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            listOf(
                                Triple("Awake", latestSleep?.awakeMinutes ?: 24, BrandCoral),
                                Triple("REM", latestSleep?.remMinutes ?: 94, BrandMint),
                                Triple("Light", latestSleep?.lightMinutes ?: 270, BrandOceanTeal),
                                Triple("Deep", latestSleep?.deepMinutes ?: 68, BrandDeepTeal)
                            ).forEach { (title, minutes, color) ->

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(9.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )

                                    Column {
                                        Text(
                                            text = title,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = colorScheme.onSurface
                                        )

                                        Text(
                                            text = "${formatSleepDuration(minutes)} • ${
                                                (stagePercentage(
                                                    minutes
                                                ) * 100).toInt()
                                            }%",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Sleep Performance",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.onSurface
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Sleep Goal",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = formatSleepDuration(latestSleep?.sleepGoalMinutes ?: 480),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Achieved",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${
                                    if ((latestSleep?.sleepGoalMinutes ?: 480) > 0) {
                                        ((latestSleep?.totalSleepMinutes ?: 432) * 100) /
                                                (latestSleep?.sleepGoalMinutes ?: 480)
                                    } else {
                                        0
                                    }
                                }%",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = BrandOceanTeal
                            )
                        }
                    }
                    LinearProgressIndicator(
                        progress = if ((latestSleep?.sleepGoalMinutes ?: 480) > 0) {
                            ((latestSleep?.totalSleepMinutes ?: 432).toFloat() /
                                    (latestSleep?.sleepGoalMinutes ?: 480)).coerceIn(0f, 1f)
                        } else {
                            0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(RoundedCornerShape(50)),
                        color = BrandOceanTeal,
                        trackColor = colorScheme.surfaceVariant
                    )
                    Text(
                        text = sleepGoalMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = BrandGold
                        )

                        Text(
                            text = "Sleep Insights",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )
                    }

                    Text(
                        text = when {
                            latestSleep == null -> "Strong deep sleep!"
                            latestSleep.deepMinutes >= 60 -> "Strong deep sleep!"
                            latestSleep.deepMinutes >= 30 -> "Moderate deep sleep"
                            else -> "Your deep sleep needs attention"
                        },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandOceanTeal
                    )

                    Text(
                        text = "Your deep sleep lasted ${
                            formatSleepDuration(latestSleep?.deepMinutes ?: 68)
                        }, which supports physical recovery. $sleepGoalMessage",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = when {
                            sleepShortfall > 0 ->
                                "Try going to bed ${formatSleepDuration(sleepShortfall)} earlier tonight."

                            sleepShortfall < 0 ->
                                "Great job! Keep maintaining your regular sleep schedule."

                            else ->
                                "You achieved your sleep goal. Keep up this healthy routine!"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = colorScheme.onSurface
                    )
                }
            }


        }


    }
}

@Composable
private fun SleepStageDetailRow(title:String, duration:String, color: Color,progress:Float){
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = title,
            modifier = Modifier.width(48.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        Text(
            text = duration,
            modifier = Modifier.width(48.dp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
@Composable
private fun SleepDetailScoreRing(score:Int){
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ){
        CircularProgressIndicator(
            progress = { score.coerceIn(0, 100) / 100f },
            modifier = Modifier.fillMaxSize(),
            color = BrandOceanTeal,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 8.dp
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$score",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "SCORE",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
private fun SleepStageDonutChart(
    awake: Int,
    rem: Int,
    light: Int,
    deep: Int
) {
    val stages = listOf(
        awake to BrandCoral,
        rem to BrandMint,
        light to BrandOceanTeal,
        deep to BrandDeepTeal
    )

    val total = stages.sumOf { it.first }.coerceAtLeast(1)

    Box(
        modifier = Modifier.size(190.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val strokeWidth = 22.dp.toPx()
            val diameter = size.minDimension - strokeWidth

            var startAngle = -90f

            stages.forEach { (minutes, color) ->
                val sweepAngle = (minutes.toFloat() / total) * 360f

                drawArc(
                    color = color,
                    startAngle = startAngle,
                    sweepAngle = (sweepAngle - 3f).coerceAtLeast(0f),
                    useCenter = false,
                    topLeft = Offset(
                        (size.width - diameter) / 2,
                        (size.height - diameter) / 2
                    ),
                    size = Size(diameter, diameter),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round
                    )
                )

                startAngle += sweepAngle
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatSleepDuration(
                    rem + light + deep
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "Total Sleep",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}