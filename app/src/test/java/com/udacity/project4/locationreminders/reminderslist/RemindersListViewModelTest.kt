package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersViewModel: RemindersListViewModel
    private lateinit var repo: FakeDataSource

    @Before
    fun setupViewModel() {
        repo = FakeDataSource()
        remindersViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            repo
        )
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun loadReminders_remindersListIsEmpty() {
        //Given that the reminders are initialized without data
        remindersViewModel.remindersList.value = emptyList()
        remindersViewModel.loadReminders()
        //the returned list is empty
        assertThat(remindersViewModel.remindersList.value?.size, IsEqual(0))
    }


    @Test
    fun loadReminders_reminderSizeIsThree() = runBlocking {
        //Given that three reminders are added
        val reminder = ReminderDTO("Title0", "Desc0", "Location0", 0.0, 0.0)

        repo.saveReminder(reminder)
        repo.saveReminder(reminder.copy(title = "Title1"))
        repo.saveReminder(reminder.copy(title = "Title2"))
        remindersViewModel.loadReminders()

        //the returned list size is correct
        assertThat(remindersViewModel.remindersList.value?.size, IsEqual(3))
    }


    @Test
    fun invalidateShowNoData_remindersListIsNull() {
        // Given we set reminders to null
        remindersViewModel.remindersList.value = null
        remindersViewModel.loadReminders()

        // Then showNoData should be true
        val value = remindersViewModel.showNoData.getOrAwaitValue()
        assertEquals(value, true)
    }

}