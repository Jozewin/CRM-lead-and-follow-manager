package com.techpuram.leadandfollowmanagement.domain.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contact")
data class Contact(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val email: String? = null,
    val mobile: String,
    val whatsappNumber: String? = null,
    val companyName: String? = null,
    val leadId: String? = null,
    val additionalMobile: String? = null,
    val street: String? = null,
    val state: String? = null,
    val city: String?  = null,
    val country: String?  = null,
    val zip: String? = null,
    val note: String? = null,
    val photoId: String?  = null,
    val createdTime: Long = System.currentTimeMillis(),
    val modifiedTime: Long = System.currentTimeMillis(),
    val cf1: String?  = null,
    val cf2: String?  = null,
    val cf3: String?  = null,
    val cf4: String?  = null,
    val cf5: String?  = null,
    val cf6: String?  = null,
    val cf7: String?  = null,
    val cf8: String?  = null,
    val cf9: String?  = null,
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
) : Parcelable
