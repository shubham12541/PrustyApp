package com.tominc.prustyapp;

import java.io.Serializable;

/**
 * Created by shubham on 17/2/16.
 */
public class User implements Serializable {
    String name, email, college, phone, year, userId;
    String[] productliked, productAdded;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCollege() {
        return college;
    }

    public void setCollege(String college) {
        this.college = college;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String[] getProductliked() {
        return productliked;
    }

    public void setProductliked(String[] productliked) {
        this.productliked = productliked;
    }

    public String[] getProductAdded() {
        return productAdded;
    }

    public void setProductAdded(String[] productAdded) {
        this.productAdded = productAdded;
    }
}