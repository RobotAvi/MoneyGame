# Задача 1: Рефакторинг GameActivity

## 📋 Описание задачи

Разбить монолитную `GameActivity.kt` (1183 строки) на отдельные компоненты для улучшения читаемости, тестируемости и поддержки кода.

## 🎯 Цели

- Уменьшить размер GameActivity до < 300 строк
- Разделить UI-логику и бизнес-логику
- Улучшить тестируемость компонентов
- Соблюсти принцип единственной ответственности

## 📁 Затронутые файлы

### Основные файлы:
- `app/src/main/java/com/financialsuccess/game/GameActivity.kt`
- `app/src/main/java/com/financialsuccess/game/GameManager.kt`

### Новые файлы для создания:
- `app/src/main/java/com/financialsuccess/game/ui/game/GameViewModel.kt`
- `app/src/main/java/com/financialsuccess/game/ui/game/GameBoardFragment.kt`
- `app/src/main/java/com/financialsuccess/game/ui/game/FinancialPanelFragment.kt`
- `app/src/main/java/com/financialsuccess/game/ui/game/AssetManagementFragment.kt`
- `app/src/main/java/com/financialsuccess/game/ui/game/GameFragment.kt`

### Тестовые файлы:
- `app/src/test/GameViewModelTest.kt`
- `app/src/test/GameBoardFragmentTest.kt`
- `app/src/test/FinancialPanelFragmentTest.kt`

## 🔍 Анализ связанных файлов

### Обязательный анализ:
1. **GameActivity.kt** - полный анализ всех методов
2. **GameManager.kt** - проверка взаимодействия с Activity
3. **Player.kt** - анализ модели данных
4. **GameState.kt** - проверка состояния игры
5. **ActivityGameBinding.kt** - анализ View Binding
6. **build.gradle** - проверка зависимостей

### Чек-лист анализа:
- [ ] Все методы в GameActivity (rollDiceAndMove, showFinancialStatement, updateUI, etc.)
- [ ] Все переменные состояния
- [ ] Все обработчики событий
- [ ] Все зависимости от GameManager
- [ ] Все UI-элементы и их обновления
- [ ] Все диалоги и всплывающие окна
- [ ] Все навигационные переходы
- [ ] Все сохранения/загрузки состояния

## 🏗️ Архитектурные изменения

### 1. Создание ViewModel
```kotlin
class GameViewModel : ViewModel() {
    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState
    
    private val gameManager = GameManager()
    
    fun rollDice() {
        val diceValue = gameManager.rollDice()
        val newState = gameManager.movePlayer(diceValue)
        _gameState.value = newState
    }
    
    fun buyAsset(asset: Asset): Boolean {
        val success = gameManager.buyAsset(asset)
        if (success) {
            _gameState.value = gameManager.getCurrentState()
        }
        return success
    }
}
```

### 2. Разбиение на фрагменты
```kotlin
// GameBoardFragment.kt - игровое поле и кубик
class GameBoardFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.cardDice.setOnClickListener {
            viewModel.rollDice()
        }
        
        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            updateGameBoard(state)
        }
    }
}

// FinancialPanelFragment.kt - финансовая информация
class FinancialPanelFragment : Fragment() {
    private val viewModel: GameViewModel by activityViewModels()
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            updateFinancialInfo(state.player)
        }
    }
}
```

### 3. Обновленная GameActivity
```kotlin
class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val viewModel: GameViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initGame()
        setupNavigation()
        
        viewModel.gameState.observe(this) { state ->
            updateUI(state)
        }
    }
    
    private fun initGame() {
        // Инициализация игры через ViewModel
        val player = intent.getParcelableExtra("player", Player::class.java)
        viewModel.initializeGame(player)
    }
    
    private fun setupNavigation() {
        // Настройка навигации между фрагментами
    }
    
    private fun updateUI(gameState: GameState) {
        // Минимальная логика обновления UI
    }
}
```

## ✅ Критерии приемки

### Функциональные требования:
- [ ] Все существующие функции работают без изменений
- [ ] Игра запускается и работает корректно
- [ ] Бросание кубика функционирует
- [ ] Покупка активов работает
- [ ] Финансовые отчеты отображаются
- [ ] Сохранение игры работает
- [ ] Все диалоги и всплывающие окна функционируют

### Архитектурные требования:
- [ ] GameActivity содержит < 300 строк кода
- [ ] Бизнес-логика вынесена в ViewModel
- [ ] UI-логика разделена по фрагментам
- [ ] Каждый компонент имеет единственную ответственность
- [ ] ViewModel не содержит прямых ссылок на View
- [ ] Фрагменты не содержат бизнес-логики

### Тестируемость:
- [ ] ViewModel покрыт unit-тестами на 90%+
- [ ] Фрагменты покрыты интеграционными тестами
- [ ] Все публичные методы ViewModel протестированы
- [ ] Тесты проверяют корректность обновления UI

### Производительность:
- [ ] Нет утечек памяти в ViewModel
- [ ] LiveData правильно очищается при уничтожении Activity
- [ ] Фрагменты не создают циклических ссылок
- [ ] Обновления UI происходят в главном потоке

## 🚨 Правила исполнения

### ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ:

#### 1. Полный анализ всех связанных файлов
- [ ] Проанализировать все методы GameActivity
- [ ] Изучить все зависимости GameManager
- [ ] Проверить все View Binding элементы
- [ ] Проанализировать все обработчики событий
- [ ] Изучить все диалоги и всплывающие окна

#### 2. Создать чек-лист проверок
- [ ] Синтаксис всех новых файлов
- [ ] Корректность импортов
- [ ] Правильность View Binding
- [ ] Корректность LiveData обновлений
- [ ] Проверка утечек памяти

#### 3. Предварительная проверка
- [ ] Синтаксис всех затрагиваемых файлов
- [ ] Структура и отступы
- [ ] Контекст изменений
- [ ] Возможные побочные эффекты

#### 4. Проверка критических областей
- [ ] Activity lifecycle
- [ ] Fragment lifecycle
- [ ] ViewModel lifecycle
- [ ] LiveData обновления
- [ ] View Binding
- [ ] Навигация между фрагментами

#### 5. Знание стандартов платформы
- [ ] Android Architecture Components
- [ ] MVVM паттерн
- [ ] Fragment lifecycle
- [ ] ViewModel best practices
- [ ] LiveData best practices

### ПРЕДПОЛОЖЕНИЯ И ЗАВИСИМОСТИ:
- [ ] AndroidX компоненты доступны
- [ ] View Binding включен в build.gradle
- [ ] Lifecycle компоненты установлены
- [ ] Fragment KTX доступен
- [ ] ViewModel KTX доступен

### ПРЕ-КОММИТ ПРОВЕРКИ:
- [ ] Автоматическая валидация синтаксиса Kotlin
- [ ] Проверка форматирования кода
- [ ] Линтинг всех измененных файлов
- [ ] Проверка на типичные ошибки Android

### ОТКАЗОУСТОЙЧИВОСТЬ:
- [ ] Обработка ошибок в ViewModel
- [ ] Восстановление после сбоев Activity
- [ ] Корректная очистка ресурсов
- [ ] Обработка конфигурационных изменений

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

- **Анализ существующего кода**: 1 день
- **Создание ViewModel**: 1 день
- **Разбиение на фрагменты**: 2 дня
- **Тестирование**: 1 день
- **Рефакторинг и оптимизация**: 1 день

**Общее время**: 6 дней