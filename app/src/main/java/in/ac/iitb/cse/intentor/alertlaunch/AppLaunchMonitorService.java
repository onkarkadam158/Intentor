package in.ac.iitb.cse.intentor.alertlaunch;

import static android.os.SystemClock.sleep;

import android.Manifest;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import in.ac.iitb.cse.intentor.R;

public class AppLaunchMonitorService extends Service {

    public UsageStatsManager usageStatsManager;
    private boolean isMonitoring = true;
    Map<String, String> targetAppPackageNames = new HashMap<>();
    public Handler handler;
    private Runnable runnable;
    public Timer timer;
    public List<UsageStats> usageStatsList;
    public long currentTime;

//          onCreate(): This method is called when the service is first created.
//        It's where you can perform one-time setup tasks for your service.
//        You might initialize resources, create threads, or set up other necessary components here.
    public void onCreate() {
        super.onCreate();
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis() - 5000, System.currentTimeMillis());
        handler = new Handler();
        timer = new Timer();

        targetAppPackageNames.put("com.instagram.android", "Instagram"); // Instagram
        targetAppPackageNames.put("com.facebook.katana", "Facebook"); // Facebook
        targetAppPackageNames.put("com.twitter.android", "Twitter"); // Twitter
        targetAppPackageNames.put("com.snapchat.android", "Snapchat"); // Snapchat
        targetAppPackageNames.put("com.linkedin.android", "LinkedIn"); // LinkedIn
        targetAppPackageNames.put("com.whatsapp", "WhatsApp"); // WhatsApp
        targetAppPackageNames.put("com.google.android.youtube", "YouTube");//Youtube

        handler = new Handler();
    }
//    onStartCommand(Intent, int, int):
//    This method is called when a client (such as an activity or another service) calls startService(Intent).
//    It's the entry point for your service's background processing logic.
//    The Intent parameter can contain data or instructions for the service.
//    You should implement your service's behavior, such as monitoring app launches, in this method.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!isMonitoring) {
            isMonitoring = true;
        }
        new Thread((new Runnable() {
            @Override
            public void run() {
                while (isMonitoring) {
                    String eventOccurred = "false";
                    Intent overlayIntent = new Intent(getApplicationContext(), OverlayService.class);
                    eventOccurred = monitorForegroundApp();

                    if (!eventOccurred.equals("false")) {

                        overlayIntent.putExtra("packageName", eventOccurred);
                        startService(overlayIntent);
                    }
//                Adding a delay to avoid excessive CPU usage
                    try {
                        Thread.sleep(500); // Check every half second (adjust interval as needed)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();
        return START_STICKY;// This  makes the service restart if it's killed.
    }


    public String monitorForegroundApp() {

//        System.out.println("Inside monitor fore app");
        if (!Context.USAGE_STATS_SERVICE.isEmpty()) {
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }
        currentTime = System.currentTimeMillis();

        usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 2000, currentTime);

        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            for (UsageStats usageStats : usageStatsList) {
                String packagename = usageStats.getPackageName();

                if (usageStats.getLastTimeUsed() >= (currentTime - 1000)) {

                    if (targetAppPackageNames.containsKey(packagename)) {

                        return packagename;
                    }
                }
            }
        }
        return "false";
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
