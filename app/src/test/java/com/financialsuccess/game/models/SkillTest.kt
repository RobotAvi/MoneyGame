package com.financialsuccess.game.models

import org.junit.Test
import org.junit.Assert.*

class SkillTest {

    @Test
    fun `test skill creation with default values`() {
        val skill = Skill("Тестирование")
        
        assertEquals("Тестирование", skill.name)
        assertEquals("", skill.description)
        assertEquals(0, skill.salaryBonus)
        assertEquals(SkillCategory.TECHNICAL, skill.category)
        assertEquals(1, skill.level)
    }

    @Test
    fun `test skill creation with all parameters`() {
        val skill = Skill(
            name = "Программирование",
            description = "Навык разработки ПО",
            salaryBonus = 15000,
            category = SkillCategory.TECHNICAL,
            level = 3
        )
        
        assertEquals("Программирование", skill.name)
        assertEquals("Навык разработки ПО", skill.description)
        assertEquals(15000, skill.salaryBonus)
        assertEquals(SkillCategory.TECHNICAL, skill.category)
        assertEquals(3, skill.level)
    }

    @Test
    fun `test skill level modification`() {
        val skill = Skill("Лидерство", category = SkillCategory.LEADERSHIP)
        assertEquals(1, skill.level)
        
        skill.level = 5
        assertEquals(5, skill.level)
    }

    @Test
    fun `test skill categories`() {
        assertEquals("Технические", SkillCategory.TECHNICAL.displayName)
        assertEquals("Мягкие навыки", SkillCategory.SOFT_SKILLS.displayName)
        assertEquals("Лидерство", SkillCategory.LEADERSHIP.displayName)
        assertEquals("Финансовые", SkillCategory.FINANCIAL.displayName)
        assertEquals("Творческие", SkillCategory.CREATIVE.displayName)
    }
}
