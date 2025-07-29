#!/bin/bash

# Fallback script for Android emulator setup
# This script is used when the main emulator setup fails

set -e

echo "🔄 ==== FALLBACK EMULATOR SETUP ===="

# Check if APK file is provided
if [ -z "$1" ]; then
    echo "❌ Error: APK file path not provided"
    exit 1
fi

APK_FILE="$1"
echo "📱 APK file: $APK_FILE"

# Check if APK exists
if [ ! -f "$APK_FILE" ]; then
    echo "❌ Error: APK file not found: $APK_FILE"
    exit 1
fi

# Setup environment variables
export ANDROID_SDK_ROOT=/home/runner/android-sdk
export ANDROID_HOME=/home/runner/android-sdk
export PATH="$PATH:/home/runner/android-sdk/emulator:/home/runner/android-sdk/platform-tools:/home/runner/android-sdk/cmdline-tools/latest/bin"

echo "🔧 Environment setup:"
echo "  ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
echo "  ANDROID_HOME: $ANDROID_HOME"

# Kill any existing emulator processes
echo "🔧 Killing existing emulator processes..."
pkill -f emulator 2>/dev/null || true
pkill -f qemu 2>/dev/null || true
adb kill-server 2>/dev/null || true
sleep 5

# Verify Android SDK installation
echo "🔧 Verifying Android SDK installation..."
if [ ! -f "$ANDROID_SDK_ROOT/platform-tools/adb" ]; then
    echo "❌ Error: adb not found in Android SDK"
    exit 1
fi

if [ ! -f "$ANDROID_SDK_ROOT/emulator/emulator" ]; then
    echo "❌ Error: emulator not found in Android SDK"
    exit 1
fi

echo "✅ Android SDK verification passed"

# Create AVD if it doesn't exist
echo "🔧 Creating AVD if needed..."
if ! avdmanager list avd | grep -q "ci_nexus5"; then
    echo "🔧 Creating AVD ci_nexus5..."
    echo "no" | avdmanager create avd --name "ci_nexus5" --package "system-images;android-34;default;x86_64" --device "Nexus 5" --force
fi

# Start emulator
echo "🔧 Starting emulator..."
emulator -avd "ci_nexus5" -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -accel off -no-snapshot -no-metrics -verbose -memory 2048 -cores 2 -skin 1080x1920 &
EMULATOR_PID=$!
echo "🔧 Emulator PID: $EMULATOR_PID"

# Wait for emulator to start
echo "🔧 Waiting for emulator to start..."
sleep 30

# Wait for device to appear in adb
echo "🔧 Waiting for device in adb..."
for i in {1..30}; do
    if adb devices | grep -q "emulator"; then
        echo "✅ Emulator detected in adb"
        break
    fi
    sleep 2
done

# Wait for Android to boot
echo "🔧 Waiting for Android to boot..."
for i in {1..60}; do
    if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
        echo "✅ Android boot completed"
        break
    fi
    sleep 5
done

# Unlock screen
echo "🔧 Unlocking screen..."
adb shell input keyevent 82
sleep 2
adb shell input keyevent 82
sleep 2
adb shell input swipe 540 1500 540 500

# Install APK
echo "🔧 Installing APK..."
adb install "$APK_FILE"

# Verify installation
echo "🔧 Verifying APK installation..."
if adb shell pm list packages | grep -q financialsuccess; then
    echo "✅ APK installed successfully"
else
    echo "❌ APK installation failed"
    exit 1
fi

# Stop emulator
echo "🔧 Stopping emulator..."
kill $EMULATOR_PID 2>/dev/null || true
pkill -f emulator 2>/dev/null || true

echo "✅ Fallback emulator setup completed successfully"