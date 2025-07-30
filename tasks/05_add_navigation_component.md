# Задача 5: Добавление Navigation Component

## 📋 Описание задачи

Внедрить Navigation Component для улучшения навигации между экранами, упрощения управления жизненным циклом фрагментов и обеспечения единообразного пользовательского опыта.

## 🎯 Цели

- Унифицировать навигацию между экранами
- Упростить передачу данных между экранами
- Улучшить управление жизненным циклом фрагментов
- Обеспечить поддержку Deep Links
- Создать единообразный пользовательский опыт

## 📁 Затронутые файлы

### Основные файлы:
- `app/src/main/java/com/financialsuccess/game/MainActivity.kt`
- `app/src/main/java/com/financialsuccess/game/GameActivity.kt`
- `app/src/main/java/com/financialsuccess/game/CharacterCreationActivity.kt`
- `app/src/main/java/com/financialsuccess/game/ProfessionSelectionActivity.kt`
- `app/src/main/java/com/financialsuccess/game/RulesActivity.kt`
- `app/src/main/AndroidManifest.xml`

### Новые файлы для создания:
- `app/src/main/res/navigation/nav_graph.xml`
- `app/src/main/java/com/financialsuccess/game/ui/main/MainFragment.kt`
- `app/src/main/java/com/financialsuccess/game/ui/game/GameFragment.kt`
- `app/src/main/java/com/financialsuccess/game/ui/character/CharacterCreationFragment.kt`
- `app/src/main/java/com/financialsuccess/game/ui/profession/ProfessionSelectionFragment.kt`
- `app/src/main/java/com/financialsuccess/game/ui/rules/RulesFragment.kt`
- `app/src/main/java/com/financialsuccess/game/navigation/GameNavigation.kt`

### Тестовые файлы:
- `app/src/test/MainFragmentTest.kt`
- `app/src/test/GameFragmentTest.kt`
- `app/src/test/CharacterCreationFragmentTest.kt`

## 🔍 Анализ связанных файлов

### Обязательный анализ:
1. **MainActivity.kt** - анализ текущей навигации
2. **GameActivity.kt** - анализ переходов в игре
3. **CharacterCreationActivity.kt** - анализ создания персонажа
4. **ProfessionSelectionActivity.kt** - анализ выбора профессии
5. **RulesActivity.kt** - анализ правил игры
6. **AndroidManifest.xml** - проверка Activity регистрации
7. **build.gradle** - проверка Navigation зависимостей

### Чек-лист анализа:
- [ ] Все переходы между Activity
- [ ] Все передаваемые данные между экранами
- [ ] Все Intent extras
- [ ] Все результаты Activity (startActivityForResult)
- [ ] Все обработчики кнопок навигации
- [ ] Все диалоги и всплывающие окна

## 🏗️ Архитектурные изменения

### 1. Добавление Navigation зависимостей в build.gradle
```gradle
dependencies {
    implementation "androidx.navigation:navigation-fragment-ktx:2.7.4"
    implementation "androidx.navigation:navigation-ui-ktx:2.7.4"
    implementation "androidx.navigation:navigation-safe-args-gradle-plugin:2.7.4"
}
```

### 2. Создание Navigation Graph
```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/nav_graph"
    app:startDestination="@id/mainFragment">

    <fragment
        android:id="@+id/mainFragment"
        android:name="com.financialsuccess.game.ui.main.MainFragment"
        android:label="Главная"
        tools:layout="@layout/fragment_main">
        
        <action
            android:id="@+id/action_main_to_profession_selection"
            app:destination="@id/professionSelectionFragment" />
            
        <action
            android:id="@+id/action_main_to_rules"
            app:destination="@id/rulesFragment" />
    </fragment>

    <fragment
        android:id="@+id/professionSelectionFragment"
        android:name="com.financialsuccess.game.ui.profession.ProfessionSelectionFragment"
        android:label="Выбор профессии"
        tools:layout="@layout/fragment_profession_selection">
        
        <action
            android:id="@+id/action_profession_selection_to_character_creation"
            app:destination="@id/characterCreationFragment">
            <argument
                android:name="profession"
                app:argType="com.financialsuccess.game.models.Profession" />
        </action>
    </fragment>

    <fragment
        android:id="@+id/characterCreationFragment"
        android:name="com.financialsuccess.game.ui.character.CharacterCreationFragment"
        android:label="Создание персонажа"
        tools:layout="@layout/fragment_character_creation">
        
        <argument
            android:name="profession"
            app:argType="com.financialsuccess.game.models.Profession" />
            
        <argument
            android:name="dream"
            app:argType="com.financialsuccess.game.models.Dream" />
            
        <action
            android:id="@+id/action_character_creation_to_game"
            app:destination="@id/gameFragment"
            app:popUpTo="@id/mainFragment"
            app:popUpToInclusive="true" />
    </fragment>

    <fragment
        android:id="@+id/gameFragment"
        android:name="com.financialsuccess.game.ui.game.GameFragment"
        android:label="Игра"
        tools:layout="@layout/fragment_game">
        
        <argument
            android:name="player"
            app:argType="com.financialsuccess.game.models.Player" />
    </fragment>

    <fragment
        android:id="@+id/rulesFragment"
        android:name="com.financialsuccess.game.ui.rules.RulesFragment"
        android:label="Правила"
        tools:layout="@layout/fragment_rules" />
</navigation>
```

### 3. Создание MainActivity с Navigation
```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupNavigation()
    }
    
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Настройка ActionBar с Navigation
        setupActionBarWithNavController(navController)
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
```

### 4. Создание MainFragment
```kotlin
class MainFragment : Fragment() {
    
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        binding.btnStartGame.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_profession_selection)
        }
        
        binding.btnRules.setOnClickListener {
            findNavController().navigate(R.id.action_main_to_rules)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

### 5. Создание ProfessionSelectionFragment
```kotlin
class ProfessionSelectionFragment : Fragment() {
    
    private var _binding: FragmentProfessionSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProfessionSelectionViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfessionSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        observeProfessions()
        
        binding.btnNext.setOnClickListener {
            val selectedProfession = viewModel.getSelectedProfession()
            if (selectedProfession != null) {
                val action = ProfessionSelectionFragmentDirections
                    .actionProfessionSelectionToCharacterCreation(selectedProfession)
                findNavController().navigate(action)
            }
        }
    }
    
    private fun setupRecyclerView() {
        val adapter = ProfessionAdapter { profession ->
            viewModel.selectProfession(profession)
        }
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
    }
    
    private fun observeProfessions() {
        viewModel.professions.observe(viewLifecycleOwner) { professions ->
            (binding.recyclerView.adapter as? ProfessionAdapter)?.submitList(professions)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

### 6. Создание GameFragment
```kotlin
class GameFragment : Fragment() {
    
    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GameViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Получаем переданного игрока
        val player = args.player
        viewModel.initializeGame(player)
        
        setupUI()
        observeGameState()
    }
    
    private fun setupUI() {
        binding.cardDice.setOnClickListener {
            viewModel.rollDice()
        }
        
        binding.btnFinancialStatement.setOnClickListener {
            showFinancialStatement()
        }
    }
    
    private fun observeGameState() {
        viewModel.gameState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

## ✅ Критерии приемки

### Функциональные требования:
- [ ] Все существующие переходы между экранами работают
- [ ] Передача данных между экранами функционирует
- [ ] Навигация назад работает корректно
- [ ] Deep Links поддерживаются
- [ ] Все диалоги и всплывающие окна работают
- [ ] ActionBar навигация функционирует

### Архитектурные требования:
- [ ] Navigation Component полностью внедрен
- [ ] Все Activity заменены на Fragment
- [ ] Navigation Graph правильно настроен
- [ ] Safe Args используются для передачи данных
- [ ] NavController правильно настроен
- [ ] Back Stack управляется корректно

### Тестируемость:
- [ ] Все Fragment покрыты unit-тестами
- [ ] Navigation тесты написаны
- [ ] Тесты проверяют передачу данных
- [ ] Тесты проверяют навигацию назад
- [ ] Тесты проверяют Deep Links

### Производительность:
- [ ] Фрагменты создаются и уничтожаются эффективно
- [ ] Нет утечек памяти при навигации
- [ ] View Binding работает корректно
- [ ] Lifecycle управляется правильно

## 🚨 Правила исполнения

### ОБЯЗАТЕЛЬНЫЕ ТРЕБОВАНИЯ:

#### 1. Полный анализ всех связанных файлов
- [ ] Проанализировать все переходы между Activity
- [ ] Изучить все передаваемые данные
- [ ] Проверить все Intent extras
- [ ] Проанализировать все результаты Activity
- [ ] Изучить все обработчики навигации

#### 2. Создать чек-лист проверок
- [ ] Синтаксис Navigation Graph
- [ ] Корректность Safe Args
- [ ] Правильность настройки NavController
- [ ] Корректность передачи данных
- [ ] Проверка Back Stack

#### 3. Предварительная проверка
- [ ] Синтаксис всех затрагиваемых файлов
- [ ] Структура и отступы
- [ ] Контекст изменений
- [ ] Возможные побочные эффекты

#### 4. Проверка критических областей
- [ ] Fragment lifecycle
- [ ] Navigation lifecycle
- [ ] Передача данных между экранами
- [ ] Back Stack management
- [ ] Deep Links

#### 5. Знание стандартов платформы
- [ ] Navigation Component best practices
- [ ] Fragment lifecycle
- [ ] Safe Args patterns
- [ ] Deep Links implementation
- [ ] Testing Navigation

### ПРЕДПОЛОЖЕНИЯ И ЗАВИСИМОСТИ:
- [ ] Navigation Component доступен
- [ ] Safe Args plugin настроен
- [ ] View Binding включен
- [ ] Fragment KTX доступен
- [ ] Все необходимые зависимости установлены

### ПРЕ-КОММИТ ПРОВЕРКИ:
- [ ] Автоматическая валидация синтаксиса XML
- [ ] Проверка форматирования кода
- [ ] Линтинг всех измененных файлов
- [ ] Проверка на типичные ошибки Navigation
- [ ] Валидация Navigation Graph

### ОТКАЗОУСТОЙЧИВОСТЬ:
- [ ] Обработка ошибок навигации
- [ ] Восстановление после сбоев
- [ ] Корректная очистка ресурсов
- [ ] Обработка некорректных данных

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

- **Анализ навигации**: 1 день
- **Создание Navigation Graph**: 1 день
- **Создание Fragment**: 3 дня
- **Настройка Safe Args**: 1 день
- **Тестирование**: 2 дня

**Общее время**: 8 дней