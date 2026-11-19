package com.akhilav.nutrilens.ui.screens.HealthyAlternatives

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Healthy Alternatives",
            style = MaterialTheme.typography.headlineMedium
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
                    contentDescription = "Search"
                )
            },
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        searchResults.forEach { product ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                onClick = {
                    viewModel.selectProduct(product)
                }) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        product.productName ?: "Unknown product",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        product.brands ?: "Unknown brand",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        selectedProduct?.let { product ->
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Selected Product",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        product.productName ?: "Unknown product",
                        style = MaterialTheme.typography.titleMedium
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
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))
            if (alternatives.isEmpty()) {
                Text(
                    text = "No similar products found in your local database yet. Search for more products in this category first.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                alternatives.forEach { alternative ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                                Text(
                                    alternative.productName ?: "Unknown product",
                                    style = MaterialTheme.typography.titleMedium
                                )

                                Text(
                                    alternative.brands ?: "Unknown brand",
                                    style = MaterialTheme.typography.bodySmall
                                )

                                Text(
                                    "Calories: ${alternative.energyKcal?.let { "$it kcal" } ?: "Not available"}"
                                )

                                Text(
                                    "Sugar (per 100g): ${alternative.sugars?.let { "$it g" } ?: "Not available"}"
                                )
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
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                "Protein: ${alternative.proteins?.let { "$it g" } ?: "Not available"}"
                            )
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
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "Fiber (per 100g): ${alternative.fiber?.let { "$it g" }?: "Not available"}"
                            )
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
                                color = MaterialTheme.colorScheme.primary
                            )


                            val alternativeScore = viewModel.calculateNutritionScore(alternative)
                            Text(
                                text = "Nutrition score: ${alternativeScore?.toString() ?: "Not available"} / 9",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                text = when{
                                    selectedProduct == null -> "Comparison unavailable"

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
                                color = MaterialTheme.colorScheme.primary
                            )

                            Text(
                                    "Fat: ${alternative.fat?.let { "$it g" } ?: "Not available"}"
                                )
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
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                }

            }
        }
    }
}