package com.example.kangw.evolut.models;

/**
 * Created by kangw on 6/1/2018.
 */

public class Post {
    public String title;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String title){
        this.title=title;
    }

}
