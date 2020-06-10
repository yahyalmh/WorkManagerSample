package com.example.workmanagersample.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.workmanagersample.R;

public class NotificationCenter {
    private static int NOTIFICATION_ID = 123;

    public static void sendNotification(Context context, int taskType) {
        String CHANNEL_ID = "notify";
        String CHANNEL_NAME = "workmanager-reminder";

        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(R.color.colorPrimary);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Workmanager sample")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification);
        if (taskType == 1){
           builder.setContentText("One time task") ;
        }else{
            builder.setContentText("Periodic Task");
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    public static void finishNotification(Context context){
        android.app.NotificationManager notificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
