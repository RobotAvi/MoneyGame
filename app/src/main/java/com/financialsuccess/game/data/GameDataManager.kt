package com.financialsuccess.game.data

import com.financialsuccess.game.models.*

object GameDataManager {
    
    fun getProfessions(): List<Profession> = listOf(
        Profession(
            id = "teacher",
            name = "Учитель",
            description = "Преподаватель в школе",
            salary = 45000,
            expenses = 25000,
            taxes = 8000,
            education = "Высшее педагогическое"
        ),
        Profession(
            id = "engineer",
            name = "Инженер",
            description = "Инженер-программист",
            salary = 80000,
            expenses = 35000,
            taxes = 15000,
            education = "Высшее техническое"
        ),
        Profession(
            id = "doctor",
            name = "Врач",
            description = "Врач-терапевт",
            salary = 90000,
            expenses = 40000,
            taxes = 18000,
            education = "Высшее медицинское"
        ),
        Profession(
            id = "manager",
            name = "Менеджер",
            description = "Менеджер по продажам",
            salary = 60000,
            expenses = 30000,
            taxes = 12000,
            education = "Высшее экономическое"
        ),
        Profession(
            id = "mechanic",
            name = "Механик",
            description = "Автомеханик",
            salary = 50000,
            expenses = 28000,
            taxes = 9000,
            education = "Среднее специальное"
        ),
        Profession(
            id = "lawyer",
            name = "Юрист",
            description = "Юрист-консультант",
            salary = 75000,
            expenses = 38000,
            taxes = 14000,
            education = "Высшее юридическое"
        )
    )
    
    fun getDreams(): List<Dream> = listOf(
        Dream(
            id = "yacht",
            name = "Собственная яхта",
            description = "Роскошная яхта для путешествий",
            cost = 2000000,
            cashFlowRequired = 50000
        ),
        Dream(
            id = "restaurant",
            name = "Ресторан",
            description = "Открыть собственный ресторан",
            cost = 1500000,
            cashFlowRequired = 40000
        ),
        Dream(
            id = "charity",
            name = "Благотворительность",
            description = "Помочь детскому дому",
            cost = 500000,
            cashFlowRequired = 20000
        ),
        Dream(
            id = "island",
            name = "Частный остров",
            description = "Купить остров в тропиках",
            cost = 5000000,
            cashFlowRequired = 100000
        ),
        Dream(
            id = "space_trip",
            name = "Космический туризм",
            description = "Полёт в космос",
            cost = 3000000,
            cashFlowRequired = 75000
        ),
        Dream(
            id = "business_empire",
            name = "Бизнес-империя",
            description = "Создать сеть компаний",
            cost = 10000000,
            cashFlowRequired = 200000
        )
    )
    
    fun getSmallDeals(): List<Asset> = listOf(
        Asset(
            name = "Однокомнатная квартира в спальном районе",
            type = AssetType.REAL_ESTATE,
            downPayment = 300000,
            value = 1500000,
            cashFlow = 8000,
            loan = 1200000,
            loanPayment = 12000
        ),
        Asset(
            name = "Акции Сбербанка (10 штук)",
            type = AssetType.STOCKS,
            downPayment = 25000,
            value = 25000,
            cashFlow = 1200,
            shares = 10
        ),
        Asset(
            name = "Автомойка",
            type = AssetType.BUSINESS,
            downPayment = 150000,
            value = 500000,
            cashFlow = 15000,
            loan = 350000,
            loanPayment = 8000
        ),
        Asset(
            name = "Биткоин (0.1 BTC)",
            type = AssetType.CRYPTO,
            downPayment = 200000,
            value = 200000,
            cashFlow = 0
        ),
        Asset(
            name = "Облигации федерального займа",
            type = AssetType.BONDS,
            downPayment = 50000,
            value = 50000,
            cashFlow = 2500
        ),
        Asset(
            name = "Гараж в центре города",
            type = AssetType.REAL_ESTATE,
            downPayment = 100000,
            value = 400000,
            cashFlow = 5000,
            loan = 300000,
            loanPayment = 3500
        )
    )
    
    fun getBigDeals(): List<Asset> = listOf(
        Asset(
            name = "Торговый центр",
            type = AssetType.REAL_ESTATE,
            downPayment = 2000000,
            value = 10000000,
            cashFlow = 150000,
            loan = 8000000,
            loanPayment = 80000
        ),
        Asset(
            name = "IT-стартап",
            type = AssetType.BUSINESS,
            downPayment = 1000000,
            value = 5000000,
            cashFlow = 100000
        ),
        Asset(
            name = "Акции Газпрома (1000 штук)",
            type = AssetType.STOCKS,
            downPayment = 250000,
            value = 250000,
            cashFlow = 15000,
            shares = 1000
        ),
        Asset(
            name = "Жилой комплекс",
            type = AssetType.REAL_ESTATE,
            downPayment = 5000000,
            value = 25000000,
            cashFlow = 300000,
            loan = 20000000,
            loanPayment = 200000
        ),
        Asset(
            name = "Сеть кафе",
            type = AssetType.BUSINESS,
            downPayment = 800000,
            value = 3000000,
            cashFlow = 75000,
            loan = 2200000,
            loanPayment = 25000
        )
    )
    
    fun getRandomEvent(): String {
        val events = listOf(
            "Рыночная волатильность! Все ваши акции потеряли 10% стоимости.",
            "Повышение! Ваша зарплата увеличилась на 5000 рублей.",
            "Налоговая проверка. Доплатите 15000 рублей налогов.",
            "Удачная инвестиция! Получите 20000 рублей дополнительного дохода.",
            "Ремонт автомобиля. Потратьте 25000 рублей.",
            "Наследство от дальнего родственника. Получите 100000 рублей.",
            "Экономический кризис. Все доходы от недвижимости снижены на 20% на следующий ход.",
            "Новые инвестиционные возможности! Выберите дополнительную карточку актива."
        )
        return events.random()
    }
}