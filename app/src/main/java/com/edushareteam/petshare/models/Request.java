package com.edushareteam.petshare.models;

public class Request {

    private String id;
    private String title;
    private String bio;
    private String imageProfile;
    private String idUser;
    private long timestamp;

    public Request(String id, String title, String bio, String imageProfile, String idUser, long timestamp) {
        this.id = id;
        this.title = title;
        this.bio = bio;
        this.imageProfile = imageProfile;
        this.idUser = idUser;
        this.timestamp = timestamp;
    }

    public Request() {

    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
