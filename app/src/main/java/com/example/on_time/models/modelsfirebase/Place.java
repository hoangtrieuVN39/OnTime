package com.example.on_time.models.modelsfirebase;

public class Place {
    private String placeID;
    private String latitude;
    private String longitude;

//    public Place(){
//    }
    public Place(String placeID, String latitude, String longitude) {
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

    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
