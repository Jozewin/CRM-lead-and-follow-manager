package com.techpuram.leadandfollowmanagement.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(
    tableName = "lead",
    foreignKeys = [ForeignKey(
        entity = Contact::class,
        parentColumns = ["id"],
        childColumns = ["contactId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )],
    indices = [Index("contactId")]
)
data class Lead (
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val name: String,
    val contactId: Int? = null,
    val isConverted: Boolean = false,
    val email: String?,
    val mobile: String,
    val status: String, // Make sure to enforce enum values in business logic
    val whatsappNumber: String?,
    val leadSource: String?,
    val createdTime: Long = System.currentTimeMillis(),
    val modifiedTime: Long = System.currentTimeMillis(),
    val cf1: String?,
    val cf2: String?,
    val cf3: String?,
    val cf4: String?,
    val cf5: String?,
    val cf6: String?,
    val cf7: String?,
    val cf8: String?,
    val cf9: String?,
    val cf10: String?,
    val cf11: String?,
    val cf12: String?,
    val cf13: String?,
    val cf14: String?,
    val cf15: String?,
    val cf16: String?,
    val cf17: String?,
    val cf18: String?,
    val cf19: String?,
    val cf20: String?,
    val prop: String?
) : Parcelable
