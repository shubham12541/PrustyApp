package com.tominc.prustyapp;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**

 * Created by shubham on 14/1/16.
 */
public class Product implements Serializable{
    private String name, phone, year, college, productId, description, userId;
    private int imageCount, price;
//    private String name;
//    private String price;
//    private String wanted;
//    private String email;
//    private String id;
//    private String description;
//    private int imageCount;
//    private ArrayList<Bitmap> imageBitmaps;
//    private String email2;
//    private String phone;
//    private String year;
//    private String college;


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }
}
