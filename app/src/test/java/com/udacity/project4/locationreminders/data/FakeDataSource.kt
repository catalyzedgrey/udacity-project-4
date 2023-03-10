package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(
    val reminders: MutableList<ReminderDTO>? = mutableListOf()
) : ReminderDataSource {

    var shouldReturnError: Boolean = false

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return if (shouldReturnError)
            Result.Error("Test error - Not possible to load this reminder")
        else
            reminders?.toList()?.let {
                Result.Success(it)
            } ?: Result.Success(emptyList())
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        val reminder = reminders?.find { it.id == id }

        return reminder?.let {
            Result.Success(it)
        } ?: Result.Error("Test error - Not possible to load this reminder")
    }

    override suspend fun deleteAllReminders() {
        reminders?.clear()
    }


}