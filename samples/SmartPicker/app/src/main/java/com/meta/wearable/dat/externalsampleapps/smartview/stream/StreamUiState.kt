// StreamUiState - SmartView Streaming UI State
//
// This data class manages UI state for camera streaming with AI analysis.

package com.meta.wearable.dat.externalsampleapps.smartview.stream

import android.graphics.Bitmap
import com.meta.wearable.dat.camera.types.StreamSessionState

data class StreamUiState(
    val streamSessionState: StreamSessionState = StreamSessionState.STOPPED,
    val videoFrame: Bitmap? = null,
    val aiAnalysis: String? = null,
    val isAnalyzing: Boolean = false,
    val isSpeaking: Boolean = false,
    val analysisEnabled: Boolean = true,
)
