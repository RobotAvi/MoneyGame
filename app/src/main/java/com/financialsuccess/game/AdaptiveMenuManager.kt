package com.financialsuccess.game

import android.content.Context
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.financialsuccess.game.models.*
import com.financialsuccess.game.databinding.ActivityGameBinding

/**
 * Менеджер адаптивного меню для GameActivity
 */
class AdaptiveMenuManager(
    private val context: Context,
    private val binding: ActivityGameBinding,
    private val gameActivity: GameActivity
) {
    
    private var adaptiveMenu: AdaptiveMenu? = null
    
    /**
     * Инициализировать адаптивное меню
     */
    fun initializeMenu(player: Player) {
        adaptiveMenu = AdaptiveMenu(player)
        updateMenuVisibility()
        setupMenuClickListeners()
    }
    
    /**
     * Обновить меню при изменении игрока
     */
    fun updateMenu(player: Player) {
        adaptiveMenu = AdaptiveMenu(player)
        updateMenuVisibility()
        updateLevelDisplay()
    }
    
    /**
     * Обновить видимость кнопок меню в зависимости от уровня
     */
    private fun updateMenuVisibility() {
        adaptiveMenu?.let { menu ->
            val currentLevel = menu.getCurrentLevel()
            
            // Обновляем доступность кнопок в зависимости от уровня
            updateButtonAccessibility(binding.btnIncome, MenuAction.SHOW_INCOME_DIALOG, currentLevel)
            updateButtonAccessibility(binding.btnExpenses, MenuAction.SHOW_EXPENSES_DIALOG, currentLevel)
            updateButtonAccessibility(binding.btnOpportunities, MenuAction.SHOW_OTHER_OPPORTUNITIES, currentLevel)
            
            // Кнопка следующего хода всегда доступна
            binding.btnNextTurn.isEnabled = true
        }
    }
    
    /**
     * Обновить доступность кнопки
     */
    private fun updateButtonAccessibility(button: ImageButton, action: MenuAction, currentLevel: PlayerLevel) {
        val isAvailable = action.requiredLevel.requiredLevel <= currentLevel.requiredLevel
        button.isEnabled = isAvailable
        button.alpha = if (isAvailable) 1.0f else 0.5f
    }
    
    /**
     * Обновить отображение уровня
     */
    private fun updateLevelDisplay() {
        adaptiveMenu?.let { menu ->
            val menuInfo = menu.getMenuInfo()
            val levelInfo = menuInfo.currentLevel
            
            // Обновляем отображение уровня в UI (если есть соответствующие поля)
            // Здесь можно добавить обновление TextView для отображения уровня
        }
    }
    
    /**
     * Настроить обработчики кликов для меню
     */
    private fun setupMenuClickListeners() {
        // Финансы и инвестиции
        binding.btnIncome.setOnClickListener {
            showFinanceMenu()
        }
        
        // Здоровье и образ жизни
        binding.btnExpenses.setOnClickListener {
            showHealthMenu()
        }
        
        // Возможности и развитие
        binding.btnOpportunities.setOnClickListener {
            showOpportunitiesMenu()
        }
        
        // Следующий ход
        binding.btnNextTurn.setOnClickListener {
            gameActivity.performNextTurn()
        }
    }
    
    /**
     * Показать меню финансов и инвестиций
     */
    private fun showFinanceMenu() {
        adaptiveMenu?.let { menu ->
            val currentLevel = menu.getCurrentLevel()
            val actions = menu.getActionsForParentButton("Финансы и инвестиции")
            
            if (actions.isEmpty()) {
                Toast.makeText(context, "Нет доступных действий для вашего уровня", Toast.LENGTH_SHORT).show()
                return
            }
            
            val options = actions.map { "${it.displayName} (${it.requiredLevel.emoji})" }.toTypedArray()
            
            AlertDialog.Builder(context)
                .setTitle("💰 Финансы и инвестиции")
                .setItems(options) { _, which ->
                    val selectedAction = actions[which]
                    executeMenuAction(selectedAction)
                }
                .show()
        }
    }
    
    /**
     * Показать меню здоровья и образа жизни
     */
    private fun showHealthMenu() {
        adaptiveMenu?.let { menu ->
            val currentLevel = menu.getCurrentLevel()
            val actions = menu.getActionsForParentButton("Здоровье и образ жизни")
            
            if (actions.isEmpty()) {
                Toast.makeText(context, "Нет доступных действий для вашего уровня", Toast.LENGTH_SHORT).show()
                return
            }
            
            val options = actions.map { "${it.displayName} (${it.requiredLevel.emoji})" }.toTypedArray()
            
            AlertDialog.Builder(context)
                .setTitle("🏥 Здоровье и образ жизни")
                .setItems(options) { _, which ->
                    val selectedAction = actions[which]
                    executeMenuAction(selectedAction)
                }
                .show()
        }
    }
    
    /**
     * Показать меню возможностей и развития
     */
    private fun showOpportunitiesMenu() {
        adaptiveMenu?.let { menu ->
            val currentLevel = menu.getCurrentLevel()
            val actions = menu.getActionsForParentButton("Возможности и развитие")
            
            if (actions.isEmpty()) {
                Toast.makeText(context, "Нет доступных действий для вашего уровня", Toast.LENGTH_SHORT).show()
                return
            }
            
            val options = actions.map { "${it.displayName} (${it.requiredLevel.emoji})" }.toTypedArray()
            
            AlertDialog.Builder(context)
                .setTitle("🚀 Возможности и развитие")
                .setItems(options) { _, which ->
                    val selectedAction = actions[which]
                    executeMenuAction(selectedAction)
                }
                .show()
        }
    }
    
    /**
     * Выполнить действие меню
     */
    private fun executeMenuAction(action: MenuAction) {
        when (action) {
            // Финансы и инвестиции
            MenuAction.SHOW_INCOME_DIALOG -> gameActivity.showIncomeDialog()
            MenuAction.SHOW_EXPENSES_DIALOG -> gameActivity.showExpensesDialog()
            MenuAction.SHOW_BALANCE_PANEL -> gameActivity.showBalancePanel()
            MenuAction.SHOW_FINANCIAL_JOURNAL -> gameActivity.showFinancialJournal()
            MenuAction.SHOW_JOURNAL_ANALYTICS -> gameActivity.showJournalAnalytics()
            MenuAction.SHOW_FINANCIAL_STATEMENT -> gameActivity.showFinancialStatement()
            MenuAction.SHOW_MARKET_DIALOG -> gameActivity.showMarketDialog()
            MenuAction.SHOW_AVAILABLE_ASSETS -> gameActivity.showAvailableAssets()
            MenuAction.SHOW_ASSET_DETAILS -> gameActivity.showAssetDetails()
            MenuAction.SHOW_AVAILABLE_INVESTMENTS -> gameActivity.showAvailableInvestments()
            MenuAction.SHOW_INVESTMENT_DETAILS -> gameActivity.showInvestmentDetails()
            MenuAction.SHOW_PORTFOLIO -> gameActivity.showPortfolio()
            
            // Здоровье и образ жизни
            MenuAction.SHOW_HEALTH_STATUS -> gameActivity.showHealthStatus()
            MenuAction.SHOW_HEALTHCARE_OPTIONS -> gameActivity.showHealthcareOptions()
            MenuAction.BUY_HEALTHCARE -> gameActivity.buyHealthcare()
            MenuAction.SHOW_HABITS_AND_FAMILY_INFLUENCE -> gameActivity.showHabitsAndFamilyInfluence()
            MenuAction.SHOW_HEALTH_AND_FAMILY_EXPENSES -> gameActivity.showHealthAndFamilyExpenses()
            MenuAction.SHOW_FAMILY_EXPENSES -> gameActivity.showFamilyExpenses()
            MenuAction.SHOW_AGE_STATISTICS -> gameActivity.showAgeStatistics()
            
            // Возможности и развитие
            MenuAction.SHOW_EDUCATION_SKILLS -> gameActivity.showEducationSkills()
            MenuAction.SHOW_COURSES_TRAINING -> gameActivity.showCoursesTraining()
            MenuAction.SHOW_UNIVERSITY -> gameActivity.showUniversity()
            MenuAction.SHOW_PERSONAL_RELATIONSHIPS -> gameActivity.showPersonalRelationships()
            MenuAction.SHOW_FAMILY_CREATION -> gameActivity.showFamilyCreation()
            MenuAction.SHOW_CAREER_BUSINESS -> gameActivity.showCareerBusiness()
            MenuAction.SHOW_BUSINESS_DEVELOPMENT -> gameActivity.showBusinessDevelopment()
            MenuAction.SHOW_OTHER_OPPORTUNITIES -> gameActivity.showOtherOpportunities()
            MenuAction.SHOW_PHILANTHROPY -> gameActivity.showPhilanthropy()
            
            // Игровые действия
            MenuAction.NEXT_TURN -> gameActivity.performNextTurn()
        }
    }
    
    /**
     * Показать информацию о текущем уровне
     */
    fun showLevelInfo() {
        adaptiveMenu?.let { menu ->
            val menuInfo = menu.getMenuInfo()
            val levelInfo = menuInfo.currentLevel
            val progress = menuInfo.levelProgress
            
            val message = """
                ${levelInfo.emoji} ${levelInfo.displayName}
                
                ${levelInfo.description}
                
                Прогресс к следующему уровню: ${progress.progress}%
                ${progress.description}
                
                Доступно действий: ${menuInfo.availableActions}/${menuInfo.totalActions}
            """.trimIndent()
            
            AlertDialog.Builder(context)
                .setTitle("📊 Информация об уровне")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show()
        }
    }
    
    /**
     * Проверить повышение уровня
     */
    fun checkLevelUp(): Boolean {
        adaptiveMenu?.let { menu ->
            if (menu.canLevelUp()) {
                val nextLevel = menu.getNextLevel()
                val message = """
                    🎉 Поздравляем!
                    
                    Вы достигли уровня: ${nextLevel.emoji} ${nextLevel.displayName}
                    
                    ${nextLevel.description}
                    
                    Новые возможности разблокированы!
                """.trimIndent()
                
                AlertDialog.Builder(context)
                    .setTitle("🎊 Повышение уровня!")
                    .setMessage(message)
                    .setPositiveButton("Отлично!") { _, _ ->
                        // Обновляем меню после повышения уровня
                        updateMenuVisibility()
                    }
                    .setCancelable(false)
                    .show()
                
                return true
            }
        }
        return false
    }
}
