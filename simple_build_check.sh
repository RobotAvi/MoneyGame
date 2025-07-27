#!/bin/bash

echo "🔍 Проверка статуса последних сборок"
echo "=================================="

# Получаем последние сборки
RUNS=$(curl -s "https://api.github.com/repos/RobotAvi/MoneyGame/actions/runs?per_page=3")

echo "📋 Последние 3 сборки:"
echo ""

# Извлекаем информацию о каждой сборке
echo "$RUNS" | grep -E '"run_number"|"status"|"conclusion"|"head_sha"' | \
while IFS= read -r line; do
    if echo "$line" | grep -q '"run_number"'; then
        RUN_NUM=$(echo "$line" | grep -o '[0-9]*')
        echo -n "Run #$RUN_NUM: "
    elif echo "$line" | grep -q '"status"'; then
        STATUS=$(echo "$line" | grep -o '"[^"]*"' | tail -1 | tr -d '"')
        echo -n "$STATUS "
    elif echo "$line" | grep -q '"conclusion"'; then
        CONCLUSION=$(echo "$line" | grep -o '"[^"]*"' | tail -1 | tr -d '"')
        if [ "$CONCLUSION" = "null" ]; then
            CONCLUSION="running"
        fi
        echo -n "($CONCLUSION) "
    elif echo "$line" | grep -q '"head_sha"'; then
        SHA=$(echo "$line" | grep -o '"[^"]*"' | tail -1 | tr -d '"' | cut -c1-8)
        echo "commit: $SHA"
    fi
done

echo ""
echo "🔍 Последняя сборка:"
echo "=================================="

# Получаем ID последней сборки
LATEST_RUN_ID=$(curl -s "https://api.github.com/repos/RobotAvi/MoneyGame/actions/runs?per_page=1" | grep -o '"id":[0-9]*' | head -1 | cut -d: -f2)

if [ -n "$LATEST_RUN_ID" ]; then
    echo "ID: $LATEST_RUN_ID"
    
    # Получаем статус последней сборки
    LATEST_RUN=$(curl -s "https://api.github.com/repos/RobotAvi/MoneyGame/actions/runs/$LATEST_RUN_ID")
    
    STATUS=$(echo "$LATEST_RUN" | grep -o '"status":"[^"]*"' | cut -d: -f2 | tr -d '"')
    CONCLUSION=$(echo "$LATEST_RUN" | grep -o '"conclusion":"[^"]*"' | cut -d: -f2 | tr -d '"')
    
    echo "Статус: $STATUS"
    echo "Результат: ${CONCLUSION:-running}"
    
    if [ "$STATUS" = "completed" ]; then
        if [ "$CONCLUSION" = "success" ]; then
            echo "✅ Сборка успешна!"
        else
            echo "❌ Сборка завершилась с ошибкой"
            echo "📋 Логи: https://github.com/RobotAvi/MoneyGame/actions/runs/$LATEST_RUN_ID"
        fi
    elif [ "$STATUS" = "in_progress" ]; then
        echo "🔄 Сборка выполняется..."
    else
        echo "❓ Неизвестный статус"
    fi
else
    echo "❌ Не удалось получить информацию"
fi