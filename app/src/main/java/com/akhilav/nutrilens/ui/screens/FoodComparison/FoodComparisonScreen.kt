package com.akhilav.nutrilens.ui.screens.FoodComparison

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.viewmodels.FoodComparisonViewModel


@Composable
fun FoodComparisonScreen(
    viewModel: FoodComparisonViewModel = hiltViewModel()
) {
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedProduct1 by viewModel.selectedProduct1.collectAsStateWithLifecycle()
    val selectedProduct2 by viewModel.selectedProduct2.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    var activeSelection by rememberSaveable { mutableIntStateOf(1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "Compare Foods",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(20.dp))

        // First food
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Select first food",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    selectedProduct1?.productName ?: "No food selected",
                    color = if (selectedProduct1 != null)
                        BrandOceanTeal
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        activeSelection = 1
                        viewModel.updateSearchQuery("")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepTeal
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose first food")
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Second food
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    "Select second food",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    selectedProduct2?.productName ?: "No food selected",
                    color = if (selectedProduct2 != null)
                        BrandOceanTeal
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        activeSelection = 2
                        viewModel.updateSearchQuery("")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepTeal
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Choose second food")
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search cached foods") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(Modifier.height(12.dp))

        if (searchQuery.isNotBlank() && searchResults.isEmpty()) {
            Text(
                "No matching cached foods found.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        searchResults.forEach { product ->
            GlassCard (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (activeSelection == 1) {
                                viewModel.selectProduct1(product)
                            } else {
                                viewModel.selectProduct2(product)
                            }
                            viewModel.updateSearchQuery("")
                        }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            product.productName ?: "Unknown product",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            product.brands ?: "Unknown brand",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Icon(
                        Icons.Default.AddCircleOutline,
                        contentDescription = "Select product",
                        tint = BrandOceanTeal
                    )
                }
            }
        }

        // Nutrition comparison
        if (selectedProduct1 != null && selectedProduct2 != null) {
            Spacer(Modifier.height(20.dp))

            Text(
                "Nutrition Comparison",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(12.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    ComparisonRow(
                        "Nutrient",
                        selectedProduct1!!.productName ?: "Food1",
                        selectedProduct2!!.productName ?: "Food2"
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

                    ComparisonRow(
                        "Calories",
                        selectedProduct1!!.energyKcal?.let { "$it kcal" } ?: "—",
                        selectedProduct2!!.energyKcal?.let { "$it kcal" } ?: "—",
                        compareNutrients(
                            selectedProduct1!!.energyKcal,
                            selectedProduct2!!.energyKcal,
                            higherIsBetter = false
                        )
                    )

                    ComparisonRow(
                        "Protein",
                        selectedProduct1!!.proteins?.let { "$it g" } ?: "—",
                        selectedProduct2!!.proteins?.let { "$it g" } ?: "—",
                        compareNutrients(
                            selectedProduct1!!.proteins,
                            selectedProduct2!!.proteins,
                            higherIsBetter = true
                        )
                    )

                    ComparisonRow(
                        "Carbohydrates",
                        selectedProduct1!!.carbohydrates?.let { "$it g" } ?: "—",
                        selectedProduct2!!.carbohydrates?.let { "$it g" } ?: "—",
                        compareNutrients(
                            selectedProduct1!!.carbohydrates,
                            selectedProduct2!!.carbohydrates,
                            higherIsBetter = false
                        )
                    )

                    ComparisonRow(
                        "Fat",
                        selectedProduct1!!.fat?.let { "$it g" } ?: "—",
                        selectedProduct2!!.fat?.let { "$it g" } ?: "—",
                        compareNutrients(
                            selectedProduct1!!.fat,
                            selectedProduct2!!.fat,
                            higherIsBetter = false
                        )
                    )

                    ComparisonRow(
                        "Sugars",
                        selectedProduct1!!.sugars?.let { "$it g" } ?: "—",
                        selectedProduct2!!.sugars?.let { "$it g" } ?: "—",
                        compareNutrients(
                            selectedProduct1!!.sugars,
                            selectedProduct2!!.sugars,
                            higherIsBetter = false
                        )
                    )

                    ComparisonRow(
                        "Fiber",
                        selectedProduct1!!.fiber?.let { "$it g" } ?: "—",
                        selectedProduct2!!.fiber?.let { "$it g" } ?: "—",
                        compareNutrients(
                            selectedProduct1!!.fiber,
                            selectedProduct2!!.fiber,
                            higherIsBetter = true
                        )
                    )

                    ComparisonRow(
                        "Salt",
                        selectedProduct1!!.salt?.let { "$it g" } ?: "—",
                        selectedProduct2!!.salt?.let { "$it g" } ?: "—",
                        compareNutrients(
                            selectedProduct1!!.salt,
                            selectedProduct2!!.salt,
                            higherIsBetter = false
                        )
                    )

                    ComparisonRow(
                        "Nutrition Grade",
                        selectedProduct1!!.nutritionGrade?.uppercase() ?: "—",
                        selectedProduct2!!.nutritionGrade?.uppercase() ?: "—"
                    )

                    ComparisonRow(
                        "NOVA Group",
                        selectedProduct1!!.novaGroup?.toString() ?: "—",
                        selectedProduct2!!.novaGroup?.toString() ?: "—"
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        OutlinedButton(
            onClick = viewModel::clearSelection,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = BrandDeepTeal
            )
        ) {
            Text("Clear Selection")
        }
    }
}


@Composable
fun ComparisonRow(
    title: String,
    food1: String,
    food2: String,
    result: Pair<String, String>? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                food1,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            result?.first?.let {
                Text(
                    it,
                    color = when (it) {
                        "Better" -> BrandOceanTeal
                        "Lower priority" -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                food2,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            result?.second?.let {
                Text(
                    it,
                    color = when (it) {
                        "Better" -> BrandOceanTeal
                        "Lower priority" -> MaterialTheme.colorScheme.onSurfaceVariant
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
private fun compareNutrients(value1: Double?, value2:Double?, higherIsBetter: Boolean): Pair<String,String>{
    if (value1 == null || value2 == null) {
        return "Not available" to "Not available"
    }

    if (value1 == value2) {
        return "Equal" to "Equal"
    }
    return if(
        (higherIsBetter && value1 > value2) || (!higherIsBetter && value1<value2)
    ){
        "Better" to "Lower Priority"
    } else {
        "Lower Priority" to "Better"
    }
}