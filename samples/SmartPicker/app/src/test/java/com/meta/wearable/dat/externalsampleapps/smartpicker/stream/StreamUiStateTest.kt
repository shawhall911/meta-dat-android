package com.meta.wearable.dat.externalsampleapps.smartpicker.stream

import com.meta.wearable.dat.camera.types.StreamSessionState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StreamUiStateTest {

    @Test
    fun `default state has correct initial values`() {
        val state = StreamUiState()
        
        assertEquals(StreamSessionState.STOPPED, state.streamSessionState)
        assertNull(state.videoFrame)
        assertNull(state.aiAnalysis)
        assertFalse(state.isAnalyzing)
        assertFalse(state.isSpeaking)
        assertTrue(state.analysisEnabled)
    }

    @Test
    fun `state copy with analysis enabled`() {
        val state = StreamUiState(analysisEnabled = false)
        val modified = state.copy(analysisEnabled = true)
        
        assertTrue(modified.analysisEnabled)
    }

    @Test
    fun `state copy with aiAnalysis`() {
        val state = StreamUiState()
        val modified = state.copy(aiAnalysis = "Test analysis result")
        
        assertEquals("Test analysis result", modified.aiAnalysis)
    }

    @Test
    fun `state copy with isAnalyzing`() {
        val state = StreamUiState(isAnalyzing = false)
        val modified = state.copy(isAnalyzing = true)
        
        assertTrue(modified.isAnalyzing)
    }

    @Test
    fun `state copy with isSpeaking`() {
        val state = StreamUiState(isSpeaking = false)
        val modified = state.copy(isSpeaking = true)
        
        assertTrue(modified.isSpeaking)
    }

    @Test
    fun `state copy with streamSessionState`() {
        val state = StreamUiState(streamSessionState = StreamSessionState.STOPPED)
        val modified = state.copy(streamSessionState = StreamSessionState.STREAMING)
        
        assertEquals(StreamSessionState.STREAMING, modified.streamSessionState)
    }

    @Test
    fun `state preserves unmodified values on copy`() {
        val original = StreamUiState(
            streamSessionState = StreamSessionState.STREAMING,
            aiAnalysis = "Original analysis",
            isAnalyzing = true,
            analysisEnabled = false
        )
        
        val modified = original.copy(isSpeaking = true)
        
        assertEquals(StreamSessionState.STREAMING, modified.streamSessionState)
        assertEquals("Original analysis", modified.aiAnalysis)
        assertTrue(modified.isAnalyzing)
        assertFalse(modified.analysisEnabled)
        assertTrue(modified.isSpeaking)
    }
}
