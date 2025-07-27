#!/bin/bash

echo "🔍 Анализ неудачных сборок"
echo "=================================="

# Получаем последние 5 сборок
RUNS_JSON=$(curl -s "https://api.github.com/repos/RobotAvi/MoneyGame/actions/runs?per_page=5")

echo "📋 Последние сборки с ошибками:"
echo ""

# Анализируем каждую сборку
echo "$RUNS_JSON" | grep -E '"run_number"|"status"|"conclusion"|"head_sha"' | \
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
        
        # Определяем, что это за коммит
        case $SHA in
            "ad9f822c")
                echo "   📝 Содержит: Документацию по анализу проблемы BuildConfig"
                ;;
            "a190a4fe")
                echo "   🔧 Содержит: Решение с заменой BuildConfig на простые константы"
                ;;
            "0edb93e1")
                echo "   🔧 Содержит: Fix BuildConfig generation with fallback mechanism"
                ;;
            *)
                echo "   ❓ Неизвестный коммит"
                ;;
        esac
        echo ""
    fi
done

echo "🔍 Анализ проблемы:"
echo "=================================="
echo "1. Сборка #248 (a190a4fe) тестировала наше решение с заменой BuildConfig"
echo "2. Сборка все равно завершилась с ошибкой"
echo "3. Это означает, что проблема НЕ в BuildConfig"
echo ""
echo "🔍 Возможные причины:"
echo "- Проблема в другом месте кода"
echo "- Проблема в конфигурации сборки"
echo "- Проблема в зависимостях"
echo "- Проблема в workflow"
echo ""
echo "📋 Ссылки на логи для анализа:"
echo "- Сборка #248: https://github.com/RobotAvi/MoneyGame/actions/runs/16548509214"
echo "- Сборка #249: https://github.com/RobotAvi/MoneyGame/actions/runs/16548511491"
echo ""
echo "💡 Рекомендация: Открыть логи в браузере для детального анализа"