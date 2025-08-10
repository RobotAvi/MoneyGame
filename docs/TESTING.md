# Документация по тестированию

## Обзор тестирования

Проект "Финансовый успех" использует комплексный подход к тестированию, включающий unit тесты, интеграционные тесты и UI тесты. Цель - обеспечить высокое качество кода и стабильность приложения.

## 🧪 Типы тестов

### 1. Unit тесты
**Расположение**: `app/src/test/`

**Цель**: Тестирование отдельных компонентов и бизнес-логики

**Технологии**: JUnit 4, Robolectric, Mockito

**Покрытие**: автоматическая генерация отчётов о покрытии не настроена (Jacoco не подключён)

#### Основные тестируемые компоненты:

##### Player.kt
```kotlin
class PlayerTest {
    @Test fun `test education bonus calculation`()
    @Test fun `test skills bonus calculation`()
    @Test fun `test salary with bonuses calculation`()
    @Test fun `test family expenses calculation`()
    @Test fun `test savings calculation`()
    @Test fun `test financial goals checking`()
    @Test fun `test life statistics`()
    @Test fun `test health status calculation`()
    @Test fun `test total expenses with family and health modifiers`()
    @Test fun `test total income with spouse`()
    @Test fun `test age and life progression`()
}
```

##### GameManager.kt
```kotlin
class GameManagerTest {
    @Test fun `test start new game`()
    @Test fun `test dice roll range`()
    @Test fun `test player movement and month progression`()
    @Test fun `test salary payment on month start`()
    @Test fun `test asset purchase and sale`()
    @Test fun `test liability payoff`()
}
```

##### GameActivity / UI фрагменты логики (через Robolectric)
```kotlin
@RunWith(RobolectricTestRunner::class)
class GameActivityTest {
    @Test fun testPlayerDeathTriggersDialog() { /* ... */ }
    @Test fun testShowFinancialJournalEmpty() { /* ... */ }
}
```

### 2. Инструментальные тесты
**Расположение**: `app/src/androidTest/`

**Цель**: Тестирование взаимодействия между компонентами и UI

**Технологии**: AndroidX Test, Espresso

#### Основные сценарии
- Валидация полей создания персонажа
- Наличие и поведение элементов UI

### 3. UI тесты (планируется расширение)
**Расположение**: `app/src/androidTest/`

**Цель**: Тестирование пользовательского интерфейса для основных сценариев

## 🚀 Запуск тестов

### Unit тесты
```bash
./gradlew test
```

### Инструментальные тесты
```bash
./gradlew connectedAndroidTest
```

### Все тесты
```bash
./gradlew check
```

## 📦 Зависимости для тестов
В проекте используются Robolectric и Mockito. Если локальная сборка сообщает об отсутствии зависимостей, добавьте в `app/build.gradle`:
```gradle
testImplementation 'org.robolectric:robolectric:4.10.3'
testImplementation 'org.mockito:mockito-core:5.12.0'
```

## 📊 Отчёты о покрытии (не настроено)
Jacoco в проект не подключён. Для включения покрытия добавьте Jacoco-плагин и соответствующие задачи в Gradle и пересмотрите этот раздел.