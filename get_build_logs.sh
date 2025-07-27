#!/bin/bash

# Скрипт для получения логов сборки
# Использование: ./get_build_logs.sh <run_number>

RUN_NUMBER="${1:-248}"
REPO="RobotAvi/MoneyGame"

echo "🔍 Получение логов сборки #$RUN_NUMBER"
echo "=================================="

# Получаем ID сборки по номеру
RUNS_JSON=$(curl -s "https://api.github.com/repos/$REPO/actions/runs?per_page=20")
RUN_ID=$(echo "$RUNS_JSON" | grep -A5 -B5 "\"run_number\": $RUN_NUMBER" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)

if [ -z "$RUN_ID" ]; then
    echo "❌ Не удалось найти сборку #$RUN_NUMBER"
    exit 1
fi

echo "ID сборки: $RUN_ID"

# Получаем информацию о сборке
RUN_INFO=$(curl -s "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID")
STATUS=$(echo "$RUN_INFO" | grep -o '"status":"[^"]*"' | cut -d: -f2 | tr -d '"')
CONCLUSION=$(echo "$RUN_INFO" | grep -o '"conclusion":"[^"]*"' | cut -d: -f2 | tr -d '"')
HEAD_SHA=$(echo "$RUN_INFO" | grep -o '"head_sha":"[^"]*"' | cut -d: -f2 | tr -d '"' | cut -c1-8)

echo "Статус: $STATUS"
echo "Результат: ${CONCLUSION:-running}"
echo "Коммит: $HEAD_SHA"

# Получаем job'ы
JOBS_JSON=$(curl -s "https://api.github.com/repos/$REPO/actions/runs/$RUN_ID/jobs")
JOB_ID=$(echo "$JOBS_JSON" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)

if [ -n "$JOB_ID" ]; then
    echo ""
    echo "🔍 Детали job'а $JOB_ID:"
    echo "=================================="
    
    # Получаем информацию о job'е
    JOB_INFO=$(curl -s "https://api.github.com/repos/$REPO/actions/jobs/$JOB_ID")
    
    # Извлекаем статус и результат
    JOB_STATUS=$(echo "$JOB_INFO" | grep -o '"status":"[^"]*"' | cut -d: -f2 | tr -d '"')
    JOB_CONCLUSION=$(echo "$JOB_INFO" | grep -o '"conclusion":"[^"]*"' | cut -d: -f2 | tr -d '"')
    
    echo "Статус job'а: $JOB_STATUS"
    echo "Результат job'а: ${JOB_CONCLUSION:-running}"
    
    # Если job завершился с ошибкой, показываем ссылку на логи
    if [ "$JOB_STATUS" = "completed" ] && [ "$JOB_CONCLUSION" != "success" ]; then
        echo ""
        echo "📋 Ссылки на логи:"
        echo "Job логи: https://github.com/$REPO/actions/runs/$RUN_ID/job/$JOB_ID"
        echo "Run логи: https://github.com/$REPO/actions/runs/$RUN_ID"
        
        echo ""
        echo "🔍 Попытка получить краткие логи..."
        
        # Попробуем получить краткие логи через API
        LOGS_URL="https://api.github.com/repos/$REPO/actions/jobs/$JOB_ID/logs"
        echo "URL логов: $LOGS_URL"
        
        # Получаем логи (первые 1000 символов)
        LOGS=$(curl -s "$LOGS_URL" | head -c 1000)
        if [ -n "$LOGS" ]; then
            echo ""
            echo "📄 Краткие логи:"
            echo "=================================="
            echo "$LOGS"
        else
            echo "❌ Не удалось получить логи через API"
        fi
    fi
else
    echo "❌ Не удалось найти job'ы для сборки"
fi