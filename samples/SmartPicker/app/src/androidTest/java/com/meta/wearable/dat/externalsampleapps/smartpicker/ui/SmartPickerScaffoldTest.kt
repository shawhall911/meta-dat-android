package com.meta.wearable.dat.externalsampleapps.smartpicker.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.meta.wearable.dat.core.types.RegistrationState
import com.meta.wearable.dat.externalsampleapps.smartpicker.settings.AiServiceType
import com.meta.wearable.dat.externalsampleapps.smartpicker.wearables.WearablesUiState
import com.meta.wearable.dat.externalsampleapps.smartpicker.wearables.WearablesViewModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SmartPickerScaffoldTest {

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
    fun showsHomeScreenWhenNotRegistered() {
        uiStateFlow.value = WearablesUiState(
            registrationState = RegistrationState.Unavailable(),
            isRegistered = false
        )

        composeTestRule.setContent {
            SmartPickerScaffold(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { mockk() }
            )
        }

        composeTestRule.onNodeWithText("Connect my glasses").assertExists()
    }

    @Test
    fun showsNonStreamScreenWhenRegistered() {
        uiStateFlow.value = WearablesUiState(
            registrationState = RegistrationState.Registered,
            isRegistered = true,
            hasActiveDevice = true
        )

        composeTestRule.setContent {
            SmartPickerScaffold(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { mockk() }
            )
        }

        composeTestRule.onNodeWithText("SmartPicker Ready").assertExists()
    }

    @Test
    fun showsWaitingForDeviceWhenRegisteredButNoDevice() {
        uiStateFlow.value = WearablesUiState(
            registrationState = RegistrationState.Registered,
            isRegistered = true,
            hasActiveDevice = false
        )

        composeTestRule.setContent {
            SmartPickerScaffold(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { mockk() }
            )
        }

        composeTestRule.onNodeWithText("Waiting for an active device").assertExists()
    }

    @Test
    fun showsHomeScreenTitleWhenNotRegistered() {
        uiStateFlow.value = WearablesUiState(
            registrationState = RegistrationState.Unavailable(),
            isRegistered = false
        )

        composeTestRule.setContent {
            SmartPickerScaffold(
                viewModel = mockViewModel,
                onRequestWearablesPermission = { mockk() }
            )
        }

        composeTestRule.onNodeWithText("SmartPicker").assertExists()
    }
}
