package com.akhilav.nutrilens.ui.screens.HealthyAlternatives

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.theme.component.GlassCard
import com.akhilav.nutrilens.viewmodels.HealthyAlternativesViewModel


@Composable
fun HealthyAlternativesScreen(
    viewModel: HealthyAlternativesViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()
    val alternatives by viewModel.alternatives.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Healthy Alternatives",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find alternative food products with better nutritional values.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = viewModel::updateSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search a food product") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = BrandOceanTeal
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        searchResults.forEach { product ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectProduct(product)
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
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select product",
                        tint = BrandOceanTeal
                    )
                }
            }
        }

        selectedProduct?.let { product ->

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Selected Product",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        product.productName ?: "Unknown product",
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandOceanTeal
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Calories: ${product.energyKcal?.let { "$it kcal" } ?: "Not available"}")
                    Text("Protein: ${product.proteins?.let { "$it g" } ?: "Not available"}")
                    Text("Carbohydrates: ${product.carbohydrates?.let { "$it g" } ?: "Not available"}")
                    Text("Fat: ${product.fat?.let { "$it g" } ?: "Not available"}")
                    Text("Sugar: ${product.sugars?.let { "$it g" } ?: "Not available"}")
                    Text("Fiber: ${product.fiber?.let { "$it g" } ?: "Not available"}")
                    Text("Nutrition Grade: ${product.nutritionGrade?.uppercase() ?: "Not available"}")
                }
            }
        }

        if (selectedProduct != null) {

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Similar Products",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (alternatives.isEmpty()) {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "No similar products found in your local database yet. Search for more products in this category first.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                alternatives.forEach { alternative ->

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                alternative.productName ?: "Unknown product",
                                style = MaterialTheme.typography.titleMedium,
                                color = BrandOceanTeal
                            )

                            Text(
                                alternative.brands ?: "Unknown brand",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text("Calories: ${alternative.energyKcal?.let { "$it kcal" } ?: "Not available"}")
                            Text("Sugar (per 100g): ${alternative.sugars?.let { "$it g" } ?: "Not available"}")

                            val selectedSugar = selectedProduct?.sugars
                            val alternativeSugar = alternative.sugars

                            Text(
                                text = when {
                                    selectedSugar == null || alternativeSugar == null ->
                                        "Sugar comparison unavailable"

                                    alternativeSugar < selectedSugar ->
                                        "Lower sugar by ${"%.2f".format(selectedSugar - alternativeSugar)} g"

                                    alternativeSugar > selectedSugar ->
                                        "Higher sugar by ${"%.2f".format(alternativeSugar - selectedSugar)} g"

                                    else -> "Same sugar level"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandOceanTeal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Protein: ${alternative.proteins?.let { "$it g" } ?: "Not available"}")

                            val selectedProtein = selectedProduct?.proteins
                            val alternativeProtein = alternative.proteins

                            Text(
                                text = when {
                                    selectedProtein == null || alternativeProtein == null ->
                                        "Protein comparison unavailable"

                                    alternativeProtein > selectedProtein ->
                                        "Higher protein by ${"%.2f".format(alternativeProtein - selectedProtein)} g"

                                    alternativeProtein < selectedProtein ->
                                        "Lower protein by ${"%.2f".format(selectedProtein - alternativeProtein)} g"

                                    else -> "Same protein level"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandOceanTeal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Fiber (per 100g): ${alternative.fiber?.let { "$it g" } ?: "Not available"}")

                            val selectedFiber = selectedProduct?.fiber
                            val alternativeFiber = alternative.fiber

                            Text(
                                text = when {
                                    selectedFiber == null || alternativeFiber == null ->
                                        "Fiber comparison unavailable"

                                    alternativeFiber > selectedFiber ->
                                        "Higher fiber by ${"%.2f".format(alternativeFiber - selectedFiber)} g"

                                    alternativeFiber < selectedFiber ->
                                        "Lower fiber by ${"%.2f".format(selectedFiber - alternativeFiber)} g"

                                    else -> "Same fiber level"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandOceanTeal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val alternativeScore =
                                viewModel.calculateNutritionScore(alternative)

                            Text(
                                text = "Nutrition score: ${alternativeScore?.toString() ?: "Not available"} / 9",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BrandDeepTeal
                            )

                            Text(
                                text = when {
                                    selectedProduct == null ->
                                        "Comparison unavailable"

                                    viewModel.calculateNutritionScore(selectedProduct!!) == null ||
                                            viewModel.calculateNutritionScore(alternative) == null ->
                                        "Comparison unavailable"

                                    viewModel.calculateNutritionScore(alternative)!! >
                                            viewModel.calculateNutritionScore(selectedProduct!!)!! ->
                                        "Better nutrition score"

                                    viewModel.calculateNutritionScore(alternative)!! <
                                            viewModel.calculateNutritionScore(selectedProduct!!)!! ->
                                        "Lower nutrition score"

                                    else -> "Same nutrition score"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandOceanTeal
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Fat: ${alternative.fat?.let { "$it g" } ?: "Not available"}")

                            val selectedFat = selectedProduct?.fat
                            val alternativeFat = alternative.fat

                            Text(
                                text = when {
                                    selectedFat == null || alternativeFat == null ->
                                        "Fat comparison unavailable"

                                    alternativeFat < selectedFat ->
                                        "Lower fat by ${"%.2f".format(selectedFat - alternativeFat)} g"

                                    alternativeFat > selectedFat ->
                                        "Higher fat by ${"%.2f".format(alternativeFat - selectedFat)} g"

                                    else -> "Same fat level"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = BrandOceanTeal
                            )
                        }
                    }
                }
            }
        }
    }
}
