package com.financialsuccess.game

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.financialsuccess.game.models.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CharacterCreationActivityTest {
    
    @Before
    fun setUp() {
        // Подготовка к тестам
    }
    
    // === ТЕСТЫ ОСНОВНОГО ИНТЕРФЕЙСА ===
    
    @Test
    fun testActivityLaunchesSuccessfully() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что основные элементы отображаются (обновлено под новый дизайн)
        onView(withId(R.id.etPlayerName)).check(matches(isDisplayed()))
        onView(withId(R.id.etAge)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_start_game)).check(matches(isDisplayed()))
        
        // Проверяем, что RecyclerView'ы отображаются
        onView(withId(R.id.recyclerViewProfessions)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerViewDreams)).check(matches(isDisplayed()))
        
        // Проверяем новые элементы дизайна
        onView(withText("👤 Ваш возраст")).check(matches(isDisplayed()))
        onView(withText("Имя персонажа")).check(matches(isDisplayed()))
        onView(withText("Дата начала игры")).check(matches(isDisplayed()))
    }
    
    @Test
    fun testDefaultValuesAreSet() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем значения по умолчанию (обновлено под новый дизайн)
        onView(withId(R.id.etPlayerName)).check(matches(withText(""))) // Пустое поле по умолчанию
        onView(withId(R.id.etAge)).check(matches(withText("25")))
        onView(withId(R.id.etStartDate)).check(matches(isDisplayed())) // Поле даты отображается
    }
    
    // === ТЕСТЫ ВВОДА ТЕКСТА ===
    
    @Test
    fun testPlayerNameInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Очищаем поле и вводим новое имя
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Александр Петров"))
        
        // Проверяем, что имя введено
        onView(withId(R.id.etPlayerName)).check(matches(withText("Александр Петров")))
    }
    
    @Test
    fun testAgeInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим возраст
        onView(withId(R.id.etAge))
            .perform(clearText())
            .perform(typeText("30"))
        
        onView(withId(R.id.etAge)).check(matches(withText("30")))
    }
    
    // === ТЕСТЫ RECYCLERVIEW ===
    
    @Test
    fun testProfessionsRecyclerView() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что RecyclerView профессий отображается
        onView(withId(R.id.recyclerViewProfessions)).check(matches(isDisplayed()))
        
        // Проверяем, что профессии загружены (должны быть видны)
        onView(withText("Программист")).check(matches(isDisplayed()))
        onView(withText("Врач")).check(matches(isDisplayed()))
        onView(withText("Учитель")).check(matches(isDisplayed()))
        onView(withText("Менеджер")).check(matches(isDisplayed()))
        onView(withText("Инженер")).check(matches(isDisplayed()))
        onView(withText("Бухгалтер")).check(matches(isDisplayed()))
    }
    
    @Test
    fun testDreamsRecyclerView() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что RecyclerView мечт отображается
        onView(withId(R.id.recyclerViewDreams)).check(matches(isDisplayed()))
        
        // Проверяем, что мечты загружены
        onView(withText("Коттедж")).check(matches(isDisplayed()))
        onView(withText("Яхта")).check(matches(isDisplayed()))
        onView(withText("Путешествие")).check(matches(isDisplayed()))
    }
    
    // === ТЕСТЫ ВЫБОРА ЭЛЕМЕНТОВ ===
    
    @Test
    fun testSelectProfession() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем профессию программиста
        onView(withText("Программист")).perform(click())
        
        // Проверяем, что кнопка старта стала активной
        onView(withId(R.id.btn_start_game)).check(matches(isEnabled()))
    }
    
    @Test
    fun testSelectDream() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем мечту
        onView(withText("Коттедж")).perform(click())
        
        // Проверяем, что кнопка старта стала активной
        onView(withId(R.id.btn_start_game)).check(matches(isEnabled()))
    }
    
    // === ТЕСТЫ ВАЛИДАЦИИ ===
    
    @Test
    fun testStartButtonDisabledInitially() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Кнопка старта должна быть неактивна изначально
        onView(withId(R.id.btn_start_game)).check(matches(not(isEnabled())))
    }
    
    @Test
    fun testStartButtonEnabledAfterSelection() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Кнопка старта должна стать активной
        onView(withId(R.id.btn_start_game)).check(matches(isEnabled()))
    }
    
    @Test
    fun testAgeValidation() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим недопустимый возраст
        onView(withId(R.id.etAge))
            .perform(clearText())
            .perform(typeText("15"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Пытаемся запустить игру
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Должно появиться сообщение об ошибке
        onView(withText(containsString("возраст"))).check(matches(isDisplayed()))
    }
    
    // === ТЕСТЫ ДАТЫ ===
    
    @Test
    fun testDatePickerDialog() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Нажимаем на поле даты
        onView(withId(R.id.etStartDate)).perform(click())
        
        // Должен открыться DatePicker
        // Проверяем, что диалог отображается
        onView(withId(android.R.id.button1)).check(matches(isDisplayed())) // OK кнопка
        onView(withId(android.R.id.button2)).check(matches(isDisplayed())) // Cancel кнопка
    }
    
    // === ТЕСТЫ ПОЛНОГО ПРОЦЕССА СОЗДАНИЯ ===
    
    @Test
    fun testCompleteCharacterCreation() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Заполняем все поля (обновлено под новый дизайн)
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Иван Иванов"))
        
        onView(withId(R.id.etAge))
            .perform(clearText())
            .perform(typeText("28"))
        
        // Выбираем профессию, мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Запускаем игру
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Должны перейти к GameActivity
        // Проверяем, что мы на игровом экране (обновлено под новый дизайн)
        onView(withId(R.id.tv_cash)).check(matches(isDisplayed()))
    }
    
    // === ТЕСТЫ ГРАНИЧНЫХ СЛУЧАЕВ ===
    
    @Test
    fun testEmptyPlayerName() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Очищаем имя игрока
        onView(withId(R.id.etPlayerName)).perform(clearText())
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Пытаемся запустить игру
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Должно появиться сообщение об ошибке
        onView(withText(containsString("имя"))).check(matches(isDisplayed()))
    }
    
    @Test
    fun testMaximumValues() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим максимальные значения
        onView(withId(R.id.etAge))
            .perform(clearText())
            .perform(typeText("65"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Игра должна запуститься
        onView(withId(R.id.btn_start_game)).check(matches(isEnabled()))
    }
    
    @Test
    fun testMinimumValues() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим минимальные значения
        onView(withId(R.id.etAge))
            .perform(clearText())
            .perform(typeText("18"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Игра должна запуститься
        onView(withId(R.id.btn_start_game)).check(matches(isEnabled()))
    }
    
    // === ТЕСТЫ ПРОИЗВОДИТЕЛЬНОСТИ ===
    
    @Test
    fun testRapidInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Быстро заполняем все поля
        onView(withId(R.id.etPlayerName))
            .perform(clearText(), typeText("Тест"))
        
        onView(withId(R.id.etAge))
            .perform(clearText(), typeText("25"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Проверяем, что все работает корректно
        onView(withId(R.id.btn_start_game)).check(matches(isEnabled()))
    }
    
    // === ТЕСТЫ НАВИГАЦИИ ===
    
    @Test
    fun testBackButton() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Нажимаем кнопку "Назад" (если есть)
        // onView(withId(R.id.btnBack)).perform(click())
        
        // Должны вернуться к предыдущему экрану
        // Это может потребовать дополнительной проверки
    }
    
    // === ТЕСТЫ НОВОГО ДИЗАЙНА ===
    
    @Test
    fun testNewDesignElements() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что новые элементы дизайна отображаются
        // Карточки с профессиями и мечтами должны иметь новый стиль
        onView(withId(R.id.recyclerViewProfessions)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerViewDreams)).check(matches(isDisplayed()))
        
        // Проверяем, что кнопка старта имеет новый стиль
        onView(withId(R.id.btn_start_game)).check(matches(isDisplayed()))
        
        // Проверяем новые заголовки секций
        onView(withText("👤 Ваш возраст")).check(matches(isDisplayed()))
        onView(withText("Имя персонажа")).check(matches(isDisplayed()))
        onView(withText("Дата начала игры")).check(matches(isDisplayed()))
    }
    
    @Test
    fun testCardSelectionVisualFeedback() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем профессию и проверяем визуальную обратную связь
        onView(withText("Программист")).perform(click())
        
        // Проверяем, что карточка выделена (если есть визуальная индикация)
        // Это может потребовать проверки состояния адаптера
    }
    
    @Test
    fun testModernUIElements() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что современные элементы UI отображаются
        // Поля ввода должны иметь новый стиль
        onView(withId(R.id.etPlayerName)).check(matches(isDisplayed()))
        onView(withId(R.id.etAge)).check(matches(isDisplayed()))
        onView(withId(R.id.etStartDate)).check(matches(isDisplayed()))
        
        // Кнопка должна иметь новый стиль
        onView(withId(R.id.btn_start_game)).check(matches(isDisplayed()))
    }
    
    // === ТЕСТЫ НОВЫХ ЭЛЕМЕНТОВ ИГРОВОГО ЭКРАНА ===
    
    @Test
    fun testGameScreenNewDesign() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Создаем персонажа и переходим к игре
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
        
        // Проверяем новые кнопки действий
        onView(withId(R.id.btn_financial_statement)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_assets)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_market)).check(matches(isDisplayed()))
        
        // Проверяем кубик
        onView(withId(R.id.card_dice)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_dice)).check(matches(isDisplayed()))
    }
    
    @Test
    fun testGameScreenStatsCards() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Создаем персонажа и переходим к игре
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем карточки статистики
        onView(withText("Наличные")).check(matches(isDisplayed()))
        onView(withText("Зарплата")).check(matches(isDisplayed()))
        onView(withText("Пассивный")).check(matches(isDisplayed()))
        onView(withText("Уровень")).check(matches(isDisplayed()))
    }
    
    @Test
    fun testGameScreenActionButtons() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Создаем персонажа и переходим к игре
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Тест Игрок"))
        
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withId(R.id.btn_start_game)).perform(click())
        
        // Проверяем кнопки действий
        onView(withText("Финансы")).check(matches(isDisplayed()))
        onView(withText("Активы")).check(matches(isDisplayed()))
        onView(withText("Рынок")).check(matches(isDisplayed()))
    }
    
    @Test
    fun testDiceInteraction() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Создаем персонажа и переходим к игре
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
        
        // Проверяем, что текст кубика изменился или остался видимым
        onView(withId(R.id.tv_dice_value)).check(matches(isDisplayed()))
    }
}