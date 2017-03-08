package com.tominc.prustyapp;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;

/**

 * Created by shubham on 14/1/16.
 */
public class Product implements Serializable{
    private String name;
    private String price;
    private String wanted;
    private String email;
    private String id;
    private String description;
    private int imageCount;
    private ArrayList<Bitmap> imageBitmaps;
    private String email2;
    private String phone;
    private String year;
    private String college;

    public String getEmail2() {
        return email2;
    }

    public void setEmail2(String email2) {
        this.email2 = email2;
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

    public ArrayList<Bitmap> getImageBitmaps() {
        return imageBitmaps;
    }

    public void setImageBitmaps(ArrayList<Bitmap> imageBitmaps) {
        this.imageBitmaps = imageBitmaps;
    }

    public int getImageCount() {
        return imageCount;
    }

    public void setImageCount(int imageCount) {
        this.imageCount = imageCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private ArrayList<Integer> images = new ArrayList<>();


    public void addImage(int image){
        images.add(image);

    }

    public ArrayList<Integer> getImages(){
        return images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getWanted() {
        return wanted;
    }

    public void setWanted(String wanted) {
        this.wanted = wanted;
    }

    public void setImages(ArrayList<Integer> images) {
        this.images = images;
    }
}
