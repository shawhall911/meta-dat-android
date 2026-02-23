# Google Cloud Vision API Setup (Recommended Alternative)

Since Hugging Face API endpoints are currently not working, let's use **Google Cloud Vision API** which is:
- ✅ **Reliable** - Well-established service
- ✅ **Free tier** - 1,000 requests/month forever free
- ✅ **Easy setup** - Just need an API key
- ✅ **Works immediately** - No endpoint issues

## Quick Setup (5 minutes)

### Step 1: Get Google Cloud API Key

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project (or select existing)
3. Enable "Cloud Vision API":
   - Go to "APIs & Services" → "Library"
   - Search for "Cloud Vision API"
   - Click "Enable"
4. Create API Key:
   - Go to "APIs & Services" → "Credentials"
   - Click "Create Credentials" → "API Key"
   - Copy the API key
   - (Optional) Restrict the key to "Cloud Vision API" only for security

### Step 2: Update Your Code

Edit `app/src/main/java/com/meta/wearable/dat/externalsampleapps/smartpicker/stream/StreamViewModel.kt`:

Find the `createAiService()` function and change:

```kotlin
private fun createAiService(): AiAnalysisService {
    // Use Google Vision API (reliable and free)
    return GoogleVisionAiService(apiKey = "YOUR_GOOGLE_API_KEY_HERE")
}
```

### Step 3: Test

The app will now use Google Vision API which:
- Detects objects, scenes, and activities in images
- Returns descriptions like: "I can see: person, outdoor, park, tree, sky"
- Works reliably with the free tier

## Free Tier Limits

- **1,000 requests/month** - Forever free
- Perfect for testing and development
- After that, it's $1.50 per 1,000 requests (very affordable)

## Why This is Better

- ✅ Actually works (unlike Hugging Face router issues)
- ✅ More reliable
- ✅ Better documentation
- ✅ Faster response times
- ✅ More accurate for scene understanding

## Alternative: Keep Using Mock

If you don't want to set up Google Vision right now, you can keep using `MockAiAnalysisService()` for testing the app flow, and switch to a real AI service later.
