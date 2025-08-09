package com.financialsuccess.game.data

import com.financialsuccess.game.models.*
import com.financialsuccess.game.R
import android.content.Context
import com.financialsuccess.game.models.Player
import com.financialsuccess.game.models.GameState
import com.google.gson.Gson
import java.io.File

object GameDataManager {
    
    fun getProfessions(): List<Profession> = listOf(
        Profession(
            id = "teacher",
            name = "Учитель",
            description = "Преподаватель в школе",
            salary = 95000,
            expenses = 50000,
            taxes = 18000,
            education = "Высшее педагогическое",
            avatarResId = R.drawable.profession_teacher
        ),
        Profession(
            id = "engineer",
            name = "Инженер",
            description = "Инженер-программист",
            salary = 130000,
            expenses = 60000,
            taxes = 26000,
            education = "Высшее техническое",
            avatarResId = R.drawable.profession_engineer
        ),
        Profession(
            id = "doctor",
            name = "Врач",
            description = "Врач-терапевт",
            salary = 150000,
            expenses = 70000,
            taxes = 30000,
            education = "Высшее медицинское",
            avatarResId = R.drawable.profession_doctor
        ),
        Profession(
            id = "manager",
            name = "Менеджер",
            description = "Менеджер по продажам",
            salary = 115000,
            expenses = 55000,
            taxes = 23000,
            education = "Высшее экономическое",
            avatarResId = R.drawable.profession_manager
        ),
        Profession(
            id = "mechanic",
            name = "Механик",
            description = "Автомеханик",
            salary = 90000,
            expenses = 45000,
            taxes = 18000,
            education = "Среднее специальное",
            avatarResId = R.drawable.profession_mechanic
        ),
        Profession(
            id = "lawyer",
            name = "Юрист",
            description = "Юрист-консультант",
            salary = 160000,
            expenses = 75000,
            taxes = 32000,
            education = "Высшее юридическое",
            avatarResId = R.drawable.profession_lawyer
        )
    )
    
    fun getDreams(): List<Dream> = listOf(
        Dream(
            id = "yacht",
            name = "Собственная яхта",
            description = "Роскошная яхта для путешествий",
            cost = 15000000,
            cashFlowRequired = 300000,
            fastTrackNumber = 5
        ),
        Dream(
            id = "restaurant",
            name = "Ресторан",
            description = "Открыть собственный ресторан",
            cost = 12000000,
            cashFlowRequired = 200000,
            fastTrackNumber = 4
        ),
        Dream(
            id = "charity",
            name = "Благотворительный фонд",
            description = "Помочь детскому дому",
            cost = 5000000,
            cashFlowRequired = 100000,
            fastTrackNumber = 2
        ),
        Dream(
            id = "island",
            name = "Частный остров",
            description = "Купить остров в тропиках",
            cost = 50000000,
            cashFlowRequired = 800000,
            fastTrackNumber = 6
        ),
        Dream(
            id = "space_trip",
            name = "Космический туризм",
            description = "Полёт в космос",
            cost = 30000000,
            cashFlowRequired = 500000,
            fastTrackNumber = 6
        ),
        Dream(
            id = "business_empire",
            name = "Бизнес-империя",
            description = "Создать сеть компаний",
            cost = 100000000,
            cashFlowRequired = 2000000,
            fastTrackNumber = 6
        )
    )
    
    fun getSmallDeals(): List<Asset> = listOf(
        // Недвижимость (СПб рынок 2025)
        Asset(
            name = "Однокомнатная квартира в СПб",
            type = AssetType.REAL_ESTATE,
            downPayment = 2000000,
            value = 8000000,
            cashFlow = 50000,
            loan = 6000000,
            loanPayment = 60000
        ),
        Asset(
            name = "Двухкомнатная квартира в СПб",
            type = AssetType.REAL_ESTATE,
            downPayment = 3000000,
            value = 12000000,
            cashFlow = 70000,
            loan = 9000000,
            loanPayment = 90000
        ),
        Asset(
            name = "Студия в СПб",
            type = AssetType.REAL_ESTATE,
            downPayment = 1500000,
            value = 7000000,
            cashFlow = 40000,
            loan = 5500000,
            loanPayment = 55000
        ),
        // Бизнес
        Asset(
            name = "Автомойка",
            type = AssetType.BUSINESS,
            downPayment = 250000,
            value = 900000,
            cashFlow = 30000,
            loan = 650000,
            loanPayment = 12000
        ),
        // Ценные бумаги / крипто
        Asset(
            name = "Акции Сбербанка (10 штук)",
            type = AssetType.STOCKS,
            downPayment = 45000,
            value = 45000,
            cashFlow = 2000,
            shares = 10
        ),
        Asset(
            name = "Биткоин (0.1 BTC)",
            type = AssetType.CRYPTO,
            downPayment = 900000,
            value = 900000,
            cashFlow = 0
        ),
        Asset(
            name = "Облигации федерального займа",
            type = AssetType.BONDS,
            downPayment = 80000,
            value = 80000,
            cashFlow = 4000
        ),
        // Прочее
        Asset(
            name = "Гараж в центре города",
            type = AssetType.REAL_ESTATE,
            downPayment = 300000,
            value = 1200000,
            cashFlow = 12000,
            loan = 900000,
            loanPayment = 12000
        )
    )
    
    fun getBigDeals(): List<Asset> = listOf(
        Asset(
            name = "Торговый центр",
            type = AssetType.REAL_ESTATE,
            downPayment = 4000000,
            value = 20000000,
            cashFlow = 250000,
            loan = 16000000,
            loanPayment = 160000
        ),
        Asset(
            name = "IT-стартап",
            type = AssetType.BUSINESS,
            downPayment = 3000000,
            value = 12000000,
            cashFlow = 200000,
            loan = 9000000,
            loanPayment = 90000
        ),
        Asset(
            name = "Акции Газпрома (1000 штук)",
            type = AssetType.STOCKS,
            downPayment = 500000,
            value = 500000,
            cashFlow = 25000,
            shares = 1000
        ),
        Asset(
            name = "Жилой комплекс",
            type = AssetType.REAL_ESTATE,
            downPayment = 9000000,
            value = 45000000,
            cashFlow = 500000,
            loan = 36000000,
            loanPayment = 360000
        ),
        Asset(
            name = "Сеть кафе",
            type = AssetType.BUSINESS,
            downPayment = 1500000,
            value = 6000000,
            cashFlow = 120000,
            loan = 4500000,
            loanPayment = 45000
        )
    )
    
    fun getInvestments(): List<Investment> = listOf(
        Investment(
            name = "ПИФ Сбербанка",
            type = AssetType.BONDS,
            cost = 10000,
            expectedReturn = 800,
            riskLevel = RiskLevel.LOW
        ),
        Investment(
            name = "Акции Яндекса",
            type = AssetType.STOCKS,
            cost = 50000,
            expectedReturn = 6000,
            riskLevel = RiskLevel.MEDIUM
        ),
        Investment(
            name = "Стартап в IT",
            type = AssetType.BUSINESS,
            cost = 200000,
            expectedReturn = 30000,
            riskLevel = RiskLevel.HIGH
        ),
        Investment(
            name = "Эфириум",
            type = AssetType.CRYPTO,
            cost = 100000,
            expectedReturn = 15000,
            riskLevel = RiskLevel.HIGH
        ),
        Investment(
            name = "REIT недвижимости",
            type = AssetType.REAL_ESTATE,
            cost = 75000,
            expectedReturn = 7500,
            riskLevel = RiskLevel.MEDIUM
        )
    )
    
    fun getRandomEvent(): String {
        val events = listOf(
            "Рыночная волатильность! Все ваши акции потеряли 10% стоимости.",
            "Повышение! Ваша зарплата увеличилась на 10000 рублей.",
            "Налоговая проверка. Доплатите 30000 рублей налогов.",
            "Удачная инвестиция! Получите 40000 рублей дополнительного дохода.",
            "Ремонт автомобиля. Потратьте 60000 рублей.",
            "Наследство от дальнего родственника. Получите 500000 рублей.",
            "Экономический кризис. Все доходы от недвижимости снижены на 20% на следующий ход.",
            "У вас родился ребёнок! Расходы увеличиваются на 18000₽/мес.",
            "Новые инвестиционные возможности! Выберите дополнительную карточку актива."
        )
        return events.random()
    }
}

object GameSaveManager {
    private const val PLAYER_FILE = "player.json"
    private const val GAME_FILE = "game_state.json"
    private val gson = Gson()

    fun savePlayer(context: Context, player: Player) {
        val file = File(context.filesDir, PLAYER_FILE)
        file.writeText(gson.toJson(player))
    }

    fun loadPlayer(context: Context): Player? {
        val file = File(context.filesDir, PLAYER_FILE)
        if (!file.exists()) return null
        return try {
            gson.fromJson(file.readText(), Player::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun saveGameState(context: Context, gameState: GameState) {
        val file = File(context.filesDir, GAME_FILE)
        file.writeText(gson.toJson(gameState))
    }

    fun loadGameState(context: Context): GameState? {
        val file = File(context.filesDir, GAME_FILE)
        if (!file.exists()) return null
        return try {
            gson.fromJson(file.readText(), GameState::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun deleteSaves(context: Context) {
        File(context.filesDir, PLAYER_FILE).delete()
        File(context.filesDir, GAME_FILE).delete()
    }
}