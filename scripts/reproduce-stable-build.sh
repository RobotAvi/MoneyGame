#!/bin/bash

# 🚀 Скрипт для воспроизведения сборки stable
# Этот скрипт выполняет все шаги из .github/workflows/stable-build.yml
# для локального тестирования и отладки

set -e  # Остановка при ошибке
set -o pipefail  # Остановка при ошибке в пайпе

# Цвета для вывода
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Отключение интерактивных режимов
export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED="true"

# Функции для логирования
log_info() {
    echo -e "${BLUE}ℹ️  $1${NC}"
}

log_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Функция для выполнения команд с таймаутом
run_with_timeout() {
    local timeout_seconds=900
    local cmd="$@"
    
    log_info "Выполнение команды с таймаутом ${timeout_seconds}с: $cmd"
    
    timeout $timeout_seconds bash -c "$cmd"
    local exit_code=$?
    
    if [ $exit_code -eq 124 ]; then
        log_error "Команда превысила таймаут ${timeout_seconds} секунд"
        return 1
    elif [ $exit_code -ne 0 ]; then
        log_error "Команда завершилась с ошибкой (код: $exit_code)"
        return $exit_code
    fi
    
    log_success "Команда выполнена успешно"
    return 0
}

# Проверка зависимостей
check_dependencies() {
    log_info "Проверка зависимостей..."
    
    # Проверка Java
    if ! command -v java &> /dev/null; then
        log_error "Java не установлен. Установите JDK 21"
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$java_version" != "21" ]; then
        log_warning "Рекомендуется Java 21, найдена версия: $java_version"
    fi
    
    # Проверка Android SDK
    if [ -z "$ANDROID_SDK_ROOT" ]; then
        log_warning "ANDROID_SDK_ROOT не установлен"
        log_info "Установка Android SDK..."
        install_android_sdk
    fi
    
    # Проверка gradlew
    if [ ! -f "gradlew" ]; then
        log_error "gradlew не найден. Убедитесь, что вы в корне проекта"
        exit 1
    fi
    
    chmod +x gradlew
    log_success "Зависимости проверены"
}

# Установка Android SDK
install_android_sdk() {
    log_info "Установка Android SDK..."
    
    # Создаем директорию для SDK
    export ANDROID_SDK_ROOT="$HOME/android-sdk"
    mkdir -p "$ANDROID_SDK_ROOT"
    
    # Скачиваем command line tools
    if [ ! -f "cmdline-tools.zip" ]; then
        wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O cmdline-tools.zip
    fi
    
    # Распаковываем
    unzip -o cmdline-tools.zip -d "$HOME"
    mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools"
    mv "$HOME/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    
    # Добавляем полные пути в PATH
    export PATH="$PATH:$ANDROID_SDK_ROOT/emulator"
    export PATH="$PATH:$ANDROID_SDK_ROOT/platform-tools"
    export PATH="$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin"
    
    # Принимаем лицензии и устанавливаем компоненты
    yes | sdkmanager --licenses
    sdkmanager "platform-tools" "emulator" "system-images;android-34;default;x86_64"
    
    log_success "Android SDK установлен"
}

# Получение информации о версии
get_version_info() {
    log_info "Получение информации о версии..."
    
    VERSION=$(grep -oP 'versionName\s+"\K[^"]+' app/build.gradle)
    VERSION_CODE=$(grep -oP 'versionCode\s+\K[0-9]+' app/build.gradle)
    DATE=$(date +'%Y-%m-%d_%H-%M')
    
    echo "📱 Version: $VERSION"
    echo "🔢 Version Code: $VERSION_CODE"
    echo "📅 Date: $DATE"
    
    export VERSION VERSION_CODE DATE
}

# Основная сборка
build_project() {
    log_info "Начинаем основную сборку..."
    
    # Очистка
    log_info "Очистка проекта..."
    ./gradlew clean --no-daemon
    
    # Запуск тестов
    log_info "Запуск unit тестов..."
    ./gradlew test --no-daemon
    
    # Сборка debug APK
    log_info "Сборка debug APK..."
    ./gradlew assembleDebug --no-daemon
    
    log_success "Основная сборка завершена"
}

# Подготовка APK для распространения
prepare_apk() {
    log_info "Подготовка APK для распространения..."
    
    mkdir -p releases/debug
    cp app/build/outputs/apk/debug/app-debug.apk releases/debug/
    cd releases/debug
    
    # Переименовываем с версией и датой
    newname="FinancialSuccess-v$VERSION-$DATE-debug.apk"
    mv -f app-debug.apk "$newname"
    
    # Создаем символическую ссылку на latest
    ln -sf "$newname" "latest-debug.apk"
    
    # Очищаем старые APK (оставляем только 5 последних)
    ls -tp FinancialSuccess-v*-debug.apk | grep -v '/$' | tail -n +6 | xargs -r rm -- 2>/dev/null || true
    
    cd ../..
    
    log_success "APK подготовлен: $newname"
    ls -la releases/debug/
}

# Установка Maestro
install_maestro() {
    log_info "Установка Maestro..."
    
    if ! command -v maestro &> /dev/null; then
        timeout 300 bash -c 'echo "n" | curl -Ls "https://get.maestro.mobile.dev" | bash'
        export PATH="$PATH:$HOME/.maestro/bin"
    fi
    
    echo "n" | maestro --version
    log_success "Maestro установлен"
}

# Запуск эмулятора
start_emulator() {
    local device_type=$1
    local profile=$2
    
    log_info "Запуск эмулятора для $device_type ($profile)..."
    
    # Безопасные имена AVD (заменяем пробелы на _)
    local safe_profile=$(echo "$profile" | tr ' ' '_')
    avd_name="test_${device_type}_${safe_profile}"
    
    if ! avdmanager list avd | grep -q "$avd_name"; then
        log_info "Создание AVD: $avd_name"
        echo "no" | avdmanager create avd \
            --name "$avd_name" \
            --package "system-images;android-34;default;x86_64" \
            --device "$profile"
    fi
    
    # Запускаем эмулятор
    emulator -avd "$avd_name" \
        -no-window \
        -gpu swiftshader_indirect \
        -noaudio \
        -no-boot-anim \
        -accel off \
        -no-snapshot \
        -no-metrics &
    
    EMULATOR_PID=$!
    
    # Ждем загрузки эмулятора с таймаутом
    log_info "Ожидание загрузки эмулятора (таймаут: 120с)..."
    emulator_timeout=120
    boot_timeout=180
    
    sleep 30
    
    # Проверяем, что эмулятор запустился с таймаутом
    for i in {1..60}; do
        if adb devices | grep -q "emulator"; then
            log_success "Эмулятор $device_type запущен"
            break
        fi
        if [ $i -eq 60 ]; then
            log_error "Не удалось запустить эмулятор $device_type за $emulator_timeout секунд"
            kill $EMULATOR_PID 2>/dev/null || true
            exit 1
        fi
        sleep 2
    done
    
    # Ждем полной загрузки Android с таймаутом
    log_info "Ожидание загрузки Android (таймаут: ${boot_timeout}с)..."
    for i in {1..90}; do
        if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
            log_success "Android загружен на эмуляторе $device_type"
            break
        fi
        if [ $i -eq 90 ]; then
            log_warning "Android не загрузился за $boot_timeout секунд, продолжаем..."
        fi
        sleep 2
    done
    
    # Разблокируем экран
    adb shell input keyevent 82
    adb shell input keyevent 82
    sleep 5
}

# Установка APK на эмулятор
install_apk_on_emulator() {
    local device_type=$1
    
    log_info "Установка APK на эмулятор $device_type..."
    
    adb install releases/debug/*.apk
    adb shell pm list packages | grep financialsuccess
    sleep 3
    
    log_success "APK установлен на эмулятор $device_type"
}

# Генерация скриншотов
generate_screenshots() {
    local device_type=$1
    
    log_info "Генерация скриншотов для $device_type..."
    
    mkdir -p "screenshots/$device_type"
    
    # Пробуем специфичную конфигурацию
    local config_file="maestro/screenshots-$device_type.yaml"
    if [ ! -f "$config_file" ]; then
        config_file="maestro/screenshots.yaml"
    fi
    
    if [ -f "$config_file" ]; then
        maestro test "$config_file" --format junit --output "screenshots/$device_type/"
        log_success "Скриншоты для $device_type сгенерированы"
    else
        log_warning "Файл конфигурации Maestro не найден: $config_file"
    fi
}

# Остановка эмулятора
stop_emulator() {
    if [ ! -z "$EMULATOR_PID" ]; then
        log_info "Остановка эмулятора..."
        kill $EMULATOR_PID 2>/dev/null || true
        sleep 5
    fi
    
    # Убиваем все процессы эмулятора
    pkill -f emulator 2>/dev/null || true
}

# Основная функция
main() {
    log_info "🚀 Начинаем воспроизведение сборки stable..."
    
    # Проверяем, что мы в корне проекта
    if [ ! -f "app/build.gradle" ]; then
        log_error "Скрипт должен запускаться из корня проекта"
        exit 1
    fi
    
    # Проверяем зависимости
    check_dependencies
    
    # Получаем информацию о версии
    get_version_info
    
    # Основная сборка с таймаутом
    run_with_timeout build_project
    
    # Подготавливаем APK
    prepare_apk
    
    # Устанавливаем Maestro
    install_maestro
    
    # Генерируем скриншоты для телефона
    log_info "=== Генерация скриншотов для телефона ==="
    start_emulator "phone" "Nexus 6"
    install_apk_on_emulator "phone"
    generate_screenshots "phone"
    stop_emulator
    
    # Генерируем скриншоты для планшета
    log_info "=== Генерация скриншотов для планшета ==="
    start_emulator "tablet" "Nexus 10"
    install_apk_on_emulator "tablet"
    generate_screenshots "tablet"
    stop_emulator
    
    # Финальный отчет
    log_success "🎉 Воспроизведение сборки stable завершено!"
    echo ""
    echo "📊 Результаты:"
    echo "📱 Version: $VERSION"
    echo "🔢 Version Code: $VERSION_CODE"
    echo "📅 Date: $DATE"
    echo "📦 APK: releases/debug/FinancialSuccess-v$VERSION-$DATE-debug.apk"
    echo "📸 Phone screenshots: screenshots/phone/"
    echo "📸 Tablet screenshots: screenshots/tablet/"
    echo ""
    echo "📁 Файлы:"
    ls -la releases/debug/
    echo ""
    echo "📸 Скриншоты телефона:"
    ls -la screenshots/phone/ 2>/dev/null || echo "Нет скриншотов телефона"
    echo ""
    echo "📸 Скриншоты планшета:"
    ls -la screenshots/tablet/ 2>/dev/null || echo "Нет скриншотов планшета"
}

# Обработка сигналов для корректного завершения
trap 'log_warning "Получен сигнал прерывания. Очистка..."; stop_emulator; exit 1' INT TERM

# Запуск основной функции
main "$@"