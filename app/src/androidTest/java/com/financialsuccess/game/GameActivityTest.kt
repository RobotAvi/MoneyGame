package com.financialsuccess.game

import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.financialsuccess.game.models.Dream
import com.financialsuccess.game.models.Profession
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameActivityTest {
    @Test
    fun testGameActivityLaunch_noCrash() {
        // Подготовим фейковые объекты профессии и мечты
        val profession = Profession("test_prof", "Тестовая профессия", 50000, "desc", "id", 0)
        val dream = Dream("Тестовая мечта", 1000000, 6)
        val intent = Intent().apply {
            putExtra("profession", profession)
            putExtra("dream", dream)
            putExtra("playerAge", 30)
            putExtra("playerName", "Тест")
            putExtra("startDate", System.currentTimeMillis())
        }
        ActivityScenario.launch<GameActivity>(intent)
        // Проверяем, что основной элемент экрана отображается
        onView(withId(R.id.ll_game_track)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_player_on_track)).check(matches(isDisplayed()))
    }
} 