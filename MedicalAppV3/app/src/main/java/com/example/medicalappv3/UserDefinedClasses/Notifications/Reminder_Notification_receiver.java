package com.example.medicalappv3.UserDefinedClasses.Notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.medicalappv3.R;

import java.util.Calendar;

public class Reminder_Notification_receiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        String medname = bundle.getString("medname");
        boolean[] days = bundle.getBooleanArray("days");
        int position = bundle.getInt("position");
        long timestamp = bundle.getLong("timeinmilli");
        timestamp = Math.abs(timestamp - Calendar.getInstance().getTimeInMillis());
        position = position + 1;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(Integer.toString(position), "Reminder " + position, NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("Medicine reminders");
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, Integer.toString(position));
        notificationBuilder.setAutoCancel(true)
                           .setDefaults(Notification.DEFAULT_ALL)
                           .setWhen(System.currentTimeMillis())
                           .setSmallIcon(R.mipmap.ic_launcher_round)
                           .setContentTitle("Medicine Reminder")
                           .setContentText(medname)
                           .setContentInfo("Information");
        for (int i = 0; i < 7; i++) {
            if (i + 1 == Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
                if (days[i]) {
                    if (timestamp <= 480000) { notificationManager.notify(position, notificationBuilder.build()); }
                    break;
                }
            }
        }
    }
}
