package com.jawnnypoo.physicslayout

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.commit451.translationviewdraghelper.TranslationViewDragHelper
import org.jbox2d.callbacks.ContactImpulse
import org.jbox2d.callbacks.ContactListener
import org.jbox2d.collision.Manifold
import org.jbox2d.collision.shapes.CircleShape
import org.jbox2d.collision.shapes.PolygonShape
import org.jbox2d.common.Vec2
import org.jbox2d.dynamics.*
import org.jbox2d.dynamics.contacts.Contact
import java.util.*
import kotlin.math.max

/**
 * Implementation for physics layout is found here, since we want to offer the main
 * layouts without requiring further extension (LinearLayout, RelativeLayout, etc.)
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
class Physics @JvmOverloads constructor(private val viewGroup: ViewGroup, attrs: AttributeSet? = null) {

    companion object {
        private val TAG = Physics::class.java.simpleName
        const val NO_GRAVITY = 0.0f
        const val MOON_GRAVITY = 1.6f
        const val EARTH_GRAVITY = 9.8f
        const val JUPITER_GRAVITY = 24.8f

        // Size in DP of the bounds (world walls) of the view
        private const val BOUND_SIZE_DP = 20
        private const val FRAME_RATE = 1 / 60f

        /**
         * Set the configuration that will be used when creating the view Body.
         * Changing view's configuration after layout has been performed will require you to call
         * [ViewGroup.requestLayout] so that the body can be created with the new
         * configuration.
         *
         * @param view   view that contains the physics body
         * @param config the new configuration for the body
         */
        fun setPhysicsConfig(view: View, config: PhysicsConfig?) {
            view.setTag(R.id.physics_layout_config_tag, config)
        }
    }

    private val debugDraw = false
    private val debugLog = false

    /**
     * Set the number of velocity iterations the world will perform at each step.
     * Default is 8
     */
    var velocityIterations = 8

    /**
     * Set the number of position iterations the world will perform at each step.
     * Default is 3
     */
    var positionIterations = 3

    /**
     * Set the number of pixels per meter. Basically makes the world feel bigger or smaller
     * Default is 20dp. More pixels per meter = ui feeling bigger in the world (faster movement)
     */
    var pixelsPerMeter = 0f

    /**
     * Get the current Box2D [World] controlling the physics of this view
     */
    var world: World? = null
        private set

    /**
     * Enable/disable physics on the view
     */
    var isPhysicsEnabled = true

    /**
     * Enable/disable fling for this View
     */
    var isFlingEnabled = false

    /**
     * Enables/disables if the view has bounds or not
     */
    var hasBounds = true

    private var boundsSize = 0f
    private val bounds = mutableListOf<Bound>()
    private var gravityX = 0.0f
    private var gravityY = EARTH_GRAVITY

    private val debugPaint: Paint by lazy {
        val paint = Paint()
        paint.color = Color.MAGENTA
        paint.style = Paint.Style.STROKE
        paint
    }

    private val density: Float
    private var width = 0
    private var height = 0
    private val viewDragHelper: TranslationViewDragHelper
    private var viewBeingDragged: View? = null
    private var onFlingListener: OnFlingListener? = null
    private var onCollisionListener: OnCollisionListener? = null
    private var onPhysicsProcessedListeners = mutableListOf<OnPhysicsProcessedListener>()
    private var onBodyCreatedListener: OnBodyCreatedListener? = null

    private val contactListener: ContactListener = object : ContactListener {
        override fun beginContact(contact: Contact) {
            if (onCollisionListener != null) {
                onCollisionListener!!.onCollisionEntered(contact.fixtureA.m_userData as Int,
                        contact.fixtureB.m_userData as Int)
            }
        }

        override fun endContact(contact: Contact) {
            if (onCollisionListener != null) {
                onCollisionListener!!.onCollisionExited(contact.fixtureA.m_userData as Int,
                        contact.fixtureB.m_userData as Int)
            }
        }

        override fun preSolve(contact: Contact, oldManifold: Manifold) {}
        override fun postSolve(contact: Contact, impulse: ContactImpulse) {}
    }

    private val viewDragHelperCallback: TranslationViewDragHelper.Callback = object : TranslationViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return true
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            super.onViewCaptured(capturedChild, activePointerId)
            viewBeingDragged = capturedChild
            val body = viewBeingDragged?.getTag(R.id.physics_layout_body_tag) as? Body
            if (body != null) {
                body.angularVelocity = 0f
                body.linearVelocity = Vec2(0f, 0f)
            }
            onFlingListener?.onGrabbed(capturedChild)
        }

        override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            viewBeingDragged = null
            val body = releasedChild?.getTag(R.id.physics_layout_body_tag) as? Body
            if (body != null) {
                translateBodyToView(body, releasedChild)
                body.linearVelocity = Vec2(pixelsToMeters(xvel), pixelsToMeters(yvel))
                body.isAwake = true
            }
            onFlingListener?.onReleased(releasedChild)
        }
    }

    init {
        viewDragHelper = TranslationViewDragHelper.create(viewGroup, 1.0f, viewDragHelperCallback)

        density = viewGroup.resources.displayMetrics.density
        if (attrs != null) {
            val a = viewGroup.context
                    .obtainStyledAttributes(attrs, R.styleable.Physics)
            isPhysicsEnabled = a.getBoolean(R.styleable.Physics_physics, isPhysicsEnabled)
            gravityX = a.getFloat(R.styleable.Physics_gravityX, gravityX)
            gravityY = a.getFloat(R.styleable.Physics_gravityY, gravityY)
            hasBounds = a.getBoolean(R.styleable.Physics_bounds, hasBounds)
            boundsSize = a.getDimension(R.styleable.Physics_boundsSize, BOUND_SIZE_DP * density)
            isFlingEnabled = a.getBoolean(R.styleable.Physics_fling, isFlingEnabled)
            velocityIterations = a
                    .getInt(R.styleable.Physics_velocityIterations, velocityIterations)
            positionIterations = a
                    .getInt(R.styleable.Physics_positionIterations, positionIterations)
            pixelsPerMeter = a.getFloat(R.styleable.Physics_pixelsPerMeter, viewGroup.resources
                    .getDimensionPixelSize(R.dimen.physics_layout_dp_per_meter).toFloat())
            a.recycle()
        }
    }

    private fun metersToPixels(meters: Float): Float {
        return meters * pixelsPerMeter
    }

    private fun pixelsToMeters(pixels: Float): Float {
        return pixels / pixelsPerMeter
    }

    private fun radiansToDegrees(radians: Float): Float {
        return radians / 3.14f * 180f
    }

    private fun degreesToRadians(degrees: Float): Float {
        return degrees / 180f * 3.14f
    }

    /**
     * Call this every time your view gets a call to onSizeChanged so that the world can
     * respond to this change.
     */
    fun onSizeChanged(width: Int, height: Int) {
        this.width = width
        this.height = height
    }

    /**
     * Call this every time your view gets a call to onLayout so that the world can
     * respond to this change.
     */
    @Suppress("UNUSED_PARAMETER")
    fun onLayout(changed: Boolean) {
        if (debugLog) {
            Log.d(TAG, "onLayout")
        }
        createWorld()
    }

    /**
     * Call this in your ViewGroup if you plan on using fling
     */
    fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!isFlingEnabled) {
            return false
        }
        val action = ev.actionMasked
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            viewDragHelper.cancel()
            return false
        }
        return viewDragHelper.shouldInterceptTouchEvent(ev)
    }

    /**
     * Call this in your ViewGroup if you plan on using fling
     * @return true if consumed, false otherwise
     */
    fun onTouchEvent(ev: MotionEvent): Boolean {
        if (!isFlingEnabled) {
            return false
        }
        viewDragHelper.processTouchEvent(ev)
        return true
    }

    /**
     * Call this when your view calls onDraw so that physics can be processed
     */
    fun onDraw(canvas: Canvas) {
        val world = world
        if (!isPhysicsEnabled || world == null) {
            return
        }
        world.step(FRAME_RATE, velocityIterations, positionIterations)
        var view: View
        var body: Body?
        for (i in 0 until viewGroup.childCount) {
            view = viewGroup.getChildAt(i)
            body = view.getTag(R.id.physics_layout_body_tag) as? Body
            if (view == viewBeingDragged) { // If we are being dragged, we process in reverse, moving the body to where the view is
                //instead of the reverse
                if (body != null) {
                    translateBodyToView(body, view)
                    view.rotation = radiansToDegrees(body.angle) % 360f
                }
                continue
            }
            if (body != null) {
                view.x = metersToPixels(body.position.x) - view.width / 2f
                view.y = metersToPixels(body.position.y) - view.height / 2f
                view.rotation = radiansToDegrees(body.angle) % 360f
                if (debugDraw) {
                    val config = view.getTag(R.id.physics_layout_config_tag) as PhysicsConfig
                    when (config.shape) {
                        Shape.RECTANGLE -> {
                            canvas.drawRect(
                                    metersToPixels(body.position.x) - view.width / 2,
                                    metersToPixels(body.position.y) - view.height / 2,
                                    metersToPixels(body.position.x) + view.width / 2,
                                    metersToPixels(body.position.y) + view.height / 2,
                                    debugPaint
                            )
                        }
                        Shape.CIRCLE -> {
                            canvas.drawCircle(
                                    metersToPixels(body.position.x),
                                    metersToPixels(body.position.y),
                                    config.radius,
                                    debugPaint
                            )
                        }
                    }
                }
            }
        }
        onPhysicsProcessedListeners.forEach { it.onPhysicsProcessed(this, world) }
        viewGroup.invalidate()
    }

    /**
     * Recreate the physics world. Will traverse all views in the hierarchy, get their current
     * PhysicsConfigs and create a body in the world. This will override the current world if it exists.
     */
    private fun createWorld() {
        // Null out all the bodies
        val oldBodiesArray = ArrayList<Body?>()
        for (i in 0 until viewGroup.childCount) {
            val body = viewGroup.getChildAt(i).getTag(R.id.physics_layout_body_tag) as? Body
            oldBodiesArray.add(body)
            viewGroup.getChildAt(i).setTag(R.id.physics_layout_body_tag, null)
        }
        bounds.clear()
        if (debugLog) {
            Log.d(TAG, "createWorld")
        }
        world = World(Vec2(gravityX, gravityY))
        world?.setContactListener(contactListener)
        if (hasBounds) {
            enableBounds()
        }
        for (i in 0 until viewGroup.childCount) {
            val body = createBody(viewGroup.getChildAt(i), oldBodiesArray[i])
            onBodyCreatedListener?.onBodyCreated(viewGroup.getChildAt(i), body)
        }
    }

    private fun enableBounds() {
        hasBounds = true
        createBounds()
    }

    private fun disableBounds() {
        hasBounds = false
        for (body in bounds) {
            world?.destroyBody(body.body)
        }
        bounds.clear()
    }

    private fun createBounds() {
        val top = createBound(
                widthInPixels = width.toFloat(),
                heightInPixels = boundsSize,
                id = R.id.physics_layout_bound_top,
                side = Bound.Side.TOP
        )
        bounds.add(top)

        val bottom = createBound(
                widthInPixels = width.toFloat(),
                heightInPixels = boundsSize,
                id = R.id.physics_layout_bound_bottom,
                side = Bound.Side.BOTTOM
        )
        bounds.add(bottom)

        val left = createBound(
                widthInPixels = boundsSize,
                heightInPixels = height.toFloat(),
                id = R.id.physics_layout_bound_left,
                side = Bound.Side.LEFT
        )
        bounds.add(left)

        val right = createBound(
                widthInPixels = boundsSize,
                heightInPixels = height.toFloat(),
                id = R.id.physics_layout_bound_right,
                side = Bound.Side.RIGHT
        )
        bounds.add(right)
    }

    private fun createBound(widthInPixels: Float, heightInPixels: Float, id: Int, side: Bound.Side): Bound {
        val bodyDef = BodyDef()
        bodyDef.type = BodyType.STATIC
        val box = PolygonShape()
        val boxWidthMeters = pixelsToMeters(widthInPixels)
        val boxHeightMeters = pixelsToMeters(heightInPixels)
        box.setAsBox(boxWidthMeters, boxHeightMeters)
        val fixtureDef = createBoundFixtureDef(box, id)
        val pair = when (side) {
            Bound.Side.TOP -> Pair(0f, -boxHeightMeters)
            Bound.Side.BOTTOM -> Pair(0f, pixelsToMeters(height.toFloat()) + boxHeightMeters)
            Bound.Side.LEFT -> Pair(-boxWidthMeters, 0f)
            Bound.Side.RIGHT -> Pair(pixelsToMeters(width.toFloat()) + boxWidthMeters, 0f)
        }
        bodyDef.position[pair.first] = pair.second
        val body = world!!.createBody(bodyDef)
        body.createFixture(fixtureDef)
        return Bound(
                widthInPixels = widthInPixels,
                heightInPixels = heightInPixels,
                body = body,
                side = side
        )
    }

    private fun createBoundFixtureDef(box: PolygonShape, id: Int): FixtureDef {
        val fixtureDef = FixtureDef()
        fixtureDef.shape = box
        fixtureDef.density = 0.5f
        fixtureDef.friction = 0.3f
        fixtureDef.restitution = 0.5f
        fixtureDef.userData = id
        return fixtureDef
    }

    private fun createBody(view: View, oldBody: Body?): Body {
        var config = view.getTag(R.id.physics_layout_config_tag) as? PhysicsConfig
        if (config == null) {
            if (view.layoutParams is PhysicsLayoutParams) {
                config = (view.layoutParams as PhysicsLayoutParams).config
            }
            if (config == null) {
                config = PhysicsConfig()
            }
            view.setTag(R.id.physics_layout_config_tag, config)
        }
        val bodyDef = config.bodyDef
        bodyDef.position[pixelsToMeters(view.x + view.width / 2)] = pixelsToMeters(view.y + view.height / 2)
        if (oldBody != null) {
            bodyDef.angle = oldBody.angle
            bodyDef.angularVelocity = oldBody.angularVelocity
            bodyDef.linearVelocity = oldBody.linearVelocity
            bodyDef.angularDamping = oldBody.angularDamping
            bodyDef.linearDamping = oldBody.linearDamping
        } else {
            bodyDef.angularVelocity = degreesToRadians(view.rotation)
        }
        val fixtureDef = config.fixtureDef
        fixtureDef.shape = if (config.shape == Shape.RECTANGLE) createBoxShape(view) else createCircleShape(view, config)
        fixtureDef.userData = view.id
        val body = world!!.createBody(bodyDef)
        body.createFixture(fixtureDef)
        view.setTag(R.id.physics_layout_body_tag, body)
        return body
    }

    private fun createBoxShape(view: View): PolygonShape {
        val box = PolygonShape()
        val boxWidth = pixelsToMeters(view.width / 2.toFloat())
        val boxHeight = pixelsToMeters(view.height / 2.toFloat())
        box.setAsBox(boxWidth, boxHeight)
        return box
    }

    private fun createCircleShape(view: View, config: PhysicsConfig): CircleShape {
        val circle = CircleShape()
        //radius was not set, set it to max of the width and height
        if (config.radius == -1f) {
            config.radius = max(view.width / 2f, view.height / 2f)
        }
        circle.m_radius = pixelsToMeters(config.radius)
        return circle
    }

    /**
     * Finds the physics [Body] that corresponds to the view. Requires the view to have an
     * id.
     * Returns null if no body exists for the view
     *
     * @param id the view's id of the body you want to retrieve
     * @return body that determines the views physics
     */
    fun findBodyById(id: Int): Body? {
        val view = viewGroup.findViewById<View>(id)
        return if (view != null) {
            view.getTag(R.id.physics_layout_body_tag) as? Body
        } else null
    }

    /**
     * Gives a random impulse to all the view bodies in the layout. Really just useful for testing,
     * but try it out if you want :)
     */
    fun giveRandomImpulse() {
        var body: Body?
        var impulse: Vec2
        val random = Random()
        for (i in 0 until viewGroup.childCount) {
            impulse = Vec2((random.nextInt(1000) - 1000).toFloat(), (random.nextInt(1000) - 1000).toFloat())
            body = viewGroup.getChildAt(i).getTag(R.id.physics_layout_body_tag) as? Body
            body?.applyLinearImpulse(impulse, body.position)
        }
    }

    private fun translateBodyToView(body: Body, view: View) {
        body.setTransform(
                Vec2(pixelsToMeters(view.x + view.width / 2),
                        pixelsToMeters(view.y + view.height / 2)),
                body.angle)
    }

    /**
     * Sets the fling listener
     *
     * @param onFlingListener listener that will respond to fling events
     */
    fun setOnFlingListener(onFlingListener: OnFlingListener?) {
        this.onFlingListener = onFlingListener
    }

    /**
     * Sets the collision listener
     *
     * @param onCollisionListener listener that will listen for collisions
     */
    fun setOnCollisionListener(onCollisionListener: OnCollisionListener?) {
        this.onCollisionListener = onCollisionListener
    }

    /**
     * Sets the size of the bounds and enables the bounds
     *
     * @param size the size of the bounds in dp
     */
    fun setBoundsSize(size: Float) {
        boundsSize = size * density
        if (hasBounds) {
            disableBounds()
        }
        enableBounds()
    }

    /**
     * Sets the gravity in the x direction for the world. Positive is right, negative is left.
     */
    fun setGravityX(newGravityX: Float) {
        setGravity(newGravityX, gravityY)
    }

    /**
     * The gravity in the x direction for the world. Positive is right, negative is left.
     */
    fun getGravityX(): Float {
        return gravityX
    }

    /**
     * Sets the gravity in the y direction for the world. Positive is down, negative is up.
     */
    fun setGravityY(newGravityY: Float) {
        setGravity(gravityX, newGravityY)
    }

    /**
     * The gravity in the x direction for the world. Positive is right, negative is left.
     */
    fun getGravityY(): Float {
        return gravityY
    }

    /**
     * Sets the gravity for the world. Positive x is right, negative is left. Positive
     * y is down, negative is up.
     */
    fun setGravity(gravityX: Float, gravityY: Float) {
        this.gravityX = gravityX
        this.gravityY = gravityY
        world?.gravity = Vec2(gravityX, gravityY)
    }

    /**
     * Returns the gravity of the world. Returns null if the world doesn't exist yet (view hasn't
     * called onLayout)
     */
    fun getGravity(): Vec2? {
        return world?.gravity
    }

    /**
     * Add a physics process listener
     */
    fun addOnPhysicsProcessedListener(listener: OnPhysicsProcessedListener) {
        onPhysicsProcessedListeners.add(listener)
    }

    /**
     * Remove a physics process listener
     */
    fun removeOnPhysicsProcessedListener(listener: OnPhysicsProcessedListener?) {
        onPhysicsProcessedListeners.remove(listener)
    }

    /**
     * Listen to when bodies are created in the world.
     */
    fun setOnBodyCreatedListener(listener: OnBodyCreatedListener?) {
        onBodyCreatedListener = listener
    }

    /**
     * Interface that allows hooks into the layout so that you can process or modify physics bodies each time that JBox2D processes physics
     */
    interface OnPhysicsProcessedListener {

        /**
         * Physics has been processed. Commence doing things that you want to do such as applying additional forces
         * @param physics the [Physics] that belongs to the view
         * @param world the Box2d world
         */
        fun onPhysicsProcessed(physics: Physics, world: World)
    }

    /**
     * A controller that will receive the drag events.
     */
    interface OnFlingListener {
        fun onGrabbed(grabbedView: View?)
        fun onReleased(releasedView: View?)
    }

    /**
     * Alerts you to collisions between views within the layout
     */
    interface OnCollisionListener {
        /**
         * Called when a collision is entered between two bodies. ViewId can also be
         * R.id.physics_layout_bound_top,
         * R.id.physics_layout_bound_bottom, R.id.physics_layout_bound_left, or
         * R.id.physics_layout_bound_right.
         * If view was not assigned an id, the return value will be [View.NO_ID].
         *
         * @param viewIdA view id of body A
         * @param viewIdB view id of body B
         */
        fun onCollisionEntered(viewIdA: Int, viewIdB: Int)

        /**
         * Called when a collision is exited between two bodies. ViewId can also be
         * R.id.physics_layout_bound_top,
         * R.id.physics_layout_bound_bottom, R.id.physics_layout_bound_left, or
         * R.id.physics_layout_bound_right.
         * If view was not assigned an id, the return value will be [View.NO_ID].
         *
         * @param viewIdA view id of body A
         * @param viewIdB view id of body B
         */
        fun onCollisionExited(viewIdA: Int, viewIdB: Int)
    }

    /**
     * Allows listening to when bodies are created for their respective views.
     */
    interface OnBodyCreatedListener {
        /**
         * A body has been created for this view
         * @param view the view associated with the body
         * @param body the body associated with the view
         */
        fun onBodyCreated(view: View, body: Body)
    }
}
