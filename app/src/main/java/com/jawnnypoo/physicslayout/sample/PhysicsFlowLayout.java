package com.jawnnypoo.physicslayout.sample;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsLayoutParams;
import com.jawnnypoo.physicslayout.PhysicsLayoutParamsProcessor;
import com.wefika.flowlayout.FlowLayout;

import androidx.annotation.NonNull;

/**
 * Typical FrameLayout with some physics added on. Call {@link #getPhysics()} to get the
 * physics component.
 */
public class PhysicsFlowLayout extends FlowLayout {

    private Physics physics;

    public PhysicsFlowLayout(Context context) {
        super(context);
        init(null);
    }

    public PhysicsFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PhysicsFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        setWillNotDraw(false);
        physics = new Physics(this, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        physics.onSizeChanged(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        physics.onLayout(changed);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        physics.onDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return physics.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        return physics.onTouchEvent(event);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public Physics getPhysics() {
        return physics;
    }

    private static class LayoutParams extends FlowLayout.LayoutParams implements PhysicsLayoutParams {

        PhysicsConfig config;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            config = PhysicsLayoutParamsProcessor.process(c, attrs);
        }

        @Override
        public PhysicsConfig getConfig() {
            return config;
        }
    }
}
