# Android Emulator Troubleshooting Guide

This guide addresses common issues with Android emulators in CI/CD environments, particularly focusing on the problems encountered in the MoneyGame project.

## Common Issues and Solutions

### 1. ADB Connection Issues

**Problem**: `adb shell getprop failed` or `adb devices` shows no devices

**Symptoms**:
- Emulator process is running but not visible to ADB
- `adb devices` returns empty list
- `adb shell` commands fail

**Solutions**:

#### A. Check Emulator Process
```bash
# Check if emulator is running
ps aux | grep emulator | grep -v grep

# Check emulator logs
adb -s emulator-5554 emu avd status
```

#### B. Restart ADB Server
```bash
# Kill ADB server
adb kill-server

# Start ADB server
adb start-server

# Check devices
adb devices
```

#### C. Alternative Emulator Parameters
```bash
# Use different GPU acceleration
emulator -avd ci_nexus5 -gpu swiftshader_indirect -no-window

# Or try software rendering
emulator -avd ci_nexus5 -gpu swiftshader -no-window

# Or disable GPU acceleration
emulator -avd ci_nexus5 -gpu off -no-window
```

### 2. System Boot Issues

**Problem**: Android system doesn't fully boot or services are not available

**Symptoms**:
- `sys.boot_completed` never becomes "1"
- `init.svc.bootanim` stays "running"
- Services like `input` and `package` are not found

**Solutions**:

#### A. Enhanced Boot Detection
```bash
# Function to check multiple boot indicators
check_system_ready() {
    local boot_completed=$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
    local bootanim_status=$(adb shell getprop init.svc.bootanim 2>/dev/null | tr -d '\r')
    local zygote_status=$(adb shell getprop init.svc.zygote 2>/dev/null | tr -d '\r')
    
    # System is ready if boot_completed = 1 or bootanim = stopped
    if [ "$boot_completed" = "1" ] || [ "$bootanim_status" = "stopped" ]; then
        return 0
    fi
    
    # Alternative check - if system responds to commands
    if adb shell "echo 'test'" 2>/dev/null | grep -q "test"; then
        return 0
    fi
    
    return 1
}
```

#### B. Increase Boot Timeout
```bash
# Increase timeout to 5 minutes
boot_timeout=300
for i in {1..60}; do
    if check_system_ready; then
        echo "✅ System is ready"
        break
    fi
    sleep 5
done
```

#### C. Alternative System Images
```bash
# Try different API levels
sdkmanager "system-images;android-33;default;x86_64"
sdkmanager "system-images;android-32;default;x86_64"

# Create AVD with different API level
avdmanager create avd --name "ci_nexus5_api33" --package "system-images;android-33;default;x86_64"
```

### 3. Input Service Issues

**Problem**: `cmd: Can't find service: input`

**Symptoms**:
- Screen unlock commands fail
- Input events don't work
- `adb shell input` commands return errors

**Solutions**:

#### A. Wait for Input Service
```bash
# Wait for input service to be available
for i in {1..30}; do
    if adb shell "service list | grep input" 2>/dev/null; then
        echo "✅ Input service is available"
        break
    fi
    sleep 2
done
```

#### B. Multiple Unlock Methods
```bash
# Method 1: Key events
adb shell input keyevent 82

# Method 2: Swipe gesture
adb shell input swipe 540 1500 540 500

# Method 3: Power + Menu
adb shell input keyevent 26  # Power
adb shell input keyevent 82  # Menu

# Method 4: Alternative swipe
adb shell input swipe 540 1600 540 400
```

#### C. Check Input Permissions
```bash
# Check if input service is running
adb shell "ps | grep input"

# Check input device permissions
adb shell "ls -la /dev/input/"
```

### 4. Package Service Issues

**Problem**: `cmd: Can't find service: package`

**Symptoms**:
- APK installation fails
- `adb install` commands return errors
- Package manager is not available

**Solutions**:

#### A. Wait for Package Service
```bash
# Wait for package service to be available
for i in {1..30}; do
    if adb shell "service list | grep package" 2>/dev/null; then
        echo "✅ Package service is available"
        break
    fi
    sleep 2
done
```

#### B. Retry Installation Logic
```bash
# Function with retry logic
install_apk_robust() {
    local apk_path="$1"
    local max_attempts=3
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        echo "📱 Installation attempt $attempt/$max_attempts..."
        
        # Wait for system stabilization
        sleep 10
        
        if adb install -r "$apk_path"; then
            echo "✅ APK installed successfully"
            return 0
        fi
        
        attempt=$((attempt + 1))
        sleep 5
    done
    
    return 1
}
```

#### C. Alternative Installation Methods
```bash
# Method 1: Standard install
adb install app-debug.apk

# Method 2: Install with replace flag
adb install -r app-debug.apk

# Method 3: Install with grant permissions
adb install -g app-debug.apk

# Method 4: Install to specific location
adb push app-debug.apk /data/local/tmp/
adb shell pm install /data/local/tmp/app-debug.apk
```

### 5. Memory and Performance Issues

**Problem**: Emulator runs slowly or crashes

**Symptoms**:
- High CPU usage
- Out of memory errors
- Emulator becomes unresponsive

**Solutions**:

#### A. Optimize Emulator Parameters
```bash
# Reduce memory usage
emulator -avd ci_nexus5 -memory 1024 -cores 1

# Use software rendering
emulator -avd ci_nexus5 -gpu swiftshader

# Disable unnecessary features
emulator -avd ci_nexus5 -no-audio -no-boot-anim -no-snapshot
```

#### B. System Resource Management
```bash
# Check available memory
free -h

# Check CPU usage
top -n 1

# Kill unnecessary processes
pkill -f chrome || true
pkill -f firefox || true
```

#### C. Alternative Hardware Configuration
```bash
# Use different device configuration
avdmanager create avd --name "ci_pixel2" --package "system-images;android-34;default;x86_64" --device "pixel_2"

# Or use smaller screen
emulator -avd ci_nexus5 -skin 720x1280
```

## Debugging Commands

### 1. Check Emulator Status
```bash
# List running emulators
adb devices

# Check emulator process
ps aux | grep emulator

# Check emulator logs
adb logcat -d | tail -100
```

### 2. Check System Properties
```bash
# Check boot status
adb shell getprop sys.boot_completed

# Check running services
adb shell getprop | grep init.svc

# Check system version
adb shell getprop ro.build.version.release
```

### 3. Check Available Services
```bash
# List all services
adb shell service list

# Check specific services
adb shell "service list | grep input"
adb shell "service list | grep package"
```

### 4. Monitor System Resources
```bash
# Check memory usage
adb shell dumpsys meminfo

# Check CPU usage
adb shell top -n 1

# Check disk space
adb shell df
```

## Fallback Strategies

### 1. Use Fallback Script
```bash
# Run the fallback emulator setup
./scripts/emulator-fallback.sh app-debug.apk
```

### 2. Alternative AVD Configuration
```bash
# Create AVD with different settings
avdmanager create avd --name "ci_fallback" \
    --package "system-images;android-33;default;x86_64" \
    --device "Nexus 5" \
    --force

# Start with minimal configuration
emulator -avd ci_fallback \
    -no-window \
    -gpu swiftshader \
    -no-audio \
    -memory 1024 \
    -cores 1
```

### 3. Use Different API Level
```bash
# Install older API level
sdkmanager "system-images;android-32;default;x86_64"

# Create AVD with older API
avdmanager create avd --name "ci_api32" \
    --package "system-images;android-32;default;x86_64" \
    --device "Nexus 5"
```

## Prevention Strategies

### 1. Pre-warm Emulator
```bash
# Start emulator before tests
emulator -avd ci_nexus5 -no-window &
sleep 60  # Wait for boot
```

### 2. Use Cached AVD
```bash
# Cache AVD configuration
mkdir -p ~/.android/avd/
cp -r cached_avd/* ~/.android/avd/
```

### 3. Monitor Resource Usage
```bash
# Check system resources before starting
free -h
df -h
nproc
```

## Emergency Recovery

### 1. Kill All Emulator Processes
```bash
# Kill all emulator processes
pkill -f emulator
pkill -f qemu

# Kill ADB server
adb kill-server
```

### 2. Clean AVD Directory
```bash
# Remove AVD files
rm -rf ~/.android/avd/*

# Recreate AVD
avdmanager create avd --name "ci_nexus5" --package "system-images;android-34;default;x86_64"
```

### 3. Restart ADB
```bash
# Restart ADB server
adb kill-server
adb start-server
adb devices
```

## Monitoring and Logging

### 1. Enable Verbose Logging
```bash
# Start emulator with verbose logging
emulator -avd ci_nexus5 -verbose -show-kernel

# Capture logs
adb logcat > emulator.log 2>&1 &
```

### 2. Monitor System Events
```bash
# Monitor boot events
adb shell getevent -l

# Monitor system properties
adb shell watchprops
```

### 3. Check Error Logs
```bash
# Check Android logs
adb logcat -d | grep -i error

# Check system logs
adb shell dmesg | tail -50
```

This troubleshooting guide should help resolve most Android emulator issues in CI/CD environments. If problems persist, consider using the fallback script or alternative emulator configurations.