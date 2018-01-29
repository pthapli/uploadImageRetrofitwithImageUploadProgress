package com.example.pratyush.uploadimageretrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pratyush on 26-01-2018.
 */

public class ApiClient {

    private static final String BaseUrl = "http://192.168.59.50:3030/";//enter server url here
    private static Retrofit retrofit;

    public static Retrofit getApiClient()
    {

        if(retrofit == null)
        {

            retrofit = new Retrofit.Builder().baseUrl(BaseUrl).addConverterFactory(GsonConverterFactory.create()).build();

        }
        return retrofit;
    }
}
