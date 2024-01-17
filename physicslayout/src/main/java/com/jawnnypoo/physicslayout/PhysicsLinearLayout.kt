package com.jawnnypoo.physicslayout

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.LinearLayout

/**
 * Typical LinearLayout with some physics added on. Call [physics] to get the
 * physics component.
 */
@Suppress("unused")
open class PhysicsLinearLayout : LinearLayout {

    lateinit var physics: Physics

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    @TargetApi(21)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        setWillNotDraw(false)
        physics = Physics(this, attrs)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        physics.onSizeChanged(w, h)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        physics.onLayout(changed)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        physics.onDraw(canvas)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return physics.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return physics.onTouchEvent(event)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    class LayoutParams(c: Context, attrs: AttributeSet?) : LinearLayout.LayoutParams(c, attrs),
        PhysicsLayoutParams {
        override var config: PhysicsConfig = PhysicsLayoutParamsProcessor.process(c, attrs)
    }
}
