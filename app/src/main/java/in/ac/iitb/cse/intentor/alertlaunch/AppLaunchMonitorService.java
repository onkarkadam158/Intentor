package in.ac.iitb.cse.intentor.alertlaunch;

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

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import in.ac.iitb.cse.intentor.R;

public class AppLaunchMonitorService extends Service {

    public UsageStatsManager usageStatsManager;
    public String targetAppPackageName = "com.google.android.youtube";
    public Handler handler;
    public Timer timer;
    public List<UsageStats> usageStatsList;
    public long currentTime;
    //    @Override
    public void onCreate() {
        super.onCreate();
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, System.currentTimeMillis()-1000,System.currentTimeMillis());
        handler = new Handler();
        timer = new Timer();

        Intent serviceIntent = new Intent(getApplicationContext(), AppLaunchMonitorService.class);
        startService(serviceIntent);
        onStartCommand(serviceIntent, 0, 0);
//        startAppLaunchMonitoring();
    }

    private boolean isMonitoring = true;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("inside start command");
//                while(true) {
                    // Event-based monitoring logic
                    System.out.println("Inside thread");
                    boolean eventOccurred = false;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                        eventOccurred = monitorForegroundApp();
                    }

//                    showCustomAlertDialog();
                    // Update the UI or show alerts on the main thread
                    if (eventOccurred) {
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showCustomAlertDialog();
                            }
                        });
                    }

//                }
        return Service.START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public boolean monitorForegroundApp() {

        System.out.println("Inside monitor fore app");
        if(!Context.USAGE_STATS_SERVICE.isEmpty()) {
            usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        }

        currentTime = System.currentTimeMillis();

        usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 1000, currentTime);
        System.out.println("Inside monitor fore app 2");
        if (usageStatsList != null && !usageStatsList.isEmpty()) {
            for (UsageStats usageStats : usageStatsList) {
                if (usageStats.getLastTimeUsed() >= (currentTime-1000)) {
                    if(usageStats.getPackageName()==targetAppPackageName){
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public void showCustomAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);

        // Set up the text view
        TextView statisticsTextView = dialogView.findViewById(R.id.statisticsTextView);
        statisticsTextView.setText("Displaying statistics here\n\n");

        // Set up buttons
        Button button1 = dialogView.findViewById(R.id.button1);
        Button button2 = dialogView.findViewById(R.id.button2);
        Button button3 = dialogView.findViewById(R.id.button3);

        button1.setText("\uD83D\uDE0A  Exit the Instagram Now");
        button2.setText("\uD83D\uDE10 Remind me again in 10 minutes");
        button3.setText("\uD83D\uDE22 Mute alert for the rest of the day");

        button1.setBackgroundColor(getResources().getColor(R.color.green));
        button2.setBackgroundColor(getResources().getColor(R.color.yellow));
        button3.setBackgroundColor(getResources().getColor(R.color.red));

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 1
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 2
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 3
            }
        });

        builder.setView(dialogView)
                .setTitle("Alert");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
