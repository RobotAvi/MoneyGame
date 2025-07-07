package com.financialsuccess.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.financialsuccess.game.databinding.ActivityRulesBinding

class RulesActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRulesBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRulesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
    }
    
    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // Заголовок экрана
        supportActionBar?.apply {
            title = getString(R.string.rules_title)
            setDisplayHomeAsUpEnabled(true)
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}