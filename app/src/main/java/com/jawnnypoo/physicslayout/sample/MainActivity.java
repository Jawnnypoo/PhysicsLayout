package com.jawnnypoo.physicslayout.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.jawnnypoo.physicslayout.PhysicsRelativeLayout;


public class MainActivity extends AppCompatActivity {

    PhysicsRelativeLayout physicsRelativeLayout;
    SwitchCompat physicsSwitch;
    SwitchCompat flingSwitch;
    View impulseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        physicsRelativeLayout = (PhysicsRelativeLayout) findViewById(R.id.physics_layout);
        physicsSwitch = (SwitchCompat) findViewById(R.id.physics_switch);
        flingSwitch = (SwitchCompat) findViewById(R.id.fling_switch);
        impulseButton = findViewById(R.id.impulse_button);
        physicsSwitch.setChecked(physicsRelativeLayout.getPhysicsHelper().isPhysicsEnabled());
        physicsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    physicsRelativeLayout.getPhysicsHelper().enablePhysics();
                } else {
                    physicsRelativeLayout.getPhysicsHelper().disablePhysics();
                }
            }
        });
        impulseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                physicsRelativeLayout.getPhysicsHelper().giveRandomImpulse();
            }
        });

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
            physicsRelativeLayout.getPhysicsHelper().resetPhysics();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
