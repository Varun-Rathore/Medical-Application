package com.example.medicalappv3.UserDefinedClasses.Notifications;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.medicalappv3.Activities.MessageAreaActivity;
import com.example.medicalappv3.R;
import com.example.medicalappv3.SQLiteDatabases.ChatInfoDataBaseHelper;
import com.example.medicalappv3.SQLiteDatabases.UserDataBaseHelper;
import com.example.medicalappv3.UserDefinedClasses.ActivatedUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessaging extends FirebaseMessagingService {
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        setNotification(remoteMessage);
    }

    private void setNotification(RemoteMessage remoteMessage) {
        String user = remoteMessage.getData().get("user");
        String body = remoteMessage.getData().get("body");
        String title = remoteMessage.getData().get("title");
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = user;
        Intent intent = new Intent(this, MessageAreaActivity.class);
        intent.putExtra("receiver", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, user,
                                                                              NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("Sample Channel description");
            notificationChannel.enableVibration(false);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                           .setDefaults(Notification.DEFAULT_ALL)
                           .setContentIntent(pendingIntent)
                           .setWhen(System.currentTimeMillis())
                           .setSmallIcon(R.mipmap.ic_launcher_round)
                           .setContentTitle(title)
                           .setContentText(body)
                           .setContentInfo("Information");
        int id = 0;
        ChatInfoDataBaseHelper chdb = new ChatInfoDataBaseHelper(this);
        UserDataBaseHelper mydb = new UserDataBaseHelper(this);
        Cursor res = mydb.getUser();
        res.moveToFirst();
        String myid = res.getString(0);
        res = chdb.getInfo(myid);
        while (res.moveToNext()) {
            id++;
            if (res.getString(0).equals(user) || res.getString(1).equals(user)) {
                if (ActivatedUser.getActivated() != null) {
                    if (ActivatedUser.getActivated().equals(user)) {
                        return;
                    }
                }
                break;
            }
        }
        notificationManager.notify(id, notificationBuilder.build());
    }
}
