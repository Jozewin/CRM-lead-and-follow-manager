package com.techpuram.leadandfollowmanagement.di

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.work.WorkManager
import com.techpuram.leadandfollowmanagement.data.local.AppDatabase
import com.techpuram.leadandfollowmanagement.data.local.LeadDao
import com.techpuram.leadandfollowmanagement.data.local.dao.ContactDao
import com.techpuram.leadandfollowmanagement.data.local.dao.CustomFieldDao
import com.techpuram.leadandfollowmanagement.data.local.dao.DealDao
import com.techpuram.leadandfollowmanagement.data.local.dao.FollowUpDao
import com.techpuram.leadandfollowmanagement.data.remote.DriveServiceHelper
import com.techpuram.leadandfollowmanagement.data.repository.AuthRepositoryImpl
import com.techpuram.leadandfollowmanagement.data.repository.BackupRestoreRepositoryImpl
import com.techpuram.leadandfollowmanagement.data.repository.ContactRepositoryImple
import com.techpuram.leadandfollowmanagement.data.repository.CustomFieldRepositoryImpl
import com.techpuram.leadandfollowmanagement.data.repository.DealRepositoryImpl
import com.techpuram.leadandfollowmanagement.data.repository.FollowUpRepositoryImpl
import com.techpuram.leadandfollowmanagement.data.repository.GoogleDriveService
import com.techpuram.leadandfollowmanagement.data.repository.LeadRepositoryImpl
import com.techpuram.leadandfollowmanagement.domain.repository.AuthRepository
import com.techpuram.leadandfollowmanagement.domain.repository.BackupRestoreRepository
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.CustomFieldRepository
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import com.techpuram.leadandfollowmanagement.domain.repository.FollowUpRepository
import com.techpuram.leadandfollowmanagement.domain.repository.LeadRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataBase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "app_database"
        ).build().also {
            Log.d("BackupRestoreRepository", "DB created at: ${app.getDatabasePath("app_database").absolutePath}")
        }
    }

    @Provides
    fun provideContactDao(database: AppDatabase): ContactDao {
        return database.contactDao()
    }

    @Provides
    @Singleton
    fun provideRepository(contactDao: ContactDao): ContactRepository {
        return ContactRepositoryImple(contactDao)
    }

    @Provides
    fun provideLeadDao(database: AppDatabase): LeadDao {
        return database.leadDao()
    }

    @Provides
    @Singleton
    fun provideLeadRepository(leadDao: LeadDao): LeadRepository {
        return LeadRepositoryImpl(leadDao)
    }

    @Provides
    fun provideCustomFieldDao(database: AppDatabase): CustomFieldDao {
        return database.customFieldDao()
    }

    @Provides
    @Singleton
    fun provideCustomFieldRepository(
        customFieldDao: CustomFieldDao,
        contactDao: ContactDao,
        leadDao: LeadDao,
        dealDao: DealDao
    ): CustomFieldRepository {
        return CustomFieldRepositoryImpl(customFieldDao, contactDao, leadDao, dealDao)
    }



    @Provides
    fun provideFollowUpDao(database: AppDatabase): FollowUpDao {
        return database.followUpDao()
    }

    @Provides
    @Singleton
    fun provideFollowUpRepository(followUpDao: FollowUpDao): FollowUpRepository {
        return FollowUpRepositoryImpl(followUpDao)
    }

    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    fun provideDealsDao(database: AppDatabase) : DealDao {
        return database.dealDao()
    }

    @Provides
    @Singleton
    fun provideDealRepository( dealDao : DealDao ) : DealRepository {
        return DealRepositoryImpl(dealDao = dealDao)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        @ApplicationContext context: Context
    ): AuthRepository {
        return AuthRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideGoogleDriveService(): GoogleDriveService {
        return GoogleDriveService()
    }

    @Provides
    @Singleton
    fun provideBackupRestoreRepository(
        @ApplicationContext context: Context,
        database: AppDatabase,
        authRepository: AuthRepository,
        googleDriveService: GoogleDriveService
    ): BackupRestoreRepository {
        return BackupRestoreRepositoryImpl(
            context = context,
            database = database,
            authRepository = authRepository,
            googleDriveService = googleDriveService
        )
    }

}
