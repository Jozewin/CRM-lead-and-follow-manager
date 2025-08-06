package com.techpuram.leadandfollowmanagement.presentation.followup.addEdit

import android.text.format.DateFormat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.R
import com.techpuram.leadandfollowmanagement.presentation.deal.addEdit.LabelWithRedAsterisk
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowUpAddEditScreen(
    onNavigateBack: () -> Unit,
    onNavigateToRecordSelection: (String) -> Unit,
    viewModel: AddEditFollowUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    // Handle navigation after save
    LaunchedEffect(key1 = state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.id == null) "Add Follow-up" else "Edit Follow-up",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Module Selection
                    var moduleDropdownExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = moduleDropdownExpanded,
                        onExpandedChange = { moduleDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = state.module,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text("Module") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = moduleDropdownExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = moduleDropdownExpanded,
                            onDismissRequest = { moduleDropdownExpanded = false }
                        ) {
                            state.availableModules.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        viewModel.onEvent(AddEditFollowUpEvent.ModuleChanged(option))
                                        moduleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Record selection field
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.onEvent(AddEditFollowUpEvent.SelectRecord)
                                onNavigateToRecordSelection(state.module)
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (state.module) {
                                    "Contact" -> Icons.Default.Person
                                    "Lead" -> ImageVector.vectorResource(R.drawable.source)
                                    "Deal" -> ImageVector.vectorResource(R.drawable.handshake)
                                    else -> Icons.Default.Person
                                },
                                contentDescription = null,
                                modifier = Modifier.padding(end = 16.dp).size(24.dp)
                            )

                            Column {
                                LabelWithRedAsterisk("Associated ${state.module}*")

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = if (state.recordName.isNotEmpty()) state.recordName else "Select a ${state.module}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Due date toggle section
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Set Due Date/Time",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Switch(
                                    checked = state.hasDueTime,
                                    onCheckedChange = {
                                        viewModel.onEvent(AddEditFollowUpEvent.HasDueTimeChanged(it))
                                    }
                                )
                            }

                            // Show/hide due date/time fields based on toggle
                            AnimatedVisibility(visible = state.hasDueTime) {
                                Column(
                                    modifier = Modifier.padding(top = 16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = state.dueTime?.let { formatDate(it) } ?: "",
                                            onValueChange = { },
                                            readOnly = true,
                                            label = { Text("Date") },
                                            modifier = Modifier.weight(1f),
                                            trailingIcon = {
                                                IconButton(onClick = { showDatePicker = true }) {
                                                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                                                }
                                            }
                                        )

                                        OutlinedTextField(
                                            value = state.dueTime?.let { formatTime(it) } ?: "",
                                            onValueChange = { },
                                            readOnly = true,
                                            label = { Text("Time") },
                                            modifier = Modifier.weight(1f),
                                            trailingIcon = {
                                                IconButton(onClick = { showTimePicker = true }) {
                                                    Icon(ImageVector.vectorResource(R.drawable.schedule), contentDescription = "Select Time")
                                                }
                                            }
                                        )
                                    }

                                    // Reminder dropdown
                                    var reminderExpanded by remember { mutableStateOf(false) }
                                    ExposedDropdownMenuBox(
                                        expanded = reminderExpanded,
                                        onExpandedChange = { reminderExpanded = it }
                                    ) {
                                        OutlinedTextField(
                                            value = state.reminder?.let { minutes ->
                                                state.availableReminders.find { it.first == minutes }?.second
                                            } ?: "No reminder",
                                            onValueChange = { },
                                            readOnly = true,
                                            label = { Text("Reminder") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = reminderExpanded) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .menuAnchor()
                                        )
                                        ExposedDropdownMenu(
                                            expanded = reminderExpanded,
                                            onDismissRequest = { reminderExpanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("No reminder") },
                                                onClick = {
                                                    viewModel.onEvent(AddEditFollowUpEvent.ReminderChanged(null))
                                                    reminderExpanded = false
                                                }
                                            )
                                            state.availableReminders.forEach { (minutes, label) ->
                                                DropdownMenuItem(
                                                    text = { Text(label) },
                                                    onClick = {
                                                        viewModel.onEvent(AddEditFollowUpEvent.ReminderChanged(minutes))
                                                        reminderExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Follow-up details card
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
                                text = "Follow-up Details",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            // Follow-up Type
                            var typeExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = typeExpanded,
                                onExpandedChange = { typeExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = state.followUpType,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = {
                                        Row {
                                            Text("Follow-up Type")
                                            Text(" *", color = Color.Red)
                                        }
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = typeExpanded,
                                    onDismissRequest = { typeExpanded = false }
                                ) {
                                    state.availableTypes.forEach { type ->
                                        DropdownMenuItem(
                                            text = { Text(type) },
                                            onClick = {
                                                viewModel.onEvent(AddEditFollowUpEvent.TypeChanged(type))
                                                typeExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Priority
                            var priorityExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = priorityExpanded,
                                onExpandedChange = { priorityExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = state.priority,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = {
                                        Row {
                                            Text("Priority")
                                            Text(" *", color = Color.Red)
                                        }
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = priorityExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = priorityExpanded,
                                    onDismissRequest = { priorityExpanded = false }
                                ) {
                                    state.availablePriorities.forEach { priority ->
                                        DropdownMenuItem(
                                            text = { Text(priority) },
                                            onClick = {
                                                viewModel.onEvent(AddEditFollowUpEvent.PriorityChanged(priority))
                                                priorityExpanded = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Stage
                            var stageExpanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = stageExpanded,
                                onExpandedChange = { stageExpanded = it }
                            ) {
                                OutlinedTextField(
                                    value = state.followUpStage,
                                    onValueChange = { },
                                    readOnly = true,
                                    label = {
                                        Row {
                                            Text("Stage")
                                            Text(" *", color = Color.Red)
                                        }
                                    },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stageExpanded) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor()
                                )
                                ExposedDropdownMenu(
                                    expanded = stageExpanded,
                                    onDismissRequest = { stageExpanded = false }
                                ) {
                                    state.availableStages.forEach { stage ->
                                        DropdownMenuItem(
                                            text = { Text(stage) },
                                            onClick = {
                                                viewModel.onEvent(AddEditFollowUpEvent.StageChanged(stage))
                                                stageExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Notes card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Notes",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            OutlinedTextField(
                                value = state.notes,
                                onValueChange = { viewModel.onEvent(AddEditFollowUpEvent.NotesChanged(it)) },
                                placeholder = { Text("Add notes here...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                minLines = 3,
                                maxLines = 5
                            )
                        }
                    }



                }
                Button(
                    onClick = { viewModel.onEvent(AddEditFollowUpEvent.SaveFollowUp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )) {

                    Text("Save Deal")
                }

            }

            // Error dialog
            if (state.error != null) {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.clearError()
                    },
                    title = { Text("Error") },
                    text = { Text("Fill the mandatory fields!") },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.clearError()
                        }) {
                            Text("Retry")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            viewModel.clearError()
                            onNavigateBack()
                        }) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = state.dueTime
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { date ->
                            // Preserve the time part
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = state.dueTime
                                    ?: (System.currentTimeMillis() + 24 * 60 * 60 * 1000)
                                val selectedCalendar = Calendar.getInstance().apply {
                                    timeInMillis = date
                                }
                                set(Calendar.YEAR, selectedCalendar.get(Calendar.YEAR))
                                set(Calendar.MONTH, selectedCalendar.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, selectedCalendar.get(Calendar.DAY_OF_MONTH))
                            }
                            viewModel.onEvent(AddEditFollowUpEvent.DueTimeChanged(calendar.timeInMillis))
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

    // Time Picker Dialog
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = Calendar.getInstance().apply { timeInMillis = state.dueTime!! }
                .get(Calendar.HOUR_OF_DAY),
            initialMinute = Calendar.getInstance().apply { timeInMillis = state.dueTime!! }
                .get(Calendar.MINUTE)
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Select Time") },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = state.dueTime!!
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        viewModel.onEvent(AddEditFollowUpEvent.DueTimeChanged(calendar.timeInMillis))
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun formatDate(timestamp: Long): String {
    return DateFormat.format("MMM dd, yyyy", Date(timestamp)).toString()
}

private fun formatTime(timestamp: Long): String {
    return DateFormat.format("hh:mm a", Date(timestamp)).toString()
}