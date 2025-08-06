    package com.techpuram.leadandfollowmanagement.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_log")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val module: String,  // 'Contact', 'Lead', 'Deal', 'FollowUp'
    val recordId: Int,  // ID of the modified record
    val action: String,  // 'CREATE', 'UPDATE', 'DELETE'
    val changedData: String?,  // JSON format of changed fields
    val performedTime: Long = System.currentTimeMillis()  // Timestamp for the performed time
)
