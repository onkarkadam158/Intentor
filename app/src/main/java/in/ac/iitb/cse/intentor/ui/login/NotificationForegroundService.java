package in.ac.iitb.cse.intentor.ui.login;

import static in.ac.iitb.cse.intentor.ui.login.LoginActivity.PERMISSION_REQUEST_CODE;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Pair;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;

import in.ac.iitb.cse.intentor.R;
import in.ac.iitb.cse.intentor.alertlaunch.AppLaunchMonitorService;
import in.ac.iitb.cse.intentor.alertlaunch.OverlayService;
import in.ac.iitb.cse.intentor.dashboard.AppUsageInfo;
import in.ac.iitb.cse.intentor.dashboard.AppUsageStatistics;
import in.ac.iitb.cse.intentor.dashboard.DashboardScrollingActivity;

public class NotificationForegroundService extends Service {

    private static final String CHANNEL_ID = "MyChannelID";
    private static final int NOTIFICATION_ID = 1;
    public UsageStatsManager usageStatsManager;
    private boolean isMonitoring = true;
    Map<String, String> targetAppPackageNames = new HashMap<>();
    public Handler handler;
    // Declare a Handler for UI thread operations
    private Handler uiHandler = new Handler();

    private boolean isUpdateThreadRunning = true;

    // Declare a Handler and a Runnable for periodic updates

    private Handler updateHandler = new Handler();
    private Runnable updateRunnable;
    public Timer timer;
    public List<UsageStats> usageStatsList;
    public long currentTime;

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("inside notification foreground");
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

        System.out.println("printing the usage time + count here");

        AppUsageStatistics appUsageStatistics = new AppUsageStatistics(this);
        String titleOFNotification = formatDuration(appUsageStatistics.calculatePhoneUsageOfToday());
        long textOfNotification = appUsageStatistics.calculatePhoneUnlockCountsOfToday();
        createAndShowNotification(titleOFNotification, textOfNotification);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

////        createNotificationChannel();
//        System.out.println("Inside notification service");
//        // Build the notification
////        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
////                .setContentTitle("Foreground Service Example")
////                .setContentText("Running...")
////                .setSmallIcon(R.drawable.ic_notification)
////                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
////                .build();
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_notification)
//                .setContentTitle("Intentor")
//                .setContentText("Todays Usage:00H00M\nTodays Unlock: 00")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        // Show the notification with a unique ID
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//        // notificationId is a unique int for each notification that you must define.
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
////            ActivityCompat.requestPermissions(NotificationForegroundService, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
//        }
//        notificationManager.notify(NOTIFICATION_ID, builder.build());
//
//        // Start the service in the foreground with the notification
//        startForeground(NOTIFICATION_ID, builder.build());
        System.out.println("inside notification onstart Command");
        // Perform your long-running task here
//        Intent serviceIntent = new Intent(getApplicationContext(), AppLaunchMonitorService.class);
//        startService(serviceIntent);

        if (!isMonitoring) {
            isMonitoring = true;
        }
        new Thread((new Runnable() {
            @Override
            public void run() {
                while (isMonitoring) {
                    String eventOccurred = "false";
                    Intent overlayIntent = new Intent(getApplicationContext(), OverlayService.class);
//                    eventOccurred = monitorForegroundApp();
                    Pair<String,String> result = isForeground();
//                    if (!eventOccurred.equals("")) {
                    overlayIntent.putExtra("packageName", result.first);
                    overlayIntent.putExtra("BackOrFore",result.second);
                    startService(overlayIntent);
//                    }
//                    String packageName=isForeground();
//                    if(!packageName.equals("")){
//                        System.out.println("Foreground: "+packageName);
//                        overlayIntent.putExtra("packageName", packageName);
//                        startService(overlayIntent);
//                    }
//                    String bgpackage=isBackGround();
//                    if(!bgpackage.equals("No_package")){
//                        System.out.println("background: "+bgpackage);
//                        stopService(overlayIntent);
//                    }
//                Adding a delay to avoid excessive CPU usage
                    try {
                        Thread.sleep(250); // Check every half/2 second (adjust interval as needed)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        })).start();

        new Thread((new Runnable() {
            @Override
            public void run() {
                while (isUpdateThreadRunning) {
                    // Update the notification content in the background
                    updateNotification();

                    // Schedule the next update after a delay (e.g., every 10 seconds)
                    try {
                        Thread.sleep(30000); // 30 seconds
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    updateNotification();
                    // Post an update to the UI thread to reflect changes in the notification
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Update the notification on the UI thread

                        }
                    });
                }
            }
        })).start();


        return START_STICKY;
    }

    private String isBackGround() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();

        // Query for usage statistics
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = usageStatsManager.queryEvents(currentTime - 499, currentTime); // Query for the last 1 second

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
                // The app switched to the background
                if(targetAppPackageNames.containsKey(event.getPackageName())){
                    return event.getPackageName();
                }
            }
        }
        return "No_package";
    }

    private Pair<String,String> isForeground() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        String resultPackageName="",status="0";
        // Query for usage statistics
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = usageStatsManager.queryEvents(currentTime - 249, currentTime); // Query for the last 1 seconds

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if (targetAppPackageNames.containsKey(event.getPackageName())){
                System.out.println("Package: "+event.getPackageName()+" event: "+event.getEventType()+" time: "+formatMillisecondsToTime(event.getTimeStamp())+" Timestamp: "+ event.getTimeStamp() + " Class: "+event.getClassName());
            }
//            opened app
//            Package: com.whatsapp event: 1 time: 08:57:14 Timestamp: 1698722834459 Class: com.whatsapp.Main
//
//            went to chat
//            Package: com.whatsapp event: 2 time: 08:57:28 Timestamp: 1698722848117 Class: com.whatsapp.HomeActivity
//            Package: com.whatsapp event: 1 time: 08:57:28 Timestamp: 1698722848126 Class: com.whatsapp.Conversation

//            Back to homepage
//            Package: com.whatsapp event: 2 time: 08:57:31 Timestamp: 1698722851562 Class: com.whatsapp.Conversation
//            Package: com.whatsapp event: 1 time: 08:57:31 Timestamp: 1698722851565 Class: com.whatsapp.HomeActivity

//            exit the app
//            Package: com.whatsapp event: 2 time: 08:58:00 Timestamp: 1698722880861 Class: com.whatsapp.HomeActivity
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
                    || event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED
//                    || event.getEventType() == UsageEvents.Event.USER_INTERACTION
                    || event.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE) {
                String foregroundAppPackageName = event.getPackageName();
                // Check if the foreground app's package name matches the one you're interested in
                if (targetAppPackageNames.containsKey(foregroundAppPackageName) ){
                    // The target app is in the foreground and visible to the user
                    resultPackageName=foregroundAppPackageName;
                    status="1";
                }
            }
            if (event.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED || event.getEventType()==UsageEvents.Event.MOVE_TO_BACKGROUND) {
                String foregroundAppPackageName = event.getPackageName();
                // Check if the foreground app's package name matches the one you're interested in
                if (targetAppPackageNames.containsKey(foregroundAppPackageName) ){
                    // The target app is in the foreground and visible to the user
                    resultPackageName=foregroundAppPackageName;
                    status="2";
                }
            }
        }
        Pair<String,String> res=Pair.create(resultPackageName,status);
        return res;
    }
    public String monitorForegroundApp() {

//        System.out.println("Inside monitor fore app");
        if (!Context.USAGE_STATS_SERVICE.isEmpty()) {
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }
        currentTime = System.currentTimeMillis();

        usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 499, currentTime);

        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            for (UsageStats usageStats : usageStatsList) {
                if (usageStats.getLastTimeUsed() >= (currentTime - 499)) {
                    String packagename = usageStats.getPackageName();
                    System.out.println("Packagename : "+ usageStats.getPackageName()+" Time: "+ formatMillisecondsToTime(usageStats.getLastTimeUsed()) + " Last: "+usageStats.getLastTimeUsed());
//                    System.out.println("package: "+usageStats.getPackageName());
                    if (targetAppPackageNames.containsKey(packagename)) {
                        return packagename;
                    }
                }
            }
        }
        return "false";
    }

    public void createAndShowNotification(String titleOFNotification, long textOfNotification) {
        // Create a notification channel (required for Android 8.0 and higher)
        createNotificationChannel();

        // Intent to delete the notification when the user clicks the button
        Intent deleteIntent = new Intent(this, NotificationDeleteReceiver.class);
        deleteIntent.setAction("DELETE_NOTIFICATION");
        deleteIntent.putExtra("NOTIFICATION_ID",NOTIFICATION_ID);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                deleteIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        // Build the notification with the delete action button
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Usage Time: " + titleOFNotification)
                .setContentText("Unlock Count: " + textOfNotification)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setOngoing(true) // Make the notification ongoing (sticky)
                .addAction(R.drawable.ic_delete, "Delete", deletePendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Show the notification with a unique ID
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
        // Schedule periodic updates
//        schedulePeriodicUpdates();
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void schedulePeriodicUpdates() {
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                // Update the notification content
                updateNotification();

                // Schedule the next update after a delay (e.g., every 10 seconds)
                updateHandler.postDelayed(this, 10000);
            }
        };

        // Start periodic updates immediately
        updateHandler.post(updateRunnable);
    }

    private void updateNotification() {

        // Create or update your notification content here
        // You can modify the content using builder
        // For example, update the unlock count
        AppUsageStatistics appUsageStatistics = new AppUsageStatistics(this);
        String titleOFNotification = formatDuration(appUsageStatistics.calculatePhoneUsageOfToday());
        long textOfNotification = appUsageStatistics.calculatePhoneUnlockCountsOfToday();

        // Intent to delete the notification when the user clicks the button
        Intent deleteIntent = new Intent(this, NotificationDeleteReceiver.class);
        deleteIntent.setAction("DELETE_NOTIFICATION");
        deleteIntent.putExtra("NOTIFICATION_ID",NOTIFICATION_ID);
        PendingIntent deletePendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                deleteIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Usage Time: " + titleOFNotification)
                .setContentText("Unlock Count: " + textOfNotification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOnlyAlertOnce(true)
                .setOngoing(true) // Make the notification ongoing (sticky)
                .addAction(R.drawable.ic_delete, "Delete", deletePendingIntent);

        // Update the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
    public void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Notification Channel";
            String description = "Channel for my notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public static String formatDuration(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60));
        // Format the duration as "hh:mm:ss"

        String formattedTime = String.format("%02d Minutes", minutes);
        if(hours!=0){
            formattedTime = String.format("%02d Hour %02d Minutes", hours, minutes);
        }
        return formattedTime;
    }
    public static String formatMillisecondsToTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date date = new Date(milliseconds);
        return sdf.format(date);
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
