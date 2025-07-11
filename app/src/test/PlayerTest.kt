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
class PlayerTest {
    
    private lateinit var player: Player
    private lateinit var profession: Profession
    private lateinit var dream: Dream
    
    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        
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
    
    // === ТЕСТЫ ОСНОВНЫХ ПАРАМЕТРОВ ===
    
    @Test
    fun `test player initialization with default values`() {
        val defaultProfession = Profession(
            name = "Default",
            salary = 0,
            expenses = 0,
            taxes = 0
        )
        val defaultDream = Dream(
            name = "Default",
            cost = 0,
            description = ""
        )
        val defaultPlayer = Player(profession = defaultProfession, dream = defaultDream)
        
        assertEquals(25, defaultPlayer.age)
        assertEquals(0, defaultPlayer.cash)
        assertEquals(0, defaultPlayer.salary)
        assertEquals(EducationLevel.BACHELOR, defaultPlayer.education)
        assertEquals(MaritalStatus.SINGLE, defaultPlayer.maritalStatus)
        assertEquals(RiskTolerance.MODERATE, defaultPlayer.riskTolerance)
        assertEquals(10, defaultPlayer.savingsRate)
        assertEquals(65, defaultPlayer.retirementAge)
        assertEquals(100000, defaultPlayer.targetPassiveIncome)
    }
    
    @Test
    fun `test player initialization with custom values`() {
        assertEquals("Тестовый игрок", player.name)
        assertEquals(25, player.age)
        assertEquals(100000, player.cash)
        assertEquals(profession, player.profession)
        assertEquals(dream, player.dream)
        assertEquals(EducationLevel.BACHELOR, player.education)
        assertEquals(2, player.workExperience)
        assertEquals(MaritalStatus.SINGLE, player.maritalStatus)
        assertEquals(0, player.childrenCount)
        assertEquals(0, player.spouseIncome)
        assertEquals(RiskTolerance.MODERATE, player.riskTolerance)
        assertEquals(InvestmentStrategy.BALANCED, player.investmentStrategy)
        assertEquals(10, player.savingsRate)
        assertEquals(HealthLevel.GOOD, player.healthLevel)
        assertEquals(StressLevel.LOW, player.stressLevel)
        assertEquals(WorkLifeBalance.BALANCED, player.workLifeBalance)
    }
    
    // === ТЕСТЫ ФИНАНСОВЫХ РАСЧЕТОВ ===
    
    @Test
    fun `test net worth calculation`() {
        val asset = Asset(
            name = "Акции",
            downPayment = 50000,
            cashFlow = 2000,
            loan = 0,
            loanPayment = 0
        )
        val liability = Liability(
            name = "Кредит",
            amount = 100000,
            payment = 5000
        )
        
        player.assets.add(asset)
        player.liabilities.add(liability)
        
        val netWorth = player.getNetWorth()
        assertEquals(50000 - 100000, netWorth) // 50000 (актив) - 100000 (пассив)
    }
    
    @Test
    fun `test cash flow calculation`() {
        player.totalIncome = 80000
        player.passiveIncome = 5000
        player.totalExpenses = 40000
        
        val cashFlow = player.getCashFlow()
        assertEquals(45000, cashFlow) // 80000 + 5000 - 40000
    }
    
    @Test
    fun `test can escape rat race`() {
        player.passiveIncome = 50000
        player.totalExpenses = 40000
        
        assertTrue(player.canEscapeRatRace())
        
        player.passiveIncome = 30000
        assertFalse(player.canEscapeRatRace())
    }
    
    // === ТЕСТЫ ОБРАЗОВАНИЯ И ОПЫТА ===
    
    @Test
    fun `test education bonus calculation`() {
        player.education = EducationLevel.HIGH_SCHOOL
        assertEquals(0, player.calculateEducationBonus())
        
        player.education = EducationLevel.COLLEGE
        assertEquals(5000, player.calculateEducationBonus())
        
        player.education = EducationLevel.BACHELOR
        assertEquals(10000, player.calculateEducationBonus())
        
        player.education = EducationLevel.MASTER
        assertEquals(15000, player.calculateEducationBonus())
        
        player.education = EducationLevel.PHD
        assertEquals(20000, player.calculateEducationBonus())
    }
    
    @Test
    fun `test work experience bonus calculation`() {
        player.workExperience = 0
        assertEquals(10000, player.calculateEducationBonus()) // только образование
        
        player.workExperience = 5
        assertEquals(20000, player.calculateEducationBonus()) // образование + опыт
    }
    
    @Test
    fun `test skills bonus calculation`() {
        val skill1 = Skill("Программирование", "Технический навык", 15000, SkillCategory.TECHNICAL)
        val skill2 = Skill("Менеджмент", "Лидерский навык", 12000, SkillCategory.LEADERSHIP)
        
        player.skills.add(skill1)
        player.skills.add(skill2)
        
        assertEquals(27000, player.calculateSkillsBonus())
    }
    
    @Test
    fun `test salary update with bonuses`() {
        player.workExperience = 3
        val skill = Skill("Программирование", "Технический навык", 15000, SkillCategory.TECHNICAL)
        player.skills.add(skill)
        
        player.updateSalaryWithBonuses()
        
        val expectedSalary = 80000 + // базовая зарплата
                            10000 + // бонус за образование (BACHELOR)
                            15000 + // бонус за навык
                            3000    // бонус за опыт (3 года * 1000)
        
        assertEquals(expectedSalary, player.salary)
    }
    
    // === ТЕСТЫ СЕМЕЙНЫХ РАСХОДОВ ===
    
    @Test
    fun `test family expenses calculation`() {
        player.childrenCount = 0
        player.maritalStatus = MaritalStatus.SINGLE
        assertEquals(0, player.calculateFamilyExpenses())
        
        player.childrenCount = 2
        player.maritalStatus = MaritalStatus.MARRIED
        assertEquals(21000, player.calculateFamilyExpenses()) // 2 * 8000 + 5000
    }
    
    @Test
    fun `test add child increases expenses`() {
        val initialExpenses = player.childrenExpenses
        player.addChild()
        assertEquals(initialExpenses + 8000, player.childrenExpenses)
    }
    
    // === ТЕСТЫ СБЕРЕЖЕНИЙ ===
    
    @Test
    fun `test savings calculation`() {
        player.totalIncome = 100000
        player.savingsRate = 20
        
        assertEquals(20000, player.calculateSavings())
    }
    
    @Test
    fun `test apply savings with sufficient cash`() {
        player.cash = 50000
        player.totalIncome = 100000
        player.savingsRate = 10
        
        val initialCash = player.cash
        val initialInvested = player.totalInvested
        
        player.applySavings()
        
        assertEquals(initialCash - 10000, player.cash)
        assertEquals(initialInvested + 10000, player.totalInvested)
    }
    
    @Test
    fun `test apply savings with insufficient cash`() {
        player.cash = 5000
        player.totalIncome = 100000
        player.savingsRate = 10
        
        val initialCash = player.cash
        val initialInvested = player.totalInvested
        
        player.applySavings()
        
        // Сбережения не применяются, если недостаточно денег
        assertEquals(initialCash, player.cash)
        assertEquals(initialInvested, player.totalInvested)
    }
    
    // === ТЕСТЫ ЖИЗНЕННОГО ЦИКЛА ===
    
    @Test
    fun `test random death age setting`() {
        player.setRandomDeathAge()
        assertTrue(player.deathAge in 70..110)
    }
    
    @Test
    fun `test month passing`() {
        val initialAge = player.age
        val initialMonths = player.monthsPlayed
        
        player.passMonth()
        
        assertEquals(initialMonths + 1, player.monthsPlayed)
        assertEquals(1, player.currentDayOfMonth)
        
        // Возраст не должен измениться после одного месяца
        assertEquals(initialAge, player.age)
    }
    
    @Test
    fun `test age increase after 12 months`() {
        val initialAge = player.age
        
        // Проходим 12 месяцев
        repeat(12) { player.passMonth() }
        
        assertEquals(initialAge + 1, player.age)
    }
    
    @Test
    fun `test player alive status`() {
        player.age = 25
        player.deathAge = 85
        assertTrue(player.isAlive())
        
        player.age = 85
        assertFalse(player.isAlive())
    }
    
    @Test
    fun `test years left calculation`() {
        player.age = 25
        player.deathAge = 85
        assertEquals(60, player.getYearsLeft())
        
        player.age = 85
        assertEquals(0, player.getYearsLeft())
    }
    
    @Test
    fun `test life percentage calculation`() {
        player.age = 25
        player.deathAge = 100
        assertEquals(25.0f, player.getLifePercentage(), 0.1f)
        
        player.age = 50
        assertEquals(50.0f, player.getLifePercentage(), 0.1f)
    }
    
    @Test
    fun `test critical age detection`() {
        player.age = 80
        player.deathAge = 85
        assertTrue(player.isInCriticalAge())
        
        player.age = 75
        assertFalse(player.isInCriticalAge())
    }
    
    // === ТЕСТЫ ФИНАНСОВОГО ЖУРНАЛА ===
    
    @Test
    fun `test add financial entry`() {
        val initialSize = player.financialJournal.size
        
        player.addFinancialEntry(
            FinancialEntryType.INCOME,
            FinancialCategory.SALARY,
            50000,
            "Зарплата"
        )
        
        assertEquals(initialSize + 1, player.financialJournal.size)
        
        val entry = player.financialJournal.last()
        assertEquals(FinancialEntryType.INCOME, entry.type)
        assertEquals(FinancialCategory.SALARY, entry.category)
        assertEquals(50000, entry.amount)
        assertEquals("Зарплата", entry.description)
    }
    
    @Test
    fun `test log income`() {
        val initialSize = player.financialJournal.size
        
        player.logIncome(FinancialCategory.SALARY, 50000, "Зарплата")
        
        assertEquals(initialSize + 1, player.financialJournal.size)
        
        val entry = player.financialJournal.last()
        assertEquals(FinancialEntryType.INCOME, entry.type)
        assertEquals(50000, entry.amount)
    }
    
    @Test
    fun `test log expense`() {
        val initialCash = player.cash
        val initialSize = player.financialJournal.size
        
        player.logExpense(FinancialCategory.FOOD, 5000, "Продукты")
        
        assertEquals(initialCash - 5000, player.cash)
        assertEquals(initialSize + 1, player.financialJournal.size)
        
        val entry = player.financialJournal.last()
        assertEquals(FinancialEntryType.EXPENSE, entry.type)
        assertEquals(-5000, entry.amount)
    }
    
    @Test
    fun `test get recent journal entries`() {
        // Добавляем несколько записей
        repeat(10) { i ->
            player.addFinancialEntry(
                FinancialEntryType.INCOME,
                FinancialCategory.SALARY,
                1000 * (i + 1),
                "Запись $i"
            )
        }
        
        val recentEntries = player.getRecentJournalEntries(5)
        assertEquals(5, recentEntries.size)
        
        // Проверяем, что записи в обратном порядке (новые сверху)
        assertEquals("Запись 9", recentEntries[0].description)
        assertEquals("Запись 5", recentEntries[4].description)
    }
    
    @Test
    fun `test category statistics`() {
        player.addFinancialEntry(FinancialEntryType.INCOME, FinancialCategory.SALARY, 1000, "Зарплата 1")
        player.addFinancialEntry(FinancialEntryType.INCOME, FinancialCategory.SALARY, 2000, "Зарплата 2")
        player.addFinancialEntry(FinancialEntryType.EXPENSE, FinancialCategory.FOOD, 500, "Продукты")
        
        val stats = player.getCategoryStats()
        
        assertEquals(3000, stats[FinancialCategory.SALARY])
        assertEquals(-500, stats[FinancialCategory.FOOD])
    }
    
    @Test
    fun `test monthly statistics`() {
        player.monthsPlayed = 5
        
        player.addFinancialEntry(FinancialEntryType.INCOME, FinancialCategory.SALARY, 1000, "Зарплата")
        player.addFinancialEntry(FinancialEntryType.EXPENSE, FinancialCategory.FOOD, 500, "Продукты")
        
        val (income, expense) = player.getMonthlyStats(5)
        
        assertEquals(1000, income)
        assertEquals(500, expense)
    }
    
    // === ТЕСТЫ ФИНАНСОВЫХ ЦЕЛЕЙ ===
    
    @Test
    fun `test check financial goals - passive income`() {
        val goal = FinancialGoal(
            name = "Пассивный доход",
            description = "Достичь пассивного дохода",
            type = GoalType.PASSIVE_INCOME,
            targetAmount = 50000,
            deadline = 12
        )
        
        player.financialGoals.add(goal)
        player.passiveIncome = 40000
        
        var achievedGoals = player.checkFinancialGoals()
        assertEquals(0, achievedGoals.size)
        
        player.passiveIncome = 60000
        achievedGoals = player.checkFinancialGoals()
        assertEquals(1, achievedGoals.size)
        assertEquals(goal, achievedGoals[0])
    }
    
    @Test
    fun `test check financial goals - net worth`() {
        val goal = FinancialGoal(
            name = "Чистая стоимость",
            description = "Достичь чистой стоимости",
            type = GoalType.NET_WORTH,
            targetAmount = 100000,
            deadline = 12
        )
        
        player.financialGoals.add(goal)
        player.cash = 50000
        
        var achievedGoals = player.checkFinancialGoals()
        assertEquals(0, achievedGoals.size)
        
        player.cash = 150000
        achievedGoals = player.checkFinancialGoals()
        assertEquals(1, achievedGoals.size)
    }
    
    // === ТЕСТЫ ЖИЗНЕННОЙ СТАТИСТИКИ ===
    
    @Test
    fun `test life statistics`() {
        player.totalEarned = 1000000
        player.totalSpent = 800000
        player.totalInvested = 150000
        player.careerPromotions = 3
        player.businessFailures = 1
        player.successfulInvestments = 5
        player.workExperience = 5
        player.childrenCount = 2
        
        val stats = player.getLifeStatistics()
        
        assertEquals(1000000, stats["totalEarned"])
        assertEquals(800000, stats["totalSpent"])
        assertEquals(150000, stats["totalInvested"])
        assertEquals(3, stats["careerPromotions"])
        assertEquals(1, stats["businessFailures"])
        assertEquals(5, stats["successfulInvestments"])
        assertEquals(5, stats["yearsWorked"])
        assertEquals(2, stats["childrenCount"])
    }
    
    // === ТЕСТЫ ГРАНИЧНЫХ СЛУЧАЕВ ===
    
    @Test
    fun `test player with null profession`() {
        val playerWithoutProfession = Player()
        
        playerWithoutProfession.updateSalaryWithBonuses()
        assertEquals(0, playerWithoutProfession.salary)
        
        playerWithoutProfession.updateTotalIncome()
        assertEquals(0, playerWithoutProfession.totalIncome)
    }
    
    @Test
    fun `test player with maximum values`() {
        player.age = 65
        player.workExperience = 40
        player.childrenCount = 10
        player.savingsRate = 50
        player.retirementAge = 80
        player.targetPassiveIncome = 1000000
        
        assertEquals(40, player.workExperience)
        assertEquals(10, player.childrenCount)
        assertEquals(50, player.savingsRate)
        assertEquals(80, player.retirementAge)
        assertEquals(1000000, player.targetPassiveIncome)
    }
    
    @Test
    fun `test player with minimum values`() {
        player.age = 18
        player.workExperience = 0
        player.childrenCount = 0
        player.savingsRate = 0
        player.retirementAge = 50
        player.targetPassiveIncome = 10000
        
        assertEquals(18, player.age)
        assertEquals(0, player.workExperience)
        assertEquals(0, player.childrenCount)
        assertEquals(0, player.savingsRate)
        assertEquals(50, player.retirementAge)
        assertEquals(10000, player.targetPassiveIncome)
    }
    
    @Test
    fun `test financial journal size limit`() {
        // Добавляем больше 500 записей
        repeat(600) { i ->
            player.addFinancialEntry(
                FinancialEntryType.INCOME,
                FinancialCategory.SALARY,
                1000,
                "Запись $i"
            )
        }
        
        // Журнал должен содержать максимум 500 записей
        assertEquals(500, player.financialJournal.size)
        
        // Последняя запись должна быть самой новой
        assertEquals("Запись 599", player.financialJournal.last().description)
    }
    
    // === ТЕСТЫ ENUM КЛАССОВ ===
    
    @Test
    fun `test education level properties`() {
        assertEquals("Среднее образование", EducationLevel.HIGH_SCHOOL.displayName)
        assertEquals(0.8, EducationLevel.HIGH_SCHOOL.salaryMultiplier, 0.01)
        
        assertEquals("Докторантура", EducationLevel.PHD.displayName)
        assertEquals(1.4, EducationLevel.PHD.salaryMultiplier, 0.01)
    }
    
    @Test
    fun `test marital status properties`() {
        assertEquals("Холост/Не замужем", MaritalStatus.SINGLE.displayName)
        assertEquals("Женат/Замужем", MaritalStatus.MARRIED.displayName)
        assertEquals("Разведен/Разведена", MaritalStatus.DIVORCED.displayName)
        assertEquals("Вдовец/Вдова", MaritalStatus.WIDOWED.displayName)
    }
    
    @Test
    fun `test risk tolerance properties`() {
        assertEquals("Консервативный", RiskTolerance.CONSERVATIVE.displayName)
        assertEquals(0.7, RiskTolerance.CONSERVATIVE.investmentMultiplier, 0.01)
        
        assertEquals("Агрессивный", RiskTolerance.AGGRESSIVE.displayName)
        assertEquals(1.3, RiskTolerance.AGGRESSIVE.investmentMultiplier, 0.01)
    }
    
    @Test
    fun `test health level properties`() {
        assertEquals("Отличное", HealthLevel.EXCELLENT.displayName)
        assertEquals(0.8, HealthLevel.EXCELLENT.expenseMultiplier, 0.01)
        
        assertEquals("Плохое", HealthLevel.POOR.displayName)
        assertEquals(1.5, HealthLevel.POOR.expenseMultiplier, 0.01)
    }
    
    @Test
    fun `test skill properties`() {
        val skill = Skill(
            name = "Тестовый навык",
            description = "Описание навыка",
            salaryBonus = 10000,
            category = SkillCategory.TECHNICAL
        )
        
        assertEquals("Тестовый навык", skill.name)
        assertEquals("Описание навыка", skill.description)
        assertEquals(10000, skill.salaryBonus)
        assertEquals(SkillCategory.TECHNICAL, skill.category)
    }
    
    @Test
    fun `test financial goal properties`() {
        val goal = FinancialGoal(
            name = "Тестовая цель",
            description = "Описание цели",
            type = GoalType.PASSIVE_INCOME,
            targetAmount = 100000,
            deadline = 12,
            isAchieved = false
        )
        
        assertEquals("Тестовая цель", goal.name)
        assertEquals("Описание цели", goal.description)
        assertEquals(GoalType.PASSIVE_INCOME, goal.type)
        assertEquals(100000, goal.targetAmount)
        assertEquals(12, goal.deadline)
        assertFalse(goal.isAchieved)
    }
}