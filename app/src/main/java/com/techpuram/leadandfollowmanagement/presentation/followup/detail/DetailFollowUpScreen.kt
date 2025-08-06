package com.techpuram.leadandfollowmanagement.presentation.followup.detail

import android.text.format.DateFormat
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.R
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowUpDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Int) -> Unit,
    viewModel: DetailFollowUpViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(
                    "Follow-up Details",
                    fontWeight = FontWeight.Bold
                ) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    state.followUp?.id?.let { id ->
                        IconButton(onClick = { onNavigateToEdit(id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }

                    IconButton(
                        onClick = {
                            showDeleteDialog = true

                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Follow-up")
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                state.followUp == null -> {
                    Text(
                        text = "Follow-up not found",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        state.recordName.let {
                            if (it.isNotEmpty()){
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(20.dp)
                                    ) {

                                        Text(
                                            text = state.followUp?.module ?: "",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.link),
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = it,
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Medium,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }}
                            }
                        }

                        // Status Card
                        DetailCard(
                            title = "Status Information",
                            content = {
                                DetailRow(
                                    icon = ImageVector.vectorResource(R.drawable.flag),
                                    label = "Stage",
                                    value = state.followUp?.followUpStage ?: ""
                                )
                                DetailRow(
                                    icon = ImageVector.vectorResource(R.drawable.priority_high),
                                    label = "Priority",
                                    value = state.followUp?.priority ?: ""
                                )
                                DetailRow(
                                    icon = ImageVector.vectorResource(R.drawable.category),
                                    label = "Type",
                                    value = state.followUp?.followUpType ?: ""
                                )
                            }
                        )

                        // Time Information
                        if (state.followUp?.dueTime != null) {
                            DetailCard(
                                title = "Time Information",
                                content = {
                                    DetailRow(
                                        icon = Icons.Default.DateRange,
                                        label = "Due Date",
                                        value = formatDate(state.followUp?.dueTime!!)
                                    )
                                    DetailRow(
                                        icon = ImageVector.vectorResource(R.drawable.schedule),
                                        label = "Due Time",
                                        value = formatTime(state.followUp?.dueTime!!)
                                    )
                                    state.followUp?.reminder?.let { reminder ->
                                        DetailRow(
                                            icon = Icons.Default.Notifications,
                                            label = "Reminder",
                                            value = "$reminder minutes before"
                                        )
                                    }
                                }
                            )
                        }


                        // Notes
                        if (!state.followUp?.notes.isNullOrBlank()) {
                            DetailCard(
                                title = "Notes",
                                content = {
                                    Text(
                                        text = state.followUp?.notes ?: "",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Error Dialog
            state.error?.let { error ->
                AlertDialog(
                    onDismissRequest = { viewModel.clearError() },
                    title = { Text("Error") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("OK")
                        }
                    }
                )
            }
            if(showDeleteDialog){
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                    },
                    title = { Text("Delete Follow-up") },
                    text = { Text("Are you sure you want to delete this follow-up?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteFollowUp()
                                showDeleteDialog = false
                                onNavigateBack()
                            }
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            showDeleteDialog = false
                        }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return DateFormat.format("EEEE, MMM dd, yyyy", Date(timestamp)).toString()
}

private fun formatTime(timestamp: Long): String {
    return DateFormat.format("hh:mm a", Date(timestamp)).toString()
} 