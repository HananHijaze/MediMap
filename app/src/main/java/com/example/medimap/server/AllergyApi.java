package com.example.medimap.server;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import java.util.List;

public interface AllergyApi {

    @GET("allergies")
    Call<List<Allergy>> getAllergies();

    @GET("allergies/{id}")
    Call<Allergy> getAllergyById(@Path("id") Long id);

    @POST("allergies")
    Call<Allergy> createAllergy(@Body Allergy allergy);

    @PUT("allergies/{id}")
    Call<Allergy> updateAllergy(@Path("id") Long id, @Body Allergy allergy);

    @DELETE("allergies/{id}")
    Call<Void> deleteAllergy(@Path("id") Long id);
}
