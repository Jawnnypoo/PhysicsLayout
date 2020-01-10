package com.jawnnypoo.physicslayout.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.jawnnypoo.physicslayout.Physics
import com.jawnnypoo.physicslayout.PhysicsFrameLayout
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.World

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TESTING"
    }

    private lateinit var toolbar: Toolbar
    private lateinit var physicsLayout: PhysicsFrameLayout
    private lateinit var physicsSwitch: SwitchCompat
    private lateinit var flingSwitch: SwitchCompat
    private lateinit var impulseButton: View
    private lateinit var addViewButton: View
    private lateinit var collisionView: TextView

    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        physicsLayout = findViewById(R.id.physics_layout)
        physicsSwitch = findViewById(R.id.physics_switch)
        flingSwitch = findViewById(R.id.fling_switch)
        impulseButton = findViewById(R.id.impulse_button)
        addViewButton = findViewById(R.id.add_view_button)
        collisionView = findViewById(R.id.collision)
        toolbar.setTitle(R.string.app_name)
        toolbar.inflateMenu(R.menu.menu_main)
        toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_contributors) {
                startActivity(AboutActivity.newInstance(this@MainActivity))
                return@OnMenuItemClickListener true
            }
            false
        })
        physicsSwitch.isChecked = physicsLayout.physics.isPhysicsEnabled
        physicsSwitch.setOnCheckedChangeListener { _, isChecked ->
            physicsLayout.physics.isPhysicsEnabled = isChecked
            if (!isChecked) {
                for (i in 0 until physicsLayout.childCount) {
                    physicsLayout.getChildAt(i)
                        .animate().translationY(0f).translationX(0f).rotation(0f)
                }
            }
        }
        flingSwitch.setOnCheckedChangeListener { _, isChecked ->
            physicsLayout.physics.isFlingEnabled = isChecked
        }
        impulseButton.setOnClickListener { physicsLayout.physics.giveRandomImpulse() }
        val circleView = findViewById<View>(R.id.circle)
        addViewButton.setOnClickListener {
            val imageView = ImageView(this@MainActivity)
            imageView.setImageResource(R.drawable.ic_logo)
            val llp = LinearLayout.LayoutParams(
                resources.getDimensionPixelSize(R.dimen.square_size),
                resources.getDimensionPixelSize(R.dimen.square_size))
            imageView.layoutParams = llp
            imageView.id = index
            physicsLayout.addView(imageView)
            index++
        }


        for (i in 0 until physicsLayout.childCount) {
            val imageView = physicsLayout.getChildAt(i) as ImageView
            imageView.id = i
            imageView.setImageResource(R.drawable.ic_logo)
        }
        index = physicsLayout.childCount

        physicsLayout.physics.setOnCollisionListener(object : Physics.OnCollisionListener {
            @SuppressLint("SetTextI18n")
            override fun onCollisionEntered(viewIdA: Int, viewIdB: Int) {
                collisionView.text = "$viewIdA collided with $viewIdB"
            }

            override fun onCollisionExited(viewIdA: Int, viewIdB: Int) {

            }
        })

        physicsLayout.physics.addOnPhysicsProcessedListener(object : Physics.OnPhysicsProcessedListener {
            override fun onPhysicsProcessed(physics: Physics, world: World) {
                val body = physics.findBodyById(circleView.id)
                body?.applyAngularImpulse(0.001f) ?: Log.e(TAG, "Cannot rotate, body was null")
            }

        })

        physicsLayout.physics.setOnBodyCreatedListener(object : Physics.OnBodyCreatedListener {
            override fun onBodyCreated(view: View, body: Body) {
                Log.d(TAG, "Body created for view ${view.id}")
            }

        })
    }
}
