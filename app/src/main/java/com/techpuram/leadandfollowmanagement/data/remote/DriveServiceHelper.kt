package com.techpuram.leadandfollowmanagement.data.remote

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.services.drive.model.File
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.getValue

class DriveServiceHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
     val driveService by lazy {
        GoogleSignIn.getLastSignedInAccount(context)?.let { account ->
            Drive.Builder(
                NetHttpTransport(),
                GsonFactory(),
                GoogleAccountCredential.usingOAuth2(
                    context,
                    listOf(DriveScopes.DRIVE_FILE)
                ).setSelectedAccount(account.account)
            )
                .setApplicationName("Lead and Follow Management")
                .build()
        }
    }

    suspend fun getLatestBackupFile(): File? {
        return driveService?.files()?.list()
            ?.setQ("mimeType='application/x-sqlite3' and name contains 'backup_'")
            ?.setOrderBy("createdTime desc")
            ?.setPageSize(1)
            ?.execute()
            ?.files
            ?.firstOrNull()
    }

    suspend fun downloadFile(fileId: String, destinationFile: java.io.File) {
        driveService?.files()?.get(fileId)
            ?.executeMediaAndDownloadTo(destinationFile.outputStream())
    }
} 