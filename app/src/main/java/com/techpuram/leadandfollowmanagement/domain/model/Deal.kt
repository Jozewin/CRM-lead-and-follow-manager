package com.techpuram.leadandfollowmanagement.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "deal",
    foreignKeys = [
        ForeignKey(
            entity = Contact::class,
            parentColumns = ["id"],
            childColumns = ["contactId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Deal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val contactId: Int? = null,
    val amount: Double?,
    val stage: String, // Pipeline stage
    val closingDate: Long?,
    val probability: Int? = null, // Success probability percentage
    val createdTime: Long = System.currentTimeMillis(),
    val modifiedTime: Long = System.currentTimeMillis(),
    val title: String, // Deal name/title
    val description: String? = null,
    val cf1: String? = null,
    val cf2: String? = null,
    val cf3: String? = null,
    val cf4: String? = null,
    val cf5: String? = null,
    val cf6: String? = null,
    val cf7: String? = null,
    val cf8: String? = null,
    val cf9: String? = null,
    val cf10: String? = null,
    val cf11: String? = null,
    val cf12: String? = null,
    val cf13: String? = null,
    val cf14: String? = null,
    val cf15: String? = null,
    val cf16: String? = null,
    val cf17: String? = null,
    val cf18: String? = null,
    val cf19: String? = null,
    val cf20: String? = null,
    val prop: String? = null
)