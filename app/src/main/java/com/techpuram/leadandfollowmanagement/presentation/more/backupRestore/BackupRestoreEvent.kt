// app/src/main/java/com/techpuram/leadandfollowmanagement/presentation/more/backup_restore/BackupRestoreEvent.kt
package com.techpuram.leadandfollowmanagement.presentation.more.backupRestore

import android.content.Intent
import android.net.Uri

sealed class BackupRestoreEvent {
    data object SignInGoogle : BackupRestoreEvent()
    data object SignOut : BackupRestoreEvent()

    // Local backup/restore
    data object LocalBackup : BackupRestoreEvent()
    data object LocalRestore : BackupRestoreEvent()

    // ZIP backup/share/import
    data object CreateZipBackup : BackupRestoreEvent()
    data object ShareZipBackup : BackupRestoreEvent()
    data class ImportZipBackup(val fileUri: Uri) : BackupRestoreEvent()

    // Google Drive backup/restore
    data class Backup(val imageUri: Uri) : BackupRestoreEvent()
    data class Restore(val fileId: String) : BackupRestoreEvent()

    data class OnSignInResult(val intent: Intent) : BackupRestoreEvent()
    data class OnAuthorize(val intent: Intent) : BackupRestoreEvent()

    data object GetFiles : BackupRestoreEvent()
}