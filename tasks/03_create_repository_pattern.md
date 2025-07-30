# Задача 3: Создание Repository Pattern

## 📋 Описание задачи

Внедрить Repository Pattern для абстракции доступа к данным, улучшения тестируемости и обеспечения единого интерфейса для работы с данными игры.

## 🎯 Цели

- Создать абстракцию для доступа к данным
- Улучшить тестируемость компонентов
- Разделить логику доступа к данным и бизнес-логику
- Обеспечить возможность легкого переключения источников данных
- Унифицировать интерфейс работы с данными

## 📁 Затронутые файлы

### Основные файлы:
- `app/src/main/java/com/financialsuccess/game/data/GameDataManager.kt`
- `app/src/main/java/com/financialsuccess/game/GameManager.kt`
- `app/src/main/java/com/financialsuccess/game/GameActivity.kt`
- `app/src/main/java/com/financialsuccess/game/CharacterCreationActivity.kt`

### Новые файлы для создания:
- `app/src/main/java/com/financialsuccess/game/domain/repository/GameRepository.kt`
- `app/src/main/java/com/financialsuccess/game/data/repository/GameRepositoryImpl.kt`
- `app/src/main/java/com/financialsuccess/game/data/datasource/GameLocalDataSource.kt`
- `app/src/main/java/com/financialsuccess/game/data/datasource/GameRemoteDataSource.kt`
- `app/src/main/java/com/financialsuccess/game/data/mapper/GameMapper.kt`

### Тестовые файлы:
- `app/src/test/GameRepositoryTest.kt`
- `app/src/test/GameRepositoryImplTest.kt`
- `app/src/test/GameLocalDataSourceTest.kt`

## 🔍 Анализ связанных файлов

### Обязательный анализ:
1. **GameDataManager.kt** - анализ всех статических методов
2. **GameManager.kt** - проверка использования GameDataManager
3. **GameActivity.kt** - анализ прямого обращения к данным
4. **CharacterCreationActivity.kt** - анализ работы с данными
5. **ProfessionSelectionActivity.kt** - анализ работы с данными
6. **Player.kt** - анализ модели данных
7. **build.gradle** - проверка зависимостей для работы с данными

### Чек-лист анализа:
- [ ] Все статические методы в GameDataManager
- [ ] Все прямые обращения к данным в Activity
- [ ] Все методы сохранения/загрузки
- [ ] Все методы получения списков данных
- [ ] Все методы валидации данных
- [ ] Все методы преобразования данных

## 🏗️ Архитектурные изменения

### 1. Создание интерфейса Repository
```kotlin
interface GameRepository {
    // Профессии
    suspend fun getProfessions(): List<Profession>
    suspend fun getProfessionById(id: String): Profession?
    
    // Мечты
    suspend fun getDreams(): List<Dream>
    suspend fun getDreamById(id: String): Dream?
    
    // Активы
    suspend fun getAssets(): List<Asset>
    suspend fun getAssetById(id: String): Asset?
    
    // Сохранение/загрузка игры
    suspend fun saveGameState(gameState: GameState): Boolean
    suspend fun loadGameState(): GameState?
    suspend fun deleteGameState(): Boolean
    
    // Игровые данные
    suspend fun getGameData(): GameData
    suspend fun updateGameData(gameData: GameData): Boolean
}
```

### 2. Создание Data Sources
```kotlin
interface GameLocalDataSource {
    suspend fun getProfessions(): List<Profession>
    suspend fun getDreams(): List<Dream>
    suspend fun getAssets(): List<Asset>
    suspend fun saveGameState(gameState: GameState): Boolean
    suspend fun loadGameState(): GameState?
    suspend fun deleteGameState(): Boolean
}

class GameLocalDataSourceImpl @Inject constructor(
    private val context: Context,
    private val gson: Gson
) : GameLocalDataSource {
    
    override suspend fun getProfessions(): List<Profession> {
        return withContext(Dispatchers.IO) {
            // Чтение из локального файла или SharedPreferences
            val json = context.assets.open("professions.json").bufferedReader().use { it.readText() }
            gson.fromJson(json, Array<Profession>::class.java).toList()
        }
    }
    
    override suspend fun saveGameState(gameState: GameState): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = gson.toJson(gameState)
                context.getSharedPreferences("game_data", Context.MODE_PRIVATE)
                    .edit()
                    .putString("game_state", json)
                    .apply()
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
```

### 3. Создание Mapper
```kotlin
class GameMapper @Inject constructor() {
    
    fun mapProfessionToEntity(profession: Profession): ProfessionEntity {
        return ProfessionEntity(
            id = profession.id,
            name = profession.name,
            description = profession.description,
            salary = profession.salary,
            expenses = profession.expenses,
            taxes = profession.taxes,
            education = profession.education,
            avatarResId = profession.avatarResId
        )
    }
    
    fun mapEntityToProfession(entity: ProfessionEntity): Profession {
        return Profession(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            salary = entity.salary,
            expenses = entity.expenses,
            taxes = entity.taxes,
            education = entity.education,
            avatarResId = entity.avatarResId
        )
    }
}
```

### 4. Создание Repository Implementation
```kotlin
class GameRepositoryImpl @Inject constructor(
    private val localDataSource: GameLocalDataSource,
    private val remoteDataSource: GameRemoteDataSource,
    private val mapper: GameMapper
) : GameRepository {
    
    override suspend fun getProfessions(): List<Profession> {
        return try {
            // Сначала пытаемся получить из локального источника
            val localProfessions = localDataSource.getProfessions()
            if (localProfessions.isNotEmpty()) {
                return localProfessions
            }
            
            // Если локальных данных нет, получаем из удаленного источника
            val remoteProfessions = remoteDataSource.getProfessions()
            // Сохраняем локально для будущего использования
            localDataSource.saveProfessions(remoteProfessions)
            remoteProfessions
        } catch (e: Exception) {
            // В случае ошибки возвращаем пустой список
            emptyList()
        }
    }
    
    override suspend fun saveGameState(gameState: GameState): Boolean {
        return try {
            localDataSource.saveGameState(gameState)
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun loadGameState(): GameState? {
        return try {
            localDataSource.loadGameState()
        } catch (e: Exception) {
            null
        }
    }
}
```

### 5. Обновление GameManager
```kotlin
class GameManager @Inject constructor(
    private val gameRepository: GameRepository
) {
    
    suspend fun getProfessions(): List<Profession> {
        return gameRepository.getProfessions()
    }
    
    suspend fun getDreams(): List<Dream> {
        return gameRepository.getDreams()
    }
    
    suspend fun saveGameState(gameState: GameState): Boolean {
        return gameRepository.saveGameState(gameState)
    }
    
    suspend fun loadGameState(): GameState? {
        return gameRepository.loadGameState()
    }
}
```

## ✅ Критерии приемки

### Функциональные требования:
- [ ] Все существующие функции работы с данными работают корректно
- [ ] Получение списков профессий, мечт, активов функционирует
- [ ] Сохранение/загрузка игры работает
- [ ] Все Activity корректно используют Repository
- [ ] Обработка ошибок работает правильно
- [ ] Кэширование данных функционирует

### Архитектурные требования:
- [ ] Repository Pattern полностью внедрен
- [ ] Интерфейс Repository определен
- [ ] Data Sources разделены на Local и Remote
- [ ] Mapper для преобразования данных создан
- [ ] GameDataManager заменен на Repository
- [ ] Нет прямых обращений к данным в UI слое

### Тестируемость:
- [ ] Repository покрыт unit-тестами на 90%+
- [ ] Data Sources покрыты тестами
- [ ] Mapper покрыт тестами
- [ ] Тесты проверяют обработку ошибок
- [ ] Тесты проверяют кэширование

### Производительность:
- [ ] Операции с данными выполняются асинхронно
- [ ] Кэширование работает эффективно
- [ ] Нет блокировки UI при работе с данными
- [ ] Обработка больших объемов данных оптимизирована

## 🚨 Правила исполнения

### ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ:

#### 1. Полный анализ всех связанных файлов
- [ ] Проанализировать все методы GameDataManager
- [ ] Изучить все обращения к данным в Activity
- [ ] Проверить все методы сохранения/загрузки
- [ ] Проанализировать все статические методы
- [ ] Изучить все зависимости от данных

#### 2. Создать чек-лист проверок
- [ ] Синтаксис всех новых файлов Repository
- [ ] Корректность интерфейсов
- [ ] Правильность реализации Data Sources
- [ ] Корректность Mapper классов
- [ ] Проверка обработки ошибок

#### 3. Предварительная проверка
- [ ] Синтаксис всех затрагиваемых файлов
- [ ] Структура и отступы
- [ ] Контекст изменений
- [ ] Возможные побочные эффекты

#### 4. Проверка критических областей
- [ ] Асинхронные операции
- [ ] Обработка ошибок
- [ ] Кэширование данных
- [ ] Преобразование данных
- [ ] Сохранение/загрузка состояния

#### 5. Знание стандартов платформы
- [ ] Repository Pattern best practices
- [ ] Android data layer guidelines
- [ ] Coroutines best practices
- [ ] Error handling patterns
- [ ] Caching strategies

### ПРЕДПОЛОЖЕНИЯ И ЗАВИСИМОСТИ:
- [ ] Coroutines доступны в проекте
- [ ] Gson доступен для сериализации
- [ ] Context доступен для работы с файлами
- [ ] SharedPreferences доступны
- [ ] Все необходимые зависимости установлены

### ПРЕ-КОММИТ ПРОВЕРКИ:
- [ ] Автоматическая валидация синтаксиса Kotlin
- [ ] Проверка форматирования кода
- [ ] Линтинг всех измененных файлов
- [ ] Проверка на типичные ошибки Repository Pattern
- [ ] Валидация асинхронных операций

### ОТКАЗОУСТОЙЧИВОСТЬ:
- [ ] Обработка ошибок сети
- [ ] Восстановление после сбоев базы данных
- [ ] Корректная очистка ресурсов
- [ ] Обработка отсутствующих данных

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

- **Анализ существующих данных**: 1 день
- **Создание интерфейсов Repository**: 1 день
- **Реализация Data Sources**: 2 дня
- **Создание Mapper классов**: 1 день
- **Обновление существующих классов**: 2 дня
- **Тестирование**: 1 день

**Общее время**: 8 дней