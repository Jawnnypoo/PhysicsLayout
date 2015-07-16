package com.jawnnypoo.physicslayout.sample;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;
import com.jawnnypoo.physicslayout.sample.github.Contributor;
import com.jawnnypoo.physicslayout.sample.github.GithubClient;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jawn on 7/15/2015.
 */
public class AboutActivity extends AppCompatActivity {

    private static final String REPO_USER = "Jawnnypoo";
    private static final String REPO_NAME = "PhysicsLayout";

    public static Intent newInstance(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        return intent;
    }

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.physics_layout)
    PhysicsFrameLayout physicsLayout;
    @OnClick(R.id.sauce)
    void onSauceClick() {
        openPage("https://github.com/Jawnnypoo/PhysicsLayout");
    }

    SensorManager sensorManager;
    Sensor gravitySensor;

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_GRAVITY) {
                WindowUtil.normalizeForOrientation(getWindow(), event);
                physicsLayout.getPhysics().setGravity(-event.values[0], event.values[1]);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) { }
    };

    private final Callback<List<Contributor>> contributorResponseCallback = new Callback<List<Contributor>>() {
        @Override
        public void success(List<Contributor> contributorList, Response response) {
            addContributors(contributorList);
        }

        @Override
        public void failure(RetrofitError error) {
            error.printStackTrace();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowUtil.lockToCurrentOrientation(this);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        GithubClient.instance().contributors(REPO_USER, REPO_NAME, contributorResponseCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(sensorEventListener, gravitySensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorEventListener);
    }

    private void addContributors(List<Contributor> contributors) {
        PhysicsConfig config = new PhysicsConfig.Builder()
                .setShapeType(PhysicsConfig.ShapeType.CIRCLE)
                .setDensity(1.0f)
                .setFriction(0.0f)
                .setRestitution(0.0f)
                .build();
        int borderSize = getResources().getDimensionPixelSize(R.dimen.border_size);
        int x = 0;
        int y = 0;
        int imageSize = getResources().getDimensionPixelSize(R.dimen.circle_size);
        for (int i=0; i<contributors.size(); i++) {
            Contributor contributor = contributors.get(i);
            CircleImageView imageView = new CircleImageView(this);
            FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(
                    imageSize,
                    imageSize);
            imageView.setLayoutParams(llp);
            imageView.setBorderWidth(borderSize);
            imageView.setBorderColor(Color.BLACK);
            Physics.setPhysicsConfig(imageView, config);
            physicsLayout.addView(imageView);
            imageView.setX(x);
            imageView.setY(y);

            x = (x + imageSize);
            if (x > physicsLayout.getWidth()) {
                x = 0;
                y = (y + imageSize) % physicsLayout.getHeight();
            }
            Picasso.with(this)
                    .load(contributor.avatarUrl)
                    .into(imageView);
        }
        physicsLayout.getPhysics().onLayout(true);
    }

    public void openPage(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "You don't have a browser... What are you doing?", Toast.LENGTH_LONG)
                    .show();
        }
    }
}
