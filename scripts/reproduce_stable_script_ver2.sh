#!/bin/bash
set -e
set -o pipefail

# Цвета для логирования
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'; BLUE='\033[0;34m'; NC='\033[0m'

log_info()    { echo -e "${BLUE}ℹ️  $1${NC}"; }
log_success() { echo -e "${GREEN}✅ $1${NC}"; }
log_warning() { echo -e "${YELLOW}⚠️  $1${NC}"; }
log_error()   { echo -e "${RED}❌ $1${NC}"; }

# Параметры эмуляторов
PHONE_IMAGE="system-images;android-21;default;x86"
PHONE_PROFILE="pixel"
TABLET_IMAGE="system-images;android-14;default;armeabi-v7a"
TABLET_PROFILE="Nexus 10"

export ANDROID_SDK_ROOT="$HOME/android-sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_SDK_ROOT/emulator:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"

trap 'log_warning "Получен сигнал прерывания. Очистка..."; stop_emulator; exit 1' INT TERM

check_dependencies() {
    log_info "Проверка зависимостей..."
    command -v java &>/dev/null || { log_error "Java не установлен"; exit 1; }
    [ -x "./gradlew" ] || { log_error "gradlew не найден"; exit 1; }
    chmod +x ./gradlew
    
    # Проверяем и создаем local.properties если SDK уже установлен
    if [ -d "$ANDROID_SDK_ROOT" ] && [ ! -f "local.properties" ]; then
        log_info "Создаем local.properties для существующего SDK..."
        echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
        echo "android.sdk.path=$ANDROID_SDK_ROOT" >> local.properties
    fi
    
    log_success "Зависимости проверены"
}

install_android_sdk() {
    # Проверяем, установлен ли уже SDK
    if [ -d "$ANDROID_SDK_ROOT/cmdline-tools/latest" ] && [ -f "local.properties" ]; then
        log_info "Android SDK уже установлен, пропускаем установку"
        return 0
    fi
    
    log_info "Установка Android SDK..."
    mkdir -p "$ANDROID_SDK_ROOT"
    [ -f "cmdline-tools.zip" ] || wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O cmdline-tools.zip
    unzip -o cmdline-tools.zip -d "$HOME"
    mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
    rm -rf "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    mv "$HOME/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    export PATH="$PATH:$ANDROID_SDK_ROOT/emulator:$ANDROID_SDK_ROOT/platform-tools:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"
    export ANDROID_HOME="$ANDROID_SDK_ROOT"
    yes | sdkmanager --licenses
    yes | sdkmanager "platform-tools" "emulator" "$PHONE_IMAGE" "$TABLET_IMAGE"
    
    # Создаем local.properties для Gradle
    echo "sdk.dir=$ANDROID_SDK_ROOT" > local.properties
    echo "android.sdk.path=$ANDROID_SDK_ROOT" >> local.properties
    
    log_success "Android SDK установлен"
}

get_version_info() {
    VERSION=$(grep -oP 'versionName\s+"\K[^"]+' app/build.gradle)
    VERSION_CODE=$(grep -oP 'versionCode\s+\K[0-9]+' app/build.gradle)
    DATE=$(date +'%Y-%m-%d_%H-%M')
    export VERSION VERSION_CODE DATE
}

build_project() {
    log_info "Сборка проекта..."
    
    # Проверяем, что SDK установлен и local.properties создан
    if [ ! -f "local.properties" ]; then
        log_error "local.properties не найден. SDK не установлен правильно."
        exit 1
    fi
    
    ./gradlew clean --no-daemon
    ./gradlew test --no-daemon
    ./gradlew assembleDebug --no-daemon
    log_success "Сборка завершена"
}

prepare_apk() {
    mkdir -p releases/debug
    cp app/build/outputs/apk/debug/app-debug.apk releases/debug/
    cd releases/debug
    newname="FinancialSuccess-v$VERSION-$DATE-debug.apk"
    mv -f app-debug.apk "$newname"
    ln -sf "$newname" "latest-debug.apk"
    ls -tp FinancialSuccess-v*-debug.apk | grep -v '/$' | tail -n +6 | xargs -r rm -- 2>/dev/null || true
    cd ../..
    log_success "APK подготовлен: $newname"
}

install_maestro() {
    command -v maestro &>/dev/null || {
        export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED="true"
        timeout 300 bash -c 'echo "n" | curl -Ls "https://get.maestro.mobile.dev" | bash'
        export PATH="$PATH:$HOME/.maestro/bin"
    }
    # Автоматически отвечаем "n" на вопрос о сборе данных
    echo "n" | maestro --version 2>/dev/null || true
    log_success "Maestro установлен"
}

start_emulator() {
    local device_type=$1 profile=$2 image=$3
    local safe_profile=$(echo "$profile" | tr ' ' '_')
    local avd_name="test_${device_type}_${safe_profile}"

    log_info "Удаление старого AVD (если есть)..."
    avdmanager delete avd -n "$avd_name" 2>/dev/null || true
    log_info "Удаление завершено"

    log_info "Установка system-image: $image"
    yes | sdkmanager "$image" || log_warning "system-image уже установлен или возникла нефатальная ошибка"
    log_info "Установка system-image завершена"

    echo "no" | avdmanager create avd \
        --name "$avd_name" \
        --package "$image" \
        --device "$profile" || {
            log_error "Ошибка создания AVD ($profile, $image)"
            exit 1
        }
    log_info "AVD создан"

    emulator -avd "$avd_name" -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -accel off -no-snapshot -no-metrics &
    EMULATOR_PID=$!
    log_info "Эмулятор запущен, ожидание загрузки..."
    wait_for_emulator_boot
    log_info "Эмулятор готов"
}

wait_for_emulator_boot() {
    for i in {1..60}; do
        if adb devices | grep -q "emulator"; then break; fi
        sleep 2
    done
    for i in {1..90}; do
        if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then break; fi
        sleep 2
    done
    adb shell input keyevent 82
    sleep 2
}

install_apk_on_emulator() {
    log_info "Установка APK на эмулятор..."
    adb install -r releases/debug/latest-debug.apk
    sleep 3
    log_success "APK установлен"
}

generate_screenshots() {
    local device_type=$1
    mkdir -p "screenshots/$device_type"
    local config_file="maestro/screenshots-$device_type.yaml"
    [ -f "$config_file" ] || config_file="maestro/screenshots.yaml"
    if [ -f "$config_file" ]; then
        export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED="true"
        maestro test "$config_file" --format junit --output "screenshots/$device_type/"
        log_success "Скриншоты для $device_type сгенерированы"
    else
        log_warning "Файл конфигурации Maestro не найден: $config_file"
    fi
}

stop_emulator() {
    [ ! -z "$EMULATOR_PID" ] && kill $EMULATOR_PID 2>/dev/null || true
    pkill -f emulator 2>/dev/null || true
    sleep 5
}

main() {
    log_info "🚀 Начинаем воспроизведение сборки stable..."
    [ -f "app/build.gradle" ] || { log_error "Скрипт должен запускаться из корня проекта"; exit 1; }
    check_dependencies
    
    # Устанавливаем SDK только если его нет
    if [ ! -d "$ANDROID_SDK_ROOT/platform-tools" ]; then
        install_android_sdk
    else
        log_info "Android SDK уже установлен, пропускаем установку"
    fi
    
    get_version_info
    build_project
    prepare_apk
    install_maestro

    log_info "=== Генерация скриншотов для телефона ==="
    start_emulator "phone" "$PHONE_PROFILE" "$PHONE_IMAGE"
    install_apk_on_emulator
    generate_screenshots "phone"
    stop_emulator

    log_info "=== Генерация скриншотов для планшета ==="
    start_emulator "tablet" "$TABLET_PROFILE" "$TABLET_IMAGE"
    install_apk_on_emulator
    generate_screenshots "tablet"
    stop_emulator

    log_success "🎉 Воспроизведение сборки stable завершено!"
    echo "📦 APK: releases/debug/FinancialSuccess-v$VERSION-$DATE-debug.apk"
    echo "📸 Phone screenshots: screenshots/phone/"
    echo "📸 Tablet screenshots: screenshots/tablet/"
}

main "$@"
