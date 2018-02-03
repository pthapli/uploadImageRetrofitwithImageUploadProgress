package com.example.pratyush.uploadimageretrofit;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pratyush on 26-01-2018.
 */

public class ImageClass {


    @SerializedName("image")
    private String Image;

    @SerializedName("response")
    private String Response;

    @SerializedName("extension")
    private String Extension;

    @SerializedName("time")
    private String Time;

    @SerializedName("venue")
    private String Venue;

    @SerializedName("date")
    private String Date;

    @SerializedName("description")
    private String Description;

    @SerializedName("title")
    private String Title;

    public String getResponse() {

        //return Response;
        return Response;
    }


}
