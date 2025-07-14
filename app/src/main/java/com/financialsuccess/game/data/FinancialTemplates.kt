package com.financialsuccess.game.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class FinancialTemplate(
    val name: String,
    val salary: Int,
    val additionalIncome: Int,
    val foodExpenses: Int,
    val transportExpenses: Int,
    val housingExpenses: Int,
    val otherExpenses: Int,
    val description: String = ""
)

object FinancialTemplates {
    private const val PREFS_NAME = "financial_templates"
    private const val KEY_TEMPLATES = "templates"
    
    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveTemplate(context: Context, template: FinancialTemplate) {
        val prefs = getSharedPreferences(context)
        val templates = getTemplates(context).toMutableList()
        
        // Проверяем, есть ли уже шаблон с таким именем
        val existingIndex = templates.indexOfFirst { it.name == template.name }
        if (existingIndex != -1) {
            templates[existingIndex] = template
        } else {
            templates.add(template)
        }
        
        val gson = Gson()
        val json = gson.toJson(templates)
        prefs.edit().putString(KEY_TEMPLATES, json).apply()
    }
    
    fun getTemplates(context: Context): List<FinancialTemplate> {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(KEY_TEMPLATES, "[]")
        val gson = Gson()
        val type = object : TypeToken<List<FinancialTemplate>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }
    
    fun deleteTemplate(context: Context, templateName: String) {
        val prefs = getSharedPreferences(context)
        val templates = getTemplates(context).toMutableList()
        templates.removeAll { it.name == templateName }
        
        val gson = Gson()
        val json = gson.toJson(templates)
        prefs.edit().putString(KEY_TEMPLATES, json).apply()
    }
    
    fun getDefaultTemplates(): List<FinancialTemplate> {
        return listOf(
            FinancialTemplate(
                name = "Студент",
                salary = 30000,
                additionalIncome = 5000,
                foodExpenses = 12000,
                transportExpenses = 3000,
                housingExpenses = 15000,
                otherExpenses = 5000,
                description = "Финансовый профиль типичного студента"
            ),
            FinancialTemplate(
                name = "Офисный работник",
                salary = 60000,
                additionalIncome = 0,
                foodExpenses = 15000,
                transportExpenses = 8000,
                housingExpenses = 25000,
                otherExpenses = 12000,
                description = "Средний офисный работник"
            ),
            FinancialTemplate(
                name = "Предприниматель",
                salary = 0,
                additionalIncome = 150000,
                foodExpenses = 20000,
                transportExpenses = 15000,
                housingExpenses = 40000,
                otherExpenses = 25000,
                description = "Малый бизнес"
            ),
            FinancialTemplate(
                name = "IT-специалист",
                salary = 120000,
                additionalIncome = 30000,
                foodExpenses = 18000,
                transportExpenses = 10000,
                housingExpenses = 35000,
                otherExpenses = 20000,
                description = "Высокооплачиваемый IT-специалист"
            )
        )
    }
}