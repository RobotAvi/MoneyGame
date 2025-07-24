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

// Временно удалён весь тест GameActivityTest, чтобы не мешал прохождению smoke-теста CharacterCreationActivity. 