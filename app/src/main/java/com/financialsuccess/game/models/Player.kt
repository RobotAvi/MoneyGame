package com.financialsuccess.game.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Player(
    var position: Int = 0,
    var cash: Int = 0,
    var salary: Int = 0,
    var totalIncome: Int = 0,
    var totalExpenses: Int = 0,
    var passiveIncome: Int = 0,
    var isInFastTrack: Boolean = false,
    // Возраст и жизнь
    var age: Int = 25,
    var deathAge: Int = 85, // Устанавливается случайно при создании персонажа
    var monthsPlayed: Int = 0,
    // Детализированные расходы
    var foodExpenses: Int = 0,
    var transportExpenses: Int = 0,
    var housingExpenses: Int = 0,
    var childrenExpenses: Int = 0,
    var taxes: Int = 0,
    var otherExpenses: Int = 0,
    val profession: Profession? = null,
    val dream: Dream? = null,
    val assets: MutableList<Asset> = mutableListOf(),
    val liabilities: MutableList<Liability> = mutableListOf(),
    val investments: MutableList<Investment> = mutableListOf(),
    val financialJournal: MutableList<FinancialEntry> = mutableListOf(),
    val activeRisks: MutableList<ProfessionalRisk> = mutableListOf(),
    val riskEffects: MutableList<RiskEffect> = mutableListOf(),
    var lastRiskActivated: ProfessionalRisk? = null,
    var currentDayOfMonth: Int = 1, // Текущий день месяца (игровой)
    var name: String? = null, // Имя персонажа
    var startDateMillis: Long? = null, // Дата начала игры (в миллисекундах)
    
    // === НОВЫЕ ПОЛЯ ДЛЯ РАСШИРЕННОЙ ПЕРСОНАЛИЗАЦИИ ===
    
    // Образование и опыт
    var education: EducationLevel = EducationLevel.BACHELOR,
    var workExperience: Int = 0, // Опыт работы в годах
    var skills: MutableList<Skill> = mutableListOf(),
    
    // Семейное положение
    var maritalStatus: MaritalStatus = MaritalStatus.SINGLE,
    var childrenCount: Int = 0,
    var spouseIncome: Int = 0, // Доход супруга/супруги
    
    // Финансовые предпочтения
    var riskTolerance: RiskTolerance = RiskTolerance.MODERATE,
    var investmentStrategy: InvestmentStrategy = InvestmentStrategy.BALANCED,
    var savingsRate: Int = 10, // Процент от дохода на сбережения (0-50)
    
    // Жизненные цели
    var financialGoals: MutableList<FinancialGoal> = mutableListOf(),
    var retirementAge: Int = 65,
    var targetPassiveIncome: Int = 100000, // Целевой пассивный доход
    
    // Дополнительные параметры
    var healthLevel: HealthLevel = HealthLevel.GOOD,
    var stressLevel: StressLevel = StressLevel.LOW,
    var workLifeBalance: WorkLifeBalance = WorkLifeBalance.BALANCED,
    
    // Статистика и достижения
    var totalEarned: Int = 0, // Общий заработок за всю жизнь
    var totalSpent: Int = 0, // Общие траты за всю жизнь
    var totalInvested: Int = 0, // Общая сумма инвестиций
    var careerPromotions: Int = 0, // Количество повышений
    var businessFailures: Int = 0, // Количество неудачных бизнесов
    var successfulInvestments: Int = 0 // Количество успешных инвестиций
) : Parcelable {
    
    companion object {
        const val DAYS_IN_MONTH = 30
    }

    fun getNetWorth(): Int = 
        assets.sumOf { it.value } - liabilities.sumOf { it.amount }
    
    fun getCashFlow(): Int = 
        totalIncome + passiveIncome - totalExpenses
    
    fun canEscapeRatRace(): Boolean = 
        passiveIncome >= totalExpenses
    
    fun updateTotalIncome() {
        // Основной доход = зарплата + доход супруга
        totalIncome = salary + spouseIncome
        passiveIncome = assets.sumOf { it.cashFlow } + investments.sumOf { it.expectedReturn }
    }
    
    fun updateTotalExpenses() {
        // Базовые расходы из профессии
        val professionExpenses = profession?.expenses ?: 0
        val professionTaxes = profession?.taxes ?: 0
        
        // Распределяем базовые расходы по категориям
        foodExpenses = (professionExpenses * 0.4).toInt()
        transportExpenses = (professionExpenses * 0.15).toInt() 
        housingExpenses = (professionExpenses * 0.3).toInt()
        otherExpenses = professionExpenses - foodExpenses - transportExpenses - housingExpenses
        taxes = professionTaxes
        
        // Добавляем семейные расходы
        childrenExpenses = childrenCount * 8000 // 8000 рублей на ребенка в месяц
        
        // Применяем модификаторы здоровья
        val healthMultiplier = healthLevel.expenseMultiplier
        foodExpenses = (foodExpenses * healthMultiplier).toInt()
        otherExpenses = (otherExpenses * healthMultiplier).toInt()
        
        // Общие расходы = базовые + кредиты + дети + семья
        totalExpenses = foodExpenses + transportExpenses + housingExpenses + 
                       childrenExpenses + taxes + otherExpenses + 
                       liabilities.sumOf { it.payment }
        // Добавляем расходы на супруга/супругу после healthMultiplier
        if (maritalStatus == MaritalStatus.MARRIED) {
            totalExpenses += 5000 // Дополнительные расходы на семью
        }
    }
    
    // Автоматическое списание расходов каждый ход (устарела - теперь используется денежный поток)
    @Deprecated("Используйте getCashFlow() вместо отдельного списания расходов")
    fun payMonthlyExpenses() {
        cash -= totalExpenses
    }
    
    // Добавить ребенка (увеличивает расходы)
    fun addChild() {
        childrenExpenses += 8000 // 8000 рублей на ребенка в месяц
        updateTotalExpenses()
    }
    
    // Получить ROI для актива
    fun getAssetROI(asset: Asset): Double {
        return if (asset.downPayment > 0) {
            (asset.cashFlow * 12.0 / asset.downPayment) * 100
        } else 0.0
    }
    
    fun addAsset(asset: Asset) {
        if (cash >= asset.downPayment) {
            cash -= asset.downPayment
            assets.add(asset)
            
            // Добавляем долг, если есть кредит
            if (asset.loan > 0) {
                val liability = Liability(
                    name = "Кредит на ${asset.name}",
                    amount = asset.loan,
                    payment = asset.loanPayment
                )
                liabilities.add(liability)
            }
            
            updateTotalIncome()
            updateTotalExpenses()
        }
    }
    
    // === ВОЗРАСТ И ЖИЗНЬ ===
    
    // Установить случайный возраст смерти
    fun setRandomDeathAge() {
        deathAge = (70..110).random()
    }
    
    // Пройти один месяц (увеличивает возраст)
    fun passMonth() {
        monthsPlayed++
        currentDayOfMonth = 1
        // Каждые 12 месяцев увеличиваем возраст на 1 год
        if (monthsPlayed % 12 == 0) {
            age++
        }
    }
    
    // Проверить, жив ли игрок
    fun isAlive(): Boolean = age < deathAge
    
    // Получить оставшиеся годы жизни
    fun getYearsLeft(): Int = maxOf(0, deathAge - age)
    
    // Получить процент прожитой жизни
    fun getLifePercentage(): Float = (age.toFloat() / deathAge.toFloat() * 100)
    
    // Проверить критический возраст (последние 5 лет)
    fun isInCriticalAge(): Boolean = getYearsLeft() <= 5
    
    // === ФИНАНСОВЫЙ ЖУРНАЛ ===
    
    // Добавить запись в финансовый журнал
    fun addFinancialEntry(
        type: FinancialEntryType,
        category: FinancialCategory,
        amount: Int,
        description: String
    ) {
        val months = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
                            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
        val startYear = 2024
        val currentMonth = monthsPlayed % 12
        val currentYear = startYear + monthsPlayed / 12
        val realDate = "$currentDayOfMonth ${months[currentMonth]} $currentYear"
        val entry = FinancialEntry(
            type = type,
            category = category,
            amount = amount,
            description = description,
            playerAge = age,
            monthNumber = monthsPlayed,
            balanceAfter = cash,
            realDate = realDate
        )
        financialJournal.add(entry)
        if (financialJournal.size > 500) {
            financialJournal.removeAt(0)
        }
    }
    
    // Логировать доход
    fun logIncome(category: FinancialCategory, amount: Int, description: String) {
        addFinancialEntry(FinancialEntryType.INCOME, category, amount, description)
    }
    
    // Логировать расход
    fun logExpense(category: FinancialCategory, amount: Int, description: String) {
        cash -= amount
        addFinancialEntry(FinancialEntryType.EXPENSE, category, -amount, description)
    }
    
    // Получить записи за определенный период
    fun getJournalEntriesForPeriod(startMonth: Int, endMonth: Int): List<FinancialEntry> {
        return financialJournal.filter { it.monthNumber in startMonth..endMonth }
    }
    
    // Получить последние N записей
    fun getRecentJournalEntries(count: Int = 50): List<FinancialEntry> {
        return financialJournal.takeLast(count).reversed() // Новые записи сверху
    }
    
    // Получить общую статистику по категориям
    fun getCategoryStats(): Map<FinancialCategory, Int> {
        return financialJournal.groupBy { it.category }
            .mapValues { (_, entries) -> entries.sumOf { it.amount } }
    }
    
    // Получить доходы/расходы за месяц
    fun getMonthlyStats(month: Int): Pair<Int, Int> {
        val monthEntries = financialJournal.filter { it.monthNumber == month }
        val income = monthEntries.filter { it.type == FinancialEntryType.INCOME }.sumOf { it.amount }
        val expense = monthEntries.filter { it.type == FinancialEntryType.EXPENSE }.sumOf { kotlin.math.abs(it.amount) }
        return Pair(income, expense)
    }
    
    // Провести ежемесячные операции с логированием (вызывается при каждом ходе)
    fun processMonthlyOperations() {
        // Проверяем профессиональные риски
        checkProfessionalRisks()
        
        // Основные расходы (ВСЕГДА списываются, даже если недостаточно денег)
        val totalMonthlyExpenses = totalExpenses
        
        // Логируем и списываем все расходы
        if (foodExpenses > 0) {
            logExpense(FinancialCategory.FOOD, foodExpenses, "Ежемесячные расходы на еду")
        }
        if (transportExpenses > 0) {
            logExpense(FinancialCategory.TRANSPORT, transportExpenses, "Ежемесячные расходы на транспорт")
        }
        if (housingExpenses > 0) {
            logExpense(FinancialCategory.HOUSING, housingExpenses, "Ежемесячные расходы на жилье")
        }
        if (childrenExpenses > 0) {
            logExpense(FinancialCategory.CHILDREN, childrenExpenses, "Ежемесячные расходы на детей")
        }
        if (taxes > 0) {
            logExpense(FinancialCategory.TAXES, taxes, "Ежемесячные налоги")
        }
        if (otherExpenses > 0) {
            logExpense(FinancialCategory.OTHER_EXPENSES, otherExpenses, "Прочие ежемесячные расходы")
        }
        
        // Выплаты по кредитам
        liabilities.forEach { liability ->
            if (liability.payment > 0) {
                logExpense(FinancialCategory.LOAN_PAYMENT, liability.payment, "Выплата по кредиту: ${liability.name}")
            }
        }
        
        // Доходы от активов (всегда получаем)
        assets.forEach { asset ->
            if (asset.cashFlow > 0) {
                cash += asset.cashFlow
                logIncome(FinancialCategory.ASSET_INCOME, asset.cashFlow, "Доход от актива: ${asset.name}")
            }
        }
        
        // Доходы от инвестиций (всегда получаем)
        investments.forEach { investment ->
            if (investment.expectedReturn > 0) {
                cash += investment.expectedReturn
                logIncome(FinancialCategory.INVESTMENT_RETURN, investment.expectedReturn, "Доходность инвестиции: ${investment.name}")
            }
        }
        
        // Дополнительный пассивный доход (всегда получаем)
        if (passiveIncome > 0) {
            val additionalPassive = passiveIncome - assets.sumOf { it.cashFlow } - investments.sumOf { it.expectedReturn }
            if (additionalPassive > 0) {
                cash += additionalPassive
                logIncome(FinancialCategory.PASSIVE_INCOME, additionalPassive, "Ежемесячный пассивный доход")
            }
        }
        
        // Применяем сбережения
        applySavings()
        
        // Обновляем статистику
        totalEarned += totalIncome + passiveIncome
        totalSpent += totalExpenses
     }
     
     // === ПРОФЕССИОНАЛЬНЫЕ РИСКИ ===
     
     // Проверить профессиональные риски (вызывается каждый ход)
     fun checkProfessionalRisks() {
         val profession = this.profession ?: return
         val availableRisks = ProfessionalRisks.getRisksForProfession(profession.name)
         
         for (risk in availableRisks) {
             // Проверяем возрастной диапазон
             if (age !in risk.ageRange) continue
             
             // Проверяем, не активен ли уже этот риск
             if (activeRisks.any { it.name == risk.name }) continue
             
             // Проверяем вероятность наступления
             if (kotlin.random.Random.nextDouble() <= risk.probability / 100.0) { // Конвертируем в правильную вероятность
                 // Риск реализовался!
                 activateRisk(risk)
             }
         }
         
         // Обновляем активные эффекты рисков
         updateRiskEffects()
     }
     
     // Активировать профессиональный риск
     private fun activateRisk(risk: ProfessionalRisk) {
         activeRisks.add(risk)
         riskEffects.addAll(risk.effects)
         
         // Сохраняем информацию о новом риске для уведомления
         lastRiskActivated = risk
         
         // Логируем событие
         logExpense(
             FinancialCategory.EMERGENCY,
             risk.effects.sumOf { it.expenseIncrease },
             "Профессиональный риск: ${risk.name} - ${risk.description}"
         )
         
         // Применяем немедленные эффекты
         risk.effects.forEach { effect ->
             // Увеличиваем расходы
             if (effect.expenseIncrease > 0) {
                 otherExpenses += effect.expenseIncrease
             }
             
             // Уменьшаем зарплату
             if (effect.salaryReduction > 0) {
                 salary = maxOf(0, salary - effect.salaryReduction)
             }
             
             // Завершение карьеры
             if (effect.careerEnd) {
                 salary = 0
                 // Можно добавить принудительную смену профессии
             }
         }
         
         updateTotalIncome()
         updateTotalExpenses()
     }
     
     // Обновить эффекты рисков (восстановление)
     private fun updateRiskEffects() {
         val recoveredEffects = mutableListOf<RiskEffect>()
         
         riskEffects.forEach { effect ->
             if (effect.recoveryTime > 0) {
                 // Уменьшаем время восстановления
                 val newEffect = effect.copy(recoveryTime = effect.recoveryTime - 1)
                 
                 if (newEffect.recoveryTime <= 0) {
                     // Восстановились!
                     recoveredEffects.add(effect)
                     
                     // Восстанавливаем зарплату (частично)
                     if (effect.salaryReduction > 0) {
                         salary += (effect.salaryReduction * 0.8).toInt() // 80% восстановление
                     }
                     
                     // Уменьшаем расходы на лечение
                     if (effect.expenseIncrease > 0) {
                         otherExpenses = maxOf(0, otherExpenses - (effect.expenseIncrease * 0.5).toInt())
                     }
                     
                     logIncome(
                         FinancialCategory.BONUS,
                         (effect.salaryReduction * 0.8).toInt(),
                         "Восстановление после ${effect.description}"
                     )
                 }
             }
         }
         
         // Удаляем восстановленные эффекты
         riskEffects.removeAll(recoveredEffects)
         
         if (recoveredEffects.isNotEmpty()) {
             updateTotalIncome()
             updateTotalExpenses()
         }
     }
     
     // Получить активные профессиональные риски
     fun getActiveRisksDescription(): String {
         return if (activeRisks.isEmpty()) {
             "Нет активных профессиональных рисков"
         } else {
             activeRisks.joinToString("\n") { "${it.icon} ${it.name}: ${it.description}" }
         }
     }
     
     // Проверить серьезность состояния здоровья
     fun getHealthStatus(): String {
         val severeRisks = riskEffects.count { it.severity == RiskSeverity.SEVERE }
         val moderateRisks = riskEffects.count { it.severity == RiskSeverity.MODERATE }
         val mildRisks = riskEffects.count { it.severity == RiskSeverity.MILD }
         
         return when {
             severeRisks > 0 -> "🔴 Серьезные проблемы со здоровьем"
             moderateRisks > 1 -> "🟡 Умеренные проблемы со здоровьем"
             moderateRisks > 0 || mildRisks > 2 -> "🟡 Легкие проблемы со здоровьем"
             else -> "🟢 Здоров"
         }
     }
     
     // === НОВЫЕ МЕТОДЫ ДЛЯ РАСШИРЕННОЙ ПЕРСОНАЛИЗАЦИИ ===
     
     // Рассчитать бонус к зарплате на основе образования и опыта
     fun calculateEducationBonus(): Int {
         val educationBonus = when (education) {
             EducationLevel.HIGH_SCHOOL -> 0
             EducationLevel.COLLEGE -> 5000
             EducationLevel.BACHELOR -> 10000
             EducationLevel.MASTER -> 15000
             EducationLevel.PHD -> 20000
         }
         
         val experienceBonus = workExperience * 2000 // 2000 рублей за каждый год опыта
         
         return educationBonus + experienceBonus
     }
     
     // Рассчитать бонус к зарплате на основе навыков
     fun calculateSkillsBonus(): Int {
         return skills.sumOf { it.salaryBonus }
     }
     
     // Обновить зарплату с учетом всех бонусов
     fun updateSalaryWithBonuses() {
         val baseSalary = profession?.salary ?: 0
         val educationBonus = calculateEducationBonus()
         val skillsBonus = calculateSkillsBonus()
         
         salary = baseSalary + educationBonus + skillsBonus
         updateTotalIncome()
     }
     
     // Рассчитать семейные расходы
     fun calculateFamilyExpenses(): Int {
         var familyExpenses = 0
         
         // Расходы на детей
         familyExpenses += childrenCount * 8000
         
         // Расходы на супруга/супругу
         if (maritalStatus == MaritalStatus.MARRIED) {
             familyExpenses += 5000 // Дополнительные расходы на семью
         }
         
         return familyExpenses
     }
     
     // Рассчитать сбережения
     fun calculateSavings(): Int {
         return (totalIncome * savingsRate) / 100
     }
     
     // Применить сбережения
     fun applySavings() {
         val savings = calculateSavings()
         if (cash >= savings) {
             cash -= savings
             totalInvested += savings
             logExpense(FinancialCategory.SAVINGS, savings, "Ежемесячные сбережения")
         }
     }
     
     // Проверить достижение финансовых целей
     fun checkFinancialGoals(): List<FinancialGoal> {
         val achievedGoals = mutableListOf<FinancialGoal>()
         
         financialGoals.forEach { goal ->
             when (goal.type) {
                 GoalType.PASSIVE_INCOME -> {
                     if (passiveIncome >= goal.targetAmount) {
                         achievedGoals.add(goal)
                     }
                 }
                 GoalType.NET_WORTH -> {
                     if (getNetWorth() >= goal.targetAmount) {
                         achievedGoals.add(goal)
                     }
                 }
                 GoalType.SAVINGS -> {
                     if (cash >= goal.targetAmount) {
                         achievedGoals.add(goal)
                     }
                 }
                 GoalType.RETIREMENT -> {
                     if (age >= retirementAge && passiveIncome >= targetPassiveIncome) {
                         achievedGoals.add(goal)
                     }
                 }
             }
         }
         
         return achievedGoals
     }
     
     // Получить общую статистику жизни
     fun getLifeStatistics(): Map<String, Any> {
         return mapOf(
             "totalEarned" to totalEarned,
             "totalSpent" to totalSpent,
             "totalInvested" to totalInvested,
             "careerPromotions" to careerPromotions,
             "businessFailures" to businessFailures,
             "successfulInvestments" to successfulInvestments,
             "yearsWorked" to workExperience,
             "childrenCount" to childrenCount,
             "netWorth" to getNetWorth(),
             "passiveIncome" to passiveIncome,
             "lifePercentage" to getLifePercentage()
         )
     }
}

// === НОВЫЕ ENUM КЛАССЫ И DATA КЛАССЫ ===

enum class EducationLevel(val displayName: String, val salaryMultiplier: Double) {
    HIGH_SCHOOL("Среднее образование", 0.8),
    COLLEGE("Среднее специальное", 0.9),
    BACHELOR("Высшее образование", 1.0),
    MASTER("Магистратура", 1.2),
    PHD("Докторантура", 1.4)
}

enum class MaritalStatus(val displayName: String) {
    SINGLE("Холост/Не замужем"),
    MARRIED("Женат/Замужем"),
    DIVORCED("Разведен/Разведена"),
    WIDOWED("Вдовец/Вдова")
}

enum class RiskTolerance(val displayName: String, val investmentMultiplier: Double) {
    CONSERVATIVE("Консервативный", 0.7),
    MODERATE("Умеренный", 1.0),
    AGGRESSIVE("Агрессивный", 1.3)
}

enum class InvestmentStrategy(val displayName: String) {
    CONSERVATIVE("Консервативная"),
    BALANCED("Сбалансированная"),
    AGGRESSIVE("Агрессивная"),
    DIVIDEND("Дивидендная"),
    GROWTH("Ростовая")
}

enum class HealthLevel(val displayName: String, val expenseMultiplier: Double) {
    EXCELLENT("Отличное", 0.8),
    GOOD("Хорошее", 1.0),
    FAIR("Удовлетворительное", 1.2),
    POOR("Плохое", 1.5)
}

enum class StressLevel(val displayName: String, val healthImpact: Double) {
    LOW("Низкий", 1.0),
    MODERATE("Умеренный", 1.1),
    HIGH("Высокий", 1.3),
    CRITICAL("Критический", 1.5)
}

enum class WorkLifeBalance(val displayName: String, val stressMultiplier: Double) {
    WORK_FOCUSED("Работа", 1.3),
    BALANCED("Баланс", 1.0),
    LIFE_FOCUSED("Личная жизнь", 0.8)
}

enum class GoalType(val displayName: String) {
    PASSIVE_INCOME("Пассивный доход"),
    NET_WORTH("Чистая стоимость"),
    SAVINGS("Сбережения"),
    RETIREMENT("Пенсия")
}

@Parcelize
data class Skill(
    val name: String,
    val description: String,
    val salaryBonus: Int,
    val category: SkillCategory
) : Parcelable

enum class SkillCategory(val displayName: String) {
    TECHNICAL("Технические"),
    SOFT_SKILLS("Мягкие навыки"),
    LEADERSHIP("Лидерство"),
    FINANCIAL("Финансовые"),
    CREATIVE("Творческие")
}

@Parcelize
data class FinancialGoal(
    val name: String,
    val description: String,
    val type: GoalType,
    val targetAmount: Int,
    val deadline: Int, // В месяцах от начала игры
    val isAchieved: Boolean = false
) : Parcelable