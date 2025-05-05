package com.example.smartroute.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Trip {
    @SerializedName("id")
    private int id;

    @SerializedName("code")
    private String code;

    @SerializedName("name")
    private String name;

    @SerializedName("originalQuery")
    private String originalQuery;

    @SerializedName("originalQueryKey")
    private String originalQueryKey;

    @SerializedName("overviewPolyline")
    private String overviewPolyline;

    @SerializedName("steps")
    private List<TripStep> steps;

    // getters & setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getOriginalQuery() { return originalQuery; }
    public void setOriginalQuery(String originalQuery) { this.originalQuery = originalQuery; }

    public String getOriginalQueryKey() { return originalQueryKey; }
    public void setOriginalQueryKey(String originalQueryKey) { this.originalQueryKey = originalQueryKey; }

    public String getOverviewPolyline() { return overviewPolyline; }
    public void setOverviewPolyline(String overviewPolyline) { this.overviewPolyline = overviewPolyline; }

    public List<TripStep> getSteps() { return steps; }
    public void setSteps(List<TripStep> steps) { this.steps = steps; }
}
