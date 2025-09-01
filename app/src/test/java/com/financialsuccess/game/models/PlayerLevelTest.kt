package com.financialsuccess.game.models

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Тесты для системы уровней зрелости игрока
 */
class PlayerLevelTest {
    
    private lateinit var player: Player
    private lateinit var teacherProfession: Profession
    private lateinit var dream: Dream
    
    @Before
    fun setUp() {
        teacherProfession = Profession(
            id = "teacher",
            name = "Учитель",
            description = "Преподаватель в школе",
            salary = 50000,
            expenses = 30000,
            taxes = 5000,
            education = "Высшее"
        )
        
        dream = Dream(
            id = "travel",
            name = "Путешествие по миру",
            description = "Посетить все континенты",
            cost = 1000000,
            cashFlowRequired = 50000
        )
        
        player = Player(
            profession = teacherProfession,
            dream = dream
        )
    }
    
    @Test
    fun `test initial level is NOVICE`() {
        assertEquals(PlayerLevel.NOVICE, player.currentLevel)
    }
    
    @Test
    fun `test level transition to AUTONOMOUS with sufficient cash and education`() {
        // Даем игроку достаточно денег и образование
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
        player.updateLevel()
        
        assertEquals(PlayerLevel.AUTONOMOUS, player.currentLevel)
    }
    
    @Test
    fun `test level transition to AUTONOMOUS fails without education`() {
        // Даем игроку достаточно денег, но нет образования
        player.cash = 25000
        player.education = EducationLevel.HIGH_SCHOOL
        
        player.updateLevel()
        
        assertEquals(PlayerLevel.NOVICE, player.currentLevel)
    }
    
    @Test
    fun `test level transition to AUTONOMOUS fails without sufficient cash`() {
        // Даем игроку образование, но недостаточно денег
        player.cash = 15000
        player.education = EducationLevel.BACHELOR
        
        player.updateLevel()
        
        assertEquals(PlayerLevel.NOVICE, player.currentLevel)
    }
    
    @Test
    fun `test level transition to INVESTOR with investments and large asset`() {
        // Сначала переходим на уровень Автономный
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        player.updateLevel()
        
        // Добавляем инвестиции и крупный актив
        val asset = Asset(
            type = AssetType.REAL_ESTATE,
            name = "Квартира",

            value = 60000,
            cashFlow = 5000,
            downPayment = 20000,
            loan = 40000,
            loanPayment = 3000
        )
        player.addAsset(asset)
        
        player.updateLevel()
        
        assertEquals(PlayerLevel.INVESTOR, player.currentLevel)
    }
    
    @Test
    fun `test level transition to PASSIVE_INCOME with passive income ratio`() {
        // Переходим на уровень Инвестор
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        player.updateLevel()
        
        val asset = Asset(
            type = AssetType.REAL_ESTATE,
            name = "Квартира",

            value = 60000,
            cashFlow = 5000,
            downPayment = 20000,
            loan = 40000,
            loanPayment = 3000
        )
        player.addAsset(asset)
        player.updateLevel()
        
        // Устанавливаем пассивный доход 50% от общего дохода
        player.passiveIncome = 25000 // 50% от зарплаты 50000
        player.monthsPlayed = 10 // Больше 5 месяцев
        
        player.updateLevel()
        
        assertEquals(PlayerLevel.PASSIVE_INCOME, player.currentLevel)
    }
    
    @Test
    fun `test level transition to FINANCIAL_MASTER with passive income covering expenses`() {
        // Переходим на уровень Пассивный доход
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        player.updateLevel()
        
        val asset = Asset(
            type = AssetType.REAL_ESTATE,
            name = "Квартира",

            value = 60000,
            cashFlow = 5000,
            downPayment = 20000,
            loan = 40000,
            loanPayment = 3000
        )
        player.addAsset(asset)
        player.updateLevel()
        
        player.passiveIncome = 25000
        player.monthsPlayed = 10
        player.updateLevel()
        
        // Устанавливаем пассивный доход покрывающий расходы и капитал 100,000
        player.passiveIncome = 40000 // Больше расходов 35000
        player.cash = 100000 // Капитал 100,000
        
        player.updateLevel()
        
        assertEquals(PlayerLevel.FINANCIAL_MASTER, player.currentLevel)
    }
    
    @Test
    fun `test level progress calculation for NOVICE`() {
        val progress = player.getLevelProgress()
        
        assertEquals(100, progress.progress)
        assertEquals("Новичок - базовый уровень", progress.description)
    }
    
    @Test
    fun `test level progress calculation for AUTONOMOUS`() {
        player.cash = 10000 // 50% от требуемых 20000
        player.education = EducationLevel.BACHELOR
        
        val progress = player.getLevelProgress()
        
        assertEquals(75, progress.progress) // (50 + 100) / 2 = 75
        assertTrue(progress.description.contains("Накопить 20000₽"))
    }
    
    @Test
    fun `test level progress calculation for INVESTOR`() {
        // Переходим на уровень Автономный
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        player.updateLevel()
        
        // Добавляем инвестиции на 5% от капитала (требуется 10%)
        val asset = Asset(
            type = AssetType.REAL_ESTATE,
            name = "Квартира",
            value = 2500, // 5% от капитала 50000
            cashFlow = 200,
            downPayment = 1000,
            loan = 1500,
            loanPayment = 100
        )
        player.addAsset(asset)
        
        val progress = player.getLevelProgress()
        
        assertEquals(25, progress.progress) // (50 + 0) / 2 = 25 (50% инвестиций, нет крупного актива)
        assertTrue(progress.description.contains("Инвестировать 10% капитала"))
    }
    
    @Test
    fun `test level counters update correctly`() {
        // Имитируем ход без дефицита бюджета
        player.cash = 1000
        player.updateLevelCounters()
        
        assertEquals(1, player.consecutiveTurnsWithoutDeficit)
        assertEquals(0, player.consecutiveInvestmentTurns)
        
        // Имитируем ход с дефицитом бюджета
        player.cash = -1000
        player.updateLevelCounters()
        
        assertEquals(0, player.consecutiveTurnsWithoutDeficit)
        assertEquals(0, player.consecutiveInvestmentTurns)
        
        // Имитируем ход с инвестициями
        val asset = Asset(
            type = AssetType.REAL_ESTATE,
            name = "Квартира",
            value = 50000,
            cashFlow = 3000,
            downPayment = 15000,
            loan = 35000,
            loanPayment = 2500
        )
        player.addAsset(asset)
        player.cash = 1000
        player.updateLevelCounters()
        
        assertEquals(1, player.consecutiveTurnsWithoutDeficit)
        assertEquals(1, player.consecutiveInvestmentTurns)
    }
    
    @Test
    fun `test level info display`() {
        val levelInfo = player.getLevelInfo()
        
        assertTrue(levelInfo.contains("🟢 Новичок"))
        assertTrue(levelInfo.contains("Прогресс к следующему уровню"))
        assertTrue(levelInfo.contains("Новичок - базовый уровень"))
    }
    
    @Test
    fun `test can level up method`() {
        // Изначально не может повысить уровень
        assertFalse(player.canLevelUp())
        
        // Даем условия для повышения уровня
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
        assertTrue(player.canLevelUp())
    }
    
    @Test
    fun `test get next level method`() {
        assertEquals(PlayerLevel.AUTONOMOUS, player.getNextLevel())
        
        player.currentLevel = PlayerLevel.AUTONOMOUS
        assertEquals(PlayerLevel.INVESTOR, player.getNextLevel())
        
        player.currentLevel = PlayerLevel.FINANCIAL_MASTER
        assertEquals(PlayerLevel.FINANCIAL_MASTER, player.getNextLevel()) // Максимальный уровень
    }
}
