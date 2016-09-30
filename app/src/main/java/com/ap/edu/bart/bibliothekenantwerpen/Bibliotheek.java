package com.ap.edu.bart.bibliothekenantwerpen;

/**
 * Created by bart on 30/09/16.
 */
public class Bibliotheek {
    private String lat;
    private String lng;

    public Bibliotheek(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public String getlng() {
        return lng;
    }

    public String getlat() {
        return lat;
    }
}

