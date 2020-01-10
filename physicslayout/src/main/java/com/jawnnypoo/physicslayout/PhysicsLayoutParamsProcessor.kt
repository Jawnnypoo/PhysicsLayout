package com.jawnnypoo.physicslayout

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import org.jbox2d.dynamics.BodyType

/**
 * Processes attributes from any PhysicsLayout and returns a [PhysicsConfig]
 */
object PhysicsLayoutParamsProcessor {

    /**
     * Processes the attributes on children Views
     *
     * @param c context
     * @param attrs attributes
     * @return the PhysicsConfig
     */
    fun process(c: Context, attrs: AttributeSet?): PhysicsConfig {
        val config = PhysicsConfig()
        val array = c.obtainStyledAttributes(attrs, R.styleable.Physics_Layout)
        processCustom(array, config)
        processBodyDef(array, config)
        processFixtureDef(array, config)
        array.recycle()
        return config
    }

    private fun processCustom(array: TypedArray, config: PhysicsConfig) {
        if (array.hasValue(R.styleable.Physics_Layout_layout_shape)) {
            val shape = when (array.getInt(R.styleable.Physics_Layout_layout_shape, 0)) {
                1 -> Shape.CIRCLE
                else -> Shape.RECTANGLE
            }
            config.shape = shape
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_circleRadius)) {
            val radius = array.getDimensionPixelSize(R.styleable.Physics_Layout_layout_circleRadius, -1)
            config.radius = radius.toFloat()
        }
    }

    private fun processBodyDef(array: TypedArray, config: PhysicsConfig) {
        if (array.hasValue(R.styleable.Physics_Layout_layout_bodyType)) {
            val type = array.getInt(R.styleable.Physics_Layout_layout_bodyType, BodyType.DYNAMIC.ordinal)
            config.bodyDef.type = BodyType.values()[type]
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_fixedRotation)) {
            val fixedRotation = array.getBoolean(R.styleable.Physics_Layout_layout_fixedRotation, false)
            config.bodyDef.fixedRotation = fixedRotation
        }
    }

    private fun processFixtureDef(array: TypedArray, config: PhysicsConfig) {
        if (array.hasValue(R.styleable.Physics_Layout_layout_friction)) {
            val friction = array.getFloat(R.styleable.Physics_Layout_layout_friction, -1f)
            config.fixtureDef.friction = friction
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_restitution)) {
            val restitution = array.getFloat(R.styleable.Physics_Layout_layout_restitution, -1f)
            config.fixtureDef.restitution = restitution
        }
        if (array.hasValue(R.styleable.Physics_Layout_layout_density)) {
            val density = array.getFloat(R.styleable.Physics_Layout_layout_density, -1f)
            config.fixtureDef.density = density
        }
    }
}
