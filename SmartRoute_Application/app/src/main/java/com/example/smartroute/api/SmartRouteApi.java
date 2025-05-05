package com.example.smartroute.api;

import com.example.smartroute.model.Trip;
import com.example.smartroute.model.TripRequestDto;
import com.example.smartroute.model.TripStep;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface SmartRouteApi {
    @Headers("Content-Type: application/json")
    @POST("api/trips")
    Call<Trip> createTrip(@Body TripRequestDto dto);

    @GET("api/trips/code/{code}")
    Call<Trip> getTrip(@Path("code") String code);

    @GET("api/tripsteps/code/{code}")
    Call<List<TripStep>> getSteps(@Path("code") String code);

    @PUT("api/trips/code/{code}")
    Call<Trip> updateTrip(
            @Path("code") String code,
            @Body TripRequestDto dto
    );

    @DELETE("api/trips/code/{code}")
    Call<Void> deleteTrip(@Path("code") String code);
}
