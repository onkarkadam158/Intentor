package in.ac.iitb.cse.intentor.ui.login;

import static in.ac.iitb.cse.intentor.ui.login.LoginActivity.PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import in.ac.iitb.cse.intentor.R;
import in.ac.iitb.cse.intentor.dashboard.DashboardScrollingActivity;

public class NotificationForegroundService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "sticky_notification_channel_id";

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("inside notification foreground");
        startForeground(NOTIFICATION_ID, createNotification());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        System.out.println("inside notification foreground");
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }

    private Notification createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel (required for Android 8.0 and higher)
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Sticky Notification Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Sticky Notification")
                .setContentText("This is a sticky notification")
                .setOngoing(true); // Set the notification as ongoing (sticky)


        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it from the user.
//            ActivityCompat.requestPermissions(, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);

        }
        notificationManager.notify(NOTIFICATION_ID, notification);
        return builder.build();
    }
}
