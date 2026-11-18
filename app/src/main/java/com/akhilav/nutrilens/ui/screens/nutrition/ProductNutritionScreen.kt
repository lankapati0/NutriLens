package com.akhilav.nutrilens.ui.screens.nutrition

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.akhilav.nutrilens.data.local.entity.ProductEntity
import kotlinx.coroutines.launch

@Composable
fun ProductNutritionScreen(
    product: ProductEntity,
    onSaveConsumption: (Double)-> Unit
){
    var quantityText by remember { mutableStateOf("") }
    val bringIntoViewRequester = remember {
        BringIntoViewRequester()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ){
        Text(
            text= product.productName ?: "Unknown product",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text= product.brands ?: "Brand unavailable",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = "Nutrition per 100 g",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.height(12.dp))
        NutritionValue("Calories",product.energyKcal,"kcal")
        NutritionValue("Protein",product.proteins,"g")
        NutritionValue("Carbohydrates",product.carbohydrates,"g")
        NutritionValue("Fat",product.fat,"g")
        NutritionValue("Sugars",product.sugars,"g")
        NutritionValue("Fiber",product.fiber,"g")
        NutritionValue("Salt",product.salt,"g")

        Spacer(Modifier.height(28.dp))

        Text(
            text = "Log consumed quantity",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(value = quantityText,
            onValueChange = {quantityText = it},
            label = {Text("Quantity in grams")},
            placeholder = { Text("Enter quantity") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            suffix = {Text("g")},
            singleLine = true, modifier=Modifier.fillMaxWidth().bringIntoViewRequester(bringIntoViewRequester)
                .onFocusEvent {focusState ->
                    if(focusState.isFocused){
                        coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                    }
                })
        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {

                val quantity = quantityText.toDoubleOrNull()

                if(quantity != null && quantity >0){
                    onSaveConsumption(quantity)
                }
            },
            enabled = (quantityText.toDoubleOrNull() ?: 0.0) >0,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Save Consumption")}

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