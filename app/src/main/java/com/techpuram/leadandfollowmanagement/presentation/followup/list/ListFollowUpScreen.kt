package com.techpuram.leadandfollowmanagement.presentation.followup.list

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.R
import com.techpuram.leadandfollowmanagement.domain.model.FollowUp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowUpListScreen(
    viewModel: ListFollowUpViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val state by viewModel.state.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Follow-ups",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Sort button
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.sort),
                                contentDescription = "Sort"
                            )
                        }

                        // Sort Menu
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            SortOrder.values().forEach { sortOrder ->
                                DropdownMenuItem(
                                    text = { Text(sortOrder.name.replace("_", " ")) },
                                    onClick = {
                                        viewModel.onEvent(
                                            ListFollowUpEvent.SortOrderChanged(sortOrder)
                                        )
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        if (state.followUps.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.info),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tap the + button to add a new follow up",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                }
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listOf("All", "Scheduled", "In Progress", "Completed")) { filterType ->
                    FilterChip(
                        onClick = {
                            selectedFilter = filterType
                            viewModel.onEvent(ListFollowUpEvent.FilterChanged(filterType))
                        },
                        label = { Text(filterType) },
                        selected = selectedFilter == filterType
                    )
                }
            }

            // Content Area
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.followUps,
                                key = { it.id ?: -1 }
                            ) { followUp ->
                                FollowUpItem(
                                    followUp = followUp,
                                    onItemClick = {
                                        followUp.id?.let { id ->
                                            onNavigateToDetail(id)
                                        }
                                    },
                                    onStageChange = { newStage ->
                                        viewModel.onEvent(
                                            ListFollowUpEvent.UpdateFollowUpStage(
                                                followUpId = followUp.id!!,
                                                newStage = newStage
                                            )
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
                        onDismissRequest = {
                            viewModel.onEvent(ListFollowUpEvent.ClearError)
                        },
                        title = { Text("Error") },
                        text = { Text(error) },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.onEvent(ListFollowUpEvent.ClearError)
                                }
                            ) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowUpItem(
    followUp: FollowUp,
    onItemClick: () -> Unit,
    onStageChange: (String) -> Unit
) {
    var showStageMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        onClick = onItemClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First Row - Type and Priority Dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Type : ${followUp.followUpType.toUpperCase()}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                // Priority Dot
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when (followUp.priority.lowercase()) {
                                "low" -> Color(0xFF4CAF50)  // Green
                                "medium" -> Color(0xFFFFC107)  // Yellow
                                "high" -> Color(0xFFF44336)  // Red
                                else -> MaterialTheme.colorScheme.primary
                            },
                            shape = CircleShape
                        )
                )
            }

            // Second Row - Due Time and Stage Chip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                followUp.dueTime?.let {
                    Text(
                        text = "Due: ${formatDateTime(it)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Stage Chip
                Box {
                    AssistChip(
                        onClick = { showStageMenu = true },
                        label = {
                            Text(
                                text = followUp.followUpStage.replaceFirstChar {
                                    if (it.isLowerCase()) it.titlecase() else it.toString()
                                }
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = when (followUp.followUpStage) {
                                    "scheduled" -> ImageVector.vectorResource(R.drawable.schedule)
                                    "in-progress" -> Icons.Default.PlayArrow
                                    "completed" -> Icons.Default.CheckCircle
                                    else -> ImageVector.vectorResource(R.drawable.schedule)
                                },
                                contentDescription = null,
                                tint = when (followUp.followUpStage) {
                                    "scheduled" -> MaterialTheme.colorScheme.primary
                                    "in-progress" -> Color(0xFFF57C00)
                                    "completed" -> Color(0xFF4CAF50)
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    )

                    DropdownMenu(
                        expanded = showStageMenu,
                        onDismissRequest = { showStageMenu = false }
                    ) {
                        listOf("scheduled", "in-progress", "completed").forEach { stage ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = stage.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase() else it.toString()
                                        }
                                    )
                                },
                                onClick = {
                                    onStageChange(stage)
                                    showStageMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Stage Selection Menu

}

private fun formatDateTime(timestamp: Long): String {
    return DateFormat.format("MMM dd, yyyy hh:mm a", Date(timestamp)).toString()
}