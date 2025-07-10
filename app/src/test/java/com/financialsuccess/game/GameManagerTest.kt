package com.financialsuccess.game

import com.financialsuccess.game.models.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class GameManagerTest {
    
    private lateinit var gameManager: GameManager
    private lateinit var profession: Profession
    private lateinit var dream: Dream
    private lateinit var player: Player
    
    @Before
    fun setUp() {
        gameManager = GameManager()
        
        profession = Profession(
            id = "test_engineer",
            name = "Тестовый инженер",
            description = "Тестовая профессия",
            salary = 80000,
            expenses = 35000,
            taxes = 15000,
            education = "Высшее техническое"
        )
        
        dream = Dream(
            id = "test_dream",
            name = "Тестовая мечта",
            description = "Тестовая мечта для тестов",
            cost = 1000000,
            cashFlowRequired = 50000
        )
        
        player = Player(
            name = "Тестовый игрок",
            age = 25,
            profession = profession,
            dream = dream,
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
            workLifeBalance = WorkLifeBalance.BALANCED,
            cash = 10000
        )
    }
    
    @Test
    fun `test start new game with basic parameters`() {
        val gameState = gameManager.startNewGame(
            profession = profession,
            dream = dream,
            playerAge = 25,
            playerName = "Тестовый игрок",
            startDateMillis = 1234567890L
        )
        
        assertNotNull(gameState)
        assertNotNull(gameState.player)
        assertEquals("Тестовый игрок", gameState.player.name)
        assertEquals(25, gameState.player.age)
        assertEquals(profession, gameState.player.profession)
        assertEquals(dream, gameState.player.dream)
        assertEquals(5000, gameState.player.cash) // Стартовый капитал
        assertEquals(80000, gameState.player.salary) // Базовая зарплата
        assertTrue(gameState.player.financialJournal.isNotEmpty()) // Должна быть начальная запись
    }
    
    @Test
    fun `test start new game with extended player`() {
        val gameState = gameManager.startNewGameWithPlayer(player)
        
        assertNotNull(gameState)
        assertNotNull(gameState.player)
        assertEquals("Тестовый игрок", gameState.player.name)
        assertEquals(25, gameState.player.age)
        assertEquals(profession, gameState.player.profession)
        assertEquals(dream, gameState.player.dream)
        assertEquals(10000, gameState.player.cash) // Сохраняется начальный капитал
        assertEquals(EducationLevel.BACHELOR, gameState.player.education)
        assertEquals(2, gameState.player.workExperience)
        assertEquals(MaritalStatus.SINGLE, gameState.player.maritalStatus)
        assertEquals(RiskTolerance.MODERATE, gameState.player.riskTolerance)
        assertEquals(10, gameState.player.savingsRate)
        assertTrue(gameState.player.financialJournal.isNotEmpty()) // Должна быть начальная запись
    }
    
    @Test
    fun `test dice roll range`() {
        repeat(100) {
            val diceValue = gameManager.rollDice()
            assertTrue("Dice value should be between 1 and 6", diceValue in 1..6)
        }
    }
    
    @Test
    fun `test player movement`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val initialPosition = gameState.player.position
        
        val newGameState = gameManager.movePlayer(3)
        
        assertNotNull(newGameState)
        assertEquals((initialPosition + 3) % 24, newGameState.player.position)
    }
    
    @Test
    fun `test player movement around board`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.position = 22 // Близко к концу доски
        
        val newGameState = gameManager.movePlayer(5)
        
        assertNotNull(newGameState)
        assertEquals(3, newGameState.player.position) // (22 + 5) % 24 = 3
    }
    
    @Test
    fun `test salary payment on full circle`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.position = 20 // 4 клетки до старта
        val initialCash = gameState.player.cash
        
        val newGameState = gameManager.movePlayer(4)
        
        assertNotNull(newGameState)
        assertEquals(0, newGameState.player.position) // Вернулись на старт
        assertEquals(initialCash + gameState.player.salary, newGameState.player.cash) // Получили зарплату
    }
    
    @Test
    fun `test month progression`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val initialMonth = gameState.player.monthsPlayed
        val initialDay = gameState.player.currentDayOfMonth
        
        // Двигаемся на 30 дней (полный месяц)
        val newGameState = gameManager.movePlayer(30)
        
        assertNotNull(newGameState)
        assertEquals(initialMonth + 1, newGameState.player.monthsPlayed)
        assertEquals(1, newGameState.player.currentDayOfMonth) // Новый месяц начинается с 1 дня
    }
    
    @Test
    fun `test age progression`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val initialAge = gameState.player.age
        
        // Проходим 12 месяцев (1 год)
        repeat(12) {
            gameManager.movePlayer(30)
        }
        
        assertEquals(initialAge + 1, gameState.player.age)
    }
    
    @Test
    fun `test fast track activation`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        // Создаем активы для достижения пассивного дохода
        val asset = Asset(
            name = "Тестовый актив",
            type = AssetType.REAL_ESTATE,
            downPayment = 100000,
            value = 500000,
            cashFlow = 10000
        )
        
        gameState.player.assets.add(asset)
        gameState.player.updateTotalIncome()
        
        // Проверяем, что игрок может выйти из крысиных бегов
        assertTrue(gameState.player.canEscapeRatRace())
        
        // Двигаемся и проверяем активацию скоростной дорожки
        val newGameState = gameManager.movePlayer(1)
        
        assertNotNull(newGameState)
        assertTrue(newGameState.player.isInFastTrack)
    }
    
    @Test
    fun `test asset purchase`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 200000 // Даем достаточно денег
        
        val asset = Asset(
            name = "Тестовый актив",
            type = AssetType.REAL_ESTATE,
            downPayment = 100000,
            value = 500000,
            cashFlow = 10000
        )
        
        val success = gameManager.buyAsset(asset)
        
        assertTrue(success)
        assertEquals(100000, gameState.player.cash) // Деньги списались
        assertEquals(1, gameState.player.assets.size) // Актив добавлен
        assertEquals(asset, gameState.player.assets[0])
    }
    
    @Test
    fun `test asset purchase insufficient funds`() {
        val gameState = gameManager.startNewGame(profession, dream)
        gameState.player.cash = 50000 // Недостаточно денег
        
        val asset = Asset(
            name = "Тестовый актив",
            type = AssetType.REAL_ESTATE,
            downPayment = 100000,
            value = 500000,
            cashFlow = 10000
        )
        
        val success = gameManager.buyAsset(asset)
        
        assertFalse(success)
        assertEquals(50000, gameState.player.cash) // Деньги не списались
        assertEquals(0, gameState.player.assets.size) // Актив не добавлен
    }
    
    @Test
    fun `test asset sale`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val asset = Asset(
            name = "Тестовый актив",
            type = AssetType.REAL_ESTATE,
            downPayment = 100000,
            value = 500000,
            cashFlow = 10000
        )
        
        gameState.player.assets.add(asset)
        val initialCash = gameState.player.cash
        
        val success = gameManager.sellAsset(0)
        
        assertTrue(success)
        assertEquals(initialCash + 500000, gameState.player.cash) // Получили стоимость актива
        assertEquals(0, gameState.player.assets.size) // Актив удален
    }
    
    @Test
    fun `test liability payoff`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val liability = Liability(
            name = "Тестовый кредит",
            amount = 100000,
            payment = 5000
        )
        
        gameState.player.liabilities.add(liability)
        gameState.player.cash = 200000 // Даем достаточно денег
        val initialCash = gameState.player.cash
        
        val success = gameManager.payOffLiability(0)
        
        assertTrue(success)
        assertEquals(initialCash - 100000, gameState.player.cash) // Деньги списались
        assertEquals(0, gameState.player.liabilities.size) // Кредит погашен
    }
    
    @Test
    fun `test liability payoff insufficient funds`() {
        val gameState = gameManager.startNewGame(profession, dream)
        val liability = Liability(
            name = "Тестовый кредит",
            amount = 100000,
            payment = 5000
        )
        
        gameState.player.liabilities.add(liability)
        gameState.player.cash = 50000 // Недостаточно денег
        val initialCash = gameState.player.cash
        
        val success = gameManager.payOffLiability(0)
        
        assertFalse(success)
        assertEquals(initialCash, gameState.player.cash) // Деньги не списались
        assertEquals(1, gameState.player.liabilities.size) // Кредит не погашен
    }
    
    @Test
    fun `test get current state`() {
        val gameState = gameManager.startNewGame(profession, dream)
        
        val currentState = gameManager.getCurrentState()
        
        assertNotNull(currentState)
        assertEquals(gameState, currentState)
    }
    
    @Test
    fun `test extended player with bonuses`() {
        // Создаем игрока с навыками и образованием
        val skills = listOf(
            Skill("Программирование", "Навыки разработки ПО", 15000, SkillCategory.TECHNICAL)
        )
        player.skills.addAll(skills)
        
        val gameState = gameManager.startNewGameWithPlayer(player)
        
        // Проверяем, что зарплата рассчитана с бонусами
        val expectedSalary = 80000 + 10000 + 15000 + 2000 // Базовая + образование + навык + опыт
        assertEquals(expectedSalary, gameState.player.salary)
        
        // Проверяем, что доходы обновлены
        assertEquals(expectedSalary, gameState.player.totalIncome)
    }
    
    @Test
    fun `test family expenses in extended player`() {
        // Создаем игрока с семьей
        player.maritalStatus = MaritalStatus.MARRIED
        player.childrenCount = 2
        player.spouseIncome = 50000
        
        val gameState = gameManager.startNewGameWithPlayer(player)
        
        // Проверяем, что доходы включают зарплату супруга
        assertTrue(gameState.player.totalIncome > gameState.player.salary)
        
        // Проверяем, что расходы включают семейные траты
        assertTrue(gameState.player.totalExpenses > 35000) // Больше базовых расходов профессии
    }
}