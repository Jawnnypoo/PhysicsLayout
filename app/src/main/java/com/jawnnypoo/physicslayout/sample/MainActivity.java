package com.jawnnypoo.physicslayout.sample;

import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsRelativeLayout;
import com.squareup.picasso.Picasso;


public class MainActivity extends BaseActivity {

    Toolbar toolbar;
    PhysicsRelativeLayout physicsLayout;
    SwitchCompat physicsSwitch;
    SwitchCompat flingSwitch;
    View impulseButton;
    View addViewButton;

    int catIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        physicsLayout = (PhysicsRelativeLayout) findViewById(R.id.physics_layout);
        physicsSwitch = (SwitchCompat) findViewById(R.id.physics_switch);
        flingSwitch = (SwitchCompat) findViewById(R.id.fling_switch);
        impulseButton = findViewById(R.id.impulse_button);
        addViewButton = findViewById(R.id.add_view_button);
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
                physicsLayout.addView(imageView);
                Picasso.with(MainActivity.this)
                        .load("http://lorempixel.com/200/200/cats/" + ((catIndex % 10) + 1))
                        .placeholder(R.drawable.ic_launcher)
                        .into(imageView);
                catIndex++;
            }
        });
        PhysicsConfig config = new PhysicsConfig.Builder()
                .setShapeType(PhysicsConfig.ShapeType.CIRCLE)
                .build();
        physicsLayout.getPhysics().setPhysicsConfig(circleView, config);


        for (int i=0; i<physicsLayout.getChildCount(); i++) {
            ImageView view = (ImageView) physicsLayout.getChildAt(i);
            Picasso.with(this)
                    .load("http://lorempixel.com/200/200/cats/" + (i + 1))
                    .placeholder(R.drawable.ic_launcher)
                    .into(view);
        }
        catIndex = physicsLayout.getChildCount();

    }
}
