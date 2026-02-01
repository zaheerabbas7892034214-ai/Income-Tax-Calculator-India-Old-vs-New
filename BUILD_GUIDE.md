# Build Guide - Income Tax Calculator Android App

This guide provides detailed instructions for building and running the Income Tax Calculator Android application.

## System Requirements

### Software Requirements
- **Operating System**: Windows 10/11, macOS 10.14+, or Linux (Ubuntu 18.04+)
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Java Development Kit 17 (JDK 17)
- **Gradle**: 8.2 (included via wrapper)
- **Android SDK**: 
  - Minimum SDK: API 24 (Android 7.0)
  - Target SDK: API 34 (Android 14)
  - Compile SDK: API 34

### Hardware Requirements
- **RAM**: Minimum 8 GB (16 GB recommended)
- **Storage**: At least 10 GB free space
- **Internet**: Required for initial Gradle sync and dependency download

## Step-by-Step Build Instructions

### 1. Install Prerequisites

#### Install Android Studio
1. Download Android Studio from: https://developer.android.com/studio
2. Run the installer
3. Follow the setup wizard
4. Install Android SDK components:
   - Android SDK Platform 34
   - Android SDK Build-Tools
   - Android Emulator (if testing on emulator)

#### Install JDK 17
Android Studio typically includes JDK 17. To verify:
1. Open Android Studio
2. Go to File → Project Structure → SDK Location
3. Check "JDK Location" - should show JDK 17

If JDK 17 is not installed:
- Download from: https://adoptium.net/
- Install and configure in Android Studio

### 2. Clone the Repository

```bash
git clone https://github.com/zaheerabbas7892034214-ai/Income-Tax-Calculator-India-Old-vs-New.git
cd Income-Tax-Calculator-India-Old-vs-New
```

### 3. Open Project in Android Studio

1. Launch Android Studio
2. Click "Open" or "Open an existing project"
3. Navigate to the cloned repository folder
4. Click "OK"

### 4. Gradle Sync

Android Studio will automatically start Gradle sync:

1. **First-time sync**: This may take 5-15 minutes as it downloads:
   - Gradle distribution
   - Android Gradle Plugin
   - All project dependencies (Compose, Room, Billing, etc.)
   - Kotlin compiler

2. **Watch for progress**: Check the status bar at the bottom of Android Studio

3. **Common issues during sync**:
   - **No internet connection**: Gradle sync will fail. Ensure internet connectivity.
   - **Proxy settings**: If behind a corporate proxy, configure in File → Settings → Appearance & Behavior → System Settings → HTTP Proxy
   - **Insufficient memory**: Increase heap size in Help → Edit Custom VM Options

### 5. Build the Project

#### Option A: Build via Android Studio UI
1. Click Build → Make Project (or press Ctrl+F9 / Cmd+F9)
2. Wait for build to complete
3. Check "Build" tab at bottom for any errors

#### Option B: Build via Command Line
```bash
# On Linux/macOS
./gradlew assembleDebug

# On Windows
gradlew.bat assembleDebug
```

**Build outputs**:
- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Build time: 2-5 minutes (first build may take longer)

### 6. Run the Application

#### Option A: Run on Emulator
1. Create an emulator (if not exists):
   - Tools → Device Manager
   - Click "Create Device"
   - Select device (e.g., Pixel 5)
   - Select system image: API 34
   - Click "Finish"

2. Run the app:
   - Click Run → Run 'app' (or press Shift+F10)
   - Select emulator from device list
   - Wait for app to launch

#### Option B: Run on Physical Device
1. Enable Developer Options on device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times

2. Enable USB Debugging:
   - Go to Settings → Developer Options
   - Enable "USB Debugging"

3. Connect device via USB

4. Run the app:
   - Click Run → Run 'app'
   - Select your device from the list
   - Click OK

### 7. Build Variants

The project supports multiple build variants:

```bash
# Debug build (debuggable, not optimized)
./gradlew assembleDebug

# Release build (optimized, requires signing)
./gradlew assembleRelease
```

**Note**: Release builds require signing configuration (not included by default for security).

## Troubleshooting

### Problem: Gradle Sync Failed

**Solution 1: Check Internet Connection**
```bash
# Test connectivity to Maven repositories
curl https://repo1.maven.org/maven2/
curl https://dl.google.com/dl/android/maven2/
```

**Solution 2: Invalidate Caches**
1. File → Invalidate Caches → Invalidate and Restart
2. Wait for Android Studio to restart
3. Wait for re-indexing and re-sync

**Solution 3: Clean and Rebuild**
```bash
./gradlew clean
./gradlew build
```

### Problem: Build Failed with "Insufficient Memory"

**Solution**: Increase heap size in `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

### Problem: Dependencies Not Downloading

**Solution 1**: Check repository accessibility
- Ensure Google Maven and Maven Central are accessible
- Try using a VPN if repositories are blocked

**Solution 2**: Use gradle with offline mode disabled
```bash
./gradlew build --refresh-dependencies
```

### Problem: KSP Errors

**Solution**: Ensure KSP version matches Kotlin version
- Kotlin 1.9.10 requires KSP 1.9.10-1.0.13
- Check compatibility at: https://github.com/google/ksp/releases

### Problem: Compose Compiler Version Mismatch

**Solution**: Verify Compose Compiler version matches Kotlin version
- Kotlin 1.9.10 → Compose Compiler 1.5.3
- Update in `app/build.gradle.kts`:
```kotlin
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.3"
}
```

### Problem: Room Schema Export Error

**Solution**: Create schema directory
```bash
mkdir -p app/schemas
```

Update `app/build.gradle.kts`:
```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}
```

### Problem: Billing Library Issues in Debug

**Note**: Google Play Billing only works on:
- Real devices with Google Play Store
- Release builds signed with release key
- Devices with valid Google account

For testing:
- Use the Google Play Billing Test library
- Set up test accounts in Google Play Console

## Testing

### Run Unit Tests
```bash
./gradlew test
```

### Run Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

**Note**: Tests are not included in this initial implementation but can be added.

## Creating Signed Release Build

### 1. Generate Keystore
```bash
keytool -genkey -v -keystore income-tax-keystore.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias income-tax-key
```

### 2. Configure Signing in `app/build.gradle.kts`
```kotlin
android {
    signingConfigs {
        create("release") {
            storeFile = file("../income-tax-keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "income-tax-key"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    
    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

### 3. Build Release APK
```bash
export KEYSTORE_PASSWORD=your_password
export KEY_PASSWORD=your_password
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### 4. Build App Bundle (for Play Store)
```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## Project Structure Verification

After successful build, verify the following structure exists:

```
Income-Tax-Calculator-India-Old-vs-New/
├── app/
│   ├── build.gradle.kts          ✓
│   ├── src/
│   │   └── main/
│   │       ├── AndroidManifest.xml ✓
│   │       ├── java/com/yourcompany/incometax/ ✓
│   │       └── res/ ✓
├── build.gradle.kts              ✓
├── settings.gradle.kts           ✓
├── gradle.properties             ✓
├── gradlew                       ✓
├── gradlew.bat                   ✓
└── gradle/wrapper/
    └── gradle-wrapper.properties ✓
```

## Performance Optimization

### Build Performance
1. **Enable Gradle Daemon** (enabled by default)
2. **Enable Parallel Builds** in `gradle.properties`:
   ```properties
   org.gradle.parallel=true
   org.gradle.workers.max=4
   ```
3. **Enable Build Cache**:
   ```properties
   org.gradle.caching=true
   ```

### App Performance
1. **Enable R8** (enabled in release builds)
2. **Enable Multidex** (if needed for large apps)
3. **Optimize images** in res/drawable

## Deployment Checklist

Before deploying to production:

- [ ] Update app version in `app/build.gradle.kts`
- [ ] Test on multiple devices (different screen sizes, API levels)
- [ ] Test in-app purchases with test accounts
- [ ] Verify PDF export on various devices
- [ ] Test both light and dark themes
- [ ] Verify all strings are localized (if supporting multiple languages)
- [ ] Update ProGuard rules if needed
- [ ] Create release notes
- [ ] Prepare Play Store listing (screenshots, description, etc.)
- [ ] Configure Play Store console (app signing, test tracks)
- [ ] Set up licensing for in-app products
- [ ] Submit for review

## Additional Resources

- **Android Developer Documentation**: https://developer.android.com
- **Jetpack Compose Documentation**: https://developer.android.com/jetpack/compose
- **Material 3 Guidelines**: https://m3.material.io
- **Billing Library Guide**: https://developer.android.com/google/play/billing
- **Room Database Guide**: https://developer.android.com/training/data-storage/room

## Support

For issues related to:
- **Build problems**: Check troubleshooting section above
- **Code issues**: Review IMPLEMENTATION_SUMMARY.md
- **Feature questions**: Review README.md

## Summary

This build guide covers:
✅ System requirements
✅ Installation steps
✅ Build instructions
✅ Running the app
✅ Troubleshooting common issues
✅ Creating release builds
✅ Performance optimization
✅ Deployment checklist

The project is ready to build and run. Follow the steps above for a successful build.
