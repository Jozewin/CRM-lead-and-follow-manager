package com.techpuram.leadandfollowmanagement.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "follow_up",
    foreignKeys = [ForeignKey(
        entity = FollowUp::class,
        parentColumns = ["id"],
        childColumns = ["parentFollowup"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index(value = ["parentFollowup"])]
)
data class FollowUp(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val module: String, // 'Contact', 'Lead', 'Deal'
    val recordId: Int, // ID of associated record
    val dueTime: Long? = null,
    val notes: String? = null,
    val followUpStage: String,
    val followUpType: String,
    val priority: String, // Validated using a list
    val reminder: Long? = null,
    val parentFollowup: Int? = null,
    val prop: String? = null, // JSON data for additional properties
    val createdTime: Long = System.currentTimeMillis()
)

