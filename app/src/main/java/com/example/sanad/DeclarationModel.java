package com.example.sanad;

public class DeclarationModel {
    private String id;
    private String username;
    private String address;
    private String phone;
    private String decText;
    private double latitude;
    private double longitude;

    public DeclarationModel() { }

    public DeclarationModel(String id, String username, String address, String phone, String decText, double latitude, double longitude) {
        this.id = id;
        this.username = username;
        this.address = address;
        this.phone = phone;
        this.decText = decText;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getDecText() {
        return decText;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
