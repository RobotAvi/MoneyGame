package com.financialsuccess.game.models

import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

class PlayerTest {
    
    private lateinit var player: Player
    private lateinit var profession: Profession
    private lateinit var dream: Dream
    
    @Before
    fun setUp() {
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
            workLifeBalance = WorkLifeBalance.BALANCED
        )
    }
    
    @Test
    fun `test education bonus calculation`() {
        // Тест бонуса за образование
        val highSchoolBonus = player.copy(education = EducationLevel.HIGH_SCHOOL).calculateEducationBonus()
        val bachelorBonus = player.copy(education = EducationLevel.BACHELOR).calculateEducationBonus()
        val masterBonus = player.copy(education = EducationLevel.MASTER).calculateEducationBonus()
        val phdBonus = player.copy(education = EducationLevel.PHD).calculateEducationBonus()
        val experienceBonus = player.calculateExperienceBonus()

        assertEquals(0, highSchoolBonus) // Только образование
        assertEquals(10000, bachelorBonus)
        assertEquals(15000, masterBonus)
        assertEquals(20000, phdBonus)
        assertEquals(4000, experienceBonus) // 2*2000
    }
    
    @Test
    fun `test skills bonus calculation`() {
        // Тест бонуса за навыки
        val skills = listOf(
            Skill("Программирование", "Навыки разработки ПО", 15000, SkillCategory.TECHNICAL),
            Skill("Менеджмент", "Управление командой", 12000, SkillCategory.LEADERSHIP)
        )
        
        player.skills.addAll(skills)
        val skillsBonus = player.calculateSkillsBonus()
        
        assertEquals(27000, skillsBonus) // 15000 + 12000
    }
    
    @Test
    fun `test salary with bonuses calculation`() {
        // Тест расчета зарплаты с бонусами
        val skills = listOf(
            Skill("Программирование", "Навыки разработки ПО", 15000, SkillCategory.TECHNICAL)
        )
        player.skills.addAll(skills)
        
        player.updateSalaryWithBonuses()
        
        // Базовая зарплата: 80000
        // Бонус за образование (BACHELOR): 10000
        // Бонус за опыт (2 года): 4000
        // Бонус за навык: 15000
        // Ожидаемая зарплата: 80000 + 10000 + 4000 + 15000 = 109000
        assertEquals(109000, player.salary)
    }
    
    @Test
    fun `test family expenses calculation`() {
        // Тест расчета семейных расходов
        val singlePlayer = player.copy(maritalStatus = MaritalStatus.SINGLE, childrenCount = 0)
        val marriedPlayer = player.copy(maritalStatus = MaritalStatus.MARRIED, childrenCount = 2)
        
        val singleExpenses = singlePlayer.calculateFamilyExpenses()
        val marriedExpenses = marriedPlayer.calculateFamilyExpenses()
        
        assertEquals(0, singleExpenses) // Нет семьи, нет детей
        assertEquals(21000, marriedExpenses) // 2*8000 (дети) + 5000 (супруг)
    }
    
    @Test
    fun `test savings calculation`() {
        // Тест расчета сбережений
        player.savingsRate = 20
        player.totalIncome = 100000
        
        val savings = player.calculateSavings()
        
        assertEquals(20000, savings) // 20% от 100000
    }
    
    @Test
    fun `test financial goals checking`() {
        // Тест проверки финансовых целей
        val goal = FinancialGoal(
            name = "Пассивный доход 50000₽",
            description = "Достичь пассивного дохода 50000₽ в месяц",
            type = GoalType.PASSIVE_INCOME,
            targetAmount = 50000,
            deadline = 60
        )
        
        player.financialGoals.add(goal)
        
        // Сначала цель не достигнута
        var achievedGoals = player.checkFinancialGoals()
        assertEquals(0, achievedGoals.size)
        
        // Устанавливаем пассивный доход выше цели
        player.passiveIncome = 60000
        achievedGoals = player.checkFinancialGoals()
        assertEquals(1, achievedGoals.size)
        assertEquals(goal, achievedGoals[0])
    }
    
    @Test
    fun `test life statistics`() {
        // Тест статистики жизни
        player.totalEarned = 1000000
        player.totalSpent = 800000
        player.totalInvested = 200000
        player.careerPromotions = 3
        player.businessFailures = 1
        player.successfulInvestments = 5
        
        val stats = player.getLifeStatistics()
        
        assertEquals(1000000, stats["totalEarned"])
        assertEquals(800000, stats["totalSpent"])
        assertEquals(200000, stats["totalInvested"])
        assertEquals(3, stats["careerPromotions"])
        assertEquals(1, stats["businessFailures"])
        assertEquals(5, stats["successfulInvestments"])
        assertEquals(2, stats["yearsWorked"])
        assertEquals(0, stats["childrenCount"])
    }
    
    @Test
    fun `test health status calculation`() {
        // Тест расчета статуса здоровья
        val healthyPlayer = player.copy(healthLevel = HealthLevel.EXCELLENT)
        val poorHealthPlayer = player.copy(healthLevel = HealthLevel.POOR)
        // Добавляем риски для тестирования
        val riskEffect = RiskEffect(
            type = RiskEffectType.STRESS_DISORDER,
            description = "Тестовый эффект",
            expenseIncrease = 1000,
            salaryReduction = 0,
            recoveryTime = 3,
            severity = RiskSeverity.SEVERE,
            careerEnd = false
        )
        poorHealthPlayer.riskEffects.clear()
        poorHealthPlayer.riskEffects.add(riskEffect)
        poorHealthPlayer.healthLevel = HealthLevel.POOR
        println("DEBUG: riskEffects.size=${poorHealthPlayer.riskEffects.size}, riskEffects=${poorHealthPlayer.riskEffects}")
        
        val healthyStatus = healthyPlayer.getHealthStatus()
        val poorStatus = poorHealthPlayer.getHealthStatus()
        println("DEBUG: poorStatus=$poorStatus")
        assertTrue(healthyStatus.contains("Здоров"))
        assertTrue(poorStatus.replace("\\s".toRegex(), "").contains("Серьезныепроблемысоздоровьем"))
    }
    
    @Test
    fun `test total expenses with family and health modifiers`() {
        // Тест общих расходов с семейными и здоровье модификаторами
        player.maritalStatus = MaritalStatus.MARRIED
        player.childrenCount = 1
        player.healthLevel = HealthLevel.POOR
        
        player.updateTotalExpenses()
        
        // Базовые расходы профессии: 35000
        // Расходы на ребенка: 8000
        // Расходы на супруга: 5000
        // Модификатор здоровья (1.5): (35000 + 5000) * 1.5 = 60000
        // Налоги: 15000
        // Ожидаемые общие расходы: 60000 + 8000 + 15000 = 83000
        assertTrue(player.totalExpenses > 80000)
    }
    
    @Test
    fun `test total income with spouse`() {
        // Тест общего дохода с учетом супруга
        player.spouseIncome = 50000
        player.salary = 80000
        player.updateTotalIncome()
        // Зарплата игрока + доход супруга
        assertEquals(130000, player.totalIncome) // 80000 + 50000
    }
    
    @Test
    fun `test age and life progression`() {
        // Тест прогрессии возраста и жизни
        val initialAge = player.age
        val initialDeathAge = player.deathAge
        
        // Проходим 12 месяцев (1 год)
        repeat(12) {
            player.passMonth()
        }
        
        assertEquals(initialAge + 1, player.age)
        assertEquals(initialDeathAge, player.deathAge) // Возраст смерти не меняется
        assertEquals(12, player.monthsPlayed)
        
        // Проверяем процент прожитой жизни
        val lifePercentage = player.getLifePercentage()
        assertTrue(lifePercentage > 0)
        assertTrue(lifePercentage < 100)
    }
    
    @Test
    fun `test marital status effects`() {
        // Тест эффектов семейного положения
        val singlePlayer = player.copy(maritalStatus = MaritalStatus.SINGLE)
        val marriedPlayer = player.copy(maritalStatus = MaritalStatus.MARRIED)
        
        singlePlayer.updateTotalExpenses()
        marriedPlayer.updateTotalExpenses()
        
        // У женатого игрока должны быть дополнительные расходы
        assertTrue(marriedPlayer.totalExpenses > singlePlayer.totalExpenses)
    }
    
    @Test
    fun `test risk tolerance effects`() {
        // Тест эффектов толерантности к риску
        val conservativePlayer = player.copy(riskTolerance = RiskTolerance.CONSERVATIVE)
        val aggressivePlayer = player.copy(riskTolerance = RiskTolerance.AGGRESSIVE)
        
        // Проверяем множители инвестиций
        assertEquals(0.7, conservativePlayer.riskTolerance.investmentMultiplier, 0.01)
        assertEquals(1.3, aggressivePlayer.riskTolerance.investmentMultiplier, 0.01)
    }
    
    @Test
    fun `test education level salary multipliers`() {
        // Тест множителей зарплаты по уровням образования
        assertEquals(0.8, EducationLevel.HIGH_SCHOOL.salaryMultiplier, 0.01)
        assertEquals(0.9, EducationLevel.COLLEGE.salaryMultiplier, 0.01)
        assertEquals(1.0, EducationLevel.BACHELOR.salaryMultiplier, 0.01)
        assertEquals(1.2, EducationLevel.MASTER.salaryMultiplier, 0.01)
        assertEquals(1.4, EducationLevel.PHD.salaryMultiplier, 0.01)
    }
    
    @Test
    fun `test health level expense multipliers`() {
        // Тест множителей расходов по уровням здоровья
        assertEquals(0.8, HealthLevel.EXCELLENT.expenseMultiplier, 0.01)
        assertEquals(1.0, HealthLevel.GOOD.expenseMultiplier, 0.01)
        assertEquals(1.2, HealthLevel.FAIR.expenseMultiplier, 0.01)
        assertEquals(1.5, HealthLevel.POOR.expenseMultiplier, 0.01)
    }
}