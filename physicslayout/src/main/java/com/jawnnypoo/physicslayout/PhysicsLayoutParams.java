package com.jawnnypoo.physicslayout;

/**
 * Signifies that the LayoutParams are able to provide a PhysicsConfig. You still need to create a
 * {@link android.view.ViewGroup.LayoutParams} implementation using the appropriate subclass, and
 * making use of {@link PhysicsLayoutParamsProcessor}
 */
public interface PhysicsLayoutParams {
    PhysicsConfig getConfig();
}
