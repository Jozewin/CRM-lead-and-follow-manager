package com.techpuram.leadandfollowmanagement.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.techpuram.leadandfollowmanagement.data.local.dao.ContactDao
import com.techpuram.leadandfollowmanagement.data.local.dao.CustomFieldDao
import com.techpuram.leadandfollowmanagement.data.local.dao.DealDao
import com.techpuram.leadandfollowmanagement.data.local.dao.FollowUpDao
import com.techpuram.leadandfollowmanagement.domain.model.Contact
import com.techpuram.leadandfollowmanagement.domain.model.CustomField
import com.techpuram.leadandfollowmanagement.domain.model.Deal
import com.techpuram.leadandfollowmanagement.domain.model.FollowUp
import com.techpuram.leadandfollowmanagement.domain.model.Lead

@Database(
    entities = [
        Contact::class,
        Lead::class,
        CustomField::class,
        FollowUp::class,
        Deal::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun leadDao(): LeadDao
    abstract fun customFieldDao(): CustomFieldDao
    abstract fun dealDao(): DealDao
    abstract fun followUpDao(): FollowUpDao
}
