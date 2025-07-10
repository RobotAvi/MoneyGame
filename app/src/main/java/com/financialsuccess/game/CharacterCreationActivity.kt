package com.financialsuccess.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.financialsuccess.game.adapters.ProfessionAdapter
import com.financialsuccess.game.adapters.DreamAdapter
import com.financialsuccess.game.adapters.SkillAdapter
import com.financialsuccess.game.data.GameDataManager
import com.financialsuccess.game.databinding.ActivityCharacterCreationBinding
import com.financialsuccess.game.models.*
import android.app.DatePickerDialog
import java.util.Calendar

class CharacterCreationActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityCharacterCreationBinding
    private var selectedProfession: Profession? = null
    private var selectedDream: Dream? = null
    private var selectedStartDate: Long? = null
    private var selectedSkills: MutableList<Skill> = mutableListOf()
    private var selectedFinancialGoals: MutableList<FinancialGoal> = mutableListOf()
    
    // Данные персонажа
    private var playerName: String = ""
    private var playerAge: Int = 25
    private var educationLevel: EducationLevel = EducationLevel.BACHELOR
    private var workExperience: Int = 0
    private var maritalStatus: MaritalStatus = MaritalStatus.SINGLE
    private var childrenCount: Int = 0
    private var spouseIncome: Int = 0
    private var riskTolerance: RiskTolerance = RiskTolerance.MODERATE
    private var investmentStrategy: InvestmentStrategy = InvestmentStrategy.BALANCED
    private var savingsRate: Int = 10
    private var retirementAge: Int = 65
    private var targetPassiveIncome: Int = 100000
    private var healthLevel: HealthLevel = HealthLevel.GOOD
    private var stressLevel: StressLevel = StressLevel.LOW
    private var workLifeBalance: WorkLifeBalance = WorkLifeBalance.BALANCED
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCharacterCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        setupSpinners()
        setupRecyclerViews()
        setupListeners()
    }
    
    private fun setupUI() {
        // Устанавливаем значения по умолчанию
        binding.etPlayerName.setText("Игрок")
        binding.etAge.setText("25")
        binding.etWorkExperience.setText("0")
        binding.etChildrenCount.setText("0")
        binding.etSpouseIncome.setText("0")
        binding.etSavingsRate.setText("10")
        binding.etRetirementAge.setText("65")
        binding.etTargetPassiveIncome.setText("100000")
        
        // Настройка DatePicker
        binding.etStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedStartDate = calendar.timeInMillis
                    binding.etStartDate.setText("%02d.%02d.%d".format(dayOfMonth, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
    }
    
    private fun setupSpinners() {
        // Образование
        val educationAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            EducationLevel.values().map { it.displayName }
        )
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEducation.adapter = educationAdapter
        binding.spinnerEducation.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                educationLevel = EducationLevel.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Семейное положение
        val maritalAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            MaritalStatus.values().map { it.displayName }
        )
        maritalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMaritalStatus.adapter = maritalAdapter
        binding.spinnerMaritalStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                maritalStatus = MaritalStatus.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Толерантность к риску
        val riskAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            RiskTolerance.values().map { it.displayName }
        )
        riskAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRiskTolerance.adapter = riskAdapter
        binding.spinnerRiskTolerance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                riskTolerance = RiskTolerance.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Стратегия инвестиций
        val investmentAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            InvestmentStrategy.values().map { it.displayName }
        )
        investmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerInvestmentStrategy.adapter = investmentAdapter
        binding.spinnerInvestmentStrategy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                investmentStrategy = InvestmentStrategy.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Уровень здоровья
        val healthAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            HealthLevel.values().map { it.displayName }
        )
        healthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerHealthLevel.adapter = healthAdapter
        binding.spinnerHealthLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                healthLevel = HealthLevel.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Уровень стресса
        val stressAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            StressLevel.values().map { it.displayName }
        )
        stressAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStressLevel.adapter = stressAdapter
        binding.spinnerStressLevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                stressLevel = StressLevel.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        
        // Баланс работы и жизни
        val balanceAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            WorkLifeBalance.values().map { it.displayName }
        )
        balanceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerWorkLifeBalance.adapter = balanceAdapter
        binding.spinnerWorkLifeBalance.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                workLifeBalance = WorkLifeBalance.values()[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
    
    private fun setupRecyclerViews() {
        // Профессии
        val professions = GameDataManager.getProfessions()
        val professionAdapter = ProfessionAdapter(professions) { profession ->
            selectedProfession = profession
            updateStartButtonState()
        }
        
        binding.recyclerViewProfessions.apply {
            layoutManager = LinearLayoutManager(this@CharacterCreationActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = professionAdapter
        }
        
        // Мечты
        val dreams = GameDataManager.getDreams()
        val dreamAdapter = DreamAdapter(dreams) { dream ->
            selectedDream = dream
            updateStartButtonState()
        }
        
        binding.recyclerViewDreams.apply {
            layoutManager = LinearLayoutManager(this@CharacterCreationActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = dreamAdapter
        }
        
        // Навыки
        val skills = getAvailableSkills()
        val skillAdapter = SkillAdapter(skills) { skill ->
            if (selectedSkills.contains(skill)) {
                selectedSkills.remove(skill)
            } else {
                if (selectedSkills.size < 3) { // Максимум 3 навыка
                    selectedSkills.add(skill)
                }
            }
            skillAdapter.notifyDataSetChanged()
        }
        
        binding.recyclerViewSkills.apply {
            layoutManager = LinearLayoutManager(this@CharacterCreationActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = skillAdapter
        }
    }
    
    private fun setupListeners() {
        binding.btnStartGame.setOnClickListener {
            if (validateInput()) {
                createPlayerAndStartGame()
            }
        }
        
        binding.btnAddGoal.setOnClickListener {
            addFinancialGoal()
        }
    }
    
    private fun validateInput(): Boolean {
        // Проверка имени
        playerName = binding.etPlayerName.text?.toString()?.trim() ?: ""
        if (playerName.isEmpty()) {
            binding.etPlayerName.error = "Введите имя персонажа"
            return false
        }
        
        // Проверка возраста
        val ageText = binding.etAge.text.toString()
        playerAge = ageText.toIntOrNull() ?: 0
        if (playerAge < 18 || playerAge > 65) {
            binding.etAge.error = "Возраст должен быть от 18 до 65 лет"
            return false
        }
        
        // Проверка опыта работы
        val experienceText = binding.etWorkExperience.text.toString()
        workExperience = experienceText.toIntOrNull() ?: 0
        if (workExperience < 0 || workExperience > 47) { // Максимум 47 лет опыта (65-18)
            binding.etWorkExperience.error = "Опыт работы должен быть от 0 до ${65 - playerAge} лет"
            return false
        }
        
        // Проверка количества детей
        val childrenText = binding.etChildrenCount.text.toString()
        childrenCount = childrenText.toIntOrNull() ?: 0
        if (childrenCount < 0 || childrenCount > 10) {
            binding.etChildrenCount.error = "Количество детей должно быть от 0 до 10"
            return false
        }
        
        // Проверка дохода супруга
        val spouseText = binding.etSpouseIncome.text.toString()
        spouseIncome = spouseText.toIntOrNull() ?: 0
        if (spouseIncome < 0) {
            binding.etSpouseIncome.error = "Доход супруга не может быть отрицательным"
            return false
        }
        
        // Проверка процента сбережений
        val savingsText = binding.etSavingsRate.text.toString()
        savingsRate = savingsText.toIntOrNull() ?: 0
        if (savingsRate < 0 || savingsRate > 50) {
            binding.etSavingsRate.error = "Процент сбережений должен быть от 0 до 50"
            return false
        }
        
        // Проверка возраста выхода на пенсию
        val retirementText = binding.etRetirementAge.text.toString()
        retirementAge = retirementText.toIntOrNull() ?: 0
        if (retirementAge < playerAge || retirementAge > 80) {
            binding.etRetirementAge.error = "Возраст выхода на пенсию должен быть от $playerAge до 80 лет"
            return false
        }
        
        // Проверка целевого пассивного дохода
        val targetText = binding.etTargetPassiveIncome.text.toString()
        targetPassiveIncome = targetText.toIntOrNull() ?: 0
        if (targetPassiveIncome < 0) {
            binding.etTargetPassiveIncome.error = "Целевой пассивный доход не может быть отрицательным"
            return false
        }
        
        // Проверка выбора профессии и мечты
        if (selectedProfession == null) {
            // Показать ошибку
            return false
        }
        
        if (selectedDream == null) {
            // Показать ошибку
            return false
        }
        
        if (selectedStartDate == null) {
            binding.etStartDate.error = "Выберите дату старта"
            return false
        }
        
        return true
    }
    
    private fun createPlayerAndStartGame() {
        // Создаем игрока с расширенными параметрами
        val player = Player(
            name = playerName,
            age = playerAge,
            startDateMillis = selectedStartDate,
            profession = selectedProfession,
            dream = selectedDream,
            education = educationLevel,
            workExperience = workExperience,
            skills = selectedSkills,
            maritalStatus = maritalStatus,
            childrenCount = childrenCount,
            spouseIncome = spouseIncome,
            riskTolerance = riskTolerance,
            investmentStrategy = investmentStrategy,
            savingsRate = savingsRate,
            financialGoals = selectedFinancialGoals,
            retirementAge = retirementAge,
            targetPassiveIncome = targetPassiveIncome,
            healthLevel = healthLevel,
            stressLevel = stressLevel,
            workLifeBalance = workLifeBalance
        )
        
        // Устанавливаем случайный возраст смерти
        player.setRandomDeathAge()
        
        // Обновляем зарплату с учетом всех бонусов
        player.updateSalaryWithBonuses()
        
        // Обновляем расходы с учетом семьи
        player.updateTotalExpenses()
        
        // Устанавливаем начальный капитал
        player.cash = 5000 // Базовый стартовый капитал
        
        // Запускаем игру
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra("player", player)
        }
        startActivity(intent)
        finish()
    }
    
    private fun getAvailableSkills(): List<Skill> {
        return listOf(
            Skill("Программирование", "Навыки разработки ПО", 15000, SkillCategory.TECHNICAL),
            Skill("Менеджмент", "Управление командой", 12000, SkillCategory.LEADERSHIP),
            Skill("Продажи", "Искусство продаж", 10000, SkillCategory.SOFT_SKILLS),
            Skill("Аналитика", "Анализ данных", 13000, SkillCategory.TECHNICAL),
            Skill("Коммуникация", "Эффективное общение", 8000, SkillCategory.SOFT_SKILLS),
            Skill("Финансовая грамотность", "Управление финансами", 11000, SkillCategory.FINANCIAL),
            Skill("Креативность", "Творческое мышление", 9000, SkillCategory.CREATIVE),
            Skill("Переговоры", "Искусство переговоров", 14000, SkillCategory.SOFT_SKILLS),
            Skill("Планирование", "Стратегическое планирование", 10000, SkillCategory.LEADERSHIP),
            Skill("Адаптивность", "Быстрая адаптация", 7000, SkillCategory.SOFT_SKILLS)
        )
    }
    
    private fun addFinancialGoal() {
        // Здесь можно добавить диалог для создания финансовой цели
        // Пока просто добавляем базовую цель
        val goal = FinancialGoal(
            name = "Пассивный доход ${targetPassiveIncome}₽",
            description = "Достичь пассивного дохода ${targetPassiveIncome}₽ в месяц",
            type = GoalType.PASSIVE_INCOME,
            targetAmount = targetPassiveIncome,
            deadline = (retirementAge - playerAge) * 12
        )
        selectedFinancialGoals.add(goal)
    }
    
    private fun updateStartButtonState() {
        binding.btnStartGame.isEnabled = selectedProfession != null && selectedDream != null
    }
}