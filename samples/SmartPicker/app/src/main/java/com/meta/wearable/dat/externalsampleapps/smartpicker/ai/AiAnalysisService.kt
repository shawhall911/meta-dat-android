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
    private val apiUrl: String = "https://api.example.com/v1/vision/analyze", // Replace with actual AI service URL
    private val apiKey: String? = null // Add your API key here
) : AiAnalysisService {

    companion object {
        private const val TAG = "AiAnalysisService"
        private val JSON = Json { ignoreUnknownKeys = true }
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    override suspend fun analyzeFrame(bitmap: Bitmap): Result<AiAnalysisResponse> {
        return withContext(Dispatchers.IO) {
            try {
                // Convert bitmap to base64
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                val imageBytes = outputStream.toByteArray()
                val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

                // Create request
                val requestBody = AiAnalysisRequest(image = base64Image)
                val jsonBody = JSON.encodeToString(AiAnalysisRequest.serializer(), requestBody)
                val mediaType = "application/json".toMediaType()
                val body = jsonBody.toRequestBody(mediaType)

                val requestBuilder = Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")

                apiKey?.let {
                    requestBuilder.addHeader("Authorization", "Bearer $it")
                }

                val request = requestBuilder.build()

                // Execute request
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    val analysisResponse = JSON.decodeFromString<AiAnalysisResponse>(responseBody)
                    Result.success(analysisResponse)
                } else {
                    Log.e(TAG, "AI analysis failed: ${response.code} - $responseBody")
                    // Return a mock response for development/testing
                    Result.success(
                        AiAnalysisResponse(
                            description = "Scene analysis unavailable. Please configure your AI service endpoint.",
                            confidence = 0.0f
                        )
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error analyzing frame", e)
                // Return a mock response for development/testing
                Result.success(
                    AiAnalysisResponse(
                        description = "Analyzing scene... (Mock response - configure AI service for real analysis)",
                        confidence = 0.5f
                    )
                )
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
