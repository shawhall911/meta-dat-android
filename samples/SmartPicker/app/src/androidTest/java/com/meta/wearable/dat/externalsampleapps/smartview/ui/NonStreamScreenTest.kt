package com.meta.wearable.dat.externalsampleapps.smartview.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.meta.wearable.dat.core.types.Permission
import com.meta.wearable.dat.core.types.PermissionStatus
import com.meta.wearable.dat.externalsampleapps.smartview.settings.AiServiceType
import com.meta.wearable.dat.externalsampleapps.smartview.wearables.WearablesUiState
import com.meta.wearable.dat.externalsampleapps.smartview.wearables.WearablesViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NonStreamScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var mockViewModel: WearablesViewModel
    private val uiStateFlow = MutableStateFlow(WearablesUiState(isRegistered = true, hasActiveDevice = true))

    @Before
    fun setup() {
        mockViewModel = mockk(relaxed = true)
        every { mockViewModel.uiState } returns uiStateFlow as StateFlow<WearablesUiState>
    }

    @Test
    fun displaysCorrectTitle() {
        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("SmartView Ready").assertExists()
    }

    @Test
    fun displaysDeviceConnectedMessageWhenActiveDevice() {
        uiStateFlow.value = WearablesUiState(isRegistered = true, hasActiveDevice = true)

        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("Device connected. Tap below to start AI analysis.")
            .assertExists()
    }

    @Test
    fun displaysWaitingMessageWhenNoActiveDevice() {
        uiStateFlow.value = WearablesUiState(isRegistered = true, hasActiveDevice = false)

        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("Waiting for an active device").assertExists()
    }

    @Test
    fun displaysCancelButtonWhenWaitingForDevice() {
        uiStateFlow.value = WearablesUiState(isRegistered = true, hasActiveDevice = false)

        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("Cancel").assertExists()
    }

    @Test
    fun clickingCancelNavigatesToHome() {
        uiStateFlow.value = WearablesUiState(isRegistered = true, hasActiveDevice = false)

        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("Cancel").performClick()

        verify { mockViewModel.navigateToHome() }
    }

    @Test
    fun displaysStartAIAnalysisButton() {
        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("Start AI Analysis").assertExists()
    }

    @Test
    fun displaysHuggingFaceAILabel() {
        uiStateFlow.value = WearablesUiState(
            isRegistered = true, 
            hasActiveDevice = true,
            aiServiceType = AiServiceType.HUGGING_FACE
        )

        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("HF AI").assertExists()
    }

    @Test
    fun displaysMockAILabel() {
        uiStateFlow.value = WearablesUiState(
            isRegistered = true, 
            hasActiveDevice = true,
            aiServiceType = AiServiceType.MOCK
        )

        composeTestRule.setContent {
            NonStreamScreen(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { PermissionStatus.Granted }
            )
        }

        composeTestRule.onNodeWithText("Mock AI").assertExists()
    }
}
