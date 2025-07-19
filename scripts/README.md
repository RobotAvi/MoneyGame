# GitHub Actions Management Scripts

Набор скриптов для управления GitHub Actions workflows, мониторинга их выполнения и анализа результатов.

## 📋 Требования

- GitHub CLI (`gh`) установлен и настроен
- Токен GitHub с правами на репозиторий
- Утилита `jq` для работы с JSON
- Bash shell

## 🔧 Настройка

1. **Создайте файл конфигурации** `config.env` в корне проекта:
```bash
# GitHub Configuration
GITHUB_TOKEN=your_github_token_here
GITHUB_REPO=RobotAvi/FinancialCalculator
```

2. **Убедитесь, что файл добавлен в .gitignore**:
```gitignore
# Configuration files with sensitive data
config.env
*.env
```

3. **Сделайте скрипты исполняемыми**:
```bash
chmod +x scripts/*.sh
```

## 📁 Описание скриптов

### 1. `github_actions.sh` - Основной CLI инструмент

Многофункциональный скрипт для работы с GitHub Actions.

**Использование:**
```bash
./github_actions.sh <команда> [параметры]
```

**Команды:**
- `list` - Список доступных workflows
- `run <workflow_name>` - Запуск workflow
- `runs [workflow_name] [limit]` - Список последних запусков
- `status <run_id>` - Статус конкретного запуска
- `logs <run_id> [job_id]` - Логи запуска или job'а
- `failed-logs <run_id>` - Логи только неудачных job'ов
- `monitor <run_id> [max_attempts] [delay]` - Мониторинг запуска
- `help` - Показать справку

**Примеры:**
```bash
# Список workflows
./github_actions.sh list

# Запуск workflow
./github_actions.sh run "🚀 Stable Build & Release"

# Мониторинг запуска
./github_actions.sh monitor 123456789 30 60

# Просмотр логов ошибок
./github_actions.sh failed-logs 123456789
```

### 2. `smart_run.sh` - Умный запуск с анализом

Запускает workflow с автоматическим анализом ошибок и повторными попытками.

**Использование:**
```bash
./smart_run.sh <workflow_name> [max_retries] [monitor_delay_seconds]
```

**Особенности:**
- Автоматический анализ причин ошибок
- Рекомендации по исправлению проблем
- Повторные попытки при неудаче
- Детальный мониторинг прогресса

**Пример:**
```bash
./smart_run.sh "🚀 Stable Build & Release" 3 60
```

### 3. `run_and_wait.sh` - Простой запуск с ожиданием

Простой скрипт для запуска workflow и ожидания завершения.

**Использование:**
```bash
./run_and_wait.sh <workflow_name> [max_attempts] [delay_seconds]
```

**Пример:**
```bash
./run_and_wait.sh "🚀 Stable Build & Release" 30 30
```

### 4. `monitor_workflows.sh` - Мониторинг в реальном времени

Отображает статус всех workflows в реальном времени.

**Использование:**
```bash
./monitor_workflows.sh [delay_seconds]
```

**Пример:**
```bash
./monitor_workflows.sh 30
```

## 🚀 Быстрый старт

1. **Проверьте доступные workflows:**
```bash
cd scripts
./github_actions.sh list
```

2. **Запустите workflow с умным мониторингом:**
```bash
./smart_run.sh "🚀 Stable Build & Release"
```

3. **Мониторьте выполнение в реальном времени:**
```bash
./monitor_workflows.sh
```

## 🔍 Анализ проблем

### Проблемы с Android эмулятором

Если workflow падает на job'ах `screenshots-phone` или `screenshots-tablet`:

**Симптомы:**
- Эмулятор не успевает загрузиться за 180 секунд
- Ошибки `adb shell getprop failed`
- Процесс эмулятора работает, но Android не загружается

**Решения:**
1. Увеличьте время ожидания в workflow
2. Запустите только build без screenshots
3. Используйте более легкий эмулятор
4. Добавьте аппаратное ускорение

### Проблемы со сборкой

**Симптомы:**
- Ошибки компиляции
- Проблемы с зависимостями
- Неудачные тесты

**Решения:**
1. Проверьте логи сборки: `./github_actions.sh logs <run_id>`
2. Обновите зависимости
3. Исправьте ошибки в коде

## 📊 Полезные команды

```bash
# Последние 5 запусков
./github_actions.sh runs "🚀 Stable Build & Release" 5

# Статус конкретного запуска
./github_actions.sh status 123456789

# Логи неудачного job'а
./github_actions.sh logs 123456789 987654321

# Мониторинг с коротким интервалом
./github_actions.sh monitor 123456789 120 10
```

## 🔧 Устранение неполадок

### Проблема с переменной PAGER
```bash
unset PAGER
./github_actions.sh list
```

### Проблема с токеном
```bash
# Проверьте статус аутентификации
gh auth status

# Переустановите токен
echo "your_token" | gh auth login --with-token
```

### Проблема с jq
```bash
# Установите jq
sudo apt update && sudo apt install jq -y
```

## 📝 Логи и отладка

Все скрипты выводят подробную информацию о процессе выполнения. Для отладки используйте:

```bash
# Включите режим отладки
set -x
./smart_run.sh "🚀 Stable Build & Release"
set +x
```

## 🤝 Вклад в проект

При добавлении новых скриптов:
1. Следуйте существующему стилю кода
2. Добавьте документацию в README
3. Сделайте скрипт исполняемым
4. Протестируйте на разных сценариях

## 📞 Поддержка

При возникновении проблем:
1. Проверьте логи с помощью `./github_actions.sh failed-logs <run_id>`
2. Убедитесь, что токен имеет необходимые права
3. Проверьте статус GitHub Actions в веб-интерфейсе
4. Создайте issue с подробным описанием проблемы