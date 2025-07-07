package com.financialsuccess.game

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.financialsuccess.game.adapters.AssetAdapter
import com.financialsuccess.game.data.GameDataManager
import com.financialsuccess.game.databinding.ActivityGameBinding
import com.financialsuccess.game.models.Dream
import com.financialsuccess.game.models.GameState
import com.financialsuccess.game.models.Profession
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
        
        if (profession != null && dream != null) {
            gameManager = GameManager()
            currentGameState = gameManager.startNewGame(profession, dream)
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
        
        setupAssetsRecyclerView()
    }
    
    private fun rollDiceAndMove() {
        val diceValue = gameManager.rollDice()
        binding.tvDiceValue.text = "Кубик: $diceValue"
        
        currentGameState = gameManager.movePlayer(diceValue)
        updateUI()
        
        // Проверяем событие на клетке
        handlePositionEvent()
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
            updateUI()
            showMessage("Зарплата получена: ${currencyFormat.format(player.salary)}")
        }
    }
    
    private fun showMarketEvent() {
        val event = GameDataManager.getRandomEvent()
        showMessage(event)
    }
    
    private fun showDoodadEvent() {
        val expenses = listOf(5000, 10000, 15000, 20000, 25000)
        val expense = expenses.random()
        
        currentGameState?.player?.let { player ->
            player.cash -= expense
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
                Наличные: ${currencyFormat.format(player.cash)}
                Общий доход: ${currencyFormat.format(player.totalIncome)}
                Пассивный доход: ${currencyFormat.format(player.passiveIncome)}
                Расходы: ${currencyFormat.format(player.totalExpenses)}
                Денежный поток: ${currencyFormat.format(player.getCashFlow())}
                Чистая стоимость: ${currencyFormat.format(player.getNetWorth())}
            """.trimIndent()
            
            AlertDialog.Builder(this)
                .setTitle("Финансовый отчёт")
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
        // Показываем доступные для покупки активы
        val smallDeals = GameDataManager.getSmallDeals()
        val bigDeals = GameDataManager.getBigDeals()
        val allDeals = smallDeals + bigDeals
        
        // Здесь можно показать диалог с выбором активов
    }
    
    private fun setupAssetsRecyclerView() {
        binding.recyclerViewAssets.layoutManager = LinearLayoutManager(this)
    }
    
    private fun updateUI() {
        currentGameState?.player?.let { player ->
            binding.tvCash.text = "Наличные: ${currencyFormat.format(player.cash)}"
            binding.tvSalary.text = "Зарплата: ${currencyFormat.format(player.salary)}"
            binding.tvPassiveIncome.text = "Пассивный доход: ${currencyFormat.format(player.passiveIncome)}"
            binding.tvExpenses.text = "Расходы: ${currencyFormat.format(player.totalExpenses)}"
            binding.tvCashFlow.text = "Денежный поток: ${currencyFormat.format(player.getCashFlow())}"
            binding.tvPosition.text = "Позиция: ${player.position}"
            
            // Обновляем список активов
            val adapter = AssetAdapter(player.assets) { assetIndex ->
                // Обработка продажи актива
                AlertDialog.Builder(this)
                    .setTitle("Продать актив?")
                    .setMessage("Вы уверены, что хотите продать этот актив?")
                    .setPositiveButton("Продать") { _, _ ->
                        if (gameManager.sellAsset(assetIndex)) {
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
        AlertDialog.Builder(this)
            .setTitle("Поздравляем!")
            .setMessage("Ваш пассивный доход превысил расходы! Вы можете выйти из крысиных бегов на скоростную дорожку!")
            .setPositiveButton("Перейти") { _, _ ->
                currentGameState?.player?.isInFastTrack = true
                updateUI()
                showMessage("Добро пожаловать на скоростную дорожку!")
            }
            .setNegativeButton("Остаться", null)
            .show()
    }
    
    private fun showMessage(message: String) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}