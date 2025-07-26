# 🔍 Pre-Build Checklist - Чек-лист проверки перед сборкой

## 🎯 Цель
Найти критические ошибки ДО запуска сборки, чтобы избежать падения CI/CD.

## ✅ Обязательные проверки

### 1. 🔧 BuildConfig и импорты
```bash
# Проверить наличие import BuildConfig
grep -n "import.*BuildConfig" app/src/main/java/**/*.kt

# Проверить использование BuildConfig
grep -n "BuildConfig\." app/src/main/java/**/*.kt
```

### 2. 📁 APK файлы в workflow
```bash
# Проверить ВСЕ ссылки на APK файлы
grep -n "app.*apk" .github/workflows/*.yml

# Проверить команды копирования
grep -n "cp.*apk" .github/workflows/*.yml

# Проверить правильность имен файлов
# ✅ Правильно: app-debug.apk, app-release.apk
# ❌ Неправильно: app.apk
```

### 3. 🔢 Версии и хардкод
```bash
# Проверить захардкоженные версии
grep -n "1\.[0-9][0-9]" app/src/main/**/*.{kt,xml}

# Проверить константы версий
grep -n "APP_VERSION\|VERSION_NAME\|VERSION_CODE" app/src/main/**/*.kt

# Проверить android:text с версиями
grep -n "android:text.*[0-9]\.[0-9]" app/src/main/**/*.xml
```

### 4. 📦 Gradle конфигурация
```bash
# Проверить buildFeatures
grep -n "buildConfig" app/build.gradle

# Проверить buildConfigField
grep -n "buildConfigField" app/build.gradle

# Проверить versionName и versionCode
grep -n "versionName\|versionCode" app/build.gradle
```

### 5. 🔄 Workflow консистентность
```bash
# Проверить единообразие имен файлов
grep -n "FinancialSuccess.*apk" .github/workflows/*.yml

# Проверить переменные версий
grep -n "VERSION\|version" .github/workflows/*.yml
```

## 🚨 Критические ошибки для поиска

### ❌ Ошибка 1: Неправильное имя APK файла
```yaml
# ❌ Неправильно
cp app/build/outputs/apk/debug/app.apk

# ✅ Правильно  
cp app/build/outputs/apk/debug/app-debug.apk
```

### ❌ Ошибка 2: Отсутствующий import BuildConfig
```kotlin
// ❌ Неправильно
binding.tvVersion.text = "Версия ${BuildConfig.VERSION_NAME}"

// ✅ Правильно
import com.financialsuccess.game.BuildConfig
binding.tvVersion.text = "Версия ${BuildConfig.VERSION_NAME}"
```

### ❌ Ошибка 3: Захардкоженная версия
```xml
<!-- ❌ Неправильно -->
<TextView android:text="Версия 1.82" />

<!-- ✅ Правильно -->
<TextView android:id="@+id/tv_version" />
```

## 🔍 Команды для автоматической проверки

```bash
#!/bin/bash
echo "🔍 Pre-Build Validation..."

# 1. Проверка APK файлов в workflow
echo "📁 Checking APK references in workflow..."
if grep -q "app\.apk" .github/workflows/*.yml; then
    echo "❌ ERROR: Found incorrect 'app.apk' reference"
    grep -n "app\.apk" .github/workflows/*.yml
    exit 1
fi

# 2. Проверка BuildConfig imports
echo "🔧 Checking BuildConfig imports..."
if ! grep -q "import.*BuildConfig" app/src/main/java/**/*.kt; then
    echo "❌ ERROR: BuildConfig import not found"
    exit 1
fi

# 3. Проверка захардкоженных версий
echo "🔢 Checking for hardcoded versions..."
if grep -q "1\.[0-9][0-9]" app/src/main/**/*.{kt,xml}; then
    echo "❌ ERROR: Found hardcoded version"
    grep -n "1\.[0-9][0-9]" app/src/main/**/*.{kt,xml}
    exit 1
fi

echo "✅ Pre-build validation passed!"
```

## 📊 Результат проверки

| Проверка | Статус | Комментарий |
|----------|--------|-------------|
| APK файлы | ✅/❌ | Правильные имена файлов |
| BuildConfig | ✅/❌ | Импорты и использование |
| Версии | ✅/❌ | Нет хардкода |
| Workflow | ✅/❌ | Консистентность |

## 🎯 Выводы

**Что я должен был сделать:**
1. ✅ Проверить ВСЕ ссылки на APK в workflow
2. ✅ Знать стандартные имена файлов Android
3. ✅ Создать автоматический чек-лист
4. ✅ Запустить проверку перед коммитом

**Результат:** Ошибка была бы найдена до сборки! 🎯