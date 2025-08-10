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
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.graphics.Typeface
import android.text.TextUtils
import java.text.NumberFormat
import java.util.Locale
import android.speech.tts.TextToSpeech
import android.widget.Toast

class CharacterCreationActivity : AppCompatActivity() {
    // Переменные для сбора данных персонажа
    private var playerName: String = ""
    private var playerAge: Int = 25
    private var selectedStartDate: Long? = null
    private var selectedProfession: Profession? = null
    private var selectedDream: Dream? = null

    // Карусель профессий
    private val professions = GameDataManager.getProfessions()
    private var professionIndex = 0
    // Карусель мечт
    private val dreams = GameDataManager.getDreams()
    private var dreamIndex = 0

    private lateinit var stepContainer: FrameLayout
    private var currentStep = 1
    private val totalSteps = 5

    // TTS для голосового приветствия
    private var textToSpeech: TextToSpeech? = null

    private val currencyFormat: NumberFormat by lazy {
        NumberFormat.getInstance(Locale("ru")).apply {
            maximumFractionDigits = 0
        }
    }

    private fun money(n: Int): String = "${currencyFormat.format(n)} ₽"

    private val REQUEST_CODE_VOICE_INPUT: Int = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra("custom", false)) {
            setContentView(R.layout.activity_character_creation)
            setupCustomUI()
        } else {
            setContentView(R.layout.activity_character_wizard)
            stepContainer = findViewById(R.id.stepContainer)
            showStep(currentStep)
            updateProgressDots()
        }

        // Инициализация TTS (русская локаль, попытка выбрать женский голос, если доступен)
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val localeRu = Locale("ru", "RU")
                val result = textToSpeech?.setLanguage(localeRu)
                val availableVoices = textToSpeech?.voices
                val femaleRuVoice = availableVoices?.firstOrNull { v ->
                    v.locale.language == "ru" && (
                        v.name.contains("female", ignoreCase = true) ||
                        v.features?.contains("female") == true
                    )
                }
                if (femaleRuVoice != null) {
                    textToSpeech?.voice = femaleRuVoice
                }
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // Локаль недоступна — используем системную по умолчанию
                }
                textToSpeech?.setPitch(1.0f)
                textToSpeech?.setSpeechRate(1.0f)
            }
        }
    }

    // Добавлено: метод переключения шагов мастера
    private fun showStep(step: Int) {
        if (!::stepContainer.isInitialized) return
        stepContainer.removeAllViews()
        when (step) {
            1 -> showNameStep()        // Имя
            2 -> showAgeStep()         // Возраст
            3 -> showDateStep()        // Дата
            4 -> showProfessionStep()  // Профессия
            5 -> showDreamStep()       // Мечта
            else -> showStartScreen()
        }
    }

    private fun updateProgressDots() {
        val dots = listOf<View>(
            findViewById(R.id.dot1),
            findViewById(R.id.dot2),
            findViewById(R.id.dot3),
            findViewById(R.id.dot4),
            findViewById(R.id.dot5)
        )
        dots.forEachIndexed { index, view ->
            val active = (index + 1) == currentStep
            view.setBackgroundResource(if (active) R.drawable.progress_dot_active else R.drawable.progress_dot_inactive)
        }
    }

    private fun buildColoredParams(lines: List<Triple<String, Pair<String, Int>, Int>>): CharSequence {
        val builder = SpannableStringBuilder()
        lines.forEachIndexed { index, triple ->
            val label = triple.first
            val value = triple.second.first
            val valueColor = triple.second.second
            val labelColor = triple.third

            val startLabel = builder.length
            builder.append(label)
            val endLabel = builder.length
            builder.setSpan(ForegroundColorSpan(labelColor), startLabel, endLabel, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            builder.append(": ")

            val startValue = builder.length
            builder.append(value)
            val endValue = builder.length
            builder.setSpan(ForegroundColorSpan(valueColor), startValue, endValue, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            builder.setSpan(StyleSpan(Typeface.BOLD), startValue, endValue, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            if (index != lines.lastIndex) builder.append("\n")
        }
        return builder
    }

    private fun fadeOutSwipeHint(tv: TextView) {
        tv.animate().alpha(0f).setDuration(600).withEndAction { tv.visibility = View.GONE }.start()
    }

    private fun showProfessionStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_profession, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 4/5"
        val ivPhoto = view.findViewById<ImageView>(R.id.ivProfessionPhoto)
        val tvName = view.findViewById<TextView>(R.id.tvProfessionName)
        val tvParams = view.findViewById<TextView>(R.id.tvProfessionParams)
        val tvSwipeHint = view.findViewById<TextView>(R.id.tvSwipeHint)
        val btnLeft = view.findViewById<Button>(R.id.btnSwipeLeft)
        val btnRight = view.findViewById<Button>(R.id.btnSwipeRight)
        val btnChoose = view.findViewById<Button>(R.id.btnChooseProfession)
        val professionCard = view.findViewById<View>(R.id.professionCard)

        fun updateProfessionUI() {
            val prof = professions[professionIndex]
            val resId = resources.getIdentifier(
                "profession_${prof.id}", "drawable", packageName
            )
            if (resId != 0) ivPhoto.setImageResource(resId)
            else ivPhoto.setImageResource(R.drawable.ic_profession_placeholder)
            tvName.text = prof.name

            val labelColor = resources.getColor(R.color.text_secondary, null)
            val salaryColor = resources.getColor(R.color.money_green, null)
            val expenseColor = resources.getColor(R.color.expense_red, null)
            val taxColor = resources.getColor(R.color.liability_orange, null)
            val educationColor = resources.getColor(R.color.asset_blue, null)

            val colored = buildColoredParams(
                listOf(
                    Triple("Зарплата", Pair(money(prof.salary), salaryColor), labelColor),
                    Triple("Расходы", Pair(money(prof.expenses), expenseColor), labelColor),
                    Triple("Налоги", Pair(money(prof.taxes), taxColor), labelColor),
                    Triple("Образование", Pair(prof.education, educationColor), labelColor)
                )
            )
            tvParams.text = TextUtils.concat(prof.description, "\n", colored)
            tvParams.setLineSpacing(8f, 1f)
        }
        updateProfessionUI()
        professionCard.setOnTouchListener(object : View.OnTouchListener {
            private var x1 = 0f
            private var x2 = 0f
            override fun onTouch(v: View?, event: android.view.MotionEvent?): Boolean {
                when (event?.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        x1 = event.x
                        v?.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    android.view.MotionEvent.ACTION_UP -> {
                        x2 = event.x
                        val deltaX = x2 - x1
                        if (deltaX > 100) btnLeft.performClick()
                        if (deltaX < -100) btnRight.performClick()
                        v?.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                return true
            }
        })
        btnChoose.setOnClickListener {
            selectedProfession = professions[professionIndex]
            currentStep++
            showStep(currentStep)
            updateProgressDots()
        }
        
        // Добавляем обработчики для кнопок влево/вправо
        btnLeft.setOnClickListener {
            professionIndex = (professionIndex - 1 + professions.size) % professions.size
            updateProfessionUI()
            fadeOutSwipeHint(tvSwipeHint)
        }
        
        btnRight.setOnClickListener {
            professionIndex = (professionIndex + 1) % professions.size
            updateProfessionUI()
            fadeOutSwipeHint(tvSwipeHint)
        }
        
        btnChoose.textSize = 16f
        btnChoose.minWidth = 220
        stepContainer.addView(view)
    }

    private fun showDreamStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_dream, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 5/5"
        val ivPhoto = view.findViewById<ImageView>(R.id.ivDreamPhoto)
        val tvName = view.findViewById<TextView>(R.id.tvDreamName)
        val tvParams = view.findViewById<TextView>(R.id.tvDreamParams)
        val tvSwipeHint = view.findViewById<TextView>(R.id.tvSwipeHint)
        val btnLeft = view.findViewById<Button>(R.id.btnSwipeLeft)
        val btnRight = view.findViewById<Button>(R.id.btnSwipeRight)
        val btnChoose = view.findViewById<Button>(R.id.btnChooseDream)
        val dreamCard = view.findViewById<View>(R.id.dreamCard)

        fun updateDreamUI() {
            val dream = dreams[dreamIndex]
            val resId = resources.getIdentifier(
                "dream_${dream.id}", "drawable", packageName
            )
            if (resId != 0) ivPhoto.setImageResource(resId)
            else ivPhoto.setImageResource(R.drawable.ic_dream_placeholder)
            tvName.text = dream.name

            val labelColor = resources.getColor(R.color.text_secondary, null)
            val costColor = resources.getColor(R.color.expense_red, null)
            val incomeColor = resources.getColor(R.color.money_green, null)
            val diceColor = resources.getColor(R.color.asset_blue, null)

            val colored = buildColoredParams(
                listOf(
                    Triple("Стоимость", Pair(money(dream.cost), costColor), labelColor),
                    Triple("Пассивный доход", Pair(money(dream.cashFlowRequired), incomeColor), labelColor),
                    Triple("Число на кубике", Pair(dream.fastTrackNumber.toString(), diceColor), labelColor)
                )
            )
            tvParams.text = colored
            tvParams.setLineSpacing(8f, 1f)
        }
        updateDreamUI()
        btnLeft.setOnClickListener {
            dreamIndex = (dreamIndex - 1 + dreams.size) % dreams.size
            updateDreamUI()
            fadeOutSwipeHint(tvSwipeHint)
        }
        btnRight.setOnClickListener {
            dreamIndex = (dreamIndex + 1) % dreams.size
            updateDreamUI()
            fadeOutSwipeHint(tvSwipeHint)
        }
        dreamCard.setOnTouchListener(object : View.OnTouchListener {
            private var x1 = 0f
            private var x2 = 0f
            override fun onTouch(v: View?, event: android.view.MotionEvent?): Boolean {
                when (event?.action) {
                    android.view.MotionEvent.ACTION_DOWN -> {
                        x1 = event.x
                        v?.parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    android.view.MotionEvent.ACTION_UP -> {
                        x2 = event.x
                        val deltaX = x2 - x1
                        if (deltaX > 100) btnLeft.performClick()
                        if (deltaX < -100) btnRight.performClick()
                        v?.parent?.requestDisallowInterceptTouchEvent(false)
                    }
                }
                return true
            }
        })
        btnChoose.setOnClickListener {
            selectedDream = dreams[dreamIndex]
            currentStep++
            showStep(currentStep)
            updateProgressDots()
        }
        btnChoose.textSize = 16f
        btnChoose.minWidth = 220
        stepContainer.addView(view)
    }

    private fun showAgeStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_age, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 2/5"
        val numberPicker = view.findViewById<NumberPicker>(R.id.numberPickerAge)
        numberPicker.minValue = 18
        numberPicker.maxValue = 65
        numberPicker.value = playerAge

        // Set white for all numbers by default with consistent size
        val defaultTextColor = resources.getColor(R.color.white, null)
        val selectedTextColor = resources.getColor(R.color.error_color, null) // red for selected
        val childCount = numberPicker.childCount
        for (i in 0 until childCount) {
            val child = numberPicker.getChildAt(i)
            if (child is TextView) {
                child.setTextColor(defaultTextColor)
                child.textSize = 40f
            }
        }
        // Also set selector wheel paint (for non-selected numbers) to white
        try {
            val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
            selectorWheelPaintField.isAccessible = true
            val paint = selectorWheelPaintField.get(numberPicker) as android.graphics.Paint
            paint.color = defaultTextColor
            numberPicker.invalidate()
        } catch (e: Exception) { }

        // Listen for value changes to highlight the selected number in red
        numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
            for (i in 0 until picker.childCount) {
                val child = picker.getChildAt(i)
                if (child is TextView) {
                    // Set all numbers to white with consistent size
                    child.setTextColor(defaultTextColor)
                    child.textSize = 40f
                }
            }
            try {
                val field = NumberPicker::class.java.getDeclaredField("mInputText")
                field.isAccessible = true
                val inputText = field.get(picker) as TextView
                inputText.setTextColor(selectedTextColor)
                inputText.textSize = 40f
            } catch (e: Exception) {
                // fallback: no-op if reflection fails
            }
            // Ensure wheel paint stays white
            try {
                val selectorWheelPaintField = NumberPicker::class.java.getDeclaredField("mSelectorWheelPaint")
                selectorWheelPaintField.isAccessible = true
                val paint = selectorWheelPaintField.get(picker) as android.graphics.Paint
                paint.color = defaultTextColor
                picker.invalidate()
            } catch (e: Exception) { }
        }

        // Initial highlight for selected value
        try {
            val field = NumberPicker::class.java.getDeclaredField("mInputText")
            field.isAccessible = true
            val inputText = field.get(numberPicker) as TextView
            inputText.setTextColor(selectedTextColor)
            inputText.textSize = 40f
        } catch (e: Exception) { }

        val btnNext = view.findViewById<Button>(R.id.btnNextAge)
        btnNext.setOnClickListener {
            playerAge = numberPicker.value
            currentStep++
            showStep(currentStep)
            updateProgressDots()
        }
        stepContainer.addView(view)
    }

    private fun showNameStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_name, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 1/5"
        val etName = view.findViewById<android.widget.EditText>(R.id.etPlayerName)
        etName.setText(playerName)
        etName.setTextColor(resources.getColor(R.color.text_primary, null))
        etName.setHintTextColor(resources.getColor(R.color.text_secondary, null))
        val btnVoice = view.findViewById<Button>(R.id.btnVoiceInput)
        btnVoice.setOnClickListener {
            try {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru-RU")
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Назовите своё имя")
                startActivityForResult(intent, REQUEST_CODE_VOICE_INPUT)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(this, "Распознавание речи недоступно", Toast.LENGTH_SHORT).show()
            }
        }
        val btnNext = view.findViewById<Button>(R.id.btnNextName)
        btnNext.setOnClickListener {
            playerName = etName.text.toString().trim()
            if (playerName.isEmpty()) {
                etName.error = "Введите имя персонажа"
            } else {
                // Произносим приветствие после подтверждения имени
                textToSpeech?.speak("Рада познакомиться, $playerName!", TextToSpeech.QUEUE_FLUSH, null, "greet_name")

                currentStep++
                showStep(currentStep)
                updateProgressDots()
            }
        }
        stepContainer.addView(view)
    }

    private fun showDateStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_date, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 3/5"
        val tvSelectedDate = view.findViewById<TextView>(R.id.tvSelectedDate)
        val btnPickDate = view.findViewById<Button>(R.id.btnPickDate)
        val btnNext = view.findViewById<Button>(R.id.btnNextDate)
        
        // Устанавливаем сегодняшнюю дату по умолчанию
        val today = Calendar.getInstance()
        selectedStartDate = today.timeInMillis
        tvSelectedDate.text = "%02d.%02d.%d".format(
            today.get(Calendar.DAY_OF_MONTH), 
            today.get(Calendar.MONTH) + 1, 
            today.get(Calendar.YEAR)
        )
        
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
                updateProgressDots()
            }
        }
        stepContainer.addView(view)
    }

    private fun showStartScreen() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_start, stepContainer, false)
        stepContainer.addView(view)
        view.postDelayed({
            val intent = Intent(this, GameActivity::class.java)
            val player = Player(
                name = playerName,
                age = playerAge,
                profession = selectedProfession!!,
                dream = selectedDream!!,
                startDateMillis = selectedStartDate
            )
            intent.putExtra("player", player)
            startActivity(intent)
            finish()
        }, 1500)
    }

    private fun setupCustomUI() {
        val professions = GameDataManager.getProfessions()
        val dreams = GameDataManager.getDreams()
        val educationLevels = EducationLevel.values().map { it.displayName }
        val maritalStatuses = MaritalStatus.values().map { it.displayName }
        val riskTolerances = RiskTolerance.values().map { it.displayName }
        val investmentStrategies = InvestmentStrategy.values().map { it.displayName }
        val healthLevels = HealthLevel.values().map { it.displayName }
        val stressLevels = StressLevel.values().map { it.displayName }
        val workLifeBalances = WorkLifeBalance.values().map { it.displayName }

        val spinnerProfession = findViewById<android.widget.Spinner>(R.id.spinnerProfession)
        val spinnerDream = findViewById<android.widget.Spinner>(R.id.spinnerDream)
        val spinnerEducation = findViewById<android.widget.Spinner>(R.id.spinnerEducation)
        val spinnerMaritalStatus = findViewById<android.widget.Spinner>(R.id.spinnerMaritalStatus)
        val spinnerRiskTolerance = findViewById<android.widget.Spinner>(R.id.spinnerRiskTolerance)
        val spinnerInvestmentStrategy = findViewById<android.widget.Spinner>(R.id.spinnerInvestmentStrategy)
        val spinnerHealthLevel = findViewById<android.widget.Spinner>(R.id.spinnerHealthLevel)
        val spinnerStressLevel = findViewById<android.widget.Spinner>(R.id.spinnerStressLevel)
        val spinnerWorkLifeBalance = findViewById<android.widget.Spinner>(R.id.spinnerWorkLifeBalance)

        spinnerProfession.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, professions.map { it.name })
        spinnerDream.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dreams.map { it.name })
        spinnerEducation.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, educationLevels)
        spinnerMaritalStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, maritalStatuses)
        spinnerRiskTolerance.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, riskTolerances)
        spinnerInvestmentStrategy.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, investmentStrategies)
        spinnerHealthLevel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, healthLevels)
        spinnerStressLevel.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, stressLevels)
        spinnerWorkLifeBalance.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, workLifeBalances)

        findViewById<Button>(R.id.btnCreateCustomPlayer).setOnClickListener {
            val name = findViewById<android.widget.EditText>(R.id.etName).text.toString().trim()
            val age = findViewById<android.widget.EditText>(R.id.etAge).text.toString().toIntOrNull() ?: 25
            val profession = professions[spinnerProfession.selectedItemPosition]
            val dream = dreams[spinnerDream.selectedItemPosition]
            val education = EducationLevel.values()[spinnerEducation.selectedItemPosition]
            val experience = findViewById<android.widget.EditText>(R.id.etExperience).text.toString().toIntOrNull() ?: 0
            val skillsRaw = findViewById<android.widget.EditText>(R.id.etSkills).text.toString()
            val skills = if (skillsRaw.isNotBlank()) skillsRaw.split(",").map { Skill(it.trim(), "", 0, SkillCategory.TECHNICAL) }.toMutableList() else mutableListOf()
            val maritalStatus = MaritalStatus.values()[spinnerMaritalStatus.selectedItemPosition]
            val children = findViewById<android.widget.EditText>(R.id.etChildren).text.toString().toIntOrNull() ?: 0
            val spouseIncome = findViewById<android.widget.EditText>(R.id.etSpouseIncome).text.toString().toIntOrNull() ?: 0
            val riskTolerance = RiskTolerance.values()[spinnerRiskTolerance.selectedItemPosition]
            val investmentStrategy = InvestmentStrategy.values()[spinnerInvestmentStrategy.selectedItemPosition]
            val savingsRate = findViewById<android.widget.EditText>(R.id.etSavingsRate).text.toString().toIntOrNull() ?: 10
            val healthLevel = HealthLevel.values()[spinnerHealthLevel.selectedItemPosition]
            val stressLevel = StressLevel.values()[spinnerStressLevel.selectedItemPosition]
            val workLifeBalance = WorkLifeBalance.values()[spinnerWorkLifeBalance.selectedItemPosition]
            val goalsRaw = findViewById<android.widget.EditText>(R.id.etGoals).text.toString()
            val goals = if (goalsRaw.isNotBlank()) goalsRaw.split(",").map { FinancialGoal(it.trim(), "", GoalType.PASSIVE_INCOME, 0, 12) }.toMutableList() else mutableListOf()

            if (name.isNotBlank()) {
                textToSpeech?.speak("Рада познакомиться, $name!", TextToSpeech.QUEUE_FLUSH, null, "greet_name_custom")
            }

            val player = Player(
                name = name,
                age = age,
                profession = profession,
                dream = dream,
                education = education,
                workExperience = experience,
                skills = skills,
                maritalStatus = maritalStatus,
                childrenCount = children,
                spouseIncome = spouseIncome,
                riskTolerance = riskTolerance,
                investmentStrategy = investmentStrategy,
                savingsRate = savingsRate,
                healthLevel = healthLevel,
                stressLevel = stressLevel,
                workLifeBalance = workLifeBalance,
                financialGoals = goals
            )
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("player", player)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_VOICE_INPUT && resultCode == RESULT_OK) {
            val results: ArrayList<String>? = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val recognized: String? = results?.firstOrNull()
            if (!recognized.isNullOrBlank()) {
                val etName = stepContainer.findViewById<android.widget.EditText>(R.id.etPlayerName)
                etName.setText(recognized)
                etName.setSelection(recognized.length)
            }
        }
    }

    override fun onDestroy() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        super.onDestroy()
    }
}