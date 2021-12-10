package com.edushareteam.petshare.models;

public class User {
    private String id;
    private String email;
    private String username;
    private String city;
    private String bio;
    private String image;
    private long timestamp;

    public User(){

    }

    public User(String id, String email, String username, String city, String bio, String image, long timestamp) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.city = city;
        this.bio = bio;
        this.image = image;
        this.timestamp = timestamp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
