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
class CharacterCreationStepTest {

    @Test
    fun testStep1_ProfessionDisplayedAndSwipe() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        onView(withText("ШАГ 1/5")).check(matches(isDisplayed()))
        onView(withId(R.id.btnSwipeRight)).perform(click())
        // Проверить, что карточка профессии изменилась (например, по имени)
    }

    @Test
    fun testStep2_DreamDisplayedAndSwipe() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        // Перейти на шаг 2
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withText("ШАГ 2/5")).check(matches(isDisplayed()))
        onView(withId(R.id.btnSwipeLeft)).perform(click())
        // Проверить, что карточка мечты изменилась
    }

    @Test
    fun testStep3_AgePicker() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        // Перейти на шаг 3
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withId(R.id.btnChooseDream)).perform(click())
        onView(withText("ШАГ 3/5")).check(matches(isDisplayed()))
        onView(withId(R.id.numberPickerAge)).check(matches(isDisplayed()))
        onView(withId(R.id.btnNextAge)).perform(click())
    }

    @Test
    fun testStep4_NameInputAndVoice() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        // Перейти на шаг 4
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withId(R.id.btnChooseDream)).perform(click())
        onView(withId(R.id.btnNextAge)).perform(click())
        onView(withText("ШАГ 4/5")).check(matches(isDisplayed()))
        onView(withId(R.id.etPlayerName)).perform(typeText("Тест"), closeSoftKeyboard())
        onView(withId(R.id.btnNextName)).perform(click())
    }

    @Test
    fun testStep5_DatePicker() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        // Перейти на шаг 5
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withId(R.id.btnChooseDream)).perform(click())
        onView(withId(R.id.btnNextAge)).perform(click())
        onView(withId(R.id.btnNextName)).perform(click())
        onView(withText("ШАГ 5/5")).check(matches(isDisplayed()))
        onView(withId(R.id.btnPickDate)).perform(click())
        // Здесь можно добавить проверку появления диалога выбора даты
        onView(withId(R.id.btnNextDate)).perform(click())
    }

    @Test
    fun testFinalScreen_StartGameAppears() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        // Пройти все шаги
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withId(R.id.btnChooseDream)).perform(click())
        onView(withId(R.id.btnNextAge)).perform(click())
        onView(withId(R.id.btnNextName)).perform(click())
        onView(withId(R.id.btnNextDate)).perform(click())
        onView(withText("Начинаем!")).check(matches(isDisplayed()))
    }
}