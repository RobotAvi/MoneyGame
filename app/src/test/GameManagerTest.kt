package com.financialsuccess.game

import com.financialsuccess.game.models.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class GameManagerTest {
    
    private lateinit var gameManager: GameManager
    private lateinit var profession: Profession
    private lateinit var dream: Dream
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        gameManager = GameManager()
        profession = Profession(
            name = "Программист",
            salary = 80000,
            expenses = 40000,
            taxes = 12000
        )
        dream = Dream(
            name = "Коттедж",
            cost = 5000000,
            description = "Собственный дом"
        )
    }
    
    // === СОЗДАНИЕ ИГРЫ ===
    
    @Test
    fun `test start new game with profession and dream`() {
        val gameState = gameManager.startNewGame(profession, dream)
        assertNotNull(gameState.player)
        assertEquals(profession, gameState.player.profession)
        assertEquals(dream, gameState.player.dream)
        assertEquals(profession.salary, gameState.player.salary)
        assertEquals(profession.taxes, gameState.player.taxes)
        assertFalse(gameState.player.isInFastTrack)
    }
    
    // === ДВИЖЕНИЕ ПО КАЛЕНДАРЮ ===
    
    @Test
    fun `test move increments day`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val day = gameState.player.currentDayOfMonth
        gameManager.movePlayer(5)
        assertEquals(day + 5, gameState.player.currentDayOfMonth)
    }
    
    @Test
    fun `test month rollover by real calendar`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val cal = Calendar.getInstance().apply { set(2025, Calendar.AUGUST, 1, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
        gameState.player.startDateMillis = cal.timeInMillis
        gameState.player.currentDayOfMonth = 1
        gameState.player.monthsPlayed = 0
        // Август 2025 — 31 день
        gameManager.movePlayer(31)
        assertEquals(1, gameState.player.currentDayOfMonth)
        assertEquals(1, gameState.player.monthsPlayed)
    }
    
    @Test
    fun `test salary only on first day`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val startCash = gameState.player.cash
        // Двигаемся на 1 день — зарплаты нет
        gameManager.movePlayer(1)
        assertEquals(startCash, gameState.player.cash)
        // Двигаемся до следующего 1-го числа
        gameManager.movePlayer(30)
        assertEquals(1, gameState.player.currentDayOfMonth)
        assertEquals(startCash + gameState.player.salary, gameState.player.cash)
    }
    
    // === АКТИВЫ/ПАССИВЫ/ЖУРНАЛ ===
    
    @Test
    fun `test buy asset with sufficient cash`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 100000
        val asset = Asset(type = AssetType.REAL_ESTATE, name = "Акции", downPayment = 50000, cashFlow = 2000, loan = 0, loanPayment = 0)
        val result = gameManager.buyAsset(type = AssetType.REAL_ESTATE, asset)
        assertTrue(result)
        assertTrue(gameState.player.assets.contains(asset))
        assertEquals(50000, gameState.player.cash)
    }
}
