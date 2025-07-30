#!/bin/bash
set -e

echo "🗑️ Универсальная очистка кэшей GitHub Actions..."

# Проверяем авторизацию
if ! gh auth status &> /dev/null; then
    echo "❌ Не авторизован в GitHub CLI. Выполните: gh auth login"
    exit 1
fi

# Получаем список кэшей
echo "🔍 Получение списка кэшей..."
CACHE_KEYS=$(gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.actions_caches[] | .key' 2>/dev/null || echo "")

if [ -n "$CACHE_KEYS" ]; then
    CACHE_COUNT=$(echo "$CACHE_KEYS" | wc -l)
    echo "📊 Найдено кэшей: $CACHE_COUNT"
    
    # Удаляем кэши
    CLEARED=0
    while IFS= read -r key; do
        if [ -n "$key" ]; then
            echo "🗑️ Удаление: $key"
            if gh api repos/RobotAvi/MoneyGame/actions/caches -X DELETE --field key="$key" >/dev/null 2>&1; then
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
REMAINING=$(gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.total_count' 2>/dev/null || echo "0")
echo "📊 Осталось кэшей: $REMAINING"

if [ "$REMAINING" -eq 0 ]; then
    echo "🎉 Все кэши успешно очищены!"
else
    echo "⚠️ Осталось $REMAINING кэшей"
fi 