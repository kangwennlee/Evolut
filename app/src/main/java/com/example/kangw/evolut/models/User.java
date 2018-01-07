package com.example.kangw.evolut.models;

import java.util.List;

/**
 * Created by Gladys Yang on 6/1/2018.
 */

public class User {
    String name;
    String email;
    double balance;
    String profilePic;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.balance = 0;
        this.profilePic = null;
    }


    protected List<User> friends;

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