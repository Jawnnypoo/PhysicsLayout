package com.jawnnypoo.physicslayout

/**
 * Signifies that the LayoutParams are able to provide a PhysicsConfig. You still need to create a
 * [android.view.ViewGroup.LayoutParams] implementation using the appropriate subclass, and
 * making use of [PhysicsLayoutParamsProcessor]. See [PhysicsFrameLayout] for an example
 */
interface PhysicsLayoutParams {
    val config: PhysicsConfig
}
