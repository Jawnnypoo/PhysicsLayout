package com.jawnnypoo.physicslayout.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsLinearLayout;


public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    PhysicsLinearLayout physicsRelativeLayout;
    SwitchCompat physicsSwitch;
    SwitchCompat flingSwitch;
    View impulseButton;
    View addViewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        physicsRelativeLayout = (PhysicsLinearLayout) findViewById(R.id.physics_layout);
        physicsSwitch = (SwitchCompat) findViewById(R.id.physics_switch);
        flingSwitch = (SwitchCompat) findViewById(R.id.fling_switch);
        impulseButton = findViewById(R.id.impulse_button);
        addViewButton = findViewById(R.id.add_view_button);
        physicsSwitch.setChecked(physicsRelativeLayout.getPhysics().isPhysicsEnabled());
        physicsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    physicsRelativeLayout.getPhysics().enablePhysics();
                } else {
                    physicsRelativeLayout.getPhysics().disablePhysics();
                }
            }
        });
        impulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                physicsRelativeLayout.getPhysics().giveRandomImpulse();
            }
        });
        final View circleView = findViewById(R.id.circle);
        addViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(R.drawable.ic_launcher);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(llp);
                physicsRelativeLayout.addView(imageView);
            }
        });
        PhysicsConfig config = new PhysicsConfig.Builder()
                .setShapeType(PhysicsConfig.ShapeType.CIRCLE)
                .build();
        physicsRelativeLayout.getPhysics().setPhysicsConfig(circleView, config);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            physicsRelativeLayout.getPhysics().resetPhysics();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
