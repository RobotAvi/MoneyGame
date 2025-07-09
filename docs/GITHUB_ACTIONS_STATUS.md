# ⚡️ Важно
> CI/CD полностью автоматизирован для всех основных веток (main/master). Сборка, релизы и релиз-ноты происходят без ручного вмешательства.

# 🎯 GitHub Actions Status

## 📊 Build Status

![Build APK](https://github.com/RobotAvi/MoneyGame/workflows/🎮%20Build%20Android%20APK/badge.svg)
![Code Quality](https://github.com/RobotAvi/MoneyGame/workflows/🔍%20Code%20Quality%20Check/badge.svg)
![Release](https://github.com/RobotAvi/MoneyGame/workflows/🚀%20Release%20APK/badge.svg)

## 🔧 Последние исправления

### ✅ v1.0.7 - GitHub Actions Fix + Fast Track Bug
- **Исправлено**: Падения GitHub Actions из-за Unicode символов в workflow
- **Исправлено**: Критический баг скоростной дорожки в игре
- **Исправлено**: Security сканирование (заменен TruffleHog)
- **Добавлено**: Настройки release сборки в Gradle
- **Результат**: Стабильные CI/CD + функциональная игра

### ✅ v1.0.6 - APK Auto-Deploy to Releases  
- **Новое**: Автоматическое сохранение APK в releases/ папку
- **Структура**: releases/debug/ и releases/release/ 
- **Symlinks**: latest.apk файлы для быстрого доступа
- **Результат**: APK доступны прямо из репозитория без GitHub Releases

### ✅ v1.0.5 - Manual Android SDK Setup  
- **Проблема**: Нестабильность android-actions/setup-android
- **Решение**: Полная ручная установка Android SDK
- **Добавлено**: Кэширование SDK, условная установка, error handling
- **Результат**: Максимально надежная сборка APK в CI/CD

### 📝 Детали исправления:
1. **Убрана зависимость** от android-actions/setup-android полностью
2. **Ручная загрузка SDK** напрямую с Google servers
3. **Кэширование Android SDK** для ускорения повторных сборок
4. **Условная установка** - SDK скачивается только при необходимости
5. **Error handling** - добавлены fallback механизмы
   ```yaml
   - name: Set up Android SDK
     run: |
       if [ ! -d "$HOME/android-sdk/cmdline-tools/latest" ]; then
         echo "Installing Android SDK..."
         mkdir -p $HOME/android-sdk/cmdline-tools
         cd $HOME/android-sdk/cmdline-tools
         wget -q https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
         unzip -q commandlinetools-linux-11076708_latest.zip
         mv cmdline-tools latest
       else
         echo "Android SDK found in cache, skipping download"
       fi
   ```
6. Обновлены все workflow файлы: build-apk.yml, release.yml, code-quality.yml

## Releases Папка

### 📁 Структура:
- **releases/debug/** - Debug APK для разработки и тестирования
- **releases/release/** - Production APK для пользователей  
- **latest symlinks** - Быстрый доступ к последним версиям

### 🔄 Автообновление:
- **Debug APK**: Обновляется при каждом push в main
- **Release APK**: Обновляется при создании git tag
- **Naming**: Версия + дата + короткий commit hash
- **Symlinks**: Всегда указывают на последний APK

### 📋 Преимущества:
✅ **Быстрый доступ** - APK прямо в репозитории  
✅ **История версий** - Все APK файлы сохраняются  
✅ **Latest links** - Симлинки на актуальные версии  
✅ **Автоматизация** - Без ручного вмешательства  

## 🚀 Workflows

### 1. 🎮 Build Android APK
- **Триггер**: Push в main/master/develop, PR
- **Время сборки**: ~5-7 минут
- **Результат**: Debug APK + тесты + коммит в releases/debug/
- **Артефакты**: 30 дней хранения + постоянное хранение в репозитории

### 2. 🚀 Release APK  
- **Триггер**: Создание git tag (v*.*.*)
- **Время сборки**: ~6-8 минут
- **Результат**: GitHub Release + коммит в releases/release/
- **Changelog**: Автогенерация на базе коммитов
- **Двойное хранение**: GitHub Releases + папка в репозитории

### 3. 🔍 Code Quality Check
- **Триггер**: Push, PR
- **Проверки**: Android Lint, Detekt, Security scan
- **Зависимости**: Проверка outdated packages
- **Отчеты**: 7 дней хранения

## 🐛 Troubleshooting

### Если сборка упала:
1. **Проверьте лог** в GitHub Actions tab
2. **SDK проблемы**: Убедитесь что используется android-actions/setup-android@v2
3. **Memory issues**: Уменьшите org.gradle.jvmargs в gradle.properties
4. **Timeout**: Увеличьте timeout-minutes в workflow

### Полезные ссылки:
- [GitHub Actions](https://github.com/RobotAvi/MoneyGame/actions)
- [Releases](https://github.com/RobotAvi/MoneyGame/releases)
- [Troubleshooting Guide](GITHUB_ACTIONS_GUIDE.md)

---
*Последнее обновление: $(date)*
## 🧪 Последняя проверка

### ✅ 2025-01-07 22:XX - Cleanup & Test
- **Очистка**: Удалены старые APK файлы из корня (27MB освобождено)
- **Реорганизация**: Картинки перемещены в graphics/professions/
- **Ресурсы**: Картинки добавлены в app/src/main/res/drawable-hdpi/
- **Test push**: Создан тестовый коммит для проверки GitHub Actions

### 📈 Ожидаемый результат:
GitHub Actions должны создать APK в releases/debug/ в течение 5-7 минут.

### 🔗 Мониторинг:
- Статус: https://github.com/RobotAvi/MoneyGame/actions
- Результат: releases/debug/FinancialSuccess-debug-latest.apk

