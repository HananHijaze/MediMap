package com.example.medimap.server;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface HydrationApi {

    @GET("hydration/{id}")
    Call<Hydration> getHydrationById(@Path("id") Long id);

    @POST("hydration")
    Call<Hydration> createHydration(@Body Hydration hydration);

    @PUT("hydration/{id}")
    Call<Hydration> updateHydration(@Path("id") Long id, @Body Hydration hydration);

    @GET("hydration")
    Call<List<Hydration>> getHydrationsByCustomerId(@Query("customerId") Long customerId);

    @DELETE("hydration/{id}")
    Call<Void> deleteHydration(@Path("id") Long id);

    @GET("hydration/last7days/{customerId}")
    Call<List<Hydration>> getLast7DaysHydration(@Path("customerId") Long customerId);

}
