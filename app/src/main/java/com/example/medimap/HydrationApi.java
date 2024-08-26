package com.example.medimap;


import java.time.LocalDate;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface HydrationApi {

    // Get a list of all hydration records
    @GET("api/hydrations")
    Call<List<Hydration>> getAllHydrations();

    // Get hydration records for a specific customer by email
    @GET("api/hydrations/customer/{email}")
    Call<List<Hydration>> getHydrationsByCustomerEmail(@Path("email") String email);

    // Get hydration record by ID
    @GET("api/hydrations/{id}")
    Call<Hydration> getHydrationById(@Path("id") Long id);

    // Create a new hydration record
    @POST("api/hydrations")
    Call<Hydration> createHydration(@Body Hydration hydration);

    // Update an existing hydration record
    @PUT("api/hydrations/{id}")
    Call<Hydration> updateHydration(@Path("id") Long id, @Body Hydration hydration);

    // Delete a hydration record by ID
    @DELETE("api/hydrations/{id}")
    Call<Void> deleteHydration(@Path("id") Long id);
}
