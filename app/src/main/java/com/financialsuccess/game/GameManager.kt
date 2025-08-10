package com.financialsuccess.game

import com.financialsuccess.game.models.*
import kotlin.random.Random
import java.util.Calendar

class GameManager {
    
    private var gameState: GameState? = null
    
    fun startNewGame(
        profession: Profession,
        dream: Dream,
        playerAge: Int = 25,
        playerName: String? = null,
        startDateMillis: Long? = null
    ): GameState {
        val player = Player(
            position = 0,
            cash = 100000, // Стартовый капитал (2025)
            salary = profession.salary,
            age = playerAge,
            profession = profession,
            dream = dream,
            name = playerName,
            startDateMillis = startDateMillis
        )
        
        // Устанавливаем случайный возраст смерти
        player.setRandomDeathAge()
        
        // Инициализируем доходы и расходы
        player.updateTotalIncome()
        player.updateTotalExpenses()
        
        // Добавляем начальную запись в журнал
        player.logIncome(
            FinancialCategory.GAME_START,
            5000,
            "Начальный капитал для старта игры"
        )
        
        gameState = GameState(player = player)
        return gameState!!
    }
    
    fun startNewGameWithPlayer(player: Player): GameState {
        player.updateSalaryWithBonuses()
        player.updateTotalIncome()
        player.updateTotalExpenses()
        player.logIncome(
            FinancialCategory.GAME_START,
            player.cash,
            "Начальный капитал для старта игры"
        )
        gameState = GameState(player = player)
        return gameState!!
    }
    
    fun rollDice(): Int = Random.nextInt(1, 7)
    
    fun movePlayer(steps: Int): GameState {
        val currentState = gameState ?: throw IllegalStateException("Game not started")
        val player = currentState.player

        // Текущая реальная игровая дата
        val cal = Calendar.getInstance().apply {
            if (player.startDateMillis != null) timeInMillis = player.startDateMillis!! else set(2024, Calendar.JANUARY, 1)
            add(Calendar.MONTH, player.monthsPlayed)
            set(Calendar.DAY_OF_MONTH, player.currentDayOfMonth)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
        }

        // Двигаем по дням
        repeat(steps) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
            player.currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH)
            // Новый месяц
            if (player.currentDayOfMonth == 1) {
                // Зарплата 1-го числа
                player.cash += player.salary
                player.logIncome(
                    FinancialCategory.SALARY,
                    player.salary,
                    "Ежемесячная зарплата по профессии ${player.profession.name}"
                )
                // Ежемесячные операции
                player.processMonthlyOperations()
                // Счётчик месяцев
                player.monthsPlayed++
            }
        }

        // Позиция теперь не критична для логики, но оставим инкремент для совместимости
        player.position = (player.position + steps) % 30

        // День рождения по точной дате
        val startDateMillis = player.startDateMillis
        val nextAgeChangeMillis = player.nextAgeChangeMillis
        if (startDateMillis != null && nextAgeChangeMillis != null) {
            if (cal.timeInMillis >= nextAgeChangeMillis) {
                player.age += 1
                val nb = Calendar.getInstance().apply {
                    timeInMillis = nextAgeChangeMillis
                    add(Calendar.YEAR, 1)
                }
                player.nextAgeChangeMillis = nb.timeInMillis
            }
        }

        // Выход из крысиных бегов
        if (!player.isInFastTrack && player.canEscapeRatRace()) {
            player.isInFastTrack = true
        }

        return currentState
    }
    
    fun buyAsset(asset: Asset): Boolean {
        val player = gameState?.player ?: return false
        if (player.cash >= asset.downPayment) {
            player.addAsset(asset)
            return true
        }
        return false
    }
    
    fun sellAsset(assetIndex: Int): Boolean {
        val player = gameState?.player ?: return false
        if (assetIndex in 0 until player.assets.size) {
            val asset = player.assets.removeAt(assetIndex)
            player.cash += asset.value
            player.updateTotalIncome()
            return true
        }
        return false
    }
    
    fun payOffLiability(liabilityIndex: Int): Boolean {
        val player = gameState?.player ?: return false
        if (liabilityIndex in 0 until player.liabilities.size) {
            val liability = player.liabilities[liabilityIndex]
            if (player.cash >= liability.amount) {
                player.cash -= liability.amount
                player.liabilities.removeAt(liabilityIndex)
                player.updateTotalExpenses()
                return true
            }
        }
        return false
    }
    
    fun getCurrentState(): GameState = gameState ?: throw IllegalStateException("Game not started")
    fun saveGameState(): String = ""
    fun loadGameState(): GameState? = null
}