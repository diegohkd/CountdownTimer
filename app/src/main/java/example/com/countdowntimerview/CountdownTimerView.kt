package example.com.countdowntimerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.media.SoundPool
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat

class CountdownTimerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // region draw properties

    private val backgroundRect: RectF by lazy { RectF() }
    private val backgroundPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private val numberPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var cornerRadiusPx: Float = 0f
    private var customBackgroundColor = Color.GREEN

    // endregion

    // region default values

    private val defaultCornerRadiusDp: Float = 16f
    private var defaultBackgroundColor = Color.GREEN
    private var defaultTextColor = Color.WHITE
    private var defaultTextSizeSp = 64f

    // endregion

    // region animation

    private val bounceAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(context, R.anim.cycle_bounce)
    }

    // endregion

    // region sound effects

    private var soundPool: SoundPool? = null
    private var countdownSoundId: Int? = null
    private var buzzSoundId: Int? = null

    // endregion

    private var countDownTimer: CountDownTimer? = null
    private var initialTime = 10
    private var currentTime = 0

    init {
        setupView()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = calculateMeasuredWidthCanvas(widthMeasureSpec)
        val measuredHeight = calculateMeasuredHeightCanvas(heightMeasureSpec)

        // Store the final measured dimensions
        // App throws IllegalStateException if this is not called
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        val canvasWidth = width.toFloat()
        val canvasHeight = height.toFloat()

        drawBackground(canvas, canvasWidth, canvasHeight)
        drawNumberText(canvas, canvasWidth, canvasHeight)
    }

    fun start() {
        currentTime = initialTime
        countDownTimer = object : CountDownTimer(currentTime * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (millisUntilFinished < (currentTime - 1) * 1000) // ignore first tick
                    decrementTime()
            }

            override fun onFinish() {
                decrementTime()
            }
        }.start()
    }

    fun stop() {
        countDownTimer?.cancel()
    }

    // region private

    private fun setupView() {
        setupBackground()
        setupNumberText()
        setupTimer()
        setupSoundEffects()
    }

    private fun setupBackground() {
        cornerRadiusPx = (defaultCornerRadiusDp * resources.displayMetrics.density).round()
        customBackgroundColor = defaultBackgroundColor

        backgroundPaint.color = customBackgroundColor
    }

    private fun setupNumberText() {
        numberPaint.color = defaultTextColor
        numberPaint.textSize = (defaultTextSizeSp * resources.displayMetrics.scaledDensity)
    }

    private fun setupTimer() {
        currentTime = initialTime
    }

    private fun setupSoundEffects() {
        if (!isInEditMode) {
            soundPool = SoundPool.Builder().setMaxStreams(2).build()
            countdownSoundId = soundPool?.load(context, R.raw.sound_countdown, 0)
            buzzSoundId = soundPool?.load(context, R.raw.sound_buzz, 0)
        }
    }

    private fun calculateMeasuredWidthCanvas(widthMeasureSpec: Int): Int {
        // Measure maximum possible width of text
        val maxTextWidth = numberPaint.measureText(initialTime.toString())

        // Add padding to maximum width calculation
        val desiredWidth = (maxTextWidth + paddingLeft + paddingRight).toInt()

        // Reconcile size that this view wants to be with the size the parent will let it be.
        return resolveSize(desiredWidth, widthMeasureSpec)
    }

    private fun calculateMeasuredHeightCanvas(heightMeasureSpec: Int): Int {
        val fontMetrics = numberPaint.fontMetrics

        // Estimate maximum possible height of text
        val maxTextHeight = -fontMetrics.top + fontMetrics.bottom

        // Add padding to maximum height calculation
        val desiredHeight = (maxTextHeight + paddingTop + paddingBottom).toInt()

        // Reconcile size that this view wants to be with the size the parent will let it be.
        return resolveSize(desiredHeight, heightMeasureSpec)
    }

    private fun drawBackground(canvas: Canvas, canvasWidth: Float, canvasHeight: Float) {
        backgroundRect.set(0f, 0f, canvasWidth, canvasHeight)
        canvas.drawRoundRect(backgroundRect, cornerRadiusPx, cornerRadiusPx, backgroundPaint)
    }

    private fun drawNumberText(canvas: Canvas, canvasWidth: Float, canvasHeight: Float) {
        val displayedCount = currentTime.toString()

        // Measure the width of text to display
        val centerX = canvasWidth / 2
        val textWidth = numberPaint.measureText(displayedCount)

        // Figure out an x-coordinate that will center the text in the canvas
        val textX = (centerX - textWidth / 2).toInt().toFloat()
        // Figure out an y-coordinate that will center the text in the canvas
        val textY = (canvasHeight / 2 - (numberPaint.descent() + numberPaint.ascent()) / 2)
        // Draw
        canvas.drawText(displayedCount, textX, textY, numberPaint)
    }

    private fun decrementTime() {
        currentTime--

        backgroundPaint.color = when (currentTime) {
            0 -> ContextCompat.getColor(context, R.color.red)
            1 -> ContextCompat.getColor(context, R.color.red1)
            2 -> ContextCompat.getColor(context, R.color.red2)
            3 -> ContextCompat.getColor(context, R.color.red3)
            4 -> ContextCompat.getColor(context, R.color.red4)
            else -> customBackgroundColor
        }

        if (currentTime < 5) {
            if (currentTime > 0) {
                countdownSoundId?.let(::playSound)
            } else {
                buzzSoundId?.let(::playSound)
            }
            startAnimation(bounceAnimation)
        }

        invalidate()
    }

    private fun playSound(soundId: Int) {
        soundPool?.play(soundId, 1f, 1f, 1, 0, 1f)
    }

    // endregion
}