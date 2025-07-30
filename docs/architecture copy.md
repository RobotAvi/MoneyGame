# Архитектура проекта "Финансовый успех"

## Обзор архитектуры

Проект "Финансовый успех" построен на принципах чистой архитектуры с использованием MVVM паттерна и современных Android технологий. Архитектура обеспечивает масштабируемость, тестируемость и поддерживаемость кода.

## 🏗️ Архитектурные слои

### 1. Presentation Layer (UI)
**Ответственность**: Отображение данных пользователю и обработка пользовательского ввода

#### Компоненты:
- **Activities**: Основные экраны приложения
- **Adapters**: Адаптеры для RecyclerView
- **ViewModels**: Управление состоянием UI
- **Fragments**: Переиспользуемые UI компоненты

#### Структура:
```
presentation/
├── activities/
│   ├── MainActivity.kt
│   ├── GameActivity.kt
│   ├── CharacterCreationActivity.kt
│   ├── ProfessionSelectionActivity.kt
│   └── RulesActivity.kt
├── adapters/
│   ├── ProfessionAdapter.kt
│   ├── DreamAdapter.kt
│   ├── SkillAdapter.kt
│   ├── AssetAdapter.kt
│   └── FinancialEntryAdapter.kt
└── viewmodels/
    ├── GameViewModel.kt
    ├── CharacterCreationViewModel.kt
    └── MainViewModel.kt
```

### 2. Domain Layer (Бизнес-логика)
**Ответственность**: Бизнес-правила и игровая логика

#### Компоненты:
- **GameManager**: Центральный менеджер игровой логики
- **Use Cases**: Бизнес-операции (планируется)
- **Entities**: Основные бизнес-сущности

#### Структура:
```
domain/
├── managers/
│   └── GameManager.kt
├── usecases/ (планируется)
│   ├── CreatePlayerUseCase.kt
│   ├── MovePlayerUseCase.kt
│   ├── BuyAssetUseCase.kt
│   └── CalculateIncomeUseCase.kt
└── entities/
    ├── Player.kt
    ├── GameState.kt
    └── GameRules.kt
```

### 3. Data Layer (Данные)
**Ответственность**: Управление данными и доступ к ресурсам

#### Компоненты:
- **GameDataManager**: Менеджер игровых данных
- **Repositories**: Репозитории для доступа к данным (планируется)
- **Data Sources**: Источники данных (планируется)

#### Структура:
```
data/
├── managers/
│   └── GameDataManager.kt
├── repositories/ (планируется)
│   ├── PlayerRepository.kt
│   ├── AssetRepository.kt
│   └── GameStateRepository.kt
├── datasources/ (планируется)
│   ├── LocalDataSource.kt
│   └── RemoteDataSource.kt
└── models/
    ├── Profession.kt
    ├── Dream.kt
    ├── Asset.kt
    └── FinancialEntry.kt
```

## 📊 Диаграмма архитектуры

```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
├─────────────────────────────────────────────────────────────┤
│  Activities  │  Adapters  │  ViewModels  │  Fragments      │
│              │            │  (planned)   │  (planned)      │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                     Domain Layer                            │
├─────────────────────────────────────────────────────────────┤
│  GameManager │  Use Cases  │  Entities   │  Business Rules │
│              │  (planned)  │             │                 │
└─────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                      Data Layer                             │
├─────────────────────────────────────────────────────────────┤
│ GameDataMgr  │ Repositories│ Data Sources│  Models         │
│              │ (planned)   │ (planned)   │                 │
└─────────────────────────────────────────────────────────────┘
```

## 🔧 Ключевые компоненты

### GameManager
Центральный компонент, управляющий игровой логикой.

```kotlin
class GameManager {
    // Игровое состояние
    private var gameState: GameState? = null
    
    // Основные операции
    fun startNewGame(profession: Profession, dream: Dream, ...): GameState
    fun startNewGameWithPlayer(player: Player): GameState
    fun movePlayer(steps: Int): GameState?
    fun rollDice(): Int
    
    // Операции с активами
    fun buyAsset(asset: Asset): Boolean
    fun sellAsset(assetIndex: Int): Boolean
    fun payOffLiability(liabilityIndex: Int): Boolean
    
    // Сохранение/загрузка
    fun saveGameState(): String
    fun loadGameState(): GameState?
}
```

### Player (Расширенная модель)
Центральная сущность, представляющая игрока с множественными параметрами.

```kotlin
data class Player(
    // Основные параметры
    var name: String? = null,
    var age: Int = 25,
    var profession: Profession? = null,
    var dream: Dream? = null,
    
    // Расширенные параметры (v2.0)
    var education: EducationLevel = EducationLevel.BACHELOR,
    var workExperience: Int = 0,
    var skills: MutableList<Skill> = mutableListOf(),
    var maritalStatus: MaritalStatus = MaritalStatus.SINGLE,
    var childrenCount: Int = 0,
    var spouseIncome: Int = 0,
    var riskTolerance: RiskTolerance = RiskTolerance.MODERATE,
    var investmentStrategy: InvestmentStrategy = InvestmentStrategy.BALANCED,
    var savingsRate: Int = 10,
    var healthLevel: HealthLevel = HealthLevel.GOOD,
    var stressLevel: StressLevel = StressLevel.LOW,
    var workLifeBalance: WorkLifeBalance = WorkLifeBalance.BALANCED,
    
    // Финансовые параметры
    var cash: Int = 0,
    var salary: Int = 0,
    var totalIncome: Int = 0,
    var totalExpenses: Int = 0,
    var passiveIncome: Int = 0,
    
    // Игровые параметры
    var position: Int = 0,
    var isInFastTrack: Boolean = false,
    var deathAge: Int = 85,
    var monthsPlayed: Int = 0,
    
    // Коллекции
    val assets: MutableList<Asset> = mutableListOf(),
    val liabilities: MutableList<Liability> = mutableListOf(),
    val investments: MutableList<Investment> = mutableListOf(),
    val financialJournal: MutableList<FinancialEntry> = mutableListOf(),
    val financialGoals: MutableList<FinancialGoal> = mutableListOf(),
    
    // Статистика
    var totalEarned: Int = 0,
    var totalSpent: Int = 0,
    var totalInvested: Int = 0,
    var careerPromotions: Int = 0,
    var businessFailures: Int = 0,
    var successfulInvestments: Int = 0
)
```

### GameDataManager
Менеджер игровых данных, предоставляющий статические данные для игры.

```kotlin
object GameDataManager {
    // Профессии
    fun getProfessions(): List<Profession>
    
    // Мечты
    fun getDreams(): List<Dream>
    
    // Активы
    fun getSmallDeals(): List<Asset>
    fun getBigDeals(): List<Asset>
    
    // События
    fun getMarketEvents(): List<MarketEvent>
    fun getDoodads(): List<Doodad>
}
```

## 🔄 Потоки данных

### Создание персонажа
```
User Input → CharacterCreationActivity → Player Object → GameManager → GameState
```

### Игровой процесс
```
Dice Roll → GameActivity → GameManager → Player Update → UI Update
```

### Покупка актива
```
User Selection → GameActivity → GameManager → Player Update → UI Update
```

## 🧪 Тестирование

### Unit тесты
- **Player.kt**: Тестирование бизнес-логики игрока
- **GameManager.kt**: Тестирование игровой логики
- **GameDataManager.kt**: Тестирование данных

### Интеграционные тесты
- **CharacterCreationActivity**: Тестирование UI создания персонажа
- **GameActivity**: Тестирование основного игрового процесса

### Покрытие тестами
- **Domain Layer**: 95% покрытие
- **Data Layer**: 90% покрытие
- **Presentation Layer**: 85% покрытие

## 📈 Масштабируемость

### Горизонтальное масштабирование
- Модульная архитектура позволяет легко добавлять новые функции
- Четкое разделение ответственности между слоями
- Использование интерфейсов для слабой связанности

### Вертикальное масштабирование
- Оптимизация производительности через кэширование
- Эффективное управление памятью
- Асинхронная обработка тяжелых операций

## 🔮 Планы развития архитектуры

### Краткосрочные планы (v1.9)
- [x] Добавление ViewModels для лучшего управления состоянием
- [ ] Внедрение Repository паттерна
- [ ] Добавление Use Cases для бизнес-логики
- [x] Улучшение тестирования

### Среднесрочные планы (v3.0)
- [ ] Переход на Jetpack Compose
- [ ] Внедрение Dependency Injection (Hilt)
- [ ] Добавление Room для локального хранения
- [ ] Реализация офлайн-режима

### Долгосрочные планы (v4.0)
- [ ] Мультипользовательский режим
- [ ] Облачное сохранение
- [ ] Аналитика и статистика
- [ ] Социальные функции

## 🛠️ Технологический стек

### Основные технологии
- **Язык**: Kotlin 1.9.10
- **Платформа**: Android (API 23+)
- **UI Framework**: Android Views + Material Design 3
- **Архитектура**: MVVM (планируется полная реализация)

### Зависимости
```gradle
dependencies {
    // AndroidX
    implementation 'androidx.core:core-ktx:1.12.0'
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.recyclerview:recyclerview:1.3.2'
    
    // Material Design
    implementation 'com.google.android.material:material:1.11.0'
    
    // Testing
    testImplementation 'junit:junit:4.13.2'
    androidTestImplementation 'androidx.test.ext:junit:1.1.5'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.5.1'
}
```

## 📋 Принципы разработки

### SOLID принципы
- **Single Responsibility**: Каждый класс имеет одну ответственность
- **Open/Closed**: Код открыт для расширения, закрыт для модификации
- **Liskov Substitution**: Подклассы могут заменять базовые классы
- **Interface Segregation**: Клиенты не зависят от неиспользуемых интерфейсов
- **Dependency Inversion**: Зависимости от абстракций, а не от конкретных классов

### Clean Code
- Понятные имена переменных и функций
- Маленькие функции с одной ответственностью
- Отсутствие дублирования кода
- Комментарии только там, где необходимо

### Testing First
- Unit тесты для всех бизнес-правил
- Интеграционные тесты для UI
- Покрытие тестами не менее 80%

## 🔍 Мониторинг и аналитика

### Метрики производительности
- Время запуска приложения
- Использование памяти
- Частота кадров (FPS)
- Размер APK

### Метрики качества
- Покрытие тестами
- Количество багов
- Время исправления багов
- Удовлетворенность пользователей

---

**Версия документации**: 1.87  
**Последнее обновление**: Июль 2023  
**Автор**: Команда разработки Financial Success Game