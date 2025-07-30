# Задача 6: Добавление Room Database

## 📋 Описание задачи

Внедрить Room Database для локального хранения данных игры, улучшения производительности и обеспечения надежного хранения состояния приложения.

## 🎯 Цели

- Создать локальную базу данных для хранения игровых данных
- Улучшить производительность работы с данными
- Обеспечить надежное хранение состояния игры
- Упростить запросы к данным
- Обеспечить миграции базы данных

## 📁 Затронутые файлы

### Основные файлы:
- `app/src/main/java/com/financialsuccess/game/data/GameDataManager.kt`
- `app/src/main/java/com/financialsuccess/game/data/repository/GameRepositoryImpl.kt`
- `app/src/main/java/com/financialsuccess/game/data/datasource/GameLocalDataSource.kt`
- `app/src/main/java/com/financialsuccess/game/models/Player.kt`
- `app/src/main/java/com/financialsuccess/game/models/GameState.kt`

### Новые файлы для создания:
- `app/src/main/java/com/financialsuccess/game/data/database/GameDatabase.kt`
- `app/src/main/java/com/financialsuccess/game/data/database/dao/GameDao.kt`
- `app/src/main/java/com/financialsuccess/game/data/database/dao/PlayerDao.kt`
- `app/src/main/java/com/financialsuccess/game/data/database/dao/AssetDao.kt`
- `app/src/main/java/com/financialsuccess/game/data/database/entity/GameStateEntity.kt`
- `app/src/main/java/com/financialsuccess/game/data/database/entity/PlayerEntity.kt`
- `app/src/main/java/com/financialsuccess/game/data/database/entity/AssetEntity.kt`
- `app/src/main/java/com/financialsuccess/game/data/database/entity/FinancialEntryEntity.kt`

### Тестовые файлы:
- `app/src/test/GameDatabaseTest.kt`
- `app/src/test/GameDaoTest.kt`
- `app/src/test/PlayerDaoTest.kt`

## 🔍 Анализ связанных файлов

### Обязательный анализ:
1. **GameDataManager.kt** - анализ всех данных для хранения
2. **Player.kt** - анализ структуры данных игрока
3. **GameState.kt** - анализ состояния игры
4. **Asset.kt** - анализ структуры активов
5. **FinancialEntry.kt** - анализ финансовых записей
6. **build.gradle** - проверка Room зависимостей
7. **GameRepositoryImpl.kt** - анализ текущего хранения данных

### Чек-лист анализа:
- [ ] Все модели данных для хранения
- [ ] Все связи между данными
- [ ] Все типы данных и их размеры
- [ ] Все индексы для оптимизации
- [ ] Все ограничения целостности
- [ ] Все миграции данных

## 🏗️ Архитектурные изменения

### 1. Добавление Room зависимостей в build.gradle
```gradle
dependencies {
    def room_version = "2.6.0"
    
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
    
    // Testing
    testImplementation "androidx.room:room-testing:$room_version"
}
```

### 2. Создание Entity классов
```kotlin
@Entity(tableName = "game_states")
data class GameStateEntity(
    @PrimaryKey val id: String = "current_game",
    val playerId: String,
    val currentPosition: Int,
    val currentDayOfMonth: Int,
    val monthsPlayed: Int,
    val isInFastTrack: Boolean,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "players",
    foreignKeys = [
        ForeignKey(
            entity = ProfessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["professionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlayerEntity(
    @PrimaryKey val id: String,
    val name: String?,
    val age: Int,
    val cash: Int,
    val salary: Int,
    val totalIncome: Int,
    val totalExpenses: Int,
    val passiveIncome: Int,
    val professionId: String,
    val dreamId: String,
    val education: String,
    val workExperience: Int,
    val maritalStatus: String,
    val childrenCount: Int,
    val spouseIncome: Int,
    val riskTolerance: String,
    val investmentStrategy: String,
    val savingsRate: Int,
    val healthLevel: String,
    val stressLevel: String,
    val workLifeBalance: String,
    val deathAge: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey val id: String,
    val playerId: String,
    val name: String,
    val description: String,
    val value: Int,
    val downPayment: Int,
    val cashFlow: Int,
    val type: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "financial_entries")
data class FinancialEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val playerId: String,
    val category: String,
    val amount: Int,
    val description: String,
    val timestamp: Long = System.currentTimeMillis()
)
```

### 3. Создание DAO интерфейсов
```kotlin
@Dao
interface GameDao {
    
    @Query("SELECT * FROM game_states WHERE id = :gameId")
    suspend fun getGameState(gameId: String): GameStateEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGameState(gameState: GameStateEntity)
    
    @Update
    suspend fun updateGameState(gameState: GameStateEntity)
    
    @Delete
    suspend fun deleteGameState(gameState: GameStateEntity)
    
    @Query("DELETE FROM game_states WHERE id = :gameId")
    suspend fun deleteGameStateById(gameId: String)
}

@Dao
interface PlayerDao {
    
    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayer(playerId: String): PlayerEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)
    
    @Update
    suspend fun updatePlayer(player: PlayerEntity)
    
    @Delete
    suspend fun deletePlayer(player: PlayerEntity)
    
    @Query("SELECT * FROM players ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestPlayer(): PlayerEntity?
}

@Dao
interface AssetDao {
    
    @Query("SELECT * FROM assets WHERE playerId = :playerId")
    suspend fun getAssetsByPlayerId(playerId: String): List<AssetEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity)
    
    @Delete
    suspend fun deleteAsset(asset: AssetEntity)
    
    @Query("DELETE FROM assets WHERE playerId = :playerId")
    suspend fun deleteAssetsByPlayerId(playerId: String)
}

@Dao
interface FinancialEntryDao {
    
    @Query("SELECT * FROM financial_entries WHERE playerId = :playerId ORDER BY timestamp DESC")
    suspend fun getFinancialEntriesByPlayerId(playerId: String): List<FinancialEntryEntity>
    
    @Insert
    suspend fun insertFinancialEntry(entry: FinancialEntryEntity)
    
    @Query("DELETE FROM financial_entries WHERE playerId = :playerId")
    suspend fun deleteFinancialEntriesByPlayerId(playerId: String)
}
```

### 4. Создание Database класса
```kotlin
@Database(
    entities = [
        GameStateEntity::class,
        PlayerEntity::class,
        AssetEntity::class,
        FinancialEntryEntity::class,
        ProfessionEntity::class,
        DreamEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GameDatabase : RoomDatabase() {
    
    abstract fun gameDao(): GameDao
    abstract fun playerDao(): PlayerDao
    abstract fun assetDao(): AssetDao
    abstract fun financialEntryDao(): FinancialEntryDao
    abstract fun professionDao(): ProfessionDao
    abstract fun dreamDao(): DreamDao
    
    companion object {
        @Volatile
        private var INSTANCE: GameDatabase? = null
        
        fun getDatabase(context: Context): GameDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java,
                    "game_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
```

### 5. Обновление GameLocalDataSource
```kotlin
class GameLocalDataSourceImpl @Inject constructor(
    private val database: GameDatabase,
    private val mapper: GameMapper
) : GameLocalDataSource {
    
    override suspend fun saveGameState(gameState: GameState): Boolean {
        return try {
            val playerEntity = mapper.mapPlayerToEntity(gameState.player)
            val gameStateEntity = mapper.mapGameStateToEntity(gameState)
            
            database.playerDao().insertPlayer(playerEntity)
            database.gameDao().insertGameState(gameStateEntity)
            
            // Сохраняем активы
            gameState.player.assets.forEach { asset ->
                val assetEntity = mapper.mapAssetToEntity(asset, gameState.player.id)
                database.assetDao().insertAsset(assetEntity)
            }
            
            // Сохраняем финансовые записи
            gameState.player.financialJournal.forEach { entry ->
                val entryEntity = mapper.mapFinancialEntryToEntity(entry, gameState.player.id)
                database.financialEntryDao().insertFinancialEntry(entryEntity)
            }
            
            true
        } catch (e: Exception) {
            false
        }
    }
    
    override suspend fun loadGameState(): GameState? {
        return try {
            val gameStateEntity = database.gameDao().getGameState("current_game") ?: return null
            val playerEntity = database.playerDao().getPlayer(gameStateEntity.playerId) ?: return null
            
            val assets = database.assetDao().getAssetsByPlayerId(playerEntity.id)
            val financialEntries = database.financialEntryDao().getFinancialEntriesByPlayerId(playerEntity.id)
            
            val player = mapper.mapEntityToPlayer(playerEntity, assets, financialEntries)
            GameState(player = player)
        } catch (e: Exception) {
            null
        }
    }
    
    override suspend fun getProfessions(): List<Profession> {
        return try {
            val professionEntities = database.professionDao().getAllProfessions()
            professionEntities.map { mapper.mapEntityToProfession(it) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
```

### 6. Обновление Mapper
```kotlin
class GameMapper @Inject constructor() {
    
    fun mapPlayerToEntity(player: Player): PlayerEntity {
        return PlayerEntity(
            id = player.id ?: generatePlayerId(),
            name = player.name,
            age = player.age,
            cash = player.cash,
            salary = player.salary,
            totalIncome = player.totalIncome,
            totalExpenses = player.totalExpenses,
            passiveIncome = player.passiveIncome,
            professionId = player.profession.id,
            dreamId = player.dream.id,
            education = player.education.name,
            workExperience = player.workExperience,
            maritalStatus = player.maritalStatus.name,
            childrenCount = player.childrenCount,
            spouseIncome = player.spouseIncome,
            riskTolerance = player.riskTolerance.name,
            investmentStrategy = player.investmentStrategy.name,
            savingsRate = player.savingsRate,
            healthLevel = player.healthLevel.name,
            stressLevel = player.stressLevel.name,
            workLifeBalance = player.workLifeBalance.name,
            deathAge = player.deathAge
        )
    }
    
    fun mapEntityToPlayer(
        entity: PlayerEntity,
        assets: List<AssetEntity>,
        financialEntries: List<FinancialEntryEntity>
    ): Player {
        return Player(
            id = entity.id,
            name = entity.name,
            age = entity.age,
            cash = entity.cash,
            salary = entity.salary,
            totalIncome = entity.totalIncome,
            totalExpenses = entity.totalExpenses,
            passiveIncome = entity.passiveIncome,
            profession = getProfessionById(entity.professionId),
            dream = getDreamById(entity.dreamId),
            education = EducationLevel.valueOf(entity.education),
            workExperience = entity.workExperience,
            maritalStatus = MaritalStatus.valueOf(entity.maritalStatus),
            childrenCount = entity.childrenCount,
            spouseIncome = entity.spouseIncome,
            riskTolerance = RiskTolerance.valueOf(entity.riskTolerance),
            investmentStrategy = InvestmentStrategy.valueOf(entity.investmentStrategy),
            savingsRate = entity.savingsRate,
            healthLevel = HealthLevel.valueOf(entity.healthLevel),
            stressLevel = StressLevel.valueOf(entity.stressLevel),
            workLifeBalance = WorkLifeBalance.valueOf(entity.workLifeBalance),
            deathAge = entity.deathAge,
            assets = assets.map { mapEntityToAsset(it) }.toMutableList(),
            financialJournal = financialEntries.map { mapEntityToFinancialEntry(it) }.toMutableList()
        )
    }
}
```

## ✅ Критерии приемки

### Функциональные требования:
- [ ] Все данные игры сохраняются в базу данных
- [ ] Загрузка состояния игры работает корректно
- [ ] Все активы сохраняются и загружаются
- [ ] Все финансовые записи сохраняются
- [ ] Производительность работы с данными улучшена
- [ ] Обработка ошибок работает правильно

### Архитектурные требования:
- [ ] Room Database полностью внедрен
- [ ] Все Entity классы созданы
- [ ] Все DAO интерфейсы реализованы
- [ ] Mapper для преобразования данных создан
- [ ] GameLocalDataSource использует Room
- [ ] Нет прямых обращений к SharedPreferences

### Тестируемость:
- [ ] Database покрыт unit-тестами
- [ ] DAO покрыты тестами
- [ ] Mapper покрыт тестами
- [ ] Тесты проверяют миграции
- [ ] Тесты проверяют обработку ошибок

### Производительность:
- [ ] Запросы к базе данных оптимизированы
- [ ] Индексы созданы для часто используемых полей
- [ ] Операции с базой данных выполняются асинхронно
- [ ] Нет блокировки UI при работе с базой данных

## 🚨 Правила исполнения

### ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ:

#### 1. Полный анализ всех связанных файлов
- [ ] Проанализировать все модели данных
- [ ] Изучить все связи между данными
- [ ] Проверить все типы данных
- [ ] Проанализировать все индексы
- [ ] Изучить все ограничения

#### 2. Создать чек-лист проверок
- [ ] Синтаксис всех Entity классов
- [ ] Корректность DAO интерфейсов
- [ ] Правильность связей между таблицами
- [ ] Корректность индексов
- [ ] Проверка миграций

#### 3. Предварительная проверка
- [ ] Синтаксис всех затрагиваемых файлов
- [ ] Структура и отступы
- [ ] Контекст изменений
- [ ] Возможные побочные эффекты

#### 4. Проверка критических областей
- [ ] Схема базы данных
- [ ] Миграции данных
- [ ] Производительность запросов
- [ ] Обработка ошибок
- [ ] Целостность данных

#### 5. Знание стандартов платформы
- [ ] Room Database best practices
- [ ] Database design patterns
- [ ] Migration strategies
- [ ] Performance optimization
- [ ] Testing databases

### ПРЕДПОЛОЖЕНИЯ И ЗАВИСИМОСТИ:
- [ ] Room Database доступен
- [ ] Kotlin Coroutines доступны
- [ ] Context доступен для создания базы данных
- [ ] Все необходимые зависимости установлены
- [ ] Права на запись в файловую систему есть

### ПРЕ-КОММИТ ПРОВЕРКИ:
- [ ] Автоматическая валидация синтаксиса Kotlin
- [ ] Проверка форматирования кода
- [ ] Линтинг всех измененных файлов
- [ ] Проверка на типичные ошибки Room
- [ ] Валидация схемы базы данных

### ОТКАЗОУСТОЙЧИВОСТЬ:
- [ ] Обработка ошибок базы данных
- [ ] Восстановление после сбоев
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

- **Анализ данных**: 1 день
- **Создание Entity классов**: 2 дня
- **Создание DAO интерфейсов**: 2 дня
- **Создание Database класса**: 1 день
- **Обновление Mapper и DataSource**: 2 дня
- **Тестирование**: 2 дня

**Общее время**: 10 дней