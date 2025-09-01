package com.financialsuccess.game.models

import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class PlayerSkillsTest {

    private lateinit var player: Player
    private lateinit var profession: Profession
    private lateinit var dream: Dream

    @Before
    fun setUp() {
        profession = Profession(
            id = "test_prof",
            name = "Тестер",
            salary = 50000,
            expenses = 20000,
            taxes = 5000,
            education = "Высшее",
            description = "Тестовая профессия"
        )
        dream = Dream(
            id = "test_dream",
            name = "Тестовая мечта",
            cost = 100000,
            description = "Тестовое описание",
            cashFlowRequired = 50000
        )
        player = Player(
            profession = profession,
            dream = dream
        )
    }

    @Test
    fun `test addSkill adds new skill`() {
        val skill = Skill("Программирование", category = SkillCategory.TECHNICAL)
        
        assertEquals(0, player.skills.size)
        player.addSkill(skill)
        
        assertEquals(1, player.skills.size)
        assertEquals("Программирование", player.skills[0].name)
        assertEquals(SkillCategory.TECHNICAL, player.skills[0].category)
    }

    @Test
    fun `test addSkill increases level of existing skill`() {
        val skill1 = Skill("Программирование", level = 1, category = SkillCategory.TECHNICAL)
        val skill2 = Skill("Программирование", level = 2, category = SkillCategory.TECHNICAL)
        
        player.addSkill(skill1)
        assertEquals(1, player.skills.size)
        assertEquals(1, player.skills[0].level)
        
        player.addSkill(skill2)
        assertEquals(1, player.skills.size) // Тот же навык
        assertEquals(3, player.skills[0].level) // Уровень увеличился
    }

    @Test
    fun `test addHobby adds new hobby`() {
        val hobby = Hobby("Чтение", cost = 1000, stressReduction = 5)
        
        assertEquals(0, player.hobbies.size)
        player.addHobby(hobby)
        
        assertEquals(1, player.hobbies.size)
        assertEquals("Чтение", player.hobbies[0].name)
        assertEquals(1000, player.hobbies[0].cost)
    }

    @Test
    fun `test addHobby does not add duplicate hobby`() {
        val hobby1 = Hobby("Спорт", cost = 2000, stressReduction = 8)
        val hobby2 = Hobby("Спорт", cost = 3000, stressReduction = 10)
        
        player.addHobby(hobby1)
        assertEquals(1, player.hobbies.size)
        
        player.addHobby(hobby2)
        assertEquals(1, player.hobbies.size) // Дубликат не добавлен
        assertEquals(2000, player.hobbies[0].cost) // Остался первый
    }

    @Test
    fun `test calculateSkillsBonus returns correct sum`() {
        val skill1 = Skill("Программирование", salaryBonus = 10000, category = SkillCategory.TECHNICAL)
        val skill2 = Skill("Лидерство", salaryBonus = 15000, category = SkillCategory.LEADERSHIP)
        
        player.addSkill(skill1)
        player.addSkill(skill2)
        
        assertEquals(25000, player.calculateSkillsBonus())
    }

    @Test
    fun `test calculateSkillsBonus returns 0 for no skills`() {
        assertEquals(0, player.calculateSkillsBonus())
    }
}
