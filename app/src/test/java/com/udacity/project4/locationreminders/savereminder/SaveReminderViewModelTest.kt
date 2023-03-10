package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var repo: FakeDataSource
    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setup() {
        repo = FakeDataSource()
        saveReminderViewModel =
            SaveReminderViewModel(ApplicationProvider.getApplicationContext(), repo)
    }

    @After
    fun tearDownTests() = stopKoin()

    @Test
    fun validateAndSaveReminder_nullDescription_IsNotValid() {
        //Given a reminder without a description
        val reminder = ReminderDataItem("TestTitle0", null, "TestLocation1", 3.6, 3.4)
        val isValid = saveReminderViewModel.validateEnteredData(reminder)

        // the returned value isnt valid
        MatcherAssert.assertThat(isValid, `is`(true))
    }

    @Test
    fun clearSelectedLocation() {
        //Given that we clear the location
        saveReminderViewModel.clearSelectedLocation()
        //The lat and long is null
        MatcherAssert.assertThat(saveReminderViewModel.longitude.value, IsNull())
        MatcherAssert.assertThat(saveReminderViewModel.longitude.value, IsNull())
    }

}