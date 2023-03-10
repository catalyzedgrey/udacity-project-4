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
import kotlinx.coroutines.test.pauseDispatcher
import kotlinx.coroutines.test.resumeDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.*
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
class remindersViewModel {

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
    fun checkLoading_() = runBlocking {
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


    //LIVE DATA TESTING
    @Test
    fun reminderList_liveData(){
        //Given a fresh ListViewModel

        //When loading one reminder from fake database
        remindersViewModel.loadReminders()

        //Then the LiveData with remindersList should have all the reminders
        val value = remindersViewModel.remindersList.getOrAwaitValue()
        assertThat(value, (not(nullValue())))

    }

    @Test
    fun loadReminders_loadData_error(){
        //Given a fresh ListViewModel and set shouldGetAnError to true
        repo.shouldReturnError = true

        //When loading one reminder from fake database
        remindersViewModel.loadReminders()

        //Then the LiveData with remindersList should be null
        val message = remindersViewModel.showSnackBar.getOrAwaitValue()
        assertThat(message, (`is`("Test error - Not possible to load this reminder")))
    }
    

    @Test
    fun loadReminders_loading(){
        //Given a fresh ListViewModel and pause dispatcher
        mainCoroutineRule.pauseDispatcher()

        //When loading one reminder from fake database
        remindersViewModel.loadReminders()
        assertThat(remindersViewModel.showLoading.getOrAwaitValue(), `is`(true))
        mainCoroutineRule.resumeDispatcher()

        //Show loading show disappear whe dispatcher resumes
        assertThat(remindersViewModel.showLoading.getOrAwaitValue(), `is`(false))

    }
}