package com.jawnnypoo.physicslayout.sample

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.graphics.drawable.DrawerArrowDrawable
import coil.load
import com.commit451.gimbal.Gimbal
import com.jawnnypoo.physicslayout.Physics
import com.jawnnypoo.physicslayout.PhysicsConfig
import com.jawnnypoo.physicslayout.Shape
import com.jawnnypoo.physicslayout.sample.databinding.ActivityAboutBinding
import com.jawnnypoo.physicslayout.sample.github.Contributor
import com.wefika.flowlayout.FlowLayout
import de.hdodenhof.circleimageview.CircleImageView

class AboutActivity : AppCompatActivity() {

    companion object {

        fun newInstance(context: Context): Intent {
            return Intent(context, AboutActivity::class.java)
        }
    }

    private lateinit var binding: ActivityAboutBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var gravitySensor: Sensor
    private lateinit var gimbal: Gimbal

    private val sensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_GRAVITY) {
                gimbal.normalizeGravityEvent(event)
                binding.physicsLayout.physics.setGravity(-event.values[0], event.values[1])
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gimbal = Gimbal(this)
        gimbal.lock()
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        findViewById<View>(R.id.sauce).setOnClickListener { openPage("https://github.com/Jawnnypoo/PhysicsLayout") }
        binding.toolbar.setTitle(R.string.app_name)
        val drawerArrowDrawable = DrawerArrowDrawable(this)
        drawerArrowDrawable.progress = 1.0f
        binding.toolbar.navigationIcon = drawerArrowDrawable
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val model: ContributorsViewModel by viewModels()
        model.getContributors().observe(this, { contributors ->
            addContributors(contributors)
        })
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(sensorEventListener)
    }

    private fun addContributors(contributors: List<Contributor>) {
        val config = PhysicsConfig(shape = Shape.CIRCLE)
        val borderSize = resources.getDimensionPixelSize(R.dimen.border_size)
        val imageSize = resources.getDimensionPixelSize(R.dimen.circle_size)
        for (i in contributors.indices) {
            val contributor = contributors[i]
            val imageView = CircleImageView(this)
            val llp = FlowLayout.LayoutParams(
                    imageSize,
                    imageSize)
            imageView.layoutParams = llp
            imageView.borderWidth = borderSize
            imageView.borderColor = Color.BLACK
            Physics.setPhysicsConfig(imageView, config)
            binding.physicsLayout.addView(imageView)

            imageView.load(contributor.avatarUrl)
        }
        binding.physicsLayout.requestLayout()
    }

    @Suppress("SameParameterValue")
    private fun openPage(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "You don't have a browser... What are you doing?", Toast.LENGTH_LONG)
                    .show()
        }

    }
}
