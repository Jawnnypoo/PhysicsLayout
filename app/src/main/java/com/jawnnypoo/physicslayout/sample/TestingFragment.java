package com.jawnnypoo.physicslayout.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jawnnypoo.physicslayout.Physics;
import com.jawnnypoo.physicslayout.PhysicsConfig;
import com.jawnnypoo.physicslayout.PhysicsRelativeLayout;
import com.squareup.picasso.Picasso;

/**
 * Fragment for testing stuffz
 * Created by Jawn on 5/5/2015.
 */
public class TestingFragment extends Fragment {

    public static TestingFragment newInstance() {
        TestingFragment fragment = new TestingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    PhysicsRelativeLayout physicsLayout;
    SwitchCompat physicsSwitch;
    SwitchCompat flingSwitch;
    View impulseButton;
    View addViewButton;
    TextView collisionView;

    int catIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_testing, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        physicsLayout = (PhysicsRelativeLayout) view.findViewById(R.id.physics_layout);
        physicsSwitch = (SwitchCompat) view.findViewById(R.id.physics_switch);
        flingSwitch = (SwitchCompat) view.findViewById(R.id.fling_switch);
        impulseButton = view.findViewById(R.id.impulse_button);
        addViewButton = view.findViewById(R.id.add_view_button);
        collisionView = (TextView) view.findViewById(R.id.collision);
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
        final View circleView = view.findViewById(R.id.circle);
        addViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ImageView imageView = new ImageView(getActivity());
                imageView.setImageResource(R.drawable.ic_launcher);
                LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.square_size),
                        getResources().getDimensionPixelSize(R.dimen.square_size));
                imageView.setLayoutParams(llp);
                imageView.setId(catIndex);
                physicsLayout.addView(imageView);
                Picasso.with(getActivity())
                        .load("http://lorempixel.com/200/200/cats/" + ((catIndex % 10) + 1))
                        .placeholder(R.drawable.ic_launcher)
                        .into(imageView);
                catIndex++;
            }
        });

        //Customizing the physics of the view
        PhysicsConfig config = new PhysicsConfig.Builder()
                .setShapeType(PhysicsConfig.ShapeType.CIRCLE)
                .setDensity(1.2f)
                .setFriction(1.2f)
                .setRestitution(1.2f)
                .build();
        Physics.setPhysicsConfig(circleView, config);


        for (int i=0; i<physicsLayout.getChildCount(); i++) {
            ImageView imageView = (ImageView) physicsLayout.getChildAt(i);
            imageView.setId(i);
            Picasso.with(getActivity())
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
