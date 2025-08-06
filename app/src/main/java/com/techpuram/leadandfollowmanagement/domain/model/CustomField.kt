package com.techpuram.leadandfollowmanagement.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_field")
data class CustomField(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val module: String,  // e.g., 'Contact', 'Lead', 'Deal'
    val fieldName: String,
    val fieldType: String, // Can be 'TEXT', 'NUMBER', or 'DROPDOWN'
    val columnName: String,
    val createdTime: Long = System.currentTimeMillis()
)
