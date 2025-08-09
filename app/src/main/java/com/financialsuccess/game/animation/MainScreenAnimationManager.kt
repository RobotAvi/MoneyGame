package com.financialsuccess.game.animation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.financialsuccess.game.R


import kotlin.random.Random
import android.util.Log
import android.widget.ImageView.ScaleType

/**
 * Менеджер анимации главного экрана с программным фоном
 * Управляет последовательной анимацией: океан -> монеты + меню
 * 
 * Анимация состоит из 3 этапов:
 * 1. OceanBackgroundView - программный океанский фон
 * 2. CoinView - анимированные монеты с индивидуальными траекториями
 * 3. MainContent - появление меню с эффектами
 * 
 * Особенности:
 * - Монеты зациклены и постоянно всплывают
 * - Эффект глубины для монет (маленькие = темнее)
 * - Оптимизация памяти через очистку анимаций
 * - Сеточное распределение монет для равномерности
 */
class MainScreenAnimationManager(
    private val oceanBackground: ImageView,
    private val coinsContainer: ViewGroup,
    private val mainContent: View
) {
    
    companion object {
        const val TAG = "AnimationManager"
        
        // Длительности анимаций (в миллисекундах)
        const val OCEAN_ANIMATION_DURATION = 600L
        const val COINS_ANIMATION_DURATION = 1500L
        const val MENU_ANIMATION_DURATION = 1000L
        
        // Задержки анимаций (в миллисекундах)
        const val COINS_START_DELAY = 600L
        const val MENU_START_DELAY = 700L
        
        // Параметры монет
        const val COINS_COUNT = 15 // Количество монет на экране
        const val COIN_MIN_SIZE = 30 // Минимальный размер монеты (px)
        const val COIN_MAX_SIZE = 60 // Максимальный размер монеты (px)
        const val COIN_MIN_DELAY = 0L // Минимальная задержка появления (ms)
        const val COIN_MAX_DELAY = 4000L // Максимальная задержка появления (ms)
        const val COIN_MIN_SPEED = 0.6f // Минимальная скорость движения
        const val COIN_MAX_SPEED = 1.0f // Максимальная скорость движения
        
        // Параметры траекторий монет
        const val TRAJECTORY_WIGGLE_AMPLITUDE = 30f // Амплитуда покачивания (px)
        const val TRAJECTORY_WIGGLE_FREQUENCY = 2f // Частота покачивания (Гц)
        
        // Параметры сетки для распределения монет
        const val COIN_GRID_COLUMNS = 5 // Количество колонок в сетке
        const val COIN_GRID_RANDOM_OFFSET_RATIO = 0.6f // Коэффициент случайного смещения
        
        // Параметры эффекта глубины
        const val COIN_DEPTH_MIN_ALPHA = 0.6f // Минимальная прозрачность (дальние монеты)
        const val COIN_DEPTH_MAX_ALPHA = 1.0f // Максимальная прозрачность (ближние монеты)
    }
    
    private val coins = mutableListOf<CoinView>()
    private var _isAnimating = false
    
    val isAnimating: Boolean
        get() = _isAnimating
    
    /**
     * Запускает полную последовательность анимации
     */
    fun startAnimation() {
        Log.d(TAG, "startAnimation called, _isAnimating: $_isAnimating")
        
        if (_isAnimating) {
            Log.d(TAG, "Animation already running, skipping")
            return
        }
        
        _isAnimating = true
        Log.d(TAG, "Animation started")
        
        // Создаем монеты
        createCoins()
        
        // Последовательность анимаций (без света)
        val animatorSet = AnimatorSet()
        
        // 1. Анимация океана (фон уже виден)
        val oceanAnimator = createOceanAnimation()
        
        // 2. Анимация монет и меню (синхронно)
        val coinsAndMenuAnimator = createCoinsAndMenuAnimation()
        
        animatorSet.playSequentially(
            oceanAnimator,
            coinsAndMenuAnimator
        )
        
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.d(TAG, "Animation sequence started")
            }
            override fun onAnimationCancel(animation: Animator) {
                Log.d(TAG, "Animation sequence cancelled")
                _isAnimating = false
            }
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                Log.d(TAG, "Animation sequence ended")
                Log.d(TAG, "Final menu state - alpha: ${mainContent.alpha}, translationY: ${mainContent.translationY}")
                _isAnimating = false
            }
        })
        
        animatorSet.start()
    }
    
    /**
     * Создает анимацию океанского фона
     */
    private fun createOceanAnimation(): AnimatorSet {
        Log.d(TAG, "Creating ocean animation")
        val animatorSet = AnimatorSet()
        
        // Анимация появления океана (если нужно)
        val alphaAnimator = ObjectAnimator.ofFloat(oceanBackground, "alpha", 0f, 1f).apply {
            duration = OCEAN_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }
        
        animatorSet.play(alphaAnimator)
        return animatorSet
    }
    

    
    /**
     * Создает монеты для анимации
     */
    private fun createCoins() {
        Log.d(TAG, "Creating $COINS_COUNT coins")
        Log.d(TAG, "CoinsContainer size: ${coinsContainer.width}x${coinsContainer.height}")
        
        if (coinsContainer.width <= 0 || coinsContainer.height <= 0) {
            Log.e(TAG, "CoinsContainer has invalid size, waiting for layout")
            coinsContainer.postDelayed({
                Log.d(TAG, "Retrying coin creation after delay")
                createCoins()
            }, 100) // Задержка 100мс для завершения layout
            return
        }
        
        coins.clear()
        coinsContainer.removeAllViews()
        
        // Создаем сетку для лучшего распределения монет
        val gridColumns = COIN_GRID_COLUMNS
        val gridRows = 3 // Оставляем 3 строки для распределения
        val cellWidth = coinsContainer.width / gridColumns
        val cellHeight = coinsContainer.height / gridRows
        
        repeat(COINS_COUNT) { index ->
            val coin = CoinView(coinsContainer.context).apply {
                // Размеры монет с эффектом глубины
                val coinSize = Random.nextInt(COIN_MIN_SIZE, COIN_MAX_SIZE)
                layoutParams = ViewGroup.LayoutParams(coinSize, coinSize)
                
                // Начальное состояние
                alpha = 0f
                scaleX = 0.3f
                scaleY = 0.3f
                translationY = coinsContainer.height.toFloat() + 200f
                
                // Лучшее распределение по X - используем сетку с небольшим случайным смещением
                val gridX = index % gridColumns
                val gridY = index / gridColumns
                val baseX = gridX * cellWidth + cellWidth / 2
                val randomOffsetX = (Random.nextFloat() - 0.5f) * cellWidth * COIN_GRID_RANDOM_OFFSET_RATIO // Случайное смещение в пределах ячейки
                translationX = baseX + randomOffsetX
                
                // Случайные параметры траектории
                val speed = Random.nextFloat() * (COIN_MAX_SPEED - COIN_MIN_SPEED) + COIN_MIN_SPEED
                setTrajectoryParameters(
                    speed = speed,
                    wiggleAmplitude = TRAJECTORY_WIGGLE_AMPLITUDE * Random.nextFloat(),
                    wiggleFrequency = TRAJECTORY_WIGGLE_FREQUENCY * Random.nextFloat()
                )
                
                // Эффект глубины - маленькие монеты темнее
                val sizeRatio = coinSize.toFloat() / COIN_MAX_SIZE
                val alphaMultiplier = COIN_DEPTH_MIN_ALPHA + (sizeRatio * (COIN_DEPTH_MAX_ALPHA - COIN_DEPTH_MIN_ALPHA)) // От 0.6 до 1.0
                setAlpha(alphaMultiplier)
            }
            
            coins.add(coin)
            coinsContainer.addView(coin)
            Log.d(TAG, "Added coin $index at position (${coin.translationX}, ${coin.translationY}) with size ${coin.layoutParams.width}x${coin.layoutParams.height}")
        }
        
        Log.d(TAG, "Created ${coins.size} coins")
    }
    
    /**
     * Создает анимацию монет и меню (синхронно)
     */
    private fun createCoinsAndMenuAnimation(): AnimatorSet {
        Log.d(TAG, "Creating coins and menu animation")
        val animatorSet = AnimatorSet()
        
        // Анимация монет
        val coinsAnimator = createCoinsAnimation()
        
        // Анимация меню
        val menuAnimator = createMenuAnimation()
        
        // Запускаем монеты и меню одновременно, но с разными задержками
        animatorSet.playTogether(coinsAnimator, menuAnimator)
        
        return animatorSet
    }
    
    /**
     * Создает анимацию всплытия монет
     */
    private fun createCoinsAnimation(): AnimatorSet {
        Log.d(TAG, "Creating coins animation for ${coins.size} coins")
        val animatorSet = AnimatorSet()
        val coinAnimators = mutableListOf<Animator>()
        
        coins.forEachIndexed { index, coin ->
            val coinAnimator = AnimatorSet()
            
            // Задержка для каждой монеты
            val delay = COINS_START_DELAY + Random.nextLong(COIN_MIN_DELAY, COIN_MAX_DELAY)
            
            // Анимация движения вверх - ЗАЦИКЛИВАЕМ
            val translationYAnimator = ObjectAnimator.ofFloat(coin, "translationY", 
                coin.translationY, 
                -coin.height.toFloat() - 100f // Останавливаемся чуть выше экрана
            ).apply {
                duration = (COINS_ANIMATION_DURATION * coin.getSpeed() * 32).toLong() // Увеличили в 2 раза (было *16, стало *32)
                interpolator = LinearInterpolator() // Линейная анимация для равномерного движения
                repeatCount = ValueAnimator.INFINITE // Зацикливаем
                repeatMode = ValueAnimator.RESTART // Перезапускаем с начала
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {}
                    override fun onAnimationCancel(animation: Animator) {}
                    override fun onAnimationRepeat(animation: Animator) {
                        // Когда анимация повторяется, возвращаем монету вниз
                        coin.translationY = coinsContainer.height.toFloat() + 200f
                        
                        // Лучшее распределение по X при перезапуске
                        val gridColumns = COIN_GRID_COLUMNS
                        val cellWidth = coinsContainer.width / gridColumns
                        val randomGridX = Random.nextInt(gridColumns)
                        val baseX = randomGridX * cellWidth + cellWidth / 2
                        val randomOffsetX = (Random.nextFloat() - 0.5f) * cellWidth * COIN_GRID_RANDOM_OFFSET_RATIO
                        coin.translationX = baseX + randomOffsetX
                        
                        // Очищаем предыдущие анимации для экономии памяти
                        coin.clearAnimation()
                        
                        // Перезапускаем покачивание и вращение
                        val newWiggleAnimator = createWiggleAnimation(coin)
                        val newRotationAnimator = ObjectAnimator.ofFloat(coin, "rotation", 
                            0f, 360f
                        ).apply {
                            duration = (COINS_ANIMATION_DURATION * coin.getSpeed() * 32).toLong()
                            repeatCount = ValueAnimator.INFINITE
                            interpolator = LinearInterpolator()
                        }
                        
                        val movementAnimator = AnimatorSet().apply {
                            playTogether(newWiggleAnimator, newRotationAnimator)
                        }
                        movementAnimator.start()
                        
                        Log.d(TAG, "Coin restarted at position (${coin.translationX}, ${coin.translationY})")
                    }
                    override fun onAnimationEnd(animation: Animator) {}
                })
            }
            
            // Анимация покачивания по X
            val wiggleAnimator = createWiggleAnimation(coin)
            
            // Анимация масштабирования - только один раз при появлении
            val scaleXAnimator = ObjectAnimator.ofFloat(coin, "scaleX", 0.3f, 1f).apply {
                duration = COINS_ANIMATION_DURATION / 2
                interpolator = DecelerateInterpolator()
            }
            
            val scaleYAnimator = ObjectAnimator.ofFloat(coin, "scaleY", 0.3f, 1f).apply {
                duration = COINS_ANIMATION_DURATION / 2
                interpolator = DecelerateInterpolator()
            }
            
            // Анимация появления - только один раз
            val alphaAnimator = ObjectAnimator.ofFloat(coin, "alpha", 0f, 1f).apply {
                duration = COINS_ANIMATION_DURATION / 2
                interpolator = DecelerateInterpolator()
            }
            
            // Легкое вращение - зацикливаем
            val rotationAnimator = ObjectAnimator.ofFloat(coin, "rotation", 
                0f, 360f
            ).apply {
                duration = (COINS_ANIMATION_DURATION * coin.getSpeed() * 32).toLong() // Увеличили в 2 раза
                repeatCount = ValueAnimator.INFINITE
                interpolator = LinearInterpolator()
            }
            
            // Сначала запускаем появление и масштабирование
            val appearanceAnimator = AnimatorSet().apply {
                playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator)
            }
            
            // Затем запускаем постоянное движение
            val movementAnimator = AnimatorSet().apply {
                playTogether(translationYAnimator, wiggleAnimator, rotationAnimator)
            }
            
            coinAnimator.playSequentially(appearanceAnimator, movementAnimator)
            coinAnimator.startDelay = delay
            coinAnimators.add(coinAnimator)
        }
        
        animatorSet.playTogether(coinAnimators)
        Log.d(TAG, "Created ${coinAnimators.size} coin animators")
        return animatorSet
    }
    
    /**
     * Создает анимацию покачивания для монеты
     */
    private fun createWiggleAnimation(coin: CoinView): AnimatorSet {
        val wiggleAnimator = AnimatorSet()
        
        val wiggleXAnimator = ObjectAnimator.ofFloat(coin, "translationX", 
            coin.translationX - coin.getWiggleAmplitude(),
            coin.translationX + coin.getWiggleAmplitude()
        ).apply {
            duration = (1000 / coin.getWiggleFrequency()).toLong()
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
        }
        
        wiggleAnimator.play(wiggleXAnimator)
        return wiggleAnimator
    }
    
    /**
     * Создает анимацию появления меню
     */
    private fun createMenuAnimation(): AnimatorSet {
        Log.d(TAG, "Creating menu animation")
        Log.d(TAG, "MainContent size: ${mainContent.width}x${mainContent.height}")
        Log.d(TAG, "MainContent current state: alpha=${mainContent.alpha}, translationY=${mainContent.translationY}")
        
        // Принудительно устанавливаем начальное состояние
        mainContent.alpha = 0f
        mainContent.translationY = 100f
        mainContent.scaleX = 0.9f
        mainContent.scaleY = 0.9f
        
        val animatorSet = AnimatorSet()
        
        // Анимация появления
        val alphaAnimator = ObjectAnimator.ofFloat(mainContent, "alpha", 0f, 1f).apply {
            duration = MENU_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }
        
        // Анимация движения снизу вверх
        val translationYAnimator = ObjectAnimator.ofFloat(mainContent, "translationY", 100f, 0f).apply {
            duration = MENU_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }
        
        // Анимация масштабирования
        val scaleXAnimator = ObjectAnimator.ofFloat(mainContent, "scaleX", 0.9f, 1f).apply {
            duration = MENU_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }
        
        val scaleYAnimator = ObjectAnimator.ofFloat(mainContent, "scaleY", 0.9f, 1f).apply {
            duration = MENU_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
        }
        
        animatorSet.playTogether(alphaAnimator, translationYAnimator, scaleXAnimator, scaleYAnimator)
        animatorSet.startDelay = MENU_START_DELAY
        
        // Добавляем слушатель для отладки
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                Log.d(TAG, "Menu animation STARTED - alpha: ${mainContent.alpha}, translationY: ${mainContent.translationY}")
            }
            override fun onAnimationEnd(animation: Animator) {
                Log.d(TAG, "Menu animation ENDED - alpha: ${mainContent.alpha}, translationY: ${mainContent.translationY}")
            }
            override fun onAnimationCancel(animation: Animator) {
                Log.d(TAG, "Menu animation CANCELLED")
            }
            override fun onAnimationRepeat(animation: Animator) {}
        })
        
        Log.d(TAG, "Created menu animation with delay: $MENU_START_DELAY")
        return animatorSet
    }
    
    /**
     * Останавливает все анимации
     */
    fun stopAnimation() {
        Log.d(TAG, "stopAnimation called")
        _isAnimating = false
        
        // Очищаем все анимации
        oceanBackground.clearAnimation()
        coins.forEach { 
            it.clearAnimation()
            // Удаляем из родительского контейнера для освобождения памяти
            (it.parent as? ViewGroup)?.removeView(it)
        }
        mainContent.clearAnimation()
        
        // Очищаем список монет
        coins.clear()
        
        Log.d(TAG, "All animations stopped and memory cleaned")
    }
    
    /**
     * Сбрасывает состояние элементов к начальному
     */
    fun resetToInitialState() {
        Log.d(TAG, "resetToInitialState called")
        
        // Очищаем все анимации
        oceanBackground.clearAnimation()
        coins.forEach { it.clearAnimation() }
        mainContent.clearAnimation()
        
        // Сбрасываем океан
        oceanBackground.alpha = 1f
        

        
        // Сбрасываем меню - ПРИНУДИТЕЛЬНО
        mainContent.alpha = 0f
        mainContent.translationY = 100f
        mainContent.scaleX = 0.9f
        mainContent.scaleY = 0.9f
        mainContent.invalidate() // Принудительное обновление
        
        // Очищаем монеты
        coinsContainer.removeAllViews()
        coins.clear()
        
        Log.d(TAG, "Reset completed - menu alpha: ${mainContent.alpha}, translationY: ${mainContent.translationY}")
    }
}

/**
 * Кастомный View для монет с параметрами траектории
 * 
 * Особенности:
 * - Прозрачный фон (без черных артефактов)
 * - Индивидуальные параметры движения для каждой монеты
 * - Эффект глубины через прозрачность
 * - Автоматическое масштабирование изображения
 * 
 * Параметры траектории:
 * - speed: скорость движения (0.6-1.0)
 * - wiggleAmplitude: амплитуда покачивания по X
 * - wiggleFrequency: частота покачивания
 */
class CoinView(context: Context) : ImageView(context) {
    
    private var speed: Float = 1f
    private var wiggleAmplitude: Float = 30f
    private var wiggleFrequency: Float = 2f
    
    init {
        // Устанавливаем изображение монетки с долларом
        setImageResource(R.drawable.coin_dollar)
        
        // Настройка масштабирования
        scaleType = ScaleType.FIT_CENTER
        
        // Параметры траектории для этой монеты
        speed = Random.nextFloat() * (MainScreenAnimationManager.COIN_MAX_SPEED - MainScreenAnimationManager.COIN_MIN_SPEED) + MainScreenAnimationManager.COIN_MIN_SPEED
        wiggleAmplitude = MainScreenAnimationManager.TRAJECTORY_WIGGLE_AMPLITUDE
        wiggleFrequency = MainScreenAnimationManager.TRAJECTORY_WIGGLE_FREQUENCY
        
        // Логирование для отладки
        Log.d(MainScreenAnimationManager.TAG, "CoinView initialized with speed: $speed, wiggle: $wiggleAmplitude")
    }
    
    fun setTrajectoryParameters(speed: Float, wiggleAmplitude: Float, wiggleFrequency: Float) {
        this.speed = speed
        this.wiggleAmplitude = wiggleAmplitude
        this.wiggleFrequency = wiggleFrequency
    }
    
    fun getSpeed(): Float = speed
    fun getWiggleAmplitude(): Float = wiggleAmplitude
    fun getWiggleFrequency(): Float = wiggleFrequency
}
