# AI Service Setup Guide

## Quick Start

### Step 1: Enable Cloud AI Service

Edit `app/src/main/java/com/meta/wearable/dat/externalsampleapps/smartpicker/stream/StreamViewModel.kt`:

Find the `createAiService()` function (around line 67) and change:

```kotlin
private fun createAiService(): AiAnalysisService {
    // Change from MockAiAnalysisService() to:
    return CloudAiAnalysisService.create(
        apiUrl = "https://your-ai-service.com/v1/analyze",
        apiKey = "your-api-key-here"
    )
}
```

### Step 2: Test the Connection

1. Build and run the app
2. Start streaming from glasses
3. Check Logcat for AI service logs:
   ```bash
   adb logcat | grep -i "CloudAiAnalysisService"
   ```
4. Look for:
   - "Requesting: https://..." (shows API calls)
   - "Response: 200" (success)
   - "Response: 401/403" (authentication error)
   - "Response: 404" (wrong URL)

## Supported AI Services

### Option 1: OpenAI GPT-4 Vision API

```kotlin
return CloudAiAnalysisService.create(
    apiUrl = "https://api.openai.com/v1/chat/completions",
    apiKey = "sk-your-openai-api-key"
)
```

**Note:** You'll need to adapt the request format. OpenAI uses a different format:
```json
{
  "model": "gpt-4-vision-preview",
  "messages": [{
    "role": "user",
    "content": [
      {"type": "text", "text": "Describe this image"},
      {"type": "image_url", "image_url": {"url": "data:image/jpeg;base64,..."}}
    ]
  }]
}
```

### Option 2: Google Cloud Vision API

```kotlin
return CloudAiAnalysisService.create(
    apiUrl = "https://vision.googleapis.com/v1/images:annotate?key=YOUR_API_KEY",
    apiKey = null // API key is in URL
)
```

### Option 3: Custom AI Service

Your service should accept:
- **Method:** POST
- **Content-Type:** application/json
- **Body:**
  ```json
  {
    "image": "base64-encoded-image-string",
    "prompt": "Analyze this scene..."
  }
  ```
- **Response:**
  ```json
  {
    "description": "I see a person walking in a park...",
    "confidence": 0.85
  }
  ```

## Testing Without Real AI Service

### Using Mock Service (Current Default)

The app currently uses `MockAiAnalysisService()` which:
- Returns rotating sample descriptions
- Simulates network delay (500ms)
- Works offline
- Perfect for testing UI and integration

### Testing API Connection

You can test the HTTP client without a real AI service:

1. **Use a test endpoint:**
   ```kotlin
   return CloudAiAnalysisService.create(
       apiUrl = "https://httpbin.org/post", // Test endpoint
       apiKey = null
   )
   ```
   This will show if network requests work (will fail parsing, but you'll see the request in logs).

2. **Use a mock server:**
   - Run a local mock server (e.g., using Postman Mock Server)
   - Point API URL to your mock server
   - Test the full flow

## Error Handling

The improved `CloudAiAnalysisService` now:

✅ **Returns proper errors** instead of mock responses
✅ **Validates configuration** (checks for placeholder URLs)
✅ **Handles network errors** (timeout, connection issues)
✅ **Logs detailed information** for debugging
✅ **Shows errors in UI** so users know what's wrong

### Common Errors

| Error | Cause | Fix |
|-------|-------|-----|
| "AI service URL not configured" | Using placeholder URL | Set real API URL |
| "Cannot reach AI service" | Network/connectivity | Check internet, verify URL |
| "Request timed out" | Service too slow | Increase timeout or optimize |
| "HTTP 401/403" | Invalid/missing API key | Check API key |
| "HTTP 404" | Wrong endpoint URL | Verify API URL |
| "Invalid response format" | API response doesn't match | Check response JSON structure |

## Configuration via BuildConfig (Advanced)

For production, you can use BuildConfig to avoid hardcoding:

1. **Add to `app/build.gradle.kts`:**
   ```kotlin
   android {
       buildTypes {
           debug {
               buildConfigField("String", "AI_API_URL", "\"https://your-dev-api.com\"")
               buildConfigField("String", "AI_API_KEY", "\"dev-key\"")
           }
           release {
               buildConfigField("String", "AI_API_URL", "\"https://your-prod-api.com\"")
               buildConfigField("String", "AI_API_KEY", "\"prod-key\"")
           }
       }
   }
   ```

2. **Use in code:**
   ```kotlin
   return CloudAiAnalysisService.create(
       apiUrl = BuildConfig.AI_API_URL,
       apiKey = BuildConfig.AI_API_KEY
   )
   ```

## Testing Checklist

- [ ] Cloud AI service enabled in `StreamViewModel.kt`
- [ ] API URL configured correctly
- [ ] API key added (if required)
- [ ] Internet permission in AndroidManifest (already added)
- [ ] Test with real glasses and camera stream
- [ ] Check Logcat for API calls
- [ ] Verify response appears in UI
- [ ] Test error handling (disconnect internet, wrong API key)
- [ ] Test TTS speaks the analysis

## Debugging Tips

### View Logs
```bash
# All AI service logs
adb logcat | grep -i "CloudAiAnalysisService"

# All SmartPicker logs
adb logcat | grep -i "SmartPicker\|StreamViewModel"

# Network requests
adb logcat | grep -i "okhttp"
```

### Test API Manually

Use curl to test your API endpoint:
```bash
curl -X POST https://your-api.com/v1/analyze \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer your-api-key" \
  -d '{
    "image": "base64-encoded-test-image",
    "prompt": "Describe this image"
  }'
```

### Verify Image Encoding

The service converts bitmaps to base64. Check the size:
- Typical JPEG: 50-200KB
- Base64 encoded: ~33% larger
- If too large, reduce JPEG quality in `AiAnalysisService.kt` (currently 80%)

## Next Steps

1. **Choose your AI provider** (OpenAI, Google, custom)
2. **Get API credentials**
3. **Update `createAiService()` in StreamViewModel**
4. **Test with real glasses**
5. **Monitor logs for issues**
6. **Optimize based on performance**
