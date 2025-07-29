#!/bin/bash

# Fallback script for Android emulator setup
# Used when the main workflow fails to start the emulator properly

set -e

echo "🔄 ==== FALLBACK EMULATOR SETUP ===="

# Configuration
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-/home/runner/android-sdk}"
ANDROID_HOME="${ANDROID_HOME:-/home/runner/android-sdk}"
AVD_NAME="ci_nexus5_fallback"
EMULATOR_TIMEOUT=180
BOOT_TIMEOUT=300

# Setup environment
export PATH="$PATH:$ANDROID_SDK_ROOT/emulator:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"

echo "🔧 Environment setup:"
echo "  - ANDROID_SDK_ROOT: $ANDROID_SDK_ROOT"
echo "  - ANDROID_HOME: $ANDROID_HOME"
echo "  - AVD_NAME: $AVD_NAME"

# Function to check if emulator is responding
check_emulator_ready() {
    local max_attempts=30
    local attempt=1
    
    echo "📱 Checking emulator availability..."
    
    while [ $attempt -le $max_attempts ]; do
        if adb devices | grep -q "emulator.*device"; then
            echo "✅ Emulator is connected and ready"
            return 0
        fi
        
        echo "⏳ Waiting for emulator... ($attempt/$max_attempts)"
        sleep 2
        attempt=$((attempt + 1))
    done
    
    echo "❌ Emulator not ready after $max_attempts attempts"
    return 1
}

# Function to check system boot with multiple indicators
check_system_boot_robust() {
    local max_attempts=60
    local attempt=1
    
    echo "🔄 Checking system boot status..."
    
    while [ $attempt -le $max_attempts ]; do
        echo "📱 Attempt $attempt/$max_attempts: checking system status..."
        
        # Check multiple boot indicators
        local boot_completed=$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
        local bootanim_status=$(adb shell getprop init.svc.bootanim 2>/dev/null | tr -d '\r')
        local zygote_status=$(adb shell getprop init.svc.zygote 2>/dev/null | tr -d '\r')
        
        echo "  - sys.boot_completed: $boot_completed"
        echo "  - init.svc.bootanim: $bootanim_status"
        echo "  - init.svc.zygote: $zygote_status"
        
        # System is ready if boot_completed = 1 or bootanim = stopped
        if [ "$boot_completed" = "1" ] || [ "$bootanim_status" = "stopped" ]; then
            echo "✅ System is fully booted"
            return 0
        fi
        
        # Alternative check - if system responds to commands
        if adb shell "echo 'test'" 2>/dev/null | grep -q "test"; then
            echo "✅ System is responding to commands"
            return 0
        fi
        
        sleep 5
        attempt=$((attempt + 1))
    done
    
    echo "❌ System failed to boot within timeout"
    return 1
}

# Function to unlock screen with multiple methods
unlock_screen_robust() {
    echo "🔓 Unlocking screen..."
    
    # Wait a bit after system boot
    sleep 5
    
    # Method 1: Key events
    echo "📱 Method 1: Key events"
    adb shell input keyevent 82 || echo "⚠️ Key event 82 failed"
    sleep 2
    adb shell input keyevent 82 || echo "⚠️ Key event 82 failed"
    sleep 2
    
    # Method 2: Swipe gesture
    echo "📱 Method 2: Swipe gesture"
    adb shell input swipe 540 1500 540 500 || echo "⚠️ Swipe failed"
    sleep 1
    
    # Method 3: Multiple key events
    echo "📱 Method 3: Multiple key events"
    adb shell input keyevent 26 || echo "⚠️ Power key failed"  # Power
    sleep 1
    adb shell input keyevent 82 || echo "⚠️ Menu key failed"   # Menu
    sleep 1
    
    # Method 4: Alternative swipe
    echo "📱 Method 4: Alternative swipe"
    adb shell input swipe 540 1600 540 400 || echo "⚠️ Alternative swipe failed"
    sleep 1
    
    echo "✅ Screen unlock attempts completed"
}

# Function to install APK with retry logic
install_apk_robust() {
    local apk_path="$1"
    local max_attempts=3
    local attempt=1
    
    if [ -z "$apk_path" ]; then
        echo "❌ APK path not provided"
        return 1
    fi
    
    if [ ! -f "$apk_path" ]; then
        echo "❌ APK file not found: $apk_path"
        return 1
    fi
    
    echo "📦 Installing APK: $apk_path"
    echo "📦 APK size: $(du -h "$apk_path" | cut -f1)"
    
    while [ $attempt -le $max_attempts ]; do
        echo "📱 Installation attempt $attempt/$max_attempts..."
        
        # Check package service availability
        echo "📱 Checking package service..."
        adb shell "service list | grep package" || echo "⚠️ Package service not found"
        
        # Wait for system stabilization
        echo "📱 Waiting for system stabilization..."
        sleep 10
        
        # Try to install APK
        if adb install -r "$apk_path"; then
            echo "✅ APK installed successfully"
            
            # Verify installation
            if adb shell pm list packages | grep -q "financialsuccess"; then
                echo "✅ Package verified in system"
                return 0
            else
                echo "⚠️ Package not found in system list"
                if [ $attempt -eq $max_attempts ]; then
                    return 1
                fi
            fi
        else
            echo "❌ Installation attempt $attempt failed"
            if [ $attempt -eq $max_attempts ]; then
                echo "📱 Getting error details..."
                adb logcat -d | tail -50 || echo "logcat failed"
                return 1
            fi
        fi
        
        attempt=$((attempt + 1))
        sleep 5
    done
    
    return 1
}

# Main function
main() {
    echo "🚀 Starting fallback emulator setup..."
    
    # Step 1: Create AVD if it doesn't exist
    echo "📱 Creating AVD: $AVD_NAME"
    if ! avdmanager list avd | grep -q "$AVD_NAME"; then
        echo "no" | avdmanager create avd --name "$AVD_NAME" --package "system-images;android-34;default;x86_64" --device "Nexus 5" --force
        echo "✅ AVD created"
    else
        echo "✅ AVD already exists"
    fi
    
    # Step 2: Start emulator with alternative parameters
    echo "📱 Starting emulator with fallback configuration..."
    emulator -avd "$AVD_NAME" \
        -no-window \
        -gpu swiftshader_indirect \
        -noaudio \
        -no-boot-anim \
        -accel off \
        -no-snapshot \
        -no-metrics \
        -verbose \
        -memory 2048 \
        -cores 2 \
        -skin 1080x1920 \
        -qemu -enable-kvm &
    
    EMULATOR_PID=$!
    echo "📱 Emulator PID: $EMULATOR_PID"
    
    # Step 3: Wait for emulator to be ready
    if ! check_emulator_ready; then
        echo "❌ Emulator failed to start"
        kill $EMULATOR_PID 2>/dev/null || true
        exit 1
    fi
    
    # Step 4: Wait for system to boot
    if ! check_system_boot_robust; then
        echo "❌ System failed to boot"
        kill $EMULATOR_PID 2>/dev/null || true
        exit 1
    fi
    
    # Step 5: Unlock screen
    unlock_screen_robust
    
    # Step 6: Install APK if provided
    if [ -n "$1" ]; then
        if ! install_apk_robust "$1"; then
            echo "❌ APK installation failed"
            kill $EMULATOR_PID 2>/dev/null || true
            exit 1
        fi
    else
        echo "ℹ️ No APK provided for installation"
    fi
    
    echo "🎉 Fallback emulator setup completed successfully!"
    echo "📱 Emulator is ready for use"
    
    # Keep emulator running for further use
    echo "📱 Keeping emulator running..."
    wait $EMULATOR_PID
}

# Handle command line arguments
case "${1:-}" in
    --help|-h)
        echo "Usage: $0 [apk_path]"
        echo ""
        echo "Fallback Android emulator setup script"
        echo "  - Creates AVD if needed"
        echo "  - Starts emulator with robust configuration"
        echo "  - Waits for system boot with multiple checks"
        echo "  - Unlocks screen with multiple methods"
        echo "  - Installs APK with retry logic (if provided)"
        echo ""
        echo "Examples:"
        echo "  $0                                    # Basic setup"
        echo "  $0 app-debug.apk                      # Setup with APK installation"
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac