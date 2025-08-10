package com.financialsuccess.game

import com.financialsuccess.game.models.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.Calendar

class GameManagerTest {
    private lateinit var gameManager: GameManager
    private lateinit var profession: Profession
    private lateinit var dream: Dream

    @Before
    fun setup() {
        gameManager = GameManager()
        profession = Profession(
            id = "engineer",
            name = "Инженер",
            description = "",
            salary = 100000,
            expenses = 50000,
            taxes = 20000,
            education = ""
        )
        dream = Dream(
            id = "island",
            name = "Остров",
            description = "",
            cost = 10_000_000,
            cashFlowRequired = 300000
        )
    }

    @Test
    fun `test player movement increments date`() {
        val state = gameManager.startNewGame(profession, dream)
        val dayBefore = state.player.currentDayOfMonth
        gameManager.movePlayer(3)
        assertEquals(dayBefore + 3, state.player.currentDayOfMonth)
    }

    @Test
    fun `test month progression by real calendar`() {
        val state = gameManager.startNewGame(profession, dream)
        // Установим стартовую дату на 1 августа 2025 для детерминизма
        val cal = Calendar.getInstance().apply { set(2025, Calendar.AUGUST, 1, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
        state.player.startDateMillis = cal.timeInMillis
        state.player.currentDayOfMonth = 1
        state.player.monthsPlayed = 0
        // В августе 31 день. Сдвигаем на 31 день, ожидаем 1 сентября (day=1, monthsPlayed=1)
        gameManager.movePlayer(31)
        assertEquals(1, state.player.currentDayOfMonth)
        assertEquals(1, state.player.monthsPlayed)
    }

    @Test
    fun `test salary on first day of month`() {
        val state = gameManager.startNewGame(profession, dream)
        val startCash = state.player.cash
        val salary = state.player.salary
        // Сдвигаем на 1 день от 1-го к 2-му, зарплаты не будет
        gameManager.movePlayer(1)
        assertEquals(startCash, state.player.cash)
        // Додвигаемся до 1-го числа следующего месяца: 29 дней (виртуальный старт Jan-1)
        gameManager.movePlayer(30)
        assertTrue(state.player.currentDayOfMonth == 1)
        assertEquals(startCash + salary, state.player.cash)
    }

    @Test
    fun `test age progression after 12 months`() {
        val state = gameManager.startNewGame(profession, dream)
        val initialAge = state.player.age
        // 12 раз до 1-го числа
        repeat(12) { gameManager.movePlayer(30) }
        assertEquals(initialAge + 1, state.player.age)
    }
}