package com.jawnnypoo.physicslayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;

/**
 * Layout that simulates physics on its direct child views
 * Created by Jawn on 4/9/2015.
 */
public class PhysicsRelativeLayout extends RelativeLayout {

    private static final String TAG = PhysicsRelativeLayout.class.getSimpleName();

    private PhysicsLayoutDelegate physicsLayoutDelegate;

    public PhysicsRelativeLayout(Context context) {
        super(context);
        init();
    }

    public PhysicsRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhysicsRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public PhysicsRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        physicsLayoutDelegate = new PhysicsLayoutDelegate(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
        physicsLayoutDelegate.onSizeChanged(w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout");
        physicsLayoutDelegate.onLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        physicsLayoutDelegate.onDraw(canvas);
    }

    public PhysicsLayoutDelegate getPhysicsDelegate() {
        return physicsLayoutDelegate;
    }

}
