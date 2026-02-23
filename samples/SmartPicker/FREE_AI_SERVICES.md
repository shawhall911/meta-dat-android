# Free Cloud AI Services for Image Analysis

## 🏆 Top Recommendations

### 1. **Google Cloud Vision API** ⭐ BEST FOR SCENE UNDERSTANDING
**Free Tier:** 1,000 requests/month (forever free)

**Why it's great:**
- Excellent for scene understanding and object detection
- Works well with real-world images from glasses
- Fast response times
- Good documentation

**Setup:**
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project (or use existing)
3. Enable "Cloud Vision API"
4. Create credentials → API Key
5. Restrict the API key to Vision API only (for security)

**Integration:**
```kotlin
// Note: Google Vision uses a different request format
// You'll need to adapt CloudAiAnalysisService or create a wrapper
return CloudAiAnalysisService.create(
    apiUrl = "https://vision.googleapis.com/v1/images:annotate?key=YOUR_API_KEY",
    apiKey = null // Key is in URL
)
```

**Request Format:**
```json
{
  "requests": [{
    "image": {"content": "base64-encoded-image"},
    "features": [{"type": "LABEL_DETECTION", "maxResults": 10}]
  }]
}
```

---

### 2. **Hugging Face Inference API** ⭐ EASIEST TO USE
**Free Tier:** Unlimited (with rate limits)

**Why it's great:**
- Completely free for public models
- No credit card required
- Many pre-trained vision models
- Simple API

**Setup:**
1. Go to [Hugging Face](https://huggingface.co/)
2. Sign up (free)
3. Go to Settings → Access Tokens
4. Create a new token

**Recommended Models:**
- `nlpconnect/vit-gpt2-image-captioning` - Generates descriptions
- `Salesforce/blip-image-captioning-base` - Scene understanding
- `microsoft/git-base` - Image to text

**Integration:**
```kotlin
return CloudAiAnalysisService.create(
    apiUrl = "https://api-inference.huggingface.co/models/Salesforce/blip-image-captioning-base",
    apiKey = "hf_your_token_here"
)
```

**Request Format:**
```json
{
  "inputs": "data:image/jpeg;base64,base64-encoded-image"
}
```

---

### 3. **Azure Computer Vision** ⭐ GENEROUS FREE TIER
**Free Tier:** 5,000 transactions/month (first 3 months), then 20/month

**Why it's great:**
- Very generous free tier initially
- Good for object detection
- Multiple analysis types

**Setup:**
1. Go to [Azure Portal](https://portal.azure.com/)
2. Create a Computer Vision resource
3. Get your API key and endpoint

**Integration:**
```kotlin
return CloudAiAnalysisService.create(
    apiUrl = "https://YOUR_REGION.api.cognitive.microsoft.com/vision/v3.2/describe",
    apiKey = "your-azure-key"
)
```

---

### 4. **Replicate** ⭐ BEST FOR ADVANCED MODELS
**Free Tier:** $10 free credits (one-time)

**Why it's great:**
- Access to cutting-edge models (GPT-4 Vision, etc.)
- Easy to use
- Good for experimentation

**Setup:**
1. Sign up at [Replicate](https://replicate.com/)
2. Get your API token
3. Choose a model (e.g., `salesforce/blip-2`)

---

### 5. **Cloudflare Workers AI** ⭐ NEW & FREE
**Free Tier:** 10,000 requests/day (free forever)

**Why it's great:**
- Very generous free tier
- Fast (edge computing)
- No credit card needed
- Multiple vision models

**Setup:**
1. Sign up at [Cloudflare](https://cloudflare.com/)
2. Go to Workers AI
3. Get your API token

---

## 🎯 My Recommendation for SmartPicker

### **Best Choice: Hugging Face Inference API**

**Why:**
1. ✅ **Completely free** - No credit card, no limits (just rate limits)
2. ✅ **Easy integration** - Simple API format
3. ✅ **Good models** - BLIP models work great for scene understanding
4. ✅ **No setup complexity** - Just sign up and get token

### Quick Setup with Hugging Face:

1. **Sign up:** https://huggingface.co/join
2. **Get token:** Settings → Access Tokens → New token
3. **Update StreamViewModel.kt:**

```kotlin
private fun createAiService(): AiAnalysisService {
    // Hugging Face BLIP model for scene understanding
    return CloudAiAnalysisService.create(
        apiUrl = "https://api-inference.huggingface.co/models/Salesforce/blip-image-captioning-base",
        apiKey = "hf_your_token_here" // Replace with your token
    )
}
```

**Note:** You'll need to adapt the response parsing since Hugging Face returns:
```json
[{"generated_text": "a person walking in a park"}]
```

---

## 🔧 Adapting CloudAiAnalysisService for Different APIs

Since different APIs use different formats, you have two options:

### Option 1: Create API-Specific Services

Create `HuggingFaceAiService.kt`:

```kotlin
class HuggingFaceAiService(
    private val apiKey: String,
    private val model: String = "Salesforce/blip-image-captioning-base"
) : AiAnalysisService {
    
    override suspend fun analyzeFrame(bitmap: Bitmap): Result<AiAnalysisResponse> {
        // Convert to base64
        val base64Image = bitmapToBase64(bitmap)
        
        // Hugging Face format
        val requestBody = """
        {
            "inputs": "data:image/jpeg;base64,$base64Image"
        }
        """.trimIndent()
        
        // Make request...
        // Parse response: [{"generated_text": "description"}]
    }
}
```

### Option 2: Make CloudAiAnalysisService More Flexible

I can update `CloudAiAnalysisService` to support different API formats. Would you like me to do that?

---

## 📊 Comparison Table

| Service | Free Tier | Setup Difficulty | Best For |
|--------|-----------|------------------|----------|
| **Hugging Face** | Unlimited (rate limited) | ⭐ Easy | Scene understanding |
| **Google Vision** | 1,000/month | ⭐⭐ Medium | Object detection |
| **Azure Vision** | 5,000/month (then 20) | ⭐⭐ Medium | General analysis |
| **Replicate** | $10 credits | ⭐ Easy | Advanced models |
| **Cloudflare AI** | 10,000/day | ⭐⭐ Medium | Fast responses |

---

## 🚀 Quick Start with Hugging Face (Recommended)

1. **Sign up:** https://huggingface.co/join
2. **Get token:** https://huggingface.co/settings/tokens
3. **Test the API:**
   ```bash
   curl https://api-inference.huggingface.co/models/Salesforce/blip-image-captioning-base \
     -H "Authorization: Bearer YOUR_TOKEN" \
     -H "Content-Type: application/json" \
     -d '{"inputs": "https://example.com/image.jpg"}'
   ```
4. **Update your code** (see above)

---

## 💡 Next Steps

1. Choose a service (I recommend Hugging Face)
2. Sign up and get API key
3. Let me know which one you choose, and I'll help you adapt the code to work with it!

Would you like me to:
- Create a Hugging Face-specific service implementation?
- Update CloudAiAnalysisService to be more flexible?
- Create adapters for multiple services?
