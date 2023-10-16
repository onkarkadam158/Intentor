package in.ac.iitb.cse.intentor.ui.login;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationManagerCompat;

public class NotificationDeleteReceiver extends BroadcastReceiver {

    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("DELETE_NOTIFICATION".equals(intent.getAction())) {
            int notificationId = intent.getIntExtra("NOTIFICATION_ID", -1);
            if (notificationId != -1) {
                // Delete the notification with the specified ID
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                notificationManager.cancel(notificationId);
            }
        }
    }
}