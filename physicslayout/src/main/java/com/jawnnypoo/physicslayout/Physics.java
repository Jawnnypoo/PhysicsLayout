package com.jawnnypoo.physicslayout;

import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.commit451.translationviewdraghelper.TranslationViewDragHelper;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation for physics layout is found here, since we want to offer the main
 * layouts without requiring further extension (LinearLayout, RelativeLayout etc)
 */
public class Physics {

    private static final String TAG = Physics.class.getSimpleName();

    public static final float NO_GRAVITY = 0.0f;
    public static final float MOON_GRAVITY = 1.6f;
    public static final float EARTH_GRAVITY = 9.8f;
    public static final float JUPITER_GRAVITY = 24.8f;

    //Size in DP of the bounds (world walls) of the view
    private static final int BOUND_SIZE_DP = 20;
    private static final float FRAME_RATE = 1 / 60f;

    /**
     * Set the configuration that will be used when creating the view Body.
     * Changing view's configuration after layout has been performed will require you to call
     * {@link ViewGroup#requestLayout()} so that the body can be created with the new
     * configuration.
     *
     * @param view   view that contains the physics body
     * @param config the new configuration for the body
     */
    public static void setPhysicsConfig(@NonNull View view, @Nullable PhysicsConfig config) {
        view.setTag(R.id.physics_layout_config_tag, config);
    }

    @Nullable
    public static Body getPhysicsBody(@NonNull View view) {
        return (Body) view.getTag(R.id.physics_layout_body_tag);
    }

    private boolean debugDraw = false;
    private boolean debugLog = false;

    private int velocityIterations = 8;
    private int positionIterations = 3;
    private float pixelsPerMeter;
    private float boundsSize;

    private World world;
    private ArrayList<Body> bounds = new ArrayList<>();
    private float gravityX = 0.0f;
    private float gravityY = EARTH_GRAVITY;
    private boolean enablePhysics = true;
    private boolean hasBounds = true;
    private boolean allowFling = false;

    private ViewGroup viewGroup;
    private Paint debugPaint;
    private float density;
    private int width;
    private int height;
    private TranslationViewDragHelper viewDragHelper;
    private View viewBeingDragged;

    private OnFlingListener onFlingListener;
    private OnCollisionListener onCollisionListener;
    private ArrayList<OnPhysicsProcessedListener> onPhysicsProcessedListeners;

    private final ContactListener contactListener = new ContactListener() {
        @Override
        public void beginContact(Contact contact) {
            if (onCollisionListener != null) {
                onCollisionListener.onCollisionEntered((int) contact.getFixtureA().m_userData,
                        (int) contact.getFixtureB().m_userData);
            }
        }

        @Override
        public void endContact(Contact contact) {
            if (onCollisionListener != null) {
                onCollisionListener.onCollisionExited((int) contact.getFixtureA().m_userData,
                        (int) contact.getFixtureB().m_userData);
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    };

    private final TranslationViewDragHelper.Callback viewDragHelperCallback = new TranslationViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return true;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            return top;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            viewBeingDragged = capturedChild;
            Body body = (Body) viewBeingDragged.getTag(R.id.physics_layout_body_tag);
            if (body != null) {
                body.setAngularVelocity(0);
                body.setLinearVelocity(new Vec2(0, 0));
            }

            if (onFlingListener != null) {
                onFlingListener.onGrabbed(capturedChild);
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            viewBeingDragged = null;
            Body body = (Body) releasedChild.getTag(R.id.physics_layout_body_tag);
            if (body != null) {
                translateBodyToView(body, releasedChild);
                body.setLinearVelocity(new Vec2(pixelsToMeters(xvel), pixelsToMeters(yvel)));
                body.setAwake(true);
            }
            if (onFlingListener != null) {
                onFlingListener.onReleased(releasedChild);
            }
        }
    };

    public float metersToPixels(float meters) {
        return meters * pixelsPerMeter;
    }

    public float pixelsToMeters(float pixels) {
        return pixels / pixelsPerMeter;
    }

    private float radiansToDegrees(float radians) {
        return radians / 3.14f * 180f;
    }

    private float degreesToRadians(float degrees) {
        return (degrees / 180f) * 3.14f;
    }

    /**
     * Call this when your view is created (remember to call from each possible constructor). Pass
     * the layout (extends ViewGroup) that you want to apply physics to.
     */
    public Physics(ViewGroup viewGroup) {
        this(viewGroup, null);
    }

    /**
     * Call this when your view is created (remember to call from each possible constructor). Pass
     * the layout (extends ViewGroup) that you want to apply physics to.
     */
    public Physics(ViewGroup viewGroup, AttributeSet attrs) {
        this.viewGroup = viewGroup;
        viewDragHelper = TranslationViewDragHelper.create(viewGroup, 1.0f, viewDragHelperCallback);
        debugPaint = new Paint();
        debugPaint.setColor(Color.MAGENTA);
        debugPaint.setStyle(Paint.Style.STROKE);
        density = viewGroup.getResources().getDisplayMetrics().density;
        if (attrs != null) {
            TypedArray a = viewGroup.getContext()
                    .obtainStyledAttributes(attrs, R.styleable.Physics);
            enablePhysics = a.getBoolean(R.styleable.Physics_physics, enablePhysics);
            gravityX = a.getFloat(R.styleable.Physics_gravityX, gravityX);
            gravityY = a.getFloat(R.styleable.Physics_gravityY, gravityY);
            hasBounds = a.getBoolean(R.styleable.Physics_bounds, hasBounds);
            boundsSize = a.getDimension(R.styleable.Physics_boundsSize, BOUND_SIZE_DP * density);
            allowFling = a.getBoolean(R.styleable.Physics_fling, allowFling);
            velocityIterations = a
                    .getInt(R.styleable.Physics_velocityIterations, velocityIterations);
            positionIterations = a
                    .getInt(R.styleable.Physics_positionIterations, positionIterations);
            pixelsPerMeter = a.getFloat(R.styleable.Physics_pixelsPerMeter, viewGroup.getResources()
                    .getDimensionPixelSize(R.dimen.physics_layout_dp_per_meter));
            a.recycle();
        }
    }

    /**
     * Call this every time your view gets a call to onSizeChanged so that the world can
     * respond to this change.
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
        if (debugLog) {
            Log.d(TAG, "onLayout");
        }
        createWorld();
    }

    /**
     * Call this in your ViewGroup if you plan on using fling
     */
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!allowFling) {
            return false;
        }
        final int action = ev.getActionMasked();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel();
            return false;
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    /**
     * Call this in your ViewGroup if you plan on using fling
     */
    public boolean onTouchEvent(MotionEvent ev) {
        if (!allowFling) {
            return false;
        }
        viewDragHelper.processTouchEvent(ev);
        return true;
    }

    /**
     * Call this when your view calls onDraw so that physics can be processed
     */
    public void onDraw(Canvas canvas) {
        if (!enablePhysics) {
            return;
        }
        world.step(FRAME_RATE, velocityIterations, positionIterations);
        View view;
        Body body;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            view = viewGroup.getChildAt(i);
            body = (Body) view.getTag(R.id.physics_layout_body_tag);

            if (view == viewBeingDragged) {
                // If we are being dragged, we process in reverse, moving the body to where the view is
                //instead of the reverse
                if (body != null) {
                    translateBodyToView(body, view);
                    view.setRotation(radiansToDegrees(body.getAngle()) % 360);
                }
                continue;
            }

            if (body != null) {
                view.setX(metersToPixels(body.getPosition().x) - view.getWidth() / 2);
                view.setY(metersToPixels(body.getPosition().y) - view.getHeight() / 2);
                view.setRotation(radiansToDegrees(body.getAngle()) % 360);

                if (debugDraw) {
                    PhysicsConfig config = (PhysicsConfig) view.getTag(R.id.physics_layout_config_tag);
                    if (config.shapeType == PhysicsConfig.SHAPE_TYPE_RECTANGLE) {
                        canvas.drawRect(metersToPixels(body.getPosition().x) - view.getWidth() / 2,
                                metersToPixels(body.getPosition().y) - view.getHeight() / 2,
                                metersToPixels(body.getPosition().x) + view.getWidth() / 2,
                                metersToPixels(body.getPosition().y) + view.getHeight() / 2, debugPaint);
                    } else if (config.shapeType == PhysicsConfig.SHAPE_TYPE_CIRCLE) {
                        canvas.drawCircle(
                                metersToPixels(body.getPosition().x),
                                metersToPixels(body.getPosition().y),
                                config.radius, //already defined in terms of pixels
                                debugPaint);
                    }
                }
            }
        }
        if (onPhysicsProcessedListeners != null) {
            for (int i = 0; i < onPhysicsProcessedListeners.size(); i++) {
                onPhysicsProcessedListeners.get(i).onPhysicsProcessed(this, world);
            }
        }
        viewGroup.invalidate();
    }

    /**
     * Recreate the physics world. Will traverse all views in the hierarchy, get their current
     * PhysicsConfigs
     * and create a body in the world. This will override the current world if it exists.
     */
    public void createWorld() {
        //Null out all the bodies
        ArrayList<Body> oldBodiesArray = new ArrayList<>();

        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            Body body = (Body) viewGroup.getChildAt(i).getTag(R.id.physics_layout_body_tag);
            if (body != null) {
                oldBodiesArray.add(body);
            } else {
                oldBodiesArray.add(null);
            }
            viewGroup.getChildAt(i).setTag(R.id.physics_layout_body_tag, null);
        }
        bounds.clear();
        if (debugLog) {
            Log.d(TAG, "createWorld");
        }
        world = new World(new Vec2(gravityX, gravityY));
        world.setContactListener(contactListener);
        if (hasBounds) {
            enableBounds();
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            createBody(viewGroup.getChildAt(i), oldBodiesArray.get(i));
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
        int boundSize = Math.round(boundsSize);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        PolygonShape box = new PolygonShape();
        int boxWidth = (int) pixelsToMeters(width);
        int boxHeight = (int) pixelsToMeters(boundSize);
        box.setAsBox(boxWidth, boxHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        fixtureDef.userData = R.id.physics_layout_bound_top;
        bodyDef.position.set(0, -boxHeight);
        Body topBody = world.createBody(bodyDef);
        topBody.createFixture(fixtureDef);
        bounds.add(topBody);

        fixtureDef.userData = R.id.physics_layout_body_bottom;
        bodyDef.position.set(0, pixelsToMeters(height) + boxHeight);
        Body bottomBody = world.createBody(bodyDef);
        bottomBody.createFixture(fixtureDef);
        bounds.add(bottomBody);
    }

    private void createLeftAndRightBounds() {
        int boundSize = Math.round(boundsSize);
        int boxWidth = (int) pixelsToMeters(boundSize);
        int boxHeight = (int) pixelsToMeters(height);
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.STATIC;
        PolygonShape box = new PolygonShape();
        box.setAsBox(boxWidth, boxHeight);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = box;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.3f;
        fixtureDef.restitution = 0.5f;

        fixtureDef.userData = R.id.physics_layout_body_left;
        bodyDef.position.set(-boxWidth, 0);
        Body leftBody = world.createBody(bodyDef);
        leftBody.createFixture(fixtureDef);
        bounds.add(leftBody);

        fixtureDef.userData = R.id.physics_layout_body_right;
        bodyDef.position.set(pixelsToMeters(width) + boxWidth, 0);
        Body rightBody = world.createBody(bodyDef);
        rightBody.createFixture(fixtureDef);
        bounds.add(rightBody);
    }

    private void createBody(View view, Body oldBody) {
        PhysicsConfig config = (PhysicsConfig) view.getTag(R.id.physics_layout_config_tag);
        if (config == null) {
            if (view.getLayoutParams() instanceof PhysicsLayoutParams) {
                config = ((PhysicsLayoutParams) view.getLayoutParams()).getConfig();
            }
            if (config == null) {
                config = PhysicsConfig.create();
            }
            view.setTag(R.id.physics_layout_config_tag, config);
        }
        BodyDef bodyDef = config.bodyDef;
        bodyDef.position.set(pixelsToMeters(view.getX() + view.getWidth() / 2),
                pixelsToMeters(view.getY() + view.getHeight() / 2));

        if (oldBody != null) {
            bodyDef.angle = oldBody.getAngle();
            bodyDef.angularVelocity = oldBody.getAngularVelocity();
            bodyDef.linearVelocity = oldBody.getLinearVelocity();
            bodyDef.angularDamping = oldBody.getAngularDamping();
            bodyDef.linearDamping = oldBody.getLinearDamping();
        } else {
            bodyDef.angularVelocity = degreesToRadians(view.getRotation());
        }

        FixtureDef fixtureDef = config.fixtureDef;
        fixtureDef.shape = config.shapeType == PhysicsConfig.SHAPE_TYPE_RECTANGLE
                ? createBoxShape(view) : createCircleShape(view, config);
        fixtureDef.userData = view.getId();

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);
        view.setTag(R.id.physics_layout_body_tag, body);
    }

    private PolygonShape createBoxShape(View view) {
        PolygonShape box = new PolygonShape();
        float boxWidth = pixelsToMeters(view.getWidth() / 2);
        float boxHeight = pixelsToMeters(view.getHeight() / 2);
        box.setAsBox(boxWidth, boxHeight);
        return box;
    }

    private CircleShape createCircleShape(View view, PhysicsConfig config) {
        CircleShape circle = new CircleShape();
        //radius was not set, set it to max of the width and height
        if (config.radius == -1) {
            config.radius = Math.max(view.getWidth() / 2, view.getHeight() / 2);
        }
        circle.m_radius = pixelsToMeters(config.radius);
        return circle;
    }

    /**
     * Finds the physics {@link Body} that corresponds to the view. Requires the view to have an
     * id.
     * Returns null if no body exists for the view
     *
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
     * Get the current Box2D {@link World} controlling the physics of this view
     *
     * @return The Box2D {@link World}
     */
    public World getWorld() {
        return world;
    }

    /**
     * Enables physics on the view
     */
    public void enablePhysics() {
        enablePhysics = true;
        viewGroup.invalidate();
    }

    public void disablePhysics() {
        enablePhysics = false;
    }

    /**
     * Does physics effect this view?
     *
     * @return physics enabled or not
     */
    public boolean isPhysicsEnabled() {
        return enablePhysics;
    }

    /**
     * Gives a random impulse to all the view bodies in the layout. Really just useful for testing,
     * but
     * try it out if you want :)
     */
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

    private void translateBodyToView(@NonNull Body body, @NonNull View view) {
        body.setTransform(
                new Vec2(pixelsToMeters(view.getX() + view.getWidth() / 2),
                        pixelsToMeters(view.getY() + view.getHeight() / 2)),
                body.getAngle());
    }

    /**
     * Allows the user to touch and fling views around the screen. Flinging will not be enabled if
     * a
     * view already has an {@link android.view.View.OnClickListener}
     */
    public void enableFling() {
        allowFling = true;
    }

    /**
     * Disable flinging on the layout
     */
    public void disableFling() {
        allowFling = false;
    }

    /**
     * Is fling enabled for this ViewGroup?
     *
     * @return physics enabled or not
     */
    public boolean isFlingEnabled() {
        return allowFling;
    }

    /**
     * Sets the fling listener
     *
     * @param onFlingListener listener that will respond to fling events
     */
    public void setOnFlingListener(OnFlingListener onFlingListener) {
        this.onFlingListener = onFlingListener;
    }

    /**
     * Sets the collision listener
     *
     * @param onCollisionListener listener that will listen for collisions
     */
    public void setOnCollisionListener(OnCollisionListener onCollisionListener) {
        this.onCollisionListener = onCollisionListener;
    }

    /**
     * Sets the size of the bounds and enables the bounds
     *
     * @param size the size of the bounds in dp
     */
    public void setBoundsSize(float size) {
        boundsSize = size * density;

        if (hasBounds()) {
            disableBounds();
        }
        enableBounds();
    }

    /**
     * Sets if the physics world has bounds or not
     *
     * @param hasBounds true if you want bounds, false if not
     */
    public void setHasBounds(boolean hasBounds) {
        this.hasBounds = hasBounds;
    }

    public boolean hasBounds() {
        return hasBounds;
    }

    /**
     * Sets the gravity in the x direction for the world. Positive is right, negative is left.
     */
    public void setGravityX(float newGravityX) {
        setGravity(newGravityX, gravityY);
    }

    public float getGravityX() {
        return gravityX;
    }

    /**
     * Sets the gravity in the y direction for the world. Positive is down, negative is up.
     */
    public void setGravityY(float newGravityY) {
        setGravity(gravityX, newGravityY);
    }

    public float getGravityY() {
        return gravityY;
    }

    /**
     * Sets the gravity for the world. Positive x is right, negative is left. Positive
     * y is down, negative is up.
     */
    public void setGravity(float gravityX, float gravityY) {
        this.gravityX = gravityX;
        this.gravityY = gravityY;
        world.setGravity(new Vec2(gravityX, gravityY));
    }

    public Vec2 getGravity() {
        return world.getGravity();
    }

    /**
     * Set the number of velocity iterations the world will perform at each step.
     * Default is 8
     *
     * @param velocityIterations number of iterations
     */
    public void setVelocityIterations(int velocityIterations) {
        this.velocityIterations = velocityIterations;
    }

    public int getVelocityIterations() {
        return velocityIterations;
    }

    /**
     * Set the number of position iterations the world will perform at each step.
     * Default is 3
     *
     * @param positionIterations number of iterations
     */
    public void setPositionIterations(int positionIterations) {
        this.positionIterations = positionIterations;
    }

    public int getPositionIterations() {
        return positionIterations;
    }

    /**
     * Set the number of pixels per meter. Basically makes the world feel bigger or smaller
     * Default is 20dp. More pixels per meter = ui feeling bigger in the world (faster movement)
     *
     * @param pixelsPerMeter number of pixels on screen per meter in box2d world
     */
    public void setPixelsPerMeter(float pixelsPerMeter) {
        this.pixelsPerMeter = pixelsPerMeter;
    }

    public float getPixelsPerMeter() {
        return pixelsPerMeter;
    }

    public void addOnPhysicsProcessedListener(OnPhysicsProcessedListener listener) {
        if (onPhysicsProcessedListeners == null) {
            onPhysicsProcessedListeners = new ArrayList<>();
        }
        onPhysicsProcessedListeners.add(listener);
    }

    public void removeOnPhysicsProcessedListener(OnPhysicsProcessedListener listener) {
        if (onPhysicsProcessedListeners != null) {
            onPhysicsProcessedListeners.remove(listener);
        }
    }

    /**
     * Interface that allows hooks into the layout so that you can process or modify physics bodies each time that JBox2D processes physics
     */
    public interface OnPhysicsProcessedListener {
        /**
         * Physics has been processed. Commence doing things that you want to do such as applying additional forces
         * @param physics the {@link Physics} that belongs to the view
         * @param world the Box2d world
         */
        void onPhysicsProcessed(Physics physics, World world);
    }

    /**
     * A controller that will receive the drag events.
     */
    public interface OnFlingListener {
        void onGrabbed(View grabbedView);

        void onReleased(View releasedView);
    }

    public interface OnCollisionListener {
        /**
         * Called when a collision is entered between two bodies. ViewId can also be
         * R.id.physics_layout_bound_top,
         * R.id.physics_layout_bound_bottom, R.id.physics_layout_bound_left, or
         * R.id.physics_layout_bound_right.
         * If view was not assigned an id, the return value will be {@link View#NO_ID}.
         *
         * @param viewIdA view id of body A
         * @param viewIdB view id of body B
         */
        void onCollisionEntered(int viewIdA, int viewIdB);

        /**
         * Called when a collision is exited between two bodies. ViewId can also be
         * R.id.physics_layout_bound_top,
         * R.id.physics_layout_bound_bottom, R.id.physics_layout_bound_left, or
         * R.id.physics_layout_bound_right.
         * If view was not assigned an id, the return value will be {@link View#NO_ID}.
         *
         * @param viewIdA view id of body A
         * @param viewIdB view id of body B
         */
        void onCollisionExited(int viewIdA, int viewIdB);
    }
}
