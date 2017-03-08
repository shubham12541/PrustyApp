package com.tominc.prustyapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;

/**
 * Created by shubham on 7/2/16.
 */
public class SliderView extends BaseSliderView {
    int image;
    Context c;
    Bitmap bitmap;
    int CHOOSE;

    protected SliderView(Context context) {
        super(context);
        this.c = context;
    }

    public void setImage(int image){
        this.image = image;
        CHOOSE=1;
    }


    public void setImage(Bitmap bitmap){
        CHOOSE=2;
        this.bitmap = bitmap;
    }


    @Override
    public View getView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.slider_item, (ViewGroup)null);

        ImageView imageId = (ImageView) v.findViewById(R.id.slider_item_image);

        if(CHOOSE==1){
            imageId.setImageResource(image);
        } else if(CHOOSE==2){
            imageId.setImageBitmap(bitmap);
        }


        return v;
    }

}
