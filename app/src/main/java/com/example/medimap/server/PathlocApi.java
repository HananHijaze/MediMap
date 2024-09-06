package com.example.medimap.server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PathlocApi {

    // Get a list of all paths
    @GET("paths")
    Call<List<Pathloc>> getAllPaths();

    // Get a path by ID
    @GET("paths/{id}")
    Call<Pathloc> getPathById(@Path("id") Long id);

    // Create a new path
    @POST("paths")
    Call<Pathloc> createPath(@Body Pathloc path);

    // Update an existing path
    @PUT("paths/{id}")
    Call<Pathloc> updatePath(@Path("id") Long id, @Body Pathloc path);

    // Delete a path by ID
    @DELETE("paths/{id}")
    Call<Void> deletePath(@Path("id") Long id);
}
