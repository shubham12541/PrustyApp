package com.tominc.prustyapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by shubham on 21/3/16.
 */
public class ProductRecyclerViewAdapter extends RecyclerView.Adapter<ProductViewHolders> {

    Context c;
    ArrayList<Product> items;
    private final String TAG = "AllProductsAdapter";

    private StorageReference mStorage;

    private final String IMAGE_DOWNLOAD_BASE = Config.BASE_URL + "uploads/";

    public ProductRecyclerViewAdapter(Context c, ArrayList<Product> items){
        this.c = c;
        this.items = items;
        mStorage = FirebaseStorage.getInstance().getReference("ProductImages");
    }

    @Override
    public ProductViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, null);
        ProductViewHolders rcv = new ProductViewHolders(layoutView, items);
        return rcv;
    }

    @Override
    public void onBindViewHolder(final ProductViewHolders holder, int position)  {
        holder.name.setText(items.get(position).getName());
        holder.price.setText(items.get(position).getPrice());
        final File localFile;
        try{
            localFile = File.createTempFile("images", "jpg");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "onBindViewHolder: Cannot Create Temp file " + e.toString());
            return;
        }

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference("ProductImages")
                .child(mUser.getUid())
                .child(items.get(position).getProductId())
                .child("images/" + position + ".jpg");

        mStorage.getFile(localFile)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        Glide.with(c)
                                .load(localFile)
                                .into(holder.image);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: File could not be downloaded " + e.toString());
                    }
                });

    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }
}
