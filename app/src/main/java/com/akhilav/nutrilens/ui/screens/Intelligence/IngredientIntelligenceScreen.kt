package com.akhilav.nutrilens.ui.screens.Intelligence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.viewmodels.IngredientIntelligenceViewModel


@Composable
fun IngredientIntelligenceScreen(
    viewModel: IngredientIntelligenceViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResult by viewModel.searchResults.collectAsStateWithLifecycle()
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Ingredient Intelligence",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Understand what's inside your food.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

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

        searchResult.forEach { product ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.selectProduct(product)
                        }
                        .padding(16.dp)
                ) {
                    Text(
                        text = product.productName ?: "Unknown product",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        product.brands ?: "Unknown brand",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Nutrition Grade: ${product.nutritionGrade?.uppercase() ?: "Not available"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BrandOceanTeal
                    )

                    Text(
                        text = "NOVA Group: ${product.novaGroup?.toString() ?: "Not available"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        selectedProduct?.let { product ->
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = product.productName ?: "Unknown product",
                style = MaterialTheme.typography.titleLarge,
                color = BrandDeepTeal
            )

            Spacer(modifier = Modifier.height(12.dp))

            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ingredients",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val ingredients = product.ingredientsText
                        ?.takeIf { it.isNotBlank() }

                    Text(
                        text = ingredients
                            ?: "Ingredient information is not available for this product.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (ingredients == null) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )

                    val additiveCodes = ingredients
                        ?.let { detectAdditiveCodes(it) }
                        .orEmpty()

                    if (additiveCodes.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Detected E-number codes",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        additiveCodes.forEach { code ->
                            GlassCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp)
                                ) {
                                    Text(
                                        text = code,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = BrandOceanTeal
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = getAdditiveInfo(code),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else if (ingredients != null) {
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "No E-number codes detected in the ingredient text.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}


private fun detectAdditiveCodes(ingredients:String):List<String>{
    val regex = Regex("""\bE\s?\d{3,4}[a-zA-Z]?\b""", RegexOption.IGNORE_CASE)
    return regex.findAll(ingredients)
        .map { it.value.replace(" ","").uppercase() }
        .distinct().toList()
}

private fun getAdditiveInfo(code:String): String {
    return when(code.uppercase()){
        "E200" -> "Sorbic acid – a preservative."
        "E201" -> "Sodium sorbate – a preservative."
        "E203" -> "Calcium sorbate – a preservative."
        "E210" -> "Benzoic acid – a preservative."
        "E212" -> "Potassium benzoate – a preservative."
        "E220" -> "Sulphur dioxide – a preservative and antioxidant."
        "E223" -> "Sodium metabisulphite – a preservative."
        "E250" -> "Sodium nitrite – a preservative."
        "E252" -> "Potassium nitrate – a preservative."
        "E407" -> "Carrageenan – a thickening and gelling agent."
        "E412" -> "Guar gum – a thickening agent."
        "E450" -> "Diphosphates – a raising and stabilising agent."
        "E500" -> "Sodium carbonates – an acidity regulator and raising agent."
        "E950" -> "Acesulfame K – a sweetener."
        "E951" -> "Aspartame – a sweetener."
        "E955" -> "Sucralose – a sweetener."
        else -> "Additive code detected. More information is not available in this lookup."
    }
}