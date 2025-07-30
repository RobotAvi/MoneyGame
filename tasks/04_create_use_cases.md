# Задача 4: Создание Use Cases

## 📋 Описание задачи

Внедрить Use Cases (сценарии использования) для инкапсуляции бизнес-логики, улучшения тестируемости и обеспечения чистого разделения между слоями приложения.

## 🎯 Цели

- Инкапсулировать бизнес-логику в отдельные Use Cases
- Улучшить тестируемость бизнес-логики
- Обеспечить переиспользование бизнес-логики
- Создать четкое разделение между слоями
- Упростить понимание и поддержку кода

## 📁 Затронутые файлы

### Основные файлы:
- `app/src/main/java/com/financialsuccess/game/GameManager.kt`
- `app/src/main/java/com/financialsuccess/game/GameActivity.kt`
- `app/src/main/java/com/financialsuccess/game/CharacterCreationActivity.kt`
- `app/src/main/java/com/financialsuccess/game/models/Player.kt`

### Новые файлы для создания:
- `app/src/main/java/com/financialsuccess/game/domain/usecase/StartGameUseCase.kt`
- `app/src/main/java/com/financialsuccess/game/domain/usecase/RollDiceUseCase.kt`
- `app/src/main/java/com/financialsuccess/game/domain/usecase/BuyAssetUseCase.kt`
- `app/src/main/java/com/financialsuccess/game/domain/usecase/SellAssetUseCase.kt`
- `app/src/main/java/com/financialsuccess/game/domain/usecase/ProcessMonthlyOperationsUseCase.kt`
- `app/src/main/java/com/financialsuccess/game/domain/usecase/CheckFinancialIndependenceUseCase.kt`
- `app/src/main/java/com/financialsuccess/game/domain/usecase/SaveGameUseCase.kt`
- `app/src/main/java/com/financialsuccess/game/domain/usecase/LoadGameUseCase.kt`

### Тестовые файлы:
- `app/src/test/StartGameUseCaseTest.kt`
- `app/src/test/RollDiceUseCaseTest.kt`
- `app/src/test/BuyAssetUseCaseTest.kt`
- `app/src/test/ProcessMonthlyOperationsUseCaseTest.kt`

## 🔍 Анализ связанных файлов

### Обязательный анализ:
1. **GameManager.kt** - анализ всех бизнес-методов
2. **GameActivity.kt** - анализ бизнес-логики в UI
3. **Player.kt** - анализ бизнес-логики в модели
4. **CharacterCreationActivity.kt** - анализ логики создания персонажа
5. **GameState.kt** - анализ управления состоянием
6. **GameRepository.kt** - анализ взаимодействия с данными

### Чек-лист анализа:
- [ ] Все методы в GameManager с бизнес-логикой
- [ ] Все методы в Player с бизнес-логикой
- [ ] Все обработчики событий с бизнес-логикой
- [ ] Все валидации и проверки
- [ ] Все вычисления и расчеты
- [ ] Все операции с состоянием игры

## 🏗️ Архитектурные изменения

### 1. Создание базового Use Case
```kotlin
abstract class UseCase<in P, R> {
    suspend operator fun invoke(parameters: P): Result<R>
}

abstract class NoParamsUseCase<R> {
    suspend operator fun invoke(): Result<R>
}
```

### 2. Создание конкретных Use Cases
```kotlin
class StartGameUseCase @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<StartGameParams, GameState> {
    
    override suspend fun invoke(parameters: StartGameParams): Result<GameState> {
        return try {
            val player = Player(
                position = 0,
                cash = 5000,
                salary = parameters.profession.salary,
                age = parameters.playerAge,
                profession = parameters.profession,
                dream = parameters.dream,
                name = parameters.playerName,
                startDateMillis = parameters.startDateMillis
            )
            
            player.setRandomDeathAge()
            player.updateTotalIncome()
            player.updateTotalExpenses()
            
            player.logIncome(
                FinancialCategory.GAME_START,
                5000,
                "Начальный капитал для старта игры"
            )
            
            val gameState = GameState(player = player)
            gameRepository.saveGameState(gameState)
            
            Result.success(gameState)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class StartGameParams(
    val profession: Profession,
    val dream: Dream,
    val playerAge: Int = 25,
    val playerName: String? = null,
    val startDateMillis: Long? = null
)
```

### 3. Use Case для бросания кубика
```kotlin
class RollDiceUseCase @Inject constructor(
    private val gameRepository: GameRepository
) : NoParamsUseCase<RollDiceResult> {
    
    override suspend fun invoke(): Result<RollDiceResult> {
        return try {
            val currentState = gameRepository.loadGameState() 
                ?: return Result.failure(IllegalStateException("Game not started"))
            
            val diceValue = Random.nextInt(1, 7)
            val oldPosition = currentState.player.position
            val newPosition = (oldPosition + diceValue) % 24
            
            // Обновляем игровой день
            currentState.player.currentDayOfMonth += diceValue
            
            // Проверяем переход на новый месяц
            while (currentState.player.currentDayOfMonth > Player.DAYS_IN_MONTH) {
                currentState.player.currentDayOfMonth -= Player.DAYS_IN_MONTH
                currentState.player.passMonth()
            }
            
            // Проверяем завершение круга
            val passedStart = (oldPosition + diceValue) >= 24
            if (passedStart) {
                currentState.player.cash += currentState.player.salary
                currentState.player.logIncome(
                    FinancialCategory.SALARY,
                    currentState.player.salary,
                    "Ежемесячная зарплата"
                )
                currentState.player.processMonthlyOperations()
            }
            
            currentState.player.position = newPosition
            
            // Проверяем возможность выхода из крысиных бегов
            if (!currentState.player.isInFastTrack && currentState.player.canEscapeRatRace()) {
                currentState.player.isInFastTrack = true
            }
            
            gameRepository.saveGameState(currentState)
            
            Result.success(RollDiceResult(
                diceValue = diceValue,
                newPosition = newPosition,
                passedStart = passedStart,
                escapedRatRace = currentState.player.isInFastTrack
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class RollDiceResult(
    val diceValue: Int,
    val newPosition: Int,
    val passedStart: Boolean,
    val escapedRatRace: Boolean
)
```

### 4. Use Case для покупки активов
```kotlin
class BuyAssetUseCase @Inject constructor(
    private val gameRepository: GameRepository
) : UseCase<BuyAssetParams, BuyAssetResult> {
    
    override suspend fun invoke(parameters: BuyAssetParams): Result<BuyAssetResult> {
        return try {
            val currentState = gameRepository.loadGameState() 
                ?: return Result.failure(IllegalStateException("Game not started"))
            
            val player = currentState.player
            val asset = parameters.asset
            
            if (player.cash >= asset.downPayment) {
                player.addAsset(asset)
                player.updateTotalIncome()
                
                gameRepository.saveGameState(currentState)
                
                Result.success(BuyAssetResult(
                    success = true,
                    remainingCash = player.cash,
                    newPassiveIncome = player.passiveIncome
                ))
            } else {
                Result.success(BuyAssetResult(
                    success = false,
                    remainingCash = player.cash,
                    newPassiveIncome = player.passiveIncome
                ))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

data class BuyAssetParams(val asset: Asset)

data class BuyAssetResult(
    val success: Boolean,
    val remainingCash: Int,
    val newPassiveIncome: Int
)
```

### 5. Обновление GameManager
```kotlin
class GameManager @Inject constructor(
    private val startGameUseCase: StartGameUseCase,
    private val rollDiceUseCase: RollDiceUseCase,
    private val buyAssetUseCase: BuyAssetUseCase,
    private val sellAssetUseCase: SellAssetUseCase,
    private val saveGameUseCase: SaveGameUseCase,
    private val loadGameUseCase: LoadGameUseCase
) {
    
    suspend fun startNewGame(
        profession: Profession,
        dream: Dream,
        playerAge: Int = 25,
        playerName: String? = null,
        startDateMillis: Long? = null
    ): Result<GameState> {
        val params = StartGameParams(
            profession = profession,
            dream = dream,
            playerAge = playerAge,
            playerName = playerName,
            startDateMillis = startDateMillis
        )
        return startGameUseCase(params)
    }
    
    suspend fun rollDice(): Result<RollDiceResult> {
        return rollDiceUseCase()
    }
    
    suspend fun buyAsset(asset: Asset): Result<BuyAssetResult> {
        return buyAssetUseCase(BuyAssetParams(asset))
    }
}
```

## ✅ Критерии приемки

### Функциональные требования:
- [ ] Все существующие функции игры работают корректно
- [ ] Бросание кубика функционирует правильно
- [ ] Покупка/продажа активов работает
- [ ] Сохранение/загрузка игры функционирует
- [ ] Все расчеты и валидации работают
- [ ] Обработка ошибок работает правильно

### Архитектурные требования:
- [ ] Вся бизнес-логика инкапсулирована в Use Cases
- [ ] Use Cases не зависят от UI слоя
- [ ] Use Cases возвращают Result для обработки ошибок
- [ ] GameManager использует Use Cases
- [ ] Нет дублирования бизнес-логики
- [ ] Четкое разделение ответственности

### Тестируемость:
- [ ] Все Use Cases покрыты unit-тестами на 90%+
- [ ] Тесты проверяют все сценарии успеха и ошибок
- [ ] Тесты изолированы и не зависят друг от друга
- [ ] Мокирование зависимостей работает корректно
- [ ] Тесты проверяют граничные случаи

### Производительность:
- [ ] Use Cases выполняются асинхронно
- [ ] Нет блокировки UI при выполнении Use Cases
- [ ] Операции с данными оптимизированы
- [ ] Кэширование работает эффективно

## 🚨 Правила исполнения

### ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ:

#### 1. Полный анализ всех связанных файлов
- [ ] Проанализировать всю бизнес-логику в GameManager
- [ ] Изучить всю бизнес-логику в Player
- [ ] Проверить всю бизнес-логику в Activity
- [ ] Проанализировать все валидации и проверки
- [ ] Изучить все вычисления и расчеты

#### 2. Создать чек-лист проверок
- [ ] Синтаксис всех новых Use Cases
- [ ] Корректность параметров и результатов
- [ ] Правильность обработки ошибок
- [ ] Корректность асинхронных операций
- [ ] Проверка изоляции бизнес-логики

#### 3. Предварительная проверка
- [ ] Синтаксис всех затрагиваемых файлов
- [ ] Структура и отступы
- [ ] Контекст изменений
- [ ] Возможные побочные эффекты

#### 4. Проверка критических областей
- [ ] Асинхронные операции
- [ ] Обработка ошибок
- [ ] Валидация входных данных
- [ ] Изоляция бизнес-логики
- [ ] Переиспользование кода

#### 5. Знание стандартов платформы
- [ ] Clean Architecture principles
- [ ] Use Case patterns
- [ ] Result pattern best practices
- [ ] Dependency injection with Use Cases
- [ ] Testing Use Cases

### ПРЕДПОЛОЖЕНИЯ И ЗАВИСИМОСТИ:
- [ ] Repository Pattern внедрен
- [ ] Dependency Injection настроен
- [ ] Coroutines доступны
- [ ] Result pattern используется
- [ ] Все необходимые зависимости установлены

### ПРЕ-КОММИТ ПРОВЕРКИ:
- [ ] Автоматическая валидация синтаксиса Kotlin
- [ ] Проверка форматирования кода
- [ ] Линтинг всех измененных файлов
- [ ] Проверка на типичные ошибки Use Cases
- [ ] Валидация асинхронных операций

### ОТКАЗОУСТОЙЧИВОСТЬ:
- [ ] Обработка ошибок в Use Cases
- [ ] Восстановление после сбоев
- [ ] Корректная очистка ресурсов
- [ ] Обработка некорректных входных данных

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

- **Анализ бизнес-логики**: 1 день
- **Создание базовых Use Cases**: 2 дня
- **Создание конкретных Use Cases**: 3 дня
- **Обновление GameManager**: 1 день
- **Тестирование**: 2 дня

**Общее время**: 9 дней