package com.jawnnypoo.physicslayout;

import android.support.annotation.IntDef;

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Configuration used when creating the {@link org.jbox2d.dynamics.Body} for each of the views in the view group
 */
public class PhysicsConfig {

    public static final int SHAPE_TYPE_RECTANGLE = 0;
    public static final int SHAPE_TYPE_CIRCLE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHAPE_TYPE_RECTANGLE, SHAPE_TYPE_CIRCLE})
    public @interface ShapeType {}

    /**
     * Create a {@link PhysicsConfig} with some nice defaults
     * @return the newly created {@link PhysicsConfig}
     */
    public static PhysicsConfig create() {
        PhysicsConfig config = new PhysicsConfig();
        config.shapeType = SHAPE_TYPE_RECTANGLE;
        config.fixtureDef = createDefaultFixtureDef();
        config.bodyDef = createDefaultBodyDef();
        config.radius = -1;
        return config;
    }

    public static FixtureDef createDefaultFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.2f;
        fixtureDef.density = 0.2f;
        return new FixtureDef();
    }

    public static BodyDef createDefaultBodyDef() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC; //movable by default
        return bodyDef;
    }

    public @ShapeType int shapeType;
    public FixtureDef fixtureDef;
    public BodyDef bodyDef;

    /**
     * Only used if ShapeType == CIRCLE, otherwise it is ignored. The radius of the circle in pixels
     */
    public float radius;

    /**
     * Creation only allowed through static method so that we can ensure nice defaults
     */
    private PhysicsConfig() {

    }
}
