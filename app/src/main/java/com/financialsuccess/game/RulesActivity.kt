package com.financialsuccess.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.OnBackPressedCallback
import com.financialsuccess.game.databinding.ActivityRulesBinding

/**
 * Активность для отображения правил игры
 * 
 * Функциональность:
 * - Отображение подробных правил игры в прокручиваемом формате
 * - Красивый дизайн с океанским фоном
 * - Современная обработка кнопки "Назад"
 * - Адаптивный layout для разных размеров экрана
 * 
 * Контент:
 * - Цель игры и основные принципы
 * - Типы активов и их характеристики
 * - Советы для успешной игры
 * - Финансовые стратегии
 * 
 * Навигация:
 * - Кнопка "Назад" внизу экрана
 * - Системная кнопка "Назад" (жест)
 * - Автоматическое возвращение в MainActivity
 */
class RulesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRulesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRulesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Настройка кнопки "Назад"
        binding.backButton.setOnClickListener {
            finish()
        }
        
        // Современная обработка кнопки "Назад"
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }
}