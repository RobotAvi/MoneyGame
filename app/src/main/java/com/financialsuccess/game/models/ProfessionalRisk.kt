package com.financialsuccess.game.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class ProfessionalRisk(
    val name: String,
    val description: String,
    val probability: Double, // Вероятность в процентах (0.0 - 1.0)
    val ageRange: @RawValue IntRange, // Возрастной диапазон риска
    val effects: List<RiskEffect>,
    val icon: String
) : Parcelable

@Parcelize
data class RiskEffect(
    val type: RiskEffectType,
    val severity: RiskSeverity,
    val salaryReduction: Int = 0, // Уменьшение зарплаты
    val expenseIncrease: Int = 0, // Увеличение расходов (лечение)
    val careerEnd: Boolean = false, // Завершение карьеры досрочно
    val recoveryTime: Int = 0, // Время восстановления в месяцах
    val description: String
) : Parcelable

@Parcelize
enum class RiskEffectType : Parcelable {
    VISION_PROBLEMS,      // Проблемы со зрением
    MENTAL_HEALTH,        // Психическое здоровье
    PHYSICAL_INJURY,      // Физические травмы
    COGNITIVE_DECLINE,    // Когнитивные нарушения
    RESPIRATORY_DISEASE,  // Респираторные заболевания
    HEARING_LOSS,         // Потеря слуха
    STRESS_DISORDER,      // Стрессовые расстройства
    BACK_PROBLEMS,        // Проблемы со спиной
    ADDICTION,            // Зависимости
    BURNOUT               // Профессиональное выгорание
}

@Parcelize
enum class RiskSeverity : Parcelable {
    MILD,     // Легкая степень
    MODERATE, // Средняя степень  
    SEVERE    // Тяжелая степень
}

// Профессиональные риски для каждой профессии
object ProfessionalRisks {
    
    fun getRisksForProfession(professionName: String): List<ProfessionalRisk> {
        return when (professionName) {
            "Программист" -> getProgrammerRisks()
            "Врач" -> getDoctorRisks()
            "Учитель" -> getTeacherRisks()
            "Инженер" -> getEngineerRisks()
            "Библиотекарь" -> getLibrarianRisks()
            "Строитель" -> getBuilderRisks()
            else -> emptyList()
        }
    }
    
    private fun getProgrammerRisks(): List<ProfessionalRisk> = listOf(
        ProfessionalRisk(
            name = "Компьютерное зрение",
            description = "Синдром сухого глаза и близорукость от длительной работы за компьютером",
            probability = 0.15, // 15% вероятность
            ageRange = 30..50,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.VISION_PROBLEMS,
                    severity = RiskSeverity.MODERATE,
                    expenseIncrease = 5000,
                    description = "Очки, капли для глаз, регулярные визиты к офтальмологу"
                )
            ),
            icon = "👓"
        ),
        ProfessionalRisk(
            name = "Туннельный синдром",
            description = "Карпальный туннельный синдром от постоянной работы с клавиатурой",
            probability = 0.12,
            ageRange = 35..55,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.PHYSICAL_INJURY,
                    severity = RiskSeverity.MODERATE,
                    salaryReduction = 8000,
                    expenseIncrease = 3000,
                    recoveryTime = 6,
                    description = "Физиотерапия, эргономичное оборудование, снижение производительности"
                )
            ),
            icon = "🖱️"
        ),
        ProfessionalRisk(
            name = "Профессиональное выгорание",
            description = "Эмоциональное истощение от постоянных дедлайнов и стресса",
            probability = 0.20,
            ageRange = 25..45,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.BURNOUT,
                    severity = RiskSeverity.SEVERE,
                    salaryReduction = 15000,
                    expenseIncrease = 8000,
                    recoveryTime = 12,
                    description = "Психотерапия, антидепрессанты, длительный отпуск"
                )
            ),
            icon = "🔥"
        )
    )
    
    private fun getDoctorRisks(): List<ProfessionalRisk> = listOf(
        ProfessionalRisk(
            name = "Профессиональный стресс",
            description = "Постоянное психологическое напряжение от ответственности за жизни",
            probability = 0.25,
            ageRange = 30..60,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.STRESS_DISORDER,
                    severity = RiskSeverity.SEVERE,
                    expenseIncrease = 10000,
                    description = "Психотерапия, медикаменты, реабилитация"
                )
            ),
            icon = "💊"
        ),
        ProfessionalRisk(
            name = "Инфекционные заболевания",
            description = "Риск заражения от пациентов",
            probability = 0.08,
            ageRange = 25..65,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.RESPIRATORY_DISEASE,
                    severity = RiskSeverity.MODERATE,
                    expenseIncrease = 15000,
                    recoveryTime = 3,
                    description = "Лечение, изоляция, восстановление иммунитета"
                )
            ),
            icon = "🦠"
        ),
        ProfessionalRisk(
            name = "Судебные иски",
            description = "Врачебные ошибки и связанные с ними проблемы",
            probability = 0.05,
            ageRange = 35..55,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.STRESS_DISORDER,
                    severity = RiskSeverity.SEVERE,
                    salaryReduction = 25000,
                    expenseIncrease = 50000,
                    description = "Юридические расходы, страховка, репутационные потери"
                )
            ),
            icon = "⚖️"
        )
    )
    
    private fun getTeacherRisks(): List<ProfessionalRisk> = listOf(
        ProfessionalRisk(
            name = "Эмоциональное выгорание",
            description = "Истощение от работы с трудными учениками и родителями",
            probability = 0.18,
            ageRange = 35..55,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.BURNOUT,
                    severity = RiskSeverity.MODERATE,
                    salaryReduction = 5000,
                    expenseIncrease = 6000,
                    recoveryTime = 6,
                    description = "Психологическая помощь, смена методики работы"
                )
            ),
            icon = "😰"
        ),
        ProfessionalRisk(
            name = "Проблемы с голосом",
            description = "Хронические заболевания голосовых связок",
            probability = 0.12,
            ageRange = 40..60,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.PHYSICAL_INJURY,
                    severity = RiskSeverity.MILD,
                    expenseIncrease = 4000,
                    description = "Логопед, лечение голосовых связок"
                )
            ),
            icon = "🗣️"
        ),
        ProfessionalRisk(
            name = "Инфекционные заболевания",
            description = "Частые ОРВИ и грипп от контакта с детьми",
            probability = 0.15,
            ageRange = 25..65,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.RESPIRATORY_DISEASE,
                    severity = RiskSeverity.MILD,
                    expenseIncrease = 3000,
                    description = "Частые болезни, укрепление иммунитета"
                )
            ),
            icon = "🤧"
        )
    )
    
    private fun getEngineerRisks(): List<ProfessionalRisk> = listOf(
        ProfessionalRisk(
            name = "Производственная травма",
            description = "Травмы на производстве или стройплощадке",
            probability = 0.10,
            ageRange = 25..55,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.PHYSICAL_INJURY,
                    severity = RiskSeverity.SEVERE,
                    salaryReduction = 20000,
                    expenseIncrease = 25000,
                    recoveryTime = 9,
                    description = "Реабилитация, инвалидность, смена специализации"
                )
            ),
            icon = "⚠️"
        ),
        ProfessionalRisk(
            name = "Проблемы со спиной",
            description = "Остеохондроз от сидячей работы и поднятия тяжестей",
            probability = 0.20,
            ageRange = 35..60,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.BACK_PROBLEMS,
                    severity = RiskSeverity.MODERATE,
                    salaryReduction = 5000,
                    expenseIncrease = 8000,
                    description = "Массаж, физиотерапия, эргономичное рабочее место"
                )
            ),
            icon = "🦴"
        )
    )
    
    private fun getLibrarianRisks(): List<ProfessionalRisk> = listOf(
        ProfessionalRisk(
            name = "Когнитивная деградация",
            description = "Снижение умственных способностей от монотонной работы",
            probability = 0.08,
            ageRange = 50..65,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.COGNITIVE_DECLINE,
                    severity = RiskSeverity.MODERATE,
                    salaryReduction = 12000,
                    careerEnd = true,
                    description = "Ранняя деменция, необходимость смены профессии"
                )
            ),
            icon = "🧠"
        ),
        ProfessionalRisk(
            name = "Аллергия на пыль",
            description = "Хронические респираторные проблемы от книжной пыли",
            probability = 0.15,
            ageRange = 30..60,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.RESPIRATORY_DISEASE,
                    severity = RiskSeverity.MILD,
                    expenseIncrease = 4000,
                    description = "Антигистаминные, очистители воздуха"
                )
            ),
            icon = "🤧"
        ),
        ProfessionalRisk(
            name = "Социальная изоляция",
            description = "Депрессия от недостатка социального взаимодействия",
            probability = 0.12,
            ageRange = 40..65,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.MENTAL_HEALTH,
                    severity = RiskSeverity.MODERATE,
                    expenseIncrease = 7000,
                    description = "Психотерапия, антидепрессанты"
                )
            ),
            icon = "😔"
        )
    )
    
    private fun getBuilderRisks(): List<ProfessionalRisk> = listOf(
        ProfessionalRisk(
            name = "Травма на стройке",
            description = "Серьезные производственные травмы",
            probability = 0.25,
            ageRange = 25..55,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.PHYSICAL_INJURY,
                    severity = RiskSeverity.SEVERE,
                    salaryReduction = 30000,
                    expenseIncrease = 40000,
                    careerEnd = true,
                    description = "Инвалидность, невозможность продолжать работу"
                )
            ),
            icon = "🚨"
        ),
        ProfessionalRisk(
            name = "Заболевания легких",
            description = "Пневмокониоз от строительной пыли",
            probability = 0.18,
            ageRange = 40..65,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.RESPIRATORY_DISEASE,
                    severity = RiskSeverity.SEVERE,
                    salaryReduction = 15000,
                    expenseIncrease = 20000,
                    description = "Хронические заболевания легких, дорогостоящее лечение"
                )
            ),
            icon = "🫁"
        ),
        ProfessionalRisk(
            name = "Потеря слуха",
            description = "Тугоухость от постоянного шума",
            probability = 0.20,
            ageRange = 35..60,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.HEARING_LOSS,
                    severity = RiskSeverity.MODERATE,
                    salaryReduction = 8000,
                    expenseIncrease = 12000,
                    description = "Слуховые аппараты, снижение трудоспособности"
                )
            ),
            icon = "🔇"
        ),
        ProfessionalRisk(
            name = "Алкоголизм",
            description = "Зависимость как способ справиться со стрессом",
            probability = 0.15,
            ageRange = 30..55,
            effects = listOf(
                RiskEffect(
                    type = RiskEffectType.ADDICTION,
                    severity = RiskSeverity.SEVERE,
                    salaryReduction = 20000,
                    expenseIncrease = 15000,
                    recoveryTime = 18,
                    description = "Лечение зависимости, реабилитация, потеря работы"
                )
            ),
            icon = "🍺"
        )
    )
}