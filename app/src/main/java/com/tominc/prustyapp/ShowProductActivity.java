package com.tominc.prustyapp;

import android.*;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.bumptech.glide.Glide;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import de.mateware.snacky.Snacky;

public class ShowProductActivity extends AppCompatActivity {
    RelativeLayout sliderLayout;
    Toolbar toolbar;

    private final String TAG = "ShowProductActivity";

    TextView productName, productPrice, productDescription, productWanted,
            profileName, profileEmail, profilePhone, profileCollege;
    CircularImageView profile_pic;

    ImageView share_call, share_message, share_email;

    LinearLayout profileCardFront, profileCardBack;
    CardView cardRootLayout;
    ImageView like_button, share_button;
    ScrollView allItems;

    private final String UPDATE_WANTED_URL = Config.BASE_URL +  "update_wanted.php";
    private final String IMAGE_DOWNLOAD_BASE = Config.BASE_URL + "uploads/";
    private final int IMAGE_HEIGHT = 200;

    private SliderLayout mSlider;
    SharedPreferences mPrefs;
    SharedPreferences mPrefs_user;
    private boolean iWantIt=false;

    private final int PERMISSION_REQUSET = 13;

    User user;
    Product prod;
    private ArrayList<Bitmap> imagesBitmap = new ArrayList<>();

    DatabaseReference mRef;
    StorageReference mStorage;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product_adapter);

        toolbar = (Toolbar) findViewById(R.id.show_product_toolbar);

        productName = (TextView) findViewById(R.id.show_product_name);
        productPrice = (TextView) findViewById(R.id.show_product_price);
        productDescription = (TextView) findViewById(R.id.show_product_description);
        productWanted = (TextView) findViewById(R.id.show_product_views);
        profileCardFront = (LinearLayout) findViewById(R.id.profile_front);
        profileCardBack = (LinearLayout) findViewById(R.id.profile_back);
        cardRootLayout = (CardView) findViewById(R.id.profile_cards_root);
        profile_pic = (CircularImageView) findViewById(R.id.show_profile_image);
        profileName = (TextView) findViewById(R.id.show_profile_name);
        profilePhone = (TextView) findViewById(R.id.show_profile_phone);
        profileEmail = (TextView) findViewById(R.id.show_profile_email);
        profileCollege = (TextView) findViewById(R.id.show_profile_college);
        like_button = (ImageView) findViewById(R.id.slider_item_like);
        share_button = (ImageView) findViewById(R.id.slider_item_share);
        share_call = (ImageView) findViewById(R.id.product_call);
        share_message = (ImageView) findViewById(R.id.product_message);
        share_email = (ImageView) findViewById(R.id.product_email);
        allItems = (ScrollView) findViewById(R.id.show_product_scroll);



        profileCardFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard(profileCardFront, profileCardBack, cardRootLayout);
            }
        });

        profileCardBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard(profileCardFront, profileCardBack, cardRootLayout);
            }
        });

        sliderLayout = (RelativeLayout) findViewById(R.id.slider_relative);
        mSlider = (SliderLayout) findViewById(R.id.show_image_slider);

        mSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.show_image_indicator));
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(12000);

        Intent in = getIntent();
        prod = (Product) in.getSerializableExtra("prod");
        mPrefs = getSharedPreferences(prod.getProductId(), MODE_PRIVATE);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        mPrefs_user = getSharedPreferences("app", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs_user.getString("user", "");
        user = gson.fromJson(json, User.class);

        loadProfilePic();

        String liked = mPrefs.getString(prod.getName(), null);
        if(liked!=null){
            iWantIt = true;
            like_button.setImageResource(android.R.drawable.star_big_on);
        }

        toolbar.setTitle(prod.getName().toUpperCase());
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        productName.setText(prod.getName());
        productPrice.setText("Price: Rs. " + prod.getPrice());
        productDescription.setText(prod.getDescription());
//        productWanted.setText("Wanted: " + prod.getWanted());
        profileName.setText(user.getName());
        profileEmail.setText(user.getEmail());
        profilePhone.setText(prod.getPhone());
        profileCollege.setText("Year " + prod.getYear() + ", " + prod.getCollege());

//        new DownloadImages(prod.getImageCount(), prod.getEmail(), prod.getName()).execute();
        downloadImagePermission();

        like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                updateWanted();
                if(!iWantIt){
                    iWantIt = true;
                    bookmarkProduct();
                    like_button.setImageResource(R.drawable.ic_bookmark_black_24dp);
//                    like_button.setImageResource(android.R.drawable.star_big_on);

                    SharedPreferences.Editor edit = mPrefs.edit();
                    edit.putString(prod.getName(), "y");
                    edit.apply();

                } else{
                    iWantIt=false;
                    unbookmarkProduct();
                    like_button.setImageResource(android.R.drawable.star_big_off);
                    like_button.setImageResource(R.drawable.ic_bookmark_border_black_24dp);

                    SharedPreferences.Editor edit = mPrefs.edit();
                    edit.putString(prod.getName(), null);
                    edit.apply();
                }
            }
        });

        share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Handle share
            }
        });

        share_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+"+91"+prod.getPhone()));
                startActivity(in);
            }
        });

        share_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsMessage();
            }
        });

        share_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setType("plain/text");
                FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                sendIntent.setData(Uri.parse(mUser.getEmail()));
                sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { mUser.getEmail() });
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, prod.getName() + ": on app");
                sendIntent.putExtra(Intent.EXTRA_TEXT, "I want to buy the product reply to negotiate.");
                startActivity(sendIntent);
            }
        });

    }

    private void downloadImagePermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(ShowProductActivity.this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d(TAG, "downloadImagePermission: Requesting permisstion");
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(ShowProductActivity.this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUSET);
        } else{
            Log.d(TAG, "onRequestPermissionsResult: permission granted");
//            new DownloadImages(prod.getImageCount(), prod.getEmail(), prod.getName()).execute();
            downloadAllImages();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_REQUSET:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
//                    new DownloadImages(prod.getImageCount(), prod.getEmail(), prod.getName()).execute();
                    downloadAllImages();
                } else{
//                    Toast.makeText(getApplicationContext(), "Permisston required to upload pic", Toast.LENGTH_SHORT)
//                            .show();
                    Snacky.builder().setView(allItems)
                            .setActivty(ShowProductActivity.this)
                            .setText(R.string.permission_warning)
                            .setDuration(Snacky.LENGTH_SHORT)
                            .warning();
                }
                return;
        }
    }


    private void flipCard(View front, View back, View root) {
        FlipAnimation flipanim = new FlipAnimation(front, back);
        if(front.getVisibility() == View.GONE){
            flipanim.reverse();
        }
        root.startAnimation(flipanim);

    }

    @Override
    protected void onStop() {
        mSlider.stopAutoCycle();
        super.onStop();
    }

    public class FlipAnimation extends Animation {
        private Camera camera;
        private View fromview, toview;
        private float centerX, centerY;

        private boolean forward = true;

        public FlipAnimation(View fromview, View toview){
            this.fromview=fromview;
            this.toview=toview;

            setDuration(700);
            setFillAfter(false);
            setInterpolator(new AccelerateDecelerateInterpolator());
        }

        public void reverse(){
            forward = false;
            View temp = fromview;
            fromview=toview;
            toview=temp;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            centerX=width/2;
            centerY=height/2;
            camera = new Camera();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final double radians = Math.PI * interpolatedTime;
            float degress = (float) (180.0*radians/Math.PI);

            if(interpolatedTime >= 0.5f){
                degress-=180.f;
                fromview.setVisibility(View.GONE);
                toview.setVisibility(View.VISIBLE);
            }
            if(forward){
                degress=-degress;
            }

            final Matrix matrix = t.getMatrix();
            camera.save();
            camera.rotateY(degress);
            camera.getMatrix(matrix);
            camera.restore();
            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);

        }
    }

    private int[] getImageDimensions(){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //                int height = size.y;

        Resources r = getResources();
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, IMAGE_HEIGHT, r.getDisplayMetrics());

        int[] dimen = new int[2];
        dimen[0] = width;
        dimen[1] = height;
        return dimen;
    }

    private void addImagesToSlider(){
        for(int i=0;i<imagesBitmap.size();i++){
            SliderView imageSliderView = new SliderView(ShowProductActivity.this);
            imageSliderView.setImage(imagesBitmap.get(i));
            imageSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
            mSlider.addSlider(imageSliderView);
        }
    }

    private void downloadAllImages(){
        File tempFile;
        for(int i=0;i<prod.getImageCount();i++){
            try {
                tempFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            mStorage = FirebaseStorage.getInstance().getReference("ProductImages")
                    .child(prod.getProductId())
                    .child("images/" + i + ".jpg");

            int[] dimen = getImageDimensions();
            final int width = dimen[0];
            final int height = dimen[1];

            final File tempFile2 = tempFile;
            final int finalI = i;

            int counter=0;

            mStorage.getFile(tempFile2)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bmp = null;
                            ImageView imageView = new ImageView(ShowProductActivity.this);
                            Glide.with(ShowProductActivity.this)
                                    .load(tempFile2)
                                    .into(imageView);

                            imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(ShowProductActivity.this, "Clicked on image " + finalI, Toast.LENGTH_SHORT).show();
                                }
                            });

                            SliderView sliderView = new SliderView(ShowProductActivity.this);
                            sliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                            sliderView.setImage(BitmapFactory.decodeFile(tempFile2.toString()));

                            mSlider.addSlider(sliderView);
                            Log.d(TAG, "onSuccess: got image ");
                            imagesBitmap.add(bmp);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "onFailure: Image could not downloaded" + e.toString() );
                        }
                    });
        }
    }


    private class DownloadImages extends AsyncTask<Void, Void, Void>{
        int imageCount;
        String userName;
        String productName;

        public DownloadImages(int imageCount, String userName, String productName) {
            this.imageCount = imageCount;
            this.userName = userName;
            this.productName = productName;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: Download images");
            File tempFile;
            for(int i=0;i<imageCount;i++){
                try {
                    tempFile = File.createTempFile("images", "jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }
                mStorage = FirebaseStorage.getInstance().getReference("ProductImages")
                        .child(mUser.getUid())
                        .child(prod.getProductId())
                        .child("images/" + i + ".jpg");

                int[] dimen = getImageDimensions();
                final int width = dimen[0];
                final int height = dimen[1];

                final File tempFile2 = tempFile;
                final int finalI = i;

                int counter=0;

                mStorage.getFile(tempFile2)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap bmp = null;
                                try {
                                    bmp = Glide.with(getApplicationContext())
                                            .load(tempFile2)
                                            .asBitmap()
                                            .centerCrop()
                                            .into(width, height)
                                            .get();
                                    Log.d(TAG, "onSuccess: got image ");
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }
                                imagesBitmap.add(bmp);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "onFailure: Image could not downloaded" + e.toString() );
                            }
                        });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(TAG, "onPostExecute: All Downloads Done");
            addImagesToSlider();
        }
    }

    //true: increase
    //false: decrease
    private void updateWanted(){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final ProgressDialog dialog = new ProgressDialog(ShowProductActivity.this);
        dialog.setMessage("Updating...");
        dialog.setCancelable(false);
        dialog.show();
        StringRequest request = new StringRequest(Request.Method.POST, UPDATE_WANTED_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                try {
                    Log.d("UpdatingWanged", s);
                    JSONObject jsonObject = new JSONObject(s);
                    String res = jsonObject.getString("status");
                    if (res.equals("done")) {
                        String new_wanted = jsonObject.getString("wanted");
                        productWanted.setText("Wanted: " + new_wanted);
                        if(!iWantIt){
                            iWantIt = true;
                            like_button.setImageResource(android.R.drawable.star_big_on);

                            SharedPreferences.Editor edit = mPrefs.edit();
                            edit.putString(prod.getName(), "y");
                            edit.apply();

                        } else{
                            iWantIt=false;
                            like_button.setImageResource(android.R.drawable.star_big_off);
                            SharedPreferences.Editor edit = mPrefs.edit();
                            edit.putString(prod.getName(), null);
                            edit.apply();
                        }
                        Log.e("Update Wanted", "done");
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialog.dismiss();

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "No Connection", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", prod.getProductId());
                params.put("email", user.getEmail());
                String what;
                if(!iWantIt){
                    what="inc";
                } else{
                    what="dec";
                }
                params.put("what", what);
                return params;
            }
        };

        queue.add(request);

    }

    private void bookmarkProduct(){
        final ProgressDialog dialog = new ProgressDialog(ShowProductActivity.this);
        dialog.setMessage("Updating...");
        dialog.setCancelable(false);
        dialog.show();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users").child(prod.getUserId());

        String productID = prod.getProductId();
        mRef.child("productliked").push().setValue(productID);

        dialog.dismiss();
    }

    private void unbookmarkProduct(){
        final ProgressDialog dialog = new ProgressDialog(ShowProductActivity.this);
        dialog.setMessage("Updating...");
        dialog.setCancelable(false);
        dialog.show();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference("users").child(prod.getUserId());
        mRef.child("productliked").child(prod.getProductId()).removeValue();

        dialog.dismiss();
    }

    private void loadProfilePic(){
        StorageReference mProfileRef = FirebaseStorage
                .getInstance()
                .getReference("profiles")
                .child("images/" + mUser.getUid() + "/profile.jpg");


        final File tempFile;
        try {
            tempFile = File.createTempFile("images", "jpg");


            mProfileRef.getFile(tempFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Glide.with(ShowProductActivity.this)
                                    .load(tempFile)
                                    .into(profile_pic);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Profile image could not be downloaded " + e.toString());
                            Glide.with(ShowProductActivity.this)
                                    .load(R.drawable.ic_male_avatar)
                                    .into(profile_pic);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "onCreateView: Temp file could not be created");
        }
    }


    private void whatsappMessage(){
        try {
            Intent in = new Intent(Intent.ACTION_SEND, Uri.parse("smsto:"+prod.getPhone()));
            in.setType("text/plain");
            String text = "Regarding your product " + prod.getName() + " in the app, I would like to buy it. Please reply to negotiate.";
            PackageInfo info = getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            in.setPackage("com.whatsapp");
            in.putExtra(Intent.EXTRA_TEXT, text);
            startActivity(Intent.createChooser(in, "with"));

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.e("ShowActivity", "whatsapp not installed");
        }

    }

    private void smsMessage(){
        Intent intentsms = new Intent( Intent.ACTION_VIEW, Uri.parse( "sms:" + "" ) );
        intentsms.putExtra( "sms_body", "Regarging your product " + prod.getName() + " in the app " + getResources().getString(R.string.app_name) + ", I would like to buy it, Please reply for furthur dealing" );
        intentsms.putExtra("address", "+91" + prod.getPhone());
        startActivity( intentsms );
    }

}
