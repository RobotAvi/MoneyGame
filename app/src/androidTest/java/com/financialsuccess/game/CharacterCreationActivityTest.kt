package com.financialsuccess.game

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterCreationActivityTest {
    @Test
    fun testProfessionCarouselWorks() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        onView(withId(R.id.tvProfessionName)).check(matches(isDisplayed()))
        // Листаем вправо
        onView(withId(R.id.btnSwipeRight)).perform(click())
        // Листаем влево
        onView(withId(R.id.btnSwipeLeft)).perform(click())
        // Проверяем параметры профессии
        onView(withId(R.id.tvProfessionParams)).check(matches(withSubstring("Зарплата:")))
        // Свайп
        onView(withId(R.id.professionCard)).perform(swipeLeft())
        // Кнопка "Выбрать"
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withText("ШАГ 2/5")).check(matches(isDisplayed()))
    }

    @Test
    fun testDreamCarouselWorks() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withId(R.id.tvDreamName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSwipeRight)).perform(click())
        onView(withId(R.id.btnSwipeLeft)).perform(click())
        onView(withId(R.id.tvDreamParams)).check(matches(withSubstring("Стоимость:")))
        onView(withId(R.id.dreamCard)).perform(swipeLeft())
        onView(withId(R.id.btnChooseDream)).perform(click())
        onView(withText("ШАГ 3/5")).check(matches(isDisplayed()))
    }

    @Test
    fun testNumberPickerStyleAndValue() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withId(R.id.btnChooseDream)).perform(click())
        onView(withId(R.id.numberPickerAge)).check(matches(isDisplayed()))
        onView(withId(R.id.btnNextAge)).perform(click())
        onView(withText("ШАГ 4/5")).check(matches(isDisplayed()))
    }

    @Test
    fun testNameScreenStyleAndInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        onView(withId(R.id.btnChooseProfession)).perform(click())
        onView(withId(R.id.btnChooseDream)).perform(click())
        onView(withId(R.id.btnNextAge)).perform(click())
        onView(withId(R.id.etPlayerName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnVoiceInput)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.etPlayerName)).perform(typeText("Тест"), closeSoftKeyboard())
        onView(withId(R.id.btnNextName)).perform(click())
        onView(withText("ШАГ 5/5")).check(matches(isDisplayed()))
    }

    // Тест перехода к игре можно реализовать через IntentsTestRule, если потребуется
}