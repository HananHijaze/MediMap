package com.example.medimap.server;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.List;

public interface UsersAllergiesApi {

    @GET("usersallergies")
    Call<List<UsersAllergies>> getAllUsersAllergies();

    @GET("usersallergies/{id}")
    Call<UsersAllergies> getUsersAllergiesById(@Path("id") Long id);

    @POST("usersallergies")
    Call<UsersAllergies> createUsersAllergies(@Body UsersAllergies usersAllergies);

    @PUT("usersallergies/{id}")
    Call<UsersAllergies> updateUsersAllergies(@Path("id") Long id, @Body UsersAllergies usersAllergies);

    @DELETE("usersallergies/{id}")
    Call<Void> deleteUsersAllergies(@Path("id") Long id);
}
