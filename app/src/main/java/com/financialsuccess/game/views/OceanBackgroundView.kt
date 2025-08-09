package com.financialsuccess.game.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.random.Random

/**
 * Кастомный View для создания программного океанского фона
 * 
 * Особенности:
 * - Линейный градиент от темно-синего к светло-синему
 * - Имитация глубины океана
 * - Программная реализация без статических изображений
 * - Оптимизированная отрисовка
 * 
 * Цветовая схема:
 * - DEEP_OCEAN_COLOR: темно-синий (#0a1a3a) - дно океана
 * - LIGHT_OCEAN_COLOR: светло-синий (#1a4a90) - поверхность
 * 
 * Градиент направлен сверху вниз для имитации глубины
 */
class OceanBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val noisePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val noiseBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
    
    // Константы для настройки океана
    companion object {
        val DEEP_OCEAN_COLOR = Color.parseColor("#000c1a")      // Тёмно-синий снизу
        val SURFACE_OCEAN_COLOR = Color.parseColor("#0f3250")   // Средний синий в середине
        val LIGHT_OCEAN_COLOR = Color.parseColor("#1a4a90")     // Светло-синий сверху
        const val NOISE_OPACITY = 0.1f                          // Прозрачность шума
        const val NOISE_SCALE = 0.02f                           // Масштаб шума
    }
    
    init {
        setupNoise()
    }
    
    private fun setupNoise() {
        val canvas = Canvas(noiseBitmap)
        
        // Создаем шум для атмосферности
        for (x in 0 until noiseBitmap.width) {
            for (y in 0 until noiseBitmap.height) {
                val noise = Random.nextFloat()
                val alpha = (noise * 50).toInt() // Полупрозрачность
                val color = Color.argb(alpha, 255, 255, 255)
                noiseBitmap.setPixel(x, y, color)
            }
        }
        
        noisePaint.alpha = (255 * NOISE_OPACITY).toInt()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Рисуем основной градиент океана
        drawOceanGradient(canvas)
        
        // Добавляем шум для атмосферности
        drawOceanNoise(canvas)
    }
    
    private fun drawOceanGradient(canvas: Canvas) {
        // Создаем радиальный градиент с центром ближе к углу экрана
        val centerX = -width * 0.3f  // Центр градиента ближе к левому краю
        val centerY = -height * 0.3f  // Центр градиента ближе к верхнему краю
        val radius = maxOf(width, height) * 1.0f  // Радиус для покрытия экрана
        
        val gradient = RadialGradient(
            centerX, centerY, radius,  // Центр и радиус
            intArrayOf(
                LIGHT_OCEAN_COLOR,     // Светло-синий в центре
                SURFACE_OCEAN_COLOR,   // Средний синий
                DEEP_OCEAN_COLOR       // Тёмно-синий по краям
            ),
            floatArrayOf(0f, 0.15f, 1f), // Резкие переходы для эффекта света в воде
            Shader.TileMode.CLAMP
        )
        
        paint.shader = gradient
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null
    }
    
    private fun drawOceanNoise(canvas: Canvas) {
        // Рисуем шум поверх градиента для создания текстуры
        val matrix = Matrix()
        matrix.setScale(
            width.toFloat() / noiseBitmap.width * NOISE_SCALE,
            height.toFloat() / noiseBitmap.height * NOISE_SCALE
        )
        
        noisePaint.shader = BitmapShader(noiseBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
        noisePaint.shader?.setLocalMatrix(matrix)
        
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), noisePaint)
        noisePaint.shader = null
    }
    
    /**
     * Обновляет цвета океана
     */
    fun updateOceanColors(
        deepColor: Int = DEEP_OCEAN_COLOR,
        surfaceColor: Int = SURFACE_OCEAN_COLOR,
        lightColor: Int = LIGHT_OCEAN_COLOR
    ) {
        invalidate()
    }
    
    /**
     * Обновляет параметры шума
     */
    fun updateNoiseSettings(
        opacity: Float = NOISE_OPACITY,
        scale: Float = NOISE_SCALE
    ) {
        noisePaint.alpha = (255 * opacity).toInt()
        invalidate()
    }
}
