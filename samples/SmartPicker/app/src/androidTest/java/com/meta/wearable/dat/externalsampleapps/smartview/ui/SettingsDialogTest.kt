package com.meta.wearable.dat.externalsampleapps.smartview.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.meta.wearable.dat.externalsampleapps.smartview.settings.AiServiceType
import org.junit.Rule
import org.junit.Test

class SettingsDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun displaysDialogTitle() {
        composeTestRule.setContent {
            SettingsDialog(
                currentType = AiServiceType.HUGGING_FACE,
                onDismiss = {},
                onSelect = {}
            )
        }

        composeTestRule.onNodeWithText("AI Service Settings").assertExists()
    }

    @Test
    fun displaysHuggingFaceOption() {
        composeTestRule.setContent {
            SettingsDialog(
                currentType = AiServiceType.HUGGING_FACE,
                onDismiss = {},
                onSelect = {}
            )
        }

        composeTestRule.onNodeWithText("Hugging Face API").assertExists()
        composeTestRule.onNodeWithText("Real AI analysis (requires internet)").assertExists()
    }

    @Test
    fun displaysMockOption() {
        composeTestRule.setContent {
            SettingsDialog(
                currentType = AiServiceType.MOCK,
                onDismiss = {},
                onSelect = {}
            )
        }

        composeTestRule.onNodeWithText("Mock Service").assertExists()
        composeTestRule.onNodeWithText("Offline mode for testing").assertExists()
    }

    @Test
    fun displaysCloseButton() {
        composeTestRule.setContent {
            SettingsDialog(
                currentType = AiServiceType.HUGGING_FACE,
                onDismiss = {},
                onSelect = {}
            )
        }

        composeTestRule.onNodeWithText("Close").assertExists()
    }

    @Test
    fun clickingCloseCallsOnDismiss() {
        var dismissCalled = false

        composeTestRule.setContent {
            SettingsDialog(
                currentType = AiServiceType.HUGGING_FACE,
                onDismiss = { dismissCalled = true },
                onSelect = {}
            )
        }

        composeTestRule.onNodeWithText("Close").performClick()

        assert(dismissCalled)
    }

    @Test
    fun displaysDescription() {
        composeTestRule.setContent {
            SettingsDialog(
                currentType = AiServiceType.HUGGING_FACE,
                onDismiss = {},
                onSelect = {}
            )
        }

        composeTestRule.onNodeWithText("Choose AI service for scene analysis:").assertExists()
    }
}
