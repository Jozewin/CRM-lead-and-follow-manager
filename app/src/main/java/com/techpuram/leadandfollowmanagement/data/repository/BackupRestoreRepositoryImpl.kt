// app/src/main/java/com/techpuram/leadandfollowmanagement/data/repository/BackupRestoreRepositoryImpl.kt
package com.techpuram.leadandfollowmanagement.data.repository
import android.content.Context
import android.util.Log
import com.techpuram.leadandfollowmanagement.data.local.AppDatabase
import com.techpuram.leadandfollowmanagement.domain.repository.AuthRepository
import com.techpuram.leadandfollowmanagement.domain.repository.BackupRestoreRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import com.techpuram.leadandfollowmanagement.domain.model.DriveFileInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupRestoreRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: AppDatabase,
    private val authRepository: AuthRepository,
    private val googleDriveService: GoogleDriveService
) : BackupRestoreRepository
{

    private val TAG = "BackupRestoreRepository"

    override suspend fun backupDatabase(): Result<String> = runCatching {
        val dbFile = context.getDatabasePath("app_database")
        val backupDir = context.getExternalFilesDir("Backups")
        if (backupDir != null && !backupDir.exists()) {
            backupDir.mkdirs()
        }

        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val backupFile = File(backupDir, "backup_$timestamp.db")

        // Close the database before copying
        database.close()

        // Copy the database file
        dbFile.copyTo(backupFile, overwrite = true)

        // Reopen the database
        database.openHelper.writableDatabase

        backupFile.absolutePath
    }

    override suspend fun restoreDatabase(): Result<Unit> = runCatching {
        val backupDir = context.getExternalFilesDir("Backups")
        if (backupDir == null || !backupDir.exists()) {
            throw Exception("Backup directory not found")
        }

        // Get the most recent backup file
        val backupFiles = backupDir.listFiles { file ->
            file.name.startsWith("backup_") && file.name.endsWith(".db")
        }?.sortedByDescending { it.lastModified() }

        val backupFile = backupFiles?.firstOrNull()
            ?: throw Exception("No backup file found")

        val dbFile = context.getDatabasePath("app_database")

        // Close the database before copying
        database.close()

        // Copy the backup file to the database location
        backupFile.copyTo(dbFile, overwrite = true)

        // Reopen the database
        database.openHelper.writableDatabase
    }

    override suspend fun backupDatabaseToDrive(): Result<String> = runCatching {
        // First, perform a local backup
        val localBackupPath = backupDatabase().getOrThrow()

        // Get the Drive service
        val driveService = authRepository.getGoogleDrive()
            ?: throw Exception("Failed to get Google Drive service. User may not be authenticated.")

        // Upload the backup file to Drive
        val backupFileName = File(localBackupPath).name
        val fileId = googleDriveService.uploadFileToDrive(
            drive = driveService,
            filePath = localBackupPath,
            fileName = backupFileName
        ) ?: throw Exception("Failed to upload backup to Google Drive $localBackupPath $backupFileName")

        fileId
    }

    override suspend fun getAvailableDriveBackups(): Result<List<DriveFileInfo>> = runCatching {
        // Get the Drive service
        val driveService = authRepository.getGoogleDrive()
            ?: throw Exception("Failed to get Google Drive service. User may not be authenticated.")

        // List all backup files from Drive
        googleDriveService.listBackupFiles(driveService)
    }

    override suspend fun restoreDatabaseFromDrive(fileId: String): Result<Unit> = runCatching {
        // Get the Drive service
        val driveService = authRepository.getGoogleDrive()
            ?: throw Exception("Failed to get Google Drive service. User may not be authenticated.")

        // Create a temporary file to store the downloaded backup
        val backupDir = context.getExternalFilesDir("Backups")
        if (backupDir != null && !backupDir.exists()) {
            backupDir.mkdirs()
        }
        val tempBackupFile = File(backupDir, "temp_restore_backup.db")

        // Download the file from Google Drive
        val downloadSuccessful = googleDriveService.downloadFileFromDrive(
            drive = driveService,
            fileId = fileId,
            destinationPath = tempBackupFile.absolutePath
        )

        if (!downloadSuccessful) {
            throw Exception("Failed to download backup from Google Drive")
        }

        val dbFile = context.getDatabasePath("app_database")

        // Close the database before copying
        database.close()

        // Copy the downloaded backup file to the database location
        tempBackupFile.copyTo(dbFile, overwrite = true)

        // Reopen the database
        database.openHelper.writableDatabase

        // Delete the temporary file
        tempBackupFile.delete()
    }
}