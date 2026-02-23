package com.meta.wearable.dat.externalsampleapps.smartpicker.wearables

import com.meta.wearable.dat.core.types.RegistrationState
import com.meta.wearable.dat.externalsampleapps.smartpicker.settings.AiServiceType
import kotlinx.collections.immutable.persistentListOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WearablesUiStateTest {

    @Test
    fun `isRegistered returns true when registration state is Registered`() {
        val state = WearablesUiState(
            registrationState = RegistrationState.Registered()
        )
        assertTrue(state.isRegistered)
    }

    @Test
    fun `isRegistered returns false when registration state is Unavailable`() {
        val state = WearablesUiState(
            registrationState = RegistrationState.Unavailable()
        )
        assertFalse(state.isRegistered)
    }

    @Test
    fun `isRegistered returns false when registration state is Registering`() {
        val state = WearablesUiState(
            registrationState = RegistrationState.Registering()
        )
        assertFalse(state.isRegistered)
    }

    @Test
    fun `isRegistered returns true when hasMockDevices is true`() {
        val state = WearablesUiState(
            registrationState = RegistrationState.Unavailable(),
            hasMockDevices = true
        )
        assertTrue(state.isRegistered)
    }

    @Test
    fun `default state has correct initial values`() {
        val state = WearablesUiState()
        
        assertFalse(state.isStreaming)
        assertFalse(state.hasMockDevices)
        assertFalse(state.isDebugMenuVisible)
        assertFalse(state.isGettingStartedSheetVisible)
        assertFalse(state.hasActiveDevice)
        assertFalse(state.showSettings)
        assertTrue(state.devices.isEmpty())
        assertEquals(AiServiceType.HUGGING_FACE, state.aiServiceType)
    }

    @Test
    fun `state copy preserves unmodified values`() {
        val original = WearablesUiState(
            registrationState = RegistrationState.Registered(),
            hasMockDevices = true,
            isStreaming = true,
            aiServiceType = AiServiceType.MOCK
        )
        
        val modified = original.copy(showSettings = true)
        
        assertTrue(modified.isStreaming)
        assertTrue(modified.hasMockDevices)
        assertTrue(modified.showSettings)
        assertEquals(AiServiceType.MOCK, modified.aiServiceType)
    }
}
