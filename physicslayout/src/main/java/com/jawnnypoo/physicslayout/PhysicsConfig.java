package com.jawnnypoo.physicslayout;

import android.support.annotation.IntDef;

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Configuration used when creating the body for each of the views in the view group
 */
public class PhysicsConfig {

    public static final int SHAPE_TYPE_RECTANGLE = 0;
    public static final int SHAPE_TYPE_CIRCLE = 1;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SHAPE_TYPE_RECTANGLE, SHAPE_TYPE_CIRCLE})
    public @interface ShapeType {}

    private @ShapeType int shapeType;
    private FixtureDef fixtureDef;
    private BodyDef bodyDef;
    //Only used if ShapeType == CIRCLE
    private float radius;

    /**
     * Creation only allowed through builder.
     */
    private PhysicsConfig() {

    }

    public @ShapeType int getShapeType() {
        return shapeType;
    }

    public FixtureDef getFixtureDef() {
        return fixtureDef;
    }

    public BodyDef getBodyDef() {
        return bodyDef;
    }

    public float getRadius() {
        return radius;
    }

    public static class Builder {
        private PhysicsConfig config;

        public Builder() {
            config = getDefaultConfig();
        }

        /**
         * Set the fixtureDef for the config. Note that this will override values set with calls such
         * as {@link #setDensity(float)}
         * @param fixtureDef the fixture def
         * @return builder
         */
        public Builder setFixtureDef(FixtureDef fixtureDef) {
            config.fixtureDef = fixtureDef;
            return this;
        }

        public Builder setDensity(float density) {
            config.fixtureDef.density = density;
            return this;
        }

        public Builder setFriction(float friction) {
            config.fixtureDef.friction = friction;
            return this;
        }

        public Builder setRestitution(float restitution) {
            config.fixtureDef.restitution = restitution;
            return this;
        }

        public Builder setShapeType(@ShapeType int shapeType) {
            config.shapeType = shapeType;
            return this;
        }

        /**
         * Set the {@link BodyDef} for the config. Note that this will override values set with calls such
         * as {@link #setAllowRotation(boolean)}
         * @param bodyDef the body def
         * @return builder
         */
        public Builder setBodyDef(BodyDef bodyDef) {
            config.bodyDef = bodyDef;
            return this;
        }

        /**
         * Shortcut to set the {@link BodyType} on the current {@link BodyDef}
         * @param type the body type
         * @return builder
         */
        public Builder setBodyDefType(BodyType type) {
            config.bodyDef.type = type;
            return this;
        }

        /**
         * Define if you would like to allow rotation or not
         * @param allowRotation true to allow rotation, false to disallow it
         * @return builder
         */
        public Builder setAllowRotation(boolean allowRotation) {
            config.bodyDef.fixedRotation = !allowRotation;
            return this;
        }

        /**
         * Set the radius. Only applicable if {@link #getShapeType()} is {@link #SHAPE_TYPE_CIRCLE}
         * @param radius the radius of the circle, in pixels
         * @return builder
         */
        public Builder setRadius(float radius) {
            if (config.shapeType != SHAPE_TYPE_CIRCLE) {
                throw new IllegalStateException("Can only set the radius if the shape is a circle");
            }
            config.radius = radius;
            return this;
        }

        public PhysicsConfig build() {
            return config;
        }
    }

    public static PhysicsConfig getDefaultConfig() {
        PhysicsConfig config = new PhysicsConfig();
        config.shapeType = SHAPE_TYPE_RECTANGLE;
        config.fixtureDef = getDefaultFixtureDef();
        config.bodyDef = getDefaultBodyDef();
        config.radius = -1;
        return config;
    }

    public static FixtureDef getDefaultFixtureDef() {
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;
        return fixtureDef;
    }

    public static BodyDef getDefaultBodyDef() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC; //movable
        bodyDef.fixedRotation = false; //allow rotation
        return bodyDef;
    }
}
