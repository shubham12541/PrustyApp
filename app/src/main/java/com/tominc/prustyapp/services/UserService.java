package com.tominc.prustyapp.services;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tominc.prustyapp.User;

/**
 * Created by shubham on 06/03/17.
 */

public class UserService {

    public void addUser(String userId, User user, String password){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");


        ref.setValue(user);
    }


}
