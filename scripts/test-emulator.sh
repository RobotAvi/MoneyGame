#!/bin/bash

# Скрипт для тестирования стабильности Android эмулятора
# Используется для отладки проблем с эмулятором в CI/CD

set -e

echo "🔧 Тестирование стабильности Android эмулятора..."

# Функция для проверки статуса эмулятора
check_emulator_status() {
    echo "📱 Проверка статуса эмулятора..."
    
    if adb devices | grep -q "emulator-5554.*device"; then
        echo "✅ Эмулятор подключен и готов"
        return 0
    elif adb devices | grep -q "emulator-5554.*offline"; then
        echo "⚠️  Эмулятор офлайн"
        return 1
    else
        echo "❌ Эмулятор не найден"
        return 1
    fi
}

# Функция для проверки загрузки системы
check_system_boot() {
    echo "🔄 Проверка загрузки системы..."
    
    local max_attempts=30
    local attempt=1
    
    while [ $attempt -le $max_attempts ]; do
        if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
            echo "✅ Система загружена"
            return 0
        else
            echo "⏳ Ожидание загрузки системы... ($attempt/$max_attempts)"
            sleep 5
            attempt=$((attempt + 1))
        fi
    done
    
    echo "❌ Система не загрузилась за отведенное время"
    return 1
}

# Функция для тестирования установки APK
test_apk_installation() {
    local apk_path="$1"
    
    if [ -z "$apk_path" ]; then
        echo "❌ Путь к APK не указан"
        return 1
    fi
    
    echo "📦 Тестирование установки APK: $apk_path"
    
    # Проверяем что файл существует
    if [ ! -f "$apk_path" ]; then
        echo "❌ APK файл не найден: $apk_path"
        return 1
    fi
    
    # Пытаемся установить APK
    if adb install -r "$apk_path"; then
        echo "✅ APK установлен успешно"
        
        # Проверяем что приложение установлено
        if adb shell pm list packages | grep -q "com.financialsuccess.game"; then
            echo "✅ Приложение найдено в системе"
            return 0
        else
            echo "⚠️  Приложение не найдено в списке пакетов"
            return 1
        fi
    else
        echo "❌ Ошибка установки APK"
        return 1
    fi
}

# Функция для тестирования Maestro
test_maestro() {
    echo "🎭 Тестирование Maestro..."
    
    # Проверяем что Maestro установлен
    if ! command -v maestro &> /dev/null; then
        echo "❌ Maestro не установлен"
        return 1
    fi
    
    echo "✅ Maestro найден: $(maestro --version)"
    
    # Создаем временный тест
    cat > /tmp/test-maestro.yaml << 'EOF'
appId: com.financialsuccess.game
name: Test Maestro
outputDir: /tmp/maestro-test

- launchApp
- takeScreenshot: "test_screenshot"
EOF
    
    # Запускаем тест
    if maestro test /tmp/test-maestro.yaml; then
        echo "✅ Maestro тест выполнен успешно"
        
        # Проверяем что скриншот создан
        if [ -f "/tmp/maestro-test/test_screenshot.png" ]; then
            echo "✅ Скриншот создан"
            return 0
        else
            echo "⚠️  Скриншот не создан"
            return 1
        fi
    else
        echo "❌ Maestro тест не выполнен"
        return 1
    fi
}

# Основная функция тестирования
main() {
    echo "🚀 Начало тестирования эмулятора..."
    
    # Шаг 1: Проверка статуса эмулятора
    if ! check_emulator_status; then
        echo "❌ Эмулятор не готов"
        exit 1
    fi
    
    # Шаг 2: Проверка загрузки системы
    if ! check_system_boot; then
        echo "❌ Система не загрузилась"
        exit 1
    fi
    
    # Шаг 3: Тестирование установки APK (если указан путь)
    if [ -n "$1" ]; then
        if ! test_apk_installation "$1"; then
            echo "❌ Ошибка установки APK"
            exit 1
        fi
    else
        echo "ℹ️  Пропуск тестирования APK (путь не указан)"
    fi
    
    # Шаг 4: Тестирование Maestro
    if ! test_maestro; then
        echo "❌ Ошибка тестирования Maestro"
        exit 1
    fi
    
    echo "🎉 Все тесты пройдены успешно!"
    echo "✅ Эмулятор стабилен и готов к работе"
}

# Обработка аргументов командной строки
case "${1:-}" in
    --help|-h)
        echo "Использование: $0 [путь_к_apk]"
        echo ""
        echo "Тестирует стабильность Android эмулятора:"
        echo "  - Проверяет подключение эмулятора"
        echo "  - Проверяет загрузку системы"
        echo "  - Тестирует установку APK (если указан путь)"
        echo "  - Тестирует работу Maestro"
        echo ""
        echo "Примеры:"
        echo "  $0                                    # Базовое тестирование"
        echo "  $0 app/build/outputs/apk/debug/app-debug.apk  # С тестированием APK"
        exit 0
        ;;
    *)
        main "$@"
        ;;
esac