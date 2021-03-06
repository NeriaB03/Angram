package com.example.angram;

import java.util.ArrayList;

public class Users {
    private String name;
    private String email;
    private String profileImage;
    private String uid;
    private ArrayList<Post> posts;

    public Users() {

    }

    public Users(String name, String email, String profileImage, String uid, ArrayList<Post> posts) {
        this.name = name;
        this.email = email;
        this.profileImage = profileImage;
        this.uid = uid;
        this.posts = posts;
    }

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

    public String getProfileImage() { return profileImage; }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<Post> getPosts() { return posts; }

    public void setPosts(ArrayList<Post> posts) { this.posts = posts; }
}
