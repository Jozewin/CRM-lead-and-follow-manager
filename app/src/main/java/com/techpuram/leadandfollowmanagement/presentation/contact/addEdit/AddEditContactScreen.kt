package com.techpuram.leadandfollowmanagement.presentation.contact.addEdit

import android.annotation.SuppressLint
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import coil3.compose.rememberAsyncImagePainter
import com.techpuram.leadandfollowmanagement.R
import java.io.File

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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrUpdateContactScreen(
    viewModel: AddEditContactViewModel = hiltViewModel(),
    onContactSaved: (Long?) -> Unit
) {
    val contactState by viewModel.contactState.collectAsState()
    val customFields by viewModel.customFields.collectAsState()
    val saveComplete by viewModel.saveComplete.collectAsState()

    // Handle save completion
    LaunchedEffect(saveComplete) {
        if (saveComplete != null) {
            onContactSaved(saveComplete)
        }
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                selectedImageUri = it
                viewModel.onEvent(AddEditContactEvent.PickedPhoto(it.toString()))
            }
        }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (contactState.id == null) "New Contact" else "Edit Contact",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onContactSaved(null) }) {
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
                // Profile Picture Section
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .shadow(8.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { imagePickerLauncher.launch("image/*") }
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    if (contactState.photoUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(
                                model = if (contactState.photoUri!!.startsWith("content://")) {
                                    Uri.parse(contactState.photoUri)
                                } else {
                                    contactState.photoUri?.let { File(it) }
                                }
                            ),
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                painterResource(R.drawable.photo_camera),
                                contentDescription = "Add Photo",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Add Photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
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
                            value = contactState.name,
                            onValueChange = {
                                viewModel.onEvent(AddEditContactEvent.EnteredName(it))
                            },
                            label = { LabelWithRedAsterisk("Name*") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Mobile
                        OutlinedTextField(
                            value = contactState.mobile,
                            onValueChange = { viewModel.onEvent(AddEditContactEvent.EnteredMobile(it)) },
                            label = { LabelWithRedAsterisk("Mobile*") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Email
                        OutlinedTextField(
                            value = contactState.email.orEmpty(),
                            onValueChange = { viewModel.onEvent(AddEditContactEvent.EnteredEmail(it)) },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Company
                        OutlinedTextField(
                            value = contactState.companyName.orEmpty(),
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditContactEvent.EnteredCompanyName(
                                        it
                                    )
                                )
                            },
                            label = { Text("Company") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // WhatsApp
                        OutlinedTextField(
                            value = contactState.whatsappNumber.orEmpty(),
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditContactEvent.EnteredWhatsappNumber(
                                        it
                                    )
                                )
                            },
                            label = { Text("WhatsApp") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
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
                            text = "Address",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = contactState.street.orEmpty(),
                            onValueChange = { viewModel.onEvent(AddEditContactEvent.EnteredStreet(it)) },
                            label = { Text("Street") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // City
                        OutlinedTextField(
                            value = contactState.city.orEmpty(),
                            onValueChange = { viewModel.onEvent(AddEditContactEvent.EnteredCity(it)) },
                            label = { Text("City") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // State
                        OutlinedTextField(
                            value = contactState.state.orEmpty(),
                            onValueChange = { viewModel.onEvent(AddEditContactEvent.EnteredState(it)) },
                            label = { Text("State") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Sentences,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Country
                        OutlinedTextField(
                            value = contactState.country.orEmpty(),
                            onValueChange = {
                                viewModel.onEvent(
                                    AddEditContactEvent.EnteredCountry(
                                        it
                                    )
                                )
                            },
                            label = { Text("Country") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Zip
                        OutlinedTextField(
                            value = contactState.zip.orEmpty(),
                            onValueChange = { viewModel.onEvent(AddEditContactEvent.EnteredZip(it)) },
                            label = { Text("Zip") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Done
                            ),
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Note
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
                            text = "Additional Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = contactState.note.orEmpty(),
                            onValueChange = { viewModel.onEvent(AddEditContactEvent.EnteredNote(it)) },
                            label = { Text("Note") },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                capitalization = KeyboardCapitalization.Words
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )
                    }
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
                                val value =
                                    viewModel.getCustomFieldValue(customField.columnName) ?: ""

                                when (customField.fieldType) {
                                    "TEXT" -> {
                                        EnhancedContactTextField(
                                            label = customField.fieldName,
                                            value = value,
                                            onValueChange = { newValue ->
                                                viewModel.onCustomFieldValueChanged(
                                                    customField.columnName,
                                                    newValue
                                                )
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                                        )
                                    }

                                    "NUMBER" -> {
                                        EnhancedContactTextField(
                                            label = customField.fieldName,
                                            value = value,
                                            onValueChange = { newValue ->
                                                if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                                    viewModel.onCustomFieldValueChanged(
                                                        customField.columnName,
                                                        newValue
                                                    )
                                                }
                                            },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                        )
                                    }

                                    "DROPDOWN" -> {
                                        EnhancedContactTextField(
                                            label = customField.fieldName,
                                            value = value,
                                            onValueChange = { newValue ->
                                                viewModel.onCustomFieldValueChanged(
                                                    customField.columnName,
                                                    newValue
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
            }

            Button(
                onClick = {
                    viewModel.onEvent(AddEditContactEvent.SaveContact)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Save Contact"
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedContactTextField(
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
                maxLines = 1,
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