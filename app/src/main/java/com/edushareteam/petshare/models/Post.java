package com.edushareteam.petshare.models;

public class Post {
    private String id;
    private String title;
    private String description;
    private Double quality;
    private String image1;
    private String image2;
    private String idUser;
    private String pet;
    private long timestamp;

    public Post(){

    }

    public Post(String id, String title, String description, Double quality, String image1, String image2, String idUser, String pet, long timestamp) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.quality = quality;
        this.image1 = image1;
        this.image2 = image2;
        this.idUser = idUser;
        this.pet = pet;
        this.timestamp = timestamp;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getQuality() {
        return quality;
    }

    public void setQuality(Double quality) {
        this.quality = quality;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getPet() {
        return pet;
    }

    public void setPet(String pet) {
        this.pet = pet;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
