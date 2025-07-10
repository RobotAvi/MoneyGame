package com.financialsuccess.game.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class FinancialEntry(
    val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val type: FinancialEntryType,
    val category: FinancialCategory,
    val amount: Int, // Положительное для доходов, отрицательное для расходов
    val description: String,
    val playerAge: Int,
    val monthNumber: Int, // Месяц игры
    val balanceAfter: Int, // Баланс после операции
    val realDate: String // Игровая дата в формате "число месяц год"
) : Parcelable

@Parcelize
enum class FinancialEntryType : Parcelable {
    INCOME,    // Доходы
    EXPENSE    // Расходы
}

@Parcelize
enum class FinancialCategory : Parcelable {
    // ДОХОДЫ
    SALARY,              // Зарплата
    PASSIVE_INCOME,      // Пассивный доход
    ASSET_INCOME,        // Доход от активов
    INVESTMENT_RETURN,   // Доходность инвестиций
    BONUS,               // Бонусы
    INHERITANCE,         // Наследство
    ASSET_SALE,          // Продажа активов
    
    // РАСХОДЫ
    FOOD,                // Еда
    TRANSPORT,           // Транспорт
    HOUSING,             // Жилье
    CHILDREN,            // Дети
    TAXES,               // Налоги
    OTHER_EXPENSES,      // Прочие расходы
    LOAN_PAYMENT,        // Выплаты по кредитам
    ASSET_PURCHASE,      // Покупка активов
    INVESTMENT,          // Инвестиции
    SAVINGS,             // Сбережения
    EMERGENCY,           // Непредвиденные расходы
    CHARITY,             // Благотворительность
    
    // СПЕЦИАЛЬНЫЕ
    GAME_START,          // Начальный капитал
    FAST_TRACK_BONUS,    // Бонусы скоростной дорожки
    DREAM_PURCHASE       // Покупка мечты
}

// Расширения для удобства
fun FinancialCategory.getIcon(): String = when (this) {
    // Доходы
    FinancialCategory.SALARY -> "💼"
    FinancialCategory.PASSIVE_INCOME -> "📈"
    FinancialCategory.ASSET_INCOME -> "🏠"
    FinancialCategory.INVESTMENT_RETURN -> "💎"
    FinancialCategory.BONUS -> "🎁"
    FinancialCategory.INHERITANCE -> "👴"
    FinancialCategory.ASSET_SALE -> "🏷️"
    
    // Расходы
    FinancialCategory.FOOD -> "🍽️"
    FinancialCategory.TRANSPORT -> "🚗"
    FinancialCategory.HOUSING -> "🏘️"
    FinancialCategory.CHILDREN -> "👶"
    FinancialCategory.TAXES -> "🏛️"
    FinancialCategory.OTHER_EXPENSES -> "🛍️"
    FinancialCategory.LOAN_PAYMENT -> "🏦"
    FinancialCategory.ASSET_PURCHASE -> "🏠"
    FinancialCategory.INVESTMENT -> "📊"
    FinancialCategory.SAVINGS -> "💰"
    FinancialCategory.EMERGENCY -> "⚠️"
    FinancialCategory.CHARITY -> "❤️"
    
    // Специальные
    FinancialCategory.GAME_START -> "🎮"
    FinancialCategory.FAST_TRACK_BONUS -> "🚀"
    FinancialCategory.DREAM_PURCHASE -> "🎯"
}

fun FinancialCategory.getDisplayName(): String = when (this) {
    // Доходы
    FinancialCategory.SALARY -> "Зарплата"
    FinancialCategory.PASSIVE_INCOME -> "Пассивный доход"
    FinancialCategory.ASSET_INCOME -> "Доход от активов"
    FinancialCategory.INVESTMENT_RETURN -> "Доходность инвестиций"
    FinancialCategory.BONUS -> "Бонус"
    FinancialCategory.INHERITANCE -> "Наследство"
    FinancialCategory.ASSET_SALE -> "Продажа активов"
    
    // Расходы
    FinancialCategory.FOOD -> "Еда"
    FinancialCategory.TRANSPORT -> "Транспорт"
    FinancialCategory.HOUSING -> "Жилье"
    FinancialCategory.CHILDREN -> "Дети"
    FinancialCategory.TAXES -> "Налоги"
    FinancialCategory.OTHER_EXPENSES -> "Прочие расходы"
    FinancialCategory.LOAN_PAYMENT -> "Кредиты"
    FinancialCategory.ASSET_PURCHASE -> "Покупка активов"
    FinancialCategory.INVESTMENT -> "Инвестиции"
    FinancialCategory.SAVINGS -> "Сбережения"
    FinancialCategory.EMERGENCY -> "Непредвиденные расходы"
    FinancialCategory.CHARITY -> "Благотворительность"
    
    // Специальные
    FinancialCategory.GAME_START -> "Начальный капитал"
    FinancialCategory.FAST_TRACK_BONUS -> "Бонус скоростной дорожки"
    FinancialCategory.DREAM_PURCHASE -> "Покупка мечты"
}