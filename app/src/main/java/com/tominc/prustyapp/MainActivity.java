package com.tominc.prustyapp;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.tominc.prustyapp.utilities.DownloadFirebaseImage;
import com.tominc.prustyapp.utilities.DownloadMethods;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.mateware.snacky.Snacky;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager;
    SharedPreferences mPrefs;
    User user;
    Fragment1 fragment1;
    private final int REQUEST_ADD_PRODUCT = 122;
    private final String TAG = "MainActivity";

    TextView nav_header_name, nav_header_email;
    ImageView nav_header_image;
    RelativeLayout nav_header_loading_image;

    StorageReference mStorage;

    private FirebaseAuth mAuth;
    private boolean isAtHome=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle(R.string.products);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser mUser = mAuth.getCurrentUser();

        mStorage = FirebaseStorage
                .getInstance()
                .getReference("profiles")
                .child("images/" + mUser.getUid() + "/profile.jpg");


        mPrefs = getSharedPreferences("app", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        user = gson.fromJson(json, User.class);


        fragment1 = Fragment1.newInstance(user);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_content, fragment1).commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();

                Intent in = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(in);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View view = navigationView.getHeaderView(0);

        nav_header_name= (TextView) view.findViewById(R.id.nav_header_name);
        nav_header_email = (TextView) view.findViewById(R.id.nav_header_email);
        nav_header_image = (ImageView) view.findViewById(R.id.nav_header_profile_pic);
        nav_header_loading_image = (RelativeLayout) view.findViewById(R.id.loading);

        showNavLoadingImage();

//        downloadProfilePic();
        downloadProfilePicViaURL();

        if(user != null){
            nav_header_name.setText(user.getName());
            nav_header_email.setText(user.getEmail());
        }

        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mUser = firebaseAuth.getCurrentUser();
                if(mUser == null){
                    Intent in = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            }
        };

//        mAuth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(isAtHome)
            super.onBackPressed();
            else {
                fragmentManager.beginTransaction().replace(R.id.frame_content,Fragment1.newInstance(user)).commit();
                isAtHome=true;
                setTitle(R.string.products);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_logout){
            SharedPreferences.Editor edit = mPrefs.edit();
            edit.clear();
            edit.apply();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ((ActivityManager)getApplicationContext().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData();
            }

            mAuth.signOut();
            Intent in = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(in);
            finish();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            isAtHome=true;
            fragmentManager.beginTransaction().replace(R.id.frame_content, Fragment1.newInstance(user)).commit();
            setTitle(item.getTitle());
        }  else if (id == R.id.nav_slideshow) {
            isAtHome=false;
            fragmentManager.beginTransaction().replace(R.id.frame_content, ProfileFragment.newInstance(user)).commit();
            setTitle(item.getTitle());
        } else if (id == R.id.nav_manage) {
            isAtHome=false;
            fragmentManager.beginTransaction().replace(R.id.frame_content, ProductLikedFragment.newInstance(user)).commit();
            setTitle(item.getTitle());
        } else if (id == R.id.nav_listed) {
            isAtHome=false;
            fragmentManager.beginTransaction().replace(R.id.frame_content, ProductLIstedFragment.newInstance(user)).commit();
            setTitle(item.getTitle());
        } else if (id == R.id.nav_share) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Download this amazing app from play store: PrustyApp");
            sendIntent.setType("text/plain");

            if (sendIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(sendIntent);
            }
        } else if (id == R.id.nav_send){
            sendFeedback();
        }

//        setTitle(item.getTitle());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendFeedback(){
        Intent in = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "shubhamchaudhary376@gmail.com"));
        in.setType("message/rfc822");
        in.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " app feedback");
        in.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.feedback_text));

        startActivity(Intent.createChooser(in, "Choose Mail Client"));
    }

    private void downloadProfilePicViaURL(){
        DownloadFirebaseImage downloadImage = new DownloadFirebaseImage(MainActivity.this);
        downloadImage.download(mStorage, nav_header_image, "Profile Pic could not be downloaded", new DownloadMethods() {
            @Override
            public void successMethod() {
                hideNavLoadingImage();
            }

            @Override
            public void failMethod() {
                hideNavLoadingImage();
            }
        });
    }

    private void showNavLoadingImage(){
        nav_header_loading_image.setVisibility(View.VISIBLE);
    }

    private void hideNavLoadingImage(){
        nav_header_loading_image.setVisibility(View.GONE);
    }

}
