package com.jawnnypoo.physicslayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

/**
 * Processes attributes from any PhysicsLayout and returns a {@link com.jawnnypoo.physicslayout.PhysicsConfig.Builder}
 */
public class PhysicsLayoutParamsProcessor {

    @SuppressWarnings("WrongConstant")
    public static PhysicsConfig process(Context c, AttributeSet attrs) {
        PhysicsConfig.Builder builder = new PhysicsConfig.Builder();
        TypedArray array = c.obtainStyledAttributes(attrs, R.styleable.Physics_Layout);
        if (array.hasValue(R.styleable.Physics_Layout_layout_shape)) {
            int shape = array.getInt(R.styleable.Physics_Layout_layout_shape, PhysicsConfig.SHAPE_TYPE_RECTANGLE);
            builder.setShapeType(shape);
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_allowRotation)) {
            boolean allowRotation = array.getBoolean(R.styleable.Physics_Layout_layout_allowRotation, true);
            builder.setAllowRotation(allowRotation);
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_circleRadius)) {
            int radius = array.getDimensionPixelSize(R.styleable.Physics_Layout_layout_circleRadius, 0);
            builder.setRadius(radius);
        }
        array.recycle();
        return builder.build();
    }
}
