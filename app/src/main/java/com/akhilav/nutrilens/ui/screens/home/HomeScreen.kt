package com.akhilav.nutrilens.ui.screens.home

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalTime

val currentHour = LocalTime.now().hour

val greeting = when (currentHour) {
    in 5..11 -> "Good morning!"
    in 12..16 -> "Good afternoon!"
    in 17..20 -> "Good evening!"
    else -> "Good night!"
}
@Composable
fun HomeScreen(onScanClick:()-> Unit){
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Welcome section
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Let's make healthier food choices today.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Daily nutrition section
        Text(
            text = "Today's Nutrition",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NutritionCard(
                title = "Calories",
                value = "--",
                unit = "kcal",
                modifier = Modifier.weight(1f)
            )
            NutritionCard(
                title = "Protein",
                value = "--",
                unit = "g",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NutritionCard(
                title = "Carbs",
                value = "--",
                unit = "g",
                modifier = Modifier.weight(1f)
            )

            NutritionCard(
                title = "Fat",
                value = "--",
                unit = "g",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Food scanner section
        Text(
            text = "Scan Your Food",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onScanClick,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(18.dp)
        ) {
            Text("Start Food Scan")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent scans section
        Text(
            text = "Recent Food Scans",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(16.dp)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your recent food scans will appear here.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
@Composable
private fun NutritionCard(
    title: String, value:String, unit:String, modifier:Modifier = Modifier
){
    Column(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ){
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$value $unit",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}