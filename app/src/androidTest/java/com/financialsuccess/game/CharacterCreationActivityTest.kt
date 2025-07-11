package com.financialsuccess.game

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterCreationActivityTest {

    @Test
    fun testActivityLaunch() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что заголовок отображается
        onView(withText("Создание персонажа"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testBasicFieldsAreDisplayed() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем основные поля
        onView(withId(R.id.etPlayerName))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etAge))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etWorkExperience))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etChildrenCount))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etSavingsRate))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSpinnersAreDisplayed() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем спиннеры
        onView(withId(R.id.spinnerEducation))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spinnerMaritalStatus))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spinnerRiskTolerance))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spinnerInvestmentStrategy))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spinnerHealthLevel))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spinnerStressLevel))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spinnerWorkLifeBalance))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testRecyclerViewsAreDisplayed() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем RecyclerView для профессий
        onView(withId(R.id.recyclerViewProfessions))
            .check(matches(isDisplayed()))
        
        // Проверяем RecyclerView для навыков
        onView(withId(R.id.recyclerViewSkills))
            .check(matches(isDisplayed()))
        
        // Проверяем RecyclerView для мечт
        onView(withId(R.id.recyclerViewDreams))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testStartButtonInitiallyDisabled() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Кнопка должна быть изначально отключена
        onView(withId(R.id.btnStartGame))
            .check(matches(not(isEnabled())))
    }

    @Test
    fun testValidationWithEmptyName() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Очищаем поле имени
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
        
        // Заполняем остальные обязательные поля
        onView(withId(R.id.etAge))
            .perform(typeText("25"), closeSoftKeyboard())
        
        onView(withId(R.id.etWorkExperience))
            .perform(typeText("2"), closeSoftKeyboard())
        
        onView(withId(R.id.etChildrenCount))
            .perform(typeText("0"), closeSoftKeyboard())
        
        onView(withId(R.id.etSavingsRate))
            .perform(typeText("10"), closeSoftKeyboard())
        
        onView(withId(R.id.etRetirementAge))
            .perform(typeText("65"), closeSoftKeyboard())
        
        onView(withId(R.id.etTargetPassiveIncome))
            .perform(typeText("100000"), closeSoftKeyboard())
        
        // Пытаемся нажать кнопку старта
        onView(withId(R.id.btnStartGame))
            .perform(click())
        
        // Должна появиться ошибка валидации
        onView(withId(R.id.etPlayerName))
            .check(matches(hasErrorText("Введите имя персонажа")))
    }

    @Test
    fun testValidationWithInvalidAge() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим недопустимый возраст
        onView(withId(R.id.etAge))
            .perform(typeText("15"), closeSoftKeyboard())
        
        // Пытаемся нажать кнопку старта
        onView(withId(R.id.btnStartGame))
            .perform(click())
        
        // Должна появиться ошибка валидации
        onView(withId(R.id.etAge))
            .check(matches(hasErrorText("Возраст должен быть от 18 до 65 лет")))
    }

    @Test
    fun testValidationWithInvalidSavingsRate() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим недопустимый процент сбережений
        onView(withId(R.id.etSavingsRate))
            .perform(typeText("60"), closeSoftKeyboard())
        
        // Пытаемся нажать кнопку старта
        onView(withId(R.id.btnStartGame))
            .perform(click())
        
        // Должна появиться ошибка валидации
        onView(withId(R.id.etSavingsRate))
            .check(matches(hasErrorText("Процент сбережений должен быть от 0 до 50")))
    }

    @Test
    fun testAddGoalButtonIsDisplayed() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем кнопку добавления цели
        onView(withId(R.id.btnAddGoal))
            .check(matches(isDisplayed()))
        
        onView(withText("Добавить цель"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAddGoalButtonWorks() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что RecyclerView целей изначально скрыт
        onView(withId(R.id.recyclerViewGoals))
            .check(matches(not(isDisplayed())))
        
        // Нажимаем кнопку добавления цели
        onView(withId(R.id.btnAddGoal))
            .perform(click())
        
        // Проверяем, что кнопка показывает обратную связь
        onView(withText("Цель добавлена! ✅"))
            .check(matches(isDisplayed()))
        
        // Проверяем, что RecyclerView целей стал видимым
        onView(withId(R.id.recyclerViewGoals))
            .check(matches(isDisplayed()))
        
        // Проверяем, что цель отображается в списке
        onView(withText("Пассивный доход 100000₽"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDatePickerFieldIsDisplayed() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем поле выбора даты
        onView(withId(R.id.etStartDate))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etStartDate))
            .check(matches(withHint("Выберите дату старта")))
    }

    @Test
    fun testSectionHeadersAreDisplayed() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем заголовки разделов
        onView(withText("👤 Основная информация"))
            .check(matches(isDisplayed()))
        
        onView(withText("💼 Выберите профессию"))
            .check(matches(isDisplayed()))
        
        onView(withText("🎯 Выберите навыки (до 3)"))
            .check(matches(isDisplayed()))
        
        onView(withText("👨‍👩‍👧‍👦 Семейное положение"))
            .check(matches(isDisplayed()))
        
        onView(withText("💰 Финансовые предпочтения"))
            .check(matches(isDisplayed()))
        
        onView(withText("🎯 Жизненные цели"))
            .check(matches(isDisplayed()))
        
        onView(withText("🏥 Здоровье и образ жизни"))
            .check(matches(isDisplayed()))
        
        onView(withText("🌟 Выберите мечту"))
            .check(matches(isDisplayed()))
        
        onView(withText("📅 Дата начала игры"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testFieldLabelsAreDisplayed() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем подписи полей
        onView(withText("Образование"))
            .check(matches(isDisplayed()))
        
        onView(withText("Статус"))
            .check(matches(isDisplayed()))
        
        onView(withText("Толерантность к риску"))
            .check(matches(isDisplayed()))
        
        onView(withText("Стратегия инвестиций"))
            .check(matches(isDisplayed()))
        
        onView(withText("Уровень здоровья"))
            .check(matches(isDisplayed()))
        
        onView(withText("Уровень стресса"))
            .check(matches(isDisplayed()))
        
        onView(withText("Баланс работы и жизни"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testDefaultValuesAreSet() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем значения по умолчанию
        onView(withId(R.id.etPlayerName))
            .check(matches(withText("Игрок")))
        
        onView(withId(R.id.etAge))
            .check(matches(withText("25")))
        
        onView(withId(R.id.etWorkExperience))
            .check(matches(withText("0")))
        
        onView(withId(R.id.etChildrenCount))
            .check(matches(withText("0")))
        
        onView(withId(R.id.etSavingsRate))
            .check(matches(withText("10")))
        
        onView(withId(R.id.etRetirementAge))
            .check(matches(withText("65")))
        
        onView(withId(R.id.etTargetPassiveIncome))
            .check(matches(withText("100000")))
    }
}