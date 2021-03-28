package com.jawnnypoo.physicslayout.sample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.jawnnypoo.physicslayout.Physics
import com.jawnnypoo.physicslayout.PhysicsConfig
import com.jawnnypoo.physicslayout.Shape
import com.jawnnypoo.physicslayout.sample.databinding.ActivityMainBinding
import org.jbox2d.dynamics.Body
import org.jbox2d.dynamics.World

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TESTING"
    }

    private lateinit var binding: ActivityMainBinding
    private var index: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.setTitle(R.string.app_name)
        binding.toolbar.inflateMenu(R.menu.menu_main)
        binding.toolbar.setOnMenuItemClickListener(Toolbar.OnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_contributors) {
                startActivity(AboutActivity.newInstance(this@MainActivity))
                return@OnMenuItemClickListener true
            }
            false
        })
        binding.physicsLayout.physics.isFlingEnabled = true
        binding.physicsSwitch.isChecked = binding.physicsLayout.physics.isPhysicsEnabled
        binding.flingSwitch.isChecked = binding.physicsLayout.physics.isFlingEnabled
        binding.physicsSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.physicsLayout.physics.isPhysicsEnabled = isChecked
            if (!isChecked) {
                for (i in 0 until binding.physicsLayout.childCount) {
                    binding.physicsLayout.getChildAt(i)
                            .animate().translationY(0f).translationX(0f).rotation(0f)
                }
            }
        }
        binding.flingSwitch.setOnCheckedChangeListener { _, isChecked ->
            binding.physicsLayout.physics.isFlingEnabled = isChecked
        }
        binding.impulseButton.setOnClickListener { binding.physicsLayout.physics.giveRandomImpulse() }
        val circlePhysicsConfig = PhysicsConfig(
                shape = Shape.CIRCLE
        )
        binding.addViewButton.setOnClickListener {
            val imageView = ImageView(this)
            imageView.setImageResource(R.drawable.ic_logo)
            val layoutParams = LinearLayout.LayoutParams(
                    resources.getDimensionPixelSize(R.dimen.square_size),
                    resources.getDimensionPixelSize(R.dimen.square_size)
            )
            imageView.layoutParams = layoutParams
            imageView.id = index
            binding.physicsLayout.addView(imageView)
            index++
            Physics.setPhysicsConfig(imageView, circlePhysicsConfig)
        }


        for (i in 0 until binding.physicsLayout.childCount) {
            val imageView = binding.physicsLayout.getChildAt(i) as ImageView
            imageView.id = i
            imageView.setImageResource(R.drawable.ic_logo)
        }
        index = binding.physicsLayout.childCount + 1

        binding.physicsLayout.physics.setOnCollisionListener(object : Physics.OnCollisionListener {
            @SuppressLint("SetTextI18n")
            override fun onCollisionEntered(viewIdA: Int, viewIdB: Int) {
                binding.collisionView.text = "$viewIdA collided with $viewIdB"
            }

            override fun onCollisionExited(viewIdA: Int, viewIdB: Int) {}
        })

        binding.physicsLayout.physics.addOnPhysicsProcessedListener(object : Physics.OnPhysicsProcessedListener {
            override fun onPhysicsProcessed(physics: Physics, world: World) {
                Log.d(TAG, "onPhysicsProcessed")
            }
        })

        binding.physicsLayout.physics.setOnBodyCreatedListener(object : Physics.OnBodyCreatedListener {
            override fun onBodyCreated(view: View, body: Body) {
                Log.d(TAG, "Body created for view ${view.id}")
            }
        })
    }
}
