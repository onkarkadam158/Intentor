package in.ac.iitb.cse.intentor.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import in.ac.iitb.cse.intentor.alertlaunch.AppLaunchMonitorService;

    public class UnlockReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(Intent.ACTION_USER_PRESENT) ||
                        intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
                    // Start your overlay service here
                    Intent serviceIntent = new Intent(context.getApplicationContext(), AppLaunchMonitorService.class);
                    context.startService(serviceIntent);
                }
            }
        }
    }

