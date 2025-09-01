package com.financialsuccess.game.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

/**
 * Уровни зрелости игрока согласно таблице 1
 */
@Parcelize
enum class PlayerLevel(
    val displayName: String,
    val emoji: String,
    val description: String,
    val requiredLevel: Int
) : Parcelable {
    NOVICE("Новичок", "🟢", "Изучение основ финансов", 0),
    AUTONOMOUS("Автономный", "🟡", "Финансовая самостоятельность", 1),
    INVESTOR("Инвестор", "🟠", "Начало инвестиций и планирование активов", 2),
    PASSIVE_INCOME("Пассивный доход", "🔴", "Развитие пассивных источников дохода", 3),
    FINANCIAL_MASTER("Финансовый мастер", "🟣", "Полная финансовая независимость", 4);

    companion object {
        fun getLevelByRequiredLevel(requiredLevel: Int): PlayerLevel {
            return values().find { it.requiredLevel == requiredLevel } ?: NOVICE
        }
    }
}

/**
 * Условия перехода между уровнями
 */
data class LevelTransitionConditions(
    val minCash: Int = 0,
    val consecutiveTurnsWithoutDeficit: Int = 0,
    val hasEducation: Boolean = false,
    val minInvestedPercentage: Double = 0.0,
    val hasLargeAsset: Boolean = false,
    val passiveIncomeRatio: Double = 0.0,
    val consecutiveInvestmentTurns: Int = 0,
    val passiveIncomeCoversExpenses: Boolean = false,
    val minCapital: Int = 0
) {
    companion object {
        fun getConditionsForLevel(level: PlayerLevel): LevelTransitionConditions {
            return when (level) {
                PlayerLevel.NOVICE -> LevelTransitionConditions()
                PlayerLevel.AUTONOMOUS -> LevelTransitionConditions(
                    minCash = 20000,
                    hasEducation = true
                )
                PlayerLevel.INVESTOR -> LevelTransitionConditions(
                    minInvestedPercentage = 0.1, // 10%
                    hasLargeAsset = true
                )
                PlayerLevel.PASSIVE_INCOME -> LevelTransitionConditions(
                    passiveIncomeRatio = 0.5, // 50%
                    consecutiveInvestmentTurns = 5
                )
                PlayerLevel.FINANCIAL_MASTER -> LevelTransitionConditions(
                    passiveIncomeCoversExpenses = true,
                    minCapital = 100000
                )
            }
        }
    }
}

/**
 * Система проверки условий перехода между уровнями
 */
object LevelTransitionChecker {
    
    /**
     * Проверяет, может ли игрок перейти на следующий уровень
     */
    fun canTransitionToLevel(player: Player, targetLevel: PlayerLevel): Boolean {
        val conditions = LevelTransitionConditions.getConditionsForLevel(targetLevel)
        
        return when (targetLevel) {
            PlayerLevel.NOVICE -> true // Всегда доступен
            
            PlayerLevel.AUTONOMOUS -> {
                val hasMinCash = player.cash >= conditions.minCash
                val hasEducation = player.education != EducationLevel.HIGH_SCHOOL
                hasMinCash && hasEducation
            }
            
            PlayerLevel.INVESTOR -> {
                val totalCapital = player.cash + player.assets.sumOf { it.value } + 
                                 player.investments.sumOf { it.cost }
                val investedAmount = player.assets.sumOf { it.value } + 
                                   player.investments.sumOf { it.cost }
                val investedPercentage = if (totalCapital > 0) investedAmount.toDouble() / totalCapital else 0.0
                
                val hasInvestedEnough = investedPercentage >= conditions.minInvestedPercentage
                val hasLargeAsset = player.assets.any { it.value >= 50000 } // Крупный актив >= 50,000
                
                hasInvestedEnough && hasLargeAsset
            }
            
            PlayerLevel.PASSIVE_INCOME -> {
                val monthlyIncome = player.totalIncome
                val passiveIncomeRatio = if (monthlyIncome > 0) player.passiveIncome.toDouble() / monthlyIncome else 0.0
                
                val hasPassiveIncomeRatio = passiveIncomeRatio >= conditions.passiveIncomeRatio
                val hasConsecutiveInvestmentTurns = player.monthsPlayed >= conditions.consecutiveInvestmentTurns
                
                hasPassiveIncomeRatio && hasConsecutiveInvestmentTurns
            }
            
            PlayerLevel.FINANCIAL_MASTER -> {
                val passiveIncomeCoversExpenses = player.passiveIncome >= player.totalExpenses
                val totalCapital = player.cash + player.assets.sumOf { it.value } + 
                                 player.investments.sumOf { it.cost }
                val hasMinCapital = totalCapital >= conditions.minCapital
                
                passiveIncomeCoversExpenses && hasMinCapital
            }
        }
    }
    
    /**
     * Определяет текущий уровень игрока на основе его состояния
     */
    fun determineCurrentLevel(player: Player): PlayerLevel {
        return when {
            canTransitionToLevel(player, PlayerLevel.FINANCIAL_MASTER) -> PlayerLevel.FINANCIAL_MASTER
            canTransitionToLevel(player, PlayerLevel.PASSIVE_INCOME) -> PlayerLevel.PASSIVE_INCOME
            canTransitionToLevel(player, PlayerLevel.INVESTOR) -> PlayerLevel.INVESTOR
            canTransitionToLevel(player, PlayerLevel.AUTONOMOUS) -> PlayerLevel.AUTONOMOUS
            else -> PlayerLevel.NOVICE
        }
    }
    
    /**
     * Проверяет, может ли игрок перейти на следующий уровень и возвращает информацию о прогрессе
     */
    fun getLevelProgress(player: Player, targetLevel: PlayerLevel): LevelProgress {
        val conditions = LevelTransitionConditions.getConditionsForLevel(targetLevel)
        val currentLevel = determineCurrentLevel(player)
        
        return when (targetLevel) {
            PlayerLevel.NOVICE -> LevelProgress(100, 100, "Новичок - базовый уровень")
            
            PlayerLevel.AUTONOMOUS -> {
                val cashProgress = minOf(100, (player.cash * 100 / conditions.minCash))
                val educationProgress = if (player.education != EducationLevel.HIGH_SCHOOL) 100 else 0
                val overallProgress = (cashProgress + educationProgress) / 2
                
                LevelProgress(
                    overallProgress,
                    overallProgress,
                    "Накопить ${conditions.minCash}₽ и получить образование"
                )
            }
            
            PlayerLevel.INVESTOR -> {
                val totalCapital = player.cash + player.assets.sumOf { it.value } + 
                                 player.investments.sumOf { it.cost }
                val investedAmount = player.assets.sumOf { it.value } + 
                                   player.investments.sumOf { it.cost }
                val investedPercentage = if (totalCapital > 0) investedAmount.toDouble() / totalCapital else 0.0
                val investmentProgress = minOf(100, (investedPercentage * 100 / conditions.minInvestedPercentage).toInt())
                
                val hasLargeAsset = player.assets.any { it.value >= 50000 }
                val assetProgress = if (hasLargeAsset) 100 else 0
                
                val overallProgress = (investmentProgress + assetProgress) / 2
                
                LevelProgress(
                    overallProgress,
                    overallProgress,
                    "Инвестировать ${(conditions.minInvestedPercentage * 100).toInt()}% капитала и купить крупный актив"
                )
            }
            
            PlayerLevel.PASSIVE_INCOME -> {
                val monthlyIncome = player.totalIncome
                val passiveIncomeRatio = if (monthlyIncome > 0) player.passiveIncome.toDouble() / monthlyIncome else 0.0
                val ratioProgress = minOf(100, (passiveIncomeRatio * 100 / conditions.passiveIncomeRatio).toInt())
                
                val turnProgress = minOf(100, (player.monthsPlayed * 100 / conditions.consecutiveInvestmentTurns))
                
                val overallProgress = (ratioProgress + turnProgress) / 2
                
                LevelProgress(
                    overallProgress,
                    overallProgress,
                    "Пассивный доход ${(conditions.passiveIncomeRatio * 100).toInt()}% от общего дохода и ${conditions.consecutiveInvestmentTurns} месяцев инвестирования"
                )
            }
            
            PlayerLevel.FINANCIAL_MASTER -> {
                val passiveIncomeCoversExpenses = player.passiveIncome >= player.totalExpenses
                val expensesProgress = if (passiveIncomeCoversExpenses) 100 else 0
                
                val totalCapital = player.cash + player.assets.sumOf { it.value } + 
                                 player.investments.sumOf { it.cost }
                val capitalProgress = minOf(100, (totalCapital * 100 / conditions.minCapital))
                
                val overallProgress = (expensesProgress + capitalProgress) / 2
                
                LevelProgress(
                    overallProgress,
                    overallProgress,
                    "Пассивный доход покрывает расходы и капитал ${conditions.minCapital}₽"
                )
            }
        }
    }
}

/**
 * Информация о прогрессе к следующему уровню
 */
data class LevelProgress(
    val progress: Int, // Процент прогресса (0-100)
    val overallProgress: Int, // Общий прогресс
    val description: String // Описание требований
)
