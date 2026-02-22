// TextToSpeechService - Audio Feedback for Scene Understanding
//
// This service converts AI analysis text to speech and plays it to the user.

package com.meta.wearable.dat.externalsampleapps.smartpicker.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

class TextToSpeechService(private val context: Context) {
    companion object {
        private const val TAG = "TextToSpeechService"
    }

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val pendingQueue = mutableListOf<String>()

    fun initialize(onInit: (Boolean) -> Unit) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Language not supported")
                    isInitialized = false
                    onInit(false)
                } else {
                    isInitialized = true
                    // Process pending queue
                    pendingQueue.forEach { speak(it) }
                    pendingQueue.clear()
                    onInit(true)
                }
            } else {
                Log.e(TAG, "TextToSpeech initialization failed")
                isInitialized = false
                onInit(false)
            }
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_ADD) {
        if (!isInitialized) {
            pendingQueue.add(text)
            return
        }

        tts?.let {
            val result = it.speak(text, queueMode, null, null)
            if (result == TextToSpeech.ERROR) {
                Log.e(TAG, "Error speaking text: $text")
            }
        }
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }

    fun isSpeaking(): Boolean {
        return tts?.isSpeaking == true
    }
}
