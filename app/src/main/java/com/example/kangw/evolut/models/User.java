package com.example.kangw.evolut.models;

import java.util.List;

/**
 * Created by Gladys Yang on 6/1/2018.
 */

public class User {
    private String name;
    private String email;
    private double balance;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
        this.balance = 0;
    }


    private List<User> friends;
    
}