package com.example.checkin.classes;

public class Place {
    String PlaceID;
    String PlaceName;
    double Lat;
    double Lng;

    public Place(String PlaceID, String PlaceName, double Lat, double Lng) {
        this.PlaceID = PlaceID;
        this.PlaceName = PlaceName;
        this.Lat = Lat;
        this.Lng = Lng;
    }

    public String getPlaceID() {
        return PlaceID;
    }

    public String getPlaceName() {
        return PlaceName;
    }

    public double getLat() {
        return Lat;
    }

    public double getLng() {
        return Lng;
    }
}
