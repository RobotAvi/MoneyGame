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

echo "🔨 Запуск только сборки (без screenshots)"
echo "📋 Этот скрипт запустит workflow только с job'ами:"
echo "   - setup-android-sdk"
echo "   - build-and-test"
echo "   - create-release"
echo ""

# Проверяем, что workflow существует
echo "🔍 Проверка доступных workflows..."
WORKFLOW_NAME="🚀 Stable Build & Release"
if ! gh workflow list | grep -q "$WORKFLOW_NAME"; then
    echo "❌ Workflow '$WORKFLOW_NAME' не найден!"
    echo "Доступные workflows:"
    gh workflow list
    exit 1
fi

echo "✅ Workflow найден: $WORKFLOW_NAME"
echo ""

# Запускаем workflow
echo "🚀 Запуск workflow..."
gh workflow run "$WORKFLOW_NAME"

# Ждем немного перед первой проверкой
echo "⏳ Ожидание 10 секунд перед первой проверкой..."
sleep 10

# Получаем ID запуска
run_info=$(gh run list --workflow="$WORKFLOW_NAME" --limit 1 --json databaseId,status)
run_id=$(echo "$run_info" | jq -r '.[0].databaseId')

echo "📋 Run ID: $run_id"
echo "🔗 URL: https://github.com/RobotAvi/MoneyGame/actions/runs/$run_id"
echo ""

# Мониторим выполнение
echo "🔍 Мониторинг выполнения..."
echo "⏱️  Максимум попыток: 60, интервал: 30с"
echo ""

for ((i=1; i<=60; i++)); do
    echo "📊 Попытка $i/60..."
    
    # Получаем статус
    run_info=$(gh run view "$run_id" --json status,conclusion,jobs)
    status=$(echo "$run_info" | jq -r '.status')
    conclusion=$(echo "$run_info" | jq -r '.conclusion // "running"')
    
    echo "   Статус: $status"
    echo "   Результат: $conclusion"
    
    # Показываем прогресс job'ов
    if [ "$status" = "in_progress" ]; then
        echo "   📋 Прогресс job'ов:"
        echo "$run_info" | jq -r '.jobs[] | "     \(.name): \(.status) \(.conclusion // "")"' | head -5
    fi
    
    if [ "$status" = "completed" ]; then
        if [ "$conclusion" = "success" ]; then
            echo ""
            echo "🎉 УСПЕХ! Сборка завершена успешно!"
            echo ""
            echo "📦 Артефакты:"
            gh run view "$run_id" --json artifacts --jq '.artifacts[] | "   - " + .name' 2>/dev/null || echo "   - Артефакты не найдены"
            echo ""
            echo "🔗 Подробности: https://github.com/RobotAvi/MoneyGame/actions/runs/$run_id"
            exit 0
        else
            echo ""
            echo "❌ Сборка завершилась с ошибкой: $conclusion"
            echo ""
            echo "🔍 Анализ ошибок..."
            
            # Получаем информацию о неудачных job'ах
            failed_jobs=$(gh run view "$run_id" --json jobs --jq '.jobs[] | select(.conclusion == "failure") | {name: .name, id: .databaseId}')
            
            if [ -n "$failed_jobs" ]; then
                echo "❌ Неудачные job'ы:"
                echo "$failed_jobs" | jq -r '.name + " (ID: " + (.id | tostring) + ")"'
                echo ""
                echo "📋 Для просмотра логов используйте:"
                echo "$failed_jobs" | jq -r '.id | tostring' | while read job_id; do
                    echo "   gh run view --log --job=$job_id"
                done
            fi
            
            exit 1
        fi
    fi
    
    if [ $i -lt 60 ]; then
        echo "   ⏳ Ожидание 30с..."
        sleep 30
    fi
done

echo "⏰ ПРЕВЫШЕНО ВРЕМЯ ОЖИДАНИЯ!"
echo "🔗 Проверьте статус вручную: https://github.com/RobotAvi/MoneyGame/actions/runs/$run_id"
exit 1