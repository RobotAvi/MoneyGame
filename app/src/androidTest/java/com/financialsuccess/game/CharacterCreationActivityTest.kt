package com.financialsuccess.game

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterCreationActivityTest {
    @Test
    fun testActivityLaunchAndFirstStep() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        // Проверяем, что отображается заголовок первого шага
        onView(withText("ШАГ 1/5")).check(matches(isDisplayed()))
    }
}