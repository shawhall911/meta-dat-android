// AiAnalysisService - Cloud AI Integration for Scene Understanding
//
// This service sends video frames to a cloud-based AI service for analysis
// and receives scene understanding results.

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
data class AiAnalysisRequest(
    val image: String, // Base64 encoded image
    val prompt: String = "Analyze this scene and provide a brief description of what you see. Focus on objects, people, activities, and the environment."
)

@Serializable
data class AiAnalysisResponse(
    val description: String,
    val confidence: Float? = null
)

interface AiAnalysisService {
    suspend fun analyzeFrame(bitmap: Bitmap): Result<AiAnalysisResponse>
}

class CloudAiAnalysisService(
    private val apiUrl: String,
    private val apiKey: String? = null,
    private val authHeader: String = "Bearer" // Can be "Bearer" or "Api-Key" etc.
) : AiAnalysisService {

    companion object {
        private const val TAG = "CloudAiAnalysisService"
        private val JSON = Json { ignoreUnknownKeys = true }
        
        // Helper to create service from configuration
        fun create(
            apiUrl: String? = null,
            apiKey: String? = null
        ): CloudAiAnalysisService {
            val url = apiUrl ?: "https://api.example.com/v1/vision/analyze"
            return CloudAiAnalysisService(apiUrl = url, apiKey = apiKey)
        }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
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
                // Validate configuration
                if (apiUrl.contains("example.com") || apiUrl.isEmpty()) {
                    return@withContext Result.failure(
                        IllegalArgumentException("AI service URL not configured. Please set a valid API endpoint.")
                    )
                }

                // Convert bitmap to base64
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val imageBytes = outputStream.toByteArray()
                val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

                Log.d(TAG, "Sending image for analysis (${imageBytes.size} bytes)")

                // Create request
                val requestBody = AiAnalysisRequest(image = base64Image)
                val jsonBody = JSON.encodeToString(AiAnalysisRequest.serializer(), requestBody)
                val mediaType = "application/json".toMediaType()
                val body = jsonBody.toRequestBody(mediaType)

                val requestBuilder = Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")

                // Add authentication header
                apiKey?.let {
                    requestBuilder.addHeader("Authorization", "$authHeader $it")
                }

                val request = requestBuilder.build()

                // Execute request
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    Log.d(TAG, "AI analysis successful: $responseBody")
                    try {
                        val analysisResponse = JSON.decodeFromString<AiAnalysisResponse>(responseBody)
                        Result.success(analysisResponse)
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse AI response", e)
                        Result.failure(
                            IllegalStateException("Invalid response format from AI service: ${e.message}")
                        )
                    }
                } else {
                    val errorMsg = "AI analysis failed: HTTP ${response.code} - $responseBody"
                    Log.e(TAG, errorMsg)
                    Result.failure(
                        IllegalStateException(errorMsg)
                    )
                }
            } catch (e: java.net.UnknownHostException) {
                val errorMsg = "Cannot reach AI service. Check your internet connection and API URL."
                Log.e(TAG, errorMsg, e)
                Result.failure(IllegalStateException(errorMsg))
            } catch (e: java.net.SocketTimeoutException) {
                val errorMsg = "AI service request timed out. The service may be slow or unavailable."
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

// Mock implementation for testing without a real AI service
class MockAiAnalysisService : AiAnalysisService {
    private val mockDescriptions = listOf(
        "I see a person walking in a park with trees in the background.",
        "There's a table with objects on it in a well-lit room.",
        "I can see a building with windows and a clear sky above.",
        "The scene shows a person using a mobile device indoors.",
        "I see various objects arranged on a surface."
    )
    private var index = 0

    override suspend fun analyzeFrame(bitmap: Bitmap): Result<AiAnalysisResponse> {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Default) {
            kotlinx.coroutines.delay(500) // Simulate network delay
            val description = mockDescriptions[index % mockDescriptions.size]
            index++
            Result.success(
                AiAnalysisResponse(
                    description = description,
                    confidence = 0.85f
                )
            )
        }
    }
}
