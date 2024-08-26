package com.example.medimap;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface PathApi {

    // Get a list of all paths
    @GET("api/paths")
    Call<List<Path>> getAllPaths();

    // Get a path by ID
    @GET("api/paths/{id}")
    Call<Path> getPathById(@Path("id") Long id);

    // Create a new path
    @POST("api/paths")
    Call<Path> createPath(@Body Path path);

    // Update an existing path
    @PUT("api/paths/{id}")
    Call<Path> updatePath(@Path("id") Long id, @Body Path path);

    // Delete a path by ID
    @DELETE("api/paths/{id}")
    Call<Void> deletePath(@Path("id") Long id);
}

