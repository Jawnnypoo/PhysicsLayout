package com.jawnnypoo.physicslayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.RelativeLayout;

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
 * Layout that simulates physics on its direct child views
 * Created by Jawn on 4/9/2015.
 */
public class PhysicsLayout extends RelativeLayout {

    private static final String TAG = PhysicsLayout.class.getSimpleName();

    private static final float EARTH_GRAVITY = 9.8f;
    //50 pixels for every meter
    private static final float RENDER_TO_PHYSICS_RATIO = 50.0f;

    private static final int BOUND_SIZE_DP = 4;
    private static final float FRAME_RATE = 1/60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 5;

    private boolean debugDraw = true;
    private Paint debugPaint;
    private World world;
    private ArrayList<Body> bounds = new ArrayList<>();
    private boolean enablePhysics;
    private int width;
    private int height;
    private float density;

    public PhysicsLayout(Context context) {
        super(context);
        init();
    }

    public PhysicsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PhysicsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public PhysicsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        debugPaint = new Paint();
        debugPaint.setColor(Color.MAGENTA);
        debugPaint.setAlpha(100);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
        width = w;
        height = h;
        createWorld();
        //TODO only do this if configured
        //enablePhysics();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout");
        createWorld();
        createAllViewBodies();
    }

    public void enablePhysics() {
        enablePhysics = true;
        invalidate();
    }

    public void disablePhysics() {
        enablePhysics = false;
    }

    public boolean isPhysicsEnabled() {
        return enablePhysics;
    }

    public void setGravity(Vec2 gravity) {
        world.setGravity(gravity);
    }

    private void createWorld() {
        //Null out all the bodies
        for (int i = 0; i < getChildCount(); i++) {
           getChildAt(i).setTag(R.id.physics_layout_body_tag, null);
        }
        bounds.clear();
        Log.d(TAG, "createWorld");
        //TODO do we need to remove the old views from the world?
        //bodies.clear();
        world = new World(new Vec2(0, EARTH_GRAVITY));
        addBounds();
    }

    private void createAllViewBodies() {
        for (int i = 0; i < getChildCount(); i++) {
            createBody(getChildAt(i));
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
    }

    public void resetPhysics() {
        View view;
        for (int i = 0; i < getChildCount(); i++) {
            view = getChildAt(i);
            view.setTranslationX(0);
            view.setTranslationY(0);
        }
        createWorld();
        createAllViewBodies();
    }

    public void setDebugDrawPhysicsBounds(boolean value) {
        debugDraw = value;
    }

    private final OnDragListener mDragListener = new OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            Log.d(TAG, "onDrag " + event.getX() +" " +  event.getY());
            if (event.getAction() == DragEvent.ACTION_DRAG_ENDED || event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
                v.setTranslationX(event.getX());
            }
            return false;
        }
    };

    private final OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick");
        }
    };

    public void setFling(boolean allow) {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setOnDragListener(allow ? mDragListener : null);
            //getChildAt(i).setOnClickListener(allow ? mOnClickListener : null);
        }
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

        bodyDef.position.set(0, pxToM(height + boundSize/2));
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
        bottomBody.setUserData(new PhysicsData(-1, boxWidth, boxHeight));
        bounds.add(bottomBody);

        bodyDef.position.set(0, pxToM(-boundSize/2));
        Body topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);
        topBody.setUserData(new PhysicsData(-1, boxWidth, boxHeight));
        bounds.add(topBody);
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
        int boxWidth = (int) pxToM(view.getWidth()/2);
        int boxHeight = (int) pxToM(view.getHeight()/2);
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

    public void giveRandomImpulse() {
        Body body;
        Vec2 impulse;
        Random random = new Random();
        for (int i = 0; i < getChildCount(); i++) {
            impulse = new Vec2(random.nextInt(5000) - 5000, random.nextInt(5000) - 5000);
            body = (Body) getChildAt(i).getTag(R.id.physics_layout_body_tag);
            body.applyLinearImpulse(impulse, body.getPosition());
        }
    }

    private Vec2 getWorldPositionFromView(View view) {
        return new Vec2(pxToM(view.getX()), pxToM(view.getY()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (enablePhysics) {

            world.step(FRAME_RATE, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            View view;
            Body body;
            PhysicsData data;
            for (int i = 0; i < getChildCount(); i++) {
                view = getChildAt(i);
                body = (Body) view.getTag(R.id.physics_layout_body_tag);
                if (body != null) {
                    data = (PhysicsData) body.getUserData();
                    //Log.d(TAG, "Position for " + i + " :" + body.getPosition());

                    //TODO figure out good ratio for mToPx
                    //Tranlate over, since Box2d origin is the center
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
                //view.setY(view.getY()+1);
            }
            PhysicsData physicsData;
            for (Body bound : bounds) {
                physicsData = (PhysicsData) bound.getUserData();
                canvas.drawRect(
                        mToPx(bound.getPosition().x) -  mToPx(physicsData.width),
                        mToPx(bound.getPosition().y) - mToPx(physicsData.height),
                        mToPx(bound.getPosition().x) + mToPx(physicsData.width),
                        mToPx(bound.getPosition().y) + mToPx(physicsData.height),
                        debugPaint);
            }
            invalidate();
        }
    }

    private static float mToPx(float meters) {
        return meters * RENDER_TO_PHYSICS_RATIO;
    }

    private static float pxToM(float pixels) {
        return pixels / RENDER_TO_PHYSICS_RATIO;
    }

    /**
     * Allows us to store needed data on a physics body,
     */
    private static class PhysicsData {
        public int viewId;
        public int width;
        public int height;

        /**
         * Init physics data
         * @param width in meters
         * @param height in meters
         */
        public PhysicsData(int viewId, int width, int height) {
            this.viewId = viewId;
            this.width = width;
            this.height = height;
        }

    }
}
