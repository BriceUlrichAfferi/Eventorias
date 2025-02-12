package com.example.eventorias

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.eventorias.model.Event
import com.example.eventorias.presentation.event.EventRepository
import com.example.eventorias.presentation.event.EventViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime

@RunWith(MockitoJUnitRunner::class)
@ExperimentalCoroutinesApi
class EventViewModelTest : KoinTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var repository: EventRepository

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: EventViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = EventViewModel()


        val testModule = module {
            single<EventRepository> { repository }
        }

        startKoin {
            modules(testModule)
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
        stopKoin()
    }

    @Test
    fun `test fetchEvents updates events when successful`() = testDispatcher.runBlockingTest {
        // Given
        val mockEvents = listOf(Event("id1", "Title1", "Category1", LocalDate.now(), LocalTime.now()))
        `when`(repository.getEventsRealtime()).thenReturn(flowOf(mockEvents))

        // When
        viewModel.fetchEvents()

        // Then
        advanceUntilIdle()
        assert(viewModel.events.value == mockEvents)
        verify(repository).getEventsRealtime()
    }

    @Test
    fun `test fetchEvents updates error when an exception occurs`() = testDispatcher.runBlockingTest {
        // Given
        val exception = RuntimeException("Test Exception")
        `when`(repository.getEventsRealtime()).thenThrow(exception)

        // When
        viewModel.fetchEvents()

        // Then
        advanceUntilIdle()
        val expectedError = "An error occurred,\nplease try again later"
        assert(viewModel.error.value == expectedError)
        verify(repository).getEventsRealtime()
    }
}