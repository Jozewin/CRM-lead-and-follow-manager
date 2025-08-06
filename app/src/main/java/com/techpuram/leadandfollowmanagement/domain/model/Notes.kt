package com.techpuram.leadandfollowmanagement.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Notes(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val module: String,  // 'Contact', 'Lead', 'Deal'
    val recordId: Int,  // ID of the associated Contact, Lead, or Deal
    val notes: String?,
    val prop: String?,  // JSON data for additional properties
    val createdTime: Long = System.currentTimeMillis()
)
