package com.example.smartroute.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TripRequestDto {
    @SerializedName("locations")
    private List<String> locations;

    @SerializedName("name")
    private String name;

    // getters & setters

    public List<String> getLocations() { return locations; }
    public void setLocations(List<String> locations) { this.locations = locations; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
