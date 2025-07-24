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
        val ivPhoto = view.findViewById<ImageView>(R.id.ivProfessionPhoto)
        val tvName = view.findViewById<TextView>(R.id.tvProfessionName)
        val tvParams = view.findViewById<TextView>(R.id.tvProfessionParams)
        val btnLeft = view.findViewById<Button>(R.id.btnSwipeLeft)
        val btnRight = view.findViewById<Button>(R.id.btnSwipeRight)
        val btnChoose = view.findViewById<Button>(R.id.btnChooseProfession)

        fun updateProfessionUI() {
            val prof = professions[professionIndex]
            // Картинка: ищем png по id профессии
            val resId = resources.getIdentifier(
                "profession_${prof.id}", "drawable", packageName
            )
            if (resId != 0) ivPhoto.setImageResource(resId)
            else ivPhoto.setImageResource(R.drawable.ic_profession_placeholder)
            tvName.text = prof.name
            tvParams.text = "${prof.description}\nЗарплата: ${prof.salary}₽\nРасходы: ${prof.expenses}₽\nНалоги: ${prof.taxes}₽\nОбразование: ${prof.education}"
        }
        updateProfessionUI()
        btnLeft.setOnClickListener {
            professionIndex = (professionIndex - 1 + professions.size) % professions.size
            updateProfessionUI()
        }
        btnRight.setOnClickListener {
            professionIndex = (professionIndex + 1) % professions.size
            updateProfessionUI()
        }
        // Свайпы
        view.setOnTouchListener(object : View.OnTouchListener {
            private var x1 = 0f
            private var x2 = 0f
            override fun onTouch(v: View?, event: android.view.MotionEvent?): Boolean {
                when (event?.action) {
                    android.view.MotionEvent.ACTION_DOWN -> x1 = event.x
                    android.view.MotionEvent.ACTION_UP -> {
                        x2 = event.x
                        val deltaX = x2 - x1
                        if (deltaX > 100) btnLeft.performClick()
                        if (deltaX < -100) btnRight.performClick()
                    }
                }
                return true
            }
        })
        btnChoose.setOnClickListener {
            selectedProfession = professions[professionIndex]
            currentStep++
            showStep(currentStep)
        }
        // Исправляю ширину кнопки
        btnChoose.textSize = 16f
        btnChoose.minWidth = 220
        stepContainer.addView(view)
    }

    private fun showDreamStep() {
        val view = LayoutInflater.from(this).inflate(R.layout.step_dream, stepContainer, false)
        val stepTitle = view.findViewById<TextView>(R.id.tvStepTitle)
        stepTitle.text = "ШАГ 2/5"
        val ivPhoto = view.findViewById<ImageView>(R.id.ivDreamPhoto)
        val tvName = view.findViewById<TextView>(R.id.tvDreamName)
        val tvParams = view.findViewById<TextView>(R.id.tvDreamParams)
        val btnLeft = view.findViewById<Button>(R.id.btnSwipeLeft)
        val btnRight = view.findViewById<Button>(R.id.btnSwipeRight)
        val btnChoose = view.findViewById<Button>(R.id.btnChooseDream)

        fun updateDreamUI() {
            val dream = dreams[dreamIndex]
            val resId = resources.getIdentifier(
                "dream_${dream.id}", "drawable", packageName
            )
            if (resId != 0) ivPhoto.setImageResource(resId)
            else ivPhoto.setImageResource(R.drawable.ic_dream_placeholder)
            tvName.text = dream.name
            tvParams.text = "${dream.description}\nСтоимость: ${dream.cost}₽\nПассивный доход: ${dream.cashFlowRequired}₽\nЧисло на кубике: ${dream.fastTrackNumber}"
        }
        updateDreamUI()
        btnLeft.setOnClickListener {
            dreamIndex = (dreamIndex - 1 + dreams.size) % dreams.size
            updateDreamUI()
        }
        btnRight.setOnClickListener {
            dreamIndex = (dreamIndex + 1) % dreams.size
            updateDreamUI()
        }
        // Свайпы
        view.setOnTouchListener(object : View.OnTouchListener {
            private var x1 = 0f
            private var x2 = 0f
            override fun onTouch(v: View?, event: android.view.MotionEvent?): Boolean {
                when (event?.action) {
                    android.view.MotionEvent.ACTION_DOWN -> x1 = event.x
                    android.view.MotionEvent.ACTION_UP -> {
                        x2 = event.x
                        val deltaX = x2 - x1
                        if (deltaX > 100) btnLeft.performClick()
                        if (deltaX < -100) btnRight.performClick()
                    }
                }
                return true
            }
        })
        btnChoose.setOnClickListener {
            selectedDream = dreams[dreamIndex]
            currentStep++
            showStep(currentStep)
        }
        btnChoose.textSize = 16f
        btnChoose.minWidth = 220
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
        // Цвет и размер
        val textColor = resources.getColor(R.color.text_primary, null)
        val count = numberPicker.childCount
        for (i in 0 until count) {
            val child = numberPicker.getChildAt(i)
            if (child is TextView) {
                child.setTextColor(textColor)
                child.textSize = 28f
            }
        }
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
        etName.setTextColor(resources.getColor(R.color.text_primary, null))
        etName.setHintTextColor(resources.getColor(R.color.text_secondary, null))
        val btnVoice = view.findViewById<Button>(R.id.btnVoiceInput)
        btnVoice.setOnClickListener {
            // Можно скрыть или реализовать голосовой ввод
            // Пока скрываю
            btnVoice.visibility = View.GONE
        }
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
        // Переход к игре через 1.5 секунды
        view.postDelayed({
            val intent = Intent(this, GameActivity::class.java)
            // Передаём игрока
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
}