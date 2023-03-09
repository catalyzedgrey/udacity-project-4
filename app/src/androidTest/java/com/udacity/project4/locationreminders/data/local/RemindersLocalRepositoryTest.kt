package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localDataSource: ReminderDataSource
    private lateinit var database: RemindersDatabase

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localDataSource =
            RemindersLocalRepository(
                database.reminderDao(),
                Dispatchers.Main
            )
    }

    @After
    fun tearDownTests() = database.close()

    @Test
    fun saveReminder_retrievesReminder() = runBlockingTest {
        // GIVEN - A new reminder saved in the database.
        val newReminder = ReminderDTO("TestTitle0", null, "TestLocation1", 3.6, 3.4)
        localDataSource.saveReminder(newReminder)

        // WHEN  - Reminder retrieved by ID.
        val result = localDataSource.getReminder(newReminder.id)

        // THEN - Same reminder is returned.
        result as Result.Success
        assertThat<ReminderDTO>(result as ReminderDTO, notNullValue())
        assertThat(result.id, `is`(newReminder.id))
        assertThat(result.title, `is`(newReminder.title))
        assertThat(result.description, `is`(newReminder.description))
    }

    @Test
    fun deleteReminders_retrievesReminders() = runBlocking {
        // GIVEN - Clear the DB
        localDataSource.deleteAllReminders()

        // WHEN  - Retrieve all reminders
        val result = localDataSource.getReminders()

        // THEN - the list is empty
        result as Result.Success
        assertThat(result.data.size, IsEqual(0))
    }



}