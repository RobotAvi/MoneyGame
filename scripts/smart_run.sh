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

WORKFLOW_NAME="$1"
MAX_RETRIES="${2:-3}"
MONITOR_DELAY="${3:-60}"

if [ -z "$WORKFLOW_NAME" ]; then
    echo "Использование: $0 <workflow_name> [max_retries] [monitor_delay_seconds]" >&2
    echo "Пример: $0 '🚀 Stable Build & Release' 3 60" >&2
    exit 1
fi

echo "🚀 Умный запуск workflow: $WORKFLOW_NAME"
echo "🔄 Максимум попыток: $MAX_RETRIES"
echo "⏱️  Интервал мониторинга: ${MONITOR_DELAY}с"
echo ""

# Функция для анализа ошибок
analyze_failure() {
    local run_id="$1"
    echo "🔍 Анализ ошибок в run $run_id..."
    
    # Получаем информацию о неудачных job'ах
    local failed_jobs=$(gh run view "$run_id" --json jobs --jq '.jobs[] | select(.conclusion == "failure") | {name: .name, id: .databaseId}')
    
    if [ -n "$failed_jobs" ]; then
        echo "❌ Неудачные job'ы:"
        echo "$failed_jobs" | jq -r '.name + " (ID: " + (.id | tostring) + ")"'
        echo ""
        
        # Проверяем, связаны ли ошибки с эмулятором
        if echo "$failed_jobs" | jq -r '.name' | grep -q "screenshots"; then
            echo "⚠️  Обнаружены проблемы с Android эмулятором"
            echo "💡 Рекомендации:"
            echo "   - Эмулятор может не успевать загрузиться"
            echo "   - Попробуйте увеличить время ожидания"
            echo "   - Или запустите только build без screenshots"
            return 1
        fi
        
        # Проверяем другие типы ошибок
        if echo "$failed_jobs" | jq -r '.name' | grep -q "build"; then
            echo "⚠️  Обнаружены проблемы со сборкой"
            echo "💡 Рекомендации:"
            echo "   - Проверьте логи сборки"
            echo "   - Возможно, есть проблемы с зависимостями"
            return 2
        fi
    fi
    
    return 0
}

# Функция для мониторинга с детальным анализом
monitor_with_analysis() {
    local run_id="$1"
    local max_attempts="${2:-60}"
    local delay="${3:-30}"
    
    echo "🔍 Детальный мониторинг run: $run_id"
    echo "⏱️  Максимум попыток: $max_attempts, интервал: ${delay}с"
    echo ""
    
    for ((i=1; i<=max_attempts; i++)); do
        echo "📊 Попытка $i/$max_attempts..."
        
        # Получаем детальную информацию о статусе
        local run_info=$(gh run view "$run_id" --json status,conclusion,jobs)
        local status=$(echo "$run_info" | jq -r '.status')
        local conclusion=$(echo "$run_info" | jq -r '.conclusion // "running"')
        
        echo "   Статус: $status"
        echo "   Результат: $conclusion"
        
        # Показываем прогресс job'ов
        if [ "$status" = "in_progress" ]; then
            echo "   📋 Прогресс job'ов:"
            echo "$run_info" | jq -r '.jobs[] | "     \(.name): \(.status) \(.conclusion // "")"' | head -5
        fi
        
        if [ "$status" = "completed" ]; then
            if [ "$conclusion" = "success" ]; then
                echo "✅ Workflow успешно завершен!"
                return 0
            else
                echo "❌ Workflow завершился с ошибкой: $conclusion"
                analyze_failure "$run_id"
                return 1
            fi
        fi
        
        if [ $i -lt $max_attempts ]; then
            echo "   ⏳ Ожидание ${delay}с..."
            sleep "$delay"
        fi
    done
    
    echo "⏰ ПРЕВЫШЕНО ВРЕМЯ ОЖИДАНИЯ!"
    echo "🔍 Анализ последнего состояния..."
    analyze_failure "$run_id"
    return 1
}

# Основной цикл запуска
for ((retry=1; retry<=MAX_RETRIES; retry++)); do
    echo "🔄 Попытка $retry/$MAX_RETRIES"
    echo "🚀 Запуск workflow..."
    
    # Запускаем workflow
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
    
    # Мониторим с анализом
    if monitor_with_analysis "$run_id" 60 "$MONITOR_DELAY"; then
        echo "🎉 УСПЕХ! Workflow завершился успешно!"
        exit 0
    else
        echo "❌ Попытка $retry завершилась неудачно"
        
        if [ $retry -lt $MAX_RETRIES ]; then
            echo "🔄 Подготовка к следующей попытке..."
            echo "⏳ Ожидание 30 секунд перед повторной попыткой..."
            sleep 30
            echo ""
        fi
    fi
done

echo "💥 Все попытки исчерпаны. Workflow не удалось выполнить успешно."
echo "🔍 Рекомендации:"
echo "   - Проверьте логи последнего запуска"
echo "   - Убедитесь, что все зависимости актуальны"
echo "   - Рассмотрите возможность запуска без проблемных job'ов"
exit 1