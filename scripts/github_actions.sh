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

# Функции для работы с GitHub Actions
list_workflows() {
    echo "==== Список доступных workflows ===="
    gh workflow list
}

run_workflow() {
    local workflow_name="$1"
    if [ -z "$workflow_name" ]; then
        echo "Использование: $0 run <workflow_name>" >&2
        echo "Доступные workflows:" >&2
        gh workflow list
        exit 1
    fi
    
    echo "==== Запуск workflow: $workflow_name ===="
    gh workflow run "$workflow_name"
}

list_runs() {
    local workflow_name="$1"
    local limit="${2:-10}"
    
    if [ -n "$workflow_name" ]; then
        echo "==== Последние запуски workflow: $workflow_name ===="
        gh run list --workflow="$workflow_name" --limit "$limit"
    else
        echo "==== Последние запуски всех workflows ===="
        gh run list --limit "$limit"
    fi
}

check_status() {
    local run_id="$1"
    if [ -z "$run_id" ]; then
        echo "Использование: $0 status <run_id>" >&2
        exit 1
    fi
    
    echo "==== Статус запуска: $run_id ===="
    gh run view "$run_id"
}

get_logs() {
    local run_id="$1"
    local job_id="$2"
    
    if [ -z "$run_id" ]; then
        echo "Использование: $0 logs <run_id> [job_id]" >&2
        exit 1
    fi
    
    if [ -n "$job_id" ]; then
        echo "==== Логи job $job_id в run $run_id ===="
        gh run view --log --job="$job_id"
    else
        echo "==== Логи run $run_id ===="
        gh run view --log "$run_id"
    fi
}

get_failed_logs() {
    local run_id="$1"
    if [ -z "$run_id" ]; then
        echo "Использование: $0 failed-logs <run_id>" >&2
        exit 1
    fi
    
    echo "==== Логи неудачных job'ов в run $run_id ===="
    gh run view --log-failed "$run_id"
}

monitor_run() {
    local run_id="$1"
    local max_attempts="${2:-60}"
    local delay="${3:-30}"
    
    if [ -z "$run_id" ]; then
        echo "Использование: $0 monitor <run_id> [max_attempts] [delay_seconds]" >&2
        exit 1
    fi
    
    echo "🔍 Мониторинг run: $run_id"
    echo "⏱️  Максимум попыток: $max_attempts, интервал: ${delay}с"
    echo ""
    
    for ((i=1; i<=max_attempts; i++)); do
        echo "📊 Попытка $i/$max_attempts..."
        
        # Получаем статус
        local status=$(gh run view "$run_id" --json status,conclusion --jq '.[0].status')
        local conclusion=$(gh run view "$run_id" --json status,conclusion --jq '.[0].conclusion // "running"')
        
        echo "   Статус: $status"
        echo "   Результат: $conclusion"
        
        if [ "$status" = "completed" ]; then
            if [ "$conclusion" = "success" ]; then
                echo "✅ Workflow успешно завершен!"
                return 0
            else
                echo "❌ Workflow завершился с ошибкой: $conclusion"
                return 1
            fi
        fi
        
        if [ $i -lt $max_attempts ]; then
            echo "   ⏳ Ожидание ${delay}с..."
            sleep "$delay"
        fi
    done
    
    echo "⏰ ПРЕВЫШЕНО ВРЕМЯ ОЖИДАНИЯ!"
    return 1
}

# Основная логика
case "${1:-help}" in
    "list")
        list_workflows
        ;;
    "run")
        run_workflow "$2"
        ;;
    "runs")
        list_runs "$2" "$3"
        ;;
    "status")
        check_status "$2"
        ;;
    "logs")
        get_logs "$2" "$3"
        ;;
    "failed-logs")
        get_failed_logs "$2"
        ;;
    "monitor")
        monitor_run "$2" "$3" "$4"
        ;;
    "help"|*)
        echo "GitHub Actions CLI Tool"
        echo ""
        echo "Использование: $0 <команда> [параметры]"
        echo ""
        echo "Команды:"
        echo "  list                    - Список доступных workflows"
        echo "  run <workflow_name>     - Запуск workflow"
        echo "  runs [workflow_name] [limit] - Список последних запусков"
        echo "  status <run_id>         - Статус конкретного запуска"
        echo "  logs <run_id> [job_id]  - Логи запуска или job'а"
        echo "  failed-logs <run_id>    - Логи только неудачных job'ов"
        echo "  monitor <run_id> [max_attempts] [delay] - Мониторинг запуска"
        echo "  help                    - Показать эту справку"
        echo ""
        echo "Примеры:"
        echo "  $0 list"
        echo "  $0 run 'stable-build.yml'"
        echo "  $0 runs 'stable-build.yml' 5"
        echo "  $0 status 123456789"
        echo "  $0 monitor 123456789 30 60"
        ;;
esac