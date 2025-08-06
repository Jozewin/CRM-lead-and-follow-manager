// app/src/main/java/com/techpuram/leadandfollowmanagement/presentation/more/backup_restore/BackupRestoreScreen.kt
package com.techpuram.leadandfollowmanagement.presentation.more.backupRestore
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.techpuram.leadandfollowmanagement.domain.model.DriveFileInfo
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.R)
@Composable
fun BackupRestoreScreen(
    viewModel: BackupRestoreViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    ) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(state.message) {
        if (state.message?.contains("Backup successful!") == true
            || state.message?.contains("Restore successful!") == true) {

            Toast.makeText(
                context,
                "Operation successful! App will restart in 3 seconds...",
                Toast.LENGTH_SHORT
            ).show()

            // Delay for 2 seconds before restarting
            kotlinx.coroutines.delay(3000)

            val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)

            exitProcess(0)
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        Log.d("BackupRestore", "Google Sign In result: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                viewModel.onEvent(BackupRestoreEvent.OnSignInResult(intent))
            }
        } else {
            Log.d("BackupRestore", "Sign in canceled or failed")
            Toast.makeText(context, "Sign in was canceled", Toast.LENGTH_SHORT).show()
        }
    }

    val googleDriveAuthorizationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        Log.d("BackupRestore", "Google Drive Auth result: ${result.resultCode}")
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                viewModel.onEvent(BackupRestoreEvent.OnAuthorize(intent))
            }
        } else {
            Log.d("BackupRestore", "Drive authorization canceled or failed")
            Toast.makeText(context, "Drive authorization was canceled", Toast.LENGTH_SHORT).show()
        }
    }
    
    // File picker launcher for importing ZIP backups
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.onEvent(BackupRestoreEvent.ImportZipBackup(it))
        }
    }
    // Handle one-time effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is MainEffect.ShowMessage -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                }
                is MainEffect.SignIn -> {
                    Log.d("BackupRestore", "SignIn effect received in collectLatest")
                    val intentSenderRequest = IntentSenderRequest.Builder(effect.intentSender).build()
                    googleSignInLauncher.launch(intentSenderRequest)
                }
                is MainEffect.Authorize -> {
                    Log.d("BackupRestore", "Authorize effect received in collectLatest")
                    val intentSenderRequest = IntentSenderRequest.Builder(effect.intentSender).build()
                    googleDriveAuthorizationLauncher.launch(intentSenderRequest)
                }
                null -> {
                    // Do nothing for null effect
                }
            }
        }
    }

    // Google Sign In launcher


    // Google Drive Authorization launcher


    // Dialog state for confirmation
    var showRestoreDialog by remember { mutableStateOf(false) }
    var selectedBackup by remember { mutableStateOf<DriveFileInfo?>(null) }

    // Show error toast if there's an error
    state.error?.let { error ->
        LaunchedEffect(key1 = error) {
            Log.e("BackupRestore", "Error: $error")
            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
        }
    }

    // Show success message if there's a message
    state.message?.let { message ->
        LaunchedEffect(key1 = message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // Main content
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Backup & Restore") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Google Account section
                GoogleAccountSection(
                    isSignedIn = state.isSignedIn,
                    email = state.email,
                    onSignInClick = {
                        Log.d("BackupRestore", "Sign In button clicked")
                        viewModel.onEvent(BackupRestoreEvent.SignInGoogle)

                    },
                    onSignOutClick = {
                        Log.d("BackupRestore", "Sign Out button clicked")
                        viewModel.onEvent(BackupRestoreEvent.SignOut)
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ZIP Backup & Share Section
                ZipBackupSection(
                    onCreateBackupClick = { viewModel.onEvent(BackupRestoreEvent.CreateZipBackup) },
                    onShareBackupClick = { viewModel.onEvent(BackupRestoreEvent.ShareZipBackup) },
                    onImportBackupClick = { filePickerLauncher.launch("application/zip") }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Local Backup/Restore Section
                LocalBackupSection(
                    onBackupClick = { viewModel.onEvent(BackupRestoreEvent.LocalBackup) },
                    onRestoreClick = { viewModel.onEvent(BackupRestoreEvent.LocalRestore) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Google Drive Backup/Restore Section
                if (state.isSignedIn) {
                    GoogleDriveBackupSection(
                        backupFiles = state.restoreFiles,
                        onBackupClick = { viewModel.onEvent(BackupRestoreEvent.Backup(android.net.Uri.EMPTY)) },
                        onRefreshClick = { viewModel.onEvent(BackupRestoreEvent.GetFiles) },
                        onBackupItemClick = { fileInfo ->
                            selectedBackup = fileInfo
                            showRestoreDialog = true
                        }
                    )
                }
            }

            // Loading indicator
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }

    // Restore confirmation dialog
    if (showRestoreDialog && selectedBackup != null) {
        RestoreConfirmationDialog(
            fileInfo = selectedBackup!!,
            onConfirm = {
                viewModel.onEvent(BackupRestoreEvent.Restore(selectedBackup!!.id))
                showRestoreDialog = false
                selectedBackup = null
            },
            onDismiss = {
                showRestoreDialog = false
                selectedBackup = null
            }
        )
    }
}

@Composable
fun GoogleAccountSection(
    isSignedIn: Boolean,
    email: String?,
    onSignInClick: () -> Unit,
    onSignOutClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Google Account",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSignedIn) "Signed in as: $email" else "Not signed in",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isSignedIn) {
                Button(
                    onClick = onSignOutClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign Out")
                }
            } else {
                Button(
                    onClick = onSignInClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Sign In with Google")
                }
            }
        }
    }
}

@Composable
fun ZipBackupSection(
    onCreateBackupClick: () -> Unit,
    onShareBackupClick: () -> Unit,
    onImportBackupClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ZIP Backup & Share",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Create a ZIP backup and share via WhatsApp, email, or other apps",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Share backup button (prominent)
            Button(
                onClick = onShareBackupClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create & Share Backup")
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // Secondary actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onCreateBackupClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Create Only", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onImportBackupClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Import", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun LocalBackupSection(
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Local Backup & Restore",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onBackupClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Backup Locally")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onRestoreClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Restore Locally")
                }
            }
        }
    }
}

@Composable
fun GoogleDriveBackupSection(
    backupFiles: List<DriveFileInfo>,
    onBackupClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onBackupItemClick: (DriveFileInfo) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Google Drive Backup",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = onRefreshClick) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onBackupClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Backup to Google Drive")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Available Backups",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (backupFiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No backups available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(backupFiles) { fileInfo ->
                        DriveBackupItem(
                            fileInfo = fileInfo,
                            onClick = { onBackupItemClick(fileInfo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DriveBackupItem(
    fileInfo: DriveFileInfo,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(fileInfo.modifiedTime))

    val fileSize = when {
        fileInfo.size < 1024 -> "${fileInfo.size} B"
        fileInfo.size < 1024 * 1024 -> "${fileInfo.size / 1024} KB"
        else -> "${fileInfo.size / (1024 * 1024)} MB"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = fileInfo.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = fileSize,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RestoreConfirmationDialog(
    fileInfo: DriveFileInfo,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(fileInfo.modifiedTime))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Restore Backup") },
        text = {
            Text("Are you sure you want to restore from the backup:\n${fileInfo.name}\nCreated on: $formattedDate?\n\nThis will replace your current data.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Restore")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}