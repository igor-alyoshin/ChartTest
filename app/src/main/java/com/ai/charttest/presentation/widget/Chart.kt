package com.ai.charttest.presentation.widget

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import androidx.core.animation.addListener
import androidx.core.os.bundleOf


class Chart @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private val paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.STROKE
        strokeWidth = 2f
        isAntiAlias = true
    }
    private val axisPaint = Paint().apply {
        color = Color.GRAY
        style = Paint.Style.STROKE
        strokeWidth = 1f
        isAntiAlias = true
    }
    private val linePath = Path()
    private var lines: List<List<Float>>? = null
    private var maxX = Float.MIN_VALUE
    private var minX = Float.MAX_VALUE
    private var maxY = Float.MIN_VALUE
    private var minY = Float.MAX_VALUE

    private val gestureDetector by lazy { GestureDetector(context, GestureListener()) }
    private val scaleDetector by lazy { ScaleGestureDetector(context, ScaleListener()) }
    private var scaleFactor = 1f
    private var maxScreenScaleFactor = 1f
    private var scaleAnimator: ValueAnimator? = null

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        maxScreenScaleFactor = getMaxScreenScaleFactor()
    }

    fun setPoints(points: List<Pair<Float, Float>>) {
        lines = points.map { point ->
            val x = point.first
            val y = point.second
            maxX = x.coerceAtLeast(maxX)
            minX = x.coerceAtMost(minX)
            maxY = y.coerceAtLeast(maxY)
            minY = y.coerceAtMost(minY)
            arrayListOf(x, y)
        }
            .flatten()
            .windowed(4, 2)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        //draw axis
        linePath.reset()
        linePath.moveTo(width / 2f, 0f)
        linePath.lineTo(width / 2f, height.toFloat())
        linePath.moveTo(0f, height / 2f)
        linePath.lineTo(width.toFloat(), height / 2f)
        canvas.drawPath(linePath, axisPaint)
        //draw lines
        linePath.reset()
        lines?.forEach { line ->
            val x1 = getScaledX(line[0])
            val y1 = getScaledY(line[1])
            val x2 = getScaledX(line[2])
            val y2 = getScaledY(line[3])
            if (x1 == x2 && y1 == y2) return@forEach
            linePath.moveTo(x1, y1)
            linePath.cubicTo((x1 + x2) / 2, y1, (x1 + x2) / 2, y2, x2, y2)
        }
        canvas.drawPath(linePath, paint)
    }

    override fun onSaveInstanceState(): Parcelable? {
        return bundleOf(
            PROPERTY_SUPER_STATE to super.onSaveInstanceState(),
            PROPERTY_SCALE_FACTOR to scaleFactor
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var parentState = state
        if (state is Bundle) {
            scaleFactor = state.getFloat(PROPERTY_SCALE_FACTOR)
            parentState = state.getParcelable(PROPERTY_SUPER_STATE)
        }
        super.onRestoreInstanceState(parentState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(e)
        scaleDetector.onTouchEvent(e)
        return true
    }

    /**
     * default 1 if data not initialized
     */
    private fun getMaxScreenScaleFactor(): Float {
        val deltaX = maxX - minX
        val deltaY = maxY - minY
        val zoomX = if (deltaX > 0 && width > 0) width / deltaX else 1f
        val zoomY = if (deltaY > 0 && height > 0) height / deltaY else 1f
        return MAX_SCALE_FACTOR * zoomX.coerceAtMost(zoomY)
    }

    private fun getScaledX(x: Float): Float {
        val scale =
            ((maxX - minX) / width).coerceAtLeast(1f) / scaleFactor
        val offset = (width / 2) - ((maxX - minX) / (2 * scale))
        return offset + (x - minX) / scale
    }

    private fun getScaledY(y: Float): Float {
        val scale =
            ((maxY - minY) / height).coerceAtLeast(1f) / scaleFactor
        val offset = (height / 2) - ((maxY - minY) / (2 * scale))
        return offset + (y - minY) / scale
    }

    private inner class ScaleListener : SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (scaleAnimator != null) return false
            scaleFactor *= detector.scaleFactor
            scaleFactor =
                MIN_SCALE_FACTOR.coerceAtLeast(scaleFactor.coerceAtMost(maxScreenScaleFactor))
            invalidate()
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            val fullScreenScaleFactor = maxScreenScaleFactor / MAX_SCALE_FACTOR
            when {
                scaleFactor < fullScreenScaleFactor -> animateScale(fullScreenScaleFactor)
                scaleFactor < maxScreenScaleFactor -> animateScale(maxScreenScaleFactor)
                else -> animateScale(DEFAULT_SCALE_FACTOR)
            }
            return true
        }
    }

    private fun animateScale(scale: Float) {
        scaleAnimator?.cancel()
        scaleAnimator = ValueAnimator.ofFloat(scaleFactor, scale).apply {
            duration = DURATION_MS
            addUpdateListener { animation ->
                val value = animation.animatedValue as Float
                scaleFactor = value
                invalidate()
            }
            addListener(
                onCancel = { scaleAnimator = null },
                onEnd = { scaleAnimator = null }
            )
        }
        scaleAnimator?.start()
    }

    companion object {
        private const val MIN_SCALE_FACTOR = 0.1f
        private const val DEFAULT_SCALE_FACTOR = 1f
        private const val MAX_SCALE_FACTOR = 3f
        private const val DURATION_MS = 250L

        private const val PROPERTY_SCALE_FACTOR = "PROPERTY_SCALE_FACTOR"
        private const val PROPERTY_SUPER_STATE = "PROPERTY_SUPER_STATE"
    }
}