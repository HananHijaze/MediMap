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

    // Get a user by ID
    @GET("users/{id}")
    Call<User> getUserById(@Path("id") Long id);


    // Get a user by email
    @GET("users/email")  // Updated to avoid conflict with getUserById
    Call<User> findByEmail(@Query("email") String email);


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
