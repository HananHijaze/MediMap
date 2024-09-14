package com.example.medimap.server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    // Get a list of all users
    @GET("users")
    Call<List<User>> getAllUsers();

    // Get user by ID using @Path
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    // Get a user by email using @Path
    @GET("users/email")
    Call<User> findByEmail(@Query("email") String email);

    @GET("users/ping")  // This points to the ping endpoint on your server
    Call<Void> pingServer();
    // Create a new user
    @POST("users")
    Call<User> createUser(@Body User user);

    // Update an existing user
    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User user);

    // Delete a user by ID
    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);
}
