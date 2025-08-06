package com.techpuram.leadandfollowmanagement.presentation.lead.addEdit

import android.annotation.SuppressLint
import android.text.format.DateFormat
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.R
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrUpdateLeadScreen(
    onNavigateToContactSelect: () -> Unit,
    onBackClick: (Long?) -> Unit,
    contactFromNextScreenData: Contact? = null,
    viewModel: AddEditLeadViewModel = hiltViewModel()
) {
    val leadState by viewModel.addOrUpdateLeadState.collectAsState()
    val customFields by viewModel.customFields.collectAsState()
    val selectedContact by viewModel.selectedContact.collectAsState()
    val saveComplete by viewModel.saveComplete.collectAsState()
    
    // Handle save completion
    LaunchedEffect(saveComplete) {
        if (saveComplete != null) {
            onBackClick(saveComplete)
        }
    }

    LaunchedEffect(contactFromNextScreenData) {
        contactFromNextScreenData?.let {
            viewModel.updateSelectedContact(it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (leadState.id != null) "Edit Lead" else "New Lead",
                        fontWeight = FontWeight.Bold
                    )

                },
                navigationIcon = {
                    IconButton(onClick = { onBackClick(null) }) {
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
            // Scrollable content
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Show general error message if any
            leadState.generalError?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
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
                        text = "Basic Information",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Name
                    OutlinedTextField(
                        value = leadState.name,
                        onValueChange = { viewModel.onEvent(AddEditLeadEvent.EnteredName(it)) },
                        label = { LabelWithRedAsterisk("Name*") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        isError = leadState.nameError != null,
                        supportingText = leadState.nameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    // Email
                    OutlinedTextField(
                        value = leadState.email ?: "",
                        onValueChange = { viewModel.onEvent(AddEditLeadEvent.EnteredEmail(it)) },
                        label = { Text("Email") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Mobile
                    OutlinedTextField(
                        value = leadState.mobile,
                        onValueChange = { viewModel.onEvent(AddEditLeadEvent.EnteredMobile(it)) },
                        label = { LabelWithRedAsterisk("Mobile*") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth(),
                        isError = leadState.mobileError != null,
                        supportingText = leadState.mobileError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                    )

                    // WhatsApp Number
                    OutlinedTextField(
                        value = leadState.whatsappNumber ?: "",
                        onValueChange = {
                            viewModel.onEvent(
                                AddEditLeadEvent.EnteredWhatsappNumber(
                                    it
                                )
                            )
                        },
                        label = { Text("WhatsApp Number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            // Status Dropdown
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
                        text = "Lead Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    var showStatusMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = showStatusMenu,
                        onExpandedChange = { showStatusMenu = it }
                    ) {
                        OutlinedTextField(
                            value = leadState.status,
                            onValueChange = {},
                            readOnly = true,
                            label = { LabelWithRedAsterisk("Status*") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showStatusMenu) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            isError = leadState.statusError != null,
                            supportingText = leadState.statusError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } }
                        )
                        ExposedDropdownMenu(
                            expanded = showStatusMenu,
                            onDismissRequest = { showStatusMenu = false }
                        ) {
                            listOf(
                                "New",
                                "Contacted",
                                "Qualified",
                                "Proposal",
                                "Negotiation",
                                "Closed Won",
                                "Closed Lost"
                            ).forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        viewModel.onEvent(AddEditLeadEvent.EnteredStatus(status))
                                        showStatusMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Lead Source
                    OutlinedTextField(
                        value = leadState.leadSource ?: "",
                        onValueChange = { viewModel.onEvent(AddEditLeadEvent.EnteredLeadSource(it)) },
                        label = { Text("Lead Source") },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        maxLines = 1,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Contact Selection
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Title row with delete icon
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            buildAnnotatedString {
                                append("Associated Record")
                            },
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Delete/Dustbin icon
                        IconButton(
                            onClick = {
                                viewModel.onEvent(AddEditLeadEvent.EmptySelectedContact)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Record",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    // Contact Selection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToContactSelect() }
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
                                text = if (leadState.contactName.isNullOrEmpty()) "Select Contact" else leadState.contactName.toString(),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // Converted Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Converted to Customer",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.weight(1f))

                Switch(
                    checked = leadState.isConverted,
                    onCheckedChange = {
                        viewModel.onEvent(AddEditLeadEvent.ToggleConverted(it))
                        viewModel.onEvent(AddEditLeadEvent.ToggleConvertDialog)
                    }
                )
            }

            // Custom Fields
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
                            val value = getCustomFieldValue(leadState, customField.columnName) ?: ""

                            when (customField.fieldType) {
                                "TEXT" -> {
                                    OutlinedTextField(
                                        value = value,
                                        onValueChange = { newValue ->
                                            viewModel.onEvent(
                                                AddEditLeadEvent.EnteredCustomField(
                                                    customField.columnName,
                                                    newValue
                                                )
                                            )
                                        },
                                        label = { Text(customField.fieldName) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                "NUMBER" -> {
                                    OutlinedTextField(
                                        value = value,
                                        onValueChange = { newValue ->
                                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                viewModel.onEvent(
                                                    AddEditLeadEvent.EnteredCustomField(
                                                        customField.columnName,
                                                        newValue
                                                    )
                                                )
                                            }
                                        },
                                        label = { Text(customField.fieldName) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                "DROPDOWN" -> {
                                    OutlinedTextField(
                                        value = value,
                                        onValueChange = { newValue ->
                                            viewModel.onEvent(
                                                AddEditLeadEvent.EnteredCustomField(
                                                    customField.columnName,
                                                    newValue
                                                )
                                            )
                                        },
                                        label = { Text(customField.fieldName) },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Convert Dialog
            if (leadState.showConvertDialog) {
                var showDatePicker by remember { mutableStateOf(false) }
                var showStageMenu by remember { mutableStateOf(false) }

                AlertDialog(
                    onDismissRequest = {
                        viewModel.onEvent(AddEditLeadEvent.ToggleConvertDialog)
                        viewModel.onEvent(AddEditLeadEvent.ToggleConverted(false))
                    },
                    title = { Text("Convert Lead to Customer") },
                    text = {
                        Column(
                            modifier = Modifier.verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Create Contact Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Create Contact from Lead")
                                Switch(
                                    checked = leadState.createContactFromLead,
                                    onCheckedChange = {
                                        viewModel.onEvent(AddEditLeadEvent.ToggleCreateContact)
                                    }
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                            Text(
                                text = "Deal Information",
                                style = MaterialTheme.typography.titleMedium
                            )

                            // Deal Title
                            OutlinedTextField(
                                value = leadState.dealTitle,
                                onValueChange = {
                                    viewModel.onEvent(
                                        AddEditLeadEvent.EnteredDealTitle(
                                            it
                                        )
                                    )
                                },
                                label = { Text("Deal Title") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Deal Amount
                            OutlinedTextField(
                                value = leadState.dealAmount,
                                onValueChange = {
                                    viewModel.onEvent(
                                        AddEditLeadEvent.EnteredDealAmount(
                                            it
                                        )
                                    )
                                },
                                label = { Text("Amount") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Deal Stage
                            ExposedDropdownMenuBox(
                                expanded = showStageMenu,
                                onExpandedChange = { showStageMenu = it }
                            ) {
                                OutlinedTextField(
                                    value = leadState.dealStage,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Stage") },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = showStageMenu
                                        )
                                    },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                )
                                ExposedDropdownMenu(
                                    expanded = showStageMenu,
                                    onDismissRequest = { showStageMenu = false }
                                ) {
                                    leadState.availableStages.forEach { stage ->
                                        DropdownMenuItem(
                                            text = { Text(stage) },
                                            onClick = {
                                                viewModel.onEvent(
                                                    AddEditLeadEvent.EnteredDealStage(
                                                        stage
                                                    )
                                                )
                                                showStageMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Deal Closing Date
                            OutlinedTextField(
                                value = leadState.dealClosingDate?.let {
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

                            // Deal Probability
                            OutlinedTextField(
                                value = leadState.dealProbability,
                                onValueChange = {
                                    viewModel.onEvent(
                                        AddEditLeadEvent.EnteredDealProbability(
                                            it
                                        )
                                    )
                                },
                                label = { Text("Success Probability (%)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Deal Description
                            OutlinedTextField(
                                value = leadState.dealDescription,
                                onValueChange = {
                                    viewModel.onEvent(
                                        AddEditLeadEvent.EnteredDealDescription(
                                            it
                                        )
                                    )
                                },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.onEvent(AddEditLeadEvent.ConvertLeadToDeal)
                            }
                        ) {
                            Text("Convert")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                viewModel.onEvent(AddEditLeadEvent.ToggleConvertDialog)
                                viewModel.onEvent(AddEditLeadEvent.ToggleConverted(false))
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )

                // Date Picker Dialog
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = leadState.dealClosingDate
                    )
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    datePickerState.selectedDateMillis?.let { date ->
                                        viewModel.onEvent(
                                            AddEditLeadEvent.EnteredDealClosingDate(
                                                date
                                            )
                                        )
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
            }

            } // End of scrollable content

            // Fixed save button at bottom
            Button(
                onClick = {
                    viewModel.onEvent(AddEditLeadEvent.SaveLead)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Save Lead")
            }
        }
    }
}

// Add the LabelWithRedAsterisk composable from DealAddEditScreen
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

// Keep the helper function unchanged
private fun getCustomFieldValue(
    addOrUpdateLeadState: AddEditLeadState,
    columnName: String
): String? {
    return when (columnName) {
        "cf1" -> addOrUpdateLeadState.cf1
        "cf2" -> addOrUpdateLeadState.cf2
        "cf3" -> addOrUpdateLeadState.cf3
        "cf4" -> addOrUpdateLeadState.cf4
        "cf5" -> addOrUpdateLeadState.cf5
        "cf6" -> addOrUpdateLeadState.cf6
        "cf7" -> addOrUpdateLeadState.cf7
        "cf8" -> addOrUpdateLeadState.cf8
        "cf9" -> addOrUpdateLeadState.cf9
        "cf10" -> addOrUpdateLeadState.cf10
        "cf11" -> addOrUpdateLeadState.cf11
        "cf12" -> addOrUpdateLeadState.cf12
        "cf13" -> addOrUpdateLeadState.cf13
        "cf14" -> addOrUpdateLeadState.cf14
        "cf15" -> addOrUpdateLeadState.cf15
        "cf16" -> addOrUpdateLeadState.cf16
        "cf17" -> addOrUpdateLeadState.cf17
        "cf18" -> addOrUpdateLeadState.cf18
        "cf19" -> addOrUpdateLeadState.cf19
        "cf20" -> addOrUpdateLeadState.cf20
        else -> null
    }
}