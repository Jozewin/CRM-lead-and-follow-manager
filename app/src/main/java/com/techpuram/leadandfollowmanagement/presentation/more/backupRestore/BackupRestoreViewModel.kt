// app/src/main/java/com/techpuram/leadandfollowmanagement/presentation/more/backup_restore/BackupRestoreViewModel.kt
package com.techpuram.leadandfollowmanagement.presentation.more.backupRestore
import android.content.Intent
import android.content.IntentSender
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techpuram.leadandfollowmanagement.domain.repository.AuthRepository
import com.techpuram.leadandfollowmanagement.domain.repository.BackupRestoreRepository
import com.techpuram.leadandfollowmanagement.util.ZipBackupManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupRestoreViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val backupRestoreRepository: BackupRestoreRepository,
    private val zipBackupManager: ZipBackupManager
) : ViewModel()
{

    private val TAG = "BackupRestoreViewModel"

    private val _state = MutableStateFlow(BackupRestoreState())
    val state = _state.asStateFlow()

    private val _effect = MutableStateFlow<MainEffect?>(null)
    val effect = _effect.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            authRepository.observeUserStatus().collect { user ->
                _state.update {
                    it.copy(
                        email = user?.email,
                        isSignedIn = user != null
                    )
                }

                // If user is signed in, get the list of available backups
                if (user != null) {
                    loadDriveBackups()
                }
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    fun onEvent(event: BackupRestoreEvent) {
        when (event) {
            is BackupRestoreEvent.Backup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }

                        backupRestoreRepository.backupDatabaseToDrive()
                        .onSuccess { fileId ->
                            _state.update {
                                it.copy(
                                    message = "Backup successful!",
                                    isLoading = false
                                )
                            }
                            // Reload backups after successful backup
                            loadDriveBackups()
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Backup failed", error)
                            _state.update {
                                it.copy(
                                    error = "Backup failed: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            }

            is BackupRestoreEvent.Restore -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }

                    backupRestoreRepository.restoreDatabaseFromDrive(event.fileId)
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    message = "Restore successful!",
                                    isLoading = false
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Restore failed", error)
                            _state.update {
                                it.copy(
                                    error = "Restore failed: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            }

            BackupRestoreEvent.GetFiles -> {
                loadDriveBackups()
            }

            BackupRestoreEvent.SignInGoogle -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        // First, get the sign-in intent sender
                        val signInIntentSender = authRepository.signInGoogle()
                        // Emit the sign-in effect with the intent sender
                        _effect.emit(MainEffect.SignIn(signInIntentSender))
                    } catch (e: Exception) {
                        Log.e(TAG, "Sign in failed", e)
                        _state.update {
                            it.copy(
                                error = "Sign in failed: ${e.message}"
                            )
                        }
                    }
                }
            }

            BackupRestoreEvent.SignOut -> {
                viewModelScope.launch(Dispatchers.IO) {
                    authRepository.signOut()
                    _state.update {
                        it.copy(
                            restoreFiles = emptyList()
                        )
                    }
                }
            }

            is BackupRestoreEvent.OnAuthorize -> {
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        authRepository.authorizeGoogleDriveResult(event.intent)
                        loadDriveBackups()
                    } catch (e: Exception) {
                        Log.e(TAG, "Authorization failed", e)
                        _state.update {
                            it.copy(
                                error = "Authorization failed: ${e.message}"
                            )
                        }
                    }
                }
            }

            is BackupRestoreEvent.OnSignInResult -> {
                viewModelScope.launch(Dispatchers.IO) {
                    onSignInResult(event.intent)
                }
            }

            BackupRestoreEvent.LocalBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }

                    backupRestoreRepository.backupDatabase()
                        .onSuccess { filePath ->
                            _state.update {
                                it.copy(
                                    message = "Local backup successful! File saved at: $filePath",
                                    isLoading = false
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Local backup failed", error)
                            _state.update {
                                it.copy(
                                    error = "Local backup failed: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            }

            BackupRestoreEvent.LocalRestore -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }

                    backupRestoreRepository.restoreDatabase()
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    message = "Local restore successful!",
                                    isLoading = false
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "Local restore failed", error)
                            _state.update {
                                it.copy(
                                    error = "Local restore failed: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            }

            BackupRestoreEvent.CreateZipBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }

                    zipBackupManager.createZipBackup()
                        .onSuccess { zipFile ->
                            _state.update {
                                it.copy(
                                    message = "ZIP backup created successfully at: ${zipFile.absolutePath}",
                                    isLoading = false
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "ZIP backup creation failed", error)
                            _state.update {
                                it.copy(
                                    error = "ZIP backup failed: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            }

            BackupRestoreEvent.ShareZipBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }

                    zipBackupManager.exportAndShare()
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    message = "ZIP backup created and shared successfully!",
                                    isLoading = false
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "ZIP backup share failed", error)
                            _state.update {
                                it.copy(
                                    error = "ZIP backup share failed: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            }

            is BackupRestoreEvent.ImportZipBackup -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update { it.copy(isLoading = true) }

                    zipBackupManager.importFromZip(event.fileUri)
                        .onSuccess { message ->
                            _state.update {
                                it.copy(
                                    message = "Import successful! $message",
                                    isLoading = false
                                )
                            }
                        }
                        .onFailure { error ->
                            Log.e(TAG, "ZIP import failed", error)
                            _state.update {
                                it.copy(
                                    error = "ZIP import failed: ${error.message}",
                                    isLoading = false
                                )
                            }
                        }
                }
            }
        }
    }

    private fun loadDriveBackups() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }

            backupRestoreRepository.getAvailableDriveBackups()
                .onSuccess { backups ->
                    _state.update {
                        it.copy(
                            restoreFiles = backups,
                            isLoading = false
                        )
                    }
                }
                .onFailure { error ->
                    Log.e(TAG, "Failed to load backups", error)
                    _state.update {
                        it.copy(
                            error = "Failed to load backups: ${error.message}",
                            isLoading = false
                        )
                    }
                }
        }
    }


    private suspend fun onSignInResult(intent: Intent) {
        try {
            val getResult = authRepository.getSignInResult(intent)
            _state.update {
                it.copy(
                    email = getResult.email,
                    isSignedIn = true
                )
            }

            // Now that we're signed in, request Drive authorization
            try {
                val authorizeGoogleDrive = authRepository.authorizeGoogleDrive()
                if (authorizeGoogleDrive.hasResolution()) {
                    Log.d(TAG, "Drive authorization needs resolution, sending intent")
                    _effect.emit(MainEffect.Authorize(authorizeGoogleDrive.pendingIntent!!.intentSender))
                } else {
                    Log.d(TAG, "No Drive authorization resolution needed, loading backups")
                    // If no resolution needed, we can immediately load backups
                    loadDriveBackups()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Drive authorization request failed", e)
                _state.update {
                    it.copy(
                        error = "Drive authorization failed: ${e.message}"
                    )
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sign in result processing failed", e)
            _state.update {
                it.copy(
                    error = "Sign in processing failed: ${e.message}"
                )
            }
        }
    }
}
sealed class MainEffect {
    data class SignIn(val intentSender: IntentSender) : MainEffect()
    data class Authorize(val intentSender: IntentSender) : MainEffect()
    data class ShowMessage(val message: String) : MainEffect()
}