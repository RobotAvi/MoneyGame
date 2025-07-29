#!/bin/bash

# Test script for Android SDK setup
# This script verifies that the Android SDK is properly installed and configured

set -e

echo "🧪 ==== TESTING ANDROID SDK SETUP ===="

# Check environment variables
echo "🔧 Checking environment variables..."
if [ -z "$ANDROID_SDK_ROOT" ]; then
    export ANDROID_SDK_ROOT=/home/runner/android-sdk
    echo "⚠️ ANDROID_SDK_ROOT not set, using default: $ANDROID_SDK_ROOT"
fi

if [ -z "$ANDROID_HOME" ]; then
    export ANDROID_HOME=/home/runner/android-sdk
    echo "⚠️ ANDROID_HOME not set, using default: $ANDROID_HOME"
fi

export PATH="$PATH:/home/runner/android-sdk/emulator:/home/runner/android-sdk/platform-tools:/home/runner/android-sdk/cmdline-tools/latest/bin"

echo "🔧 Environment:"
echo "  ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
echo "  ANDROID_HOME: $ANDROID_HOME"
echo "  PATH includes Android tools: $(echo $PATH | grep -o '/home/runner/android-sdk/[^:]*' | head -3 | tr '\n' ' ')"

# Check directory structure
echo "🔧 Checking directory structure..."
if [ ! -d "$ANDROID_SDK_ROOT" ]; then
    echo "❌ ANDROID_SDK_ROOT directory does not exist: $ANDROID_SDK_ROOT"
    exit 1
fi

if [ ! -d "$ANDROID_SDK_ROOT/cmdline-tools/latest" ]; then
    echo "❌ cmdline-tools/latest directory does not exist"
    exit 1
fi

if [ ! -d "$ANDROID_SDK_ROOT/platform-tools" ]; then
    echo "❌ platform-tools directory does not exist"
    exit 1
fi

if [ ! -d "$ANDROID_SDK_ROOT/emulator" ]; then
    echo "❌ emulator directory does not exist"
    exit 1
fi

echo "✅ Directory structure is correct"

# Check executable files
echo "🔧 Checking executable files..."
if [ ! -f "$ANDROID_SDK_ROOT/platform-tools/adb" ]; then
    echo "❌ adb not found"
    exit 1
fi

if [ ! -f "$ANDROID_SDK_ROOT/emulator/emulator" ]; then
    echo "❌ emulator not found"
    exit 1
fi

if [ ! -f "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]; then
    echo "❌ sdkmanager not found"
    exit 1
fi

if [ ! -f "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" ]; then
    echo "❌ avdmanager not found"
    exit 1
fi

echo "✅ All required executables found"

# Check file permissions
echo "🔧 Checking file permissions..."
if [ ! -x "$ANDROID_SDK_ROOT/platform-tools/adb" ]; then
    echo "❌ adb is not executable"
    exit 1
fi

if [ ! -x "$ANDROID_SDK_ROOT/emulator/emulator" ]; then
    echo "❌ emulator is not executable"
    exit 1
fi

if [ ! -x "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager" ]; then
    echo "❌ sdkmanager is not executable"
    exit 1
fi

if [ ! -x "$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager" ]; then
    echo "❌ avdmanager is not executable"
    exit 1
fi

echo "✅ All executables have proper permissions"

# Test command execution
echo "🔧 Testing command execution..."
echo "📱 Testing adb version:"
$ANDROID_SDK_ROOT/platform-tools/adb version || { echo "❌ adb version failed"; exit 1; }

echo "📱 Testing emulator version:"
$ANDROID_SDK_ROOT/emulator/emulator -version || { echo "❌ emulator version failed"; exit 1; }

echo "📱 Testing sdkmanager list:"
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/sdkmanager --list | head -5 || { echo "❌ sdkmanager list failed"; exit 1; }

echo "📱 Testing avdmanager list:"
$ANDROID_SDK_ROOT/cmdline-tools/latest/bin/avdmanager list avd || { echo "❌ avdmanager list failed"; exit 1; }

echo "✅ All commands executed successfully"

# Check system images
echo "🔧 Checking system images..."
if [ ! -d "$ANDROID_SDK_ROOT/system-images/android-34/default/x86_64" ]; then
    echo "❌ Android 34 system image not found"
    exit 1
fi

echo "✅ System images are available"

echo "🎉 Android SDK setup test completed successfully!"
echo "📱 All components are properly installed and configured"