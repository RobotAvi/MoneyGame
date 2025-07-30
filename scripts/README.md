# Скрипты очистки кэшей GitHub Actions

## 🗑️ Доступные скрипты

### 1. **clear-cache-simple.ps1** (Рекомендуется для Windows)
```powershell
powershell -ExecutionPolicy Bypass -File scripts/clear-cache-simple.ps1
```

### 2. **clear-cache.ps1** (Расширенная версия для Windows)
```powershell
powershell -ExecutionPolicy Bypass -File scripts/clear-cache.ps1
```

### 3. **clear-cache-simple.sh** (Для Linux/macOS)
```bash
bash scripts/clear-cache-simple.sh
```

### 4. **clear-cache-universal.sh** (Универсальный)
```bash
bash scripts/clear-cache-universal.sh
```

### 5. **clear-cache-api.sh** (Через GitHub API)
```bash
bash scripts/clear-cache-api.sh
```

### 6. **clear-cache-gh.sh** (Через GitHub CLI)
```bash
bash scripts/clear-cache-gh.sh
```

## 🚀 Быстрый запуск

### Windows (PowerShell):
```powershell
powershell -ExecutionPolicy Bypass -File scripts/clear-cache-simple.ps1
```

### Linux/macOS (Bash):
```bash
bash scripts/clear-cache-simple.sh
```

## 📋 Требования

- **GitHub CLI** (`gh`) - [Установка](https://cli.github.com/)
- **Авторизация** в GitHub CLI: `gh auth login`

## 🔧 Проверка статуса

```bash
# Проверить количество кэшей
gh api repos/RobotAvi/MoneyGame/actions/caches --jq '.total_count'

# Проверить авторизацию
gh auth status
```

## 📊 Результат

Скрипты покажут:
- Количество найденных кэшей
- Процесс удаления каждого кэша
- Итоговое количество удаленных кэшей
- Количество оставшихся кэшей

## 🎯 Пример вывода

```
Clearing GitHub Actions caches...
Getting cache list...
Found caches: 30
Deleting: Linux-gradle-abc123...
Deleted: Linux-gradle-abc123
...
Deleted caches: 30 from 30
Checking after cleanup...
Remaining caches: 0
All caches cleared successfully!
```

## ⚠️ Примечания

- Скрипты используют GitHub CLI для безопасного доступа к API
- Все операции логируются для отслеживания
- При ошибках скрипт продолжит работу с другими кэшами
- Рекомендуется запускать перед проблемными сборками