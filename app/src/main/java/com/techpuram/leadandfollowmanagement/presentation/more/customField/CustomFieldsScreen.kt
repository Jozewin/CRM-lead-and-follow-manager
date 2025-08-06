package com.techpuram.leadandfollowmanagement.presentation.more.customField

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.domain.model.CustomField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomFieldsScreen(
    onBackClick: () -> Unit,
    viewModel: CustomFieldsViewModel = hiltViewModel()
) {
    val customFields by viewModel.customFields.collectAsState()
    val selectedModule by viewModel.selectedModule.collectAsState()

    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var customFieldToDelete by remember { mutableStateOf<CustomField?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Custom Fields") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Custom Field",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Module selector section
            Text(
                text = "Select Module",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ModuleButton(
                    text = "Contact",
                    isSelected = selectedModule == "Contact",
                    onClick = { viewModel.setSelectedModule("Contact") },
                    modifier = Modifier.weight(1f)
                )

                ModuleButton(
                    text = "Lead",
                    isSelected = selectedModule == "Lead",
                    onClick = { viewModel.setSelectedModule("Lead") },
                    modifier = Modifier.weight(1f)
                )

                ModuleButton(
                    text = "Deal",
                    isSelected = selectedModule == "Deal",
                    onClick = { viewModel.setSelectedModule("Deal") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Custom fields list section
            Text(
                text = "Custom Fields for $selectedModule",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val filteredFields = customFields.filter { it.module == selectedModule }

            if (filteredFields.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No custom fields yet. Click + to add one.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredFields) { customField ->
                        CustomFieldItem(
                            customField = customField,
                            onDeleteClick = {
                                customFieldToDelete = customField
                                showDeleteConfirmation = true
                            }
                        )
                    }
                }
            }
        }
    }

    // Add custom field dialog
    if (showAddDialog) {
        AddCustomFieldDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, type ->
                viewModel.createCustomField(name, type)
                showAddDialog = false
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation && customFieldToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteConfirmation = false
                customFieldToDelete = null
            },
            title = { Text("Delete Custom Field") },
            text = {
                Text(
                    "Are you sure you want to delete '${customFieldToDelete?.fieldName}'? " +
                            "This will remove the field and clear all data stored in it."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        customFieldToDelete?.let { viewModel.deleteCustomField(it) }
                        showDeleteConfirmation = false
                        customFieldToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        customFieldToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ModuleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun CustomFieldItem(
    customField: CustomField,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = customField.fieldName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Type: ${customField.fieldType}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Column: ${customField.columnName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun AddCustomFieldDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, type: String) -> Unit
) {
    var fieldName by remember { mutableStateOf("") }
    var fieldType by remember { mutableStateOf("TEXT") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Field") },
        text = {
            Column(
                modifier = Modifier.padding(top = 8.dp)
            ) {
                OutlinedTextField(
                    value = fieldName,
                    onValueChange = { fieldName = it },
                    label = { Text("Field Name") },
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )

                Text(
                    text = "Field Type",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FieldTypeButton(
                        text = "Text",
                        isSelected = fieldType == "TEXT",
                        onClick = { fieldType = "TEXT" },
                        modifier = Modifier.weight(1f)
                    )

                    FieldTypeButton(
                        text = "Number",
                        isSelected = fieldType == "NUMBER",
                        onClick = { fieldType = "NUMBER" },
                        modifier = Modifier.weight(1f)
                    )

                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(fieldName, fieldType) },
                enabled = fieldName.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun FieldTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = MaterialTheme.shapes.small,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}