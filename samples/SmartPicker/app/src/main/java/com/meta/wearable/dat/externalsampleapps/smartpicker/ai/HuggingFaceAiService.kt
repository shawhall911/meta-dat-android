// HuggingFaceAiService - Free AI Service for Scene Understanding
//
// This service uses Hugging Face Inference API which is completely free
// and perfect for image captioning and scene understanding.
// Updated to use v1/chat/completions endpoint with Qwen model

package com.meta.wearable.dat.externalsampleapps.smartpicker.ai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

@Serializable
data class ChatCompletionMessage(
    val role: String,
    val content: List<ChatContentItem>
)

@Serializable
data class ChatContentItem(
    val type: String,
    val text: String? = null,
    val image_url: ImageUrl? = null
)

@Serializable
data class ImageUrl(
    val url: String
)

@Serializable
data class ChatCompletionRequest(
    val messages: List<ChatCompletionMessage>,
    val model: String,
    val stream: Boolean = false
)

@Serializable
data class ChatChoice(
    val message: ChatResponseMessage
)

@Serializable
data class ChatResponseMessage(
    val content: String
)

@Serializable
data class ChatCompletionResponse(
    val choices: List<ChatChoice>
)

class HuggingFaceAiService(
    private val apiKey: String,
    private val model: String = "Qwen/Qwen3.5-397B-A17B:novita"
) : AiAnalysisService {

    companion object {
        private const val TAG = "HuggingFaceAiService"
        private val JSON = Json { 
            ignoreUnknownKeys = true
            encodeDefaults = true  // Ensure default values are encoded
        }
        
        // Alternative models you can try:
        // "Qwen/Qwen3.5-397B-A17B:novita" - Default, good for scene understanding
        // "meta-llama/Llama-3.2-11B-Vision-Instruct:novita" - Llama vision model
    }

    // Hugging Face API endpoint - using v1/chat/completions (OpenAI-compatible)
    private val apiUrl = "https://router.huggingface.co/v1/chat/completions"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request()
            Log.d(TAG, "Requesting: ${request.url}")
            val response = chain.proceed(request)
            Log.d(TAG, "Response: ${response.code}")
            response
        }
        .build()

    override suspend fun analyzeFrame(bitmap: Bitmap): Result<AiAnalysisResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Convert bitmap to base64
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val imageBytes = outputStream.toByteArray()
                val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)
                
                // Data URI format for image
                val dataUri = "data:image/jpeg;base64,$base64Image"

                Log.d(TAG, "Sending image for analysis (${imageBytes.size} bytes)")

                // Create request body using v1/chat/completions format
                val requestBody = ChatCompletionRequest(
                    messages = listOf(
                        ChatCompletionMessage(
                            role = "user",
                            content = listOf(
                                ChatContentItem(
                                    type = "text",
                                    text = "Describe this image in one sentence."
                                ),
                                ChatContentItem(
                                    type = "image_url",
                                    image_url = ImageUrl(url = dataUri)
                                )
                            )
                        )
                    ),
                    model = model,
                    stream = false
                )
                
                val jsonBody = JSON.encodeToString(ChatCompletionRequest.serializer(), requestBody)
                // Log just the beginning to see the structure (without huge base64)
                val logJson = jsonBody.replace(Regex("data:image/jpeg;base64,[A-Za-z0-9+/=]+"), "data:image/jpeg;base64,...TRUNCATED...")
                Log.d(TAG, "Request JSON structure: ${logJson.take(500)}")
                val mediaType = "application/json".toMediaType()
                val body = jsonBody.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()

                // Execute request
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                Log.d(TAG, "Response code: ${response.code}")
                Log.d(TAG, "Response body: $responseBody")

                if (response.isSuccessful && responseBody != null) {
                    Log.d(TAG, "AI analysis successful: $responseBody")
                    try {
                        // Parse OpenAI-style response: {"choices":[{"message":{"content":"description"}}]}
                        val chatResponse = JSON.decodeFromString<ChatCompletionResponse>(responseBody)
                        if (chatResponse.choices.isNotEmpty()) {
                            val description = chatResponse.choices[0].message.content
                            Result.success(
                                AiAnalysisResponse(
                                    description = description,
                                    confidence = 0.9f
                                )
                            )
                        } else {
                            Result.failure(IllegalStateException("Empty response from Hugging Face"))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse Hugging Face response", e)
                        if (responseBody.contains("error")) {
                            Result.failure(IllegalStateException("Hugging Face error: $responseBody"))
                        } else {
                            Result.failure(IllegalStateException("Invalid response format: ${e.message}"))
                        }
                    }
                } else {
                    val errorMsg = "Hugging Face API failed: HTTP ${response.code} - $responseBody"
                    Log.e(TAG, errorMsg)
                    
                    if (response.code == 503) {
                        Result.failure(IllegalStateException("Model is loading. Please wait a moment and try again."))
                    } else {
                        Result.failure(IllegalStateException(errorMsg))
                    }
                }
            } catch (e: java.net.UnknownHostException) {
                val errorMsg = "Cannot reach Hugging Face API. Check your internet connection."
                Log.e(TAG, errorMsg, e)
                Result.failure(IllegalStateException(errorMsg))
            } catch (e: java.net.SocketTimeoutException) {
                val errorMsg = "Hugging Face API request timed out. The service may be slow."
                Log.e(TAG, errorMsg, e)
                Result.failure(IllegalStateException(errorMsg))
            } catch (e: Exception) {
                val errorMsg = "Error analyzing frame: ${e.message}"
                Log.e(TAG, errorMsg, e)
                Result.failure(IllegalStateException(errorMsg))
            }
        }
    }
}
