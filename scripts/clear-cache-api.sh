#!/bin/bash
set -e

echo "🗑️ Очистка кэшей через GitHub API..."

# Получаем токен
TOKEN=$(gh auth token)
if [ -z "$TOKEN" ]; then
    echo "❌ Не удалось получить токен GitHub"
    exit 1
fi

# Получаем список всех кэшей
echo "🔍 Получение списка кэшей..."
CACHES_RESPONSE=$(curl -s -H "Authorization: token $TOKEN" \
    -H "Accept: application/vnd.github.v3+json" \
    "https://api.github.com/repos/RobotAvi/MoneyGame/actions/caches")

# Извлекаем ключи кэшей
CACHE_KEYS=$(echo "$CACHES_RESPONSE" | jq -r '.actions_caches[] | .key' 2>/dev/null || echo "")

if [ -n "$CACHE_KEYS" ]; then
    CACHE_COUNT=$(echo "$CACHE_KEYS" | wc -l)
    echo "📊 Найдено кэшей: $CACHE_COUNT"
    
    # Удаляем каждый кэш
    CLEARED=0
    while IFS= read -r key; do
        if [ -n "$key" ]; then
            echo "🗑️ Удаление: $key"
            DELETE_RESPONSE=$(curl -s -X DELETE \
                -H "Authorization: token $TOKEN" \
                -H "Accept: application/vnd.github.v3+json" \
                "https://api.github.com/repos/RobotAvi/MoneyGame/actions/caches?key=$key")
            
            if [ $? -eq 0 ]; then
                echo "✅ Удален: $key"
                ((CLEARED++))
            else
                echo "❌ Ошибка: $key"
            fi
        fi
    done <<< "$CACHE_KEYS"
    
    echo "🎉 Удалено кэшей: $CLEARED из $CACHE_COUNT"
else
    echo "ℹ️ Кэши не найдены"
fi

# Проверяем результат
echo "📊 Проверка после очистки..."
REMAINING_RESPONSE=$(curl -s -H "Authorization: token $TOKEN" \
    -H "Accept: application/vnd.github.v3+json" \
    "https://api.github.com/repos/RobotAvi/MoneyGame/actions/caches")
REMAINING=$(echo "$REMAINING_RESPONSE" | jq -r '.total_count' 2>/dev/null || echo "0")
echo "📊 Осталось кэшей: $REMAINING" 