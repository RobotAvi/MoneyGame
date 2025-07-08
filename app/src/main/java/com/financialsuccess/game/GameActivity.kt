package com.financialsuccess.game

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.financialsuccess.game.adapters.AssetAdapter
import com.financialsuccess.game.data.GameDataManager
import com.financialsuccess.game.databinding.ActivityGameBinding
import com.financialsuccess.game.models.*
import java.text.NumberFormat
import java.util.*

class GameActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityGameBinding
    private lateinit var gameManager: GameManager
    private var currentGameState: GameState? = null
    
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("ru", "RU"))
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        initGame()
        setupUI()
    }
    
    private fun initGame() {
        val profession = intent.getParcelableExtra<Profession>("profession")
        val dream = intent.getParcelableExtra<Dream>("dream")
        val playerAge = intent.getIntExtra("playerAge", 25)
        
        if (profession != null && dream != null) {
            gameManager = GameManager()
            currentGameState = gameManager.startNewGame(profession, dream, playerAge)
            updateUI()
        } else {
            finish()
        }
    }
    
    private fun setupUI() {
        binding.btnRollDice.setOnClickListener {
            rollDiceAndMove()
        }
        
        binding.btnFinancialStatement.setOnClickListener {
            showFinancialStatement()
        }
        
        binding.btnAssets.setOnClickListener {
            showAssets()
        }
        
        binding.btnMarket.setOnClickListener {
            showMarket()
        }
        
        binding.btnFinancialJournal.setOnClickListener {
            showFinancialJournal()
        }
        
        setupAssetsRecyclerView()
    }
    
    private fun rollDiceAndMove() {
        val diceValue = gameManager.rollDice()
        val player = currentGameState?.player ?: return
        
        // Устанавливаем соответствующее изображение кубика
        val diceRes = when (diceValue) {
            1 -> R.drawable.dice_1
            2 -> R.drawable.dice_2
            3 -> R.drawable.dice_3
            4 -> R.drawable.dice_4
            5 -> R.drawable.dice_5
            6 -> R.drawable.dice_6
            else -> R.drawable.dice_1
        }
        
        try {
            val diceField = binding.javaClass.getDeclaredField("ivDice")
            val imageView = diceField.get(binding) as? android.widget.ImageView
            imageView?.setBackgroundResource(diceRes)
        } catch (e: Exception) {
            // Игнорируем если поле не найдено (совместимость)
        }
        
        if (player.isInFastTrack) {
            // Логика скоростной дорожки
            handleFastTrackDice(diceValue)
        } else {
            // Обычная логика крысиных бегов
            binding.tvDiceValue.text = "Кубик: $diceValue"
            currentGameState = gameManager.movePlayer(diceValue)
            updateUI()
            
            // Проверяем событие на клетке
            handlePositionEvent()
        }
    }
    
    private fun handleFastTrackDice(diceValue: Int) {
        val player = currentGameState?.player ?: return
        val dreamNumber = player.dream?.fastTrackNumber ?: return
        
        binding.tvDiceValue.text = "Результат: $diceValue (Нужно: ${dreamNumber})"
        
        if (diceValue == dreamNumber) {
            // Попал на мечту!
            if (player.cash >= (player.dream?.cost ?: return)) {
                // Может купить мечту - победа!
                showVictoryDialog()
            } else {
                // Попал, но недостаточно денег
                val needed = (player.dream?.cost ?: 0) - player.cash
                showMessage("🎯 Вы попали на свою мечту!\n\nОднако вам не хватает ${currencyFormat.format(needed)} для её покупки.\n\nПродолжайте инвестировать и накапливать деньги!")
                
                // Получаем денежный поток за ход
                player.cash += player.getCashFlow()
                updateUI()
            }
        } else {
            // Не попал на мечту
            val messages = listOf(
                "🎲 Мимо! Попробуйте ещё раз.",
                "🎯 Почти попали! Следующий бросок будет удачнее.",
                "💪 Не сдавайтесь! Ваша мечта близко.",
                "📈 Время инвестировать больше и копить деньги!",
                "🚀 Каждый бросок приближает к цели!"
            )
            showMessage(messages.random())
            
            // Получаем денежный поток за ход
            player.cash += player.getCashFlow()
            
            // Случайные события на скоростной дорожке
            if (diceValue == 1 || diceValue == 6) {
                handleFastTrackEvent()
            }
            
            updateUI()
        }
    }
    
    private fun handleFastTrackEvent() {
        val events = listOf(
            "💰 Дивиденды от инвестиций: +50,000₽" to 50000,
            "📈 Удачная сделка: +100,000₽" to 100000, 
            "🎁 Бонус от клиента: +25,000₽" to 25000,
            "💎 Рост акций: +75,000₽" to 75000,
            "🏆 Премия за результат: +30,000₽" to 30000
        )
        
        val (message, amount) = events.random()
        currentGameState?.player?.cash = (currentGameState?.player?.cash ?: 0) + amount
        showMessage(message)
    }
    
    private fun showVictoryDialog() {
        val player = currentGameState?.player ?: return
        
        AlertDialog.Builder(this)
            .setTitle("🎉 ПОБЕДА!")
            .setMessage("Поздравляем! Вы достигли своей мечты: ${player.dream?.name ?: "неизвестная мечта"}!\n\nВы успешно вышли из крысиных бегов и осуществили финансовую мечту!\n\nИтоговый капитал: ${currencyFormat.format(player.cash)}\nПассивный доход: ${currencyFormat.format(player.passiveIncome)}")
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
            
            🎯 Мечта: ${player.dream?.name ?: "неизвестная мечта"}
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
            1 -> showPaycheck()
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
                    // Логируем покупку актива
                    currentGameState?.player?.logExpense(
                        FinancialCategory.ASSET_PURCHASE,
                        deal.downPayment,
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
                    // Логируем покупку актива
                    currentGameState?.player?.logExpense(
                        FinancialCategory.ASSET_PURCHASE,
                        deal.downPayment,
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
                "Ежемесячная зарплата по профессии ${player.profession?.name}"
            )
            
            updateUI()
            showMessage("Зарплата получена: ${currencyFormat.format(player.salary)}")
        }
    }
    
    private fun showMarketEvent() {
        val event = GameDataManager.getRandomEvent()
        
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
                    player.cash -= taxAmount
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
        val expenses = listOf(5000, 10000, 15000, 20000, 25000)
        val expense = expenses.random()
        
        val expenseReasons = listOf(
            "Поломка автомобиля",
            "Срочный ремонт бытовой техники", 
            "Медицинские расходы",
            "Штраф за нарушение ПДД",
            "Поломка смартфона"
        )
        
        currentGameState?.player?.let { player ->
            player.cash -= expense
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
                    player.cash -= donation
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
                    // Логируем покупку актива
                    currentGameState?.player?.logExpense(
                        FinancialCategory.ASSET_PURCHASE,
                        asset.downPayment,
                        "Покупка на рынке: ${asset.name} (денежный поток: +${currencyFormat.format(asset.cashFlow)}/мес)"
                    )
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
                        player.cash -= investment.cost
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
    
    private fun setupAssetsRecyclerView() {
        binding.recyclerViewAssets.layoutManager = LinearLayoutManager(this)
    }
    
    private fun updateUI() {
        currentGameState?.player?.let { player ->
            // Проверяем смерть игрока
            if (!player.isAlive()) {
                showDeathDialog()
                return
            }
            
            binding.tvCash.text = "Наличные: ${currencyFormat.format(player.cash)}"
            binding.tvSalary.text = "Зарплата: ${currencyFormat.format(player.salary)}"
            binding.tvPassiveIncome.text = "Пассивный доход: ${currencyFormat.format(player.passiveIncome)}"
            binding.tvExpenses.text = "Расходы: ${currencyFormat.format(player.totalExpenses)}"
            binding.tvCashFlow.text = "Денежный поток: ${currencyFormat.format(player.getCashFlow())}"
            
            // Отображаем возраст с цветовой индикацией
            val ageColor = when {
                player.isInCriticalAge() -> "🔴" // Критический возраст
                player.getYearsLeft() <= 10 -> "🟡" // Предупреждение
                else -> "🟢" // Нормальный возраст
            }
            
            binding.tvAge.text = "$ageColor Возраст: ${player.age} лет (осталось: ${player.getYearsLeft()})"
            
            // Изменяем интерфейс в зависимости от трека
            if (player.isInFastTrack) {
                binding.tvPosition.text = "🎯 СКОРОСТНАЯ ДОРОЖКА"
                binding.tvDiceValue.text = "Цель: ${player.dream?.name ?: "неизвестная мечта"} (${currencyFormat.format(player.dream?.cost ?: 0)})"
                
                // Меняем фон на скоростную дорожку
                try {
                    binding.root.setBackgroundResource(R.drawable.bg_main_screen)
                } catch (e: Exception) {
                    // Игнорируем если фон не найден
                }
                
                // Изменяем текст кнопки
                binding.btnRollDice.text = "🎲 Бросить на мечту"
            } else {
                binding.tvPosition.text = "Позиция: ${player.position}"
                binding.btnRollDice.text = "🎲 Бросить кубик"
                
                // Возвращаем обычный фон
                try {
                    binding.root.setBackgroundResource(R.drawable.bg_game_field)
                } catch (e: Exception) {
                    // Игнорируем если фон не найден
                }
            }
            
            // Обновляем список активов
            val adapter = AssetAdapter(player.assets) { assetIndex ->
                // Обработка продажи актива
                AlertDialog.Builder(this)
                    .setTitle("Продать актив?")
                    .setMessage("Вы уверены, что хотите продать этот актив?")
                    .setPositiveButton("Продать") { _, _ ->
                        val assetToSell = player.assets[assetIndex]
                        if (gameManager.sellAsset(assetIndex)) {
                            // Логируем продажу актива
                            player.logIncome(
                                FinancialCategory.ASSET_SALE,
                                assetToSell.value,
                                "Продажа актива: ${assetToSell.name}"
                            )
                            updateUI()
                            showMessage("Актив продан!")
                        }
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
            binding.recyclerViewAssets.adapter = adapter
            
            // Проверяем, может ли игрок выйти из крысиных бегов
            if (player.canEscapeRatRace() && !player.isInFastTrack) {
                showEscapeRatRaceDialog()
            }
        }
    }
    
    private fun showEscapeRatRaceDialog() {
        val player = currentGameState?.player ?: return
        
        AlertDialog.Builder(this)
            .setTitle("🎉 Поздравляем!")
            .setMessage("Ваш пассивный доход превысил расходы!\n\nВы можете выйти из крысиных бегов на скоростную дорожку!\n\n🎯 На скоростной дорожке:\n• Ваша цель: ${player.dream?.name ?: "неизвестная мечта"}\n• Нужно выбросить: ${player.dream?.fastTrackNumber ?: 6}\n• Стоимость мечты: ${currencyFormat.format(player.dream?.cost ?: 0)}\n• Вы получаете денежный поток каждый ход")
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
            .setMessage("Добро пожаловать на скоростную дорожку!\n\n🎲 Как играть:\n• Бросайте кубик каждый ход\n• Нужно выбросить ${player.dream?.fastTrackNumber ?: 6} для вашей мечты\n• При попадании вы можете купить мечту если хватает денег\n• Каждый ход вы получаете денежный поток\n• При 1 или 6 возможны бонусы!\n\n💰 Ваши деньги: ${currencyFormat.format(player.cash)}\n🎯 Нужно для мечты: ${currencyFormat.format(player.dream?.cost ?: 0)}")
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
        
        AlertDialog.Builder(this)
            .setTitle("📊 Журнал финансов")
            .setView(dialogView)
            .setPositiveButton("Закрыть", null)
            .setNeutralButton("📈 Аналитика") { _, _ ->
                showJournalAnalytics()
            }
            .show()
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
    
    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}