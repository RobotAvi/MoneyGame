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
    val investments: MutableList<Investment> = mutableListOf()
) : Parcelable {
    
    fun getNetWorth(): Int = 
        assets.sumOf { it.value } - liabilities.sumOf { it.amount }
    
    fun getCashFlow(): Int = 
        totalIncome + passiveIncome - totalExpenses
    
    fun canEscapeRatRace(): Boolean = 
        passiveIncome > totalExpenses
    
    fun updateTotalIncome() {
        totalIncome = salary
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
        
        // Общие расходы = базовые + кредиты + дети
        totalExpenses = foodExpenses + transportExpenses + housingExpenses + 
                       childrenExpenses + taxes + otherExpenses + 
                       liabilities.sumOf { it.payment }
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
}