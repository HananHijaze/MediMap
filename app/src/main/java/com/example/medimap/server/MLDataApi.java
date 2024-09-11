package com.example.medimap.server;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MLDataApi {

    @POST("/api/mldata/add")
    Call<Void> addMLData(@Body MLData mlData);

    @DELETE("/api/mldata/delete/{id}")
    Call<Void> deleteMLData(@Path("id") Long id);
}
