// GoogleVisionAiService - Google Cloud Vision API for Scene Understanding
//
// This service uses Google Cloud Vision API which has a generous free tier
// (1,000 requests/month forever free) and is very reliable.

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
data class GoogleVisionRequest(
    val requests: List<AnnotateImageRequest>
)

@Serializable
data class AnnotateImageRequest(
    val image: ImageData,
    val features: List<Feature>
)

@Serializable
data class ImageData(
    val content: String // Base64 encoded image
)

@Serializable
data class Feature(
    val type: String = "LABEL_DETECTION",
    val maxResults: Int = 10
)

@Serializable
data class GoogleVisionResponse(
    val responses: List<AnnotateImageResponse>
)

@Serializable
data class AnnotateImageResponse(
    val labelAnnotations: List<LabelAnnotation>? = null,
    val error: ErrorResponse? = null
)

@Serializable
data class LabelAnnotation(
    val description: String,
    val score: Float
)

@Serializable
data class ErrorResponse(
    val code: Int,
    val message: String
)

class GoogleVisionAiService(
    private val apiKey: String
) : AiAnalysisService {

    companion object {
        private const val TAG = "GoogleVisionAiService"
        private val JSON = Json { ignoreUnknownKeys = true }
    }

    private val apiUrl = "https://vision.googleapis.com/v1/images:annotate?key=$apiKey"
    
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
                // Convert bitmap to base64
                val outputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                val imageBytes = outputStream.toByteArray()
                val base64Image = Base64.encodeToString(imageBytes, Base64.NO_WRAP)

                Log.d(TAG, "Sending image for analysis (${imageBytes.size} bytes)")

                // Create Google Vision API request
                val requestBody = GoogleVisionRequest(
                    requests = listOf(
                        AnnotateImageRequest(
                            image = ImageData(content = base64Image),
                            features = listOf(
                                Feature(type = "LABEL_DETECTION", maxResults = 10)
                            )
                        )
                    )
                )
                
                val jsonBody = JSON.encodeToString(GoogleVisionRequest.serializer(), requestBody)
                val mediaType = "application/json".toMediaType()
                val body = jsonBody.toRequestBody(mediaType)

                val request = Request.Builder()
                    .url(apiUrl)
                    .post(body)
                    .addHeader("Content-Type", "application/json")
                    .build()

                // Execute request
                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && responseBody != null) {
                    Log.d(TAG, "AI analysis successful: $responseBody")
                    try {
                        val visionResponse = JSON.decodeFromString<GoogleVisionResponse>(responseBody)
                        if (visionResponse.responses.isNotEmpty()) {
                            val annotateResponse = visionResponse.responses[0]
                            
                            // Check for errors
                            annotateResponse.error?.let { error ->
                                return@withContext Result.failure(
                                    IllegalStateException("Google Vision API error: ${error.message}")
                                )
                            }
                            
                            // Build description from labels
                            val labels = annotateResponse.labelAnnotations ?: emptyList()
                            if (labels.isNotEmpty()) {
                                val descriptions = labels.take(5).map { it.description }
                                val description = "I can see: ${descriptions.joinToString(", ")}"
                                val avgConfidence = labels.take(5).map { it.score }.average().toFloat()
                                
                                Result.success(
                                    AiAnalysisResponse(
                                        description = description,
                                        confidence = avgConfidence
                                    )
                                )
                            } else {
                                Result.failure(IllegalStateException("No labels detected in image"))
                            }
                        } else {
                            Result.failure(IllegalStateException("Empty response from Google Vision"))
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to parse Google Vision response", e)
                        Result.failure(IllegalStateException("Invalid response format: ${e.message}"))
                    }
                } else {
                    val errorMsg = "Google Vision API failed: HTTP ${response.code} - $responseBody"
                    Log.e(TAG, errorMsg)
                    Result.failure(IllegalStateException(errorMsg))
                }
            } catch (e: java.net.UnknownHostException) {
                val errorMsg = "Cannot reach Google Vision API. Check your internet connection."
                Log.e(TAG, errorMsg, e)
                Result.failure(IllegalStateException(errorMsg))
            } catch (e: java.net.SocketTimeoutException) {
                val errorMsg = "Google Vision API request timed out."
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
