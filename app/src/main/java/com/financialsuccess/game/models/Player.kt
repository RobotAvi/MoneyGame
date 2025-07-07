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
    val profession: Profession? = null,
    val dream: Dream? = null,
    val assets: MutableList<Asset> = mutableListOf(),
    val liabilities: MutableList<Liability> = mutableListOf(),
    val investments: MutableList<Investment> = mutableListOf()
) : Parcelable {
    
    fun getNetWorth(): Int = 
        assets.sumOf { it.value } - liabilities.sumOf { it.amount }
    
    fun getCashFlow(): Int = 
        totalIncome + passiveIncome - totalExpenses
    
    fun canEscapeRatRace(): Boolean = 
        passiveIncome > totalExpenses
    
    fun updateTotalIncome() {
        totalIncome = salary + assets.sumOf { it.cashFlow }
    }
    
    fun updateTotalExpenses() {
        totalExpenses = (profession?.expenses ?: 0) + (profession?.taxes ?: 0) + liabilities.sumOf { it.payment }
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
}