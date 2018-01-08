package com.example.kangw.evolut.models;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gladys Yang on 6/1/2018.
 */

public class User {
    String uid;
    String name;
    String email;
    double balance;
    String profilePic;
    User user;

    public User(String uid, String name, String email, String profilePic) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.balance = 0;
        this.profilePic = profilePic;
        //this.users.add(new User(uid, name, email, profilePic));
    }

    public User() {

    }

    public User getUserByUId(final String uid){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        Query query = mDatabase.orderByChild(uid);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot userSnapshot: dataSnapshot.getChildren()){
                    String userName = userSnapshot.child("Name").getValue().toString();
                    String userEmail = userSnapshot.child("Email").getValue().toString();
                    String userProfilePic = userSnapshot.child("ProfilePic").getValue().toString();

                    user = new User(uid, userName, userEmail, userProfilePic);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return user;
    }

    public void setProfilePic(String profilePic){
        this.profilePic  = profilePic;

    }
    public void setName(String name){
        this.name = name;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setBalance(double balance){
        this.balance = balance;
    }

    public String getProfilePic(){
        return this.profilePic;
    }
    public String getName(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
    }
    public double getBalance(){
        return this.balance;
    }
}