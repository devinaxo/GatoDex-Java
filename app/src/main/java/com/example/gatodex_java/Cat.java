package com.example.gatodex_java;

public class Cat {
    private String imagePath;
    private long id;
    private String nickname;
    private String type;
    private String placeMet;
    private String dateMet;

    public Cat(long id, String nickname, String type, String placeMet, String imagePath, String dateMet) {
        this.id = id;
        this.nickname = nickname;
        this.type = type;
        this.placeMet = placeMet;
        this.imagePath = imagePath;
        this.dateMet = dateMet;
    }
    public Cat(String nickname, String type, String placeMet, String imagePath, String dateMet) {
        this.nickname = nickname;
        this.type = type;
        this.placeMet = placeMet;
        this.imagePath = imagePath;
        this.dateMet = dateMet;
    }
    public String getImagePath() {
        return imagePath;
    }
    public long getId() {
        return id;
    }
    public String getNickname() {
        return nickname;
    }
    public String getType() {
        return type;
    }
    public String getPlaceMet() {
        return placeMet;
    }
    public String getDateMet(){return dateMet;}
}
