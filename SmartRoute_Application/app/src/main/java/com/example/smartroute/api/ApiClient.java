package com.example.smartroute.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static SmartRouteApi INSTANCE;

    public static SmartRouteApi get() {
        if (INSTANCE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    //Previous Azure Implementation
                    //.baseUrl("https://smartrouteapiservice.azurewebsites.net/")
                    .baseUrl("https://smartroute-i92g.onrender.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            INSTANCE = retrofit.create(SmartRouteApi.class);
        }
        return INSTANCE;
    }
}
