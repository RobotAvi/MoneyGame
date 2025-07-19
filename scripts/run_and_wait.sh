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
MAX_ATTEMPTS="${2:-30}"
DELAY="${3:-30}"

if [ -z "$WORKFLOW_NAME" ]; then
    echo "Использование: $0 <workflow_name> [max_attempts] [delay_seconds]" >&2
    echo "Пример: $0 stable-build.yml 30 30" >&2
    exit 1
fi

echo "🚀 Запуск workflow: $WORKFLOW_NAME"
echo "⏱️  Максимум попыток: $MAX_ATTEMPTS, интервал: ${DELAY}с"
echo ""

# Запускаем workflow
gh workflow run "$WORKFLOW_NAME"

# Ждем немного перед первой проверкой
echo "⏳ Ожидание 10 секунд перед первой проверкой..."
sleep 10

# Ожидаем завершения
for i in $(seq 1 "$MAX_ATTEMPTS"); do
    echo "📊 Попытка $i/$MAX_ATTEMPTS..."
    
    # Получаем последний запуск
    RUN_INFO=$(gh run list --workflow="$WORKFLOW_NAME" --limit 1 --json databaseId,status,conclusion,url)
    RUN_ID=$(echo "$RUN_INFO" | jq -r '.[0].databaseId')
    STATUS=$(echo "$RUN_INFO" | jq -r '.[0].status')
    CONCLUSION=$(echo "$RUN_INFO" | jq -r '.[0].conclusion')
    URL=$(echo "$RUN_INFO" | jq -r '.[0].url')
    
    echo "   Run ID: $RUN_ID"
    echo "   Статус: $STATUS"
    echo "   URL: $URL"
    
    if [ "$STATUS" = "completed" ]; then
        if [ "$CONCLUSION" = "success" ]; then
            echo ""
            echo "✅ УСПЕХ! Workflow успешно завершен!"
            echo "   Run ID: $RUN_ID"
            echo "   URL: $URL"
            exit 0
        else
            echo ""
            echo "❌ ОШИБКА! Workflow завершился с ошибкой."
            echo "   Run ID: $RUN_ID"
            echo "   Статус: $CONCLUSION"
            echo "   URL: $URL"
            echo ""
            echo "📋 Логи ошибки:"
            gh run view "$RUN_ID" --log
            exit 1
        fi
    fi
    
    if [ "$i" -lt "$MAX_ATTEMPTS" ]; then
        echo "   ⏳ Ожидание ${DELAY}с..."
        sleep "$DELAY"
    fi
done

echo ""
echo "⏰ ПРЕВЫШЕНО ВРЕМЯ ОЖИДАНИЯ!"
echo "Последний Run ID: $RUN_ID"
echo "URL: $URL"
exit 1