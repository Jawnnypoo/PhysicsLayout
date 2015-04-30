package com.jawnnypoo.physicslayout.sample;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jawnnypoo.physicslayout.PhysicsLinearLayout;

/**
 * Created by Jawn on 4/29/2015.
 */
public class TypicalActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typical);
        PhysicsLinearLayout layout = (PhysicsLinearLayout) findViewById(R.id.physics_layout);
        layout.getPhysics().enablePhysics();
    }
}
