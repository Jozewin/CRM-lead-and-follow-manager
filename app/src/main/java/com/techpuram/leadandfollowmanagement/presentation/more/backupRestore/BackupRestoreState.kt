package com.techpuram.leadandfollowmanagement.presentation.more.backupRestore

import com.techpuram.leadandfollowmanagement.domain.model.DriveFileInfo


data class BackupRestoreState(
    val email: String? = null,
    val isSignedIn: Boolean = false,
    val isLoading: Boolean = false,
    val restoreFiles: List<DriveFileInfo> = emptyList(),
    val error: String? = null,
    val message: String? = null
)