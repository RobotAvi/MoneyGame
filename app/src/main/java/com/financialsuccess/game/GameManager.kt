package com.financialsuccess.game

import com.financialsuccess.game.models.*
import kotlin.random.Random

class GameManager {
    
    private var gameState: GameState? = null
    
    fun startNewGame(profession: Profession, dream: Dream, playerAge: Int = 25): GameState {
        val player = Player(
            position = 0,
            cash = 5000, // Стартовый капитал
            salary = profession.salary,
            age = playerAge,
            profession = profession,
            dream = dream
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
    
    fun rollDice(): Int {
        val diceValue = Random.nextInt(1, 7)
        return diceValue
    }
    
    fun movePlayer(steps: Int): GameState? {
        val currentState = gameState ?: return null
        val oldPosition = currentState.player.position
        val newPosition = (oldPosition + steps) % 24

        // Если прошли полный круг (вернулись на старт)
        val passedStart = (oldPosition + steps) >= 24
        if (passedStart) {
            currentState.player.passMonth()
            currentState.player.processMonthlyOperations()
        }

        currentState.player.position = newPosition

        // Проверяем, может ли игрок выйти из крысиных бегов
        if (!currentState.player.isInFastTrack && currentState.player.canEscapeRatRace()) {
            currentState.player.isInFastTrack = true
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
    
    fun getCurrentState(): GameState? = gameState
    
    fun saveGameState(): String {
        // Здесь можно добавить сериализацию в JSON для сохранения игры
        return ""
    }
    
    fun loadGameState(savedState: String): GameState? {
        // Здесь можно добавить десериализацию из JSON для загрузки игры
        return null
    }
}