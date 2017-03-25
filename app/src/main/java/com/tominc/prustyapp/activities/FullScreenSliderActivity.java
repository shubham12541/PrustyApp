package com.tominc.prustyapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.tominc.prustyapp.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class FullScreenSliderActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener {

    private SliderLayout mSlider;
    private ArrayList<String> imagePaths;
    private String prodId;
    SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_slider);

        mSlider = (SliderLayout) findViewById(R.id.slider);

        Intent in = getIntent();
        prodId = in.getStringExtra("prodId");

        mPrefs = getSharedPreferences(prodId, MODE_PRIVATE);

        int imageCount = mPrefs.getInt("imageCount", 0);

        for(int i=0;i<imageCount;i++){
            String filePath = mPrefs.getString("image"+i, null);
            if(filePath!=null){
                addImage(filePath, i);
            }
        }

        mSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000);

        mSlider.setPresetTransformer("Stack");

    }

    private void addImage(String filePath, int count){
        File file = new File(filePath);

        TextSliderView textSliderView = new TextSliderView(FullScreenSliderActivity.this);
        textSliderView.description((count+1) + "")
                .image(file)
                .setScaleType(BaseSliderView.ScaleType.FitCenterCrop)
                .setOnSliderClickListener(FullScreenSliderActivity.this);

        textSliderView.bundle(new Bundle());
        textSliderView.getBundle().putString("extra", "image" + count);
        mSlider.addSlider(textSliderView);
    }

    @Override
    protected void onStop() {
        mSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        //TODO: show/hide toolbar
        Toast.makeText(FullScreenSliderActivity.this, slider.getBundle().get("extra") + "", Toast.LENGTH_SHORT).show();
    }
}
