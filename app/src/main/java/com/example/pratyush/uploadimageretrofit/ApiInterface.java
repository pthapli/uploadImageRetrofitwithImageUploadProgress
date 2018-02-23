package com.example.pratyush.uploadimageretrofit;

import android.util.Log;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by pratyush on 26-01-2018.
 */

public interface ApiInterface {




    @FormUrlEncoded
    @POST("dbUpload")
    Call<ImageClass> uploadImage(@Field("image")String image,@Field("extension")String extension,
                                 @Field("title")String title,@Field("date")String date,@Field("venue")String venue,
                                 @Field("time")String time,@Field("description")String description);

    //@Multipart
   /* @POST("dbUpload")
    Call<ImageClass> uploadImage(@Body ImageClass imageClass);*/

/*@Part("image")String image,@Part("extension")String extension,
                                 @Part("title")String title,@Part("date")String date,@Part("venue")String venue,
                                 @Part("time")String time,@Part("description")String description*/

   /* @Multipart
    @POST("dbUpload")
    Call<ResponseBody> uploadPhoto(
            @Part("description")RequestBody description,
            @Part("title")RequestBody title,
            @Part("date")RequestBody date,
            @Part("venue")RequestBody venue,
            @Part("time")RequestBody time,
            @Part MultipartBody.Part photo
            );*/
}
