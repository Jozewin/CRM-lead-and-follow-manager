package com.techpuram.leadandfollowmanagement.domain.repository

import com.techpuram.leadandfollowmanagement.domain.model.DriveFileInfo


interface BackupRestoreRepository {
    // Local backup/restore
    suspend fun backupDatabase(): Result<String>
    suspend fun restoreDatabase(): Result<Unit>

    // Google Drive backup/restore
    suspend fun backupDatabaseToDrive(): Result<String>
    suspend fun restoreDatabaseFromDrive(fileId: String): Result<Unit>
    suspend fun getAvailableDriveBackups(): Result<List<DriveFileInfo>>
}