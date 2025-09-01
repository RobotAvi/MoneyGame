package com.financialsuccess.game

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.financialsuccess.game.adapters.AssetAdapter
import com.financialsuccess.game.data.GameDataManager
import com.financialsuccess.game.databinding.ActivityGameBinding
import com.financialsuccess.game.models.*
import java.text.NumberFormat
import java.util.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.os.Build
import android.media.MediaPlayer
import android.content.Intent
import androidx.recyclerview.widget.GridLayoutManager
import com.financialsuccess.game.adapters.CalendarAdapter
import android.media.SoundPool
import android.media.AudioAttributes
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import androidx.appcompat.widget.PopupMenu
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class GameActivity : AppCompatActivity() {
    
    private val viewModel: GameViewModel by viewModels()
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameManager: GameManager
    private lateinit var adaptiveMenuManager: AdaptiveMenuManager
    private var currentGameState: GameState? = null
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    private var lastDiceValue: Int? = null
    private var gamePlayer: MediaPlayer? = null

    private var soundPool: SoundPool? = null
    private var sfxDice: Int = 0
    private var sfxOk: Int = 0
    private var sfxError: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initSounds()
        
        // Удаляем меню с верхнего тулбара: никаких обработчиков не навешиваем

        // Observe event panel state from ViewModel
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.eventPanel.collectLatest { state ->
                    if (state == null) {
                        binding.eventMotion.transitionToStart()
                    } else {
                        binding.tvEventTitle.text = state.title
                        binding.tvEventMessage.text = state.message
                        binding.btnEventPrimary.visibility = if (state.primaryText != null && state.onPrimary != null) View.VISIBLE else View.GONE
                        binding.btnEventSecondary.visibility = if (state.secondaryText != null && state.onSecondary != null) View.VISIBLE else View.GONE
                        state.primaryText?.let { binding.btnEventPrimary.text = it }
                        state.secondaryText?.let { binding.btnEventSecondary.text = it }
                        binding.btnEventPrimary.setOnClickListener { state.onPrimary?.invoke(); viewModel.hideEventPanel() }
                        binding.btnEventSecondary.setOnClickListener { state.onSecondary?.invoke(); viewModel.hideEventPanel() }
                        binding.eventMotion.transitionToEnd()
                    }
                }
            }
        }

        initGame()
        setupUI()
        
        // Инициализируем адаптивное меню
        adaptiveMenuManager = AdaptiveMenuManager(this, binding, this)
        currentGameState?.let { gameState ->
            adaptiveMenuManager.initializeMenu(gameState.player)
        }
    }

    override fun onResume() { super.onResume(); if (gamePlayer == null) { gamePlayer = MediaPlayer.create(this, R.raw.game).apply { isLooping = true; setVolume(0.4f, 0.4f); start() } } else { gamePlayer?.start() } }
    override fun onPause() { super.onPause(); gamePlayer?.pause() }
    override fun onDestroy() { super.onDestroy(); gamePlayer?.release(); gamePlayer = null; soundPool?.release(); soundPool = null }
    
    override fun onBackPressed() {
        showExtendedBackMenu()
    }
    
    private fun showExtendedBackMenu() {
        val popupMenu = PopupMenu(this, binding.root)
        popupMenu.menuInflater.inflate(R.menu.game_actions, popupMenu.menu)
        
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_save -> {
                    saveGame()
                    true
                }
                R.id.menu_exit -> {
                    finish()
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }
    
    private fun initSounds() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(5)
                .setAudioAttributes(audioAttributes)
                .build()
        } else {
            soundPool = SoundPool(5, android.media.AudioManager.STREAM_MUSIC, 0)
        }
        
        // Загружаем звуки (если они есть)
        try {
            sfxDice = soundPool?.load(this, R.raw.dice_drop, 1) ?: 0
            sfxOk = soundPool?.load(this, R.raw.ok, 1) ?: 0
            sfxError = soundPool?.load(this, R.raw.error, 1) ?: 0
        } catch (e: Exception) {
            // Звуки могут отсутствовать
        }
    }
    
    private fun initGame() {
        gameManager = GameManager()
        
        // Загружаем сохраненную игру или создаем новую
        try {
            currentGameState = GameDataManager.GameSaveManager.loadGame(this)
            if (currentGameState == null) {
                currentGameState = createNewGame()
            }
        } catch (e: Exception) {
            // Если нет сохраненной игры, создаем новую
            currentGameState = createNewGame()
        }
        
        updateUI()
    }
    
    private fun createNewGame(): GameState {
        // Создаем базовую игру с дефолтными значениями
        val profession = Profession(
            id = "teacher",
            name = "Учитель",
            description = "Преподаватель в школе",
            salary = 50000,
            expenses = 30000,
            taxes = 5000,
            education = "Высшее"
        )
        
        val dream = Dream(
            id = "travel",
            name = "Путешествие по миру",
            description = "Посетить все континенты",
            cost = 1000000,
            cashFlowRequired = 50000
        )
        
        return gameManager.startNewGame(profession, dream, 25, "Игрок")
    }
    
    private fun setupUI() {
        // Настройка кнопок
        binding.btnNextTurn.setOnClickListener { performNextTurn() }
        
        // Кнопка следующий ход
        binding.btnNextTurn.setOnClickListener {
            performNextTurn()
        }
    }

    fun performNextTurn() {
        // Выполнить следующий ход
        currentGameState?.let { gameState ->
            // Обновляем счетчики для системы уровней
            gameState.player.updateLevelCounters()
            
            // Выполняем ход
            val diceValue = gameManager.rollDice()
            gameManager.movePlayer(diceValue)
            
            // Обновляем уровень игрока
            gameState.player.updateLevel()
            
            // Обновляем адаптивное меню
            adaptiveMenuManager.updateMenu(gameState.player)
            
            // Проверяем повышение уровня
            adaptiveMenuManager.checkLevelUp()
            
            updateUI()
            saveGame()
        }
    }
    
    private fun updateUI() {
        currentGameState?.let { gameState ->
            updatePlayerInfo(gameState.player)
            updateGameBoard(gameState)
            updateLifeProgress()
        }
    }
    
    private fun updatePlayerInfo(player: Player) {
        // Обновляем информацию игрока в соответствии с реальным layout
        binding.tvPlayerNameAge.text = "${player.name ?: "Игрок"}, ${player.age} лет"
        
        // Обновляем цель
        val goalText = "Цель: ${player.dream.name} (${currencyFormat.format(player.dream.cost)})"
        binding.tvFinancialGoal.text = goalText
        
        // Обновляем шкалу жизни
        updateLifeProgress()
        
        // Обновляем отображение уровня
        updateLevelDisplay(player)
    }
    
    private fun updateLevelDisplay(player: Player) {
        val levelInfo = player.getLevelInfo()
        // Здесь можно добавить обновление TextView для отображения уровня
        // Например: binding.tvLevelInfo?.text = levelInfo
    }
    
    private fun updateGameBoard(gameState: GameState) {
        val player = gameState.player
        
        // Обновляем информацию о текущем месяце и годе
        val currentMonth = (player.monthsPlayed % 12) + 1
        val currentYear = 2024 + (player.monthsPlayed / 12)
        
        // Обновляем календарь
        updateCalendar(currentMonth, currentYear)
    }
    
    private fun updateCalendar(month: Int, year: Int) {
        // Обновляем календарь если есть RecyclerView
        // binding.recyclerViewCalendar?.let { recyclerView ->
        //     val calendarAdapter = CalendarAdapter(month, year)
        //     recyclerView.adapter = calendarAdapter
        //     recyclerView.layoutManager = GridLayoutManager(this, 7)
        // }
    }
    
    private fun updateLifeProgress() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val lifeProgress = (player.getNetWorth() * 100 / player.dream.cost).coerceAtMost(100)
            
            // Обновляем прогресс если есть ProgressBar
            // binding.progressBarLife?.progress = lifeProgress
            // binding.tvLifeProgress?.text = "$lifeProgress%"
        }
    }
    
    private fun rollDice() {
        val diceValue = (1..6).random()
        lastDiceValue = diceValue
        
        // Анимация кубика
        // binding.ivDice?.setImageResource(getDiceResource(diceValue))
        
        // Звук кубика
        soundPool?.play(sfxDice, 1f, 1f, 1, 0, 1f)
        
        // Показываем результат
        Toast.makeText(this, "Выпало: $diceValue", Toast.LENGTH_SHORT).show()
    }
    
    private fun getDiceResource(value: Int): Int {
        return when (value) {
            1 -> R.drawable.ic_calendar_rest
            2 -> R.drawable.ic_calendar_work
            3 -> R.drawable.ic_calendar_game
            4 -> R.drawable.ic_calendar_finance
            5 -> R.drawable.ic_action_report
            6 -> R.drawable.ic_action_market
            else -> R.drawable.ic_calendar_rest
        }
    }
    
    private fun saveGame() {
        currentGameState?.let { gameState ->
            try {
                GameDataManager.GameSaveManager.saveGame(this, gameState)
                Toast.makeText(this, "Игра сохранена", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Ошибка сохранения: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun getCurrentMonthText(month: Int): String {
        val monthNames = arrayOf(
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
        )
        return monthNames[month - 1]
    }

    // === МЕТОДЫ ДЛЯ АДАПТИВНОГО МЕНЮ ===
    
    // Финансы и инвестиции
    fun showIncomeDialog() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                💰 Доходы:
                
                Зарплата: ${currencyFormat.format(player.salary)}
                Пассивный доход: ${currencyFormat.format(player.passiveIncome)}
                Общий доход: ${currencyFormat.format(player.totalIncome)}
                
                Денежный поток: ${currencyFormat.format(player.getCashFlow())}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("💰 Доходы")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showExpensesDialog() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                💸 Расходы:
                
                Общие расходы: ${currencyFormat.format(player.totalExpenses)}
                Налоги: ${currencyFormat.format(player.taxes)}
                Жилье: ${currencyFormat.format(player.housingExpenses)}
                Транспорт: ${currencyFormat.format(player.transportExpenses)}
                Питание: ${currencyFormat.format(player.foodExpenses)}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("💸 Расходы")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showBalancePanel() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                💼 Баланс:
                
                Наличные: ${currencyFormat.format(player.cash)}
                Активы: ${currencyFormat.format(player.assets.sumOf { it.value })}
                Инвестиции: ${currencyFormat.format(player.investments.sumOf { it.cost })}
                
                Последние записи: ${player.financialJournal.size}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("💼 Баланс")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showFinancialJournal() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val entries = player.financialJournal.takeLast(10)
            val message = if (entries.isNotEmpty()) {
                entries.joinToString("\n") { entry ->
                    "${entry.realDate}: ${entry.description} - ${currencyFormat.format(entry.amount)}"
                }
            } else {
                "Записей пока нет"
            }
            
            AlertDialog.Builder(this)
                .setTitle("📊 Финансовый журнал")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showJournalAnalytics() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                📈 Аналитика:
                
                Общий доход: ${currencyFormat.format(player.totalEarned)}
                Общие расходы: ${currencyFormat.format(player.totalSpent)}
                Чистая прибыль: ${currencyFormat.format(player.totalEarned - player.totalSpent)}
                Количество сделок: ${player.financialJournal.size}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📈 Аналитика журнала")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showFinancialStatement() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                📋 Финансовый отчет:
                
                Активы:
                - Наличные: ${currencyFormat.format(player.cash)}
                - Активы: ${currencyFormat.format(player.assets.sumOf { it.value })}
                - Инвестиции: ${currencyFormat.format(player.investments.sumOf { it.cost })}
                
                Пассивы:
                - Обязательства: ${currencyFormat.format(player.liabilities.sumOf { it.amount })}
                
                Чистая стоимость: ${currencyFormat.format(player.getNetWorth())}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📋 Финансовый отчет")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showMarketDialog() {
        currentGameState?.let { gameState ->
            val message = """
                📈 Рынок:
                
                Доступные активы:
                - Недвижимость
                - Акции
                - Бизнес
                - Криптовалюты
                
                Требуется уровень: Инвестор
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📈 Рынок")
                .setMessage(message)
                .setPositiveButton("Просмотреть активы") { _, _ ->
                    showAvailableAssets()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showAvailableAssets() {
        currentGameState?.let { gameState ->
            val message = """
                🏠 Доступные активы:
                
                Недвижимость:
                - Квартира (50,000₽)
                - Дом (200,000₽)
                - Коммерческая недвижимость (500,000₽)
                
                Акции:
                - Дивидендные акции (10,000₽)
                - Ростовые акции (25,000₽)
                
                Требуется уровень: Инвестор
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🏠 Доступные активы")
                .setMessage(message)
                .setPositiveButton("Просмотреть инвестиции") { _, _ ->
                    showAvailableInvestments()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showAssetDetails() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = if (player.assets.isNotEmpty()) {
                player.assets.joinToString("\n\n") { asset ->
                    """
                    ${asset.name}:
                    - Стоимость: ${currencyFormat.format(asset.value)}
                    - Денежный поток: ${currencyFormat.format(asset.cashFlow)}
                    - Первоначальный взнос: ${currencyFormat.format(asset.downPayment)}
                    - Кредит: ${currencyFormat.format(asset.loan)}
                    """.trimIndent()
                }
            } else {
                "У вас пока нет активов"
            }
            
            AlertDialog.Builder(this)
                .setTitle("🏠 Детали активов")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showAvailableInvestments() {
        currentGameState?.let { gameState ->
            val message = """
                💹 Доступные инвестиции:
                
                Облигации:
                - Государственные облигации (5,000₽)
                - Корпоративные облигации (15,000₽)
                
                Фонды:
                - Индексный фонд (20,000₽)
                - ETF (30,000₽)
                
                Требуется уровень: Инвестор
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("💹 Доступные инвестиции")
                .setMessage(message)
                .setPositiveButton("Портфель") { _, _ ->
                    showPortfolio()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showInvestmentDetails() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = if (player.investments.isNotEmpty()) {
                player.investments.joinToString("\n\n") { investment ->
                    """
                    ${investment.name}:
                    - Стоимость: ${currencyFormat.format(investment.cost)}
                    - Ожидаемая доходность: ${currencyFormat.format(investment.expectedReturn)}
                    - Уровень риска: ${investment.riskLevel}
                    """.trimIndent()
                }
            } else {
                "У вас пока нет инвестиций"
            }
            
            AlertDialog.Builder(this)
                .setTitle("💹 Детали инвестиций")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showPortfolio() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val totalAssets = player.assets.sumOf { it.value }
            val totalInvestments = player.investments.sumOf { it.cost }
            val totalValue = totalAssets + totalInvestments
            
            val message = """
                📊 Портфель:
                
                Активы: ${currencyFormat.format(totalAssets)}
                Инвестиции: ${currencyFormat.format(totalInvestments)}
                Общая стоимость: ${currencyFormat.format(totalValue)}
                
                Распределение:
                - Недвижимость: ${if (totalValue > 0) (player.assets.filter { it.type == AssetType.REAL_ESTATE }.sumOf { it.value } * 100 / totalValue).toInt() else 0}%
                - Акции: ${if (totalValue > 0) (player.assets.filter { it.type == AssetType.STOCKS }.sumOf { it.value } * 100 / totalValue).toInt() else 0}%
                - Облигации: ${if (totalValue > 0) (player.investments.filter { it.type == AssetType.BONDS }.sumOf { it.cost } * 100 / totalValue).toInt() else 0}%
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📊 Портфель")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    // Здоровье и образ жизни
    fun showHealthStatus() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                🏥 Статус здоровья:
                
                Возраст: ${player.age} лет
                Ожидаемая продолжительность жизни: ${player.deathAge} лет
                Осталось лет: ${player.deathAge - player.age}
                
                Факторы здоровья:
                - Образование: ${player.education.displayName}
                - Профессия: ${player.profession.name}
                - Навыки: ${player.skills.size}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🏥 Статус здоровья")
                .setMessage(message)
                .setPositiveButton("Варианты страхования") { _, _ ->
                    showHealthcareOptions()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showHealthcareOptions() {
        currentGameState?.let { gameState ->
            val message = """
                🏥 Варианты страхования:
                
                Базовое страхование:
                - Медицинская страховка (2,000₽/мес)
                - Стоматологическая страховка (1,500₽/мес)
                
                Расширенное страхование:
                - Полная медицинская страховка (5,000₽/мес)
                - Страхование жизни (3,000₽/мес)
                
                Требуется уровень: Автономный
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🏥 Варианты страхования")
                .setMessage(message)
                .setPositiveButton("Купить страховку") { _, _ ->
                    buyHealthcare()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun buyHealthcare() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val cost = 2000 // Базовая медицинская страховка
            
            if (player.cash >= cost) {
                player.cash -= cost
                player.totalExpenses += cost
                
                AlertDialog.Builder(this)
                    .setTitle("✅ Страховка куплена")
                    .setMessage("Вы приобрели базовую медицинскую страховку за ${currencyFormat.format(cost)}")
                    .setPositiveButton("OK") { _, _ ->
                        updateUI()
                    }
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("❌ Недостаточно средств")
                    .setMessage("Для покупки страховки нужно ${currencyFormat.format(cost)}")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }
    
    fun showHabitsAndFamilyInfluence() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                👨‍👩‍👧‍👦 Привычки и влияние семьи:
                
                Текущие привычки:
                - Финансовая грамотность: ${if (player.education != EducationLevel.HIGH_SCHOOL) "Высокая" else "Низкая"}
                - Инвестиционные навыки: ${if (player.assets.isNotEmpty() || player.investments.isNotEmpty()) "Развиты" else "Не развиты"}
                - Планирование: ${if (player.cash > 10000) "Хорошее" else "Требует улучшения"}
                
                Влияние семьи:
                - Поддержка в образовании: ${if (player.education != EducationLevel.HIGH_SCHOOL) "Есть" else "Нет"}
                - Финансовые традиции: "Передается из поколения в поколение"
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("👨‍👩‍👧‍👦 Привычки и влияние семьи")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showHealthAndFamilyExpenses() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                💊 Расходы на здоровье и семью:
                
                Текущие расходы:
                - Медицинские расходы: ${currencyFormat.format(player.totalExpenses * 0.1)}
                - Семейные расходы: ${currencyFormat.format(player.totalExpenses * 0.2)}
                - Образование детей: ${currencyFormat.format(player.totalExpenses * 0.15)}
                
                Рекомендации:
                - Откладывать 10% на медицинские нужды
                - Планировать семейные расходы
                - Инвестировать в образование
                
                Требуется уровень: Автономный
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("💊 Расходы на здоровье и семью")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showFamilyExpenses() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                👨‍👩‍👧‍👦 Семейные расходы:
                
                Текущие расходы:
                - Питание семьи: ${currencyFormat.format(player.foodExpenses)}
                - Жилье: ${currencyFormat.format(player.housingExpenses)}
                - Транспорт: ${currencyFormat.format(player.transportExpenses)}
                - Развлечения: ${currencyFormat.format(player.totalExpenses * 0.1)}
                
                Планирование:
                - Бюджет на детей: ${currencyFormat.format(player.totalExpenses * 0.2)}
                - Образование: ${currencyFormat.format(player.totalExpenses * 0.15)}
                - Отдых: ${currencyFormat.format(player.totalExpenses * 0.1)}
                
                Требуется уровень: Инвестор
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("👨‍👩‍👧‍👦 Семейные расходы")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showAgeStatistics() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                📊 Возрастная статистика:
                
                Текущий возраст: ${player.age} лет
                Ожидаемая продолжительность жизни: ${player.deathAge} лет
                Осталось лет: ${player.deathAge - player.age}
                
                Финансовые цели по возрастам:
                - 25-30: Накопить первый капитал
                - 30-40: Инвестировать в активы
                - 40-50: Развивать пассивный доход
                - 50+: Финансовая независимость
                
                Ваш прогресс: ${(player.getNetWorth() * 100 / player.dream.cost).coerceAtMost(100)}%
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📊 Возрастная статистика")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    // Возможности и развитие
    fun showEducationSkills() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                🎓 Образование и навыки:
                
                Текущее образование: ${player.education.displayName}
                Навыки: ${player.skills.joinToString(", ") { it.name }}
                
                Доступные курсы:
                - Финансовая грамотность (5,000₽)
                - Инвестиции для начинающих (8,000₽)
                - Управление бизнесом (12,000₽)
                - Программирование (15,000₽)
                
                Рекомендации:
                - Изучать финансовую грамотность
                - Развивать профессиональные навыки
                - Инвестировать в образование
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🎓 Образование и навыки")
                .setMessage(message)
                .setPositiveButton("Курсы и тренинги") { _, _ ->
                    showCoursesTraining()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showCoursesTraining() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                📚 Курсы и тренинги:
                
                Доступные курсы:
                - Финансовая грамотность (5,000₽)
                - Инвестиции для начинающих (8,000₽)
                - Управление бизнесом (12,000₽)
                - Программирование (15,000₽)
                
                Ваши навыки: ${player.skills.joinToString(", ") { it.name }}
                
                Рекомендации:
                - Начать с финансовой грамотности
                - Затем изучить инвестиции
                - Развивать профессиональные навыки
                
                Требуется уровень: Автономный
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📚 Курсы и тренинги")
                .setMessage(message)
                .setPositiveButton("Университет") { _, _ ->
                    showUniversity()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showUniversity() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                🎓 Университет:
                
                Доступные программы:
                - Бакалавриат (200,000₽)
                - Магистратура (300,000₽)
                - MBA (500,000₽)
                
                Ваше образование: ${player.education.displayName}
                
                Преимущества высшего образования:
                - Повышение зарплаты
                - Лучшие карьерные возможности
                - Доступ к престижным профессиям
                - Развитие критического мышления
                
                Требуется уровень: Автономный
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🎓 Университет")
                .setMessage(message)
                .setPositiveButton("Личные отношения") { _, _ ->
                    showPersonalRelationships()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showPersonalRelationships() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                💕 Личные отношения:
                
                Текущий статус: ${if (player.age > 25) "В поисках партнера" else "Фокус на развитии"}
                
                Варианты развития:
                - Знакомства и свидания
                - Создание семьи
                - Развитие отношений
                - Семейное планирование
                
                Влияние на финансы:
                - Совместные расходы
                - Планирование бюджета
                - Инвестиции в будущее семьи
                
                Требуется уровень: Автономный
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("💕 Личные отношения")
                .setMessage(message)
                .setPositiveButton("Создание семьи") { _, _ ->
                    showFamilyCreation()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showFamilyCreation() {
        currentGameState?.let { gameState ->
            val message = """
                👨‍👩‍👧‍👦 Создание семьи:
                
                Варианты:
                - Брак (50,000₽)
                - Рождение ребенка (100,000₽)
                - Покупка семейного жилья (500,000₽)
                
                Финансовые аспекты:
                - Совместный бюджет
                - Планирование расходов
                - Инвестиции в будущее детей
                - Страхование семьи
                
                Требуется уровень: Инвестор
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("👨‍👩‍👧‍👦 Создание семьи")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showCareerBusiness() {
        currentGameState?.let { gameState ->
            val player = gameState.player
            val message = """
                💼 Карьера и бизнес:
                
                Текущая профессия: ${player.profession.name}
                Зарплата: ${currencyFormat.format(player.salary)}
                
                Варианты развития:
                - Поиск новой работы
                - Карьерный рост
                - Создание бизнеса
                - Фриланс
                
                Рекомендации:
                - Развивать профессиональные навыки
                - Искать возможности роста
                - Рассматривать предпринимательство
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("💼 Карьера и бизнес")
                .setMessage(message)
                .setPositiveButton("Развитие бизнеса") { _, _ ->
                    showBusinessDevelopment()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showBusinessDevelopment() {
        currentGameState?.let { gameState ->
            val message = """
                🚀 Развитие бизнеса:
                
                Варианты бизнеса:
                - Онлайн-бизнес (50,000₽)
                - Розничная торговля (200,000₽)
                - Услуги (100,000₽)
                - Производство (500,000₽)
                
                Этапы развития:
                1. Идея и планирование
                2. Регистрация и запуск
                3. Развитие и масштабирование
                4. Выход на рынок
                
                Требуется уровень: Пассивный доход
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🚀 Развитие бизнеса")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    fun showOtherOpportunities() {
        currentGameState?.let { gameState ->
            val message = """
                🌟 Прочие возможности:
                
                Варианты развития:
                - Хобби и увлечения
                - Путешествия
                - Проекты
                - Благотворительность
                
                Финансовые возможности:
                - Побочный доход
                - Инвестиции в хобби
                - Монетизация увлечений
                - Социальные проекты
                
                Рекомендации:
                - Развивать увлечения
                - Искать новые возможности
                - Балансировать работу и жизнь
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🌟 Прочие возможности")
                .setMessage(message)
                .setPositiveButton("Благотворительность") { _, _ ->
                    showPhilanthropy()
                }
                .setNegativeButton("Отмена", null)
                .show()
        }
    }
    
    fun showPhilanthropy() {
        currentGameState?.let { gameState ->
            val message = """
                🤝 Благотворительность:
                
                Варианты помощи:
                - Денежные пожертвования
                - Волонтерство
                - Создание фондов
                - Социальные проекты
                
                Финансовые аспекты:
                - Налоговые льготы
                - Социальная ответственность
                - Наследие и репутация
                
                Требуется уровень: Финансовый мастер
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("🤝 Благотворительность")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
}