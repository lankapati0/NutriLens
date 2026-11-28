package com.akhilav.nutrilens.ui.screens.weightTrack

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandMint
import com.akhilav.nutrilens.viewmodels.weightEntry.WeightViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WeightDetailScreen(

    viewModel: WeightViewModel = hiltViewModel()
){
    val weightHistory by viewModel.weightHistory.collectAsStateWithLifecycle()
    val latestWeight by viewModel.latestWeight.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 20.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {

        }
    }

}

@Composable
private fun WeightDetailOverviewCard(currentWeight: Double?, previousWeight:Double?,
                                     recordedAt:Long?){
    val weightChange = if (
        currentWeight != null && previousWeight != null
    ) {
        currentWeight - previousWeight
    } else null

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = BrandDeepTeal
        )
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp)
        ){
            Text(
                text = "CURRENT BODY WEIGHT",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.75f),
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(18.dp))
            Row(
                verticalAlignment = Alignment.Bottom
            ){
                Text(
                    text = currentWeight?.let {
                        String.format(Locale.getDefault(), "%.1f", it)
                    } ?: "--",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "kg",
                    style = MaterialTheme.typography.titleLarge,
                    color = BrandMint
                )
            }
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ){
                Icon(
                    imageVector = when {
                        weightChange == null -> Icons.Default.Info
                        weightChange < 0 -> Icons.Default.TrendingDown
                        weightChange > 0 -> Icons.Default.TrendingUp
                        else -> Icons.Default.TrendingFlat
                    },
                    contentDescription = null,
                    tint = BrandMint
                )
                Text(
                    text = when {
                        weightChange == null -> "No previous measurement"
                        weightChange < 0 ->
                            "${String.format(Locale.getDefault(), "%.1f", kotlin.math.abs(weightChange))} kg down since last record"
                        weightChange > 0 ->
                            "${String.format(Locale.getDefault(), "%.1f", weightChange)} kg up since last record"
                        else -> "No change since last record"
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandMint
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(
                color = Color.White.copy(alpha = 0.2f)
            )

            Spacer(modifier = Modifier.height(12.dp))
            }
            Text(
                text = "Latest measurement",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f)
            )
        Text(
            text = recordedAt?.let {
                SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date(it))
            } ?: "No measurement yet",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        }
    }
