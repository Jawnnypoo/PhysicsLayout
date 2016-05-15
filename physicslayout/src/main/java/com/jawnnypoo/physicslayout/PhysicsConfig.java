package com.jawnnypoo.physicslayout;

import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * Configuration used when creating the body for each of the views in the view group
 */
public class PhysicsConfig {

    public enum ShapeType {
        RECTANGLE, CIRCLE
    }

    private ShapeType shapeType;
    private FixtureDef fixtureDef;
    private BodyDef bodyDef;
    //Only used if ShapeType == CIRCLE
    private float radius;

    /**
     * Creation only allowed through builder.
     */
    private PhysicsConfig() {

    }

    public ShapeType getShapeType() {
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

        public Builder setShapeType(ShapeType shapeType) {
            config.shapeType = shapeType;
            return this;
        }

        public Builder setBodyDefType(BodyType type) {
            config.bodyDef.type = type;
            return this;
        }

        public Builder setAllowRotation(boolean allowRotation) {
            config.bodyDef.fixedRotation = !allowRotation;
            return this;
        }

        public Builder setRadius(float radius) {
            if (config.shapeType != ShapeType.CIRCLE) {
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
        config.shapeType = ShapeType.RECTANGLE;
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
