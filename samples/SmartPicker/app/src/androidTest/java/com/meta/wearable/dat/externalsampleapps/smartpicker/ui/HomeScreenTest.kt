package com.meta.wearable.dat.externalsampleapps.smartpicker.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.meta.wearable.dat.externalsampleapps.smartpicker.settings.AiServiceType
import com.meta.wearable.dat.externalsampleapps.smartpicker.wearables.WearablesUiState
import com.meta.wearable.dat.externalsampleapps.smartpicker.wearables.WearablesViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: WearablesViewModel
    private val uiStateFlow = MutableStateFlow(WearablesUiState())

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.uiState } returns uiStateFlow as StateFlow<WearablesUiState>
    }

    @Test
    fun displaysCorrectTitle() {
        composeTestRule.setContent {
            HomeScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("SmartPicker").assertExists()
    }

    @Test
    fun displaysCorrectSubtitle() {
        composeTestRule.setContent {
            HomeScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("AI-Powered Scene Understanding").assertExists()
    }

    @Test
    fun displaysConnectGlassesButton() {
        composeTestRule.setContent {
            HomeScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Connect my glasses").assertExists()
    }

    @Test
    fun displaysDescriptionText() {
        composeTestRule.setContent {
            HomeScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Connect your Meta AI glasses to start analyzing scenes with AI and receive audio feedback.")
            .assertExists()
    }

    @Test
    fun clickingConnectGlassesCallsViewModel() {
        composeTestRule.setContent {
            HomeScreen(viewModel = mockViewModel)
        }

        composeTestRule.onNodeWithText("Connect my glasses").performClick()
        
        verify { mockViewModel.startRegistration(any()) }
    }
}
