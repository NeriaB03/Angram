package com.example.angram;

import java.util.ArrayList;
import java.util.Date;

public class Post {
    private String uid;
    private String name;
    private String profileImage;
    private String image;
    private String description;
    private String likes;
    private String publishDate;

    public Post() {

    }

    public Post(String uid, String name, String profileImage, String image, String description, String likes, String publishDate) {
        this.uid = uid;
        this.name = name;
        this.profileImage = profileImage;
        this.image = image;
        this.description = description;
        this.likes = likes;
        this.publishDate = publishDate;
    }

    public String getUid() { return this.uid; }

    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return this.name; }

    public void setName(String name) { this.name = name; }

    public String getProfileImage() { return this.profileImage; }

    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public String getImage() { return this.image; }

    public void setImage(String image) { this.image = image; }

    public String getDescription() { return this.description; }

    public void setDescription(String description) { this.description = description; }

    public String getLikes() { return this.likes; }

    public void setLikes(String likes) { this.likes = likes; }

    public String getPublishDate() { return this.publishDate; }

    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
}
