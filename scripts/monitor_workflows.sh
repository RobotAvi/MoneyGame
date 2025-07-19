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

DELAY="${1:-30}"

echo "🔍 Мониторинг GitHub Actions workflows"
echo "⏱️  Интервал обновления: ${DELAY}с"
echo "Нажмите Ctrl+C для остановки"
echo ""

while true; do
    clear
    echo "=== GitHub Actions Status - $(date) ==="
    echo ""
    
    # Получаем список всех последних запусков
    echo "📋 Последние запуски workflows:"
    gh run list --limit 5 --json databaseId,workflowName,status,conclusion,createdAt,url | \
        jq -r '.[] | "\(.databaseId) | \(.workflowName) | \(.status) | \(.conclusion // "running") | \(.createdAt)"' | \
        while IFS='|' read -r id workflow status conclusion created; do
            # Определяем эмодзи для статуса
            case "$status" in
                "completed")
                    if [ "$conclusion" = "success" ]; then
                        emoji="✅"
                    else
                        emoji="❌"
                    fi
                    ;;
                "in_progress")
                    emoji="🔄"
                    ;;
                "queued")
                    emoji="⏳"
                    ;;
                "waiting")
                    emoji="⏸️"
                    ;;
                *)
                    emoji="❓"
                    ;;
            esac
            
            printf "%-8s %s %-20s %-12s %s\n" "$id" "$emoji" "$workflow" "$status" "$conclusion"
        done
    
    echo ""
    echo "📊 Статистика по workflows:"
    
    # Получаем статистику по каждому workflow
    gh workflow list --json name,state,updatedAt | \
        jq -r '.[] | "\(.name) | \(.state)"' | \
        while IFS='|' read -r name state; do
            # Получаем количество успешных и неуспешных запусков
            success_count=$(gh run list --workflow="$name" --limit 100 --json conclusion | jq -r '[.[] | select(.conclusion == "success")] | length')
            failure_count=$(gh run list --workflow="$name" --limit 100 --json conclusion | jq -r '[.[] | select(.conclusion == "failure")] | length')
            
            printf "%-20s | Успешно: %-3s | Ошибок: %-3s | Статус: %s\n" "$name" "$success_count" "$failure_count" "$state"
        done
    
    echo ""
    echo "⏳ Обновление через ${DELAY}с... (Ctrl+C для остановки)"
    sleep "$DELAY"
done