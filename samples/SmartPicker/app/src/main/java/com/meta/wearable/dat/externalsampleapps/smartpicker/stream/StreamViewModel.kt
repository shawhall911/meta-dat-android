// StreamViewModel - SmartPicker Camera Streaming with AI Analysis
//
// This ViewModel demonstrates:
// - Creating and managing stream sessions with wearable devices
// - Receiving video frames from device cameras
// - Sending frames to cloud AI for scene analysis
// - Converting AI analysis to speech for audio feedback

package com.meta.wearable.dat.externalsampleapps.smartpicker.stream

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.meta.wearable.dat.camera.StreamSession
import com.meta.wearable.dat.camera.startStreamSession
import com.meta.wearable.dat.camera.types.StreamConfiguration
import com.meta.wearable.dat.camera.types.StreamSessionState
import com.meta.wearable.dat.camera.types.VideoFrame
import com.meta.wearable.dat.camera.types.VideoQuality
import com.meta.wearable.dat.core.Wearables
import com.meta.wearable.dat.core.selectors.DeviceSelector
import com.meta.wearable.dat.externalsampleapps.smartpicker.ai.AiAnalysisService
import com.meta.wearable.dat.externalsampleapps.smartpicker.ai.CloudAiAnalysisService
import com.meta.wearable.dat.externalsampleapps.smartpicker.ai.GoogleVisionAiService
import com.meta.wearable.dat.externalsampleapps.smartpicker.ai.HuggingFaceAiService
import com.meta.wearable.dat.externalsampleapps.smartpicker.ai.MockAiAnalysisService
import com.meta.wearable.dat.externalsampleapps.smartpicker.audio.TextToSpeechService
import com.meta.wearable.dat.externalsampleapps.smartpicker.wearables.WearablesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class StreamViewModel(
    application: Application,
    private val wearablesViewModel: WearablesViewModel,
) : AndroidViewModel(application) {

  companion object {
    private const val TAG = "StreamViewModel"
    private val INITIAL_STATE = StreamUiState()
    private const val ANALYSIS_INTERVAL_MS = 2000L // Analyze every 2 seconds
  }

  private val deviceSelector: DeviceSelector = wearablesViewModel.deviceSelector
  private var streamSession: StreamSession? = null

  private val _uiState = MutableStateFlow(INITIAL_STATE)
  val uiState: StateFlow<StreamUiState> = _uiState.asStateFlow()

  private var videoJob: Job? = null
  private var stateJob: Job? = null
  private var analysisJob: Job? = null

  // AI and TTS services
  // To use cloud AI: Replace MockAiAnalysisService() with CloudAiAnalysisService.create(apiUrl, apiKey)
  // Example: CloudAiAnalysisService.create("https://your-api.com/analyze", "your-api-key")
  private val aiService: AiAnalysisService = createAiService()
  private val ttsService: TextToSpeechService = TextToSpeechService(application)
  
  private fun createAiService(): AiAnalysisService {
    // ============================================
    // AI SERVICE CONFIGURATION
    // ============================================
    // See AI_SERVICE_SETUP.md, FREE_AI_SERVICES.md, and GOOGLE_VISION_SETUP.md
    
    // Option 1: Use mock for testing (works offline, no API needed)
    return MockAiAnalysisService()
    
    // Option 2: Use Google Cloud Vision API (RECOMMENDED - Reliable & Free)
    // 1. Get API key from https://console.cloud.google.com/
    // 2. Enable "Cloud Vision API"
    // 3. Uncomment below and add your API key:
    // return GoogleVisionAiService(apiKey = "YOUR_GOOGLE_API_KEY_HERE")
    
    // Option 3: Use Hugging Face (Now working with v1/chat/completions endpoint)
    // Get your free API key at: https://huggingface.co/settings/tokens
    // return HuggingFaceAiService(apiKey = "YOUR_HUGGINGFACE_API_KEY_HERE")
    
    // Option 4: Use custom cloud AI service
    // return CloudAiAnalysisService.create(
    //     apiUrl = "https://your-ai-service.com/v1/analyze",
    //     apiKey = "your-api-key-here"
    // )
  }

  init {
    // Initialize TTS
    ttsService.initialize { success ->
      if (success) {
        Log.d(TAG, "TextToSpeech initialized successfully")
      } else {
        Log.e(TAG, "TextToSpeech initialization failed")
      }
    }
  }

  fun startStream() {
    videoJob?.cancel()
    stateJob?.cancel()
    analysisJob?.cancel()
    
    val streamSession =
        Wearables.startStreamSession(
                getApplication(),
                deviceSelector,
                StreamConfiguration(videoQuality = VideoQuality.MEDIUM, 24),
            )
            .also { streamSession = it }
    
    videoJob = viewModelScope.launch { 
      streamSession.videoStream.collect { handleVideoFrame(it) } 
    }
    
    stateJob =
        viewModelScope.launch {
          streamSession.state.collect { currentState ->
            val prevState = _uiState.value.streamSessionState
            _uiState.update { it.copy(streamSessionState = currentState) }

            // navigate back when state transitioned to STOPPED
            if (currentState != prevState && currentState == StreamSessionState.STOPPED) {
              stopStream()
              wearablesViewModel.navigateToDeviceSelection()
            }
          }
        }
  }

  fun stopStream() {
    videoJob?.cancel()
    videoJob = null
    stateJob?.cancel()
    stateJob = null
    analysisJob?.cancel()
    analysisJob = null
    streamSession?.close()
    streamSession = null
    ttsService.stop()
    _uiState.update { INITIAL_STATE }
  }

  fun toggleAnalysis() {
    _uiState.update { it.copy(analysisEnabled = !it.analysisEnabled) }
    if (!_uiState.value.analysisEnabled) {
      analysisJob?.cancel()
      ttsService.stop()
    }
  }

  private var lastAnalysisTime = 0L
  private var lastFrameBitmap: Bitmap? = null

  private fun handleVideoFrame(videoFrame: VideoFrame) {
    // VideoFrame contains raw I420 video data in a ByteBuffer
    val buffer = videoFrame.buffer
    val dataSize = buffer.remaining()
    val byteArray = ByteArray(dataSize)

    // Save current position
    val originalPosition = buffer.position()
    buffer.get(byteArray)
    // Restore position
    buffer.position(originalPosition)

    // Convert I420 to NV21 format which is supported by Android's YuvImage
    val nv21 = convertI420toNV21(byteArray, videoFrame.width, videoFrame.height)
    val image = YuvImage(nv21, ImageFormat.NV21, videoFrame.width, videoFrame.height, null)
    val out =
        ByteArrayOutputStream().use { stream ->
          image.compressToJpeg(Rect(0, 0, videoFrame.width, videoFrame.height), 50, stream)
          stream.toByteArray()
        }

    val bitmap = BitmapFactory.decodeByteArray(out, 0, out.size)
    _uiState.update { it.copy(videoFrame = bitmap) }
    lastFrameBitmap = bitmap

    // Analyze frame periodically if analysis is enabled
    if (_uiState.value.analysisEnabled && 
        _uiState.value.streamSessionState == StreamSessionState.STREAMING) {
      val currentTime = System.currentTimeMillis()
      if (currentTime - lastAnalysisTime >= ANALYSIS_INTERVAL_MS) {
        lastAnalysisTime = currentTime
        analyzeFrame(bitmap)
      }
    }
  }

  private fun analyzeFrame(bitmap: Bitmap) {
    if (_uiState.value.isAnalyzing) {
      return // Skip if already analyzing
    }

    analysisJob = viewModelScope.launch {
      _uiState.update { it.copy(isAnalyzing = true) }
      
      try {
        val result = aiService.analyzeFrame(bitmap)
        result.getOrNull()?.let { analysisResponse ->
          val description = analysisResponse.description
          _uiState.update { 
            it.copy(
              aiAnalysis = description,
              isAnalyzing = false
            )
          }
          
          // Speak the analysis
          if (description.isNotBlank() && !ttsService.isSpeaking()) {
            _uiState.update { it.copy(isSpeaking = true) }
            ttsService.speak(description, android.speech.tts.TextToSpeech.QUEUE_FLUSH)
            // Reset speaking state after a delay
            delay(3000)
            _uiState.update { it.copy(isSpeaking = false) }
          }
        } ?: run {
          val error = result.exceptionOrNull()
          Log.e(TAG, "AI analysis failed", error)
          val errorMessage = "Analysis error: ${error?.message ?: "Unknown error"}"
          _uiState.update { 
            it.copy(
              aiAnalysis = errorMessage,
              isAnalyzing = false
            )
          }
          // Optionally speak the error
          if (!ttsService.isSpeaking()) {
            ttsService.speak("Analysis unavailable", android.speech.tts.TextToSpeech.QUEUE_FLUSH)
          }
        }
      } catch (e: Exception) {
        Log.e(TAG, "Error during analysis", e)
        _uiState.update { it.copy(isAnalyzing = false) }
      }
    }
  }

  // Convert I420 (YYYYYYYY:UUVV) to NV21 (YYYYYYYY:VUVU)
  private fun convertI420toNV21(input: ByteArray, width: Int, height: Int): ByteArray {
    val output = ByteArray(input.size)
    val size = width * height
    val quarter = size / 4

    input.copyInto(output, 0, 0, size) // Y is the same

    for (n in 0 until quarter) {
      output[size + n * 2] = input[size + quarter + n] // V first
      output[size + n * 2 + 1] = input[size + n] // U second
    }
    return output
  }

  override fun onCleared() {
    super.onCleared()
    stopStream()
    ttsService.shutdown()
  }

  class Factory(
      private val application: Application,
      private val wearablesViewModel: WearablesViewModel,
  ) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(StreamViewModel::class.java)) {
        @Suppress("UNCHECKED_CAST", "KotlinGenericsCast")
        return StreamViewModel(
            application = application,
            wearablesViewModel = wearablesViewModel,
        )
            as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}
