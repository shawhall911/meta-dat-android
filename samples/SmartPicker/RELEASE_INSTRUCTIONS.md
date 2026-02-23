# SmartPicker Release Instructions

The app is ready to publish! Follow these steps:

## What's Been Done

✅ **Code committed and pushed to GitHub**
- Secure API key management (via local.properties)
- Unit tests for all ViewModels and UI states
- Integration tests for Compose screens
- UI improvements (cancel button, settings dialog)
- Updated app icon

✅ **Production APK built**
- Located at: `/Users/peizheng/Downloads/SmartPicker-v1.3-Production.apk`
- Size: 87MB
- Includes working HuggingFace API integration

## Create GitHub Release (Manual Steps)

1. **Go to GitHub**:
   - Visit: https://github.com/shawhall911/meta-dat-android/releases/new

2. **Create new release**:
   - Tag version: `v1.3`
   - Title: `SmartPicker v1.3 - Production Release`
   
3. **Upload APK**:
   - Attach: `/Users/peizheng/Downloads/SmartPicker-v1.3-Production.apk`
   
4. **Release notes** (copy this):

```markdown
## Features
- Real-time camera streaming from Ray-Ban Meta glasses
- AI scene analysis using Hugging Face API
- Audio feedback via text-to-speech
- Settings UI to switch between Mock and HuggingFace AI services
- Cancel button when waiting for device connection
- Updated glasses app icon

## Setup Instructions
1. Install APK on Android phone paired with Ray-Ban Meta glasses
2. Open Meta AI app and enable Developer Mode
3. Launch SmartPicker and tap 'Connect my glasses'
4. Configure AI service in Settings (gear icon)
5. Start AI analysis

## API Key Setup
To use HuggingFace AI:
1. Get free API key at https://huggingface.co/settings/tokens
2. Build from source and add key to local.properties

## Testing
- Unit tests included for ViewModels and UI state
- Integration tests for Compose UI screens
```

## Alternative: Use GitHub CLI

If you want to use `gh` CLI, authenticate first:
```bash
gh auth login
```

Then create the release:
```bash
gh release create v1.3 \
  --title "SmartPicker v1.3 - Production Release" \
  --notes-file RELEASE_NOTES.md \
  --repo shawhall911/meta-dat-android \
  /Users/peizheng/Downloads/SmartPicker-v1.3-Production.apk
```

## What's in This Release

### Code Quality
- ✅ Unit tests (WearablesViewModel, UI states)
- ✅ Integration tests (Compose screens)
- ✅ Secure API key management
- ✅ Clean architecture

### Features
- ✅ Real-time camera streaming
- ✅ AI scene analysis
- ✅ Text-to-speech feedback
- ✅ Settings UI
- ✅ Cancel button
- ✅ Updated app icon

### Security
- ✅ API keys stored in local.properties (gitignored)
- ✅ No hardcoded secrets in source code
- ✅ BuildConfig used for secure key injection

## Next Steps

1. Create the GitHub release manually (link above)
2. Share the release URL with users
3. Users can download and install the APK directly
