package com.financialsuccess.game.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameState(
    val player: Player,
    val currentTurn: Int = 1,
    val diceValue: Int = 0,
    val gamePhase: GamePhase = GamePhase.RAT_RACE,
    val lastEvent: String = "",
    val availableCards: List<Card> = emptyList(),
    val gameStartTime: Long = System.currentTimeMillis()
) : Parcelable

enum class GamePhase {
    RAT_RACE,
    FAST_TRACK,
    COMPLETED
}

@Parcelize
data class FinancialStatement(
    val income: IncomeStatement,
    val expenses: ExpenseStatement,
    val assets: AssetStatement,
    val liabilities: LiabilityStatement
) : Parcelable {
    
    fun getCashFlow(): Int = 
        income.getTotalIncome() - expenses.getTotalExpenses()
}

@Parcelize
data class IncomeStatement(
    val salary: Int = 0,
    val realEstate: Int = 0,
    val dividends: Int = 0,
    val business: Int = 0,
    val other: Int = 0
) : Parcelable {
    
    fun getTotalIncome(): Int = salary + realEstate + dividends + business + other
    fun getPassiveIncome(): Int = realEstate + dividends + business + other
}

@Parcelize
data class ExpenseStatement(
    val taxes: Int = 0,
    val mortgage: Int = 0,
    val schoolLoan: Int = 0,
    val carLoan: Int = 0,
    val creditCard: Int = 0,
    val other: Int = 0,
    val perChildExpense: Int = 0,
    val childCount: Int = 0
) : Parcelable {
    
    fun getTotalExpenses(): Int = 
        taxes + mortgage + schoolLoan + carLoan + creditCard + other + (perChildExpense * childCount)
}

@Parcelize
data class AssetStatement(
    val realEstate: MutableList<RealEstateAsset> = mutableListOf(),
    val stocks: MutableList<StockAsset> = mutableListOf(),
    val business: MutableList<BusinessAsset> = mutableListOf(),
    val savings: Int = 0
) : Parcelable {
    
    fun getTotalValue(): Int = 
        realEstate.sumOf { it.value } + 
        stocks.sumOf { it.value } + 
        business.sumOf { it.value } + 
        savings
}

@Parcelize
data class LiabilityStatement(
    val mortgage: Int = 0,
    val schoolLoan: Int = 0,
    val carLoan: Int = 0,
    val creditCard: Int = 0,
    val other: Int = 0
) : Parcelable {
    
    fun getTotalLiabilities(): Int = 
        mortgage + schoolLoan + carLoan + creditCard + other
}

@Parcelize
data class RealEstateAsset(
    val name: String,
    val downPayment: Int,
    val value: Int,
    val mortgage: Int,
    val cashFlow: Int
) : Parcelable

@Parcelize
data class StockAsset(
    val symbol: String,
    val shares: Int,
    val price: Int,
    val dividend: Int
) : Parcelable {
    val value: Int get() = shares * price
}

@Parcelize
data class BusinessAsset(
    val name: String,
    val downPayment: Int,
    val value: Int,
    val cashFlow: Int
) : Parcelable