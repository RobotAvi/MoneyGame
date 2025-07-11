import com.financialsuccess.game.models.Player
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PlayerDateLogicTest {
    private lateinit var player: Player
    private lateinit var profession: com.financialsuccess.game.models.Profession
    private lateinit var dream: com.financialsuccess.game.models.Dream

    @Before
    fun setUp() {
        profession = com.financialsuccess.game.models.Profession(
            id = "test_prof",
            name = "Тестовая профессия",
            description = "desc",
            salary = 1000,
            expenses = 500,
            taxes = 100,
            education = ""
        )
        dream = com.financialsuccess.game.models.Dream(
            id = "test_dream",
            name = "Тестовая мечта",
            description = "desc",
            cost = 10000,
            cashFlowRequired = 1000
        )
        player = Player(profession = profession, dream = dream)
    }

    @Test
    fun testInitialDay() {
        assertEquals(1, player.currentDayOfMonth)
    }

    @Test
    fun testDayIncreaseWithinMonth() {
        player.currentDayOfMonth = 1
        player.currentDayOfMonth += 5
        assertEquals(6, player.currentDayOfMonth)
    }

    @Test
    fun testMonthOverflowAndReset() {
        player.currentDayOfMonth = 28
        player.currentDayOfMonth += 5 // 33
        while (player.currentDayOfMonth > Player.DAYS_IN_MONTH) {
            player.currentDayOfMonth -= Player.DAYS_IN_MONTH
            player.passMonth()
        }
        assertEquals(3, player.currentDayOfMonth)
        assertEquals(1, player.monthsPlayed)
    }

    @Test
    fun testDateStringFormat() {
        player.currentDayOfMonth = 15
        player.monthsPlayed = 2 // Март
        val months = arrayOf("Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь")
        val startYear = 2024
        val currentMonth = player.monthsPlayed % 12
        val currentYear = startYear + player.monthsPlayed / 12
        val realDate = "${player.currentDayOfMonth} ${months[currentMonth]} $currentYear"
        assertEquals("15 Март 2024", realDate)
    }

    @Test
    fun testPlayerNameAndStartDate() {
        val name = "Алексей"
        val startDate = 1717977600000L // 10.06.2024
        val player = Player(name = name, startDateMillis = startDate, profession = profession, dream = dream)
        assertEquals(name, player.name)
        assertEquals(startDate, player.startDateMillis)
    }
}