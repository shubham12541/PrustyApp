package com.tominc.prustyapp.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.tominc.prustyapp.ShowProductActivity;

import de.mateware.snacky.Snacky;

/**
 * Created by shubham on 02/04/17.
 */

public class DownloadFirebaseImage{
    Context c;
    private final String TAG = "DownloadFirebaseImage";

    public DownloadFirebaseImage(Context c){
        this.c = c;
    }

    public void download(StorageReference mRef, final ImageView imageView, final String failMessage, final DownloadMethods downloadMethods){
        mRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: Image download link generated");
                        Glide.with(c)
                                .load(uri)
                                .listener(new RequestListener<Uri, GlideDrawable>() {
                                    @Override
                                    public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                                        Log.d(TAG, "onException: Image downloaded");
                                        Snacky.builder()
                                                .setActivty((Activity) c)
                                                .setDuration(Snacky.LENGTH_SHORT)
                                                .setText(failMessage)
                                                .warning().show();
                                        downloadMethods.successMethod();
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                        Log.d(TAG, "onResourceReady: Image could not downloaded");
                                        downloadMethods.failMethod();
                                        return false;
                                    }
                                })
                                .into(imageView);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Image could be downloaded");
                        downloadMethods.failMethod();
                    }
                });
    }
}

