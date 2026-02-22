# SmartPicker App

A sample Android application demonstrating AI-powered scene understanding using Meta Wearables Device Access Toolkit. This app showcases streaming video from Meta AI glasses, analyzing frames with cloud-based AI, and providing audio feedback via text-to-speech.

## Features

- Connect to Meta AI glasses
- Stream camera feed from the device
- Analyze video frames using cloud-based AI for scene understanding
- Receive audio feedback via text-to-speech describing the scene
- Real-time AI analysis overlay on video stream

## Prerequisites

- Android Studio Arctic Fox (2021.3.1) or newer
- JDK 11 or newer
- Android SDK 31+ (Android 12.0+)
- Meta Wearables Device Access Toolkit (included as a dependency)
- A Meta AI glasses device for testing (optional for development)
- Cloud AI service endpoint (configured in `AiAnalysisService.kt`)

## Building the app

### Using Android Studio

1. Clone this repository
1. Open the project in Android Studio
1. Add your personal access token (classic) to the `local.properties` file (see [SDK for Android setup](https://wearables.developer.meta.com/docs/getting-started-toolkit/#sdk-for-android-setup))
1. (Optional) Configure your AI service endpoint in `app/src/main/java/com/meta/wearable/dat/externalsampleapps/smartpicker/ai/AiAnalysisService.kt`
   - Update `apiUrl` with your AI service endpoint
   - Add your API key if required
   - Or use the `MockAiAnalysisService` for testing without a real AI service
1. Click **File** > **Sync Project with Gradle Files**
1. Click **Run** > **Run...** > **app**

## Running the app

1. Turn 'Developer Mode' on in the Meta AI app.
1. Launch the app.
1. Press the "Connect" button to complete app registration.
1. Once connected, press "Start AI Analysis" to begin streaming
1. The camera stream from the device will be displayed
1. AI analysis will appear as an overlay on the video
1. Audio feedback will speak the AI analysis results
1. Use the on-screen controls to:
   - Enable/disable AI analysis
   - Stop streaming

## AI Service Configuration

The app includes a mock AI service for testing. To use a real cloud AI service:

1. Open `app/src/main/java/com/meta/wearable/dat/externalsampleapps/smartpicker/ai/AiAnalysisService.kt`
2. Replace `MockAiAnalysisService()` with `CloudAiAnalysisService()` in `StreamViewModel.kt`
3. Update the `apiUrl` and `apiKey` parameters in `CloudAiAnalysisService`
4. Ensure your AI service accepts:
   - POST requests with JSON body containing base64-encoded images
   - Returns JSON with `description` field containing scene analysis text

Example AI service response format:
```json
{
  "description": "I see a person walking in a park with trees in the background.",
  "confidence": 0.85
}
```

## Troubleshooting

For issues related to the Meta Wearables Device Access Toolkit, please refer to the [developer documentation](https://wearables.developer.meta.com/docs/develop/) or visit the [discussions forum](https://github.com/facebook/meta-wearables-dat-android/discussions)
