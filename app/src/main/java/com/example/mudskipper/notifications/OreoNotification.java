package com.example.mudskipper.notifications;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;


public class OreoNotification extends ContextWrapper {

    private static final String CHANNEL_ID = "com.example.myapplication";
    private static final String CHANNEL_NAME = "myapplication";
    private NotificationManager notificationManager;


    public OreoNotification(Context base) {
        super(base);

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannel();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel(){

        NotificationChannel channel  = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(false);
        channel.enableVibration(true);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
    }

    public NotificationManager getNotificationManager(){
        if (notificationManager==null){
            //notificationManager = (NotificationManager)getSystemService();
        }
        return notificationManager;
    }
}
