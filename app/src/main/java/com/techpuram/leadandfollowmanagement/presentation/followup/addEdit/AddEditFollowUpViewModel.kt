package com.techpuram.leadandfollowmanagement.presentation.followup.addEdit

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.techpuram.leadandfollowmanagement.util.ReminderWorker
import com.techpuram.leadandfollowmanagement.domain.model.FollowUp
import com.techpuram.leadandfollowmanagement.domain.repository.ContactRepository
import com.techpuram.leadandfollowmanagement.domain.repository.DealRepository
import com.techpuram.leadandfollowmanagement.domain.repository.FollowUpRepository
import com.techpuram.leadandfollowmanagement.domain.repository.LeadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AddEditFollowUpViewModel @Inject constructor(
    private val followUpRepository: FollowUpRepository,
    private val workManager: WorkManager,
    private val contactRepository: ContactRepository,
    private val leadRepository: LeadRepository,
    private val dealRepository: DealRepository,
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle
) : ViewModel()
{

    private val _state = MutableStateFlow(AddEditFollowUpState())
    val state: StateFlow<AddEditFollowUpState> = _state.asStateFlow()

    private val followUpId: Int? = savedStateHandle.get<Int>("followUpId")

    init {
        followUpId?.let { id ->
            if (id != -1) {
                loadFollowUp(id)
            }
        }
    }

    fun onEvent(event: AddEditFollowUpEvent) {
        when (event) {
            is AddEditFollowUpEvent.ModuleChanged -> {
                _state.update { it.copy(
                    module = event.module,
                    // Reset record selection when module changes
                    recordId = null,
                    recordName = ""
                ) }
            }
            is AddEditFollowUpEvent.RecordSelected -> {
                _state.update { it.copy(
                    recordId = event.id,
                    recordName = event.name
                ) }
            }
            is AddEditFollowUpEvent.DueTimeChanged -> {
                _state.update { it.copy(dueTime = event.timestamp) }
            }
            is AddEditFollowUpEvent.NotesChanged -> {
                _state.update { it.copy(notes = event.notes) }
            }
            is AddEditFollowUpEvent.StageChanged -> {
                _state.update { it.copy(followUpStage = event.stage) }
            }
            is AddEditFollowUpEvent.TypeChanged -> {
                _state.update { it.copy(followUpType = event.type) }
            }
            is AddEditFollowUpEvent.PriorityChanged -> {
                _state.update { it.copy(priority = event.priority) }
            }
            is AddEditFollowUpEvent.ReminderChanged -> {
                _state.update { it.copy(reminder = event.minutes) }
            }
            is AddEditFollowUpEvent.SaveFollowUp -> {
                saveFollowUp()
            }
            is AddEditFollowUpEvent.SelectRecord -> {
                // This will be handled by the UI to navigate to the appropriate selection screen
            }
            is AddEditFollowUpEvent.HasDueTimeChanged -> {
                _state.update { it.copy(
                    hasDueTime = event.hasDueTime,
                    // Clear due time and reminder if toggle is turned off
                    dueTime = if (event.hasDueTime) it.dueTime else null,
                    reminder = if (event.hasDueTime) it.reminder else null
                ) }
            }
        }
    }

    private fun loadFollowUp(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                followUpRepository.getFollowUpById(id)?.let { followUp ->
                    _state.update { it.copy(
                        id = followUp.id,
                        module = followUp.module,
                        recordId = followUp.recordId,
                        hasDueTime = followUp.dueTime != null,
                        dueTime = followUp.dueTime,
                        notes = followUp.notes ?: "",
                        followUpStage = followUp.followUpStage,
                        followUpType = followUp.followUpType,
                        priority = followUp.priority,
                        reminder = followUp.reminder,
                        isLoading = false
                    ) }

                    // Load record name based on module and recordId
                    try {
                        val recordName = when (followUp.module) {
                            "Contact" -> {
                                val contact = contactRepository.getContactById(followUp.recordId)
                                contact?.name ?: "Unknown Contact"
                            }
                            "Lead" -> {
                                val lead = leadRepository.getLeadById(followUp.recordId)
                                lead?.name ?: "Unknown Lead"
                            }
                            "Deal" -> {
                                val deal = dealRepository.getDealById(followUp.recordId)
                                deal?.title ?: "Unknown Deal"
                            }
                            else -> "Unknown Record"
                        }

                        _state.update {
                            it.copy(recordName = recordName)
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(recordName = "Error loading record name")
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message,
                    isLoading = false
                ) }
            }
        }
    }

    private fun saveFollowUp() {
        val currentState = _state.value

        // Validate input
        if (currentState.recordId == null) {
            _state.update { it.copy(error = "Please select a record") }
            return
        }

        if (currentState.followUpType.isBlank()) {
            _state.update { it.copy(error = "Please select a follow-up type") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val followUp = FollowUp(
                    id = currentState.id,
                    module = currentState.module,
                    recordId = currentState.recordId,
                    dueTime = currentState.dueTime,
                    notes = currentState.notes.takeIf { it.isNotBlank() },
                    followUpStage = currentState.followUpStage,
                    followUpType = currentState.followUpType,
                    priority = currentState.priority,
                    reminder = currentState.reminder
                )

                val followUpId = if (followUp.id == null) {
                    followUpRepository.insertFollowUp(followUp).toInt()
                } else {
                    followUpRepository.updateFollowUp(followUp)
                    followUp.id
                }

                // Schedule notifications
                if(_state.value.hasDueTime)
                    scheduleNotifications(followUpId, followUp)

                _state.update { it.copy(
                    isLoading = false,
                    isSaved = true
                ) }
            } catch (e: Exception) {
                _state.update { it.copy(
                    error = e.message,
                    isLoading = false
                ) }
            }
        }
    }

    private fun scheduleNotifications(followUpId: Int, followUp: FollowUp) {
        // Cancel existing notifications for this follow-up
        workManager.cancelUniqueWork("followup_due_$followUpId")
        workManager.cancelUniqueWork("followup_reminder_$followUpId")

        // Normalize due time to nearest minute
        val dueTimeClean = normalizeToMinute(followUp.dueTime!!)

        // Schedule due time notification
        val dueData = Data.Builder()
            .putInt("followUpId", followUpId)
            .putString("title", "Follow-up Due")
            .putString("message", "Your ${followUp.followUpType} follow-up is now due")
            .build()

        val dueDelay = maxOf(0, dueTimeClean - nowNormalizedToMinute())

        val dueWork = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(dueData)
            .setInitialDelay(dueDelay, TimeUnit.MILLISECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "followup_due_$followUpId",
            ExistingWorkPolicy.REPLACE,
            dueWork
        )

        // Schedule reminder notification if set
        followUp.reminder?.let { reminderMinutes ->
            val reminderTime = dueTimeClean - (reminderMinutes * 60 * 1000)

            if (reminderTime > System.currentTimeMillis()) {
                val reminderData = Data.Builder()
                    .putInt("followUpId", followUpId)
                    .putString("title", "Follow-up Reminder")
                    .putString("message", "Reminder: You have a ${followUp.followUpType} follow-up in $reminderMinutes minutes")
                    .build()

                val reminderDelay = maxOf(0, reminderTime - nowNormalizedToMinute())

                val reminderWork = OneTimeWorkRequestBuilder<ReminderWorker>()
                    .setInputData(reminderData)
                    .setInitialDelay(reminderDelay, TimeUnit.MILLISECONDS)
                    .build()

                workManager.enqueueUniqueWork(
                    "followup_reminder_$followUpId",
                    ExistingWorkPolicy.REPLACE,
                    reminderWork
                )
            }
        }
    }
    private fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
    /**
     * Normalizes a timestamp to have seconds and milliseconds set to 00
     */
    private fun normalizeToMinute(timestamp: Long): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun nowNormalizedToMinute(): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }
    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}