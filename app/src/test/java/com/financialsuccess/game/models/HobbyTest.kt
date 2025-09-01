package com.financialsuccess.game.models

import org.junit.Test
import org.junit.Assert.*

class HobbyTest {

    @Test
    fun `test hobby creation with default values`() {
        val hobby = Hobby("Чтение")
        
        assertEquals("Чтение", hobby.name)
        assertEquals(0, hobby.cost)
        assertEquals(0, hobby.stressReduction)
        assertEquals("Общее", hobby.category)
    }

    @Test
    fun `test hobby creation with all parameters`() {
        val hobby = Hobby(
            name = "Спорт",
            cost = 5000,
            stressReduction = 10,
            category = "Активный отдых"
        )
        
        assertEquals("Спорт", hobby.name)
        assertEquals(5000, hobby.cost)
        assertEquals(10, hobby.stressReduction)
        assertEquals("Активный отдых", hobby.category)
    }

    @Test
    fun `test hobby cost calculation`() {
        val expensiveHobby = Hobby("Яхтинг", cost = 50000)
        val cheapHobby = Hobby("Рисование", cost = 1000)
        
        assertTrue(expensiveHobby.cost > cheapHobby.cost)
        assertEquals(50000, expensiveHobby.cost)
        assertEquals(1000, cheapHobby.cost)
    }

    @Test
    fun `test hobby stress reduction`() {
        val relaxingHobby = Hobby("Медитация", stressReduction = 15)
        val activeHobby = Hobby("Бег", stressReduction = 8)
        
        assertTrue(relaxingHobby.stressReduction > activeHobby.stressReduction)
        assertEquals(15, relaxingHobby.stressReduction)
        assertEquals(8, activeHobby.stressReduction)
    }
}
