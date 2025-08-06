package com.techpuram.leadandfollowmanagement.data.repository

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.http.FileContent
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.techpuram.leadandfollowmanagement.domain.model.DriveFileInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleDriveService @Inject constructor() {
    
    private val TAG = "GoogleDriveService"
    
    /**
     * Upload a file to Google Drive's appDataFolder
     * @param drive The Drive service instance
     * @param filePath Local file path to upload
     * @param fileName Name to give the file in Drive
     * @return The file ID if successful, null otherwise
     */
    suspend fun uploadFileToDrive(drive: Drive, filePath: String, fileName: String): String? = withContext(Dispatchers.IO) {
        try {
            val fileMetadata = File()
            fileMetadata.setName(fileName)
            fileMetadata.setParents(listOf("appDataFolder"))

            val localFile = java.io.File(filePath)
            val mediaContent = FileContent("application/octet-stream", localFile)

            // Create the request but don't execute it yet
            val request = drive.files().create(fileMetadata, mediaContent)
                .setFields("id")

            // Properly execute the request in a way that's safe for coroutines
            val uploadedFile = withContext(Dispatchers.IO) {
                try {
                    request.execute()
                } catch (e: Exception) {
                    Log.e(TAG, "Exception during Drive API execute", e)
                    if (e is UserRecoverableAuthIOException) {
                        // Rethrow this specific exception so it can be handled at the ViewModel level
                        
                        throw e
                    }
                    null
                }
            }

            if (uploadedFile != null) {
                Log.d(TAG, "File uploaded to Drive with ID: ${uploadedFile.id}")
                return@withContext uploadedFile.id
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to upload file to Drive", e)
            // Rethrow the exception if it's a UserRecoverableAuthIOException
            if (e is UserRecoverableAuthIOException) {
                throw e
            }
            return@withContext null
        }
    }
    /**
     * List backup files available in the appDataFolder
     * @param drive The Drive service instance
     * @return List of DriveFileInfo objects containing file info
     */
    suspend fun listBackupFiles(drive: Drive): List<DriveFileInfo> = withContext(Dispatchers.IO) {
        try {
            val result = drive.files().list()
                .setSpaces("appDataFolder")
                .setFields("files(id, name, modifiedTime, size)")
                .execute()
                
            return@withContext result.files.map { file ->
                DriveFileInfo(
                    id = file.id,
                    name = file.name,
                    modifiedTime = file.modifiedTime?.value ?: 0L,
                    size = file.getSize()?.toLong() ?: 0L
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to list backup files from Drive", e)
            return@withContext emptyList<DriveFileInfo>()
        }
    }
    
    /**
     * Download a file from Google Drive to local storage
     * @param drive The Drive service instance
     * @param fileId The ID of the file to download
     * @param destinationPath The local path where the file should be saved
     * @return True if download was successful, false otherwise
     */
    suspend fun downloadFileFromDrive(drive: Drive, fileId: String, destinationPath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val outputStream = FileOutputStream(java.io.File(destinationPath))
            
            drive.files().get(fileId)
                .executeMediaAndDownloadTo(outputStream)
                
            outputStream.close()
            Log.d(TAG, "File downloaded from Drive successfully")
            return@withContext true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to download file from Drive", e)
            return@withContext false
        }
    }
    
    /**
     * Delete a file from Google Drive
     * @param drive The Drive service instance
     * @param fileId The ID of the file to delete
     * @return True if deletion was successful, false otherwise
     */
    suspend fun deleteFileFromDrive(drive: Drive, fileId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            drive.files().delete(fileId).execute()
            Log.d(TAG, "File deleted from Drive successfully")
            return@withContext true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete file from Drive", e)
            return@withContext false
        }
    }
}

/**
 * Data class to hold information about files stored in Google Drive
 */
