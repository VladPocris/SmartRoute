package com.example.smartroute.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TripStep implements Serializable {
    @SerializedName("from")
    private String from;
    @SerializedName("to")
    private String to;
    @SerializedName("distanceKm")
    private double distanceKm;
    @SerializedName("durationMinutes")
    private int durationMinutes;
    @SerializedName("order")
    private int order;
    @SerializedName("startLat")
    private double startLat;
    @SerializedName("startLng")
    private double startLng;
    @SerializedName("endLat")
    private double endLat;
    @SerializedName("endLng")
    private double endLng;
    @SerializedName("encodedPolyline")
    private String encodedPolyline;

    public String getFrom() {
        return from;
    }
    public String getTo() {
        return to;
    }
    public double getDistanceKm() {
        return distanceKm;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public int getOrder() {
        return order;
    }
    public double getStartLat() {
        return startLat;
    }
    public double getStartLng() {
        return startLng;
    }
    public double getEndLat() {
        return endLat;
    }
    public double getEndLng() {
        return endLng;
    }
    public String getEncodedPolyline() {
        return encodedPolyline;
    }
}
