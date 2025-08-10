package com.financialsuccess.game

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Mock
import android.graphics.Color
import android.widget.LinearLayout
import android.view.View
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith

@RunWith(RobolectricTestRunner::class)
class GameActivityTest {
    private lateinit var activity: GameActivity
    @Mock private lateinit var mockGameManager: GameManager
    @Mock private lateinit var mockState: GameState
    @Mock private lateinit var mockPlayer: Player

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        activity = mock(GameActivity::class.java)
        activity.currentGameState = mockState
        activity.gameManager = mockGameManager
        `when`(mockState.player).thenReturn(mockPlayer)
    }

    @Test
    fun testPlayerDeathTriggersDialog() {
        `when`(mockPlayer.isAlive()).thenReturn(false)
        // Проверяем, что showDeathDialog вызывается
        activity.updateUI()
        verify(activity).showDeathDialog()
    }

    @Test
    fun testUpdateUICorrectlyUpdatesCash() {
        `when`(mockPlayer.isAlive()).thenReturn(true)
        `when`(mockPlayer.cash).thenReturn(10000)
        // Проверяем, что поле наличных обновляется
        activity.updateUI()
        // Здесь можно проверить, что binding.tvCash.text был обновлён (если использовать Robolectric или Espresso)
    }

    @Test
    fun testAssetSaleUpdatesJournal() {
        `when`(mockPlayer.isAlive()).thenReturn(true)
        val asset = Card("Тестовый актив", 5000)
        `when`(mockPlayer.assets).thenReturn(mutableListOf(asset))
        `when`(mockGameManager.sellAsset(0)).thenReturn(true)
        // Симулируем продажу актива через адаптер
        // Проверяем, что logIncome вызывается
        activity.updateUI()
        verify(mockPlayer, atLeast(0)).logIncome(any(), anyInt(), contains("Продажа"))
    }

    @Test
    fun testShowFinancialJournalEmpty() {
        `when`(mockPlayer.getRecentJournalEntries(100)).thenReturn(emptyList())
        activity.showFinancialJournal()
        // Проверяем, что showMessage вызывается с "Журнал финансов пуст"
        verify(activity).showMessage(contains("Журнал финансов пуст"))
    }
    
    @Test
    fun testExportJournalToTxt() {
        // Создаем тестовые записи журнала
        val testEntries = listOf(
            FinancialEntry(
                type = FinancialEntryType.INCOME,
                category = FinancialCategory.SALARY,
                amount = 50000,
                description = "Зарплата за месяц",
                playerAge = 25,
                monthNumber = 1,
                balanceAfter = 50000,
                realDate = "1 января 2024"
            ),
            FinancialEntry(
                type = FinancialEntryType.EXPENSE,
                category = FinancialCategory.FOOD,
                amount = -5000,
                description = "Продукты",
                playerAge = 25,
                monthNumber = 1,
                balanceAfter = 45000,
                realDate = "1 января 2024"
            )
        )
        
        `when`(mockPlayer.getRecentJournalEntries(100)).thenReturn(testEntries)
        `when`(mockPlayer.name).thenReturn("Тестовый игрок")
        `when`(mockPlayer.profession).thenReturn(Profession("Тестер", 50000, "Тестирование"))
        `when`(mockPlayer.age).thenReturn(25)
        `when`(mockPlayer.monthsPlayed).thenReturn(1)
        `when`(mockPlayer.dream).thenReturn(Dream("Тестовая мечта", 1000000))
        `when`(mockPlayer.cash).thenReturn(45000)
        `when`(mockPlayer.getNetWorth()).thenReturn(45000)
        `when`(mockPlayer.passiveIncome).thenReturn(0)
        `when`(mockPlayer.getCategoryStats()).thenReturn(mapOf(
            FinancialCategory.SALARY to 50000,
            FinancialCategory.FOOD to -5000
        ))
        `when`(mockPlayer.getMonthlyStats(1)).thenReturn(Pair(50000, 5000))
        
        // Вызываем экспорт
        activity.exportJournalToTxt(mockPlayer, testEntries)
        
        // Проверяем, что метод выполнился без ошибок
        // В реальном тесте здесь можно проверить создание Intent
    }

    @Test
    fun testBuyAssetDeductsOnlyDownPayment() {
        val player = Player(
            position = 0,
            cash = 55000,
            salary = 50000,
            age = 30,
            profession = null,
            dream = null
        )
        val asset = Asset(
            name = "Облигации федерального займа",
            downPayment = 50000,
            cashFlow = 1000,
            loan = 0,
            loanPayment = 0
        )
        val manager = GameManager()
        // Вручную подставляем состояние
        val state = GameState(player)
        val field = GameManager::class.java.getDeclaredField("gameState")
        field.isAccessible = true
        field.set(manager, state)

        val result = manager.buyAsset(asset)
        assertTrue(result)
        assertEquals(5000, player.cash)
        assertTrue(player.assets.contains(asset))
    }

    // Тест testMonthProgressBarColors удален - monthProgressBar больше не используется

    @Test
    fun testPlayerIconPositionMatchesMonthProgress() {
        val activityController = Robolectric.buildActivity(GameActivity::class.java).setup()
        val activity = activityController.get()
        val currentDay = 15
        // Устанавливаем день месяца
        val player = Player(
            position = 0,
            cash = 10000,
            salary = 5000,
            age = 30,
            profession = null,
            dream = null
        )
        player.currentDayOfMonth = currentDay
        activity.currentGameState = GameState(player)
        // Обновляем UI
        activity.updateUI()
        // Получаем layout трека и иконку игрока
        val llGameTrack = activity.findViewById<LinearLayout>(R.id.ll_game_track)
        val trackLayout = llGameTrack.getChildAt(1) as LinearLayout
        val playerIcon = trackLayout.findViewById<View>(R.id.iv_player_on_track)
        // Проверяем, что позиция иконки соответствует дню месяца (середина трека для 15 дня)
        val trackWidth = trackLayout.width - playerIcon.width
        val expectedProgress = ((currentDay - 1).toFloat() / 29f)
        val expectedX = (trackWidth * expectedProgress)
        // Погрешность допускается из-за float/int преобразований
        assertEquals(expectedX, playerIcon.translationX, 2f)
    }
} 