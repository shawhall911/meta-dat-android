# APK Size Analysis - SmartPicker (87MB)

## Size Breakdown

The APK is large primarily due to:

### 1. Native Libraries (~40MB)
- **arm64-v8a**: ~25MB (for 64-bit devices)
- **armeabi-v7a**: ~15MB (for 32-bit devices)

Major native libraries from Meta Wearable SDK:
- libxplat_wearable_xms_*: ~2.5MB
- libxplat_wearable_comms_calling_hera_*: ~1.2MB
- libsession_jni.so: ~1MB
- libmanifest_jni.so: ~600KB
- libserializer.so: ~1MB
- libairshield_light_mbed_jni.so: ~848KB + 568KB
- libc++_shared.so: ~991KB + 689KB
- And 50+ other libraries

### 2. DEX Files (~52MB)
- classes.dex: 32MB
- classes2.dex: 11MB  
- classes3.dex: 8.7MB

These contain all the compiled Kotlin/Java code plus dependencies (Compose, Meta SDK, OkHttp, etc.)

### 3. Resources (~1MB)
- Images, icons, layouts, etc.

## Why So Large?

**Meta Wearable SDK** is the primary cause:
- Video streaming requires many native codecs
- Audio processing libraries
- Camera access libraries
- Communication protocols (Bluetooth, WiFi)
- Security/encryption libraries (mbedtls)
- All of these are included for both ARM architectures

This is **normal** for apps using the Meta Wearable SDK. The SDK includes many native libraries for:
- Real-time video streaming
- Audio processing for TTS
- Bluetooth communication with glasses
- Security protocols
- Device management

## Solutions to Reduce APK Size

### Option 1: Use Android App Bundle (Recommended)
```bash
./gradlew bundleRelease
```
This creates an `.aab` file. Google Play will:
- Generate optimized APKs per device
- Include only the needed architecture (arm64 OR armeabi)
- Reduce download size by ~50%

### Option 2: Build Separate APKs per Architecture
Add to `app/build.gradle.kts`:
```kotlin
android {
    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a")
            isUniversalApk = false
        }
    }
}
```

This creates 2 smaller APKs (~45MB each) instead of 1 large APK.

### Option 3: Enable App Bundle for Debug
```bash
./gradlew bundleDebug
```

### Option 4: ProGuard/R8 (Already Enabled)
The release build already uses R8, which removes unused code.

### Option 5: Remove Unused Architectures
If targeting only modern devices, remove 32-bit support:
```kotlin
android {
    ndk {
        abiFilters += listOf("arm64-v8a")
    }
}
```

This reduces size by ~15MB but excludes older devices.

## Comparison with Similar Apps

- **WhatsApp**: ~45MB
- **Instagram**: ~60MB
- **Messenger**: ~50MB
- **SmartPicker**: ~87MB (includes Meta Wearable SDK)

Our app is larger because it includes the full Meta Wearable SDK for glasses connectivity.

## Recommendation

**For production**: Use Android App Bundle (.aab)
- Upload to Google Play
- Users download only what they need
- Actual download size: ~45MB

**For testing**: Current APK is fine
- Install time is acceptable
- One APK works on all devices

## File Size Details

```
Total: 88.9MB (uncompressed)
Download: 87MB (compressed)

Major components:
- Native libs (2 architectures): 40MB
- DEX files: 52MB
- Resources: 1MB
```

## Action Items

For immediate testing: APK is acceptable
For Google Play release: Build App Bundle

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`
