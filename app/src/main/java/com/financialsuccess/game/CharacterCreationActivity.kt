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
import android.widget.FrameLayout
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.speech.RecognizerIntent
import android.content.ActivityNotFoundException

class CharacterCreationActivity : AppCompatActivity() {
    
    private lateinit var stepContainer: FrameLayout
    private var currentStep = 1
    private val totalSteps = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_character_creation)
        stepContainer = findViewById(R.id.stepContainer)
        showStep(currentStep)
    }

    private fun showStep(step: Int) {
        stepContainer.removeAllViews()
        when (step) {
            1 -> showProfessionStep()
            2 -> showDreamStep()
            3 -> showAgeStep()
            4 -> showNameStep()
            5 -> showDateStep()
            6 -> showStartScreen()
        }
    }

    private fun showProfessionStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_profession, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 1/5"
        val btnChoose = view.findViewById<Button>(R.id.btnChooseProfession)
        btnChoose.setOnClickListener {
            // TODO: получить выбранную профессию из UI
            // selectedProfession = ...
            currentStep++
            showStep(currentStep)
        }
        stepContainer.addView(view)
    }

    private fun showDreamStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_dream, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 2/5"
        val btnChoose = view.findViewById<Button>(R.id.btnChooseDream)
        btnChoose.setOnClickListener {
            // TODO: получить выбранную мечту из UI
            // selectedDream = ...
            currentStep++
            showStep(currentStep)
        }
        stepContainer.addView(view)
    }

    private fun showAgeStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_age, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 3/5"
        val numberPicker = view.findViewById<NumberPicker>(R.id.numberPickerAge)
        numberPicker.minValue = 18
        numberPicker.maxValue = 65
        numberPicker.value = playerAge
        val btnNext = view.findViewById<Button>(R.id.btnNextAge)
        btnNext.setOnClickListener {
            playerAge = numberPicker.value
            currentStep++
            showStep(currentStep)
        }
        stepContainer.addView(view)
    }

    private fun showNameStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_name, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 4/5"
        val etName = view.findViewById<android.widget.EditText>(R.id.etPlayerName)
        etName.setText(playerName)
        val btnNext = view.findViewById<Button>(R.id.btnNextName)
        btnNext.setOnClickListener {
            playerName = etName.text.toString().trim()
            if (playerName.isEmpty()) {
                etName.error = "Введите имя персонажа"
            } else {
                currentStep++
                showStep(currentStep)
            }
        }
        stepContainer.addView(view)
    }

    private fun showDateStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_date, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 5/5"
        val tvSelectedDate = view.findViewById<TextView>(R.id.tvSelectedDate)
        val btnPickDate = view.findViewById<Button>(R.id.btnPickDate)
        val btnNext = view.findViewById<Button>(R.id.btnNextDate)
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val datePicker = DatePickerDialog(this,
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth, 0, 0, 0)
                    calendar.set(Calendar.MILLISECOND, 0)
                    selectedStartDate = calendar.timeInMillis
                    tvSelectedDate.text = "%02d.%02d.%d".format(dayOfMonth, month + 1, year)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }
        btnNext.setOnClickListener {
            if (selectedStartDate == null) {
                tvSelectedDate.error = "Выберите дату старта"
            } else {
                currentStep++
                showStep(currentStep)
            }
        }
        stepContainer.addView(view)
    }

    private fun showStartScreen() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_start, stepContainer, false)
        stepContainer.addView(view)
        // TODO: анимация и переход к игре
    }
    
    // Удалены: binding, setupUI, setupSpinners, setupRecyclerViews, setupListeners, validateInput, и все методы, использующие binding.*
    
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