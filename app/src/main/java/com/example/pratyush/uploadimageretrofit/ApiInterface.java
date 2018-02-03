package com.example.pratyush.uploadimageretrofit;

import android.util.Log;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;

/**
 * Created by pratyush on 26-01-2018.
 */

public interface ApiInterface {




    @FormUrlEncoded
    @POST("dbUpload")
    Call<ImageClass> uploadImage(@Field("image")String image,@Field("extension")String extension,
                                 @Field("title")String title,@Field("date")String date,@Field("venue")String venue,
                                 @Field("time")String time,@Field("description")String description);


}
