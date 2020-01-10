package com.jawnnypoo.physicslayout

import org.jbox2d.dynamics.BodyDef
import org.jbox2d.dynamics.BodyType
import org.jbox2d.dynamics.FixtureDef

/**
 * Configuration used when creating the [org.jbox2d.dynamics.Body] for each of the views in the view group
 */
data class PhysicsConfig(
    /**
     * The shape of the physics body, either rectangle or circle. This changes how the body will
     * collide with other bodies.
     */
    var shape: Shape = Shape.RECTANGLE,
    /**
     * The fixture definition. Leave alone if you want the defaults. Learn more: [FixtureDef]
     */
    var fixtureDef: FixtureDef = createDefaultFixtureDef(),
    /**
     * The body definition. Leave alone if you want the defaults. Learn more: [BodyDef]
     */
    var bodyDef: BodyDef = createDefaultBodyDef()
) {

    /**
     * Only used if shape == CIRCLE, otherwise it is ignored. The radius of the circle in pixels.
     * Will be processed and set by its view size.
     */
    internal var radius: Float = -1f

    companion object {

        fun createDefaultFixtureDef(): FixtureDef {
            val fixtureDef = FixtureDef()
            fixtureDef.friction = 0.3f
            fixtureDef.restitution = 0.2f
            fixtureDef.density = 0.2f
            return fixtureDef
        }

        fun createDefaultBodyDef(): BodyDef {
            val bodyDef = BodyDef()
            bodyDef.type = BodyType.DYNAMIC //movable by default
            return bodyDef
        }
    }
}
