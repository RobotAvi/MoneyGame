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
        
        // Проверяем, что основные элементы отображаются
        onView(withId(R.id.tvTitle)).check(matches(isDisplayed()))
        onView(withId(R.id.etPlayerName)).check(matches(isDisplayed()))
        onView(withId(R.id.etAge)).check(matches(isDisplayed()))
        onView(withId(R.id.btnStartGame)).check(matches(isDisplayed()))
    }
    
    @Test
    fun testDefaultValuesAreSet() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем значения по умолчанию
        onView(withId(R.id.etPlayerName)).check(matches(withText("Игрок")))
        onView(withId(R.id.etAge)).check(matches(withText("25")))
        onView(withId(R.id.etWorkExperience)).check(matches(withText("0")))
        onView(withId(R.id.etChildrenCount)).check(matches(withText("0")))
        onView(withId(R.id.etSpouseIncome)).check(matches(withText("0")))
        onView(withId(R.id.etSavingsRate)).check(matches(withText("10")))
        onView(withId(R.id.etRetirementAge)).check(matches(withText("65")))
        onView(withId(R.id.etTargetPassiveIncome)).check(matches(withText("100000")))
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
    
    @Test
    fun testWorkExperienceInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etWorkExperience))
            .perform(clearText())
            .perform(typeText("5"))
        
        onView(withId(R.id.etWorkExperience)).check(matches(withText("5")))
    }
    
    @Test
    fun testChildrenCountInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etChildrenCount))
            .perform(clearText())
            .perform(typeText("2"))
        
        onView(withId(R.id.etChildrenCount)).check(matches(withText("2")))
    }
    
    @Test
    fun testSpouseIncomeInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etSpouseIncome))
            .perform(clearText())
            .perform(typeText("50000"))
        
        onView(withId(R.id.etSpouseIncome)).check(matches(withText("50000")))
    }
    
    @Test
    fun testSavingsRateInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etSavingsRate))
            .perform(clearText())
            .perform(typeText("25"))
        
        onView(withId(R.id.etSavingsRate)).check(matches(withText("25")))
    }
    
    @Test
    fun testRetirementAgeInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etRetirementAge))
            .perform(clearText())
            .perform(typeText("55"))
        
        onView(withId(R.id.etRetirementAge)).check(matches(withText("55")))
    }
    
    @Test
    fun testTargetPassiveIncomeInput() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.etTargetPassiveIncome))
            .perform(clearText())
            .perform(typeText("200000"))
        
        onView(withId(R.id.etTargetPassiveIncome)).check(matches(withText("200000")))
    }
    
    // === ТЕСТЫ SPINNER'ОВ ===
    
    @Test
    fun testEducationSpinner() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Открываем спиннер образования
        onView(withId(R.id.spinnerEducation)).perform(click())
        
        // Проверяем, что все варианты образования отображаются
        onView(withText("Среднее образование")).check(matches(isDisplayed()))
        onView(withText("Среднее специальное")).check(matches(isDisplayed()))
        onView(withText("Высшее образование")).check(matches(isDisplayed()))
        onView(withText("Магистратура")).check(matches(isDisplayed()))
        onView(withText("Докторантура")).check(matches(isDisplayed()))
        
        // Выбираем магистратуру
        onView(withText("Магистратура")).perform(click())
    }
    
    @Test
    fun testMaritalStatusSpinner() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.spinnerMaritalStatus)).perform(click())
        
        // Проверяем варианты семейного положения
        onView(withText("Холост/Не замужем")).check(matches(isDisplayed()))
        onView(withText("Женат/Замужем")).check(matches(isDisplayed()))
        onView(withText("Разведен/Разведена")).check(matches(isDisplayed()))
        onView(withText("Вдовец/Вдова")).check(matches(isDisplayed()))
        
        // Выбираем женат/замужем
        onView(withText("Женат/Замужем")).perform(click())
    }
    
    @Test
    fun testRiskToleranceSpinner() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.spinnerRiskTolerance)).perform(click())
        
        // Проверяем варианты толерантности к риску
        onView(withText("Консервативный")).check(matches(isDisplayed()))
        onView(withText("Умеренный")).check(matches(isDisplayed()))
        onView(withText("Агрессивный")).check(matches(isDisplayed()))
        
        // Выбираем агрессивный
        onView(withText("Агрессивный")).perform(click())
    }
    
    @Test
    fun testInvestmentStrategySpinner() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.spinnerInvestmentStrategy)).perform(click())
        
        // Проверяем варианты стратегий инвестиций
        onView(withText("Консервативная")).check(matches(isDisplayed()))
        onView(withText("Сбалансированная")).check(matches(isDisplayed()))
        onView(withText("Агрессивная")).check(matches(isDisplayed()))
        onView(withText("Дивидендная")).check(matches(isDisplayed()))
        onView(withText("Ростовая")).check(matches(isDisplayed()))
        
        // Выбираем агрессивную
        onView(withText("Агрессивная")).perform(click())
    }
    
    @Test
    fun testHealthLevelSpinner() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.spinnerHealthLevel)).perform(click())
        
        // Проверяем варианты уровня здоровья
        onView(withText("Отличное")).check(matches(isDisplayed()))
        onView(withText("Хорошее")).check(matches(isDisplayed()))
        onView(withText("Удовлетворительное")).check(matches(isDisplayed()))
        onView(withText("Плохое")).check(matches(isDisplayed()))
        
        // Выбираем отличное
        onView(withText("Отличное")).perform(click())
    }
    
    @Test
    fun testStressLevelSpinner() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.spinnerStressLevel)).perform(click())
        
        // Проверяем варианты уровня стресса
        onView(withText("Низкий")).check(matches(isDisplayed()))
        onView(withText("Умеренный")).check(matches(isDisplayed()))
        onView(withText("Высокий")).check(matches(isDisplayed()))
        onView(withText("Критический")).check(matches(isDisplayed()))
        
        // Выбираем низкий
        onView(withText("Низкий")).perform(click())
    }
    
    @Test
    fun testWorkLifeBalanceSpinner() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        onView(withId(R.id.spinnerWorkLifeBalance)).perform(click())
        
        // Проверяем варианты баланса работы и жизни
        onView(withText("Работа")).check(matches(isDisplayed()))
        onView(withText("Баланс")).check(matches(isDisplayed()))
        onView(withText("Личная жизнь")).check(matches(isDisplayed()))
        
        // Выбираем баланс
        onView(withText("Баланс")).perform(click())
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
    
    @Test
    fun testSkillsRecyclerView() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Проверяем, что RecyclerView навыков отображается
        onView(withId(R.id.recyclerViewSkills)).check(matches(isDisplayed()))
        
        // Проверяем, что навыки загружены
        onView(withText("Программирование")).check(matches(isDisplayed()))
        onView(withText("Менеджмент")).check(matches(isDisplayed()))
        onView(withText("Продажи")).check(matches(isDisplayed()))
        onView(withText("Коммуникация")).check(matches(isDisplayed()))
        onView(withText("Финансовая грамотность")).check(matches(isDisplayed()))
    }
    
    // === ТЕСТЫ ВЫБОРА ЭЛЕМЕНТОВ ===
    
    @Test
    fun testSelectProfession() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем профессию программиста
        onView(withText("Программист")).perform(click())
        
        // Проверяем, что кнопка старта стала активной
        onView(withId(R.id.btnStartGame)).check(matches(isEnabled()))
    }
    
    @Test
    fun testSelectDream() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем мечту
        onView(withText("Коттедж")).perform(click())
        
        // Проверяем, что кнопка старта стала активной
        onView(withId(R.id.btnStartGame)).check(matches(isEnabled()))
    }
    
    @Test
    fun testSelectSkills() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем несколько навыков
        onView(withText("Программирование")).perform(click())
        onView(withText("Менеджмент")).perform(click())
        onView(withText("Финансовая грамотность")).perform(click())
        
        // Проверяем, что навыки выбраны (визуально должны быть выделены)
        // Это может потребовать дополнительной проверки состояния адаптера
    }
    
    // === ТЕСТЫ ВАЛИДАЦИИ ===
    
    @Test
    fun testStartButtonDisabledInitially() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Кнопка старта должна быть неактивна изначально
        onView(withId(R.id.btnStartGame)).check(matches(not(isEnabled())))
    }
    
    @Test
    fun testStartButtonEnabledAfterSelection() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Кнопка старта должна стать активной
        onView(withId(R.id.btnStartGame)).check(matches(isEnabled()))
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
        onView(withId(R.id.btnStartGame)).perform(click())
        
        // Должно появиться сообщение об ошибке
        onView(withText(containsString("возраст"))).check(matches(isDisplayed()))
    }
    
    @Test
    fun testSavingsRateValidation() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим недопустимый процент сбережений
        onView(withId(R.id.etSavingsRate))
            .perform(clearText())
            .perform(typeText("60"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Пытаемся запустить игру
        onView(withId(R.id.btnStartGame)).perform(click())
        
        // Должно появиться сообщение об ошибке
        onView(withText(containsString("сбережения"))).check(matches(isDisplayed()))
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
        
        // Заполняем все поля
        onView(withId(R.id.etPlayerName))
            .perform(clearText())
            .perform(typeText("Иван Иванов"))
        
        onView(withId(R.id.etAge))
            .perform(clearText())
            .perform(typeText("28"))
        
        onView(withId(R.id.etWorkExperience))
            .perform(clearText())
            .perform(typeText("5"))
        
        onView(withId(R.id.etChildrenCount))
            .perform(clearText())
            .perform(typeText("1"))
        
        onView(withId(R.id.etSpouseIncome))
            .perform(clearText())
            .perform(typeText("40000"))
        
        onView(withId(R.id.etSavingsRate))
            .perform(clearText())
            .perform(typeText("15"))
        
        onView(withId(R.id.etRetirementAge))
            .perform(clearText())
            .perform(typeText("60"))
        
        onView(withId(R.id.etTargetPassiveIncome))
            .perform(clearText())
            .perform(typeText("150000"))
        
        // Выбираем спиннеры
        onView(withId(R.id.spinnerEducation)).perform(click())
        onView(withText("Магистратура")).perform(click())
        
        onView(withId(R.id.spinnerMaritalStatus)).perform(click())
        onView(withText("Женат/Замужем")).perform(click())
        
        onView(withId(R.id.spinnerRiskTolerance)).perform(click())
        onView(withText("Умеренный")).perform(click())
        
        onView(withId(R.id.spinnerInvestmentStrategy)).perform(click())
        onView(withText("Сбалансированная")).perform(click())
        
        onView(withId(R.id.spinnerHealthLevel)).perform(click())
        onView(withText("Хорошее")).perform(click())
        
        onView(withId(R.id.spinnerStressLevel)).perform(click())
        onView(withText("Низкий")).perform(click())
        
        onView(withId(R.id.spinnerWorkLifeBalance)).perform(click())
        onView(withText("Баланс")).perform(click())
        
        // Выбираем профессию, мечту и навыки
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        onView(withText("Программирование")).perform(click())
        onView(withText("Менеджмент")).perform(click())
        
        // Запускаем игру
        onView(withId(R.id.btnStartGame)).perform(click())
        
        // Должны перейти к GameActivity
        // Проверяем, что мы на игровом экране
        onView(withId(R.id.tvCash)).check(matches(isDisplayed()))
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
        onView(withId(R.id.btnStartGame)).perform(click())
        
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
        
        onView(withId(R.id.etWorkExperience))
            .perform(clearText())
            .perform(typeText("40"))
        
        onView(withId(R.id.etChildrenCount))
            .perform(clearText())
            .perform(typeText("10"))
        
        onView(withId(R.id.etSavingsRate))
            .perform(clearText())
            .perform(typeText("50"))
        
        onView(withId(R.id.etRetirementAge))
            .perform(clearText())
            .perform(typeText("80"))
        
        onView(withId(R.id.etTargetPassiveIncome))
            .perform(clearText())
            .perform(typeText("1000000"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Игра должна запуститься
        onView(withId(R.id.btnStartGame)).check(matches(isEnabled()))
    }
    
    @Test
    fun testMinimumValues() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Вводим минимальные значения
        onView(withId(R.id.etAge))
            .perform(clearText())
            .perform(typeText("18"))
        
        onView(withId(R.id.etWorkExperience))
            .perform(clearText())
            .perform(typeText("0"))
        
        onView(withId(R.id.etChildrenCount))
            .perform(clearText())
            .perform(typeText("0"))
        
        onView(withId(R.id.etSavingsRate))
            .perform(clearText())
            .perform(typeText("0"))
        
        onView(withId(R.id.etRetirementAge))
            .perform(clearText())
            .perform(typeText("50"))
        
        onView(withId(R.id.etTargetPassiveIncome))
            .perform(clearText())
            .perform(typeText("10000"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Игра должна запуститься
        onView(withId(R.id.btnStartGame)).check(matches(isEnabled()))
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
        
        onView(withId(R.id.etWorkExperience))
            .perform(clearText(), typeText("3"))
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Проверяем, что все работает корректно
        onView(withId(R.id.btnStartGame)).check(matches(isEnabled()))
    }
    
    @Test
    fun testMultipleSkillSelection() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Выбираем много навыков
        onView(withText("Программирование")).perform(click())
        onView(withText("Менеджмент")).perform(click())
        onView(withText("Продажи")).perform(click())
        onView(withText("Коммуникация")).perform(click())
        onView(withText("Переговоры")).perform(click())
        onView(withText("Адаптивность")).perform(click())
        onView(withText("Аналитика")).perform(click())
        onView(withText("Планирование")).perform(click())
        onView(withText("Финансовая грамотность")).perform(click())
        onView(withText("Креативность")).perform(click())
        
        // Выбираем профессию и мечту
        onView(withText("Программист")).perform(click())
        onView(withText("Коттедж")).perform(click())
        
        // Проверяем, что все работает
        onView(withId(R.id.btnStartGame)).check(matches(isEnabled()))
    }
    
    // === ТЕСТЫ НАВИГАЦИИ ===
    
    @Test
    fun testBackButton() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Нажимаем кнопку "Назад"
        onView(withId(R.id.btnBack)).perform(click())
        
        // Должны вернуться к предыдущему экрану
        // Это может потребовать дополнительной проверки
    }
    
    @Test
    fun testScrollBehavior() {
        ActivityScenario.launch(CharacterCreationActivity::class.java)
        
        // Прокручиваем вниз
        onView(withId(R.id.scrollView)).perform(swipeUp())
        
        // Прокручиваем вверх
        onView(withId(R.id.scrollView)).perform(swipeDown())
        
        // Проверяем, что все элементы все еще доступны
        onView(withId(R.id.etPlayerName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnStartGame)).check(matches(isDisplayed()))
    }
}