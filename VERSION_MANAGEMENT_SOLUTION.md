# Правильное решение управления версиями с номером билда

## 🎯 Цель

Создать автоматическую систему управления версиями, которая:
- ✅ Автоматически обновляет версию при каждом релизе
- ✅ Показывает номер билда в главном окне приложения
- ✅ Работает как в локальной разработке, так и в CI/CD
- ✅ Исключает ручное обновление версий

## 🔧 Реализация

### 1. Настройка BuildConfig в build.gradle

```gradle
android {
    buildFeatures {
        viewBinding true
        buildConfig true  // Включаем генерацию BuildConfig
    }
    
    defaultConfig {
        versionCode 83
        versionName "1.82"
        
        // Добавляем номер билда и дату в BuildConfig
        buildConfigField "String", "BUILD_NUMBER", "\"${System.getenv('GITHUB_RUN_NUMBER') ?: 'local'}\""
        buildConfigField "String", "BUILD_DATE", "\"${new Date().format('yyyy-MM-dd HH:mm')}\""
    }
}
```

### 2. Отображение версии в MainActivity

```kotlin
import com.financialsuccess.game.BuildConfig

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Отображаем версию с номером билда
        val versionText = if (BuildConfig.BUILD_NUMBER != "local") {
            "Версия ${BuildConfig.VERSION_NAME} (build ${BuildConfig.BUILD_NUMBER})"
        } else {
            "Версия ${BuildConfig.VERSION_NAME} (local build)"
        }
        binding.tvVersion.text = versionText
    }
}
```

### 3. Автоматическое обновление версии в workflow

В `.github/workflows/stable-build.yml` уже настроено автоматическое увеличение версии:

```yaml
- name: Bump version in build.gradle
  run: |
    VERSION_LINE=$(grep -n 'versionName' app/build.gradle | cut -d: -f1)
    CODE_LINE=$(grep -n 'versionCode' app/build.gradle | cut -d: -f1)
    CURR_VERSION=$(grep -oP 'versionName\s+"\K[^"]+' app/build.gradle)
    CURR_CODE=$(grep -oP 'versionCode\s+\K[0-9]+' app/build.gradle)
    NEW_CODE=$((CURR_CODE+1))
    # Увеличиваем минорную версию
    MAJOR=$(echo $CURR_VERSION | cut -d. -f1)
    MINOR=$(echo $CURR_VERSION | cut -d. -f2)
    NEW_MINOR=$((MINOR+1))
    NEW_VERSION="$MAJOR.$NEW_MINOR"
    sed -i "${CODE_LINE}s/versionCode.*/versionCode $NEW_CODE/" app/build.gradle
    sed -i "${VERSION_LINE}s/versionName.*/versionName \"$NEW_VERSION\"/" app/build.gradle
```

## 📱 Отображение версии

### В CI/CD (GitHub Actions):
```
Версия 1.82 (build 123)
```

### В локальной разработке:
```
Версия 1.82 (local build)
```

## 🔄 Автоматический процесс

1. **Push в main** → Запускается workflow
2. **Сборка APK** → Генерируется BuildConfig с номером билда
3. **Увеличение версии** → versionCode +1, versionName +0.1
4. **Коммит изменений** → Автоматический коммит с новой версией
5. **Следующий релиз** → Новая версия уже готова

## ✅ Преимущества

### Автоматизация
- Версия обновляется автоматически при каждом релизе
- Номер билда берется из GitHub Actions
- Нет необходимости вручную обновлять версии

### Информативность
- Пользователи видят точную версию и номер билда
- Разработчики могут легко отследить, какая сборка используется
- Локальные сборки отличаются от CI/CD сборок

### Надежность
- BuildConfig генерируется правильно в CI/CD
- Нет ошибок компиляции
- Совместимость с Android Gradle Plugin

## 📊 Примеры версий

| Сборка | Отображение | Описание |
|--------|-------------|----------|
| CI/CD #123 | `Версия 1.82 (build 123)` | Официальная сборка |
| CI/CD #124 | `Версия 1.83 (build 124)` | Следующий релиз |
| Локальная | `Версия 1.82 (local build)` | Разработка |

## 🔍 Проверка всех мест использования версии

Проверены все файлы:
- ✅ `app/build.gradle` - основная версия
- ✅ `MainActivity.kt` - отображение версии
- ✅ `activity_main.xml` - TextView для версии
- ✅ `.github/workflows/stable-build.yml` - автоматическое обновление
- ✅ Нет захардкоженных версий в других файлах

## 🚀 Статус

✅ **РЕАЛИЗОВАНО** - Полная автоматизация управления версиями с номером билда