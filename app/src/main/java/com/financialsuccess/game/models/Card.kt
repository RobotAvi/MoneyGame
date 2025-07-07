package com.financialsuccess.game.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

// Базовый класс для всех карточек
@Parcelize
sealed class Card(
    open val id: String,
    open val name: String,
    open val description: String,
    open val type: CardType
) : Parcelable

enum class CardType {
    PROFESSION,
    SMALL_DEAL,
    BIG_DEAL, 
    DOODAD,
    MARKET,
    PAYCHECK,
    DREAM
}

// Карточка профессии
@Parcelize
data class Profession(
    override val id: String,
    override val name: String,
    override val description: String,
    val salary: Int,
    val expenses: Int,
    val taxes: Int,
    val education: String
) : Card(id, name, description, CardType.PROFESSION)

// Карточка мечты
@Parcelize
data class Dream(
    override val id: String,
    override val name: String,
    override val description: String,
    val cost: Int,
    val cashFlowRequired: Int
) : Card(id, name, description, CardType.DREAM)

// Карточка актива
@Parcelize
data class Asset(
    val name: String,
    val type: AssetType,
    val downPayment: Int,
    val value: Int,
    val cashFlow: Int,
    val loan: Int = 0,
    val loanPayment: Int = 0,
    val shares: Int = 1
) : Parcelable

enum class AssetType {
    REAL_ESTATE,
    STOCKS,
    BUSINESS,
    CRYPTO,
    BONDS
}

// Карточка пассива
@Parcelize
data class Liability(
    val name: String,
    val amount: Int,
    val payment: Int
) : Parcelable

// Карточка инвестиции
@Parcelize
data class Investment(
    val name: String,
    val type: AssetType,
    val cost: Int,
    val expectedReturn: Int,
    val riskLevel: RiskLevel
) : Parcelable

enum class RiskLevel {
    LOW, MEDIUM, HIGH
}