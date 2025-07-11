# 🔧 Устранение проблем с Android эмулятором в CI/CD

## Проблема

Сборка падает на шаге с эмулятором Android с ошибками:
- `adb: device offline`
- `android.os.DeadSystemException`
- Эмулятор нестабилен и часто "умирает"

## Причины проблем

### 1. Нестабильность эмулятора в CI окружении
- **Проблема**: Эмулятор Android в CI/CD окружении часто нестабилен
- **Причина**: Ограниченные ресурсы, отсутствие графического интерфейса, проблемы с виртуализацией

### 2. Проблемы с ADB подключением
- **Проблема**: ADB не может стабильно подключиться к эмулятору
- **Причина**: Эмулятор еще не полностью загрузился или упал

### 3. DeadSystemException
- **Проблема**: Система эмулятора "умирает" во время выполнения команд
- **Причина**: Недостаточно ресурсов или проблемы с конфигурацией

## Решения

### Решение 1: Улучшенная конфигурация эмулятора

Создан файл `build-with-screenshots-fixed.yml` с улучшениями:

#### Ключевые изменения:
1. **Оптимизированные параметры эмулятора**:
   ```yaml
   emulator-options: |
     -no-window
     -no-audio
     -no-boot-anim
     -gpu swiftshader_indirect
     -camera-back none
     -camera-front none
     -memory 2048
     -cores 2
     -skin 1080x1920
     -accel on
   ```

2. **Улучшенный скрипт инициализации**:
   ```bash
   # Ждем полной загрузки эмулятора
   adb wait-for-device
   
   # Проверяем статус загрузки
   while ! adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; do
     echo "Waiting for boot completion..."
     sleep 5
   done
   
   # Дополнительное ожидание для стабилизации
   sleep 10
   ```

3. **Retry логика для установки APK**:
   ```bash
   install_apk_with_retry() {
     local max_attempts=5
     local attempt=1
     
     while [ $attempt -le $max_attempts ]; do
       # Проверяем что эмулятор доступен
       if ! adb devices | grep -q "emulator-5554"; then
         echo "Emulator not found, waiting..."
         sleep 10
         attempt=$((attempt + 1))
         continue
       fi
       
       # Пытаемся установить APK
       if adb install releases/debug/*.apk; then
         echo "APK installed successfully!"
         return 0
       else
         echo "Installation failed, attempt $attempt/$max_attempts"
         sleep 10
         attempt=$((attempt + 1))
       fi
     done
   }
   ```

### Решение 2: Альтернативный стабильный подход

Создан файл `build-with-screenshots-alternative.yml` с более консервативными настройками:

#### Ключевые особенности:
1. **Более стабильная конфигурация эмулятора**:
   - API Level 29 вместо 30
   - Меньше памяти (1536MB вместо 2048MB)
   - Один CPU core для стабильности

2. **Упрощенные тесты Maestro**:
   - Минимальные действия
   - Проверки видимости элементов
   - Резервный тест только с запуском приложения

3. **Дополнительные проверки стабильности**:
   ```bash
   # Очищаем кэш пакетного менеджера
   adb shell pm clear com.android.packageinstaller 2>/dev/null || true
   
   # Проверяем что приложение установлено
   if adb shell pm list packages | grep -q financialsuccess; then
     echo "Application verified as installed"
   ```

### Решение 3: Использование Firebase Test Lab (опционально)

Для максимальной стабильности можно использовать Firebase Test Lab:

```yaml
- name: Run Firebase Test Lab
  run: |
    gcloud firebase test android run \
      --type instrumentation \
      --app releases/debug/*.apk \
      --test maestro/screenshots.yaml \
      --device model=redfin,version=30,locale=en,orientation=portrait \
      --timeout 30m \
      --results-dir=firebase-test-results
```

**Требует настройки**:
- Google Cloud Project
- Service Account Key
- Настройка Firebase Test Lab

## Рекомендации по использованию

### 1. Начните с исправленной версии
Используйте `build-with-screenshots-fixed.yml` - он содержит все основные исправления.

### 2. Если проблемы продолжаются
Переключитесь на `build-with-screenshots-alternative.yml` с более консервативными настройками.

### 3. Для продакшена
Рассмотрите использование Firebase Test Lab для максимальной стабильности.

## Мониторинг и отладка

### Полезные команды для отладки:
```bash
# Проверка статуса эмулятора
adb devices

# Проверка загрузки системы
adb shell getprop sys.boot_completed

# Проверка установленных приложений
adb shell pm list packages | grep financialsuccess

# Логи эмулятора
adb logcat
```

### Типичные проблемы и решения:

1. **Эмулятор не запускается**:
   - Увеличьте timeout в конфигурации
   - Проверьте доступность ресурсов

2. **APK не устанавливается**:
   - Используйте retry логику
   - Очистите кэш пакетного менеджера

3. **Maestro падает**:
   - Упростите тесты
   - Добавьте проверки видимости элементов

## Заключение

Проблемы с эмулятором Android в CI/CD - распространенная проблема. Предложенные решения должны значительно повысить стабильность сборки:

1. **Исправленная версия** - основные улучшения
2. **Альтернативная версия** - более консервативный подход
3. **Firebase Test Lab** - максимальная стабильность

Выберите подходящий вариант в зависимости от ваших требований к стабильности и скорости сборки.