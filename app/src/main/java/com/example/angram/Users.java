package com.example.angram;

import java.util.ArrayList;

public class Users {
    private String name;
    private String email;
    private String profileImage;
    private String uid;
    private ArrayList<Post> posts;
    private String followers;
    private String following;

    public Users() {

    }

    public Users(String name, String email, String image, String uid, ArrayList<Post> posts, String followers, String following) {
        this.name = name;
        this.email = email;
        this.profileImage = image;
        this.uid = uid;
        this.posts = posts;
        this.followers = followers;
        this.following = following;
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

    public String getImage() { return profileImage; }

    public void setImage(String profileImage) {
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

    public String getFollowers() { return followers; }

    public void setFollowers(String followers) { this.followers = followers; }

    public String getFollowing() { return following; }

    public void setFollowing(String following) { this.following = following; }
}
