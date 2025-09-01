package com.financialsuccess.game.models

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Тесты для LevelTransitionChecker
 */
class LevelTransitionCheckerTest {
    
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
    fun `test determine current level for NOVICE`() {
        val currentLevel = LevelTransitionChecker.determineCurrentLevel(player)
        assertEquals(PlayerLevel.NOVICE, currentLevel)
    }
    
    @Test
    fun `test determine current level for AUTONOMOUS`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
        val currentLevel = LevelTransitionChecker.determineCurrentLevel(player)
        assertEquals(PlayerLevel.AUTONOMOUS, currentLevel)
    }
    
    @Test
    fun `test determine current level for INVESTOR`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
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
        
        val currentLevel = LevelTransitionChecker.determineCurrentLevel(player)
        assertEquals(PlayerLevel.INVESTOR, currentLevel)
    }
    
    @Test
    fun `test determine current level for PASSIVE_INCOME`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 25000
        player.monthsPlayed = 10
        
        val currentLevel = LevelTransitionChecker.determineCurrentLevel(player)
        assertEquals(PlayerLevel.PASSIVE_INCOME, currentLevel)
    }
    
    @Test
    fun `test determine current level for FINANCIAL_MASTER`() {
        player.cash = 100000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 40000
        player.monthsPlayed = 10
        
        val currentLevel = LevelTransitionChecker.determineCurrentLevel(player)
        assertEquals(PlayerLevel.FINANCIAL_MASTER, currentLevel)
    }
    
    @Test
    fun `test can transition to NOVICE level`() {
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.NOVICE)
        assertTrue(canTransition)
    }
    
    @Test
    fun `test can transition to AUTONOMOUS level with conditions met`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.AUTONOMOUS)
        assertTrue(canTransition)
    }
    
    @Test
    fun `test cannot transition to AUTONOMOUS level without education`() {
        player.cash = 25000
        player.education = EducationLevel.HIGH_SCHOOL
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.AUTONOMOUS)
        assertFalse(canTransition)
    }
    
    @Test
    fun `test cannot transition to AUTONOMOUS level without sufficient cash`() {
        player.cash = 15000
        player.education = EducationLevel.BACHELOR
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.AUTONOMOUS)
        assertFalse(canTransition)
    }
    
    @Test
    fun `test can transition to INVESTOR level with conditions met`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
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
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.INVESTOR)
        assertTrue(canTransition)
    }
    
    @Test
    fun `test cannot transition to INVESTOR level without large asset`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
        val asset = Asset(
            type = AssetType.REAL_ESTATE,
            name = "Маленькая квартира",
            value = 30000, // Меньше 50000
            cashFlow = 2000,
            downPayment = 10000,
            loan = 20000,
            loanPayment = 1500
        )
        player.addAsset(asset)
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.INVESTOR)
        assertFalse(canTransition)
    }
    
    @Test
    fun `test can transition to PASSIVE_INCOME level with conditions met`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 25000
        player.monthsPlayed = 10
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.PASSIVE_INCOME)
        assertTrue(canTransition)
    }
    
    @Test
    fun `test cannot transition to PASSIVE_INCOME level without sufficient passive income ratio`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 15000 // 30% от зарплаты 50000, требуется 50%
        player.monthsPlayed = 10
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.PASSIVE_INCOME)
        assertFalse(canTransition)
    }
    
    @Test
    fun `test can transition to FINANCIAL_MASTER level with conditions met`() {
        player.cash = 100000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 40000
        player.monthsPlayed = 10
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.FINANCIAL_MASTER)
        assertTrue(canTransition)
    }
    
    @Test
    fun `test cannot transition to FINANCIAL_MASTER level without sufficient capital`() {
        player.cash = 50000 // Меньше 100000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 40000
        player.monthsPlayed = 10
        
        val canTransition = LevelTransitionChecker.canTransitionToLevel(player, PlayerLevel.FINANCIAL_MASTER)
        assertFalse(canTransition)
    }
    
    @Test
    fun `test level progress for NOVICE`() {
        val progress = LevelTransitionChecker.getLevelProgress(player, PlayerLevel.NOVICE)
        
        assertEquals(100, progress.progress)
        assertEquals("Новичок - базовый уровень", progress.description)
    }
    
    @Test
    fun `test level progress for AUTONOMOUS with partial conditions`() {
        player.cash = 10000 // 50% от требуемых 20000
        player.education = EducationLevel.BACHELOR
        
        val progress = LevelTransitionChecker.getLevelProgress(player, PlayerLevel.AUTONOMOUS)
        
        assertEquals(75, progress.progress) // (50 + 100) / 2 = 75
        assertTrue(progress.description.contains("Накопить 20000₽"))
    }
    
    @Test
    fun `test level progress for INVESTOR with partial conditions`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
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
        
        val progress = LevelTransitionChecker.getLevelProgress(player, PlayerLevel.INVESTOR)
        
        assertEquals(25, progress.progress) // (50 + 0) / 2 = 25 (50% инвестиций, нет крупного актива)
        assertTrue(progress.description.contains("Инвестировать 10% капитала"))
    }
    
    @Test
    fun `test level progress for PASSIVE_INCOME with partial conditions`() {
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 15000 // 30% от зарплаты 50000
        player.monthsPlayed = 3 // 60% от требуемых 5 месяцев
        
        val progress = LevelTransitionChecker.getLevelProgress(player, PlayerLevel.PASSIVE_INCOME)
        
        assertEquals(45, progress.progress) // (60 + 30) / 2 = 45
        assertTrue(progress.description.contains("Пассивный доход 50% от общего дохода"))
    }
    
    @Test
    fun `test level progress for FINANCIAL_MASTER with partial conditions`() {
        player.cash = 50000 // 50% от требуемых 100000
        player.education = EducationLevel.BACHELOR
        
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
        
        player.passiveIncome = 30000 // Не покрывает расходы 35000
        
        val progress = LevelTransitionChecker.getLevelProgress(player, PlayerLevel.FINANCIAL_MASTER)
        
        assertEquals(25, progress.progress) // (0 + 50) / 2 = 25 (нет покрытия расходов, 50% капитала)
        assertTrue(progress.description.contains("Пассивный доход покрывает расходы"))
    }
}
