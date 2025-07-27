package com.financialsuccess.game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.financialsuccess.game.databinding.ActivityMainBinding
import android.widget.Button
import com.financialsuccess.game.data.GameSaveManager
import com.financialsuccess.game.models.Player
import android.view.View
import com.financialsuccess.game.GameActivity
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    
    companion object {
        private const val APP_VERSION = "1.83"
        private const val BUILD_NUMBER = "247" // Номер текущего PR
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Отображаем версию приложения с номером билда
        val versionText = "Версия $APP_VERSION (build $BUILD_NUMBER)"
        binding.tvVersion.text = versionText
        setupUI()
    }
    
    private fun setupUI() {
        val btnNew = binding.btnNewGame
        val btnLoad = binding.btnLoadGame
        btnLoad.visibility = if (GameSaveManager.loadPlayer(this) != null) View.VISIBLE else View.GONE

        btnNew.setOnClickListener {
            // Запуск только стандартного флоу (без custom)
            startActivity(Intent(this, CharacterCreationActivity::class.java))
        }
        btnLoad.setOnClickListener {
            val player = GameSaveManager.loadPlayer(this)
            if (player != null) {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra("player", player)
                startActivity(intent)
            } else {
                btnLoad.visibility = View.GONE
            }
        }
        
        binding.btnAdvancedCharacter.setOnClickListener {
            val intent = Intent(this, CharacterCreationActivity::class.java)
            intent.putExtra("custom", true)
            startActivity(intent)
        }
        
        binding.btnRules.setOnClickListener {
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }
        
        binding.btnExit.setOnClickListener {
            finish()
        }
    }
}