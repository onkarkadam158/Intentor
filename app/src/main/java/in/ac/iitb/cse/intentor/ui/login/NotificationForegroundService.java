package in.ac.iitb.cse.intentor.ui.login;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import in.ac.iitb.cse.intentor.R;
import in.ac.iitb.cse.intentor.dashboard.DashboardScrollingActivity;

public class NotificationForegroundService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        createNotificationMethod();
        return null;
    }

    public void createNotificationMethod() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "notif0",
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "my_channel_id")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Intentor")
                .setContentText("Your Todays usage: 00H:00M")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Add actions, if needed
        // builder.addAction(R.drawable.ic_action, "Action Name", pendingIntent);
        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        int notificationId = 1; // A unique ID for the notification
        startForeground(notificationId, notification);

        Intent intent = new Intent(this, DashboardScrollingActivity.class);

        // Create a back stack for the intent (optional)
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(LoginActivity.this);
//        stackBuilder.addNextIntentWithParentStack(intent);
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Set the pendingIntent as the notification's click action
//        builder.setContentIntent(pendingIntent);
    }
}
