#!/bin/bash

# Скрипт для проверки статуса последних сборок
# Использует GitHub API для публичного репозитория

REPO="RobotAvi/MoneyGame"

echo "🔍 Проверка статуса сборок для репозитория: $REPO"
echo ""

# Получаем последние 5 сборок
echo "📋 Последние сборки:"
echo "=================================="

# Получаем JSON и извлекаем нужные поля
RUNS_JSON=$(curl -s "https://api.github.com/repos/$REPO/actions/runs?per_page=5")

# Простой парсинг с помощью grep и sed
echo "$RUNS_JSON" | grep -E '"run_number"|"status"|"conclusion"|"head_branch"|"head_sha"' | \
while IFS= read -r line; do
    if echo "$line" | grep -q '"run_number"'; then
        RUN_NUM=$(echo "$line" | sed 's/.*"run_number":\([0-9]*\).*/\1/')
        echo -n "Run #$RUN_NUM | "
    elif echo "$line" | grep -q '"status"'; then
        STATUS=$(echo "$line" | sed 's/.*"status":"\([^"]*\)".*/\1/')
        echo -n "$STATUS | "
    elif echo "$line" | grep -q '"conclusion"'; then
        CONCLUSION=$(echo "$line" | sed 's/.*"conclusion":"\([^"]*\)".*/\1/')
        if [ "$CONCLUSION" = "null" ]; then
            CONCLUSION="running"
        fi
        echo -n "$CONCLUSION | "
    elif echo "$line" | grep -q '"head_branch"'; then
        BRANCH_NAME=$(echo "$line" | sed 's/.*"head_branch":"\([^"]*\)".*/\1/')
        echo -n "$BRANCH_NAME | "
    elif echo "$line" | grep -q '"head_sha"'; then
        SHA=$(echo "$line" | sed 's/.*"head_sha":"\([^"]*\)".*/\1/' | cut -c1-8)
        echo "$SHA"
    fi
done

echo ""
echo "🔍 Детали последней сборки:"
echo "=================================="

# Получаем ID последней сборки
LATEST_RUN_ID=$(curl -s "https://api.github.com/repos/$REPO/actions/runs?per_page=1" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)

if [ -n "$LATEST_RUN_ID" ]; then
    echo "ID последней сборки: $LATEST_RUN_ID"
    
    # Получаем детали последней сборки
    LATEST_RUN=$(curl -s "https://api.github.com/repos/$REPO/actions/runs/$LATEST_RUN_ID")
    
    # Извлекаем информацию
    RUN_NUMBER=$(echo "$LATEST_RUN" | grep -o '"run_number":[0-9]*' | cut -d: -f2)
    STATUS=$(echo "$LATEST_RUN" | grep -o '"status":"[^"]*"' | cut -d: -f2 | tr -d '"')
    CONCLUSION=$(echo "$LATEST_RUN" | grep -o '"conclusion":"[^"]*"' | cut -d: -f2 | tr -d '"')
    HEAD_SHA=$(echo "$LATEST_RUN" | grep -o '"head_sha":"[^"]*"' | cut -d: -f2 | tr -d '"' | cut -c1-8)
    
    echo "Номер сборки: $RUN_NUMBER"
    echo "Статус: $STATUS"
    echo "Результат: ${CONCLUSION:-running}"
    echo "Коммит: $HEAD_SHA"
    
    # Определяем эмодзи для статуса
    if [ "$STATUS" = "completed" ]; then
        if [ "$CONCLUSION" = "success" ]; then
            EMOJI="✅"
        else
            EMOJI="❌"
        fi
    elif [ "$STATUS" = "in_progress" ]; then
        EMOJI="🔄"
    else
        EMOJI="❓"
    fi
    
    echo ""
    echo "$EMOJI Итоговый статус: $STATUS (${CONCLUSION:-running})"
    
    # Если сборка неудачная, показываем ссылку на логи
    if [ "$STATUS" = "completed" ] && [ "$CONCLUSION" != "success" ]; then
        echo ""
        echo "📋 Ссылка на логи:"
        echo "https://github.com/$REPO/actions/runs/$LATEST_RUN_ID"
    fi
else
    echo "❌ Не удалось получить информацию о последней сборке"
fi