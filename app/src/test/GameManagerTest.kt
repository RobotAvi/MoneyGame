package com.financialsuccess.game

import com.financialsuccess.game.models.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GameManagerTest {
    
    private lateinit var gameManager: GameManager
    private lateinit var profession: Profession
    private lateinit var dream: Dream
    private lateinit var player: Player
    
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
        
        player = Player(
            name = "Тестовый игрок",
            age = 25,
            profession = profession,
            dream = dream,
            cash = 100000,
            education = EducationLevel.BACHELOR,
            workExperience = 2,
            maritalStatus = MaritalStatus.SINGLE,
            childrenCount = 0,
            spouseIncome = 0,
            riskTolerance = RiskTolerance.MODERATE,
            investmentStrategy = InvestmentStrategy.BALANCED,
            savingsRate = 10,
            healthLevel = HealthLevel.GOOD,
            stressLevel = StressLevel.LOW,
            workLifeBalance = WorkLifeBalance.BALANCED
        )
    }
    
    // === ТЕСТЫ СОЗДАНИЯ ИГРЫ ===
    
    @Test
    fun `test start new game with profession and dream`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        assertNotNull(gameState)
        assertNotNull(gameState.player)
        assertEquals(profession, gameState.player.profession)
        assertEquals(dream, gameState.player.dream)
        assertEquals(0, gameState.player.position)
        assertEquals(profession.salary, gameState.player.salary)
        assertEquals(profession.expenses, gameState.player.totalExpenses)
        assertEquals(profession.taxes, gameState.player.taxes)
        assertFalse(gameState.player.isInFastTrack)
    }
    
    @Test
    fun `test start new game with player`() {
        val gameState = gameManager.startNewGameWithPlayer(player)
        
        assertNotNull(gameState)
        assertNotNull(gameState.player)
        assertEquals(player.name, gameState.player.name)
        assertEquals(player.age, gameState.player.age)
        assertEquals(player.profession, gameState.player.profession)
        assertEquals(player.dream, gameState.player.dream)
        assertEquals(player.education, gameState.player.education)
        assertEquals(player.workExperience, gameState.player.workExperience)
        assertEquals(player.maritalStatus, gameState.player.maritalStatus)
        assertEquals(player.childrenCount, gameState.player.childrenCount)
        assertEquals(player.spouseIncome, gameState.player.spouseIncome)
        assertEquals(player.riskTolerance, gameState.player.riskTolerance)
        assertEquals(player.investmentStrategy, gameState.player.investmentStrategy)
        assertEquals(player.savingsRate, gameState.player.savingsRate)
        assertEquals(player.healthLevel, gameState.player.healthLevel)
        assertEquals(player.stressLevel, gameState.player.stressLevel)
        assertEquals(player.workLifeBalance, gameState.player.workLifeBalance)
        assertEquals(0, gameState.player.position)
        assertFalse(gameState.player.isInFastTrack)
    }
    
    @Test
    fun `test start new game initializes player correctly`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val player = gameState.player
        
        // Проверяем базовые параметры
        assertEquals(0, player.position)
        assertEquals(profession.salary, player.salary)
        assertEquals(profession.expenses, player.totalExpenses)
        assertEquals(profession.taxes, player.taxes)
        
        // Проверяем расширенные параметры по умолчанию
        assertEquals(EducationLevel.BACHELOR, player.education)
        assertEquals(0, player.workExperience)
        assertEquals(MaritalStatus.SINGLE, player.maritalStatus)
        assertEquals(0, player.childrenCount)
        assertEquals(0, player.spouseIncome)
        assertEquals(RiskTolerance.MODERATE, player.riskTolerance)
        assertEquals(InvestmentStrategy.BALANCED, player.investmentStrategy)
        assertEquals(10, player.savingsRate)
        assertEquals(HealthLevel.GOOD, player.healthLevel)
        assertEquals(StressLevel.LOW, player.stressLevel)
        assertEquals(WorkLifeBalance.BALANCED, player.workLifeBalance)
        
        // Проверяем инициализацию доходов и расходов
        player.updateTotalIncome()
        player.updateTotalExpenses()
        
        assertEquals(profession.salary, player.totalIncome)
        assertTrue(player.totalExpenses > 0)
    }
    
    // === ТЕСТЫ ДВИЖЕНИЯ ИГРОКА ===
    
    @Test
    fun `test move player forward`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val initialPosition = gameState.player.position
        
        val newState = gameManager.movePlayer(3)
        
        assertNotNull(newState)
        assertEquals(initialPosition + 3, newState.player.position)
    }
    
    @Test
    fun `test move player around board`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.position = 25 // Близко к концу доски
        
        val newState = gameManager.movePlayer(10)
        
        assertNotNull(newState)
        // Позиция должна обернуться вокруг доски
        assertEquals(5, newState.player.position) // 25 + 10 - 30 = 5
    }
    
    @Test
    fun `test move player to fast track`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.position = 28 // Близко к быстрой дорожке
        
        val newState = gameManager.movePlayer(3)
        
        assertNotNull(newState)
        assertEquals(1, newState.player.position) // Позиция на быстрой дорожке
        assertTrue(newState.player.isInFastTrack)
    }
    
    @Test
    fun `test move player on fast track`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.isInFastTrack = true
        gameState.player.position = 5
        
        val newState = gameManager.movePlayer(3)
        
        assertNotNull(newState)
        assertEquals(8, newState.player.position)
        assertTrue(newState.player.isInFastTrack)
    }
    
    // === ТЕСТЫ УПРАВЛЕНИЯ АКТИВАМИ ===
    
    @Test
    fun `test buy asset with sufficient cash`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 100000
        
        val asset = Asset(
            name = "Акции",
            downPayment = 50000,
            cashFlow = 2000,
            loan = 0,
            loanPayment = 0
        )
        
        val result = gameManager.buyAsset(asset)
        
        assertTrue(result)
        assertTrue(gameState.player.assets.contains(asset))
        assertEquals(50000, gameState.player.cash) // 100000 - 50000
    }
    
    @Test
    fun `test buy asset with insufficient cash`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 30000
        
        val asset = Asset(
            name = "Акции",
            downPayment = 50000,
            cashFlow = 2000,
            loan = 0,
            loanPayment = 0
        )
        
        val result = gameManager.buyAsset(asset)
        
        assertFalse(result)
        assertFalse(gameState.player.assets.contains(asset))
        assertEquals(30000, gameState.player.cash) // Деньги не изменились
    }
    
    @Test
    fun `test buy asset with loan`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 100000
        
        val asset = Asset(
            name = "Недвижимость",
            downPayment = 50000,
            cashFlow = 3000,
            loan = 200000,
            loanPayment = 5000
        )
        
        val result = gameManager.buyAsset(asset)
        
        assertTrue(result)
        assertTrue(gameState.player.assets.contains(asset))
        assertEquals(50000, gameState.player.cash) // 100000 - 50000
        assertEquals(1, gameState.player.liabilities.size)
        
        val liability = gameState.player.liabilities.first()
        assertEquals("Кредит на Недвижимость", liability.name)
        assertEquals(200000, liability.amount)
        assertEquals(5000, liability.payment)
    }
    
    @Test
    fun `test sell asset`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 100000
        
        val asset = Asset(
            name = "Акции",
            downPayment = 50000,
            cashFlow = 2000,
            loan = 0,
            loanPayment = 0
        )
        
        // Сначала покупаем актив
        gameManager.buyAsset(asset)
        val initialCash = gameState.player.cash
        val initialAssetsCount = gameState.player.assets.size
        
        // Затем продаем его
        val result = gameManager.sellAsset(0)
        
        assertTrue(result)
        assertEquals(initialAssetsCount - 1, gameState.player.assets.size)
        assertEquals(initialCash + 50000, gameState.player.cash) // Возвращаем первоначальный взнос
    }
    
    @Test
    fun `test sell asset with invalid index`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        val result = gameManager.sellAsset(5) // Несуществующий индекс
        
        assertFalse(result)
    }
    
    // === ТЕСТЫ УПРАВЛЕНИЯ ПАССИВАМИ ===
    
    @Test
    fun `test pay off liability with sufficient cash`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 100000
        
        val liability = Liability(
            name = "Кредит",
            amount = 50000,
            payment = 2000
        )
        
        gameState.player.liabilities.add(liability)
        val initialCash = gameState.player.cash
        val initialLiabilitiesCount = gameState.player.liabilities.size
        
        val result = gameManager.payOffLiability(0)
        
        assertTrue(result)
        assertEquals(initialLiabilitiesCount - 1, gameState.player.liabilities.size)
        assertEquals(initialCash - 50000, gameState.player.cash)
    }
    
    @Test
    fun `test pay off liability with insufficient cash`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 30000
        
        val liability = Liability(
            name = "Кредит",
            amount = 50000,
            payment = 2000
        )
        
        gameState.player.liabilities.add(liability)
        val initialCash = gameState.player.cash
        val initialLiabilitiesCount = gameState.player.liabilities.size
        
        val result = gameManager.payOffLiability(0)
        
        assertFalse(result)
        assertEquals(initialLiabilitiesCount, gameState.player.liabilities.size)
        assertEquals(initialCash, gameState.player.cash)
    }
    
    @Test
    fun `test pay off liability with invalid index`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        val result = gameManager.payOffLiability(5) // Несуществующий индекс
        
        assertFalse(result)
    }
    
    // === ТЕСТЫ ИГРОВОГО СОСТОЯНИЯ ===
    
    @Test
    fun `test get current game state`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        val currentState = gameManager.getCurrentGameState()
        
        assertNotNull(currentState)
        assertEquals(gameState.player, currentState.player)
        assertEquals(gameState.player.position, currentState.player.position)
        assertEquals(gameState.player.cash, currentState.player.cash)
    }
    
    @Test
    fun `test game state is initialized correctly`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        assertNotNull(gameState.player)
        assertEquals(0, gameState.player.position)
        assertEquals(profession.salary, gameState.player.salary)
        assertEquals(profession.expenses, gameState.player.totalExpenses)
        assertEquals(profession.taxes, gameState.player.taxes)
        assertFalse(gameState.player.isInFastTrack)
        assertTrue(gameState.player.isAlive())
    }
    
    // === ТЕСТЫ РАСШИРЕННЫХ ВОЗМОЖНОСТЕЙ ===
    
    @Test
    fun `test player with education and experience bonuses`() {
        val playerWithBonuses = Player(
            name = "Опытный игрок",
            age = 30,
            profession = profession,
            dream = dream,
            education = EducationLevel.MASTER,
            workExperience = 5,
            skills = mutableListOf(
                Skill("Программирование", "Технический навык", 15000, SkillCategory.TECHNICAL)
            )
        )
        
        val gameState = gameManager.startNewGameWithPlayer(playerWithBonuses)
        
        // Обновляем зарплату с бонусами
        gameState.player.updateSalaryWithBonuses()
        
        val expectedSalary = profession.salary + // базовая зарплата
                            15000 + // бонус за образование (MASTER)
                            15000 + // бонус за навык
                            5000    // бонус за опыт (5 лет * 1000)
        
        assertEquals(expectedSalary, gameState.player.salary)
    }
    
    @Test
    fun `test player with family expenses`() {
        val playerWithFamily = Player(
            name = "Семейный игрок",
            age = 35,
            profession = profession,
            dream = dream,
            maritalStatus = MaritalStatus.MARRIED,
            childrenCount = 2,
            spouseIncome = 40000
        )
        
        val gameState = gameManager.startNewGameWithPlayer(playerWithFamily)
        
        gameState.player.updateTotalIncome()
        gameState.player.updateTotalExpenses()
        
        // Проверяем доход супруга
        assertEquals(profession.salary + 40000, gameState.player.totalIncome)
        
        // Проверяем семейные расходы
        val familyExpenses = gameState.player.calculateFamilyExpenses()
        assertEquals(21000, familyExpenses) // 2 * 8000 + 5000
    }
    
    @Test
    fun `test player with health modifiers`() {
        val playerWithHealth = Player(
            name = "Игрок с проблемами здоровья",
            age = 40,
            profession = profession,
            dream = dream,
            healthLevel = HealthLevel.POOR
        )
        
        val gameState = gameManager.startNewGameWithPlayer(playerWithHealth)
        
        gameState.player.updateTotalExpenses()
        
        // При плохом здоровье расходы должны быть увеличены на 50%
        val baseExpenses = profession.expenses
        val expectedExpenses = (baseExpenses * 1.5).toInt()
        
        assertTrue(gameState.player.totalExpenses >= expectedExpenses)
    }
    
    @Test
    fun `test player with savings rate`() {
        val playerWithSavings = Player(
            name = "Игрок со сбережениями",
            age = 28,
            profession = profession,
            dream = dream,
            savingsRate = 20,
            cash = 100000
        )
        
        val gameState = gameManager.startNewGameWithPlayer(playerWithSavings)
        
        gameState.player.updateTotalIncome()
        gameState.player.updateTotalExpenses()
        
        val initialCash = gameState.player.cash
        val initialInvested = gameState.player.totalInvested
        
        gameState.player.applySavings()
        
        val expectedSavings = (gameState.player.totalIncome * 20) / 100
        assertEquals(initialCash - expectedSavings, gameState.player.cash)
        assertEquals(initialInvested + expectedSavings, gameState.player.totalInvested)
    }
    
    // === ТЕСТЫ ГРАНИЧНЫХ СЛУЧАЕВ ===
    
    @Test
    fun `test game manager with null profession`() {
        val result = gameManager.startNewGame(null, dream)
        
        assertNotNull(result)
        assertNotNull(result.player)
        assertNull(result.player.profession)
        assertEquals(0, result.player.salary)
        assertEquals(0, result.player.totalExpenses)
    }
    
    @Test
    fun `test game manager with null dream`() {
        val result = gameManager.startNewGame(profession, null)
        
        assertNotNull(result)
        assertNotNull(result.player)
        assertNull(result.player.dream)
    }
    
    @Test
    fun `test move player with zero steps`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val initialPosition = gameState.player.position
        
        val newState = gameManager.movePlayer(0)
        
        assertNotNull(newState)
        assertEquals(initialPosition, newState.player.position)
    }
    
    @Test
    fun `test move player with negative steps`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.position = 10
        
        val newState = gameManager.movePlayer(-3)
        
        assertNotNull(newState)
        assertEquals(7, newState.player.position)
    }
    
    @Test
    fun `test buy asset with zero down payment`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 1000
        
        val asset = Asset(
            name = "Бесплатный актив",
            downPayment = 0,
            cashFlow = 100,
            loan = 0,
            loanPayment = 0
        )
        
        val result = gameManager.buyAsset(asset)
        
        assertTrue(result)
        assertTrue(gameState.player.assets.contains(asset))
        assertEquals(1000, gameState.player.cash) // Деньги не изменились
    }
    
    @Test
    fun `test buy asset with negative cash flow`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 100000
        
        val asset = Asset(
            name = "Убыточный актив",
            downPayment = 50000,
            cashFlow = -2000, // Отрицательный денежный поток
            loan = 0,
            loanPayment = 0
        )
        
        val result = gameManager.buyAsset(asset)
        
        assertTrue(result)
        assertTrue(gameState.player.assets.contains(asset))
        assertEquals(50000, gameState.player.cash)
    }
    
    // === ТЕСТЫ ПРОИЗВОДИТЕЛЬНОСТИ ===
    
    @Test
    fun `test multiple moves performance`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        // Выполняем много ходов подряд
        repeat(100) { i ->
            val newState = gameManager.movePlayer(1)
            assertNotNull(newState)
            assertEquals((i + 1) % 30, newState.player.position)
        }
    }
    
    @Test
    fun `test multiple asset purchases performance`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 1000000
        
        val asset = Asset(
            name = "Тестовый актив",
            downPayment = 10000,
            cashFlow = 500,
            loan = 0,
            loanPayment = 0
        )
        
        // Покупаем много активов
        repeat(50) { i ->
            val result = gameManager.buyAsset(asset)
            assertTrue(result)
            assertEquals(i + 1, gameState.player.assets.size)
        }
    }
    
    // === ТЕСТЫ ИНТЕГРАЦИИ ===
    
    @Test
    fun `test complete game flow`() {
        // Создаем игру
        val gameState = gameManager.startNewGame(profession, dream)
        
        // Двигаемся по доске
        var currentState = gameManager.movePlayer(5)
        assertEquals(5, currentState.player.position)
        
        // Покупаем актив
        val asset = Asset(
            name = "Акции",
            downPayment = 50000,
            cashFlow = 2000,
            loan = 0,
            loanPayment = 0
        )
        currentState.player.cash = 100000
        val buyResult = gameManager.buyAsset(asset)
        assertTrue(buyResult)
        
        // Проверяем, что актив добавлен
        assertEquals(1, currentState.player.assets.size)
        assertEquals(50000, currentState.player.cash)
        
        // Продаем актив
        val sellResult = gameManager.sellAsset(0)
        assertTrue(sellResult)
        assertEquals(0, currentState.player.assets.size)
        assertEquals(100000, currentState.player.cash)
    }
    
    @Test
    fun `test player with all extended features`() {
        val comprehensivePlayer = Player(
            name = "Комплексный игрок",
            age = 35,
            profession = profession,
            dream = dream,
            education = EducationLevel.PHD,
            workExperience = 10,
            skills = mutableListOf(
                Skill("Программирование", "Технический навык", 15000, SkillCategory.TECHNICAL),
                Skill("Менеджмент", "Лидерский навык", 12000, SkillCategory.LEADERSHIP),
                Skill("Финансовая грамотность", "Финансовый навык", 11000, SkillCategory.FINANCIAL)
            ),
            maritalStatus = MaritalStatus.MARRIED,
            childrenCount = 2,
            spouseIncome = 60000,
            riskTolerance = RiskTolerance.AGGRESSIVE,
            investmentStrategy = InvestmentStrategy.AGGRESSIVE,
            savingsRate = 25,
            retirementAge = 55,
            targetPassiveIncome = 200000,
            healthLevel = HealthLevel.EXCELLENT,
            stressLevel = StressLevel.LOW,
            workLifeBalance = WorkLifeBalance.BALANCED,
            cash = 200000
        )
        
        val gameState = gameManager.startNewGameWithPlayer(comprehensivePlayer)
        
        // Проверяем все параметры
        assertEquals("Комплексный игрок", gameState.player.name)
        assertEquals(35, gameState.player.age)
        assertEquals(EducationLevel.PHD, gameState.player.education)
        assertEquals(10, gameState.player.workExperience)
        assertEquals(3, gameState.player.skills.size)
        assertEquals(MaritalStatus.MARRIED, gameState.player.maritalStatus)
        assertEquals(2, gameState.player.childrenCount)
        assertEquals(60000, gameState.player.spouseIncome)
        assertEquals(RiskTolerance.AGGRESSIVE, gameState.player.riskTolerance)
        assertEquals(InvestmentStrategy.AGGRESSIVE, gameState.player.investmentStrategy)
        assertEquals(25, gameState.player.savingsRate)
        assertEquals(55, gameState.player.retirementAge)
        assertEquals(200000, gameState.player.targetPassiveIncome)
        assertEquals(HealthLevel.EXCELLENT, gameState.player.healthLevel)
        assertEquals(StressLevel.LOW, gameState.player.stressLevel)
        assertEquals(WorkLifeBalance.BALANCED, gameState.player.workLifeBalance)
        assertEquals(200000, gameState.player.cash)
        
        // Обновляем зарплату с бонусами
        gameState.player.updateSalaryWithBonuses()
        gameState.player.updateTotalIncome()
        gameState.player.updateTotalExpenses()
        
        // Проверяем расчеты
        val expectedSalary = profession.salary + // базовая зарплата
                            20000 + // бонус за образование (PHD)
                            38000 + // бонус за навыки (15000 + 12000 + 11000)
                            10000   // бонус за опыт (10 лет * 1000)
        
        assertEquals(expectedSalary, gameState.player.salary)
        assertEquals(expectedSalary + 60000, gameState.player.totalIncome) // зарплата + доход супруга
    }
}