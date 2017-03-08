package com.tominc.prustyapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.usb.UsbRequest;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText f_name, l_name, email, pass, c_pass, phone, college, year;
    Button submit;
    CircularImageView profile_image;
    Toolbar toolbar;

    private final int IMAGE_REQUEST=12;
    private final String ADD_USER_URL = Config.BASE_URL + "register.php";
    SharedPreferences mPref;

    private final String TAG = "RegisterActivity";

    private Uri profilePic;

    private StorageReference mStorageRef;
    private DatabaseReference mRefs;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mStorageRef = FirebaseStorage.getInstance().getReference("profiles");
        mRefs = FirebaseDatabase.getInstance().getReference("users");

        toolbar = (Toolbar) findViewById(R.id.register_toolbar);
        toolbar.setTitle("Register");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);


        mPref = getSharedPreferences("app", MODE_PRIVATE);

        f_name = (EditText) findViewById(R.id.register_f_name);
        l_name = (EditText) findViewById(R.id.register_l_name);
        email = (EditText) findViewById(R.id.register_email);
        pass = (EditText) findViewById(R.id.register_pass);
        college = (EditText) findViewById(R.id.register_college);
        c_pass = (EditText) findViewById(R.id.register_con_pass);
        phone = (EditText) findViewById(R.id.register_phone);
        year = (EditText) findViewById(R.id.register_year);
        profile_image = (CircularImageView) findViewById(R.id.register_image);
        submit = (Button) findViewById(R.id.register_submit);

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in, IMAGE_REQUEST);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s_name = f_name.getText().toString() + " " + l_name.getText().toString();
                String s_email = email.getText().toString();
                String s_pass = pass.getText().toString();
                String s_c_pass = c_pass.getText().toString();
                String s_phone = phone.getText().toString();
                String s_college = college.getText().toString();
                String s_year = year.getText().toString();

                if (s_name.length() == 0 || s_email.length() == 0 || s_pass.length() == 0 || s_c_pass.length() == 0
                        || s_phone.length() == 0 || s_c_pass.length() == 0 || s_college.length() == 0
                        || s_year.length()==0) {
                    Toast.makeText(getApplicationContext(), "Fill all details", Toast.LENGTH_SHORT).show();
                } else {
                    if (s_pass.equals(s_c_pass)) {
                        addUser(s_name, s_email, s_pass, s_college, s_phone, s_year);
                    } else {
                        Toast.makeText(getApplicationContext(), "Password donot match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST){
            if(resultCode == RESULT_OK){
                Uri selectedImage = data.getData();
                profilePic = selectedImage;
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                    Bitmap bitmap = BitmapFactory.decodeFile(imgDecoableString);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    int height = bitmap.getHeight();
                    int width = bitmap.getWidth();
                    Bitmap bmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);

                    profile_image.setImageBitmap(bmp);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addUser(final String name, final String email, final String pass, final String college, final String phone, final String year){

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Log.d(TAG, "onComplete: User Created");
                        if(profilePic != null) {

                            mStorageRef = mStorageRef.child("images/" + profilePic.getLastPathSegment());
                            StorageMetadata metadata = new StorageMetadata.Builder()
                                    .setContentType("image/jpg")
                                    .build();
                            UploadTask uploadTask = mStorageRef.putFile(profilePic, metadata);


                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: Image cannot be uploaded");
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    Log.d(TAG, "onSuccess: File Uploaded succesfully");
                                }
                            });

                        }
                        FirebaseUser firebaseUser = task.getResult().getUser();


                        UserProfileChangeRequest profileUpdates;
                        if(profilePic != null){
                            profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .setPhotoUri(profilePic)
                                    .build();
                        } else{
                            profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();
                        }

                        firebaseUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d(TAG, "onComplete: User Profile Updated");
                                        }
                                    }
                                });

                        User user = new User();
                        user.setName(name);
                        user.setEmail(email);
                        user.setCollege(college);
                        user.setPhone(phone);
                        user.setYear(year);

//                        String userId = mRefs.push().getKey();
                        String userId = mAuth.getCurrentUser().getUid();
                        mRefs.child(userId).setValue(user);
//                        mRefs.child(email).setValue(user);

                        SharedPreferences.Editor edit = mPref.edit();
                        Gson gson = new Gson();
                        String json = gson.toJson(user);
                        edit.putString("user", json);
                        edit.putString("logedIn", "yes");
                        edit.apply();

                        Intent in = new Intent(RegisterActivity.this, MainActivity.class);
//                        in.putExtra("user", user);
                        startActivity(in);
                        finish();
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
