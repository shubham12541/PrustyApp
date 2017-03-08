package com.tominc.prustyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    EditText email, pass;
    TextView skip, register;
    Button submit;
    Toolbar toolbar;

    private final String LOGIN_URL = Config.BASE_URL + "login.php";
    SharedPreferences mPrefs;

    private final String TAG = "LoginActivity";
    SharedPreferences mPref;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    DatabaseReference mRefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mRefs = FirebaseDatabase.getInstance().getReference("users");

        mPref = getSharedPreferences("app", MODE_PRIVATE);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG, "onAuthStateChanged: User Logged In");
                    Intent in = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(in);
                    finish();
                } else{
                    Log.d(TAG, "onAuthStateChanged: User not Logged In");
                }
            }
        };

        mPrefs = getSharedPreferences("app", MODE_PRIVATE);
        String isLogin = mPrefs.getString("logedIn", null);
        if(isLogin!=null && isLogin.equals("yes")){
            Intent in = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(in);
            finish();
        }

        toolbar = (Toolbar) findViewById(R.id.login_toolbar);
        toolbar.setTitle("PrustyApp");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        email = (EditText) findViewById(R.id.login_email);
        pass = (EditText) findViewById(R.id.login_pass);
        submit = (Button) findViewById(R.id.login_submit);

        findViewById(R.id.login_skip).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signInAnonymously()
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onComplete: User logged annousmously");

                                if(!task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this, "Annonymous User Authentication Failed", Toast.LENGTH_SHORT).show();
                                }
                                Intent in = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(in);
                                finish();
                            }
                        });

            }
        });

        findViewById(R.id.login_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(in);
                finish();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_email = email.getText().toString();
                String s_pass = pass.getText().toString();

                if (s_email.length() == 0 || s_pass.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Incomplete Information", Toast.LENGTH_SHORT).show();
                } else {
                    loginUser(s_email, s_pass);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    private void loginUser(final String email, final String pass){

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onComplete: User Signed In");
                        if(!task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }
                        String userId = task.getResult().getUser().getUid();

                        Log.d(TAG, "onComplete: Userid: " + userId);
                        mRefs.child(userId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if(user!=null) {
                                    Log.d(TAG, "onDataChange: Got user info " + user.toString());


                                    SharedPreferences.Editor edit = mPref.edit();
                                    Gson gson = new Gson();
                                    String json = gson.toJson(user);
                                    edit.putString("user", json);
                                    edit.putString("logedIn", "yes");
                                    edit.apply();

                                    Intent in = new Intent(LoginActivity.this, MainActivity.class);
//                                  in.putExtra("user", user);
                                    startActivity(in);
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
    }
}
