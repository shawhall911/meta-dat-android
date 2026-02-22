# SmartPicker End-to-End Testing Guide

## Prerequisites Setup

### 1. Copy Required Files (if not already present)

From the `CameraAccess` sample, copy these files to `SmartPicker`:
```bash
cd /Users/peizheng/StudioProjects/meta-dat-android/samples

# Copy Gradle wrapper files
cp CameraAccess/gradlew SmartPicker/
cp CameraAccess/gradlew.bat SmartPicker/
cp CameraAccess/gradle/wrapper/gradle-wrapper.jar SmartPicker/gradle/wrapper/

# Copy keystore for signing
cp CameraAccess/app/sample.keystore SmartPicker/app/

# Make gradlew executable
chmod +x SmartPicker/gradlew
```

### 2. Verify Configuration

Check that `local.properties` has:
- `github_token` (for accessing Meta SDK)
- `sdk.dir` (pointing to your Android SDK)

## Building the App

### Option A: Using Android Studio (Recommended)

1. **Open Project**
   - Open Android Studio
   - File → Open → Navigate to `samples/SmartPicker`
   - Wait for Gradle sync to complete

2. **Sync Gradle**
   - File → Sync Project with Gradle Files
   - Wait for dependencies to download

3. **Build APK**
   - Build → Build Bundle(s) / APK(s) → Build APK(s)
   - Or use: Build → Make Project (Ctrl+F9 / Cmd+F9)

### Option B: Using Command Line

```bash
cd /Users/peizheng/StudioProjects/meta-dat-android/samples/SmartPicker

# Set Java home (if needed)
export JAVA_HOME=/usr/local/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home

# Build debug APK
./gradlew assembleDebug

# APK will be at: app/build/outputs/apk/debug/app-debug.apk
```

## Installing on Your Phone

### Method 1: Direct Install via Android Studio

1. **Enable USB Debugging on Phone**
   - Settings → About Phone → Tap "Build Number" 7 times
   - Settings → Developer Options → Enable "USB Debugging"
   - Connect phone via USB

2. **Run from Android Studio**
   - Click Run button (green play icon)
   - Select your phone from device list
   - App will build and install automatically

### Method 2: Install APK Manually

1. **Transfer APK to Phone**
   ```bash
   # Using ADB
   adb install app/build/outputs/apk/debug/app-debug.apk
   
   # Or transfer via USB/email and install manually
   ```

2. **Allow Installation from Unknown Sources**
   - Settings → Security → Enable "Install from Unknown Sources"
   - Open the APK file and install

## Setting Up Meta AI Glasses

### 1. Enable Developer Mode

1. **On Your Phone:**
   - Open the **Meta AI** app
   - Go to Settings → Developer Mode
   - Enable Developer Mode
   - This allows apps to connect to your glasses

2. **Pair Glasses (if not already)**
   - Ensure glasses are paired with your phone via Bluetooth
   - Glasses should be powered on and in range

## Testing the App End-to-End

### Step 1: Launch SmartPicker

1. Open SmartPicker app on your phone
2. Grant permissions when prompted:
   - Bluetooth
   - Bluetooth Connect
   - Internet

### Step 2: Connect to Glasses

1. **Tap "Connect my glasses" button**
   - You'll be redirected to Meta AI app
   - Confirm the connection request
   - Return to SmartPicker app

2. **Wait for Registration**
   - App will show "Waiting for an active device"
   - Once glasses are detected, button changes to "Start AI Analysis"

### Step 3: Start Streaming & AI Analysis

1. **Tap "Start AI Analysis"**
   - App requests camera permission from glasses
   - Confirm in Meta AI app if prompted
   - Video stream should appear

2. **Observe AI Analysis**
   - Video feed from glasses camera appears
   - AI analysis overlay appears at top showing scene description
   - Audio feedback speaks the analysis (check phone volume!)

3. **Test Controls**
   - **"Disable AI" / "Enable AI"**: Toggle analysis on/off
   - **"Stop Stream"**: Stop streaming and return to device selection

### Step 4: Verify Features

✅ **Video Streaming**
- Live video feed from glasses camera
- Smooth frame updates

✅ **AI Analysis**
- Analysis text appears in overlay card
- Updates every ~2 seconds
- Currently using mock AI (will show sample descriptions)

✅ **Audio Feedback**
- Text-to-speech reads analysis
- Check phone volume is up
- Speaking indicator (volume icon) appears when active

## Troubleshooting

### App Won't Build

**Issue**: Gradle sync fails
- **Fix**: Check `local.properties` has correct `github_token`
- **Fix**: Verify Android SDK path in `local.properties`

**Issue**: Missing dependencies
- **Fix**: File → Invalidate Caches → Restart
- **Fix**: Delete `.gradle` folder and rebuild

### Can't Connect to Glasses

**Issue**: "Waiting for an active device" forever
- **Fix**: Ensure Developer Mode is enabled in Meta AI app
- **Fix**: Check glasses are powered on and paired
- **Fix**: Restart Meta AI app and try again

**Issue**: Permission denied
- **Fix**: Go to Meta AI app → Settings → App Permissions
- **Fix**: Grant camera permission to SmartPicker

### No Video Stream

**Issue**: Black screen or no video
- **Fix**: Check camera permission was granted
- **Fix**: Ensure glasses camera is working (test in Meta AI app)
- **Fix**: Try stopping and restarting stream

### No Audio Feedback

**Issue**: Text appears but no speech
- **Fix**: Check phone volume is up
- **Fix**: Verify TTS is initialized (check Logcat for errors)
- **Fix**: Test TTS in phone settings → Accessibility → Text-to-Speech

### AI Analysis Not Working

**Issue**: No analysis overlay
- **Fix**: Currently using mock AI - this is expected
- **Fix**: To use real AI, configure `CloudAiAnalysisService` in `AiAnalysisService.kt`

## Testing Checklist

- [ ] App builds successfully
- [ ] App installs on phone
- [ ] Permissions granted
- [ ] App connects to glasses
- [ ] Video stream displays
- [ ] AI analysis overlay appears
- [ ] Audio feedback works
- [ ] Enable/Disable AI toggle works
- [ ] Stop stream button works

## Next Steps

1. **Configure Real AI Service** (Optional)
   - Edit `AiAnalysisService.kt`
   - Replace `MockAiAnalysisService()` with `CloudAiAnalysisService()`
   - Add your AI service URL and API key

2. **Customize Analysis**
   - Modify analysis interval in `StreamViewModel.kt` (ANALYSIS_INTERVAL_MS)
   - Adjust AI prompt in `AiAnalysisService.kt`
   - Customize TTS settings in `TextToSpeechService.kt`

3. **Add Features**
   - Photo capture
   - Analysis history
   - Custom voice settings
   - Analysis confidence thresholds

## Debug Tips

### View Logs

```bash
# Using ADB
adb logcat | grep -i "SmartPicker\|StreamViewModel\|AiAnalysis\|TextToSpeech"

# Or in Android Studio
# View → Tool Windows → Logcat
# Filter by: SmartPicker
```

### Common Log Tags
- `StreamViewModel`: Video streaming and analysis
- `AiAnalysisService`: AI service calls
- `TextToSpeechService`: TTS initialization and speech

### Test Mock AI

The mock AI service cycles through sample descriptions. You should see:
- "I see a person walking in a park..."
- "There's a table with objects..."
- "I can see a building..."
- etc.

This confirms the integration is working before connecting a real AI service.
