package com.example.smartroute.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static SmartRouteApi INSTANCE;

    public static SmartRouteApi get() {
        if (INSTANCE == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://smartrouteapiservice.azurewebsites.net/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            INSTANCE = retrofit.create(SmartRouteApi.class);
        }
        return INSTANCE;
    }
}
