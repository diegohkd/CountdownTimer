package example.com.countdowntimerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CountdownTimerView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    // region draw properties

    private val backgroundRect: RectF by lazy { RectF() }
    private val backgroundPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
    private var cornerRadiusPx: Float = 0f
    private var customBackgroundColor = Color.GREEN

    // endregion

    // region default values

    private val defaultCornerRadiusDp: Float = 0f
    private var defaultBackgroundColor = Color.GREEN

    // endregion

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
    }

    // region private

    private fun setupView() {
        setupBackground()
    }

    private fun setupBackground() {
        cornerRadiusPx = (defaultCornerRadiusDp * resources.displayMetrics.density).round()
        customBackgroundColor = defaultBackgroundColor

        backgroundPaint.color = customBackgroundColor
    }

    private fun calculateMeasuredWidthCanvas(widthMeasureSpec: Int): Int {
        val desiredWidth = 0

        // Reconcile size that this view wants to be with the size the parent will let it be.
        return resolveSize(desiredWidth, widthMeasureSpec)
    }

    private fun calculateMeasuredHeightCanvas(heightMeasureSpec: Int): Int {
        val desiredHeight = 0

        // Reconcile size that this view wants to be with the size the parent will let it be.
        return resolveSize(desiredHeight, heightMeasureSpec)
    }

    private fun drawBackground(canvas: Canvas, canvasWidth: Float, canvasHeight: Float) {
        backgroundRect.set(0f, 0f, canvasWidth, canvasHeight)
        canvas.drawRoundRect(backgroundRect, cornerRadiusPx, cornerRadiusPx, backgroundPaint)
    }

    // endregion
}