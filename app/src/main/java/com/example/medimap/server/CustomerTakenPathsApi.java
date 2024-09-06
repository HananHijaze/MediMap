package com.example.medimap.server;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CustomerTakenPathsApi {

    // Get a list of all customer taken paths
    @GET("customer-taken-paths")
    Call<List<CustomerTakenPaths>> getAllCustomerTakenPaths();

    // Get a customer taken path by ID
    @GET("customer-taken-paths/{id}")
    Call<CustomerTakenPaths> getCustomerTakenPathById(@Path("id") Long id);

    // Create a new customer taken path
    @POST("customer-taken-paths")
    Call<CustomerTakenPaths> createCustomerTakenPath(@Body CustomerTakenPaths customerTakenPaths);

    // Update an existing customer taken path
    @PUT("customer-taken-paths/{id}")
    Call<CustomerTakenPaths> updateCustomerTakenPath(@Path("id") Long id, @Body CustomerTakenPaths customerTakenPaths);

    // Delete a customer taken path by ID
    @DELETE("customer-taken-paths/{id}")
    Call<Void> deleteCustomerTakenPath(@Path("id") Long id);
}
