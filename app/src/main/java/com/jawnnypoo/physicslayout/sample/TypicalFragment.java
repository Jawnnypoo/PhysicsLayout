package com.jawnnypoo.physicslayout.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jawnnypoo.physicslayout.PhysicsRelativeLayout;
import com.squareup.picasso.Picasso;

/**
 * This may seem familiar....
 * Created by Jawn on 5/5/2015.
 */
public class TypicalFragment extends Fragment{

    public static TypicalFragment newInstance() {
        TypicalFragment fragment = new TypicalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    PhysicsRelativeLayout mainLayout;


    private final View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainLayout.getPhysics().enablePhysics();
            mainLayout.getPhysics().enableFling();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_typical, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainLayout = (PhysicsRelativeLayout) view;
        ImageView image = (ImageView) view.findViewById(R.id.image);
        Picasso.with(getActivity())
                .load("http://lorempixel.com/1600/900/cats/4")
                .placeholder(R.drawable.ic_launcher)
                .into(image);

        ImageView profilePic = (ImageView) view.findViewById(R.id.profile_pic);
        Picasso.with(getActivity())
                .load("http://lorempixel.com/200/200/cats/1")
                .placeholder(R.drawable.ic_launcher)
                .into(profilePic);

        ImageView profilePicOp = (ImageView) view.findViewById(R.id.profile_pic_op);
        Picasso.with(getActivity())
                .load("http://lorempixel.com/1600/900/cats/4")
                .fit()
                .centerCrop()
                .placeholder(R.drawable.ic_launcher)
                .into(profilePicOp);

        View fab = mainLayout.findViewById(R.id.fab);
        fab.setOnClickListener(onClickListener);

    }
}
