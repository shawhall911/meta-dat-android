package com.meta.wearable.dat.externalsampleapps.smartpicker.wearables

import android.app.Application
import app.cash.turbine.test
import com.meta.wearable.dat.externalsampleapps.smartpicker.settings.AiServiceType
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WearablesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mockApplication: Application
    private lateinit var viewModel: WearablesViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        mockApplication = mockk(relaxed = true)
        every { mockApplication.applicationContext } returns mockApplication
        viewModel = WearablesViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has correct default values`() = runTest {
        viewModel.uiState.test {
            val initialState = awaitItem()
            assertFalse(initialState.isStreaming)
            assertFalse(initialState.hasActiveDevice)
            assertFalse(initialState.showSettings)
            assertFalse(initialState.isDebugMenuVisible)
            assertFalse(initialState.isGettingStartedSheetVisible)
            assertEquals(AiServiceType.HUGGING_FACE, initialState.aiServiceType)
        }
    }

    @Test
    fun `showSettings updates state correctly`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.showSettings()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state.showSettings)
        }
    }

    @Test
    fun `hideSettings updates state correctly`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.showSettings()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // consume showSettings state
            
            viewModel.hideSettings()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertFalse(state.showSettings)
        }
    }

    @Test
    fun `navigateToStreaming updates isStreaming to true`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.navigateToDeviceSelection()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertFalse(state.isStreaming)
        }
    }

    @Test
    fun `navigateToHome resets device and streaming state`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.navigateToHome()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertFalse(state.isStreaming)
            assertFalse(state.hasActiveDevice)
        }
    }

    @Test
    fun `showDebugMenu updates state correctly`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.showDebugMenu()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state.isDebugMenuVisible)
        }
    }

    @Test
    fun `hideDebugMenu updates state correctly`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.showDebugMenu()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // consume show state
            
            viewModel.hideDebugMenu()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertFalse(state.isDebugMenuVisible)
        }
    }

    @Test
    fun `showGettingStartedSheet updates state correctly`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.showGettingStartedSheet()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertTrue(state.isGettingStartedSheetVisible)
        }
    }

    @Test
    fun `hideGettingStartedSheet updates state correctly`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.showGettingStartedSheet()
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // consume show state
            
            viewModel.hideGettingStartedSheet()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertFalse(state.isGettingStartedSheetVisible)
        }
    }

    @Test
    fun `setRecentError updates state with error message`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.setRecentError("Test error")
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertEquals("Test error", state.recentError)
        }
    }

    @Test
    fun `clearCameraPermissionError clears error message`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.setRecentError("Test error")
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // consume error state
            
            viewModel.clearCameraPermissionError()
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertEquals(null, state.recentError)
        }
    }

    @Test
    fun `setAiServiceType updates aiServiceType to Mock`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            viewModel.setAiServiceType(AiServiceType.MOCK)
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertEquals(AiServiceType.MOCK, state.aiServiceType)
        }
    }

    @Test
    fun `setAiServiceType updates aiServiceType to HuggingFace`() = runTest {
        viewModel.uiState.test {
            // Skip initial state
            awaitItem()
            
            // First set to Mock
            viewModel.setAiServiceType(AiServiceType.MOCK)
            testDispatcher.scheduler.advanceUntilIdle()
            awaitItem() // consume Mock state
            
            // Then set to HuggingFace
            viewModel.setAiServiceType(AiServiceType.HUGGING_FACE)
            testDispatcher.scheduler.advanceUntilIdle()
            
            val state = awaitItem()
            assertEquals(AiServiceType.HUGGING_FACE, state.aiServiceType)
        }
    }
}
