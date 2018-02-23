package com.example.pratyush.uploadimageretrofit;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.view.View;

/**
 * Created by pratyush on 14-02-2018.
 */

public class NotificationHelper extends ContextWrapper {

    public static final String ID="123";
    public static final String CHANNEL_NAME="channel";

    private NotificationManager manager;
    public NotificationHelper(Context base) {
        super(base);
        createChannel();
    }

    private void createChannel() {

        NotificationChannel notificationChannel = new NotificationChannel(ID,CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(false);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationChannel.setSound(null,null);
        
        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager() {

        if(manager==null)
            manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    public Notification.Builder getChannelNotification(String title,String body,long dataSent,long imageSize)
    {


        return new Notification.Builder(getApplicationContext(),ID)
                .setContentText(body)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setAutoCancel(true)
                .setProgress(1000,(int)(dataSent/imageSize)*100,false);
    }

    public Notification.Builder getChannelNotificationUpdate(String s)
    {

        return new Notification.Builder(getApplicationContext(),ID).setContentText(s)
                .setContentTitle(s)
                .setSmallIcon(R.drawable.ic_date_range_black_24dp)
                .setAutoCancel(true);
    }

    public Notification.Builder getChannelNotificationClear()
    {

        return new Notification.Builder(getApplicationContext(),ID)
                .setTimeoutAfter(10)
                .setSmallIcon(R.drawable.ic_date_range_black_24dp)
                .setContentText("not successful");
    }

    public Notification.Builder getChannelNotificationUploadSuccessful()
    {

        return new Notification.Builder(getApplicationContext(),ID)
                .setTimeoutAfter(10)
                .setSmallIcon(R.drawable.ic_date_range_black_24dp)
                .setContentText("upload successful");
    }


}
