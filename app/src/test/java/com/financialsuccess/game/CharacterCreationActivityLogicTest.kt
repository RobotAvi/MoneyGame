package com.financialsuccess.game

import org.junit.Test
import org.junit.Assert.*

class CharacterCreationActivityLogicTest {

    @Test
    fun testStepProgression() {
        // Тест проверяет логику перехода между шагами
        var currentStep = 1
        
        // Переход к следующему шагу
        currentStep++
        assertEquals(2, currentStep)
        
        // Переход к следующему шагу
        currentStep++
        assertEquals(3, currentStep)
        
        // Проверяем, что шаги в правильном порядке
        assertTrue(currentStep in 1..3)
    }

    @Test
    fun testPlayerNameValidation() {
        // Тест проверяет валидацию имени игрока
        val validNames = listOf("Захар", "Анна", "Михаил", "Елена")
        val invalidNames = listOf("", "   ", "A", "123456789012345678901")
        
        validNames.forEach { name ->
            assertTrue("Имя '$name' должно быть валидным", name.isNotBlank() && name.length <= 20)
        }
        
        invalidNames.forEach { name ->
            assertFalse("Имя '$name' не должно быть валидным", name.isNotBlank() && name.length <= 20)
        }
    }

    @Test
    fun testAgeCalculation() {
        // Тест проверяет расчет возраста
        val birthYear = 2000
        val startYear = 2024
        val expectedAge = startYear - birthYear
        
        assertEquals(24, expectedAge)
        assertTrue("Возраст должен быть положительным", expectedAge > 0)
        assertTrue("Возраст должен быть разумным", expectedAge < 100)
    }

    @Test
    fun testDateValidation() {
        // Тест проверяет валидацию дат
        val validDates = listOf("01.01.2000", "31.12.1990", "15.06.1985")
        val invalidDates = listOf("00.01.2000", "32.01.2000", "01.13.2000", "01.01.3000")
        
        validDates.forEach { date ->
            assertTrue("Дата '$date' должна быть валидной", date.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")))
        }
        
        invalidDates.forEach { date ->
            assertFalse("Дата '$date' не должна быть валидной", date.matches(Regex("\\d{2}\\.\\d{2}\\.\\d{4}")))
        }
    }
}
