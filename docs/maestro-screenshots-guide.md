---
marp: true
theme: default
paginate: true
backgroundColor: #fff
backgroundImage: url('https://marp.app/assets/hero-background.svg')
---

# Автоматическое снятие скриншотов Android приложений с Maestro

## Полное руководство от установки до результата

---

## Содержание

1. **Подготовка окружения**
2. **Установка Android SDK и эмулятора**
3. **Установка Maestro**
4. **Настройка приложения**
5. **Создание сценариев Maestro**
6. **Запуск и получение скриншотов**
7. **Интеграция с CI/CD**

---

## 1. Подготовка окружения

### Системные требования:
- **OS:** Linux, macOS или Windows
- **RAM:** Минимум 4GB (рекомендуется 8GB+)
- **Диск:** 10GB свободного места
- **Процессор:** Поддержка виртуализации (KVM/Intel VT-x/AMD-V)

### Установка зависимостей:

```bash
# Ubuntu/Debian
sudo apt-get update
sudo apt-get install -y openjdk-11-jdk wget unzip curl

# macOS
brew install openjdk@11 wget curl

# Windows
# Скачать и установить Java 11 с официального сайта
```

---

## 2. Установка Android SDK

### Скачивание и установка:

```bash
# Скачиваем Android Command Line Tools
wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip

# Распаковываем
unzip commandlinetools-linux-9477386_latest.zip -d $HOME

# Создаем структуру папок
mkdir -p $HOME/android-sdk/cmdline-tools
mv $HOME/cmdline-tools $HOME/android-sdk/cmdline-tools/latest

# Настраиваем переменные окружения
export ANDROID_SDK_ROOT=$HOME/android-sdk
export PATH=$PATH:$HOME/android-sdk/cmdline-tools/latest/bin
```

---

## 3. Установка компонентов Android SDK

### Установка необходимых компонентов:

```bash
# Принимаем лицензии
yes | sdkmanager --licenses

# Устанавливаем platform-tools (включает adb)
sdkmanager "platform-tools"

# Устанавливаем эмулятор
sdkmanager "emulator"

# Устанавливаем системный образ Android
sdkmanager "system-images;android-30;default;x86_64"

# Обновляем PATH
# Для CI/CD (например, GitHub Actions) используйте:
echo "$HOME/android-sdk/platform-tools" >> $GITHUB_PATH
# Это гарантирует, что adb будет доступен во всех шагах workflow.
export PATH=$PATH:$HOME/android-sdk/emulator
```

---

## 4. Создание и запуск эмулятора

### Создание AVD (Android Virtual Device):

```bash
# Создаем эмулятор
echo "no" | avdmanager create avd -n test-maestro-avd \
  -k "system-images;android-30;default;x86_64" --force

# Запускаем эмулятор в headless режиме
emulator -avd test-maestro-avd -no-window \
  -gpu swiftshader_indirect -no-snapshot \
  -no-audio -no-boot-anim -accel off -no-metrics &

# Ждем запуска
adb wait-for-device
```

---

## 5. Установка Maestro

### Установка Maestro CLI:

```bash
# Скачиваем и устанавливаем Maestro
curl -Ls "https://get.maestro.mobile.dev" | bash

# Добавляем в PATH
export PATH="$PATH:$HOME/.maestro/bin"

# Проверяем установку
maestro --version
```

### Настройка переменных окружения:

```bash
# Отключаем уведомления об аналитике
export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED="true"

# Автоматически отвечаем "n" на вопросы об аналитике
echo "n" | maestro --version
```

---

## 6. Подготовка приложения

### Установка APK на эмулятор:

```bash
# Устанавливаем APK
adb install path/to/your/app-debug.apk

# Проверяем установку
adb shell pm list packages | grep your.package.name

# Запускаем приложение для проверки
adb shell monkey -p your.package.name -c android.intent.category.LAUNCHER 1
```

### Определение правильного package name:

```bash
# Используем aapt для получения информации о APK
$ANDROID_SDK_ROOT/build-tools/30.0.3/aapt dump badging your-app.apk | grep package
```

---

## 7. Создание сценария Maestro

### Базовый сценарий (`screenshots-minimal.yaml`):

```yaml
appId: your.package.name.debug
name: App Screenshots (Minimal)
---
- launchApp
- assertVisible: "Текст на главном экране"
- takeScreenshot: "01_main_screen"
```

### Расширенный сценарий:

```yaml
appId: your.package.name.debug
name: App Screenshots (Full)
outputDir: screenshots/phone

- launchApp
- assertVisible: "Главный текст"
- takeScreenshot: "01_main_screen"

- tapOn: "Кнопка"
- takeScreenshot: "02_after_button"

- assertVisible: "Новый экран"
- takeScreenshot: "03_new_screen"
```

---

## 8. Запуск Maestro и получение скриншотов

### Запуск теста:

```bash
# Запускаем сценарий
maestro test maestro/screenshots-minimal.yaml

# Запуск с дополнительными опциями
maestro test maestro/screenshots-minimal.yaml \
  --format junit \
  --output screenshots/
```

### Проверка результатов:

```bash
# Смотрим созданные файлы
ls -la screenshots/

# Проверяем логи
find ~/.maestro/tests/ -name "*.log" | tail -1 | xargs cat
```

---

## 9. Ручное снятие скриншота (fallback)

### Если Maestro не работает:

```bash
# Запускаем приложение
adb shell monkey -p your.package.name -c android.intent.category.LAUNCHER 1

# Ждем загрузки
sleep 5

# Делаем скриншот
adb shell screencap -p /sdcard/manual_screen.png

# Копируем на компьютер
adb pull /sdcard/manual_screen.png ./manual_screen.png
```

---

## 10. Интеграция с GitHub Actions

### Workflow файл (`.github/workflows/screenshots.yml`):

```yaml
name: Screenshots

on: [push, pull_request]

jobs:
  screenshots:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      
      - name: Install Android SDK
        run: |
          wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
          unzip commandlinetools-linux-9477386_latest.zip -d $HOME
          mkdir -p $HOME/android-sdk/cmdline-tools
          mv $HOME/cmdline-tools $HOME/android-sdk/cmdline-tools/latest
          export ANDROID_SDK_ROOT=$HOME/android-sdk
          export PATH=$PATH:$HOME/android-sdk/cmdline-tools/latest/bin
          yes | sdkmanager --licenses
          sdkmanager "platform-tools" "emulator"
      
      - name: Install Maestro
        run: |
          curl -Ls "https://get.maestro.mobile.dev" | bash
          export PATH="$PATH:$HOME/.maestro/bin"
          export MAESTRO_CLI_ANALYSIS_NOTIFICATION_DISABLED="true"
          echo "n" | maestro --version
      
      - name: Start Android Emulator
        uses: reactivecircus/android-emulator-runner@v2
        with:
          api-level: 30
          target: google_apis
          arch: x86_64
          profile: Nexus 6
          disable-animations: true
          emulator-options: -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim -accel off -no-snapshot -no-metrics
          script: |
            adb install app/build/outputs/apk/debug/app-debug.apk
            maestro test maestro/screenshots-minimal.yaml
      
      - name: Upload Screenshots
        uses: actions/upload-artifact@v4
        with:
          name: screenshots
          path: screenshots/
```

---

## 11. Частые проблемы и решения

### Проблема: `adb: command not found`
**Решение:** Установить platform-tools и добавить в PATH

### Проблема: Эмулятор не запускается
**Решение:** Использовать параметры без KVM:
```bash
emulator -avd test-avd -no-window -gpu swiftshader_indirect -accel off
```

### Проблема: Приложение не запускается
**Решение:** Проверить package name и установку APK

### Проблема: Скриншот пустой/неправильный
**Решение:** Добавить `assertVisible` перед `takeScreenshot`

---

## 12. Лучшие практики

### ✅ Рекомендуется:
- Использовать `assertVisible` для ожидания элементов
- Добавлять паузы между действиями
- Тестировать на разных размерах экранов
- Сохранять скриншоты в структурированные папки
- Использовать CI/CD для автоматизации

### ❌ Избегать:
- Жестко заданных координат
- Отсутствия проверок видимости элементов
- Запуска без проверки готовности эмулятора
- Игнорирования ошибок установки APK

---

## 13. Полезные команды

### Отладка:
```bash
# Проверка устройств
adb devices

# Логи приложения
adb logcat | grep your.package

# Информация о приложении
adb shell dumpsys package your.package

# UI иерархия
adb shell uiautomator dump
adb pull /sdcard/window_dump.xml
```

### Очистка:
```bash
# Очистка данных приложения
adb shell pm clear your.package

# Перезапуск эмулятора
adb emu kill
```

---

## 14. Результат

После выполнения всех шагов вы получите:

1. **Автоматизированное снятие скриншотов** вашего приложения
2. **Интеграцию с CI/CD** для регулярного тестирования
3. **Документированные сценарии** для разных экранов
4. **Надежную инфраструктуру** для UI тестирования

### Файлы результатов:
- `screenshots/01_main_screen.png`
- `screenshots/02_after_action.png`
- `maestro.log` (логи выполнения)

---

## Спасибо за внимание!

### Полезные ссылки:
- [Maestro Documentation](https://maestro.mobile.dev/)
- [Android SDK Documentation](https://developer.android.com/studio/command-line)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)

### Контакты:
- GitHub: [@your-username](https://github.com/your-username)
- Email: your.email@example.com

---

# Вопросы и ответы

## Готов ответить на ваши вопросы!

*Как настроить Maestro для вашего проекта?*