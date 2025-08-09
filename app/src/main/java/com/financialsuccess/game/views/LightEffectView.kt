package com.financialsuccess.game.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * Кастомный View для создания двухкомпонентного светового эффекта
 * 
 * Компоненты:
 * A. Круговой спектр (аура) - радиальный градиент от центра
 * B. Динамические лучи - линейные градиенты от верхней границы
 * 
 * Оптимизации:
 * - Минимальная прозрачность для предотвращения перекрытия UI
 * - Убран BlurMaskFilter для производительности
 * - Условное рисование только при alpha > 0
 * - Эффективная очистка ресурсов
 * 
 * Настройки:
 * - SPECTRUM_CENTER_X_RATIO: позиция центра спектра (0.3f)
 * - SPECTRUM_CENTER_Y_RATIO: позиция центра спектра (0.2f)
 * - RAY_COUNT: количество лучей (5)
 * - RAY_WIDTH: ширина луча (80f)
 */
class LightEffectView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val rayPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path = Path()
    
    // Константы для настройки света
    companion object {
        const val SPECTRUM_CENTER_X_RATIO = 0.3f    // Позиция центра спектра по X (относительно ширины)
        const val SPECTRUM_CENTER_Y_RATIO = 0.2f    // Позиция центра спектра по Y (относительно высоты)
        const val SPECTRUM_RADIUS_RATIO = 1.5f      // Радиус спектра (относительно диагонали экрана)
        const val RAY_COUNT = 5                      // Количество световых лучей
        const val RAY_WIDTH = 80f                    // Ширина луча в пикселях
        const val RAY_LENGTH_RATIO = 0.8f           // Длина луча (относительно высоты экрана)
        const val RAY_SPREAD_ANGLE = 60f            // Угол расхождения лучей в градусах
        val BRIGHT_COLOR = Color.rgb(255, 255, 220)  // Яркий бело-жёлтый
        val MEDIUM_COLOR = Color.rgb(200, 220, 255)  // Средний бело-голубой
        val FADE_COLOR = Color.TRANSPARENT     // Прозрачный
    }
    
    // Анимационные параметры
    var spectrumAlpha: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    
    var raysAlpha: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    
    var spectrumScale: Float = 0.8f
        set(value) {
            field = value
            invalidate()
        }
    
    var raysRotation: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    
    init {
        setupPaints()
    }
    
    private fun setupPaints() {
        // Настройка для спектра
        paint.isAntiAlias = true
        paint.isDither = true
        
        // Настройка для лучей - УБИРАЕМ BlurMaskFilter
        rayPaint.isAntiAlias = true
        rayPaint.isDither = true
        // rayPaint.maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL) // УБРАНО
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Включаем только круговой спектр с очень низкой прозрачностью
        if (spectrumAlpha > 0f) {
            drawCircularSpectrum(canvas)
        }
        
        // Лучи пока отключены
        // if (raysAlpha > 0f) {
        //     drawLightRays(canvas)
        // }
    }
    
    private fun drawCircularSpectrum(canvas: Canvas) {
        val centerX = width * SPECTRUM_CENTER_X_RATIO
        val centerY = height * SPECTRUM_CENTER_Y_RATIO
        val radius = (Math.sqrt((width * width + height * height).toDouble()) * SPECTRUM_RADIUS_RATIO * spectrumScale).toFloat()
        
        // Создаем радиальный градиент для спектра
        val gradient = RadialGradient(
            centerX, centerY, radius,
            intArrayOf(
                Color.argb((spectrumAlpha * 30).toInt(), Color.red(BRIGHT_COLOR), Color.green(BRIGHT_COLOR), Color.blue(BRIGHT_COLOR)), // Уменьшили с 100 до 30
                Color.argb((spectrumAlpha * 20).toInt(), Color.red(MEDIUM_COLOR), Color.green(MEDIUM_COLOR), Color.blue(MEDIUM_COLOR)),   // Уменьшили с 60 до 20
                Color.argb((spectrumAlpha * 10).toInt(), Color.red(MEDIUM_COLOR), Color.green(MEDIUM_COLOR), Color.blue(MEDIUM_COLOR)),   // Уменьшили с 30 до 10
                FADE_COLOR
            ),
            floatArrayOf(0f, 0.3f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
        
        paint.shader = gradient
        canvas.drawCircle(centerX, centerY, radius, paint)
        paint.shader = null
    }
    
    private fun drawLightRays(canvas: Canvas) {
        val centerX = width / 2f
        val startY = 0f
        val rayLength = height * RAY_LENGTH_RATIO
        
        // Вычисляем углы для лучей
        val startAngle = -RAY_SPREAD_ANGLE / 2f + raysRotation
        val angleStep = RAY_SPREAD_ANGLE / (RAY_COUNT - 1)
        
        for (i in 0 until RAY_COUNT) {
            val angle = Math.toRadians((startAngle + i * angleStep).toDouble())
            val endX = centerX + (rayLength * Math.sin(angle)).toFloat()
            val endY = startY + (rayLength * Math.cos(angle)).toFloat()
            
            drawLightRay(canvas, centerX, startY, endX, endY)
        }
    }
    
    private fun drawLightRay(canvas: Canvas, startX: Float, startY: Float, endX: Float, endY: Float) {
        path.reset()
        
        // Вычисляем перпендикулярный вектор для создания ширины луча
        val dx = endX - startX
        val dy = endY - startY
        val length = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        
        if (length <= 0f) return
        
        val perpX = -dy / length * RAY_WIDTH / 2f
        val perpY = dx / length * RAY_WIDTH / 2f
        
        // Создаем путь для луча
        path.moveTo(startX - perpX, startY - perpY)
        path.lineTo(startX + perpX, startY + perpY)
        path.lineTo(endX + perpX, endY + perpY)
        path.lineTo(endX - perpX, endY - perpY)
        path.close()
        
        // Создаем линейный градиент для луча
        val gradient = LinearGradient(
            startX, startY, endX, endY,
            intArrayOf(
                Color.argb((raysAlpha * 80).toInt(), Color.red(BRIGHT_COLOR), Color.green(BRIGHT_COLOR), Color.blue(BRIGHT_COLOR)),  // Уменьшили с 255 до 80
                Color.argb((raysAlpha * 50).toInt(), Color.red(MEDIUM_COLOR), Color.green(MEDIUM_COLOR), Color.blue(MEDIUM_COLOR)),    // Уменьшили с 180 до 50
                Color.argb((raysAlpha * 20).toInt(), Color.red(MEDIUM_COLOR), Color.green(MEDIUM_COLOR), Color.blue(MEDIUM_COLOR)),    // Уменьшили с 100 до 20
                FADE_COLOR
            ),
            floatArrayOf(0f, 0.3f, 0.7f, 1f),
            Shader.TileMode.CLAMP
        )
        
        rayPaint.shader = gradient
        canvas.drawPath(path, rayPaint)
        rayPaint.shader = null
    }
    
    /**
     * Обновляет параметры спектра
     */
    fun updateSpectrumSettings(
        centerXRatio: Float = SPECTRUM_CENTER_X_RATIO,
        centerYRatio: Float = SPECTRUM_CENTER_Y_RATIO,
        radiusRatio: Float = SPECTRUM_RADIUS_RATIO
    ) {
        invalidate()
    }
    
    /**
     * Обновляет параметры лучей
     */
    fun updateRaysSettings(
        count: Int = RAY_COUNT,
        width: Float = RAY_WIDTH,
        lengthRatio: Float = RAY_LENGTH_RATIO,
        spreadAngle: Float = RAY_SPREAD_ANGLE
    ) {
        invalidate()
    }
    
    /**
     * Обновляет цвета света
     */
    fun updateLightColors(
        brightColor: Int = BRIGHT_COLOR,
        mediumColor: Int = MEDIUM_COLOR
    ) {
        invalidate()
    }
}
