package com.jawnnypoo.physicslayout.sample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.jawnnypoo.physicslayout.PhysicsLayout;


public class MainActivity extends ActionBarActivity {

    PhysicsLayout physicsLayout;
    SwitchCompat physicsSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        physicsLayout = (PhysicsLayout) findViewById(R.id.physics_layout);
        physicsSwitch = (SwitchCompat) findViewById(R.id.physics_switch);
        physicsSwitch.setChecked(true);
        physicsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    physicsLayout.enablePhysics();
                } else {
                    physicsLayout.disablePhysics();
                }
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
            physicsLayout.resetPhysics();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
