package com.techpuram.leadandfollowmanagement.presentation.lead.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.R
import com.techpuram.leadandfollowmanagement.domain.model.Lead

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeadDetailScreen(
    leadId: Int,
    onBackClick: () -> Unit,
    onEditClick: (Lead) -> Unit,
    onConvertClick: (Lead) -> Unit,
    viewModel: DetailLeadViewModel = hiltViewModel()
) {
    val leadDetail by viewModel.leadDetail.collectAsState()
    val contact by viewModel.contact.collectAsState()
    val customFields by viewModel.customFields.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(leadId) {
        viewModel.getLeadById(leadId)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Lead Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                actions = {
                    if (leadDetail != null) {
                        // Convert button (only show if not already converted)
                        if (!leadDetail!!.isConverted) {
                            IconButton(onClick = { onConvertClick(leadDetail!!) }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Convert lead to deal"
                                )
                            }
                        }
                        IconButton(onClick = { onEditClick(leadDetail!!) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit lead"
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete lead"
                            )
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
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            when {
                isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading lead details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                leadDetail != null -> {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Lead Header Card
                            LeadHeaderCard(lead = leadDetail!!)

                            // Basic Information Card
                            BasicInfoCard(lead = leadDetail!!)

                            // Contact Information Card
                            contact?.let {
                                AssociatedContactCard(contact = it)
                            }

                            // Status Information Card
                            StatusInfoCard(lead = leadDetail!!)

                            // Custom Fields Card
                            val hasCustomFieldData = customFields.any { customField ->
                                getCustomFieldValue(leadDetail!!, customField.columnName).isNotBlank()
                            }

                            if (hasCustomFieldData) {
                                CustomFieldsCard(
                                    lead = leadDetail!!,
                                    customFields = customFields
                                )
                            }
                        }
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Lead not found",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Delete Confirmation Dialog
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    icon = {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    title = {
                        Text(
                            "Delete Lead",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Text(
                            "Are you sure you want to delete \"${leadDetail?.name}\"? This action cannot be undone.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                leadDetail?.let { viewModel.deleteLead(it) }
                                showDeleteDialog = false
                                onBackClick()
                            },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Delete", fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun LeadHeaderCard(lead: Lead) {
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
                text = lead.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.handshake),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lead.status,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun BasicInfoCard(lead: Lead) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Contact Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LeadDetailRow(
                label = "Mobile Number",
                value = lead.mobile,
                icon = Icons.Default.Phone
            )

            if (!lead.email.isNullOrBlank()) {
                LeadDetailRow(
                    label = "Email Address",
                    value = lead.email,
                    icon = Icons.Default.Email
                )
            }

            if (!lead.whatsappNumber.isNullOrBlank()) {
                LeadDetailRow(
                    label = "WhatsApp Number",
                    value = lead.whatsappNumber,
                    icon = ImageVector.vectorResource(R.drawable.whatsapp)
                )
            }
        }
    }
}

@Composable
private fun AssociatedContactCard(contact: com.techpuram.leadandfollowmanagement.domain.model.Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Associated Contact",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LeadDetailRow(
                label = "Contact Name",
                value = contact.name,
                icon = Icons.Default.Person
            )

            LeadDetailRow(
                label = "Contact Mobile",
                value = contact.mobile,
                icon = Icons.Default.Phone
            )

            if (!contact.email.isNullOrBlank()) {
                LeadDetailRow(
                    label = "Contact Email",
                    value = contact.email,
                    icon = Icons.Default.Email
                )
            }
        }
    }
}

@Composable
private fun StatusInfoCard(lead: Lead) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Lead Status Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!lead.leadSource.isNullOrBlank()) {
                LeadDetailRow(
                    label = "Lead Source",
                    value = lead.leadSource,
                    icon = ImageVector.vectorResource(R.drawable.source)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (lead.isConverted) Icons.Default.CheckCircle else ImageVector.vectorResource(R.drawable.radio_button),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (lead.isConverted) Color.Green else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Conversion Status",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (lead.isConverted) "Converted to Customer" else "Not Converted",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = if (lead.isConverted) Color.Green else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomFieldsCard(
    lead: Lead,
    customFields: List<com.techpuram.leadandfollowmanagement.domain.model.CustomField>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Additional Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            customFields.forEach { customField ->
                val value = getCustomFieldValue(lead, customField.columnName)
                if (value.isNotBlank()) {
                    LeadDetailRow(
                        label = customField.fieldName,
                        value = value,
                        icon = when (customField.fieldType) {
                            "NUMBER" -> ImageVector.vectorResource(R.drawable.numbers)
                            "DROPDOWN" -> Icons.Default.ArrowDropDown
                            else -> Icons.Default.Info
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LeadDetailRow(
    label: String,
    value: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

private fun getCustomFieldValue(lead: Lead, columnName: String): String {
    return when (columnName) {
        "cf1" -> lead.cf1 ?: ""
        "cf2" -> lead.cf2 ?: ""
        "cf3" -> lead.cf3 ?: ""
        "cf4" -> lead.cf4 ?: ""
        "cf5" -> lead.cf5 ?: ""
        "cf6" -> lead.cf6 ?: ""
        "cf7" -> lead.cf7 ?: ""
        "cf8" -> lead.cf8 ?: ""
        "cf9" -> lead.cf9 ?: ""
        "cf10" -> lead.cf10 ?: ""
        "cf11" -> lead.cf11 ?: ""
        "cf12" -> lead.cf12 ?: ""
        "cf13" -> lead.cf13 ?: ""
        "cf14" -> lead.cf14 ?: ""
        "cf15" -> lead.cf15 ?: ""
        "cf16" -> lead.cf16 ?: ""
        "cf17" -> lead.cf17 ?: ""
        "cf18" -> lead.cf18 ?: ""
        "cf19" -> lead.cf19 ?: ""
        "cf20" -> lead.cf20 ?: ""
        else -> ""
    }
}