package com.akhilav.nutrilens.ui.screens.nutrition

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.viewmodels.settings.SettingsViewModel
import kotlinx.coroutines.launch


@Composable
fun ProductNutritionScreen(
    product: ProductEntity,
    onSaveConsumption: (Double, String) -> Unit,
    onToggleFavorite: (String, Boolean) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    var quantityText by remember {
        mutableStateOf("")
    }

    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }
    var selectedMeal by remember {mutableStateOf("Breakfast")}

    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    val veganEnabled by settingsViewModel.veganEnabled.collectAsStateWithLifecycle()
    val vegetarianEnabled by settingsViewModel.vegetarianEnabled.collectAsStateWithLifecycle()
    val glutenFreeEnabled by settingsViewModel.glutenFreeEnabled.collectAsStateWithLifecycle()
    val dairyFreeEnabled by settingsViewModel.dairyFreeEnabled.collectAsStateWithLifecycle()

    val veganResult = if(veganEnabled){
        checkDietaryCompatibility(product.ingredientsAnalysisTags,"en:vegan","en:non-vegan")
    } else {null}

    val vegetarianResult = if(vegetarianEnabled){
        checkDietaryCompatibility(product.ingredientsAnalysisTags,"en:vegetarian","en:non-vegetarian")
    } else {null}


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        fun formatProductTags(tags:String):String{
            if(tags.isNullOrBlank()) return "Not available"

            return tags.split(",").joinToString(", "){ tag ->
                tag.substringAfterLast(":").replace("-"," ")
                    .replaceFirstChar { it.uppercase() }
            }
        }


        Text(
            text = product.productName ?: "Unknown product",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = product.brands ?: "Brand unavailable",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {
                onToggleFavorite(product.barcode, true)
            },
            enabled = !product.isFavorite,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandDeepTeal,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = if (product.isFavorite) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                },
                contentDescription = null
            )

            Spacer(Modifier.width(8.dp))

            Text(
                if (product.isFavorite) {
                    "Already in Favorites"
                } else {
                    "Add to Favorites"
                }
            )
        }

        Spacer(Modifier.height(24.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Nutrition per 100 g",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(12.dp))

                NutritionValue("Calories", product.energyKcal, "kcal")
                NutritionValue("Protein", product.proteins, "g")
                NutritionValue("Carbohydrates", product.carbohydrates, "g")
                NutritionValue("Fat", product.fat, "g")
                NutritionValue("Sugars", product.sugars, "g")
                NutritionValue("Fiber", product.fiber, "g")
                NutritionValue("Salt", product.salt, "g")
            }
        }

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Food Quality & Safety",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))
        GlassCard(modifier= Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Nutritional Quality",
                    style = MaterialTheme.typography.titleMedium
                )
                QualityValue(
                    "Nutri-Score",
                    product.nutritionGrade?.uppercase() ?: "Not available"
                )
                QualityValue(
                    "NOVA Group",
                    product.novaGroup?.toString() ?: "Not available"
                )

                QualityValue(
                    "Additives",
                    product.additivesCount?.toString() ?: "Not available"
                )
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Nutrient Quality Levels",
                    style = MaterialTheme.typography.titleMedium
                )
                QualityValue("Fat", product.fatLevel?.replaceFirstChar { it.uppercase() } ?: "Not available")
                QualityValue("Saturated Fat", product.saturatedFatLevel?.replaceFirstChar { it.uppercase() } ?: "Not available")
                QualityValue("Sugars", product.sugarLevel?.replaceFirstChar { it.uppercase() } ?: "Not available")
                QualityValue("Salt", product.saltLevel?.replaceFirstChar { it.uppercase() } ?: "Not available")
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Ingredients & Processing",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = product.ingredientsText ?: "Ingredients not available",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(16.dp))

                val commonAllergens = listOf(
                    "Gluten" to "en:gluten",
                    "Milk" to "en:milk",
                    "Eggs" to "en:eggs",
                    "Peanuts" to "en:peanuts",
                    "Nuts" to "en:nuts",
                    "Soybeans" to "en:soybeans",
                    "Sesame" to "en:sesame-seeds",
                    "Celery" to "en:celery",
                    "Mustard" to "en:mustard",
                    "Lupin" to "en:lupin",
                    "Fish" to "en:fish",
                    "Crustaceans" to "en:crustaceans",
                    "Molluscs" to "en:molluscs",
                    "Sulphites" to "en:sulphur-dioxide-and-sulphites"
                )

                Text(
                    text = "Allergens",
                    style = MaterialTheme.typography.titleMedium
                )

                QualityValue(
                    "Contains",
                    product.allergens ?: "Not available"
                )
                product.tracesTags?.let {
                    QualityValue(
                        "Traces",
                        formatProductTags(it))
                }
                Text(
                    text = "⚠️ Allergen information may be incomplete. Always check the product packaging before consumption.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Dietary Compatibility",
                    style = MaterialTheme.typography.titleMedium
                )

                if(veganEnabled){
                    QualityValue("Vegan",veganResult ?: "Not available")
                }

                if(vegetarianEnabled){
                    QualityValue("Vegetarian",vegetarianResult ?: "Not available")
                }

                commonAllergens.forEach { (name,tag) ->
                    AllergenValue(name,
                        checkAllergenCompatibility(product.allergensTags,
                            product.tracesTags,tag))
                }

                if(!product.labelsTags.isNullOrBlank()){
                    QualityValue("Labels / Certifications",
                        formatProductTags(product.labelsTags))
                }

                QualityValue("Eco-score",product.ecoScoreGrade?.uppercase() ?: "Not available")

            }

        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Log consumed quantity",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(12.dp))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity in grams") },
                    placeholder = { Text("Enter quantity") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    suffix = { Text("g") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(bringIntoViewRequester)
                        .onFocusEvent { focusState ->
                            if (focusState.isFocused) {
                                coroutineScope.launch {
                                    bringIntoViewRequester.bringIntoView()
                                }
                            }
                        },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(16.dp))

                Text(text="Select Meal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(10.dp))

                val mealTypes = listOf("Breakfast","Lunch","Snacks","Dinner","Other")

                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)){
                    mealTypes.forEach { meal ->
                        FilterChip(
                            selected = selectedMeal == meal,
                            onClick = {selectedMeal = meal},
                            label = {Text(meal, maxLines = 1, softWrap = false)},
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        val quantity = quantityText.toDoubleOrNull()

                        if (quantity != null && quantity > 0) {
                            onSaveConsumption(quantity,selectedMeal)
                            keyboardController?.hide()
                        }
                    },
                    enabled = (quantityText.toDoubleOrNull() ?: 0.0) > 0,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepTeal,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Consumption")
                }
            }
        }
    }


}


@Composable
fun NutritionValue(title:String, value:Double?,unit:String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title)
        Text(
            text = value?.let { "$it $unit" } ?: "Not available",
            color = if (value == null) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
    }

}

@Composable
fun QualityValue(title:String, value:String){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top

    ) {
        Text(title, modifier = Modifier.weight(1f),textAlign = TextAlign.Start)
        Spacer(Modifier.height(8.dp))
        Text(
            text = value,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
fun checkDietaryCompatibility(tags:String?,suitableTag:String, unsuitableTag:String):String{
    if(tags.isNullOrBlank()) return "Not Available"

    val tagList = tags.lowercase().split(",").map { it.trim() }

    val normalizedTags = tags.lowercase()
    return when {
        suitableTag in tagList -> "Suitable"
        unsuitableTag in tagList -> "Not suitable"
        else -> "Not available"
    }
}
fun checkAllergenCompatibility(allergens:String?,traces:String?,allergenTag:String?): String {
    val allergenList = allergens?.lowercase()?.split(",")
        ?.map { it.trim() } ?: emptyList()

    val traceList = traces?.lowercase()?.split(",")
        ?.map { it.trim() } ?: emptyList()

    if(allergenTag in allergenList){
        return "Contains"
    }
    if(traces in traceList){
        return "May contain traces"
    }
    return "Not available"
}
@Composable
fun AllergenValue(title:String,value:String){
    val backgroundColor = when (value) {
        "Contains" -> Color(0xFFFFE3E3)
        "May contain traces" -> Color(0xFFFFF1CC)
        else -> Color(0xFFE8EAED)
    }

    val textColor = when (value) {
        "Contains" -> Color(0xFFB3261E)
        "May contain traces" -> Color(0xFF8A5A00)
        else -> Color(0xFF5F6368)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            color = backgroundColor,
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = value,
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 6.dp
                ),
                color = textColor,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}