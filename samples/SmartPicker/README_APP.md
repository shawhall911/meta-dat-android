# SmartView - AI-Powered Scene Understanding

SmartView is an Android app that uses Ray-Ban Meta glasses to capture video and analyze scenes with AI.

## Features

- **Real-time Camera Streaming**: Stream video from Ray-Ban Meta glasses to your phone
- **AI Scene Analysis**: Analyze video frames using Hugging Face AI
- **Audio Feedback**: Text-to-speech reads AI analysis aloud
- **Settings UI**: Switch between Mock (offline) and HuggingFace AI services

## Setup

### 1. Prerequisites

- Android phone paired with Ray-Ban Meta glasses
- Meta AI app installed with Developer Mode enabled
- Android Studio (for building)
- Hugging Face API key (free tier available)

### 2. API Key Configuration

1. Get your free Hugging Face API key at: https://huggingface.co/settings/tokens
2. Create or edit `local.properties` in the project root:
   ```properties
   huggingface.api.key=YOUR_API_KEY_HERE
   ```
3. This file is automatically ignored by git (keeps your key secure)

### 3. Enable Developer Mode

1. Open Meta AI app on your phone
2. Go to Settings > About
3. Tap "Build number" 7 times to enable Developer Mode
4. Go to Developer Options > Enable "Wearables Developer Mode"

### 4. Build and Install

```bash
./gradlew assembleDebug
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Usage

1. **Connect Glasses**: Tap "Connect my glasses" and follow the Meta AI app flow
2. **Configure AI**: Tap the settings icon (gear) to switch between:
   - **Hugging Face API**: Real AI analysis (requires internet)
   - **Mock Service**: Offline testing mode
3. **Start Analysis**: Tap "Start AI Analysis" when device is connected
4. **View Results**: AI analysis appears on screen and is read aloud

## Architecture

```
app/
├── ai/                    # AI service implementations
│   ├── AiAnalysisService.kt
│   ├── HuggingFaceAiService.kt
│   └── MockAiAnalysisService.kt
├── audio/                 # Text-to-speech service
├── settings/              # User preferences
├── stream/                # Camera streaming & AI analysis
├── ui/                    # Compose UI screens
└── wearables/             # DAT SDK integration
```

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumented tests:
```bash
./gradlew connectedAndroidTest
```

## Troubleshooting

### "Waiting for an active device"
- Ensure glasses are powered on and connected via Bluetooth
- Check Meta AI app shows glasses as connected
- Try restarting the app

### "Analysis unavailable"
- Verify internet connection
- Check API key in `local.properties`
- Try switching to Mock mode in settings

### Camera permission errors
- Grant camera permission in Meta AI app
- Reconnect glasses in Meta AI app

## License

This project is for educational purposes. See the main repository for license details.

## Credits

- Meta Wearables Device Access Toolkit (DAT)
- Hugging Face Inference API
