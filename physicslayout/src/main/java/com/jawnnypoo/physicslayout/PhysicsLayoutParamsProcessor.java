package com.jawnnypoo.physicslayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Processes attributes from any PhysicsLayout and returns a {@link com.jawnnypoo.physicslayout.PhysicsConfig}
 */
public class PhysicsLayoutParamsProcessor {

    @SuppressWarnings("WrongConstant")
    public static PhysicsConfig process(Context c, AttributeSet attrs) {
        PhysicsConfig config = PhysicsConfig.create();
        TypedArray array = c.obtainStyledAttributes(attrs, R.styleable.Physics_Layout);
        if (array.hasValue(R.styleable.Physics_Layout_layout_shape)) {
            int shape = array.getInt(R.styleable.Physics_Layout_layout_shape, PhysicsConfig.SHAPE_TYPE_RECTANGLE);
            config.shapeType = shape;
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_fixedRotation)) {
            boolean fixedRotation = array.getBoolean(R.styleable.Physics_Layout_layout_fixedRotation, false);
            config.bodyDef.fixedRotation = fixedRotation;
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_circleRadius)) {
            int radius = array.getDimensionPixelSize(R.styleable.Physics_Layout_layout_circleRadius, -1);
            config.radius = radius;
        }
        array.recycle();
        return config;
    }
}
