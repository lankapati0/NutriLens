package com.akhilav.nutrilens.ui.screens.profile

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.akhilav.nutrilens.data.local.entity.UserProfileEntity
import com.akhilav.nutrilens.viewmodels.profile.UserProfileViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Composable
fun ProfileScreen(
    profileViewModel: UserProfileViewModel = hiltViewModel()
){
    val profile by profileViewModel.profile.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    val indianDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var displayName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var heightInchesInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var measurementSystem by remember { mutableStateOf("Metric") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            selectedImageUri = it

            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Keep the selected image URI.
                selectedImageUri = it
            }
        }
    }

    LaunchedEffect(profile) {
        displayName = profile?.displayName ?: ""
        dateOfBirth = profile?.dateOfBirth ?: ""
        measurementSystem = profile?.measurementSystem ?: "Metric"

        heightInput = profile?.heightCm?.let {
            if(measurementSystem == "Metric"){
                it.toString()
            } else {
                val totalInches = it/2.54
                (totalInches/12).toInt().toString()
            }
        } ?: ""

        heightInchesInput = profile?.heightCm?.let {
            if(measurementSystem == "Imperial"){
                val totalInches = it/2.54
                (totalInches%12).toInt().toString()
            } else {
                ""
            }
        } ?: ""

        weightInput = profile?.weightKg?.let {
            if(measurementSystem == "Metric"){
                it.toString()
            } else {
                (it*2.20462).toString()
            }
        } ?: ""
    }

    Column(modifier= Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()).imePadding(),
        verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            "My Profile",
            style= MaterialTheme.typography.headlineMedium
        )

        val imageModel = selectedImageUri
            ?: profile?.profileImageUrl
                ?.takeIf { it.isNotBlank() }
                ?.let { Uri.parse(it) }

        Box(
            modifier = Modifier
                .size(130.dp)
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable {
                    imagePicker.launch(
                        androidx.activity.result.PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = "Profile Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "📷\nSelect Photo",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        TextButton(
            onClick = {
                imagePicker.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        ) {
            Text("Change Profile Photo")
        }

        OutlinedTextField(
            value = displayName,
            onValueChange = { displayName = it },
            label = { Text("Display Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(4.dp),
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = runCatching {
                LocalDate.parse(dateOfBirth).format(indianDateFormatter)
            }.getOrDefault(""),
            onValueChange = {},
            readOnly = true,
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth(),
            label = {Text("Date of Birth")},
            placeholder = {Text("DD/MM/YYYY")},
            trailingIcon = {
                TextButton({showDatePicker = true}) { Text("Select") }
            }
        )

        Spacer(Modifier.height(16.dp))
        val calculateAge = remember(dateOfBirth){
            runCatching {
                Period.between(LocalDate.parse(dateOfBirth), LocalDate.now()).years
            }.getOrNull()
        }

        calculateAge?.takeIf { it>=0 }?.let {
            Text(
                text = "Age: $it years",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "Measurement Units",
            style = MaterialTheme.typography.titleMedium
        )

        SingleChoiceSegmentedButtonRow(modifier= Modifier.fillMaxWidth()) {
            SegmentedButton(
                selected = measurementSystem == "Metric",
                onClick = { measurementSystem = "Metric" },
                shape = SegmentedButtonDefaults.itemShape(0,2)
            ) {Text("Metric") }

            SegmentedButton(
                selected = measurementSystem == "Imperial",
                onClick = {measurementSystem = "Imperial"},
                shape = SegmentedButtonDefaults.itemShape(1,2)
            ) {Text("Imperial") }
        }

        Text(
            text = "Height",
            style = MaterialTheme.typography.titleMedium
        )

        if(measurementSystem == "Metric"){
            OutlinedTextField(
                value = heightInput,
                onValueChange = {heightInput=it},
                label = { Text("Height (cm)") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = heightInput,
                    onValueChange = { heightInput = it },
                    label = { Text("Feet") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )

                OutlinedTextField(
                    value = heightInchesInput,
                    onValueChange = { heightInchesInput = it },
                    label = { Text("Inches") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        Text(
            text = "Weight",
            style = MaterialTheme.typography.titleMedium
        )

        OutlinedTextField(
            value = weightInput,
            onValueChange = {weightInput = it},
            label = {
                Text(
                    if(measurementSystem == "Metric"){
                        "Weight (kg)"
                    } else {
                        "Weight (lb)"
                    }
                )
            },
            shape = RoundedCornerShape(4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            val heightCm = if(measurementSystem == "Metric"){
                heightInput.toDoubleOrNull()
            } else {
                val feet = heightInput.toDoubleOrNull()
                val inches = heightInchesInput.toDoubleOrNull()

                if(feet != null && inches != null){
                    (feet * 12 + inches)*2.54
                } else {null}
            }

            val weightKg = weightInput.toDoubleOrNull()?.let {
                if(measurementSystem == "Metric"){
                    it
                } else {
                    it/2.20462
                }
            }

            profileViewModel.saveProfile(UserProfileEntity(
                displayName= displayName.trim(),
                dateOfBirth=dateOfBirth.ifBlank { null },
                heightCm = heightCm,
                weightKg = weightKg,
                profileImageUrl = selectedImageUri?.toString() ?: profile?.profileImageUrl,
                measurementSystem = measurementSystem
            ))

        },     modifier = Modifier.fillMaxWidth()) {
            Text("Save Profile")
        }
    }

    if(showDatePicker){
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dateOfBirth = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC)
                            .toLocalDate().toString()

                    }
                    showDatePicker = false
                }) {Text("Ok") }
            },
            dismissButton = {
                TextButton({showDatePicker = false}) { Text("Cancel")}
            }
        ) { DatePicker(state = datePickerState)}
    }
}