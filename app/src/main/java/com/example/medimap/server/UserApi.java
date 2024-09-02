package com.example.medimap.server;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserApi {

    // Get a list of all users
    @GET("api/users")
    Call<List<User>> getAllUsers();

    // Get a user by ID
    @GET("api/users/{id}")
    Call<User> getUserById(@Path("id") Long id);

    // Get a user by eMail
    @GET("api/users/{email}")
    static Call<User> getUserByEmail(@Path("email") String email) {
        return null;
    }

    // Create a new user
    @POST("api/users")
    Call<User> createUser(@Body User user);

    // Update an existing user
    @PUT("api/users/{id}")
    Call<User> updateUser(@Path("id") Long id, @Body User user);

    // Delete a user by ID
    @DELETE("api/users/{id}")
    Call<Void> deleteUser(@Path("id") Long id);
}
