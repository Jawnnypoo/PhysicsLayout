package com.jawnnypoo.physicslayout.sample

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent

import com.jawnnypoo.physicslayout.Physics
import com.jawnnypoo.physicslayout.PhysicsConfig
import com.jawnnypoo.physicslayout.PhysicsLayoutParams
import com.jawnnypoo.physicslayout.PhysicsLayoutParamsProcessor
import com.wefika.flowlayout.FlowLayout

/**
 * Typical FrameLayout with some physics added on. Call [physics] to get the
 * physics component.
 */
class PhysicsFlowLayout : FlowLayout {

    lateinit var physics: Physics

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return physics.onTouchEvent(event)
    }

    override fun generateLayoutParams(attrs: AttributeSet): LayoutParams {
        return LayoutParams(context, attrs)
    }

    class LayoutParams(c: Context, attrs: AttributeSet) : FlowLayout.LayoutParams(c, attrs),
        PhysicsLayoutParams {
        override var config: PhysicsConfig = PhysicsLayoutParamsProcessor.process(c, attrs)
    }
}
