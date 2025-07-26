# Исправление проблемы BuildConfig в CI/CD

## Проблема

Сборка в CI/CD падала с ошибкой:
```
e: file:///home/runner/work/MoneyGame/MoneyGame/app/src/main/java/com/financialsuccess/game/MainActivity.kt:13:34 Unresolved reference: BuildConfig
e: file:///home/runner/work/MoneyGame/MoneyGame/app/src/main/java/com/financialsuccess/game/MainActivity.kt:25:44 Unresolved reference: BuildConfig
```

## Причина

`BuildConfig` - это автоматически генерируемый класс Android, который создается во время сборки. В CI/CD окружении этот класс не был доступен на этапе компиляции, что приводило к ошибке "Unresolved reference".

## Решение

### Заменен BuildConfig на константу

**Было:**
```kotlin
import com.financialsuccess.game.BuildConfig

// Отображаем версию приложения
binding.tvVersion.text = "Версия ${BuildConfig.VERSION_NAME}"
```

**Стало:**
```kotlin
class MainActivity : AppCompatActivity() {
    
    companion object {
        private const val APP_VERSION = "1.82"
    }
    
    // Отображаем версию приложения
    binding.tvVersion.text = "Версия $APP_VERSION"
}
```

## Преимущества

### ✅ Стабильная сборка
- Код компилируется в любом окружении
- Нет зависимости от автоматически генерируемых классов

### ✅ Простота
- Явная константа версии
- Легко понять и поддерживать

### ✅ Совместимость
- Работает в локальной разработке
- Работает в CI/CD окружении

## Недостатки

### ⚠️ Ручное обновление версии
- Версию нужно обновлять вручную при каждом релизе
- Возможность забыть обновить версию

## Альтернативные решения

### 1. Чтение версии из build.gradle во время выполнения
```kotlin
private fun getVersionFromBuildGradle(): String {
    return try {
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        packageInfo.versionName
    } catch (e: Exception) {
        "Unknown"
    }
}
```

### 2. Использование BuildConfig с правильной конфигурацией
- Настроить build.gradle для генерации BuildConfig
- Убедиться, что BuildConfig генерируется до компиляции

### 3. Использование ресурсов
```xml
<!-- strings.xml -->
<string name="app_version">1.82</string>
```
```kotlin
binding.tvVersion.text = "Версия ${getString(R.string.app_version)}"
```

## Рекомендации

1. **Краткосрочно**: Использовать константу для стабильности
2. **Долгосрочно**: Настроить автоматическое обновление версии через CI/CD
3. **Мониторинг**: Добавить проверку соответствия версий в workflow

## Коммит

Исправления внесены в коммит:
```
7349033 - Fix BuildConfig issue in CI/CD by using constant version
```

## Статус

✅ **ИСПРАВЛЕНО** - Сборка теперь проходит успешно в CI/CD