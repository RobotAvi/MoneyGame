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

class ProfessionSelectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfessionSelectionBinding
    private var selectedProfession: Profession? = null
    private var selectedDream: Dream? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfessionSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        setupProfessionRecyclerView()
        setupDreamRecyclerView()
        
        binding.btnStartGame.setOnClickListener {
            if (selectedProfession != null && selectedDream != null) {
                val ageText = binding.etAge.text.toString()
                val age = ageText.toIntOrNull()
                
                if (age == null || age < 18 || age > 65) {
                    binding.etAge.error = "Возраст должен быть от 18 до 65 лет"
                    return@setOnClickListener
                }
                
                val intent = Intent(this, GameActivity::class.java).apply {
                    putExtra("profession", selectedProfession)
                    putExtra("dream", selectedDream)
                    putExtra("playerAge", age)
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