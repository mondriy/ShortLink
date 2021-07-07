package com.example.shortlink.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ShortLinkAPI {
    @POST("/generate")
    public Call<Get> postData(@Body Post data);
}
