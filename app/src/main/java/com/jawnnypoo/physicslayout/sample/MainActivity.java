package com.jawnnypoo.physicslayout.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsFrameLayout;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.physics_layout)
    PhysicsFrameLayout physicsLayout;
    @Bind(R.id.physics_switch)
    SwitchCompat physicsSwitch;
    @Bind(R.id.fling_switch)
    SwitchCompat flingSwitch;
    @Bind(R.id.impulse_button)
    View impulseButton;
    @Bind(R.id.add_view_button)
    View addViewButton;
    @Bind(R.id.collision)
    TextView collisionView;

    int catIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        toolbar.setTitle(R.string.app_name);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_contributors) {
                    startActivity(AboutActivity.newInstance(MainActivity.this));
                    return true;
                }
                return false;
            }
        });
        physicsSwitch.setChecked(physicsLayout.getPhysics().isPhysicsEnabled());
        physicsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    physicsLayout.getPhysics().enablePhysics();
                } else {
                    physicsLayout.getPhysics().disablePhysics();
                    for (int i=0; i<physicsLayout.getChildCount(); i++) {
                        physicsLayout.getChildAt(i)
                                .animate().translationY(0).translationX(0).rotation(0);
                    }
                }
            }
        });
        flingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    physicsLayout.getPhysics().enableFling();
                } else {
                    physicsLayout.getPhysics().disableFling();
                }
            }
        });
        impulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                physicsLayout.getPhysics().giveRandomImpulse();
            }
        });
        final View circleView = findViewById(R.id.circle);
        addViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(R.drawable.ic_launcher);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.square_size),
                        getResources().getDimensionPixelSize(R.dimen.square_size));
                imageView.setLayoutParams(llp);
                imageView.setId(catIndex);
                physicsLayout.addView(imageView);
                Picasso.with(MainActivity.this)
                        .load("http://lorempixel.com/200/200/cats/" + ((catIndex % 10) + 1))
                        .placeholder(R.drawable.ic_launcher)
                        .into(imageView);
                catIndex++;
            }
        });

        //Customizing the physics of the view
//        PhysicsConfig config = new PhysicsConfig.Builder()
//                .setShapeType(PhysicsConfig.SHAPE_TYPE_CIRCLE)
//                .setDensity(0.6f)
//                .setFriction(0.4f)
//                .setRestitution(0.4f)
//                .build();
//        Physics.setPhysicsConfig(circleView, config);


        for (int i=0; i<physicsLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) physicsLayout.getChildAt(i);
            imageView.setId(i);
            Picasso.with(this)
                    .load("http://lorempixel.com/200/200/cats/" + (i + 1))
                    .placeholder(R.drawable.ic_launcher)
                    .into(imageView);
        }
        catIndex = physicsLayout.getChildCount();

        physicsLayout.getPhysics().setOnCollisionListener(new Physics.OnCollisionListener() {
            @Override
            public void onCollisionEntered(int viewIdA, int viewIdB) {
                collisionView.setText(viewIdA + " collided with " + viewIdB);
            }

            @Override
            public void onCollisionExited(int viewIdA, int viewIdB) {

            }
        });
    }
}
