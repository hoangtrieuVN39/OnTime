package com.example.checkin.models.classes;

public class Place {
    private String placeID;
    private double latitude;
    private double longitude;
    private String name;

    public Place(String placeID, String name, double latitude, double longitude) {
        this.name = name;
         this.placeID = placeID;
         this.latitude = latitude;
         this.longitude = longitude;
    }

    public String getPlaceID() {
        return placeID;
    }
    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    public double getLongitude() {
        return longitude;
    }
    public String getPlaceName() {
        return name;
    }

    public String setPlaceName() {
        return name;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
