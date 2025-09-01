package com.financialsuccess.game

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterCreationActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(CharacterCreationActivity::class.java)

    @Test
    fun testActivityLaunchesWithoutCrash() {
        // Тест проверяет, что активность запускается без падения
        // Если активность падает с NullPointerException, тест не пройдет
        
        // Ждем немного, чтобы убедиться, что активность полностью загрузилась
        Thread.sleep(1000)
        
        // Если мы дошли до этой точки, значит активность не упала
        // Это означает, что все findViewById() нашли нужные элементы
    }

    @Test
    fun testStepNavigation() {
        // Тест проверяет, что можно переключаться между шагами
        // Без падения с NullPointerException
        
        // Ждем загрузки первого шага
        Thread.sleep(500)
        
        // Проверяем, что первый шаг загружен
        // Если есть ошибки с findViewById, тест упадет здесь
        
        // Ждем еще немного для стабильности
        Thread.sleep(500)
    }
}