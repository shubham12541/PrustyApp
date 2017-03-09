package com.tominc.prustyapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;


public class ProfileFragment extends Fragment {

    Toolbar toolbar;
    Button liked, listed;
    CircularImageView profile_image;
    TextView name, email, phone, year, college;
    SharedPreferences mPrefs;


    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_profile, container, false);

        Bundle bundle = getArguments();
        User user = (User) bundle.getSerializable("user");

        Log.d("ProfileFragment", user.getEmail());

        name = (TextView) root.findViewById(R.id.show_profile_name);
        email = (TextView) root.findViewById(R.id.show_profile_email);
        phone = (TextView) root.findViewById(R.id.show_profile_phone);
        year = (TextView) root.findViewById(R.id.show_profile_year);
        college = (TextView) root.findViewById(R.id.show_profile_college);
        profile_image = (CircularImageView) root.findViewById(R.id.show_profile_image);
        liked = (Button) root.findViewById(R.id.show_profile_liked);
        listed = (Button) root.findViewById(R.id.show_profile_listed);


        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        year.setText("Year: " + user.getYear());
        college.setText("College: " + user.getCollege());

        liked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        listed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        return root;

    }


}