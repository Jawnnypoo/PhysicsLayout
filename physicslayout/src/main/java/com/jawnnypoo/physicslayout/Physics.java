package com.jawnnypoo.physicslayout;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

    private boolean debugDraw = true;
    private boolean enablePhysics;

    private World world;
    private ArrayList<Body> bounds = new ArrayList<>();

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
        this.viewGroup = viewGroup;
        debugPaint = new Paint();
        debugPaint.setColor(Color.MAGENTA);
        debugPaint.setAlpha(100);
        density = viewGroup.getResources().getDisplayMetrics().density;
    }

    public void onSizeChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void onLayout() {
        createWorld();
        createAllViewBodies();
    }

    public void onDraw(Canvas canvas) {
        if (enablePhysics) {
            world.step(FRAME_RATE, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            View view;
            Body body;
            PhysicsData data;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                view = viewGroup.getChildAt(i);
                body = (Body) view.getTag(R.id.physics_layout_body_tag);
                if (body != null) {
                    data = (PhysicsData) body.getUserData();
                    //Log.d(TAG, "Position for " + i + " :" + body.getPosition());
                    view.setX(mToPx(body.getPosition().x) - mToPx(data.width));
                    view.setY(mToPx(body.getPosition().y) - mToPx(data.height));
                    if (debugDraw) {
                        canvas.drawRect(
                                mToPx(body.getPosition().x) - mToPx(data.width),
                                mToPx(body.getPosition().y) - mToPx(data.height),
                                mToPx(body.getPosition().x) + mToPx(data.width),
                                mToPx(body.getPosition().y) + mToPx(data.height),
                                debugPaint);
                    }
                }
            }
            viewGroup.invalidate();
        }
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
        world = new World(new Vec2(0, EARTH_GRAVITY));
        addBounds();
    }

    private void createAllViewBodies() {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            createBody(viewGroup.getChildAt(i));
        }
    }

    private void addBounds() {
        createTopAndBottomBounds();
        createLeftAndRightBounds();
    }

    private void removeBounds() {
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

        bodyDef.position.set(0, -boxHeight/2);
        Body topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);
        topBody.setUserData(new PhysicsData(-1, boxWidth, boxHeight));
        bounds.add(topBody);

        bodyDef.position.set(0, pxToM(height) - boxHeight/2);
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
        bottomBody.setUserData(new PhysicsData(-1, boxWidth, boxHeight));
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

        bodyDef.position.set(-boxWidth/2, 0);
        Body leftBody = world.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);
        leftBody.setUserData(new PhysicsData(-1, boxWidth, boxHeight));
        bounds.add(leftBody);

        bodyDef.position.set(pxToM(width), pxToM(-boundSize/2));
        Body rightBody = world.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
        rightBody.setUserData(new PhysicsData(-1, boxWidth, boxHeight));
        bounds.add(rightBody);
    }

    private void createBody(View view) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DYNAMIC; //movable
        //TODO allow for rotation?
        bodyDef.fixedRotation = true;
        bodyDef.position.set(pxToM(view.getX() + view.getWidth()/2), pxToM(view.getY() + view.getHeight()/2));
        PolygonShape box = new PolygonShape();
        float boxWidth = pxToM(view.getWidth()/2);
        float boxHeight = pxToM(view.getHeight()/2);
        box.setAsBox(boxWidth, boxHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        Body body  = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        body.setUserData(new PhysicsData(view.getId(), boxWidth, boxHeight));
        view.setTag(R.id.physics_layout_body_tag, body);
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

    public void setDebugDrawPhysicsBounds(boolean value) {
        debugDraw = value;
    }

    public void setGravity(Vec2 gravity) {
        world.setGravity(gravity);
    }

    /**
     * Allows us to store needed data on a physics body,
     */
    private static class PhysicsData {
        public int viewId;
        public float width;
        public float height;

        /**
         * Init physics data
         * @param width in meters
         * @param height in meters
         */
        public PhysicsData(int viewId, float width, float height) {
            this.viewId = viewId;
            this.width = width;
            this.height = height;
        }

    }
}
