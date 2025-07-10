# Руководство по контрибьюции

Спасибо за интерес к проекту "Финансовый успех"! Мы приветствуем вклад от всех участников сообщества. Это руководство поможет вам начать работу с проектом.

## 🚀 Быстрый старт

### Предварительные требования
- **Android Studio** Arctic Fox или новее
- **Android SDK** 34
- **Kotlin** 1.9.10
- **Gradle** 8.1.2
- **Git** для управления версиями

### Настройка окружения
1. **Форкните репозиторий**
   ```bash
   # Перейдите на GitHub и нажмите "Fork"
   # Затем клонируйте ваш форк
   git clone https://github.com/YOUR_USERNAME/financial-success-game.git
   cd financial-success-game
   ```

2. **Настройте upstream**
   ```bash
   git remote add upstream https://github.com/original-owner/financial-success-game.git
   ```

3. **Откройте проект в Android Studio**
   - Запустите Android Studio
   - Выберите "Open an existing project"
   - Укажите путь к клонированному проекту
   - Дождитесь синхронизации Gradle

4. **Запустите тесты**
   ```bash
   ./gradlew test
   ./gradlew connectedAndroidTest
   ```

## 📋 Процесс контрибьюции

### 1. Выбор задачи
- Просмотрите [Issues](../../issues) для поиска задач
- Или создайте новое Issue с описанием вашей идеи
- Обсудите подход с командой перед началом работы

### 2. Создание ветки
```bash
# Обновите main ветку
git checkout main
git pull upstream main

# Создайте новую ветку для вашей задачи
git checkout -b feature/your-feature-name
# или
git checkout -b fix/your-bug-fix
```

### 3. Разработка
- Следуйте [стандартам кодирования](#стандарты-кодирования)
- Пишите тесты для новой функциональности
- Обновляйте документацию при необходимости

### 4. Коммиты
```bash
# Добавьте изменения
git add .

# Создайте коммит с описательным сообщением
git commit -m "feat: add new character creation system

- Add extended player model with 20+ new fields
- Implement skill system with 10 skills in 5 categories
- Add education system with salary multipliers
- Create CharacterCreationActivity with Material Design 3
- Add comprehensive validation and error handling

Closes #123"
```

### 5. Push и Pull Request
```bash
# Отправьте ветку в ваш форк
git push origin feature/your-feature-name

# Создайте Pull Request на GitHub
```

## 📝 Стандарты кодирования

### Kotlin стиль
- Следуйте [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Используйте **4 пробела** для отступов
- Максимальная длина строки: **120 символов**

### Именование
```kotlin
// Классы и объекты - PascalCase
class PlayerManager
object GameDataManager

// Функции и переменные - camelCase
fun calculateSalary()
var playerName: String

// Константы - UPPER_SNAKE_CASE
const val MAX_SKILLS = 3
const val DEFAULT_SAVINGS_RATE = 10

// Enum значения - UPPER_SNAKE_CASE
enum class EducationLevel {
    HIGH_SCHOOL,
    BACHELOR,
    MASTER,
    PHD
}
```

### Структура файлов
```kotlin
// 1. Package declaration
package com.financialsuccess.game.models

// 2. Imports (стандартные библиотеки, затем сторонние)
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// 3. Class/object declaration
@Parcelize
data class Player(
    var name: String? = null,
    var age: Int = 25,
    // ... другие поля
) : Parcelable {
    
    // 4. Companion object (если есть)
    companion object {
        const val DEFAULT_AGE = 25
        const val MIN_AGE = 18
        const val MAX_AGE = 65
    }
    
    // 5. Public functions
    fun calculateSalary(): Int {
        // реализация
    }
    
    // 6. Private functions
    private fun calculateEducationBonus(): Int {
        // реализация
    }
}
```

### Комментарии
```kotlin
/**
 * Рассчитывает зарплату игрока с учетом всех бонусов.
 * 
 * @return Общая зарплата с бонусами за образование, навыки и опыт
 */
fun calculateSalary(): Int {
    // Базовая зарплата из профессии
    val baseSalary = profession?.salary ?: 0
    
    // Добавляем бонусы
    val educationBonus = calculateEducationBonus()
    val skillsBonus = calculateSkillsBonus()
    val experienceBonus = workExperience * 1000
    
    return baseSalary + educationBonus + skillsBonus + experienceBonus
}
```

## 🧪 Тестирование

### Unit тесты
- Пишите тесты для всей бизнес-логики
- Используйте описательные названия тестов
- Следуйте паттерну AAA (Arrange, Act, Assert)

```kotlin
@Test
fun `test education bonus calculation for bachelor degree`() {
    // Arrange
    val player = Player(
        education = EducationLevel.BACHELOR,
        workExperience = 2
    )
    
    // Act
    val bonus = player.calculateEducationBonus()
    
    // Assert
    assertEquals(14000, bonus) // 10000 + 2*2000
}
```

### Интеграционные тесты
- Тестируйте взаимодействие между компонентами
- Используйте Espresso для UI тестов
- Проверяйте реальные пользовательские сценарии

```kotlin
@Test
fun testCharacterCreationValidation() {
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

### Покрытие тестами
- Поддерживайте покрытие не менее 80%
- Новый код должен быть покрыт тестами на 90%+
- Запускайте тесты перед каждым коммитом

## 📚 Документация

### Обновление документации
- Обновляйте README.md при добавлении новых функций
- Документируйте API изменения
- Добавляйте примеры использования

### Комментарии в коде
- Документируйте сложную бизнес-логику
- Объясняйте неочевидные решения
- Используйте KDoc для публичных API

## 🔍 Code Review

### Требования к PR
- [ ] Код следует стандартам проекта
- [ ] Все тесты проходят
- [ ] Покрытие тестами не уменьшилось
- [ ] Документация обновлена
- [ ] Код прошел линтер

### Процесс ревью
1. **Создайте Pull Request** с описанием изменений
2. **Дождитесь автоматических проверок** (CI/CD)
3. **Исправьте замечания** от линтера и тестов
4. **Получите одобрение** от команды
5. **Дождитесь мержа** в main ветку

## 🐛 Сообщение о багах

### Создание Issue
При создании Issue для бага, пожалуйста, включите:

1. **Краткое описание** проблемы
2. **Шаги для воспроизведения**
3. **Ожидаемое поведение**
4. **Фактическое поведение**
5. **Информация об окружении**:
   - Версия Android
   - Версия приложения
   - Модель устройства
6. **Скриншоты** (если применимо)
7. **Логи** (если доступны)

### Пример Issue
```
**Описание бага**
При создании персонажа с возрастом 15 лет не появляется ошибка валидации.

**Шаги для воспроизведения**
1. Откройте приложение
2. Нажмите "Создать персонажа"
3. Введите возраст 15
4. Нажмите "Начать игру"

**Ожидаемое поведение**
Должна появиться ошибка "Возраст должен быть от 18 до 65 лет"

**Фактическое поведение**
Игра начинается без ошибок

**Окружение**
- Android: 11
- Версия приложения: 2.0.0
- Устройство: Samsung Galaxy S21
```

## 💡 Предложения улучшений

### Создание Feature Request
При предложении новой функции:

1. **Опишите проблему** или потребность
2. **Предложите решение** с деталями
3. **Объясните преимущества** для пользователей
4. **Предложите альтернативы** (если есть)
5. **Добавьте примеры** использования

### Пример Feature Request
```
**Описание**
Добавить систему достижений для мотивации игроков.

**Решение**
Создать систему достижений с наградами:
- "Первая инвестиция" - за покупку первого актива
- "Миллионер" - за накопление 1,000,000₽
- "Финансовая независимость" - за выход из крысиных бегов

**Преимущества**
- Повышает вовлеченность игроков
- Создает чувство прогресса
- Мотивирует к изучению финансовой грамотности

**Примеры**
- Уведомления о достижениях
- Экран статистики достижений
- Награды в виде бонусов к игре
```

## 🏗️ Архитектурные принципы

### SOLID принципы
- **Single Responsibility**: Каждый класс имеет одну ответственность
- **Open/Closed**: Код открыт для расширения, закрыт для модификации
- **Liskov Substitution**: Подклассы могут заменять базовые классы
- **Interface Segregation**: Клиенты не зависят от неиспользуемых интерфейсов
- **Dependency Inversion**: Зависимости от абстракций, а не от конкретных классов

### Clean Architecture
- **Presentation Layer**: UI и пользовательский ввод
- **Domain Layer**: Бизнес-логика и правила
- **Data Layer**: Управление данными и доступ к ресурсам

### Паттерны проектирования
- **MVVM**: Для управления состоянием UI
- **Repository**: Для доступа к данным
- **Factory**: Для создания объектов
- **Observer**: Для реактивного программирования

## 🚀 Запуск проекта

### Локальная разработка
```bash
# Клонирование
git clone https://github.com/YOUR_USERNAME/financial-success-game.git
cd financial-success-game

# Сборка
./gradlew assembleDebug

# Запуск тестов
./gradlew test
./gradlew connectedAndroidTest

# Запуск приложения
./gradlew installDebug
```

### Отладка
```bash
# Очистка проекта
./gradlew clean

# Пересборка
./gradlew assembleDebug

# Запуск с отладкой
./gradlew assembleDebug --debug
```

## 📞 Поддержка

### Получение помощи
- **GitHub Issues**: Для багов и предложений
- **Discussions**: Для общих вопросов
- **Email**: support@financial-success-game.com

### Сообщество
- Присоединяйтесь к нашему Discord серверу
- Участвуйте в обсуждениях на GitHub
- Делитесь опытом с другими разработчиками

## 🙏 Благодарности

Спасибо всем участникам сообщества за:
- Тестирование и отзывы
- Предложения по улучшению
- Сообщения о багах
- Технические советы
- Документацию и примеры

---

**Версия руководства**: 2.0.0  
**Последнее обновление**: Декабрь 2024  
**Автор**: Команда разработки Financial Success Game