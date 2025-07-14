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
import com.financialsuccess.game.adapters.FinancialGoalAdapter
import com.financialsuccess.game.adapters.SkillAdapter
import com.financialsuccess.game.data.GameDataManager
import com.financialsuccess.game.data.FinancialTemplates
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
    private lateinit var goalAdapter: FinancialGoalAdapter
    
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
    
    // Финансовая информация
    private var salary: Int = 0
    private var additionalIncome: Int = 0
    private var foodExpenses: Int = 0
    private var transportExpenses: Int = 0
    private var housingExpenses: Int = 0
    private var otherExpenses: Int = 0
    private var initialCapital: Int = 5000
    
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
        
        // Инициализация финансовых полей
        binding.etSalary.setText("0")
        binding.etAdditionalIncome.setText("0")
        binding.etFoodExpenses.setText("0")
        binding.etTransportExpenses.setText("0")
        binding.etHousingExpenses.setText("0")
        binding.etOtherExpenses.setText("0")
        binding.etInitialCapital.setText("5000")
        
        // Настройка DatePicker
        binding.etStartDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            // Устанавливаем разумную дату по умолчанию (текущий год, май, 15-е число)
            val currentYear = calendar.get(Calendar.YEAR)
            calendar.set(currentYear, Calendar.MAY, 15)
            
            val datePicker = DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    // Устанавливаем время на 00:00:00 для избежания проблем с часовыми поясами
                    calendar.set(Calendar.HOUR_OF_DAY, 0)
                    calendar.set(Calendar.MINUTE, 0)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
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
            updateFinancialFieldsFromProfession(profession)
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
        val skillAdapter = SkillAdapter(skills, selectedSkills) { skill ->
            // Уведомляем адаптер об изменениях
            (binding.recyclerViewSkills.adapter as? SkillAdapter)?.notifyDataSetChanged()
        }
        
        binding.recyclerViewSkills.apply {
            layoutManager = LinearLayoutManager(this@CharacterCreationActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = skillAdapter
        }
        
        // Финансовые цели
        goalAdapter = FinancialGoalAdapter(selectedFinancialGoals) { position ->
            selectedFinancialGoals.removeAt(position)
            goalAdapter.removeGoal(position)
            updateGoalsVisibility()
        }
        
        binding.recyclerViewGoals.apply {
            layoutManager = LinearLayoutManager(this@CharacterCreationActivity, LinearLayoutManager.VERTICAL, false)
            adapter = goalAdapter
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
        
        binding.btnResetFinancial.setOnClickListener {
            selectedProfession?.let { profession ->
                updateFinancialFieldsFromProfession(profession)
            } ?: run {
                com.google.android.material.snackbar.Snackbar.make(
                    binding.root,
                    "Сначала выберите профессию",
                    com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                ).show()
            }
        }
        
        binding.btnLoadTemplate.setOnClickListener {
            showTemplateSelectionDialog()
        }
        
        binding.btnSaveTemplate.setOnClickListener {
            showSaveTemplateDialog()
        }
        
        // Слушатели для обновления финансовой сводки
        binding.etSalary.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etAdditionalIncome.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etFoodExpenses.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etTransportExpenses.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etHousingExpenses.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etOtherExpenses.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etSpouseIncome.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etChildrenCount.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
        
        binding.etSavingsRate.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: android.text.Editable?) {
                updateFinancialSummary()
            }
        })
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
        
        // Валидация финансовой информации
        val salaryText = binding.etSalary.text.toString()
        salary = salaryText.toIntOrNull() ?: 0
        if (salary < 0) {
            binding.etSalary.error = "Зарплата не может быть отрицательной"
            return false
        }
        
        val additionalIncomeText = binding.etAdditionalIncome.text.toString()
        additionalIncome = additionalIncomeText.toIntOrNull() ?: 0
        if (additionalIncome < 0) {
            binding.etAdditionalIncome.error = "Дополнительные доходы не могут быть отрицательными"
            return false
        }
        
        val foodText = binding.etFoodExpenses.text.toString()
        foodExpenses = foodText.toIntOrNull() ?: 0
        if (foodExpenses < 0) {
            binding.etFoodExpenses.error = "Расходы на питание не могут быть отрицательными"
            return false
        }
        
        val transportText = binding.etTransportExpenses.text.toString()
        transportExpenses = transportText.toIntOrNull() ?: 0
        if (transportExpenses < 0) {
            binding.etTransportExpenses.error = "Расходы на транспорт не могут быть отрицательными"
            return false
        }
        
        val housingText = binding.etHousingExpenses.text.toString()
        housingExpenses = housingText.toIntOrNull() ?: 0
        if (housingExpenses < 0) {
            binding.etHousingExpenses.error = "Расходы на жилье не могут быть отрицательными"
            return false
        }
        
        val otherText = binding.etOtherExpenses.text.toString()
        otherExpenses = otherText.toIntOrNull() ?: 0
        if (otherExpenses < 0) {
            binding.etOtherExpenses.error = "Прочие расходы не могут быть отрицательными"
            return false
        }
        
        val initialCapitalText = binding.etInitialCapital.text.toString()
        initialCapital = initialCapitalText.toIntOrNull() ?: 5000
        if (initialCapital < 0) {
            binding.etInitialCapital.error = "Начальный капитал не может быть отрицательным"
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
            profession = selectedProfession!!,
            dream = selectedDream!!,
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
        
        // Устанавливаем финансовые данные, введенные пользователем
        player.salary = salary
        player.foodExpenses = foodExpenses
        player.transportExpenses = transportExpenses
        player.housingExpenses = housingExpenses
        player.otherExpenses = otherExpenses
        
        // Обновляем общий доход с учетом дополнительных доходов
        player.updateTotalIncome()
        player.totalIncome += additionalIncome
        
        // Обновляем общие расходы с учетом введенных данных
        player.updateTotalExpenses()
        
        // Устанавливаем начальный капитал
        player.cash = initialCapital
        
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
        // Получаем текущие значения из полей
        val targetIncome = binding.etTargetPassiveIncome.text.toString().toIntOrNull() ?: targetPassiveIncome
        val retirement = binding.etRetirementAge.text.toString().toIntOrNull() ?: retirementAge
        val age = binding.etAge.text.toString().toIntOrNull() ?: playerAge
        
        // Создаем финансовую цель
        val goal = FinancialGoal(
            name = "Пассивный доход ${targetIncome}₽",
            description = "Достичь пассивного дохода ${targetIncome}₽ в месяц",
            type = GoalType.PASSIVE_INCOME,
            targetAmount = targetIncome,
            deadline = (retirement - age) * 12
        )
        
        // Добавляем цель в список и адаптер
        selectedFinancialGoals.add(goal)
        goalAdapter.addGoal(goal)
        
        // Показываем RecyclerView с целями
        updateGoalsVisibility()
        
        // Показываем обратную связь пользователю
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            "Цель добавлена!",
            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show()
        // Прокручиваем к списку целей, чтобы пользователь увидел результат
        binding.recyclerViewGoals.post {
            binding.recyclerViewGoals.smoothScrollToPosition(selectedFinancialGoals.size - 1)
        }
        binding.btnAddGoal.text = "Добавить цель"
    }
    
    private fun updateGoalsVisibility() {
        if (selectedFinancialGoals.isNotEmpty()) {
            binding.recyclerViewGoals.visibility = View.VISIBLE
        } else {
            binding.recyclerViewGoals.visibility = View.GONE
        }
    }
    
    private fun updateStartButtonState() {
        binding.btnStartGame.isEnabled = selectedProfession != null && selectedDream != null
    }
    
    private fun updateFinancialFieldsFromProfession(profession: Profession) {
        // Автоматически заполняем поля на основе выбранной профессии
        binding.etSalary.setText(profession.salary.toString())
        
        // Распределяем расходы профессии по категориям
        val totalExpenses = profession.expenses
        binding.etFoodExpenses.setText((totalExpenses * 0.4).toInt().toString())
        binding.etTransportExpenses.setText((totalExpenses * 0.15).toInt().toString())
        binding.etHousingExpenses.setText((totalExpenses * 0.3).toInt().toString())
        binding.etOtherExpenses.setText((totalExpenses * 0.15).toInt().toString())
        
        // Обновляем сводку
        updateFinancialSummary()
        
        // Показываем пользователю, что поля заполнены автоматически
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            "Финансовые поля заполнены на основе выбранной профессии. Вы можете изменить их вручную.",
            com.google.android.material.snackbar.Snackbar.LENGTH_LONG
        ).show()
    }
    
    private fun updateFinancialSummary() {
        try {
            val salary = binding.etSalary.text.toString().toIntOrNull() ?: 0
            val additionalIncome = binding.etAdditionalIncome.text.toString().toIntOrNull() ?: 0
            val foodExpenses = binding.etFoodExpenses.text.toString().toIntOrNull() ?: 0
            val transportExpenses = binding.etTransportExpenses.text.toString().toIntOrNull() ?: 0
            val housingExpenses = binding.etHousingExpenses.text.toString().toIntOrNull() ?: 0
            val otherExpenses = binding.etOtherExpenses.text.toString().toIntOrNull() ?: 0
            val spouseIncome = binding.etSpouseIncome.text.toString().toIntOrNull() ?: 0
            val childrenCount = binding.etChildrenCount.text.toString().toIntOrNull() ?: 0
            val savingsRate = binding.etSavingsRate.text.toString().toIntOrNull() ?: 10
            
            val totalIncome = salary + additionalIncome + spouseIncome
            val totalExpenses = foodExpenses + transportExpenses + housingExpenses + otherExpenses + (childrenCount * 8000)
            val cashFlow = totalIncome - totalExpenses
            val monthlySavings = (totalIncome * savingsRate) / 100
            
            val summary = """
                💰 Общий доход: ${totalIncome}₽/мес
                📊 Общие расходы: ${totalExpenses}₽/мес
                💵 Денежный поток: ${cashFlow}₽/мес
                💾 Ежемесячные сбережения: ${monthlySavings}₽/мес
                
                📈 Доходы:
                • Зарплата: ${salary}₽
                • Доп. доходы: ${additionalIncome}₽
                • Доход супруга: ${spouseIncome}₽
                
                📉 Расходы:
                • Питание: ${foodExpenses}₽
                • Транспорт: ${transportExpenses}₽
                • Жилье: ${housingExpenses}₽
                • Прочие: ${otherExpenses}₽
                • Дети: ${childrenCount * 8000}₽
            """.trimIndent()
            
            binding.tvFinancialSummary.text = summary
        } catch (e: Exception) {
            binding.tvFinancialSummary.text = "Ошибка при расчете сводки"
        }
    }
    
    private fun showTemplateSelectionDialog() {
        val templates = FinancialTemplates.getDefaultTemplates() + FinancialTemplates.getTemplates(this)
        val templateNames = templates.map { it.name }.toTypedArray()
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Выберите финансовый шаблон")
            .setItems(templateNames) { _, which ->
                val selectedTemplate = templates[which]
                applyTemplate(selectedTemplate)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun showSaveTemplateDialog() {
        val input = android.widget.EditText(this)
        input.hint = "Название шаблона"
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Сохранить финансовый шаблон")
            .setView(input)
            .setPositiveButton("Сохранить") { _, _ ->
                val templateName = input.text.toString().trim()
                if (templateName.isNotEmpty()) {
                    saveCurrentAsTemplate(templateName)
                } else {
                    com.google.android.material.snackbar.Snackbar.make(
                        binding.root,
                        "Введите название шаблона",
                        com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
    
    private fun applyTemplate(template: com.financialsuccess.game.data.FinancialTemplate) {
        binding.etSalary.setText(template.salary.toString())
        binding.etAdditionalIncome.setText(template.additionalIncome.toString())
        binding.etFoodExpenses.setText(template.foodExpenses.toString())
        binding.etTransportExpenses.setText(template.transportExpenses.toString())
        binding.etHousingExpenses.setText(template.housingExpenses.toString())
        binding.etOtherExpenses.setText(template.otherExpenses.toString())
        
        updateFinancialSummary()
        
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            "Шаблон '${template.name}' применен",
            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show()
    }
    
    private fun saveCurrentAsTemplate(templateName: String) {
        val salary = binding.etSalary.text.toString().toIntOrNull() ?: 0
        val additionalIncome = binding.etAdditionalIncome.text.toString().toIntOrNull() ?: 0
        val foodExpenses = binding.etFoodExpenses.text.toString().toIntOrNull() ?: 0
        val transportExpenses = binding.etTransportExpenses.text.toString().toIntOrNull() ?: 0
        val housingExpenses = binding.etHousingExpenses.text.toString().toIntOrNull() ?: 0
        val otherExpenses = binding.etOtherExpenses.text.toString().toIntOrNull() ?: 0
        
        val template = com.financialsuccess.game.data.FinancialTemplate(
            name = templateName,
            salary = salary,
            additionalIncome = additionalIncome,
            foodExpenses = foodExpenses,
            transportExpenses = transportExpenses,
            housingExpenses = housingExpenses,
            otherExpenses = otherExpenses,
            description = "Пользовательский шаблон"
        )
        
        FinancialTemplates.saveTemplate(this, template)
        
        com.google.android.material.snackbar.Snackbar.make(
            binding.root,
            "Шаблон '${templateName}' сохранен",
            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
        ).show()
    }
}