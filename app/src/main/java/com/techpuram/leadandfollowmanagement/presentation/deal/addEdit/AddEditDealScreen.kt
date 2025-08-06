package com.techpuram.leadandfollowmanagement.presentation.deal.addEdit

import android.text.format.DateFormat
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.R
import java.util.*

@Composable
fun LabelWithRedAsterisk(text: String) {
    Text(
        buildAnnotatedString {
            val asteriskIndex = text.indexOf('*')
            if (asteriskIndex >= 0) {
                append(text.substring(0, asteriskIndex))
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append("*")
                }
                if (asteriskIndex < text.length - 1) {
                    append(text.substring(asteriskIndex + 1))
                }
            } else {
                append(text)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealAddEditScreen(
    onNavigateBack: () -> Unit,
    onNavigateToContactSelection: () -> Unit,
    viewModel: AddEditDealViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val customFields by viewModel.customFields.collectAsState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showStageMenu by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (state.id == null) "New Deal" else "Edit Deal",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Title
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                )
                {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Deal Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { viewModel.onEvent(AddEditDealEvent.TitleChanged(it)) },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            maxLines = 1,
                            label = { LabelWithRedAsterisk("Deal Title*") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Amount
                        OutlinedTextField(
                            value = state.amount,
                            onValueChange = { viewModel.onEvent(AddEditDealEvent.AmountChanged(it)) },
                            label = { Text("Amount") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Next
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Stage

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                )
                {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Deals Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        ExposedDropdownMenuBox(
                            expanded = showStageMenu,
                            onExpandedChange = { showStageMenu = it }
                        ) {
                            OutlinedTextField(
                                value = state.stage,
                                onValueChange = {},
                                readOnly = true,
                                label = { LabelWithRedAsterisk("Stage*") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStageMenu) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = showStageMenu,
                                onDismissRequest = { showStageMenu = false }
                            ) {
                                state.availableStages.forEach { stage ->
                                    DropdownMenuItem(
                                        text = { Text(stage) },
                                        onClick = {
                                            viewModel.onEvent(AddEditDealEvent.StageChanged(stage))
                                            showStageMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Probability
                        OutlinedTextField(
                            value = state.probability,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditDealEvent.ProbabilityChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text("Success Probability (%)") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Closing Date

                        OutlinedTextField(
                            value = state.closingDate?.let {
                                DateFormat.format("MMM dd, yyyy", Date(it)).toString()
                            } ?: "",
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Closing Date") },
                            trailingIcon = {
                                IconButton(onClick = { showDatePicker = true }) {
                                    Icon(Icons.Default.DateRange, "Select Date")
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                // Contact/Lead Selection
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {


                        Text(
                            buildAnnotatedString {
                                append("Associated Record")
                                withStyle(style = SpanStyle(color = Color.Red)) {
                                    append("*")
                                }
                            },
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Contact Selection
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToContactSelection() }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Contact",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = state.contactName.ifEmpty { "Select Contact" },
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Description",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        // Description
                        OutlinedTextField(
                            value = state.description,
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditDealEvent.DescriptionChanged(
                                        it
                                    )
                                )
                            },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
                }

                if (customFields.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Custom Fields",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )

                            customFields.forEach { customField ->
                                val value = getCustomFieldValue(state, customField.columnName) ?: ""

                                when (customField.fieldType) {
                                    "TEXT" -> {
                                        EnhancedLeadTextField(
                                            label = customField.fieldName,
                                            value = value,
                                            onValueChange = { newValue ->
                                                viewModel.onEvent(
                                                    AddEditDealEvent.CustomFieldValueChanged(
                                                        customField.columnName,
                                                        newValue
                                                    )
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                                        )
                                    }

                                    "NUMBER" -> {
                                        EnhancedLeadTextField(
                                            label = customField.fieldName,
                                            value = value,
                                            onValueChange = { newValue ->
                                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                    viewModel.onEvent(
                                                        AddEditDealEvent.CustomFieldValueChanged(
                                                            customField.columnName,
                                                            newValue
                                                        )
                                                    )
                                                }
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    }

                                    "DROPDOWN" -> {
                                        EnhancedLeadTextField(
                                            label = customField.fieldName,
                                            value = value,
                                            onValueChange = { newValue ->
                                                viewModel.onEvent(
                                                    AddEditDealEvent.CustomFieldValueChanged(
                                                        customField.columnName,
                                                        newValue
                                                    )
                                                )
                                            },
                                            isDropdown = true
                                        )
                                    }
                                }
                            }
                        }
                    }
                }


                // Save Button

            }
            Button(
                onClick = { viewModel.onEvent(AddEditDealEvent.SaveDeal) },

                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Save Deal")
                }
            }
        }



        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = state.closingDate
            )
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { date ->
                                // Preserve the time part
                                val calendar = Calendar.getInstance().apply {
                                    timeInMillis = state.closingDate
                                        ?: (System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                                    val selectedCalendar = Calendar.getInstance().apply {
                                        timeInMillis = date
                                    }
                                    set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR))
                                    set(Calendar.MONTH, selectedCalendar.get(Calendar.MONTH))
                                    set(
                                        Calendar.DAY_OF_MONTH,
                                        selectedCalendar.get(Calendar.DAY_OF_MONTH)
                                    )
                                }
                                viewModel.onEvent(AddEditDealEvent.ClosingDateChanged(calendar.timeInMillis))
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // Error Dialog
        state.error?.let { error ->
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(AddEditDealEvent.ClearError) },
                title = { Text("Error") },
                text = { Text("Fill the mandatory fields!") },
                confirmButton = {
                    TextButton(onClick = { viewModel.onEvent(AddEditDealEvent.ClearError) }) {
                        Text("Retry")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        viewModel.onEvent(AddEditDealEvent.ClearError)
                        onNavigateBack
                    }) {
                        Text("Dismiss")
                    }
                }
            )
        }
    }
}


@Composable
fun EnhancedLeadTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    singleLine: Boolean = true,
    minHeight: Dp = 56.dp,
    isDropdown: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        if (isDropdown) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    keyboardOptions = keyboardOptions,
                    singleLine = singleLine,
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 8.dp)
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "Dropdown",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = keyboardOptions,
                singleLine = singleLine,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    )
            )
        }
    }
}

// Helper function to get custom field value - kept unchanged
private fun getCustomFieldValue(dealAddEditState: AddEditDealState, columnName: String): String? {
    return when (columnName) {
        "cf1" -> dealAddEditState.cf1
        "cf2" -> dealAddEditState.cf2
        "cf3" -> dealAddEditState.cf3
        "cf4" -> dealAddEditState.cf4
        "cf5" -> dealAddEditState.cf5
        "cf6" -> dealAddEditState.cf6
        "cf7" -> dealAddEditState.cf7
        "cf8" -> dealAddEditState.cf8
        "cf9" -> dealAddEditState.cf9
        "cf10" -> dealAddEditState.cf10
        "cf11" -> dealAddEditState.cf11
        "cf12" -> dealAddEditState.cf12
        "cf13" -> dealAddEditState.cf13
        "cf14" -> dealAddEditState.cf14
        "cf15" -> dealAddEditState.cf15
        "cf16" -> dealAddEditState.cf16
        "cf17" -> dealAddEditState.cf17
        "cf18" -> dealAddEditState.cf18
        "cf19" -> dealAddEditState.cf19
        "cf20" -> dealAddEditState.cf20
        else -> null
    }
}



