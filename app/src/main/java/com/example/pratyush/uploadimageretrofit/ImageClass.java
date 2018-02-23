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

   /*private String extension;
   private String time;
   private String date;
   private String venue;
   private String description;
   private String title;

    public ImageClass(String extension, String time, String date, String venue, String description, String title) {
        this.extension = extension;
        this.time = time;
        this.date = date;
        this.venue = venue;
        this.description = description;
        this.title = title;
    }*/
}
