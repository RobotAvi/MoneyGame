package com.financialsuccess.game.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Система адаптивного меню согласно таблице 2
 */
@Parcelize
enum class MenuAction(
    val actionName: String,
    val displayName: String,
    val description: String,
    val parentButton: String,
    val group: String,
    val requiredLevel: PlayerLevel,
    val relatedActions: List<String> = emptyList()
) : Parcelable {
    
    // === ФИНАНСЫ И ИНВЕСТИЦИИ ===
    
    // Доходы и расходы
    SHOW_INCOME_DIALOG(
        "showIncomeDialog",
        "Доходы",
        "Информация о доходах (зарплата, пассивный, общий доход)",
        "Финансы и инвестиции",
        "Доходы и расходы",
        PlayerLevel.NOVICE
    ),
    
    SHOW_EXPENSES_DIALOG(
        "showExpensesDialog",
        "Расходы",
        "Информация о расходах (общие, налоги, жильё, транспорт, питание)",
        "Финансы и инвестиции",
        "Доходы и расходы",
        PlayerLevel.NOVICE
    ),
    
    // Финансовые отчёты и аналитика
    SHOW_BALANCE_PANEL(
        "showBalancePanel",
        "Финансовый журнал",
        "Финансовый журнал, аналитика, старые отчёты",
        "Финансы и инвестиции",
        "Финансовые отчёты и аналитика",
        PlayerLevel.AUTONOMOUS,
        listOf("showFinancialJournal", "showJournalAnalytics", "showFinancialStatement")
    ),
    
    SHOW_FINANCIAL_JOURNAL(
        "showFinancialJournal",
        "Журнал",
        "Просмотр и экспорт TXT журнала",
        "Финансы и инвестиции",
        "Финансовые отчёты и аналитика",
        PlayerLevel.AUTONOMOUS,
        listOf("showBalancePanel")
    ),
    
    SHOW_JOURNAL_ANALYTICS(
        "showJournalAnalytics",
        "Аналитика",
        "Построение графиков и аналитических отчётов",
        "Финансы и инвестиции",
        "Финансовые отчёты и аналитика",
        PlayerLevel.INVESTOR,
        listOf("showBalancePanel")
    ),
    
    SHOW_FINANCIAL_STATEMENT(
        "showFinancialStatement",
        "Отчёты",
        "Просмотр старых финансовых отчетов",
        "Финансы и инвестиции",
        "Финансовые отчёты и аналитика",
        PlayerLevel.AUTONOMOUS,
        listOf("showBalancePanel")
    ),
    
    // Инвестиции и портфель
    SHOW_MARKET_DIALOG(
        "showMarketDialog",
        "Рынок",
        "Меню активов, инвестиций и портфеля",
        "Финансы и инвестиции",
        "Инвестиции и портфель",
        PlayerLevel.INVESTOR,
        listOf("showAvailableAssets", "showAvailableInvestments", "showPortfolio")
    ),
    
    SHOW_AVAILABLE_ASSETS(
        "showAvailableAssets",
        "Активы",
        "Список доступных активов",
        "Финансы и инвестиции",
        "Инвестиции и портфель",
        PlayerLevel.INVESTOR,
        listOf("showMarketDialog")
    ),
    
    SHOW_ASSET_DETAILS(
        "showAssetDetails",
        "Детали актива",
        "Детали выбранного актива и кнопки «Купить/Отмена»",
        "Финансы и инвестиции",
        "Инвестиции и портфель",
        PlayerLevel.INVESTOR,
        listOf("showAvailableAssets")
    ),
    
    SHOW_AVAILABLE_INVESTMENTS(
        "showAvailableInvestments",
        "Инвестиции",
        "Список инвестиционных инструментов",
        "Финансы и инвестиции",
        "Инвестиции и портфель",
        PlayerLevel.INVESTOR,
        listOf("showMarketDialog")
    ),
    
    SHOW_INVESTMENT_DETAILS(
        "showInvestmentDetails",
        "Детали инвестиции",
        "Детали инструмента и кнопки «Инвестировать/Отмена»",
        "Финансы и инвестиции",
        "Инвестиции и портфель",
        PlayerLevel.INVESTOR,
        listOf("showAvailableInvestments")
    ),
    
    SHOW_PORTFOLIO(
        "showPortfolio",
        "Портфель",
        "Просмотр текущего портфеля",
        "Финансы и инвестиции",
        "Инвестиции и портфель",
        PlayerLevel.INVESTOR,
        listOf("showMarketDialog")
    ),
    
    // === ЗДОРОВЬЕ И ОБРАЗ ЖИЗНИ ===
    
    // Статус здоровья
    SHOW_HEALTH_STATUS(
        "showHealthStatus",
        "Здоровье",
        "Параметры здоровья (возраст, дни жизни, мораль)",
        "Здоровье и образ жизни",
        "Статус здоровья",
        PlayerLevel.NOVICE,
        listOf("showHealthcareOptions")
    ),
    
    SHOW_HEALTHCARE_OPTIONS(
        "showHealthcareOptions",
        "Медицина",
        "Список профилактических услуг",
        "Здоровье и образ жизни",
        "Статус здоровья",
        PlayerLevel.AUTONOMOUS,
        listOf("showHealthStatus")
    ),
    
    BUY_HEALTHCARE(
        "buyHealthcare",
        "Купить услугу",
        "Покупка выбранной услуги",
        "Здоровье и образ жизни",
        "Статус здоровья",
        PlayerLevel.AUTONOMOUS,
        listOf("showHealthcareOptions")
    ),
    
    // Привычки и семья
    SHOW_HABITS_AND_FAMILY_INFLUENCE(
        "showHabitsAndFamilyInfluence",
        "Привычки и семья",
        "Влияние полезных и вредных привычек, семьи на здоровье",
        "Здоровье и образ жизни",
        "Привычки и семья",
        PlayerLevel.NOVICE
    ),
    
    // Расходы на здоровье и семью
    SHOW_HEALTH_AND_FAMILY_EXPENSES(
        "showHealthAndFamilyExpenses",
        "Расходы на здоровье",
        "Медицинские и семейные расходы",
        "Здоровье и образ жизни",
        "Расходы на здоровье и семью",
        PlayerLevel.AUTONOMOUS
    ),
    
    SHOW_FAMILY_EXPENSES(
        "showFamilyExpenses",
        "Семейные расходы",
        "Расходы на семью (питание, уход за детьми)",
        "Здоровье и образ жизни",
        "Расходы на здоровье и семью",
        PlayerLevel.INVESTOR,
        listOf("showHealthAndFamilyExpenses")
    ),
    
    // Статистика
    SHOW_AGE_STATISTICS(
        "showAgeStatistics",
        "Статистика возраста",
        "Статистика возраста",
        "Здоровье и образ жизни",
        "Статистика",
        PlayerLevel.NOVICE,
        listOf("showHealthStatus")
    ),
    
    // === ВОЗМОЖНОСТИ И РАЗВИТИЕ ===
    
    // Образование и навыки
    SHOW_EDUCATION_SKILLS(
        "showEducationSkills",
        "Образование",
        "Курсы, университет",
        "Возможности и развитие",
        "Образование и навыки",
        PlayerLevel.NOVICE,
        listOf("showCoursesTraining", "showUniversity")
    ),
    
    SHOW_COURSES_TRAINING(
        "showCoursesTraining",
        "Курсы",
        "Список курсов и тренингов",
        "Возможности и развитие",
        "Образование и навыки",
        PlayerLevel.NOVICE,
        listOf("showEducationSkills")
    ),
    
    SHOW_UNIVERSITY(
        "showUniversity",
        "Университет",
        "Университетские программы",
        "Возможности и развитие",
        "Образование и навыки",
        PlayerLevel.NOVICE,
        listOf("showEducationSkills")
    ),
    
    // Личные отношения
    SHOW_PERSONAL_RELATIONSHIPS(
        "showPersonalRelationships",
        "Отношения",
        "Женитьба, создание семьи",
        "Возможности и развитие",
        "Личные отношения",
        PlayerLevel.AUTONOMOUS
    ),
    
    SHOW_FAMILY_CREATION(
        "showFamilyCreation",
        "Создание семьи",
        "Создание семьи",
        "Возможности и развитие",
        "Личные отношения",
        PlayerLevel.INVESTOR,
        listOf("showPersonalRelationships")
    ),
    
    // Карьера и бизнес
    SHOW_CAREER_BUSINESS(
        "showCareerBusiness",
        "Карьера",
        "Опции поиска работы и бизнеса",
        "Возможности и развитие",
        "Карьера и бизнес",
        PlayerLevel.NOVICE
    ),
    
    SHOW_BUSINESS_DEVELOPMENT(
        "showBusinessDevelopment",
        "Бизнес",
        "Открытие и развитие бизнеса",
        "Возможности и развитие",
        "Карьера и бизнес",
        PlayerLevel.PASSIVE_INCOME,
        listOf("showCareerBusiness")
    ),
    
    // Прочие возможности
    SHOW_OTHER_OPPORTUNITIES(
        "showOtherOpportunities",
        "Возможности",
        "Хобби, путешествия, социальные проекты",
        "Возможности и развитие",
        "Прочие возможности",
        PlayerLevel.NOVICE
    ),
    
    SHOW_PHILANTHROPY(
        "showPhilanthropy",
        "Филантропия",
        "Филантропия и социальные проекты",
        "Возможности и развитие",
        "Прочие возможности",
        PlayerLevel.FINANCIAL_MASTER,
        listOf("showOtherOpportunities")
    ),
    
    // === ИГРОВЫЕ ДЕЙСТВИЯ ===
    
    NEXT_TURN(
        "btn_next_turn",
        "Следующий ход",
        "Переход к следующему игровому дню",
        "",
        "",
        PlayerLevel.NOVICE,
        listOf("performNextTurn")
    );

    companion object {
        fun getActionsForLevel(level: PlayerLevel): List<MenuAction> {
            return values().filter { it.requiredLevel.requiredLevel <= level.requiredLevel }
        }
        
        fun getActionsByParentButton(parentButton: String, playerLevel: PlayerLevel): List<MenuAction> {
            return values().filter { 
                it.parentButton == parentButton && 
                it.requiredLevel.requiredLevel <= playerLevel.requiredLevel 
            }
        }
        
        fun getActionByName(actionName: String): MenuAction? {
            return values().find { it.actionName == actionName }
        }
    }
}

/**
 * Меню-группа для организации действий
 */
data class MenuGroup(
    val name: String,
    val displayName: String,
    val actions: List<MenuAction>,
    val isExpanded: Boolean = false
)

/**
 * Адаптивное меню для игрока
 */
class AdaptiveMenu(private val player: Player) {
    
    private val currentLevel = LevelTransitionChecker.determineCurrentLevel(player)
    
    /**
     * Получить все доступные действия для текущего уровня игрока
     */
    fun getAvailableActions(): List<MenuAction> {
        return MenuAction.getActionsForLevel(currentLevel)
    }
    
    /**
     * Получить действия, сгруппированные по родительским кнопкам
     */
    fun getActionsByParentButtons(): Map<String, List<MenuAction>> {
        val actions = getAvailableActions()
        return actions.groupBy { it.parentButton }
    }
    
    /**
     * Получить действия для конкретной родительской кнопки
     */
    fun getActionsForParentButton(parentButton: String): List<MenuAction> {
        return MenuAction.getActionsByParentButton(parentButton, currentLevel)
    }
    
    /**
     * Проверить, доступно ли действие для текущего уровня
     */
    fun isActionAvailable(actionName: String): Boolean {
        val action = MenuAction.getActionByName(actionName)
        return action?.let { it.requiredLevel.requiredLevel <= currentLevel.requiredLevel } ?: false
    }
    
    /**
     * Получить текущий уровень игрока
     */
    fun getCurrentLevel(): PlayerLevel = currentLevel
    
    /**
     * Получить прогресс к следующему уровню
     */
    fun getLevelProgress(): LevelProgress {
        val nextLevel = getNextLevel()
        return LevelTransitionChecker.getLevelProgress(player, nextLevel)
    }
    
    /**
     * Получить следующий уровень
     */
    fun getNextLevel(): PlayerLevel {
        val levels = PlayerLevel.values()
        val currentIndex = levels.indexOf(currentLevel)
        return if (currentIndex < levels.size - 1) levels[currentIndex + 1] else currentLevel
    }
    
    /**
     * Проверить, может ли игрок перейти на следующий уровень
     */
    fun canLevelUp(): Boolean {
        val nextLevel = getNextLevel()
        return LevelTransitionChecker.canTransitionToLevel(player, nextLevel)
    }
    
    /**
     * Получить информацию о меню для отображения
     */
    fun getMenuInfo(): MenuInfo {
        return MenuInfo(
            currentLevel = currentLevel,
            nextLevel = getNextLevel(),
            levelProgress = getLevelProgress(),
            canLevelUp = canLevelUp(),
            availableActions = getAvailableActions().size,
            totalActions = MenuAction.values().size
        )
    }
}

/**
 * Информация о меню для отображения
 */
data class MenuInfo(
    val currentLevel: PlayerLevel,
    val nextLevel: PlayerLevel,
    val levelProgress: LevelProgress,
    val canLevelUp: Boolean,
    val availableActions: Int,
    val totalActions: Int
)
