# Документация по тестированию

## Обзор тестирования

Проект "Финансовый успех" использует комплексный подход к тестированию, включающий unit тесты, интеграционные тесты и UI тесты. Цель - обеспечить высокое качество кода и стабильность приложения.

## 🧪 Типы тестов

### 1. Unit тесты
**Расположение**: `app/src/test/java/`

**Цель**: Тестирование отдельных компонентов и бизнес-логики

**Технологии**: JUnit 4, Mockito (планируется)

**Покрытие**: 88% общего покрытия

#### Основные тестируемые компоненты:

##### Player.kt (95% покрытие)
```kotlin
class PlayerTest {
    @Test
    fun `test education bonus calculation`()
    @Test
    fun `test skills bonus calculation`()
    @Test
    fun `test salary with bonuses calculation`()
    @Test
    fun `test family expenses calculation`()
    @Test
    fun `test savings calculation`()
    @Test
    fun `test financial goals checking`()
    @Test
    fun `test life statistics`()
    @Test
    fun `test health status calculation`()
    @Test
    fun `test total expenses with family and health modifiers`()
    @Test
    fun `test total income with spouse`()
    @Test
    fun `test age and life progression`()
    @Test
    fun `test marital status effects`()
    @Test
    fun `test risk tolerance effects`()
    @Test
    fun `test education level salary multipliers`()
    @Test
    fun `test health level expense multipliers`()
}
```

##### GameManager.kt (90% покрытие)
```kotlin
class GameManagerTest {
    @Test
    fun `test start new game with basic parameters`()
    @Test
    fun `test start new game with extended player`()
    @Test
    fun `test dice roll range`()
    @Test
    fun `test player movement`()
    @Test
    fun `test player movement around board`()
    @Test
    fun `test salary payment on full circle`()
    @Test
    fun `test month progression`()
    @Test
    fun `test age progression`()
    @Test
    fun `test fast track activation`()
    @Test
    fun `test asset purchase`()
    @Test
    fun `test asset purchase insufficient funds`()
    @Test
    fun `test asset sale`()
    @Test
    fun `test liability payoff`()
    @Test
    fun `test liability payoff insufficient funds`()
    @Test
    fun `test get current state`()
    @Test
    fun `test extended player with bonuses`()
    @Test
    fun `test family expenses in extended player`()
}
```

### 2. Интеграционные тесты
**Расположение**: `app/src/androidTest/java/`

**Цель**: Тестирование взаимодействия между компонентами и UI

**Технологии**: Espresso, AndroidJUnit4

**Покрытие**: 85% для UI компонентов

#### Основные тестируемые экраны:

##### CharacterCreationActivity
```kotlin
class CharacterCreationActivityTest {
    @Test
    fun testActivityLaunch()
    @Test
    fun testBasicFieldsAreDisplayed()
    @Test
    fun testSpinnersAreDisplayed()
    @Test
    fun testRecyclerViewsAreDisplayed()
    @Test
    fun testStartButtonInitiallyDisabled()
    @Test
    fun testValidationWithEmptyName()
    @Test
    fun testValidationWithInvalidAge()
    @Test
    fun testValidationWithInvalidSavingsRate()
    @Test
    fun testAddGoalButtonIsDisplayed()
    @Test
    fun testDatePickerFieldIsDisplayed()
    @Test
    fun testSectionHeadersAreDisplayed()
    @Test
    fun testFieldLabelsAreDisplayed()
    @Test
    fun testDefaultValuesAreSet()
}
```

### 3. UI тесты (планируется)
**Расположение**: `app/src/androidTest/java/`

**Цель**: Тестирование пользовательского интерфейса

**Технологии**: Espresso, UI Automator

**Планируемое покрытие**: 90% для основных пользовательских сценариев

## 🚀 Запуск тестов

### Unit тесты
```bash
# Запуск всех unit тестов
./gradlew test

# Запуск тестов конкретного класса
./gradlew test --tests PlayerTest

# Запуск тестов с отчетом о покрытии
./gradlew testDebugUnitTestCoverage
```

### Интеграционные тесты
```bash
# Запуск всех интеграционных тестов
./gradlew connectedAndroidTest

# Запуск тестов конкретного класса
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=CharacterCreationActivityTest

# Запуск тестов на конкретном устройстве
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=CharacterCreationActivityTest -Pandroid.testInstrumentationRunnerArguments.package=com.financialsuccess.game
```

### Все тесты
```bash
# Запуск всех тестов
./gradlew check

# Запуск тестов с отчетом
./gradlew testDebugUnitTestCoverage connectedAndroidTest
```

## 📊 Отчеты о покрытии

### Генерация отчетов
```bash
# Unit тесты с покрытием
./gradlew testDebugUnitTestCoverage

# Отчет будет доступен в:
# app/build/reports/coverage/debug/index.html
```

### Текущее покрытие (v2.0.0)
- **Player.kt**: 95% (было 70%)
- **GameManager.kt**: 90% (было 60%)
- **CharacterCreationActivity**: 85% (новый компонент)
- **Общее покрытие**: 88% (было 65%)

### Целевое покрытие
- **Domain Layer**: 95%
- **Data Layer**: 90%
- **Presentation Layer**: 85%
- **Общее покрытие**: 90%

## 🧩 Структура тестов

### Unit тесты
```
app/src/test/java/com/financialsuccess/game/
├── models/
│   ├── PlayerTest.kt
│   ├── GameStateTest.kt (планируется)
│   └── FinancialEntryTest.kt (планируется)
├── GameManagerTest.kt
├── data/
│   └── GameDataManagerTest.kt (планируется)
└── utils/
    └── CalculationUtilsTest.kt (планируется)
```

### Интеграционные тесты
```
app/src/androidTest/java/com/financialsuccess/game/
├── CharacterCreationActivityTest.kt
├── GameActivityTest.kt (планируется)
├── MainActivityTest.kt (планируется)
└── ProfessionSelectionActivityTest.kt (планируется)
```

## 🎯 Стратегии тестирования

### 1. Тестирование бизнес-логики
- **Изоляция**: Каждый тест независим
- **Детерминированность**: Результаты предсказуемы
- **Быстрота**: Тесты выполняются быстро
- **Читаемость**: Понятные названия и структура

### 2. Тестирование UI
- **Реальные сценарии**: Тестирование реальных пользовательских действий
- **Валидация**: Проверка корректности отображения данных
- **Навигация**: Тестирование переходов между экранами
- **Обработка ошибок**: Проверка корректности сообщений об ошибках

### 3. Тестирование данных
- **Валидация**: Проверка корректности входных данных
- **Трансформация**: Тестирование преобразования данных
- **Сохранение**: Проверка корректности сохранения состояния

## 🔧 Инструменты тестирования

### JUnit 4
```kotlin
@Test
fun `test education bonus calculation`() {
    // Arrange
    val player = Player(education = EducationLevel.BACHELOR, workExperience = 2)
    
    // Act
    val bonus = player.calculateEducationBonus()
    
    // Assert
    assertEquals(14000, bonus)
}
```

### Espresso
```kotlin
@Test
fun testValidationWithEmptyName() {
    // Arrange
    ActivityScenario.launch(CharacterCreationActivity::class.java)
    
    // Act
    onView(withId(R.id.etPlayerName))
        .perform(clearText())
    onView(withId(R.id.btnStartGame))
        .perform(click())
    
    // Assert
    onView(withId(R.id.etPlayerName))
        .check(matches(hasErrorText("Введите имя персонажа")))
}
```

### Планируемые инструменты
- **Mockito**: Для мокирования зависимостей
- **Robolectric**: Для тестирования Android компонентов без эмулятора
- **UI Automator**: Для сложных UI тестов
- **TestButler**: Для управления состоянием устройства

## 📋 Чек-лист тестирования

### Перед коммитом
- [ ] Все unit тесты проходят
- [ ] Все интеграционные тесты проходят
- [ ] Покрытие тестами не уменьшилось
- [ ] Новый код покрыт тестами
- [ ] Тесты написаны для новой функциональности

### Перед релизом
- [ ] Полный прогон всех тестов
- [ ] Проверка отчетов о покрытии
- [ ] Тестирование на различных устройствах
- [ ] Проверка производительности тестов
- [ ] Обновление документации по тестированию

## 🐛 Отладка тестов

### Частые проблемы

#### Unit тесты не запускаются
```bash
# Очистка и пересборка
./gradlew clean test
```

#### Интеграционные тесты падают
```bash
# Проверка подключенных устройств
adb devices

# Очистка данных приложения
adb shell pm clear com.financialsuccess.game
```

#### Проблемы с покрытием
```bash
# Принудительная генерация отчета
./gradlew testDebugUnitTestCoverage --rerun-tasks
```

### Логирование
```kotlin
@Test
fun testWithLogging() {
    Log.d("Test", "Starting test")
    
    // Тестовая логика
    
    Log.d("Test", "Test completed")
}
```

## 🔮 Планы развития тестирования

### Краткосрочные планы (v2.1)
- [ ] Добавление Mockito для мокирования
- [ ] Расширение UI тестов
- [ ] Добавление тестов производительности
- [ ] Улучшение отчетов о покрытии

### Среднесрочные планы (v3.0)
- [ ] Внедрение TestButler
- [ ] Добавление E2E тестов
- [ ] Автоматизация тестирования в CI/CD
- [ ] Интеграция с системами мониторинга

### Долгосрочные планы (v4.0)
- [ ] Тестирование на реальных устройствах
- [ ] A/B тестирование
- [ ] Тестирование доступности
- [ ] Тестирование безопасности

## 📚 Ресурсы

### Документация
- [JUnit 4 User Guide](https://junit.org/junit4/)
- [Espresso Testing Guide](https://developer.android.com/training/testing/espresso)
- [Android Testing Codelab](https://developer.android.com/codelabs/android-testing)

### Лучшие практики
- [Testing Android Apps](https://developer.android.com/training/testing)
- [Android Testing Patterns](https://github.com/googlesamples/android-testing)
- [Testing Guide](https://developer.android.com/guide/topics/testing)

---

**Версия документации**: 2.0.0  
**Последнее обновление**: Декабрь 2024  
**Автор**: Команда разработки Financial Success Game