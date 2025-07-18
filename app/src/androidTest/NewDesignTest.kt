package com.financialsuccess.game

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NewDesignTest {
    
    @Before
    fun setUp() {
        // Подготовка к тестам нового дизайна
    }
    
    // === ТЕСТЫ ГЛАВНОГО ЭКРАНА ===
    
    @Test
    fun testMainScreenNewDesign() {
        ActivityScenario.launch(MainActivity::class.java)
        
        // Проверяем новые элементы главного экрана
        onView(withId(R.id.btn_new_game)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_advanced_character)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_rules)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_exit)).check(matches(isDisplayed()))
        
        // Проверяем, что кнопки имеют новый стиль
        onView(withId(R.id.btn_new_game)).check(matches(withText("Начать игру")))
        onView(withId(R.id.btn_advanced_character)).check(matches(withText("Создать персонажа")))
        onView(withId(R.id.btn_rules)).check(matches(withText("Правила")))
    }
    
    // === ТЕСТЫ ЭКРАНА СОЗДАНИЯ ПЕРСОНАЖА ===
    
    @Test
    fun testCharacterCreationNewDesign() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем новые элементы дизайна
        onView(withId(R.id.etPlayerName)).check(matches(isDisplayed()))
        onView(withId(R.id.etAge)).check(matches(isDisplayed()))
        onView(withId(R.id.etStartDate)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_start_game)).check(matches(isDisplayed()))
        
        // Проверяем RecyclerView'ы
        onView(withId(R.id.recyclerViewProfessions)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerViewDreams)).check(matches(isDisplayed()))
    }
    
    // === ТЕСТЫ ИГРОВОГО ЭКРАНА ===
    
    @Test
    fun testGameScreenNewDesign() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем новые элементы игрового экрана
        onView(withId(R.id.tv_cash)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_salary)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_passive_income)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_level)).check(matches(isDisplayed()))
        
        // Проверяем кубик
        onView(withId(R.id.card_dice)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_dice)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_dice_value)).check(matches(isDisplayed()))
        
        // Проверяем кнопки действий
        onView(withId(R.id.btn_financial_statement)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_assets)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_market)).check(matches(isDisplayed()))
        
        // Проверяем статус игры
        onView(withId(R.id.tv_game_status)).check(matches(isDisplayed()))
    }
    
    @Test
    fun testGameScreenTopPanel() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем верхнюю панель с показателями
        onView(withText("Наличные")).check(matches(isDisplayed()))
        onView(withText("Зарплата")).check(matches(isDisplayed()))
        onView(withText("Пассивный")).check(matches(isDisplayed()))
        onView(withText("Уровень")).check(matches(isDisplayed()))
        
        // Проверяем значения по умолчанию
        onView(withText("170")).check(matches(isDisplayed())) // Наличные
        onView(withText("0.00")).check(matches(isDisplayed())) // Зарплата
        onView(withText("196")).check(matches(isDisplayed())) // Пассивный доход
        onView(withText("530")).check(matches(isDisplayed())) // Уровень
    }
    
    @Test
    fun testGameScreenCenterArea() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем центральную область
        onView(withId(R.id.card_dice)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_dice_value)).check(matches(withText("Брось кубик!")))
        onView(withId(R.id.tv_game_status)).check(matches(withText("🐀 Крысиные бега")))
    }
    
    @Test
    fun testGameScreenBottomPanel() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем нижнюю панель с кнопками
        onView(withId(R.id.btn_financial_statement)).check(matches(withText(containsString("Финансы"))))
        onView(withId(R.id.btn_assets)).check(matches(withText(containsString("Активы"))))
        onView(withId(R.id.btn_market)).check(matches(withText(containsString("Рынок"))))
    }
    
    @Test
    fun testDiceInteraction() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем, что кубик кликабелен
        onView(withId(R.id.card_dice)).check(matches(isClickable()))
        
        // Нажимаем на кубик
        onView(withId(R.id.card_dice)).perform(click())
        
        // Проверяем, что кубик остается видимым после клика
        onView(withId(R.id.card_dice)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_dice)).check(matches(isDisplayed()))
    }
    
    @Test
    fun testNavigationButtons() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем навигацию по кнопкам
        onView(withId(R.id.btn_financial_statement)).perform(click())
        // Должен открыться экран финансов
        
        // Возвращаемся назад
        onView(withId(android.R.id.button1)).perform(click()) // OK кнопка или back
        
        onView(withId(R.id.btn_assets)).perform(click())
        // Должен открыться экран активов
        
        // Возвращаемся назад
        onView(withId(android.R.id.button1)).perform(click()) // OK кнопка или back
        
        onView(withId(R.id.btn_market)).perform(click())
        // Должен открыться экран рынка
    }
    
    @Test
    fun testNewDesignColorScheme() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем, что все элементы отображаются (косвенно проверяем цветовую схему)
        onView(withId(R.id.tv_cash)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_salary)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_passive_income)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_level)).check(matches(isDisplayed()))
        onView(withId(R.id.card_dice)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_financial_statement)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_assets)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_market)).check(matches(isDisplayed()))
    }
    
    @Test
    fun testNewDesignAccessibility() {
        // Создаем персонажа и переходим к игре
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем доступность элементов
        onView(withId(R.id.card_dice)).check(matches(isClickable()))
        onView(withId(R.id.btn_financial_statement)).check(matches(isClickable()))
        onView(withId(R.id.btn_assets)).check(matches(isClickable()))
        onView(withId(R.id.btn_market)).check(matches(isClickable()))
        
        // Проверяем, что текстовые элементы читаемы
        onView(withId(R.id.tv_cash)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_salary)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_passive_income)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_level)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_dice_value)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_game_status)).check(matches(isDisplayed()))
    }
}