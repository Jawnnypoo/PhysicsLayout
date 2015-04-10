package com.jawnnypoo.physicslayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

/**
 * Created by Jawn on 4/9/2015.
 */
public class PhysicsLayout extends RelativeLayout {

    private static final String TAG = PhysicsLayout.class.getSimpleName();

    private static final float EARTH_GRAVITY = 9.8f;
    private static final int RENDER_TO_PHYSICS_RATIO = 100;
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 5;

    private World world;
    //private ArrayList<Body> bodies = new ArrayList<>();
    private boolean enablePhysics;
    private int width;
    private int height;
    private long lastDrawTime = System.currentTimeMillis();

    private ImageView testView;

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
        testView = new ImageView(getContext());
        ViewGroup.LayoutParams params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        testView.setLayoutParams(params);
        testView.setImageResource(R.drawable.ic_test_image);
        addView(testView);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
        createWorld(w,h);
        //TODO only do this if configured
        enablePhysics();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        //TODO add views to world as they are laid out
        for (int i = 0; i < getChildCount(); i++) {

        }
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        super.addView(child, width, height);
    }

    public void enablePhysics() {
        enablePhysics = true;
        invalidate();
    }

    public void disablePhysics() {
        enablePhysics = false;
    }

    //TODO traverse children and readd to world
    private void createWorld(int width, int height) {
        Log.d(TAG, "createWorld");
        //TODO clear all bodies in the views
        //bodies.clear();
        world = new World(new Vec2(0, EARTH_GRAVITY));
        //top left to right bound
        createBound(new Vec2[] {
                new Vec2(0, 0),
                new Vec2(pxToM(width), 0)
        });

        createBody(testView);
    }

    private void createBound(Vec2[] verts)
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
        aboutPlayer.position.set(0.0f, 200.0f);
        PolygonShape playerBox = new PolygonShape();
        playerBox.setAsBox(10.0f, 10.0f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = playerBox;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;

        Body body  = world.createBody(aboutPlayer);
        body.createFixture(fixtureDef);
        testView.setTag(R.id.physics_layout_body_tag, body);
    }

    private Vec2 getWorldPositionFromView(View view) {
        return new Vec2(pxToM(view.getX()), pxToM(view.getY()));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (enablePhysics) {
            world.step(0.01666667F, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            View view;
            Body body;
            for (int i = 0; i < getChildCount(); i++) {
                view = getChildAt(i);
                body = (Body) view.getTag(R.id.physics_layout_body_tag);
                if (body != null) {
                    Log.d(TAG, "Position for " + i + " :" + body.getPosition());

                    //TODO figure out good ratio for mToPx
//                    view.setX(mToPx(body.getPosition().x));
//                    view.setY(mToPx(body.getPosition().y));
                    view.setX(body.getPosition().x);
                    view.setY(body.getPosition().y);
                }
                //view.setY(view.getY()+1);
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

    private static class PhysicsData {

    }
}
