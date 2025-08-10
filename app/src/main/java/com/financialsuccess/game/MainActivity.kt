package com.financialsuccess.game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.financialsuccess.game.databinding.ActivityMainBinding
import android.media.MediaPlayer
import com.financialsuccess.game.animation.MainScreenAnimationManager
import android.util.Log
import com.financialsuccess.game.BuildConfig

/**
 * Главная активность приложения - стартовый экран
 * 
 * Функциональность:
 * - Красивая анимация появления с океаном, светом и монетами
 * - Фоновая музыка с автоматическим управлением
 * - Навигация к другим экранам приложения
 * - Управление жизненным циклом анимации
 * 
 * Меню:
 * - "Начать" → CharacterCreationActivity
 * - "Правила" → RulesActivity
 */
class MainActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityMainBinding
    private var menuPlayer: MediaPlayer? = null
    private lateinit var animationManager: MainScreenAnimationManager
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        Log.d(TAG, "MainActivity onCreate started")
        
        // Инициализация менеджера анимации
        animationManager = MainScreenAnimationManager(
            oceanBackground = binding.oceanBackground,
            coinsContainer = binding.coinsContainer,
            mainContent = binding.mainContent
        )
        
        Log.d(TAG, "AnimationManager initialized")
        
        // Настройка кнопок
        setupButtons()
        
        // Обновление информации о версии
        updateVersionInfo()
        
        // Инициализация музыки
        initMusic()
        
        // Запуск анимации при создании активности
        startMainScreenAnimation()
    }
    
    private fun initMusic() {
        try {
            menuPlayer = MediaPlayer.create(this, R.raw.menu).apply {
                isLooping = true
                setVolume(0.5f, 0.5f)
                start()
            }
            Log.d(TAG, "Music initialized and started")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing music: ${e.message}")
            // Продолжаем работу без музыки
        }
    }
    
    private fun setupButtons() {
        // Кнопка "Начать"
        binding.newGameButton.setOnClickListener { startNewGame() }
        
        // Кнопка "Правила"
        binding.rulesButton.setOnClickListener { showRules() }
    }
    
    private fun updateVersionInfo() {
        binding.versionInfo.text = "Версия ${BuildConfig.VERSION_NAME}"
    }
    
    private fun startMainScreenAnimation() {
        try {
            Log.d(TAG, "Starting main screen animation")

            // Сброс к начальному состоянию
            animationManager.resetToInitialState()

            // Проверяем состояние меню после сброса
            Log.d(TAG, "After reset - mainContent alpha: ${binding.mainContent.alpha}, visible: ${binding.mainContent.visibility}")

            // Запуск анимации после завершения layout
            binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : android.view.ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    try {
                        Log.d(TAG, "Layout completed, starting animation")
                        Log.d(TAG, "Layout sizes - root: ${binding.root.width}x${binding.root.height}")
                        Log.d(TAG, "Layout sizes - coinsContainer: ${binding.coinsContainer.width}x${binding.coinsContainer.height}")
                        Log.d(TAG, "Layout sizes - mainContent: ${binding.mainContent.width}x${binding.mainContent.height}")
                        binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)

                        // Проверяем, видно ли меню перед запуском анимации
                        Log.d(TAG, "Before animation - mainContent alpha: ${binding.mainContent.alpha}, visible: ${binding.mainContent.visibility}")

                        animationManager.startAnimation()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in layout listener: ${e.message}")
                    }
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Error starting animation: ${e.message}")
        }
    }
    
    private fun startNewGame() {
        // Остановка анимации
        animationManager.stopAnimation()
        
        // Запуск создания персонажа
        val intent = Intent(this, CharacterCreationActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    private fun showRules() {
        // Остановка анимации
        animationManager.stopAnimation()
        
        // Запуск активности с правилами
        val intent = Intent(this, RulesActivity::class.java)
        startActivity(intent)
    }
    
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume called")

        // Перезапускаем анимацию при возвращении только если активность не уничтожается
        if (!isFinishing && !isDestroyed && !animationManager.isAnimating) {
            Log.d(TAG, "Restarting animation in onResume")
            startMainScreenAnimation()
        }

        // Возобновляем музыку только если она была инициализирована
        try {
            menuPlayer?.start()
        } catch (e: Exception) {
            Log.e(TAG, "Error resuming music: ${e.message}")
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause called")

        // Останавливаем анимацию при уходе с экрана
        try {
            animationManager.stopAnimation()
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping animation: ${e.message}")
        }

        // Останавливаем музыку
        try {
            menuPlayer?.pause()
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing music: ${e.message}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called")
        try {
            menuPlayer?.stop()
            menuPlayer?.release()
            menuPlayer = null
        } catch (e: Exception) {
            Log.e(TAG, "Error releasing music: ${e.message}")
        }
    }
}