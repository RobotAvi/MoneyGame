# Задача 2: Добавление Dependency Injection

## 📋 Описание задачи

Внедрить Dependency Injection (DI) в приложение для улучшения тестируемости, снижения связанности компонентов и упрощения управления зависимостями.

## 🎯 Цели

- Устранить прямое создание зависимостей в коде
- Улучшить тестируемость компонентов
- Снизить связанность между классами
- Упростить управление жизненным циклом объектов
- Обеспечить единую точку управления зависимостями

## 📁 Затронутые файлы

### Основные файлы:
- `app/build.gradle`
- `app/src/main/java/com/financialsuccess/game/GameActivity.kt`
- `app/src/main/java/com/financialsuccess/game/GameManager.kt`
- `app/src/main/java/com/financialsuccess/game/CharacterCreationActivity.kt`
- `app/src/main/java/com/financialsuccess/game/ProfessionSelectionActivity.kt`

### Новые файлы для создания:
- `app/src/main/java/com/financialsuccess/game/di/GameModule.kt`
- `app/src/main/java/com/financialsuccess/game/di/AppModule.kt`
- `app/src/main/java/com/financialsuccess/game/di/ViewModelModule.kt`
- `app/src/main/java/com/financialsuccess/game/FinancialSuccessApplication.kt`

### Тестовые файлы:
- `app/src/test/GameModuleTest.kt`
- `app/src/test/AppModuleTest.kt`

## 🔍 Анализ связанных файлов

### Обязательный анализ:
1. **build.gradle** - проверка текущих зависимостей
2. **GameActivity.kt** - анализ всех зависимостей
3. **GameManager.kt** - проверка зависимостей
4. **CharacterCreationActivity.kt** - анализ зависимостей
5. **ProfessionSelectionActivity.kt** - анализ зависимостей
6. **GameDataManager.kt** - проверка статических методов
7. **AndroidManifest.xml** - проверка Application класса

### Чек-лист анализа:
- [ ] Все прямые создания объектов (new, конструкторы)
- [ ] Все статические методы и объекты
- [ ] Все синглтоны
- [ ] Все зависимости в конструкторах
- [ ] Все фабричные методы
- [ ] Все утилитарные классы
- [ ] Все менеджеры и сервисы

## 🏗️ Архитектурные изменения

### 1. Добавление Hilt в build.gradle
```gradle
plugins {
    id 'com.android.application'
    id 'org.jetbrains.kotlin.android'
    id 'kotlin-parcelize'
    id 'kotlin-kapt'
    id 'dagger.hilt.android.plugin'
}

dependencies {
    implementation "com.google.dagger:hilt-android:2.48"
    kapt "com.google.dagger:hilt-compiler:2.48"
    implementation "androidx.hilt:hilt-navigation-compose:1.1.0"
}
```

### 2. Создание Application класса
```kotlin
@HiltAndroidApp
class FinancialSuccessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Инициализация приложения
    }
}
```

### 3. Создание модулей DI
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideGameManager(): GameManager {
        return GameManager()
    }
    
    @Provides
    @Singleton
    fun provideGameDataManager(): GameDataManager {
        return GameDataManager
    }
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
}

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    
    @Provides
    fun provideGameRepository(
        gameDataManager: GameDataManager,
        gson: Gson
    ): GameRepository {
        return GameRepositoryImpl(gameDataManager, gson)
    }
}
```

### 4. Обновление Activity с DI
```kotlin
@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    
    @Inject
    lateinit var gameManager: GameManager
    
    @Inject
    lateinit var gameRepository: GameRepository
    
    private val viewModel: GameViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Использование внедренных зависимостей
    }
}
```

### 5. Обновление GameManager
```kotlin
class GameManager @Inject constructor(
    private val gameRepository: GameRepository
) {
    // Использование внедренной зависимости
    fun getProfessions(): List<Profession> {
        return gameRepository.getProfessions()
    }
}
```

## ✅ Критерии приемки

### Функциональные требования:
- [ ] Приложение запускается без ошибок
- [ ] Все существующие функции работают корректно
- [ ] Игра инициализируется правильно
- [ ] Все Activity работают с внедренными зависимостями
- [ ] Сохранение/загрузка игры функционирует
- [ ] Все диалоги и всплывающие окна работают

### Архитектурные требования:
- [ ] Все зависимости внедряются через DI
- [ ] Нет прямых созданий объектов в коде
- [ ] Все синглтоны управляются через DI
- [ ] Модули DI правильно структурированы
- [ ] Зависимости имеют правильные scope
- [ ] Нет циклических зависимостей

### Тестируемость:
- [ ] Все компоненты можно легко мокать в тестах
- [ ] Unit тесты работают с внедренными зависимостями
- [ ] Интеграционные тесты используют DI
- [ ] Тесты покрывают все публичные методы

### Производительность:
- [ ] Нет утечек памяти из-за DI
- [ ] Объекты создаются в правильном scope
- [ ] Синглтоны работают корректно
- [ ] Время инициализации приложения не увеличилось значительно

## 🚨 Правила исполнения

### ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ:

#### 1. Полный анализ всех связанных файлов
- [ ] Проанализировать все прямые создания объектов
- [ ] Изучить все статические методы и объекты
- [ ] Проверить все синглтоны
- [ ] Проанализировать все зависимости в конструкторах
- [ ] Изучить все фабричные методы

#### 2. Создать чек-лист проверок
- [ ] Синтаксис всех новых файлов DI
- [ ] Корректность аннотаций Hilt
- [ ] Правильность scope зависимостей
- [ ] Отсутствие циклических зависимостей
- [ ] Проверка правильности внедрения

#### 3. Предварительная проверка
- [ ] Синтаксис всех затрагиваемых файлов
- [ ] Структура и отступы
- [ ] Контекст изменений
- [ ] Возможные побочные эффекты

#### 4. Проверка критических областей
- [ ] Application lifecycle
- [ ] Activity lifecycle с DI
- [ ] ViewModel lifecycle с DI
- [ ] Правильность scope объектов
- [ ] Инициализация Hilt

#### 5. Знание стандартов платформы
- [ ] Hilt best practices
- [ ] Dependency Injection patterns
- [ ] Android DI guidelines
- [ ] Scope management
- [ ] Testing with DI

### ПРЕДПОЛОЖЕНИЯ И ЗАВИСИМОСТИ:
- [ ] Android Gradle Plugin поддерживает Hilt
- [ ] Kotlin KAPT доступен
- [ ] Все необходимые Hilt зависимости доступны
- [ ] Application класс может быть изменен
- [ ] Все Activity могут быть обновлены

### ПРЕ-КОММИТ ПРОВЕРКИ:
- [ ] Автоматическая валидация синтаксиса Kotlin
- [ ] Проверка форматирования кода
- [ ] Линтинг всех измененных файлов
- [ ] Проверка на типичные ошибки Hilt
- [ ] Валидация DI графа

### ОТКАЗОУСТОЙЧИВОСТЬ:
- [ ] Обработка ошибок инициализации DI
- [ ] Восстановление после сбоев DI
- [ ] Корректная очистка ресурсов
- [ ] Обработка отсутствующих зависимостей

## 📊 Отчет о выполнении

Перед коммитом предоставить отчет:
- [ ] Все проверки выполнены
- [ ] Автоматизированные проверки пройдены
- [ ] Синтаксис проверен автоматически
- [ ] Форматирование соответствует стандартам
- [ ] Проведена визуальная проверка
- [ ] Выполнены все пре-коммит проверки
- [ ] Ошибок не найдено
- [ ] Все предположения проверены
- [ ] Все зависимости удовлетворены
- [ ] Отказоустойчивость обеспечена
- [ ] Проверена обработка ошибок
- [ ] Проверено восстановление после сбоев
- [ ] Готов к коммиту

## ⏱️ Оценка времени

- **Анализ зависимостей**: 1 день
- **Настройка Hilt**: 1 день
- **Создание модулей DI**: 1 день
- **Обновление существующих классов**: 2 дня
- **Тестирование**: 1 день

**Общее время**: 6 дней