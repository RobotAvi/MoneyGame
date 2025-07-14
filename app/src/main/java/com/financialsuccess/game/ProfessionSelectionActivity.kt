package com.financialsuccess.game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.financialsuccess.game.adapters.ProfessionAdapter
import com.financialsuccess.game.adapters.DreamAdapter
import com.financialsuccess.game.data.GameDataManager
import com.financialsuccess.game.databinding.ActivityProfessionSelectionBinding
import com.financialsuccess.game.models.Dream
import com.financialsuccess.game.models.Profession
import android.app.DatePickerDialog
import java.util.Calendar

class ProfessionSelectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfessionSelectionBinding
    private var selectedProfession: Profession? = null
    private var selectedDream: Dream? = null
    private var selectedStartDate: Long? = null
    private var playerName: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfessionSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        setupProfessionRecyclerView()
        setupDreamRecyclerView()

        // Date picker for start date
        val etStartDate = binding.etStartDate
        etStartDate.setOnClickListener {
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
                    etStartDate.setText("%02d.%02d.%d".format(dayOfMonth, month + 1, year))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        binding.btnStartGame.setOnClickListener {
            if (selectedProfession != null && selectedDream != null) {
                val ageText = binding.etAge.text.toString()
                val age = ageText.toIntOrNull()
                playerName = binding.etPlayerName.text?.toString()?.trim()

                if (age == null || age < 18 || age > 65) {
                    binding.etAge.error = "Возраст должен быть от 18 до 65 лет"
                    return@setOnClickListener
                }
                if (playerName.isNullOrEmpty()) {
                    binding.etPlayerName.error = "Введите имя персонажа"
                    return@setOnClickListener
                }
                if (selectedStartDate == null) {
                    binding.etStartDate.error = "Выберите дату старта"
                    return@setOnClickListener
                }

                val intent = Intent(this, GameActivity::class.java).apply {
                    putExtra("profession", selectedProfession)
                    putExtra("dream", selectedDream)
                    putExtra("playerAge", age)
                    putExtra("playerName", playerName)
                    putExtra("startDate", selectedStartDate)
                }
                startActivity(intent)
                finish()
            }
        }

        updateStartButtonState()
    }
    
    private fun setupProfessionRecyclerView() {
        val professions = GameDataManager.getProfessions()
        val adapter = ProfessionAdapter(professions) { profession ->
            selectedProfession = profession
            updateStartButtonState()
        }
        
        binding.recyclerViewProfessions.apply {
            layoutManager = LinearLayoutManager(this@ProfessionSelectionActivity)
            this.adapter = adapter
        }
    }
    
    private fun setupDreamRecyclerView() {
        val dreams = GameDataManager.getDreams()
        val adapter = DreamAdapter(dreams) { dream ->
            selectedDream = dream
            updateStartButtonState()
        }
        
        binding.recyclerViewDreams.apply {
            layoutManager = LinearLayoutManager(this@ProfessionSelectionActivity)
            this.adapter = adapter
        }
    }
    
    private fun updateStartButtonState() {
        binding.btnStartGame.isEnabled = selectedProfession != null && selectedDream != null
    }
}