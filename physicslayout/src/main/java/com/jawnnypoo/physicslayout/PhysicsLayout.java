package com.jawnnypoo.physicslayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

/**
 * Created by Jawn on 4/9/2015.
 */
public class PhysicsLayout extends RelativeLayout {

    private static final String TAG = PhysicsLayout.class.getSimpleName();

    private static final float EARTH_GRAVITY = 9.8f;
    //10 pixels for every meter
    private static final int RENDER_TO_PHYSICS_RATIO = 10;

    private static final float FRAME_RATE = 1/60f;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 5;

    private Paint debugPaint;
    private World world;
    private ArrayList<Body> bounds = new ArrayList<>();
    private boolean enablePhysics;
    private int width;
    private int height;

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
        debugPaint = new Paint();
        debugPaint.setColor(Color.RED);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged");
        width = w;
        height = h;
        createWorld();
        //TODO only do this if configured
        enablePhysics();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout");
        for (int i = 0; i < getChildCount(); i++) {
            createBody(getChildAt(i));
        }
    }

    public void enablePhysics() {
        enablePhysics = true;
        invalidate();
    }

    public void disablePhysics() {
        enablePhysics = false;
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

    private void addBounds() {
        Body bottomBound = createBound(new Vec2[] { new Vec2(0.0F, pxToM(height)), new Vec2(pxToM(width), pxToM(height)) });
        bottomBound.setUserData(new PhysicsData(width, 4));
        bounds.add(bottomBound);
        //createBound(new Vec2[] { new Vec2(0.0F, 0.0F), new Vec2(pxToM(width), 0.0F) });
        //createBound(new Vec2[] { new Vec2(pxToM(width), 0.0F), new Vec2(pxToM(width), pxToM(height)) });
        Body leftBound = createBound(new Vec2[]{new Vec2(0.0F, 0.0F), new Vec2(0.0F, pxToM(height))});
        leftBound.setUserData(new PhysicsData(1, height));
        bounds.add(leftBound);
//        //top left to top right bound
//        createBound(new Vec2[] {
//                new Vec2(0, 0),
//                new Vec2(pxToM(width), 0)
//        });
//
//        //top right to bottom right bound
//        createBound(new Vec2[] {
//                new Vec2(pxToM(width), 0),
//                new Vec2(pxToM(width), pxToM(height))
//        });
//
//        //bottom right to bottom left bound
//        createBound(new Vec2[] {
//                new Vec2(pxToM(width), pxToM(height)),
//                new Vec2(0, pxToM(height))
//        });
//
//        //bottom left to top left bound
//        createBound(new Vec2[] {
//                new Vec2(0, pxToM(height)),
//                new Vec2(0, 0)
//        });
    }

    private void removeBounds() {
        for (Body body : bounds) {
            world.destroyBody(body);
        }
    }

    public void resetPhysics() {
        createWorld();
    }

    private Body createBound(Vec2[] verts)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        ChainShape chainShape = new ChainShape();
        chainShape.createChain(verts, 2);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chainShape;
        fixtureDef.friction = 2.5F;
        fixtureDef.restitution = 0.0F;
        //Add the body to our world
        Body body = world.createBody(bodyDef);
        //localBody.setUserData(new AttendeeHeadData(null, false, 0.0F, null));
        //define the fixture for the body that we added to the world
        body.createFixture(fixtureDef);
        return body;
    }

    private void createBody(View view) {
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyType.DYNAMIC;
//        bodyDef.position.set(getWorldPositionFromView(view));
//
//        PolygonShape shape = new PolygonShape();
//        shape.setAsBox(pxToM(view.getWidth() / 2), pxToM(view.getHeight() / 2));
//
//        FixtureDef fixtureDef = new FixtureDef();
//        fixtureDef.shape = shape;
//        fixtureDef.density = 0.5F;
//        fixtureDef.friction = 0.2F;
//        fixtureDef.restitution = 0.5F;
//
//        Body body = world.createBody(bodyDef);
//        body.createFixture(fixtureDef);
//
//        body.setUserData(view);
//        view.setTag(R.id.physics_layout_body_tag, body);
        //Create Player
        BodyDef aboutPlayer = new BodyDef();
        aboutPlayer.type = BodyType.DYNAMIC; //movable
        aboutPlayer.position.set(pxToM(view.getX()), pxToM(view.getY()));
        PolygonShape playerBox = new PolygonShape();
        int width = (int) pxToM(view.getWidth());
        int height = (int) pxToM(view.getHeight());
        playerBox.setAsBox(width, height);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = playerBox;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        Body body  = world.createBody(aboutPlayer);
        body.createFixture(fixtureDef);
        body.setUserData(new PhysicsData(width, height));
        view.setTag(R.id.physics_layout_body_tag, body);
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
                    Log.d(TAG, "Position for " + i + " :" + body.getPosition());

                    //TODO figure out good ratio for mToPx
                    view.setTranslationX(mToPx(body.getPosition().x) - mToPx(data.width/2));
                    view.setTranslationY(mToPx(body.getPosition().y) - mToPx(data.height/2));
//                    view.setX(body.getPosition().x);
//                    view.setY(body.getPosition().y);
                }
                //view.setY(view.getY()+1);
            }
            PhysicsData physicsData;
            for (Body bound : bounds) {
                physicsData = (PhysicsData) bound.getUserData();
                canvas.drawRect(bound.getPosition().x,
                        bound.getPosition().y,
                        bound.getPosition().x + physicsData.width,
                        bound.getPosition().y + physicsData.height,
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
        public int width;
        public int height;

        /**
         * Init physics data
         * @param width in meters
         * @param height in meters
         */
        public PhysicsData(int width, int height) {
            this.width = width;
            this.height = height;
        }

    }
}
