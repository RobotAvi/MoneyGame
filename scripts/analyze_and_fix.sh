#!/usr/bin/env bash
set -e

# Загружаем конфигурацию
if [ -f "../config.env" ]; then
    source ../config.env
elif [ -f "config.env" ]; then
    source config.env
else
    echo "Ошибка: Файл config.env не найден!" >&2
    exit 1
fi

# Устанавливаем токен для gh CLI
export GH_TOKEN="$GITHUB_TOKEN"
# Отключаем pager для корректной работы
unset PAGER

echo "🔍 Анализ проблем GitHub Actions"
echo "=================================="
echo ""

# Получаем последние запуски
echo "📋 Последние запуски workflow '🚀 Stable Build & Release':"
echo ""

runs=$(gh run list --workflow="🚀 Stable Build & Release" --limit 5 --json databaseId,status,conclusion,createdAt,url)

echo "$runs" | jq -r '.[] | "ID: \(.databaseId) | Статус: \(.status) | Результат: \(.conclusion // "running") | Время: \(.createdAt)"'
echo ""

# Анализируем последний неудачный запуск
latest_failed=$(echo "$runs" | jq -r '.[] | select(.conclusion == "failure") | .databaseId' | head -1)

if [ -n "$latest_failed" ]; then
    echo "🔍 Анализ последнего неудачного запуска: $latest_failed"
    echo "=================================================="
    
    # Получаем детальную информацию о job'ах
    run_info=$(gh run view "$latest_failed" --json jobs,status,conclusion)
    
    echo ""
    echo "📊 Статус job'ов:"
    echo "$run_info" | jq -r '.jobs[] | "   \(.name): \(.status) \(.conclusion // "")"'
    echo ""
    
    # Анализируем неудачные job'ы
    failed_jobs=$(echo "$run_info" | jq -r '.jobs[] | select(.conclusion == "failure") | .name')
    
    if [ -n "$failed_jobs" ]; then
        echo "❌ Неудачные job'ы:"
        echo "$failed_jobs" | while read job_name; do
            echo "   - $job_name"
        done
        echo ""
        
        # Проверяем типы ошибок
        if echo "$failed_jobs" | grep -q "screenshots"; then
            echo "⚠️  ПРОБЛЕМА: Ошибки в job'ах screenshots"
            echo "=========================================="
            echo ""
            echo "🔍 Причина: Android эмулятор не успевает загрузиться"
            echo ""
            echo "📋 Симптомы:"
            echo "   - Эмулятор запускается, но Android не загружается за 180 секунд"
            echo "   - Ошибки 'adb shell getprop failed'"
            echo "   - Процесс эмулятора работает, но система не отвечает"
            echo ""
            echo "💡 РЕШЕНИЯ:"
            echo ""
            echo "1. 🔧 Увеличить время ожидания в workflow:"
            echo "   - Найдите в .github/workflows/stable-build.yml"
            echo "   - Увеличьте timeout для job'ов screenshots"
            echo "   - Или добавьте условие для пропуска screenshots"
            echo ""
            echo "2. 🚀 Запустить только build без screenshots:"
            echo "   - Создайте отдельный workflow только для сборки"
            echo "   - Или добавьте параметр для пропуска screenshots"
            echo ""
            echo "3. ⚡ Оптимизировать эмулятор:"
            echo "   - Использовать более легкий эмулятор"
            echo "   - Добавить аппаратное ускорение"
            echo "   - Уменьшить разрешение экрана"
            echo ""
            echo "4. 🔄 Использовать альтернативные решения:"
            echo "   - Firebase Test Lab"
            echo "   - BrowserStack"
            echo "   - Локальные скриншоты без эмулятора"
            echo ""
        fi
        
        if echo "$failed_jobs" | grep -q "build"; then
            echo "⚠️  ПРОБЛЕМА: Ошибки в job'ах сборки"
            echo "====================================="
            echo ""
            echo "🔍 Причина: Проблемы с компиляцией или тестами"
            echo ""
            echo "💡 РЕШЕНИЯ:"
            echo "1. Проверьте логи сборки: ./github_actions.sh logs $latest_failed"
            echo "2. Обновите зависимости"
            echo "3. Исправьте ошибки в коде"
            echo ""
        fi
    fi
    
    # Проверяем успешные job'ы
    successful_jobs=$(echo "$run_info" | jq -r '.jobs[] | select(.conclusion == "success") | .name')
    
    if [ -n "$successful_jobs" ]; then
        echo "✅ Успешные job'ы:"
        echo "$successful_jobs" | while read job_name; do
            echo "   - $job_name"
        done
        echo ""
        
        # Если build прошел успешно, но screenshots упали
        if echo "$successful_jobs" | grep -q "build-and-test" && echo "$failed_jobs" | grep -q "screenshots"; then
            echo "🎉 ХОРОШИЕ НОВОСТИ!"
            echo "=================="
            echo "✅ Сборка и тесты прошли успешно!"
            echo "📦 APK файл создан и готов к использованию"
            echo ""
            echo "🔗 Скачать артефакты:"
            echo "   https://github.com/RobotAvi/MoneyGame/actions/runs/$latest_failed"
            echo ""
            echo "💡 Рекомендация:"
            echo "   Можно использовать эту сборку, игнорируя проблемы с screenshots"
            echo ""
        fi
    fi
else
    echo "✅ Не найдено неудачных запусков в последних 5 попытках"
fi

echo "🔧 ДОПОЛНИТЕЛЬНЫЕ РЕКОМЕНДАЦИИ"
echo "==============================="
echo ""
echo "1. 📊 Мониторинг:"
echo "   ./monitor_workflows.sh"
echo ""
echo "2. 🔍 Детальный анализ:"
echo "   ./github_actions.sh failed-logs <run_id>"
echo ""
echo "3. 🚀 Умный запуск:"
echo "   ./smart_run.sh '🚀 Stable Build & Release'"
echo ""
echo "4. 🔧 Создание отдельного workflow:"
echo "   - Создайте workflow только для сборки"
echo "   - Или добавьте параметры для условного запуска job'ов"
echo ""
echo "5. 📝 Документация:"
echo "   - Обновите README с инструкциями по устранению проблем"
echo "   - Добавьте troubleshooting guide"
echo ""

echo "🎯 СЛЕДУЮЩИЕ ШАГИ"
echo "=================="
echo ""
echo "1. 🔧 Исправьте workflow для пропуска проблемных job'ов"
echo "2. 🚀 Запустите исправленный workflow"
echo "3. ✅ Добейтесь зеленой сборки"
echo "4. 📝 Документируйте решения"
echo ""