package com.jawnnypoo.physicslayout;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation for physics layout is found here, since we want to offer the main
 * layouts without requiring further extension (LinearLayout, RelativeLayout etc)
 * Created by Jawn on 4/29/2015.
 */
public class Physics {

    private static final String TAG = Physics.class.getSimpleName();

    public static final float NO_GRAVITY = 0.0f;
    public static final float MOON_GRAVITY = 1.6f;
    public static final float EARTH_GRAVITY = 9.8f;
    public static final float JUPITER_GRAVITY = 24.8f;
    //50 pixels for every meter
    private static final float RENDER_TO_PHYSICS_RATIO = 50.0f;
    //Size in DP of the bounds (world walls) of the view
    private static final int BOUND_SIZE_DP = 20;
    private static final float FRAME_RATE = 1/60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 5;

    private static float mToPx(float meters) {
        return meters * RENDER_TO_PHYSICS_RATIO;
    }

    private static float pxToM(float pixels) {
        return pixels / RENDER_TO_PHYSICS_RATIO;
    }

    private static float radiansToDegrees(float radians) {
        return radians/3.14f*180f;
    }

    private boolean debugDraw = true;

    private World world;
    private ArrayList<Body> bounds = new ArrayList<>();
    private float gravityX = 0.0f;
    private float gravityY = EARTH_GRAVITY;
    private boolean enablePhysics = true;
    private boolean hasBounds = true;

    private ViewGroup viewGroup;
    private Paint debugPaint;
    private float density;
    private int width;
    private int height;

    /**
     * Call this when your view is created (remember to call from each possible constructor). Pass
     * the layout (extends ViewGroup) that you want to apply physics to.
     */
    public Physics(ViewGroup viewGroup) {
        this(viewGroup, null);
    }

    public Physics(ViewGroup viewGroup, AttributeSet attrs) {
        this.viewGroup = viewGroup;
        debugPaint = new Paint();
        debugPaint.setColor(Color.MAGENTA);
        debugPaint.setStyle(Paint.Style.STROKE);
        density = viewGroup.getResources().getDisplayMetrics().density;
        if (attrs != null) {
            TypedArray a = viewGroup.getContext().obtainStyledAttributes(attrs, R.styleable.Physics);
            enablePhysics = a.getBoolean(R.styleable.Physics_physics, enablePhysics);
            gravityX = a.getFloat(R.styleable.Physics_gravityX, gravityX);
            gravityY = a.getFloat(R.styleable.Physics_gravityY, gravityY);
            hasBounds = a.getBoolean(R.styleable.Physics_bounds, hasBounds);
            a.recycle();
        }
    }

    /**
     * Call this every time your view gets a call to onSizeChanged so that the world can
     * respond to this change.
     * @param width
     * @param height
     */
    public void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Call this every time your view gets a call to onLayout so that the world can
     * respond to this change.
     */
    public void onLayout(boolean changed) {
        Log.d(TAG, "onLayout");
        if (world == null || changed) {
            createWorld();
            createAllViewBodies();
        }
    }

    /**
     * Call this when your view calls onDraw so that physics can be processed
     * @param canvas
     */
    public void onDraw(Canvas canvas) {
        if (!enablePhysics) {
            return;
        }
        //TODO do this on a different thread. Has potential to cause ANRs
        world.step(FRAME_RATE, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        View view;
        Body body;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            view = viewGroup.getChildAt(i);
            body = (Body) view.getTag(R.id.physics_layout_body_tag);
            if (body != null) {
                view.setX(mToPx(body.getPosition().x) - view.getWidth()/2);
                view.setY(mToPx(body.getPosition().y) - view.getHeight()/2);
                view.setRotation(radiansToDegrees(body.getAngle()));
                if (debugDraw) {
                    //TODO figure out if circle or rect and draw accordingly
                    canvas.drawRect(
                            mToPx(body.getPosition().x) - view.getWidth()/2,
                            mToPx(body.getPosition().y) - view.getHeight()/2,
                            mToPx(body.getPosition().x) + view.getWidth()/2,
                            mToPx(body.getPosition().y) + view.getHeight()/2,
                            debugPaint);
                }
            }
        }
        viewGroup.invalidate();
    }

    private void createWorld() {
        //Null out all the bodies
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setTag(R.id.physics_layout_body_tag, null);
        }
        bounds.clear();
        Log.d(TAG, "createWorld");
        //TODO do we need to remove the old views from the world?
        //bodies.clear();
        world = new World(new Vec2(gravityX, gravityY));
        if (hasBounds) {
            enableBounds();
        }
    }

    private void createAllViewBodies() {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            createBody(viewGroup.getChildAt(i));
        }
    }

    private void enableBounds() {
        hasBounds = true;
        createTopAndBottomBounds();
        createLeftAndRightBounds();
    }

    private void disableBounds() {
        hasBounds = false;
        for (Body body : bounds) {
            world.destroyBody(body);
        }
        bounds.clear();
    }

    private void createTopAndBottomBounds() {
        int boundSize = Math.round((float)BOUND_SIZE_DP * density);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC; //movable
        PolygonShape box = new PolygonShape();
        int boxWidth = (int) pxToM(width);
        int boxHeight = (int) pxToM(boundSize);
        box.setAsBox(boxWidth, boxHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        bodyDef.position.set(0, -boxHeight / 2);
        Body topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);
        bounds.add(topBody);

        bodyDef.position.set(0, pxToM(height) - boxHeight / 2);
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
        bounds.add(bottomBody);
    }

    private void createLeftAndRightBounds() {
        int boundSize = Math.round((float)BOUND_SIZE_DP * density);
        int boxWidth = (int) pxToM(boundSize);
        int boxHeight = (int) pxToM(height);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        PolygonShape box = new PolygonShape();
        box.setAsBox(boxWidth, boxHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        bodyDef.position.set(-boxWidth / 2, 0);
        Body leftBody = world.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);
        bounds.add(leftBody);

        bodyDef.position.set(pxToM(width), pxToM(-boundSize / 2));
        Body rightBody = world.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
        bounds.add(rightBody);
    }

    private void createBody(View view) {
        PhysicsConfig config = (PhysicsConfig) view.getTag(R.id.physics_layout_config_tag);
        if (config == null) {
            config = PhysicsConfig.getDefaultConfig();
            view.setTag(R.id.physics_layout_config_tag, config);
        }
        BodyDef bodyDef = config.getBodyDef();
        bodyDef.position.set(pxToM(view.getX() + view.getWidth() / 2), pxToM(view.getY() + view.getHeight() / 2));

        FixtureDef fixtureDef = config.getFixtureDef();
        fixtureDef.shape = config.getShapeType() == PhysicsConfig.ShapeType.RECTANGLE ? getBoxShape(view) :
                getCircleShape(view, config);

        Body body  = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        view.setTag(R.id.physics_layout_body_tag, body);
    }

    private PolygonShape getBoxShape(View view) {
        PolygonShape box = new PolygonShape();
        float boxWidth = pxToM(view.getWidth() / 2);
        float boxHeight = pxToM(view.getHeight() / 2);
        box.setAsBox(boxWidth, boxHeight);
        return box;
    }

    private CircleShape getCircleShape(View view, PhysicsConfig config) {
        CircleShape circle = new CircleShape();
        float radius = config.getRadius();
        //radius was not set, set it to max of the width and height
        if (radius == -1) { radius = Math.max(view.getWidth() / 2, view.getHeight() / 2); }
        circle.m_radius = pxToM(radius);
        return circle;
    }

    /**
     * Finds the physics body that corresponds to the view. Requires the view to have an id.
     * Returns null if no body exists for the view
     * @param id the view's id of the body you want to retrieve
     * @return body that determines the views physics
     */
    public Body findBodyById(int id) {
        View view = viewGroup.findViewById(id);
        if (view != null) {
            return (Body) view.getTag(R.id.physics_layout_body_tag);
        }
        return null;
    }

    /**
     * Set the configuration that will be used when creating the view Body.
     * Changing view's configuration after layout has been performed will require you to call
     * requestLayout() so that the body can be created with the new configuration.
     * @param view
     * @param config
     */
    public void setPhysicsConfig(View view, PhysicsConfig config) {
        view.setTag(R.id.physics_layout_config_tag, config);
    }

    /**
     * Get the current Box2D world controlling the physics of this view
     * @return The Box2D world
     */
    public World getWorld() {
        return world;
    }

    public void enablePhysics() {
        enablePhysics = true;
        viewGroup.invalidate();
    }

    public void disablePhysics() {
        enablePhysics = false;
    }

    public boolean isPhysicsEnabled() {
        return enablePhysics;
    }

    public void resetPhysics() {
        View view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            view = viewGroup.getChildAt(i);
            view.setTranslationX(0);
            view.setTranslationY(0);
        }
        createWorld();
        createAllViewBodies();
    }

    public void giveRandomImpulse() {
        Body body;
        Vec2 impulse;
        Random random = new Random();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            impulse = new Vec2(random.nextInt(1000) - 1000, random.nextInt(1000) - 1000);
            body = (Body) viewGroup.getChildAt(i).getTag(R.id.physics_layout_body_tag);
            body.applyLinearImpulse(impulse, body.getPosition());
        }
    }

}
