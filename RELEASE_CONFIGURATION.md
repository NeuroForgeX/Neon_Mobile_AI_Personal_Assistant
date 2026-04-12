# Release Configuration Guide

## Overview

This document explains how to build release candidates and final releases for MyHappyBot.

## Build Types

### 1. Debug Build

```bash
./gradlew assembleDebug
```

- **App ID**: `com.forge.bright.debug`
- **Version**: `1.0-rc1-debug`
- **Usage**: Development and testing
- **Features**: Debuggable, no code shrinking

### 2. Release Candidate Build

```bash
./gradlew assembleReleaseCandidate
```

- **App ID**: `com.forge.bright.rc`
- **Version**: `1.0-rc1-rc`
- **Usage**: Pre-release testing
- **Features**: No code obfuscation, signed, production-like
- **ProGuard**: Disabled for easier debugging

### 3. Release Build

```bash
./gradlew assembleRelease
```

- **App ID**: `com.forge.bright`
- **Version**: `1.0-rc1`
- **Usage**: Production deployment
- **Features**: No code obfuscation, signed, production-ready
- **ProGuard**: Disabled for easier debugging

## Keystore Setup

### 1. Create Release Keystore

```bash
keytool -genkey -v -keystore release.keystore -alias your_key_alias -keyalg RSA -keysize 2048 -validity 10000
```

### 2. Configure keystore.properties

Create `keystore.properties` file in project root:

```properties
keystore.file=../keystore/release.keystore
keystore.password=your_keystore_password
keystore.key.alias=your_key_alias
keystore.key.password=your_key_password
```

### 3. Security Notes

- **NEVER** commit `keystore.properties` to version control
- **NEVER** share your keystore passwords
- **BACKUP** your keystore file securely
- Add `keystore.properties` to `.gitignore`

## Build Commands

### Clean Build

```bash
./gradlew clean
```

### Build All Variants

```bash
./gradlew assembleDebug assembleReleaseCandidate assembleRelease
```

### Install Release Candidate

```bash
./gradlew installReleaseCandidate
```

### Generate Signed APK

```bash
./gradlew assembleReleaseCandidate
```

Output: `app/build/outputs/apk/releaseCandidate/release/releaseCandidate.apk`

## Version Management

### Current Version

- **Version Code**: 2
- **Version Name**: 1.0-rc1

### Version Bumping

Update `app/build.gradle.kts`:

```kotlin
defaultConfig {
    versionCode = 3  // Increment for each release
    versionName = "1.0-rc2"  // Update version name
}
```

## ProGuard Configuration

### Main Rules (proguard-rules.pro)

- Basic obfuscation and optimization
- Keeps essential classes for functionality
- Preserves debugging information

### Release Candidate Rules (proguard-rules-release-candidate.pro)

- More aggressive optimization
- Better debugging capabilities
- Preserves more class information for testing

## Testing Before Release

### 1. Install Release Candidate

```bash
adb install app/build/outputs/apk/releaseCandidate/release/releaseCandidate.apk
```

### 2. Test Critical Features

- [ ] App launches successfully
- [ ] Permissions requested properly
- [ ] AI model loading works
- [ ] Chat functionality works
- [ ] Model download works
- [ ] Fullscreen mode works
- [ ] File operations work

### 3. Check Logs

```bash
adb logcat | grep MyHappyBot
```

## Release Checklist

### Before Building Release

- [ ] All tests pass
- [ ] Version number updated
- [ ] Keystore configured
- [ ] ProGuard rules tested
- [ ] Release notes prepared

### After Building Release

- [ ] APK signature verified
- [ ] APK size optimized
- [ ] Installation tested on multiple devices
- [ ] Performance tested
- [ ] Security reviewed

## Troubleshooting

### Common Issues

1. **Keystore not found**: Check `keystore.properties` path
2. **Signing failed**: Verify keystore passwords
3. **ProGuard errors**: Update ProGuard rules
4. **Build crashes**: Check dependencies and versions

### Debugging Release Builds

Enable debugging in release build temporarily:

```kotlin
buildTypes {
    release {
        isDebuggable = true  // Temporary for debugging
        // ... other config
    }
}
```

## Deployment

### Google Play Store

- Use `assembleRelease` for Play Store
- Upload `app-release.aab` (Android App Bundle)
- Follow Play Store guidelines

### Direct Distribution

- Use `assembleReleaseCandidate` for testing
- Distribute `releaseCandidate.apk`
- Ensure installation from unknown sources enabled

## Support

For build issues, check:

1. Gradle logs: `./gradlew build --stacktrace`
2. ProGuard logs: `app/build/outputs/mapping/release/`
3. Device logs: `adb logcat`
