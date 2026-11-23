package com.akhilav.nutrilens.ui.screens.profile
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.akhilav.nutrilens.data.local.entity.UserProfileEntity
import com.akhilav.nutrilens.ui.theme.BrandDeepTeal
import com.akhilav.nutrilens.ui.theme.BrandOceanTeal
import com.akhilav.nutrilens.ui.component.GlassCard
import com.akhilav.nutrilens.viewmodels.profile.UserProfileViewModel
import com.akhilav.nutrilens.viewmodels.settings.SettingsViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    profileViewModel: UserProfileViewModel = hiltViewModel(),
) {
    val profile by profileViewModel.profile.collectAsStateWithLifecycle()
    val settingViewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

    val pickerTextColor = MaterialTheme.colorScheme.onSurface.toArgb()

    val indianDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val bmi = remember(profile?.heightCm, profile?.weightKg) {
        val height = profile?.heightCm?.div(100.0)
        val weight = profile?.weightKg

        if (height != null && weight != null && height > 0) {
            weight / (height * height)
        } else {
            null
        }
    }

    val nutritionRecommendation = remember(bmi) {
        when {
            bmi == null ->
                "Complete your height and weight to see basic nutrition guidance."

            bmi < 18.5 ->
                "Focus on balanced meals with adequate calories, protein, healthy fats and nutrient-rich foods."

            bmi < 25 ->
                "Maintain a balanced diet with vegetables, fruits, whole grains and adequate protein."

            bmi < 30 ->
                "Focus on balanced portions, vegetables, whole grains and regular physical activity."

            else ->
                "Focus on balanced meals, fibre-rich foods and regular physical activity. Consider professional nutrition guidance."
        }
    }

    val activityRecommendation = when (profile?.activityLevel) {
        "Sedentary" ->
            "Try adding regular walking and gradually increase daily movement."

        "Lightly Active" ->
            "Maintain regular movement and include a mix of walking and strength activities."

        "Moderately Active" ->
            "Continue your activity routine and support it with balanced meals and hydration."

        "Very Active" ->
            "Support your activity with adequate energy, protein, recovery and hydration."

        else ->
            "Select your activity level to receive activity guidance."
    }

    val age = remember(profile?.dateOfBirth) {
        profile?.dateOfBirth?.let {
            runCatching {
                Period.between(LocalDate.parse(it), LocalDate.now()).years
            }.getOrNull()
        }
    }

    var displayName by remember { mutableStateOf("") }
    var showHeightSheet by remember { mutableStateOf(false) }
    var showWeightSheet by remember { mutableStateOf(false) }
    var profileValidationError by remember { mutableStateOf<String?>(null) }
    var dateOfBirth by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var heightInchesInput by remember { mutableStateOf("") }
    var weightInput by remember { mutableStateOf("") }
    var measurementSystem by remember { mutableStateOf("Metric") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var selectedHeightCm by remember { mutableIntStateOf(170) }
    var selectedHeightFeet by remember { mutableIntStateOf(5) }
    var selectedHeightInches by remember { mutableIntStateOf(7) }
    var selectedWeight by remember { mutableIntStateOf(70) }

    var bmrGender by remember { mutableStateOf("") }
    var activityLevel by remember { mutableStateOf("") }

    val hasValidHeight = if (measurementSystem == "Metric") {
        (heightInput.toDoubleOrNull() ?: 0.0) > 0
    } else {
        val feet = heightInput.toDoubleOrNull() ?: 0.0
        val inches = heightInchesInput.toDoubleOrNull() ?: 0.0
        (feet * 12 + inches) > 0
    }

    val completedProfileItems = listOf(
        selectedImageUri != null || !profile?.profileImageUrl.isNullOrBlank(),
        displayName.isNotBlank(),
        dateOfBirth.isNotBlank(),
        hasValidHeight,
        (weightInput.toDoubleOrNull() ?: 0.0) > 0
    ).count { it }

    val profileCompletion = completedProfileItems * 20

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            selectedImageUri = it

            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                selectedImageUri = it
            }
        }
    }

    LaunchedEffect(profile) {
        displayName = profile?.displayName ?: ""
        dateOfBirth = profile?.dateOfBirth ?: ""
        measurementSystem = profile?.measurementSystem ?: "Metric"
        bmrGender = profile?.bmrSex ?: ""
        activityLevel = profile?.activityLevel ?: ""

        heightInput = profile?.heightCm?.let {
            if (measurementSystem == "Metric") {
                it.toString()
            } else {
                val totalInches = it / 2.54
                (totalInches / 12).toInt().toString()
            }
        } ?: ""

        heightInchesInput = profile?.heightCm?.let {
            if (measurementSystem == "Imperial") {
                val totalInches = it / 2.54
                (totalInches % 12).toInt().toString()
            } else {
                ""
            }
        } ?: ""

        weightInput = profile?.weightKg?.let {
            if (measurementSystem == "Metric") {
                it.toString()
            } else {
                (it * 2.20462).toString()
            }
        } ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            "My Profile",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Profile Completion

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Profile Completion",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        "$profileCompletion%",
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandOceanTeal
                    )
                }

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    color = BrandOceanTeal,
                    progress = { profileCompletion / 100f }
                )

                Text(
                    "$completedProfileItems of 5 details completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Profile Photo

        val imageModel = selectedImageUri
            ?: profile?.profileImageUrl
                ?.takeIf { it.isNotBlank() }
                ?.let { Uri.parse(it) }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            imagePicker.launch(
                                PickVisualMediaRequest(
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
                            "📷\nSelect Photo",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface
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
                    Text(
                        "Change Profile Photo",
                        color = BrandOceanTeal
                    )
                }
            }
        }

        // Personal Details

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Personal Details",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = runCatching {
                        LocalDate.parse(dateOfBirth).format(indianDateFormatter)
                    }.getOrDefault(""),
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Date of Birth") },
                    placeholder = { Text("DD/MM/YYYY") },
                    trailingIcon = {
                        TextButton({ showDatePicker = true }) {
                            Text("Select", color = BrandOceanTeal)
                        }
                    }
                )

                val calculateAge = remember(dateOfBirth) {
                    runCatching {
                        Period.between(
                            LocalDate.parse(dateOfBirth),
                            LocalDate.now()
                        ).years
                    }.getOrNull()
                }

                calculateAge?.takeIf { it >= 0 }?.let {
                    Text(
                        "Age: $it years",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        // Calorie Requirements

        val bmr = remember(
            age,
            profile?.heightCm,
            profile?.weightKg,
            profile?.bmrSex
        ) {
            val currentAge = age
            val height = profile?.heightCm
            val weight = profile?.weightKg

            if (
                currentAge != null &&
                height != null &&
                weight != null &&
                height > 0 &&
                weight > 0
            ) {
                when (profile?.bmrSex) {
                    "Male" -> 10 * weight + 6.25 * height - 5 * currentAge + 5
                    "Female" -> 10 * weight + 6.25 * height - 5 * currentAge - 161
                    else -> null
                }
            } else {
                null
            }
        }

        val activityMultiplier = when (profile?.activityLevel) {
            "Sedentary" -> 1.2
            "Lightly Active" -> 1.375
            "Moderately Active" -> 1.55
            "Very Active" -> 1.725
            else -> null
        }

        val dailyCalorieEstimate = remember(bmr, activityMultiplier) {
            if (bmr != null && activityMultiplier != null) {
                (bmr * activityMultiplier).roundToInt()
            } else {
                null
            }
        }

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Calorie Requirements",
                    style = MaterialTheme.typography.titleLarge
                )

                HorizontalDivider()

                Text(
                    "Estimated BMR: ${
                        bmr?.roundToInt()?.let { "$it kcal/day" } ?: "Not available"
                    }"
                )

                Text(
                    "Estimated Daily Calories: ${
                        dailyCalorieEstimate?.let { "$it kcal/day" }
                            ?: "Complete your profile details"
                    }"
                )

                Button(
                    onClick = {
                        dailyCalorieEstimate?.let {
                            settingViewModel.saveCalorieTarget(it.toDouble())
                        }
                    },
                    enabled = dailyCalorieEstimate != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepTeal
                    )
                ) {
                    Text("Use as Daily Calorie Goal")
                }

                Text(
                    "These are estimates based on your age, height, weight and activity level.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Nutrition Recommendations

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Nutrition Recommendations",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    "General Nutrition Guidance",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandOceanTeal
                )

                Text(
                    nutritionRecommendation,
                    style = MaterialTheme.typography.bodyMedium
                )

                HorizontalDivider()

                Text(
                    "Activity Guidance",
                    style = MaterialTheme.typography.labelLarge,
                    color = BrandOceanTeal
                )

                Text(
                    activityRecommendation,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Measurement Units

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Measurement Units",
                    style = MaterialTheme.typography.titleLarge
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SegmentedButton(
                        selected = measurementSystem == "Metric",
                        onClick = { measurementSystem = "Metric" },
                        shape = SegmentedButtonDefaults.itemShape(0, 2)
                    ) {
                        Text("Metric")
                    }

                    SegmentedButton(
                        selected = measurementSystem == "Imperial",
                        onClick = { measurementSystem = "Imperial" },
                        shape = SegmentedButtonDefaults.itemShape(1, 2)
                    ) {
                        Text("Imperial")
                    }
                }

                Text(
                    "Height",
                    style = MaterialTheme.typography.titleMedium
                )

                if (measurementSystem == "Metric") {
                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Height (cm)") },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    awaitFirstDown(pass = PointerEventPass.Initial)
                                    waitForUpOrCancellation(
                                        pass = PointerEventPass.Initial
                                    )?.let {
                                        selectedHeightCm =
                                            heightInput.toDoubleOrNull()?.toInt() ?: 170
                                        showHeightSheet = true
                                    }
                                }
                            },
                        singleLine = true
                    )
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Feet") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        awaitFirstDown(pass = PointerEventPass.Initial)
                                        waitForUpOrCancellation(
                                            pass = PointerEventPass.Initial
                                        )?.let {
                                            selectedHeightFeet =
                                                heightInput.toDoubleOrNull()?.toInt() ?: 5
                                            selectedHeightInches =
                                                heightInchesInput.toDoubleOrNull()?.toInt() ?: 7
                                            showHeightSheet = true
                                        }
                                    }
                                },
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = heightInchesInput,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Inches") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .pointerInput(Unit) {
                                    awaitEachGesture {
                                        awaitFirstDown(pass = PointerEventPass.Initial)
                                        waitForUpOrCancellation(
                                            pass = PointerEventPass.Initial
                                        )?.let {
                                            selectedHeightFeet =
                                                heightInput.toDoubleOrNull()?.toInt() ?: 5
                                            selectedHeightInches =
                                                heightInchesInput.toDoubleOrNull()?.toInt() ?: 7
                                            showHeightSheet = true
                                        }
                                    }
                                },
                            singleLine = true
                        )
                    }
                }

                Text(
                    "Weight",
                    style = MaterialTheme.typography.titleMedium
                )

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(
                            if (measurementSystem == "Metric") {
                                "Weight (kg)"
                            } else {
                                "Weight (lb)"
                            }
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(Unit) {
                            awaitEachGesture {
                                awaitFirstDown(pass = PointerEventPass.Initial)
                                waitForUpOrCancellation(
                                    pass = PointerEventPass.Initial
                                )?.let {
                                    selectedWeight =
                                        weightInput.toDoubleOrNull()?.toInt() ?: 70
                                    showWeightSheet = true
                                }
                            }
                        },
                    singleLine = true
                )
            }
        }

        // Gender and Activity

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    "Gender for BMR Calculation",
                    style = MaterialTheme.typography.titleMedium
                )

                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("Male", "Female", "Prefer not to say")
                        .forEachIndexed { index, option ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = 3
                                ),
                                selected = bmrGender == option,
                                onClick = { bmrGender = option }
                            ) {
                                Text(
                                    text = option,
                                    maxLines = 1
                                )
                            }
                        }
                }

                HorizontalDivider()

                Text(
                    "Activity Level",
                    style = MaterialTheme.typography.titleMedium
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "Sedentary",
                        "Lightly Active",
                        "Moderately Active",
                        "Very Active"
                    ).forEach { level ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { activityLevel = level },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = activityLevel == level,
                                onClick = { activityLevel = level },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = BrandOceanTeal
                                )
                            )

                            Text(
                                text = level,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // BMI

        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Body Mass Index",
                    style = MaterialTheme.typography.titleLarge
                )

                if (bmi != null) {
                    Text(
                        "Your BMI: ${"%.1f".format(bmi)}",
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandOceanTeal
                    )

                    val bmiCategory = when {
                        bmi < 18.5 -> "Underweight"
                        bmi < 25.0 -> "Normal weight"
                        bmi < 30.0 -> "Overweight"
                        else -> "Obesity"
                    }

                    Text(
                        "Category: $bmiCategory",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        "Enter your height and weight to calculate BMI.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Save Profile

        Button(
            onClick = {
                profileValidationError = null

                if (displayName.isBlank()) {
                    profileValidationError = "Please enter your display name."
                    return@Button
                }

                if (dateOfBirth.isNotBlank()) {
                    val birthDate = runCatching {
                        LocalDate.parse(dateOfBirth)
                    }.getOrNull()

                    if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
                        profileValidationError = "Please enter a valid date of birth."
                        return@Button
                    }
                }

                val heightCm = if (measurementSystem == "Metric") {
                    heightInput.toDoubleOrNull()
                } else {
                    val feet = heightInput.toDoubleOrNull()
                    val inches = heightInchesInput.toDoubleOrNull()

                    if (feet != null && inches != null) {
                        (feet * 12 + inches) * 2.54
                    } else {
                        null
                    }
                }

                val weightKg = weightInput.toDoubleOrNull()?.let {
                    if (measurementSystem == "Metric") {
                        it
                    } else {
                        it / 2.20462
                    }
                }

                if (heightInput.isNotBlank() || heightInchesInput.isNotBlank()) {
                    if (heightCm == null || heightCm !in 50.0..250.0) {
                        profileValidationError =
                            "Height must be between 50 and 250 cm."
                        return@Button
                    }
                }

                if (weightInput.isNotBlank()) {
                    if (weightKg == null || weightKg !in 10.0..500.0) {
                        profileValidationError =
                            "Weight must be between 10 and 500 kg."
                        return@Button
                    }
                }

                profileViewModel.saveProfile(
                    UserProfileEntity(
                        displayName = displayName.trim(),
                        dateOfBirth = dateOfBirth.ifBlank { null },
                        heightCm = heightCm,
                        weightKg = weightKg,
                        profileImageUrl = selectedImageUri?.toString()
                            ?: profile?.profileImageUrl,
                        measurementSystem = measurementSystem,
                        bmrSex = bmrGender.ifBlank { null },
                        activityLevel = activityLevel.ifBlank { null }
                    )
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandDeepTeal
            )
        ) {
            Text("Save Profile")
        }

        profileValidationError?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    // Date Picker

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        dateOfBirth = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                            .toString()
                    }
                    showDatePicker = false
                }) {
                    Text("Ok")
                }
            },
            dismissButton = {
                TextButton({ showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Height Picker

    if (showHeightSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showHeightSheet = false
            },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Your Height",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(24.dp))

                if (measurementSystem == "Metric") {
                    WheelNumberPicker(
                        value = selectedHeightCm,
                        range = 50..250,
                        onValueChange = { selectedHeightCm = it }
                    )

                    Text("cm", color = MaterialTheme.colorScheme.onSurface)
                } else {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            WheelNumberPicker(
                                value = selectedHeightFeet,
                                range = 2..8,
                                onValueChange = { selectedHeightFeet = it }
                            )
                            Text("ft")
                        }

                        Spacer(Modifier.width(16.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            WheelNumberPicker(
                                value = selectedHeightInches,
                                range = 0..11,
                                onValueChange = { selectedHeightInches = it }
                            )
                            Text("in")
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (measurementSystem == "Metric") {
                            heightInput = selectedHeightCm.toString()
                        } else {
                            heightInput = selectedHeightFeet.toString()
                            heightInchesInput = selectedHeightInches.toString()
                        }

                        showHeightSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepTeal
                    )
                ) {
                    Text("Save Height")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    // Weight Picker

    if (showWeightSheet) {
        ModalBottomSheet(
            onDismissRequest = { showWeightSheet = false },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Your Weight",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WheelNumberPicker(
                        value = selectedWeight,
                        range = if (measurementSystem == "Metric") {
                            10..500
                        } else {
                            22..1102
                        },
                        onValueChange = { selectedWeight = it }
                    )

                    Text(
                        if (measurementSystem == "Metric") "kg" else "lb",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        weightInput = selectedWeight.toString()
                        showWeightSheet = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandDeepTeal
                    )
                ) {
                    Text("Save Weight")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun WheelNumberPicker(value:Int, range:IntRange,
                      onValueChange:(Int) -> Unit){

    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (value - range.first)
            .coerceIn(0, range.count() - 1)
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                onValueChange(
                    (index + range.first).coerceIn(range)
                )
            }
    }
    Box(
        modifier = Modifier
            .width(90.dp)
            .height(200.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(40.dp)
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                )
        )

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 80.dp),
            flingBehavior = rememberSnapFlingBehavior(
                lazyListState = listState
            ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(range.toList()) { number ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString(),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = if (number == value) {
                            MaterialTheme.typography.titleLarge
                        } else {
                            MaterialTheme.typography.bodyLarge
                        }
                    )
                }
            }
        }
    }
}