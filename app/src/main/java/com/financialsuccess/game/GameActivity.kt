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
import com.financialsuccess.game.data.GameSaveManager
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

class GameActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameManager: GameManager
    private var currentGameState: GameState? = null
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    private var lastDiceValue: Int? = null
    private var gamePlayer: MediaPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_save -> {
                    currentGameState?.let { GameSaveManager.saveGameState(this, it) }
                    currentGameState?.player?.let { GameSaveManager.savePlayer(this, it) }
                    Toast.makeText(this, "Игра сохранена!", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_rules -> { startActivity(android.content.Intent(this, RulesActivity::class.java)); true }
                R.id.menu_exit -> { finish(); true }
                else -> false
            }
        }

        initGame()
        setupUI()
    }

    override fun onResume() { super.onResume(); if (gamePlayer == null) { gamePlayer = MediaPlayer.create(this, R.raw.game).apply { isLooping = true; setVolume(0.4f, 0.4f); start() } } else { gamePlayer?.start() } }
    override fun onPause() { super.onPause(); gamePlayer?.pause() }
    override fun onDestroy() { super.onDestroy(); gamePlayer?.release(); gamePlayer = null }
    
    private fun initGame() {
        val player: Player? = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("player", Player::class.java)
        } else { @Suppress("DEPRECATION") intent.getParcelableExtra("player") }
        if (player != null) {
            gameManager = GameManager()
            currentGameState = gameManager.startNewGameWithPlayer(player)
            updateUI()
        } else {
            val profession: Profession? = if (Build.VERSION.SDK_INT >= 33) { intent.getParcelableExtra("profession", Profession::class.java) } else { @Suppress("DEPRECATION") intent.getParcelableExtra("profession") }
            val dream: Dream? = if (Build.VERSION.SDK_INT >= 33) { intent.getParcelableExtra("dream", Dream::class.java) } else { @Suppress("DEPRECATION") intent.getParcelableExtra("dream") }
            val playerAge = intent.getIntExtra("playerAge", 25)
            val playerName = intent.getStringExtra("playerName")
            val startDateMillis = intent.getLongExtra("startDate", 0L).takeIf { it != 0L }
            if (profession != null && dream != null) {
                gameManager = GameManager()
                currentGameState = gameManager.startNewGame(profession, dream, playerAge, playerName, startDateMillis)
                updateUI()
            } else finish()
        }
    }
    
    private fun setupUI() {
        binding.btnDice.setOnClickListener { rollDiceAndMove() }
        binding.btnChart.setOnClickListener { showBalancePanel() }
        binding.btnUp.setOnClickListener { showActionsMenu() }
        binding.btnDown.setOnClickListener { currentGameState?.player?.let { it.salary = (it.salary - 5000).coerceAtLeast(0); it.updateTotalIncome(); showMessage("Понижение: -${currencyFormat.format(5000)} к зарплате"); updateUI() } }
        binding.tvAge.setOnClickListener { showAgeStatistics() }
        setupAssetsRecyclerView()
        setupCalendarRecycler()
    }

    private fun showActionsMenu() {
        val options = arrayOf("📦 Активы", "🏪 Рынок", "📊 Портфель")
        AlertDialog.Builder(this)
            .setTitle("Действия")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAssets()
                    1 -> showMarketDialog()
                    2 -> showPortfolio()
                }
            }
            .show()
    }

    private fun showMarketDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_market, null)
        val recycler = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewMarket)
        val btnAssets = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnFilterAssets)
        val btnInvest = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnFilterInvestments)
        val btnRefresh = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnRefreshMarket)
        recycler.layoutManager = LinearLayoutManager(this)

        fun loadAssets() { showAvailableAssets() }
        fun loadInvestments() { showAvailableInvestments() }

        val dialog = AlertDialog.Builder(this)
            .setTitle("🏪 Финансовый рынок")
            .setView(view)
            .setNegativeButton("Закрыть", null)
            .create()

        btnAssets.setOnClickListener { dialog.dismiss(); loadAssets() }
        btnInvest.setOnClickListener { dialog.dismiss(); loadInvestments() }
        btnRefresh.setOnClickListener { dialog.dismiss(); showMarket() }

        dialog.show()
    }
    
    private fun showBalancePanel() {
        val player = currentGameState?.player ?: return
        val dialogView = layoutInflater.inflate(R.layout.dialog_balance_panel, null)
        val tvCash = dialogView.findViewById<android.widget.TextView>(R.id.tvBalanceCash)
        val tvSalary = dialogView.findViewById<android.widget.TextView>(R.id.tvBalanceSalary)
        val tvPassive = dialogView.findViewById<android.widget.TextView>(R.id.tvBalancePassive)
        val tvExpenses = dialogView.findViewById<android.widget.TextView>(R.id.tvBalanceExpenses)
        val tvCashFlow = dialogView.findViewById<android.widget.TextView>(R.id.tvBalanceCashFlow)
        val btnJournal = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnOpenJournal)
        val btnAnalytics = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnOpenAnalytics)
        val btnOldReport = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnOpenOldReport)

        tvCash.text = "Наличные: ${currencyFormat.format(player.cash)}"
        tvSalary.text = "Зарплата: ${currencyFormat.format(player.salary)}"
        tvPassive.text = "Пассивный доход: ${currencyFormat.format(player.passiveIncome)}"
        tvExpenses.text = "Расходы: ${currencyFormat.format(player.totalExpenses)}"
        tvCashFlow.text = "Денежный поток: ${currencyFormat.format(player.getCashFlow())}"

        val dialog = AlertDialog.Builder(this)
            .setTitle("📊 Финансовая сводка")
            .setView(dialogView)
            .setPositiveButton("Закрыть", null)
            .create()

        btnJournal.setOnClickListener { dialog.dismiss(); showFinancialJournal() }
        btnAnalytics.setOnClickListener { dialog.dismiss(); showJournalAnalytics() }
        btnOldReport.setOnClickListener { dialog.dismiss(); showFinancialStatement() }

        dialog.show()
    }
    
    private fun realPlayerCalendar(player: Player): Calendar = Calendar.getInstance().apply {
        if (player.startDateMillis != null) timeInMillis = player.startDateMillis!! else set(2024, Calendar.JANUARY, 1)
        add(Calendar.MONTH, player.monthsPlayed)
        set(Calendar.DAY_OF_MONTH, player.currentDayOfMonth)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }

    private fun weekStart(cal: Calendar): Calendar {
        val c = cal.clone() as Calendar
        // Понедельник — начало недели
        while (c.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            c.add(Calendar.DAY_OF_MONTH, -1)
        }
        return c
    }

    private fun buildFourWeekWindow(anchor: Calendar): List<Calendar> {
        val startOfCurrentWeek = weekStart(anchor)
        val startOfPrevWeek = (startOfCurrentWeek.clone() as Calendar).apply { add(Calendar.DAY_OF_MONTH, -7) }
        val dates = mutableListOf<Calendar>()
        // 4 недели: пред + текущая + 2 будущих
        var cursor = startOfPrevWeek.clone() as Calendar
        repeat(28) {
            dates.add((cursor.clone() as Calendar))
            cursor.add(Calendar.DAY_OF_MONTH, 1)
        }
        return dates
    }

    private var calendarAnchor: Calendar? = null

    private fun setupCalendarRecycler() {
        binding.recyclerCalendar.layoutManager = GridLayoutManager(this, 7)
        val player = currentGameState?.player ?: return
        if (calendarAnchor == null) calendarAnchor = realPlayerCalendar(player)
        val currentCal = realPlayerCalendar(player)
        val dates = buildFourWeekWindow(calendarAnchor!!)

        val iconProvider: (Calendar) -> Int = { date ->
            when (date.get(Calendar.DAY_OF_MONTH) % 6) {
                0 -> R.drawable.ic_finance_logo
                1 -> R.drawable.ic_runner
                2 -> R.drawable.ic_businessman_successful
                3 -> R.drawable.ic_car
                4 -> R.drawable.ic_engineer_successful
                else -> R.drawable.ic_teacher
            }
        }
        val typeProvider: (Calendar) -> com.financialsuccess.game.adapters.CalendarAdapter.DayType = { date ->
            when (date.get(Calendar.DAY_OF_MONTH) % 4) {
                0 -> com.financialsuccess.game.adapters.CalendarAdapter.DayType.FINANCE
                1 -> com.financialsuccess.game.adapters.CalendarAdapter.DayType.WORK
                2 -> com.financialsuccess.game.adapters.CalendarAdapter.DayType.GAME
                else -> com.financialsuccess.game.adapters.CalendarAdapter.DayType.REST
            }
        }
        binding.recyclerCalendar.adapter = com.financialsuccess.game.adapters.CalendarAdapter(
            dates = dates,
            currentDate = currentCal,
            iconProvider = iconProvider,
            typeProvider = typeProvider,
            selectedDate = calendarAnchor!!
        ) { date ->
            calendarAnchor = (date.clone() as Calendar)
            updateMonthLabel()
        }
        updateMonthLabel()
        setupCalendarNav()
    }
    
    private fun rollDiceAndMove() {
        val diceValue = gameManager.rollDice()
        lastDiceValue = diceValue
        val player = currentGameState?.player ?: return
        val diceRes = when (diceValue) { 1 -> R.drawable.dice_1; 2 -> R.drawable.dice_2; 3 -> R.drawable.dice_3; 4 -> R.drawable.dice_4; 5 -> R.drawable.dice_5; 6 -> R.drawable.dice_6; else -> R.drawable.dice_1 }
        binding.btnDice.setImageResource(diceRes)
        showMessage("Выпало $diceValue")
        handleSlowTrackDice(diceValue)
    }
    
    private fun handleFastTrackDice(diceValue: Int) {
        val player = currentGameState?.player ?: return
        val dreamNumber = player.dream.fastTrackNumber
        
        if (diceValue == dreamNumber) {
            if (player.cash >= player.dream.cost) {
                showVictoryDialog()
            } else {
                val needed = player.dream.cost - player.cash
                showMessage("🎯 Вы попали на свою мечту! Не хватает ${currencyFormat.format(needed)}")
                player.cash += player.getCashFlow()
                updateUI()
            }
        } else {
            val messages = listOf(
                "🎲 Мимо! Попробуйте ещё раз.",
                "🎯 Почти попали! Следующий бросок будет удачнее.",
                "💪 Не сдавайтесь! Ваша мечта близко.",
                "📈 Время инвестировать больше и копить деньги!",
                "🚀 Каждый бросок приближает к цели!"
            )
            showMessage(messages.random())
            player.cash += player.getCashFlow()
            if (diceValue == 1 || diceValue == 6) {
                handleFastTrackEvent()
            }
            updateUI()
        }
    }

    private fun handleSlowTrackDice(diceValue: Int) {
        currentGameState = gameManager.movePlayer(diceValue)
        updateUI()
        handlePositionEvent()
    }
    
    private fun handleFastTrackEvent() {
        val events = listOf(
            "💰 Дивиденды от инвестиций: +100,000₽" to 100000,
            "📈 Удачная сделка: +200,000₽" to 200000, 
            "🎁 Бонус от клиента: +50,000₽" to 50000,
            "💎 Рост акций: +150,000₽" to 150000,
            "🏆 Премия за результат: +75,000₽" to 75000
        )
        
        val (message, amount) = events.random()
        currentGameState?.player?.cash = (currentGameState?.player?.cash ?: 0) + amount
        showMessage(message)
    }
    
    private fun showVictoryDialog() {
        val player = currentGameState?.player ?: return
        
        AlertDialog.Builder(this)
            .setTitle("🎉 ПОБЕДА!")
            .setMessage("Поздравляем! Вы достигли своей мечты: ${player.dream.name}!\n\nВы успешно вышли из крысиных бегов и осуществили финансовую мечту!\n\nИтоговый капитал: ${currencyFormat.format(player.cash)}\nПассивный доход: ${currencyFormat.format(player.passiveIncome)}")
            .setPositiveButton("🎊 Новая игра") { _, _ ->
                // Перезапуск игры
                finish()
            }
            .setNegativeButton("📊 Итоги") { _, _ ->
                showFinalStats()
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showFinalStats() {
        val player = currentGameState?.player ?: return
        
        val message = """
            🏆 ФИНАЛЬНАЯ СТАТИСТИКА
            
            🎯 Мечта: ${player.dream.name}
            💰 Итоговый капитал: ${currencyFormat.format(player.cash)}
            📊 Пассивный доход: ${currencyFormat.format(player.passiveIncome)}
            🏠 Активов: ${player.assets.size}
            💼 Инвестиций: ${player.investments.size}
            💳 Чистая стоимость: ${currencyFormat.format(player.getNetWorth())}
            
            🎉 Вы успешно прошли путь от крысиных бегов до финансовой свободы!
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("📊 Итоги игры")
            .setMessage(message)
            .setPositiveButton("🔄 Новая игра") { _, _ ->
                finish()
            }
            .show()
    }
    
    private fun handlePositionEvent() {
        val position = currentGameState?.player?.position ?: return
        
        when (position % 6) {
            0 -> showSmallDeal()
            1 -> showBonusEvent() // Заменяем зарплату на бонусное событие
            2 -> showMarketEvent()
            3 -> showBigDeal()
            4 -> showDoodadEvent()
            5 -> showCharityEvent()
        }
    }
    
    private fun showSmallDeal() {
        val deals = GameDataManager.getSmallDeals()
        val deal = deals.random()
        
        AlertDialog.Builder(this)
            .setTitle("Малая сделка")
            .setMessage("${deal.name}\nПервоначальный взнос: ${currencyFormat.format(deal.downPayment)}\nДенежный поток: +${currencyFormat.format(deal.cashFlow)}/мес")
            .setPositiveButton("Купить") { _, _ ->
                if (gameManager.buyAsset(deal)) {
                    // Логируем покупку актива (без повторного списания)
                    currentGameState?.player?.addFinancialEntry(
                        FinancialEntryType.EXPENSE,
                        FinancialCategory.ASSET_PURCHASE,
                        -deal.downPayment,
                        "Малая сделка: ${deal.name} (денежный поток: +${currencyFormat.format(deal.cashFlow)}/мес)"
                    )
                    updateUI()
                    showMessage("Актив приобретён!")
                } else {
                    showMessage("Недостаточно средств")
                }
            }
            .setNegativeButton("Пропустить", null)
            .show()
    }
    
    private fun showBigDeal() {
        val deals = GameDataManager.getBigDeals()
        val deal = deals.random()
        
        AlertDialog.Builder(this)
            .setTitle("Крупная сделка")
            .setMessage("${deal.name}\nПервоначальный взнос: ${currencyFormat.format(deal.downPayment)}\nДенежный поток: +${currencyFormat.format(deal.cashFlow)}/мес")
            .setPositiveButton("Купить") { _, _ ->
                if (gameManager.buyAsset(deal)) {
                    // Логируем покупку актива (без повторного списания)
                    currentGameState?.player?.addFinancialEntry(
                        FinancialEntryType.EXPENSE,
                        FinancialCategory.ASSET_PURCHASE,
                        -deal.downPayment,
                        "Крупная сделка: ${deal.name} (денежный поток: +${currencyFormat.format(deal.cashFlow)}/мес)"
                    )
                    updateUI()
                    showMessage("Актив приобретён!")
                } else {
                    showMessage("Недостаточно средств")
                }
            }
            .setNegativeButton("Пропустить", null)
            .show()
    }
    
    private fun showPaycheck() {
        currentGameState?.player?.let { player ->
            player.cash += player.salary
            
            // Логируем получение зарплаты
            player.logIncome(
                FinancialCategory.SALARY,
                player.salary,
                "Ежемесячная зарплата по профессии ${player.profession.name}"
            )
            
            updateUI()
            showMessage("Зарплата получена: ${currencyFormat.format(player.salary)}")
        }
    }
    
    private fun showBonusEvent() {
        val bonuses = listOf(
            "Премия за отличную работу" to 3000,
            "Бонус за проект" to 5000,
            "Награда за инициативу" to 2000,
            "Доплата за сверхурочные" to 4000,
            "Бонус за экономию" to 2500
        )
        
        val (bonusName, bonusAmount) = bonuses.random()
        
        currentGameState?.player?.let { player ->
            player.cash += bonusAmount
            player.logIncome(
                FinancialCategory.BONUS,
                bonusAmount,
                bonusName
            )
            updateUI()
            showMessage("🎁 $bonusName: +${currencyFormat.format(bonusAmount)}")
        }
    }
    
    private fun showMarketEvent() {
        val event = GameDataManager.getRandomEvent()
        
        // Риски проверяются в processMonthlyOperations, здесь просто показываем событие

        // Обработка специальных событий
        when {
            event.contains("ребёнок") -> {
                currentGameState?.player?.let { player ->
                    player.addChild()
                    player.logExpense(
                        FinancialCategory.CHILDREN,
                        8000,
                        "Рождение ребенка - дополнительные расходы 8000₽/мес"
                    )
                    updateUI()
                }
            }
            event.contains("Повышение") -> {
                currentGameState?.player?.let { player ->
                    val bonus = 5000
                    player.salary += bonus
                    player.updateTotalIncome()
                    player.logIncome(
                        FinancialCategory.BONUS,
                        bonus,
                        "Повышение зарплаты на ${currencyFormat.format(bonus)}"
                    )
                    updateUI()
                }
            }
            event.contains("Налоговая") -> {
                currentGameState?.player?.let { player ->
                    val taxAmount = 15000
                    player.logExpense(
                        FinancialCategory.TAXES,
                        taxAmount,
                        "Доплата налогов по результатам проверки"
                    )
                    updateUI()
                }
            }
            event.contains("Наследство") -> {
                currentGameState?.player?.let { player ->
                    val inheritanceAmount = 100000
                    player.cash += inheritanceAmount
                    player.logIncome(
                        FinancialCategory.INHERITANCE,
                        inheritanceAmount,
                        "Неожиданное наследство от дальнего родственника"
                    )
                    updateUI()
                }
            }
        }
        
        showMessage("🎲 $event")
    }
    
    private fun showDoodadEvent() {
        val expenses = listOf(1000, 2000, 3000, 4000, 5000)
        val expense = expenses.random()
        
        val expenseReasons = listOf(
            "Поломка автомобиля",
            "Срочный ремонт бытовой техники", 
            "Медицинские расходы",
            "Штраф за нарушение ПДД",
            "Поломка смартфона"
        )
        
        currentGameState?.player?.let { player ->
            player.logExpense(
                FinancialCategory.EMERGENCY,
                expense,
                expenseReasons.random()
            )
            updateUI()
            showMessage("Непредвиденные расходы: ${currencyFormat.format(expense)}")
        }
    }
    
    private fun showCharityEvent() {
        AlertDialog.Builder(this)
            .setTitle("Благотворительность")
            .setMessage("Хотите пожертвовать 10% от вашего дохода на благотворительность?")
            .setPositiveButton("Да") { _, _ ->
                currentGameState?.player?.let { player ->
                    val donation = (player.totalIncome * 0.1).toInt()
                    player.logExpense(
                        FinancialCategory.CHARITY,
                        donation,
                        "Пожертвование на благотворительность (10% от дохода)"
                    )
                    updateUI()
                    showMessage("Спасибо за пожертвование: ${currencyFormat.format(donation)}")
                }
            }
            .setNegativeButton("Нет", null)
            .show()
    }
    
    private fun showFinancialStatement() {
        currentGameState?.player?.let { player ->
            val message = """
                💰 ДОХОДЫ:
                Зарплата: ${currencyFormat.format(player.totalIncome)}
                Пассивный доход: ${currencyFormat.format(player.passiveIncome)}
                Общий доход: ${currencyFormat.format(player.totalIncome + player.passiveIncome)}
                
                💸 РАСХОДЫ:
                Еда: ${currencyFormat.format(player.foodExpenses)}
                Транспорт: ${currencyFormat.format(player.transportExpenses)} 
                Жильё: ${currencyFormat.format(player.housingExpenses)}
                Дети: ${currencyFormat.format(player.childrenExpenses)}
                Налоги: ${currencyFormat.format(player.taxes)}
                Прочее: ${currencyFormat.format(player.otherExpenses)}
                Кредиты: ${currencyFormat.format(player.liabilities.sumOf { it.payment })}
                Общие расходы: ${currencyFormat.format(player.totalExpenses)}
                
                📊 ИТОГО:
                Наличные: ${currencyFormat.format(player.cash)}
                Денежный поток: ${currencyFormat.format(player.getCashFlow())}
                Чистая стоимость: ${currencyFormat.format(player.getNetWorth())}
                Активы: ${player.assets.size} шт.
                
                ${if (player.canEscapeRatRace()) "🎉 Готов к скоростной дорожке!" else "💪 Увеличивайте пассивный доход"}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📋 Финансовый отчёт")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    private fun showAssets() {
        val player = currentGameState?.player
        if (player == null || player.assets.isEmpty()) {
            Toast.makeText(this, "У вас пока нет активов. Купите первый актив на рынке!", Toast.LENGTH_SHORT).show()
            return
        }
        // Показываем список активов игрока
        binding.recyclerViewAssets.visibility = 
            if (binding.recyclerViewAssets.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }
    
    private fun showMarket() {
        val items = arrayOf("📈 Активы", "💼 Инвестиции", "📊 Портфель")
        
        AlertDialog.Builder(this)
            .setTitle("🏪 Финансовый рынок")
            .setItems(items) { _, which ->
                when (which) {
                    0 -> showAvailableAssets()
                    1 -> showAvailableInvestments()
                    2 -> showPortfolio()
                }
            }
            .show()
    }
    
    private fun showAvailableAssets() {
        val smallDeals = GameDataManager.getSmallDeals()
        val bigDeals = GameDataManager.getBigDeals()
        val allDeals = smallDeals + bigDeals
        
        val names = allDeals.map { "${it.name} - ${currencyFormat.format(it.downPayment)}" }.toTypedArray()
        
        AlertDialog.Builder(this)
            .setTitle("📈 Доступные активы")
            .setItems(names) { _, which ->
                val asset = allDeals[which]
                showAssetDetails(asset)
            }
            .show()
    }
    
    private fun showAvailableInvestments() {
        val investments = GameDataManager.getInvestments()
        val names = investments.map { 
            "${it.name} - ${currencyFormat.format(it.cost)} (${getRiskText(it.riskLevel)})"
        }.toTypedArray()
        
        AlertDialog.Builder(this)
            .setTitle("💼 Инвестиционные возможности")
            .setItems(names) { _, which ->
                val investment = investments[which]
                showInvestmentDetails(investment)
            }
            .show()
    }
    
    private fun showAssetDetails(asset: Asset) {
        val roi = if (asset.downPayment > 0) {
            (asset.cashFlow * 12.0 / asset.downPayment) * 100
        } else 0.0
        
        val message = """
            ${asset.name}
            
            Первоначальный взнос: ${currencyFormat.format(asset.downPayment)}
            Стоимость: ${currencyFormat.format(asset.value)}
            Денежный поток: +${currencyFormat.format(asset.cashFlow)}/мес
            ROI: ${String.format("%.1f", roi)}% годовых
            
            ${if (asset.loan > 0) "Кредит: ${currencyFormat.format(asset.loan)} (${currencyFormat.format(asset.loanPayment)}/мес)" else "Без кредита"}
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("📊 Детали актива")
            .setMessage(message)
            .setPositiveButton("💰 Купить") { _, _ ->
                if (gameManager.buyAsset(asset)) {
                    updateUI()
                    showMessage("✅ Актив приобретён!")
                } else {
                    showMessage("❌ Недостаточно средств")
                }
            }
            .setNegativeButton("❌ Отмена", null)
            .show()
    }
    
    private fun showInvestmentDetails(investment: Investment) {
        val roi = if (investment.cost > 0) {
            (investment.expectedReturn * 12.0 / investment.cost) * 100
        } else 0.0
        
        val message = """
            ${investment.name}
            
            Стоимость: ${currencyFormat.format(investment.cost)}
            Ожидаемая доходность: ${currencyFormat.format(investment.expectedReturn)}/мес
            ROI: ${String.format("%.1f", roi)}% годовых
            Риск: ${getRiskText(investment.riskLevel)}
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("💼 Детали инвестиции")
            .setMessage(message)
            .setPositiveButton("💰 Инвестировать") { _, _ ->
                currentGameState?.player?.let { player ->
                    if (player.cash >= investment.cost) {
                        player.investments.add(investment)
                        
                        // Логируем инвестицию
                        player.logExpense(
                            FinancialCategory.INVESTMENT,
                            investment.cost,
                            "Инвестиция: ${investment.name} (доходность: +${currencyFormat.format(investment.expectedReturn)}/мес)"
                        )
                        
                        updateUI()
                        showMessage("✅ Инвестиция оформлена!")
                    } else {
                        showMessage("❌ Недостаточно средств")
                    }
                }
            }
            .setNegativeButton("❌ Отмена", null)
            .show()
    }
    
    private fun showPortfolio() {
        currentGameState?.player?.let { player ->
            val message = """
                📈 АКТИВЫ (${player.assets.size} шт.):
                ${player.assets.joinToString("\n") { "• ${it.name}: +${currencyFormat.format(it.cashFlow)}/мес" }}
                
                💼 ИНВЕСТИЦИИ (${player.investments.size} шт.):
                ${player.investments.joinToString("\n") { "• ${it.name}: +${currencyFormat.format(it.expectedReturn)}/мес" }}
                
                📊 ОБЩИЙ ПАССИВНЫЙ ДОХОД:
                ${currencyFormat.format(player.passiveIncome + player.investments.sumOf { it.expectedReturn })}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("📊 Мой портфель")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    private fun getRiskText(risk: RiskLevel): String = when (risk) {
        RiskLevel.LOW -> "🟢 Низкий"
        RiskLevel.MEDIUM -> "🟡 Средний" 
        RiskLevel.HIGH -> "🔴 Высокий"
    }
    
    private fun setupAssetsRecyclerView() { binding.recyclerViewAssets.layoutManager = LinearLayoutManager(this) }

    private fun updateUI() {
        val player = currentGameState?.player ?: return
        if (!player.isAlive()) { showDeathDialog(); return }
        binding.tvPlayerName.text = player.name ?: "Игрок"
        updateCurrentDate(player)
        updateGameStatus(player)
        updatePlayerAvatar(player)
        binding.tvProfession.text = "Профессия: ${player.profession.name}"
        binding.tvCash.text = "Наличные: ${currencyFormat.format(player.cash)}"
        binding.tvSalary.text = "Зарплата: ${currencyFormat.format(player.salary)}"
        binding.tvPassiveIncome.text = "Пассивный доход: ${currencyFormat.format(player.passiveIncome)}"
        binding.tvExpenses.text = "Расходы: ${currencyFormat.format(player.totalExpenses)}"
        binding.tvCashFlow.text = "Денежный поток: ${currencyFormat.format(player.getCashFlow())}"
        val ageColor = when { player.isInCriticalAge() -> "🔴"; player.getYearsLeft() <= 10 -> "🟡"; else -> "🟢" }
        binding.tvAge.text = "$ageColor Возраст: ${player.age} лет (осталось: ${player.getYearsLeft()})"
        binding.tvHealthStatus.text = player.getHealthStatus()
        // Обновляем 4-недельное окно календаря; при переходе конца недели текущее сместится наверх
        setupCalendarRecycler()
        if (player.canEscapeRatRace() && !player.isInFastTrack) { showEscapeRatRaceDialog() }

        // Установить иконку мечты в шапке в соответствии с выбранной мечтой
        val dreamIconRes = when (player.dream.id) {
            "yacht" -> R.drawable.dream_yacht
            "restaurant" -> R.drawable.dream_restaurant
            "charity" -> R.drawable.dream_charity
            "island" -> R.drawable.dream_island
            "space_trip" -> R.drawable.dream_space
            "business_empire" -> R.drawable.dream_business
            else -> R.drawable.ic_dream_placeholder
        }
        binding.ivDream.setImageResource(dreamIconRes)
    }
    
    // Метод updateMonthProgressBar удален - monthProgressBar больше не используется
    
    private fun updateMonthLabel() {
        val anchor = calendarAnchor ?: return
        val monthName = android.text.format.DateFormat.format("MMMM yyyy", anchor).toString().replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() }
        binding.tvMonthLabel.text = monthName
    }

    private fun setupCalendarNav() {
        binding.btnPrevMonth.setOnClickListener {
            calendarAnchor = (calendarAnchor ?: Calendar.getInstance()).apply { add(Calendar.MONTH, -1) }
            setupCalendarRecycler()
        }
        binding.btnNextMonth.setOnClickListener {
            calendarAnchor = (calendarAnchor ?: Calendar.getInstance()).apply { add(Calendar.MONTH, 1) }
            setupCalendarRecycler()
        }
        // Simple swipe gestures without GestureDetector (for broad compatibility)
        var startX = 0f
        val swipeThreshold = 100
        binding.recyclerCalendar.setOnTouchListener { _, event ->
            when (event.action) {
                android.view.MotionEvent.ACTION_DOWN -> { startX = event.x; true }
                android.view.MotionEvent.ACTION_UP -> {
                    val diffX = event.x - startX
                    if (kotlin.math.abs(diffX) > swipeThreshold) {
                        if (diffX < 0) binding.btnNextMonth.performClick() else binding.btnPrevMonth.performClick()
                        true
                    } else false
                }
                else -> false
            }
        }
    }

    private fun showEscapeRatRaceDialog() {
        val player = currentGameState?.player ?: return
        
        AlertDialog.Builder(this)
            .setTitle("🎉 Поздравляем!")
            .setMessage("Ваш пассивный доход превысил расходы!\n\nВы можете выйти из крысиных бегов на скоростную дорожку!\n\n🎯 На скоростной дорожке:\n• Ваша цель: ${player.dream.name}\n• Нужно выбросить: ${player.dream.fastTrackNumber}\n• Стоимость мечты: ${currencyFormat.format(player.dream.cost)}\n• Вы получаете денежный поток каждый ход")
            .setPositiveButton("🚀 Перейти") { _, _ ->
                currentGameState?.player?.isInFastTrack = true
                updateUI()
                showFastTrackWelcome()
            }
            .setNegativeButton("Остаться", null)
            .show()
    }
    
    private fun showFastTrackWelcome() {
        val player = currentGameState?.player ?: return
        
        AlertDialog.Builder(this)
            .setTitle("🎯 Скоростная дорожка!")
            .setMessage("Добро пожаловать на скоростную дорожку!\n\n🎲 Как играть:\n• Бросайте кубик каждый ход\n• Нужно выбросить ${player.dream.fastTrackNumber} для вашей мечты\n• При попадании вы можете купить мечту если хватает денег\n• Каждый ход вы получаете денежный поток\n• При 1 или 6 возможны бонусы!\n\n💰 Ваши деньги: ${currencyFormat.format(player.cash)}\n🎯 Нужно для мечты: ${currencyFormat.format(player.dream.cost)}")
            .setPositiveButton("🎮 Играть!", null)
            .show()
    }
    
    private fun showDeathDialog() {
        val player = currentGameState?.player ?: return
        
        val finalStats = """
            ⚰️ ИГРА ОКОНЧЕНА ⚰️
            
            👴 Возраст смерти: ${player.age} лет
            🎮 Игра длилась: ${player.monthsPlayed} месяцев (${player.monthsPlayed / 12} лет)
            
            💰 ФИНАЛЬНЫЕ РЕЗУЛЬТАТЫ:
            💵 Итоговый капитал: ${currencyFormat.format(player.cash)}
            📊 Пассивный доход: ${currencyFormat.format(player.passiveIncome)}
            🏠 Активов: ${player.assets.size}
            💼 Инвестиций: ${player.investments.size}
            💳 Чистая стоимость: ${currencyFormat.format(player.getNetWorth())}
            
            ${if (player.isInFastTrack) "🎯 Достигли скоростной дорожки!" else "💼 Остались в крысиных бегах"}
            
            💡 Помните: жизнь коротка, инвестируйте мудро!
        """.trimIndent()
        
        AlertDialog.Builder(this)
            .setTitle("💀 Конец жизни")
            .setMessage(finalStats)
            .setPositiveButton("🔄 Новая жизнь") { _, _ ->
                finish() // Возврат к выбору профессии
            }
            .setCancelable(false)
            .show()
    }
    
    private fun showFinancialJournal() {
        val player = currentGameState?.player ?: return
        val entries = player.getRecentJournalEntries(100) // Последние 100 записей
        
        if (entries.isEmpty()) {
            showMessage("📋 Журнал финансов пуст")
            return
        }
        
        // Создаем диалог с RecyclerView
        val dialogView = layoutInflater.inflate(R.layout.dialog_financial_journal, null)
        val recyclerView = dialogView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewJournal)
        val tvStats = dialogView.findViewById<android.widget.TextView>(R.id.tvJournalStats)
        val btnExportTxt = dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnExportTxt)
        
        // Настраиваем RecyclerView
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.adapter = com.financialsuccess.game.adapters.FinancialJournalAdapter(entries)
        
        // Статистика
        val totalIncome = entries.filter { it.amount > 0 }.sumOf { it.amount }
        val totalExpenses = entries.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
        val balance = totalIncome - totalExpenses
        
        tvStats.text = """
            📊 СТАТИСТИКА (последние ${entries.size} операций):
            
            ➕ Общие доходы: ${currencyFormat.format(totalIncome)}
            ➖ Общие расходы: ${currencyFormat.format(totalExpenses)}
            💰 Чистый результат: ${currencyFormat.format(balance)}
            
            📈 Записей в журнале: ${player.financialJournal.size}
        """.trimIndent()
        
        // Обработка нажатия на кнопку экспорта
        btnExportTxt.setOnClickListener {
            exportJournalToTxt(player, entries)
        }
        
        AlertDialog.Builder(this)
            .setTitle("📊 Журнал финансов")
            .setView(dialogView)
            .setPositiveButton("Закрыть", null)
            .setNeutralButton("📈 Аналитика") { _, _ ->
                showJournalAnalytics()
            }
            .show()
    }
    
    /**
     * Экспортирует финансовый журнал в TXT формат и делится им
     */
    private fun exportJournalToTxt(player: Player, entries: List<FinancialEntry>) {
        try {
            val txtContent = generateJournalTxtContent(player, entries)
            
            // Создаем Intent для отправки текста
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "📊 Финансовый журнал - ${player.name ?: "Игрок"}")
                putExtra(Intent.EXTRA_TEXT, txtContent)
            }
            
            // Запускаем диалог выбора приложения для отправки
            startActivity(Intent.createChooser(shareIntent, "📤 Экспорт журнала в TXT"))
            
        } catch (e: Exception) {
            showMessage("❌ Ошибка при экспорте: ${e.message}")
        }
    }
    
    /**
     * Генерирует содержимое TXT файла из данных журнала
     */
    private fun generateJournalTxtContent(player: Player, entries: List<FinancialEntry>): String {
        val sb = StringBuilder()
        
        // Заголовок
        sb.append("=".repeat(80)).append("\n")
        sb.append("📊 ФИНАНСОВЫЙ ЖУРНАЛ - ИГРА 'ФИНАНСОВЫЙ УСПЕХ'\n")
        sb.append("=".repeat(80)).append("\n\n")
        
        // Информация об игроке
        sb.append("👤 ИНФОРМАЦИЯ ОБ ИГРОКЕ:\n")
        sb.append("-".repeat(40)).append("\n")
        sb.append("Имя: ${player.name ?: "Не указано"}\n")
        sb.append("Профессия: ${player.profession.name}\n")
        sb.append("Возраст: ${player.age} лет\n")
        sb.append("Месяцев в игре: ${player.monthsPlayed}\n")
        sb.append("Мечта: ${player.dream.name}\n")
        sb.append("Текущий баланс: ${currencyFormat.format(player.cash)}\n")
        sb.append("Чистая стоимость: ${currencyFormat.format(player.getNetWorth())}\n")
        sb.append("Пассивный доход: ${currencyFormat.format(player.passiveIncome)}\n\n")
        
        // Общая статистика
        val totalIncome = entries.filter { it.amount > 0 }.sumOf { it.amount }
        val totalExpenses = entries.filter { it.amount < 0 }.sumOf { kotlin.math.abs(it.amount) }
        val balance = totalIncome - totalExpenses
        
        sb.append("📈 ОБЩАЯ СТАТИСТИКА:\n")
        sb.append("-".repeat(40)).append("\n")
        sb.append("Всего доходов: ${currencyFormat.format(totalIncome)}\n")
        sb.append("Всего расходов: ${currencyFormat.format(totalExpenses)}\n")
        sb.append("Чистый результат: ${currencyFormat.format(balance)}\n")
        sb.append("Всего записей: ${entries.size}\n\n")
        
        // Статистика по категориям
        val categoryStats = player.getCategoryStats()
        sb.append("🏷️ СТАТИСТИКА ПО КАТЕГОРИЯМ:\n")
        sb.append("-".repeat(40)).append("\n")
        
        // Доходы
        sb.append("💰 ДОХОДЫ:\n")
        categoryStats.filter { it.value > 0 }
            .toList()
            .sortedByDescending { it.second }
            .forEach { (category, amount) ->
                sb.append("  ${category.getIcon()} ${category.getDisplayName()}: ${currencyFormat.format(amount)}\n")
            }
        
        sb.append("\n💸 РАСХОДЫ:\n")
        categoryStats.filter { it.value < 0 }
            .toList()
            .sortedBy { it.second } // От больших расходов к меньшим
            .forEach { (category, amount) ->
                sb.append("  ${category.getIcon()} ${category.getDisplayName()}: ${currencyFormat.format(kotlin.math.abs(amount))}\n")
            }
        
        // Месячная статистика
        if (player.monthsPlayed > 0) {
            val (monthIncome, monthExpense) = player.getMonthlyStats(player.monthsPlayed)
            sb.append("\n📅 ТЕКУЩИЙ МЕСЯЦ (${player.monthsPlayed}):\n")
            sb.append("  Доходы: ${currencyFormat.format(monthIncome)}\n")
            sb.append("  Расходы: ${currencyFormat.format(monthExpense)}\n")
            sb.append("  Баланс: ${currencyFormat.format(monthIncome - monthExpense)}\n\n")
        }
        
        // Детальные записи
        sb.append("📝 ДЕТАЛЬНЫЕ ЗАПИСИ:\n")
        sb.append("-".repeat(80)).append("\n")
        
        entries.forEachIndexed { index, entry ->
            val entryNumber = index + 1
            val isPositive = entry.amount > 0
            val amountText = if (isPositive) {
                "+${currencyFormat.format(entry.amount)}"
            } else {
                currencyFormat.format(entry.amount)
            }
            
            sb.append("${entryNumber}. ${entry.category.getIcon()} ${entry.category.getDisplayName()}\n")
            sb.append("   Описание: ${entry.description}\n")
            sb.append("   Сумма: $amountText\n")
            sb.append("   Дата: ${entry.realDate}\n")
            sb.append("   Возраст игрока: ${entry.playerAge} лет\n")
            sb.append("   Месяц игры: ${entry.monthNumber}\n")
            sb.append("   Баланс после: ${currencyFormat.format(entry.balanceAfter)}\n")
            sb.append("   Тип: ${if (isPositive) "Доход" else "Расход"}\n")
            sb.append("-".repeat(60)).append("\n")
        }
        
        // Футер
        sb.append("\n" + "=".repeat(80)).append("\n")
        sb.append("📅 Дата экспорта: ${java.text.SimpleDateFormat("dd.MM.yyyy HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n")
        sb.append("🎮 Версия игры: ${packageManager.getPackageInfo(packageName, 0).versionName}\n")
        sb.append("=".repeat(80)).append("\n")
        
        return sb.toString()
    }
    
    private fun showJournalAnalytics() {
        val player = currentGameState?.player ?: return
        val stats = player.getCategoryStats()
        
        val analyticsText = buildString {
            append("📈 АНАЛИТИКА ПО КАТЕГОРИЯМ:\n\n")
            
            append("💰 ДОХОДЫ:\n")
            stats.filter { it.value > 0 }.toList()
                .sortedByDescending { it.second }
                .forEach { (category, amount) ->
                    append("${category.getIcon()} ${category.getDisplayName()}: ${currencyFormat.format(amount)}\n")
                }
            
            append("\n💸 РАСХОДЫ:\n")
            stats.filter { it.value < 0 }.toList()
                .sortedBy { it.second } // От больших расходов к меньшим
                .forEach { (category, amount) ->
                    append("${category.getIcon()} ${category.getDisplayName()}: ${currencyFormat.format(kotlin.math.abs(amount))}\n")
                }
            
            val currentMonth = player.monthsPlayed
            if (currentMonth > 0) {
                val (monthIncome, monthExpense) = player.getMonthlyStats(currentMonth)
                append("\n📅 ТЕКУЩИЙ МЕСЯЦ (${currentMonth}):\n")
                append("➕ Доходы: ${currencyFormat.format(monthIncome)}\n")
                append("➖ Расходы: ${currencyFormat.format(monthExpense)}\n")
                append("💰 Баланс: ${currencyFormat.format(monthIncome - monthExpense)}\n")
            }
        }
        
        AlertDialog.Builder(this)
            .setTitle("📈 Финансовая аналитика")
            .setMessage(analyticsText)
            .setPositiveButton("OK", null)
            .show()
    }
    
    private fun showHealthStatus() {
        val player = currentGameState?.player ?: return
        
        val healthInfo = buildString {
            append("🏥 СОСТОЯНИЕ ЗДОРОВЬЯ:\n\n")
            append("📊 Общий статус: ${player.getHealthStatus()}\n\n")
            
            if (player.activeRisks.isNotEmpty()) {
                append("⚠️ АКТИВНЫЕ ПРОФЕССИОНАЛЬНЫЕ РИСКИ:\n")
                player.activeRisks.forEach { risk ->
                    append("${risk.icon} ${risk.name}\n")
                    append("   ${risk.description}\n")
                    risk.effects.forEach { effect ->
                        append("   💰 Расходы: +${currencyFormat.format(effect.expenseIncrease)}\n")
                        if (effect.salaryReduction > 0) {
                            append("   💼 Снижение зарплаты: -${currencyFormat.format(effect.salaryReduction)}\n")
                        }
                        if (effect.recoveryTime > 0) {
                            append("   ⏰ Восстановление: ${effect.recoveryTime} мес.\n")
                        }
                        if (effect.careerEnd) {
                            append("   🚫 Завершение карьеры\n")
                        }
                    }
                    append("\n")
                }
            } else {
                append("✅ Нет активных профессиональных рисков\n\n")
            }
            
            // Информация о рисках профессии
            val professionRisks = ProfessionalRisks.getRisksForProfession(player.profession.name)
            if (professionRisks.isNotEmpty()) {
                append("⚠️ ВОЗМОЖНЫЕ РИСКИ ПРОФЕССИИ:\n")
                professionRisks.forEach { risk ->
                    val isActive = player.activeRisks.any { it.name == risk.name }
                    val ageMatch = player.age in risk.ageRange
                    
                    append("${risk.icon} ${risk.name}")
                    when {
                        isActive -> append(" (АКТИВЕН)")
                        !ageMatch -> append(" (возраст ${risk.ageRange.first}-${risk.ageRange.last})")
                        else -> append(" (риск ${(risk.probability * 100).toInt()}%)")
                    }
                    append("\n")
                }
            }
        }
        
        AlertDialog.Builder(this)
            .setTitle("🏥 Здоровье и профессиональные риски")
            .setMessage(healthInfo)
            .setPositiveButton("OK", null)
            .setNeutralButton("💊 Профилактика") { _, _ ->
                showHealthcareOptions()
            }
            .show()
    }
    
    private fun showHealthcareOptions() {
        val player = currentGameState?.player ?: return
        
        val options = arrayOf(
            "💊 Медосмотр (5,000₽) - Снижает риски на 20%",
            "🏃 Спорт и диета (3,000₽) - Улучшает общее здоровье", 
            "🧘 Психотерапия (8,000₽) - Снижает стресс и выгорание",
            "🏥 Полное обследование (15,000₽) - Выявляет скрытые проблемы"
        )
        
        AlertDialog.Builder(this)
            .setTitle("💊 Профилактика и лечение")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> buyHealthcare("Медосмотр", 5000, "Профилактический медосмотр")
                    1 -> buyHealthcare("Спорт и диета", 3000, "Занятия спортом и здоровое питание")
                    2 -> buyHealthcare("Психотерапия", 8000, "Сеансы психотерапии")
                    3 -> buyHealthcare("Полное обследование", 15000, "Комплексное медицинское обследование")
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun buyHealthcare(name: String, cost: Int, description: String) {
        val player = currentGameState?.player ?: return
        
        if (player.cash >= cost) {
            player.logExpense(
                FinancialCategory.EMERGENCY,
                cost,
                "Медицинские услуги: $description"
            )
            
            // Простой эффект - небольшое улучшение здоровья
            if (player.riskEffects.isNotEmpty()) {
                // Ускоряем восстановление на 1-2 месяца
                val effect = player.riskEffects.random()
                if (effect.recoveryTime > 0) {
                    val newEffect = effect.copy(recoveryTime = maxOf(0, effect.recoveryTime - 2))
                    player.riskEffects.remove(effect)
                    if (newEffect.recoveryTime > 0) {
                        player.riskEffects.add(newEffect)
                    }
                }
            }
            
            updateUI()
            showMessage("✅ $name оплачен! Состояние здоровья улучшилось.")
        } else {
            showMessage("❌ Недостаточно средств для оплаты медицинских услуг")
        }
    }
    
    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
    
    // === НОВЫЕ МЕТОДЫ ДЛЯ УЛУЧШЕННОГО ИНТЕРФЕЙСА ===
    
    private fun updateCurrentDate(player: Player) {
        binding.tvCurrentDate.text = player.getCurrentDateString()
    }
    
    private fun updateGameStatus(player: Player) {
        val status = if (player.isInFastTrack) {
            "🚀 Скоростная дорожка"
        } else {
            "🐀 Крысиные бега"
        }
        binding.tvGameStatus.text = status
    }
    
    private fun updatePlayerAvatar(player: Player) {
        val avatarResource = player.profession.avatarResId
        try {
            binding.ivPlayerAvatar.setImageResource(avatarResource)
        } catch (e: Exception) {
            binding.ivPlayerAvatar.setImageResource(R.drawable.player_token)
        }
    }
    
    private fun updateGameTrackVisualization(player: Player) {
        // Обновляем позицию игрока на треке
        // Fix: Используем binding.llTrackLine вместо getChildAt(1)
        val trackLayout = binding.llTrackLine
        val playerIcon = trackLayout.findViewById<ImageView>(R.id.iv_player_on_track)
        
        // Позиционируем игрока на треке (процент от 0 до 100)
        val progress = if (player.isInFastTrack) {
            val dreamCost = player.dream.cost
            ((player.cash.toFloat() / dreamCost.toFloat()) * 100).coerceAtMost(100f)
        } else {
            // Теперь используем день месяца (1..30)
            ((player.currentDayOfMonth.toFloat() - 1f) / 29f) * 100f
        }
        
        // Устанавливаем позицию (в процентах от ширины трека)
        trackLayout.post {
            val trackWidth = trackLayout.width - playerIcon.width
            val newX = (trackWidth * progress / 100).toInt()
            playerIcon.translationX = newX.toFloat()
        }
        
        // Обновляем иконку игрока на треке
        val trackIconResource = when {
            player.isInFastTrack -> when {
                player.passiveIncome > 100000 -> R.drawable.ic_luxury_car  // Роскошный автомобиль
                player.passiveIncome > 50000 -> R.drawable.ic_car          // Обычный автомобиль
                else -> R.drawable.ic_motorcycle                           // Мотоцикл
            }
            else -> R.drawable.player_token  // Токен игрока по умолчанию
        }
        try {
            playerIcon.setImageResource(trackIconResource)
        } catch (e: Exception) {
            playerIcon.setImageResource(R.drawable.player_token)
        }
        
        // Обновляем информацию о треке
        val trackInfo = if (player.isInFastTrack) {
            val vehicleType = when {
                player.passiveIncome > 100000 -> "🏎️ Мчитесь к мечте на роскошном автомобиле!"
                player.passiveIncome > 50000 -> "🚗 Едете к мечте на автомобиле!"
                else -> "🏍️ Летите к мечте на мотоцикле!"
            }
            vehicleType
        } else {
            "🏃 Бегите от зарплаты до зарплаты"
        }
        binding.tvTrackInfo.text = trackInfo
        
        // Обновляем финиш/мечту
        binding.tvFinishGoal.text = if (player.isInFastTrack) {
            "🎯\n${player.dream.name}"
        } else {
            "🔄\nКруг"
        }
    }
    
    private fun showEventPanel(
        title: String,
        message: String,
        primaryText: String? = null,
        onPrimary: (() -> Unit)? = null,
        secondaryText: String? = null,
        onSecondary: (() -> Unit)? = null
    ) {
        binding.tvEventTitle.text = title
        binding.tvEventMessage.text = message
        val primaryBtn = binding.btnEventPrimary
        val secondaryBtn = binding.btnEventSecondary

        if (primaryText != null && onPrimary != null) {
            primaryBtn.visibility = View.VISIBLE
            primaryBtn.text = primaryText
            primaryBtn.setOnClickListener { onPrimary.invoke(); hideEventPanel() }
        } else {
            primaryBtn.visibility = View.GONE
        }

        if (secondaryText != null && onSecondary != null) {
            secondaryBtn.visibility = View.VISIBLE
            secondaryBtn.text = secondaryText
            secondaryBtn.setOnClickListener { onSecondary.invoke(); hideEventPanel() }
        } else {
            secondaryBtn.visibility = View.GONE
        }

        if (binding.cardEventPanel.visibility != View.VISIBLE) {
            binding.cardEventPanel.alpha = 0f
            binding.cardEventPanel.visibility = View.VISIBLE
            binding.cardEventPanel.animate().alpha(1f).setDuration(200).start()
        }
    }

    private fun hideEventPanel() {
        if (binding.cardEventPanel.visibility == View.VISIBLE) {
            binding.cardEventPanel.animate().alpha(0f).setDuration(150).withEndAction {
                binding.cardEventPanel.visibility = View.GONE
            }.start()
        }
    }
    
    private fun showAgeStatistics() {
        val player = currentGameState?.player ?: return
        // Получаем статистику по социальной группе (по id профессии)
        val averageLifeExpectancy = when (player.profession.id) {
            "doctor" -> 78
            "engineer" -> 75
            "teacher" -> 77
            "manager" -> 73
            "mechanic" -> 72
            "lawyer" -> 76
            else -> 75
        }
        val socialGroup = when (player.profession.id) {
            "doctor" -> "медицинских работников"
            "engineer" -> "инженеров"
            "teacher" -> "работников образования"
            "manager" -> "менеджеров"
            "mechanic" -> "механиков"
            "lawyer" -> "юристов"
            else -> "людей с вашей профессией"
        }
        val remainingYears = maxOf(0, averageLifeExpectancy - player.age)
        val lifeProgress = (player.age.toFloat() / averageLifeExpectancy.toFloat() * 100).toInt()
        val message = """
            📊 СТАТИСТИКА ПО ВОЗРАСТУ
            
            👤 Ваш текущий возраст: ${player.age} лет
            📈 Средняя продолжительность жизни для $socialGroup: $averageLifeExpectancy лет
            
            ⏰ Статистически вам осталось примерно: $remainingYears лет
            📊 Прожито: $lifeProgress% от средней продолжительности жизни
            
            💡 Помните: это средние данные, ваша реальная продолжительность жизни может отличаться в зависимости от образа жизни, здоровья и финансового положения.
            
            ${if (player.passiveIncome > player.totalExpenses) "✅ Ваша финансовая свобода увеличивает качество жизни!" else "⚠️ Финансовый стресс может влиять на здоровье и продолжительность жизни."}
        """.trimIndent()
        AlertDialog.Builder(this)
            .setTitle("📈 Статистика продолжительности жизни")
            .setMessage(message)
            .setPositiveButton("OK", null)
            .setNeutralButton("💪 Здоровье") { _, _ ->
                showHealthStatus()
            }
            .show()
    }
}