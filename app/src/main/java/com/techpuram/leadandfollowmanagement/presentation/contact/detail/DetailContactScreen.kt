package com.techpuram.leadandfollowmanagement.presentation.contact.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.rememberAsyncImagePainter
import com.techpuram.leadandfollowmanagement.R
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailsScreen(
    contactId: Contact,
    onBackClick: () -> Unit,
    viewModel: DetailContactViewModel = hiltViewModel(),
    onEditClick: (Contact) -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    val contact by viewModel.contact.collectAsState()
    val customFields by viewModel.customFields.collectAsState()

    LaunchedEffect(contactId.id) {
        viewModel.getContactId(contactId.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Contact Details",
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
                    if (contact != null) {
                        IconButton(onClick = { onEditClick(contact!!) }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit contact"
                            )
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete contact"
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
                contact == null -> {
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
                            text = "Loading contact details...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                contact != null -> {
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
                            // Contact Header Card
                            ContactHeaderCard(contact = contact!!)

                            // Contact Information Card
                            ContactInformationCard(contact = contact!!)

                            // Address Card
                            if (hasAddressInfo(contact!!)) {
                                AddressCard(contact = contact!!)
                            }

                            // Company Information Card
                            if (!contact!!.companyName.isNullOrBlank()) {
                                CompanyInfoCard(companyName = contact!!.companyName!!)
                            }

                            // Custom Fields Card
                            if (customFields.isNotEmpty() && hasCustomFieldData(contact!!, customFields)) {
                                CustomFieldsCard(contact = contact!!, customFields = customFields)
                            }

                            // Notes Card
                            if (!contact!!.note.isNullOrBlank()) {
                                NotesCard(notes = contact!!.note!!)
                            }
                        }
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
                            "Delete Contact",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    text = {
                        Text(
                            "Are you sure you want to delete \"${contact?.name}\"? This action cannot be undone.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteContact(contact!!)
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
private fun ContactHeaderCard(contact: Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Contact photo
            if (contact.photoId != null) {
                val file = File(contact.photoId)
                if (file.exists()) {
                    Image(
                        painter = rememberAsyncImagePainter(file),
                        contentDescription = "Contact Photo",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    ContactInitialCircle(contact.name)
                }
            } else {
                ContactInitialCircle(contact.name)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = contact.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (!contact.companyName.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contact.companyName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}

@Composable
private fun ContactInitialCircle(name: String) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.first().toString().uppercase(),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun ContactInformationCard(contact: Contact) {
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

            // Primary Mobile
            ContactDetailRow(
                label = "Mobile",
                value = contact.mobile,
                icon = Icons.Default.Phone
            )

            // Email
            if (!contact.email.isNullOrBlank()) {
                ContactDetailRow(
                    label = "Email",
                    value = contact.email,
                    icon = Icons.Default.Email
                )
            }

            // WhatsApp
            if (!contact.whatsappNumber.isNullOrBlank()) {
                ContactDetailRow(
                    label = "WhatsApp",
                    value = contact.whatsappNumber,
                    icon = ImageVector.vectorResource(R.drawable.chat_outline)
                )
            }

            // Additional Mobile
            if (!contact.additionalMobile.isNullOrBlank()) {
                ContactDetailRow(
                    label = "Additional Mobile",
                    value = contact.additionalMobile,
                    icon = Icons.Default.Phone
                )
            }
        }
    }
}

@Composable
private fun AddressCard(contact: Contact) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Address",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Street
            if (!contact.street.isNullOrBlank()) {
                ContactDetailRow(
                    label = "Street",
                    value = contact.street,
                    icon = Icons.Default.LocationOn
                )
            }

            // City
            if (!contact.city.isNullOrBlank()) {
                ContactDetailRow(
                    label = "City",
                    value = contact.city,
                    icon = ImageVector.vectorResource(R.drawable.location_city)
                )
            }

            // State
            if (!contact.state.isNullOrBlank()) {
                ContactDetailRow(
                    label = "State",
                    value = contact.state,
                    icon = ImageVector.vectorResource(R.drawable.map)
                )
            }

            // Country
            if (!contact.country.isNullOrBlank()) {
                ContactDetailRow(
                    label = "Country",
                    value = contact.country,
                    icon = ImageVector.vectorResource(R.drawable.publicc)
                )
            }

            // Zip
            if (!contact.zip.isNullOrBlank()) {
                ContactDetailRow(
                    label = "Zip Code",
                    value = contact.zip,
                    icon = ImageVector.vectorResource(R.drawable.pin_drop)
                )
            }
        }
    }
}

@Composable
private fun CompanyInfoCard(companyName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Company Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ContactDetailRow(
                label = "Company",
                value = companyName,
                icon = ImageVector.vectorResource(R.drawable.business)
            )
        }
    }
}

@Composable
private fun CustomFieldsCard(contact: Contact, customFields: List<com.techpuram.leadandfollowmanagement.domain.model.CustomField>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Custom Fields",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            customFields.forEach { customField ->
                val value = getCustomFieldValue(contact, customField.columnName)
                if (!value.isNullOrBlank()) {
                    ContactDetailRow(
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
private fun NotesCard(notes: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.note),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4
                )
            }
        }
    }
}

@Composable
private fun ContactDetailRow(
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

// Helper functions
private fun hasAddressInfo(contact: Contact): Boolean {
    return !contact.street.isNullOrBlank() ||
            !contact.city.isNullOrBlank() ||
            !contact.state.isNullOrBlank() ||
            !contact.country.isNullOrBlank() ||
            !contact.zip.isNullOrBlank()
}

private fun hasCustomFieldData(contact: Contact, customFields: List<com.techpuram.leadandfollowmanagement.domain.model.CustomField>): Boolean {
    return customFields.any { customField ->
        val value = getCustomFieldValue(contact, customField.columnName)
        !value.isNullOrBlank()
    }
}

private fun getCustomFieldValue(contact: Contact, columnName: String): String? {
    return when (columnName) {
        "cf1" -> contact.cf1
        "cf2" -> contact.cf2
        "cf3" -> contact.cf3
        "cf4" -> contact.cf4
        "cf5" -> contact.cf5
        "cf6" -> contact.cf6
        "cf7" -> contact.cf7
        "cf8" -> contact.cf8
        "cf9" -> contact.cf9
        "cf10" -> contact.cf10
        "cf11" -> contact.cf11
        "cf12" -> contact.cf12
        "cf13" -> contact.cf13
        "cf14" -> contact.cf14
        "cf15" -> contact.cf15
        "cf16" -> contact.cf16
        "cf17" -> contact.cf17
        "cf18" -> contact.cf18
        "cf19" -> contact.cf19
        "cf20" -> contact.cf20
        else -> null
    }
}