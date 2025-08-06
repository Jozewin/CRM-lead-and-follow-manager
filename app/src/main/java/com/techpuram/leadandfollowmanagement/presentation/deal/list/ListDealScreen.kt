package com.techpuram.leadandfollowmanagement.presentation.deal.list

import android.text.format.DateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.R
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealListScreen(
    emptyText: Boolean,
    onNavigateToDealDetail: (Int) -> Unit,
    onDealClick: ((com.techpuram.leadandfollowmanagement.domain.model.Deal) -> Unit)? = null,
    viewModel: ListDealViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by remember { mutableStateOf("All Deals") }

    val availableStages = listOf(
        "Initial Contact",
        "Proposal",
        "Negotiation",
        "Due Diligence",
        "Closed Won",
        "Closed Lost"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Deals",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Sort menu
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier
                        ) {
                            Icon(ImageVector.vectorResource(R.drawable.sort), "Sort")
                        }
                        // Sort Menu
                        DropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            modifier = Modifier.align(Alignment.TopEnd)
                        ) {
                            DealSortOrder.values().forEach { sortOrder ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            sortOrder.name
                                                .replace("_", " ")
                                                .lowercase()
                                                .replaceFirstChar { it.uppercase() }
                                        )
                                    },
                                    onClick = {
                                        viewModel.onEvent(ListDealEvent.UpdateSort(sortOrder))
                                        showSortMenu = false
                                    }
                                )
                            }
                        }
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
        if (state.deals.isEmpty()) {
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
                        text = if(emptyText){
                            "Deal list is empty"
                        } else {
                            "Tap the + button to add a new Deal"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if(emptyText){
                        Text(
                            text = "Go to Deal tab to add Deal",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )}

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
                item {
                    FilterChip(
                        onClick = {
                            selectedFilter = "All Deals"
                            viewModel.onEvent(ListDealEvent.FilterByStage(null))
                        },
                        label = { Text("All Deals") },
                        selected = selectedFilter == "All Deals"
                    )
                }
                items(availableStages) { stage ->
                    FilterChip(
                        onClick = {
                            selectedFilter = stage
                            viewModel.onEvent(ListDealEvent.FilterByStage(stage))
                        },
                        label = { Text(stage) },
                        selected = selectedFilter == stage
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    state.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    state.deals.isEmpty() -> {}
                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = state.deals,
                                key = { it.deal.id }
                            ) { dealWithInfo ->
                                DealCard(
                                    dealWithInfo = dealWithInfo,
                                    availableStages = availableStages,
                                    onDealClick = { 
                                        onDealClick?.invoke(dealWithInfo.deal) ?: onNavigateToDealDetail(dealWithInfo.deal.id) 
                                    },
                                    onStageChange = { newStage ->
                                        viewModel.onEvent(
                                            ListDealEvent.UpdateDealStage(
                                                dealId = dealWithInfo.deal.id,
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
                        onDismissRequest = { viewModel.onEvent(ListDealEvent.ClearError) },
                        title = { Text("Error") },
                        text = { Text(error) },
                        confirmButton = {
                            TextButton(onClick = { viewModel.onEvent(ListDealEvent.ClearError) }) {
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
fun DealCard(
    dealWithInfo: DealWithInfo,
    availableStages: List<String>,
    onDealClick: () -> Unit,
    onStageChange: (String) -> Unit
) {
    var showStageMenu by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDealClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
              verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dealWithInfo.deal.title.replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase()
                        } else {
                            it.toString()
                        }
                    },
                    style = MaterialTheme.typography.titleMedium
                )
                Box {
                    AssistChip(
                        onClick = { showStageMenu = true },
                        label = { Text(dealWithInfo.deal.stage) }
                    )

                    // Stage Menu positioned relative to the AssistChip
                    DropdownMenu(
                        expanded = showStageMenu,
                        onDismissRequest = { showStageMenu = false }
                    ) {
                        availableStages.forEach { stage ->
                            DropdownMenuItem(
                                text = { Text(stage) },
                                onClick = {
                                    onStageChange(stage)
                                    showStageMenu = false
                                }
                            )
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(dealWithInfo.deal.amount != null){
                    Text(
                        text = NumberFormat.getCurrencyInstance().format(dealWithInfo.deal.amount),
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    Text(
                        text = "Amount : Not Set",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }


                if(dealWithInfo.deal.closingDate != null){
                    Text(
                        text = "Closing: ${
                            DateFormat.format("MMM dd, yyyy", Date(dealWithInfo.deal.closingDate))
                        }",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Text(
                        text = "Closing: Not Set",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

        }
    }

//    // Stage Menu
//    DropdownMenu(
//        expanded = showStageMenu,
//        onDismissRequest = { showStageMenu = false }
//    ) {
//        availableStages.forEach { stage ->
//            DropdownMenuItem(
//                text = { Text(stage) },
//                onClick = {
//                    onStageChange(stage)
//                    showStageMenu = false
//                }
//            )
//        }
//    }

    // Delete Confirmation Dialog
}