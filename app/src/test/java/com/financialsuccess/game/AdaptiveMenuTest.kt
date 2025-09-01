package com.financialsuccess.game

import com.financialsuccess.game.models.*
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Тесты для адаптивного меню
 */
class AdaptiveMenuTest {
    
    private lateinit var player: Player
    private lateinit var teacherProfession: Profession
    private lateinit var dream: Dream
    private lateinit var adaptiveMenu: AdaptiveMenu
    
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
        
        adaptiveMenu = AdaptiveMenu(player)
    }
    
    @Test
    fun `test initial menu has NOVICE level actions`() {
        val availableActions = adaptiveMenu.getAvailableActions()
        
        // Проверяем, что доступны действия для новичка
        assertTrue(availableActions.any { it.actionName == "showIncomeDialog" })
        assertTrue(availableActions.any { it.actionName == "showExpensesDialog" })
        assertTrue(availableActions.any { it.actionName == "showHealthStatus" })
        assertTrue(availableActions.any { it.actionName == "showEducationSkills" })
        assertTrue(availableActions.any { it.actionName == "btn_next_turn" })
        
        // Проверяем, что недоступны действия для более высоких уровней
        assertFalse(availableActions.any { it.actionName == "showBalancePanel" })
        assertFalse(availableActions.any { it.actionName == "showMarketDialog" })
        assertFalse(availableActions.any { it.actionName == "showPhilanthropy" })
    }
    
    @Test
    fun `test menu actions by parent button for NOVICE level`() {
        val financeActions = adaptiveMenu.getActionsForParentButton("Финансы и инвестиции")
        val healthActions = adaptiveMenu.getActionsForParentButton("Здоровье и образ жизни")
        val opportunitiesActions = adaptiveMenu.getActionsForParentButton("Возможности и развитие")
        
        // Финансы - только базовые действия
        assertTrue(financeActions.any { it.actionName == "showIncomeDialog" })
        assertTrue(financeActions.any { it.actionName == "showExpensesDialog" })
        assertFalse(financeActions.any { it.actionName == "showBalancePanel" })
        
        // Здоровье - базовые действия
        assertTrue(healthActions.any { it.actionName == "showHealthStatus" })
        assertTrue(healthActions.any { it.actionName == "showHabitsAndFamilyInfluence" })
        assertTrue(healthActions.any { it.actionName == "showAgeStatistics" })
        assertFalse(healthActions.any { it.actionName == "showHealthcareOptions" })
        
        // Возможности - базовые действия
        assertTrue(opportunitiesActions.any { it.actionName == "showEducationSkills" })
        assertTrue(opportunitiesActions.any { it.actionName == "showCareerBusiness" })
        assertTrue(opportunitiesActions.any { it.actionName == "showOtherOpportunities" })
        assertFalse(opportunitiesActions.any { it.actionName == "showPersonalRelationships" })
    }
    
    @Test
    fun `test menu actions for AUTONOMOUS level`() {
        // Повышаем игрока до уровня Автономный
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        player.updateLevel()
        
        val newAdaptiveMenu = AdaptiveMenu(player)
        val availableActions = newAdaptiveMenu.getAvailableActions()
        
        // Проверяем, что доступны действия для автономного уровня
        assertTrue(availableActions.any { it.actionName == "showBalancePanel" })
        assertTrue(availableActions.any { it.actionName == "showFinancialJournal" })
        assertTrue(availableActions.any { it.actionName == "showFinancialStatement" })
        assertTrue(availableActions.any { it.actionName == "showHealthcareOptions" })
        assertTrue(availableActions.any { it.actionName == "buyHealthcare" })
        assertTrue(availableActions.any { it.actionName == "showHealthAndFamilyExpenses" })
        assertTrue(availableActions.any { it.actionName == "showPersonalRelationships" })
        
        // Проверяем, что недоступны действия для более высоких уровней
        assertFalse(availableActions.any { it.actionName == "showMarketDialog" })
        assertFalse(availableActions.any { it.actionName == "showJournalAnalytics" })
        assertFalse(availableActions.any { it.actionName == "showPhilanthropy" })
    }
    
    @Test
    fun `test menu actions for INVESTOR level`() {
        // Повышаем игрока до уровня Инвестор
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
        
        val newAdaptiveMenu = AdaptiveMenu(player)
        val availableActions = newAdaptiveMenu.getAvailableActions()
        
        // Проверяем, что доступны действия для инвестора
        assertTrue(availableActions.any { it.actionName == "showMarketDialog" })
        assertTrue(availableActions.any { it.actionName == "showAvailableAssets" })
        assertTrue(availableActions.any { it.actionName == "showAssetDetails" })
        assertTrue(availableActions.any { it.actionName == "showAvailableInvestments" })
        assertTrue(availableActions.any { it.actionName == "showInvestmentDetails" })
        assertTrue(availableActions.any { it.actionName == "showPortfolio" })
        assertTrue(availableActions.any { it.actionName == "showJournalAnalytics" })
        assertTrue(availableActions.any { it.actionName == "showFamilyExpenses" })
        assertTrue(availableActions.any { it.actionName == "showFamilyCreation" })
        
        // Проверяем, что недоступны действия для более высоких уровней
        assertFalse(availableActions.any { it.actionName == "showBusinessDevelopment" })
        assertFalse(availableActions.any { it.actionName == "showPhilanthropy" })
    }
    
    @Test
    fun `test menu actions for PASSIVE_INCOME level`() {
        // Повышаем игрока до уровня Пассивный доход
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
        
        val newAdaptiveMenu = AdaptiveMenu(player)
        val availableActions = newAdaptiveMenu.getAvailableActions()
        
        // Проверяем, что доступны действия для пассивного дохода
        assertTrue(availableActions.any { it.actionName == "showBusinessDevelopment" })
        
        // Проверяем, что недоступны действия для более высоких уровней
        assertFalse(availableActions.any { it.actionName == "showPhilanthropy" })
    }
    
    @Test
    fun `test menu actions for FINANCIAL_MASTER level`() {
        // Повышаем игрока до уровня Финансовый мастер
        player.cash = 100000
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
        
        player.passiveIncome = 40000
        player.monthsPlayed = 10
        player.updateLevel()
        
        val newAdaptiveMenu = AdaptiveMenu(player)
        val availableActions = newAdaptiveMenu.getAvailableActions()
        
        // Проверяем, что доступны все действия
        assertTrue(availableActions.any { it.actionName == "showPhilanthropy" })
        assertEquals(MenuAction.values().size, availableActions.size)
    }
    
    @Test
    fun `test action availability check`() {
        // Проверяем доступность действий для новичка
        assertTrue(adaptiveMenu.isActionAvailable("showIncomeDialog"))
        assertTrue(adaptiveMenu.isActionAvailable("showExpensesDialog"))
        assertTrue(adaptiveMenu.isActionAvailable("btn_next_turn"))
        assertFalse(adaptiveMenu.isActionAvailable("showBalancePanel"))
        assertFalse(adaptiveMenu.isActionAvailable("showMarketDialog"))
        assertFalse(adaptiveMenu.isActionAvailable("showPhilanthropy"))
    }
    
    @Test
    fun `test menu info for NOVICE level`() {
        val menuInfo = adaptiveMenu.getMenuInfo()
        
        assertEquals(PlayerLevel.NOVICE, menuInfo.currentLevel)
        assertEquals(PlayerLevel.AUTONOMOUS, menuInfo.nextLevel)
        assertFalse(menuInfo.canLevelUp)
        assertTrue(menuInfo.availableActions > 0)
        assertTrue(menuInfo.totalActions > menuInfo.availableActions)
    }
    
    @Test
    fun `test menu info for AUTONOMOUS level`() {
        // Повышаем игрока до уровня Автономный
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        player.updateLevel()
        
        val newAdaptiveMenu = AdaptiveMenu(player)
        val menuInfo = newAdaptiveMenu.getMenuInfo()
        
        assertEquals(PlayerLevel.AUTONOMOUS, menuInfo.currentLevel)
        assertEquals(PlayerLevel.INVESTOR, menuInfo.nextLevel)
        assertFalse(menuInfo.canLevelUp) // Нет инвестиций и крупного актива
        assertTrue(menuInfo.availableActions > 0)
    }
    
    @Test
    fun `test menu info for FINANCIAL_MASTER level`() {
        // Повышаем игрока до уровня Финансовый мастер
        player.cash = 100000
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
        
        player.passiveIncome = 40000
        player.monthsPlayed = 10
        player.updateLevel()
        
        val newAdaptiveMenu = AdaptiveMenu(player)
        val menuInfo = newAdaptiveMenu.getMenuInfo()
        
        assertEquals(PlayerLevel.FINANCIAL_MASTER, menuInfo.currentLevel)
        assertEquals(PlayerLevel.FINANCIAL_MASTER, menuInfo.nextLevel) // Максимальный уровень
        assertFalse(menuInfo.canLevelUp)
        assertEquals(MenuAction.values().size, menuInfo.availableActions)
        assertEquals(MenuAction.values().size, menuInfo.totalActions)
    }
    
    @Test
    fun `test level progress calculation`() {
        val levelProgress = adaptiveMenu.getLevelProgress()
        
        assertTrue(levelProgress.progress >= 0)
        assertTrue(levelProgress.progress <= 100)
        assertTrue(levelProgress.description.isNotEmpty())
    }
    
    @Test
    fun `test can level up method`() {
        // Изначально не может повысить уровень
        assertFalse(adaptiveMenu.canLevelUp())
        
        // Даем условия для повышения уровня
        player.cash = 25000
        player.education = EducationLevel.BACHELOR
        
        val newAdaptiveMenu = AdaptiveMenu(player)
        assertTrue(newAdaptiveMenu.canLevelUp())
    }
}
