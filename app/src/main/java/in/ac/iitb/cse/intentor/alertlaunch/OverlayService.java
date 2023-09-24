package in.ac.iitb.cse.intentor.alertlaunch;


import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import in.ac.iitb.cse.intentor.R;

public class OverlayService extends Service {

    private WindowManager windowManager;
    private View overlayView,closeAppMessageView,timerView;
    String appName = "App";
    Map<String, String> targetAppPackageNames = new HashMap<>();
    public Button button1,button2,button3;

    private SharedPreferences mutedApps,mutedAppsWithTime,exitedApps,remindMelaterTimes ;

    TextView countdownTextView;

    CountDownTimer countDownTimer;
    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("inside overlay start");
        targetAppPackageNames.put("com.instagram.android", "Instagram"); // Instagram
        targetAppPackageNames.put("com.facebook.katana", "Facebook"); // Facebook
        targetAppPackageNames.put("com.twitter.android", "Twitter"); // Twitter
        targetAppPackageNames.put("com.snapchat.android", "Snapchat"); // Snapchat
        targetAppPackageNames.put("com.linkedin.android", "LinkedIn"); // LinkedIn
        targetAppPackageNames.put("com.whatsapp", "WhatsApp"); // WhatsApp
        targetAppPackageNames.put("com.google.android.youtube", "YouTube");//Youtube
        // Initialize WindowManager
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Create and configure the overlay view (inflate your custom layout here)
        overlayView = LayoutInflater.from(this).inflate(R.layout.dialogue_layout, null);
        closeAppMessageView = LayoutInflater.from(this).inflate(R.layout.close_app_prompt_layout,null);
        timerView = LayoutInflater.from(this).inflate(R.layout.timer_countdown_after_wait_button_clicked,null);

        // Set up the text view
        TextView statisticsTextView = overlayView.findViewById(R.id.statisticsTextView);
        statisticsTextView.setText("Todays usage time \n" + getTime() + getUnlockCount() + "\n");

        TextView closePromptMessage = closeAppMessageView.findViewById(R.id.message_prompt_to_close_app);
        closePromptMessage.setText("\n\nGreat to see you controlling your Usage.\n Press home or back button to exit the app.\n\n");

        countdownTextView = timerView.findViewById(R.id.timer);

        TextView timertext = timerView.findViewById(R.id.timer);
        timertext.setText("00:00");

        // Set up buttons
        setUpButtons();
        // Define layout parameters for the overlay view
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Overlay type
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,   // Make it non-focusable
                PixelFormat.TRANSLUCENT   // Allow transparency
        );
        // Add the view to the window manager
        windowManager.addView(overlayView, layoutParams);

        // creating/accessing SharedPreferences
        mutedApps = getSharedPreferences("appsWhoseInterventionMutedForTheDay", Context.MODE_PRIVATE);
        mutedAppsWithTime = getSharedPreferences("appsWhoseInterventionMutedForTheDayWithAccurateTime", Context.MODE_PRIVATE);
        exitedApps = getSharedPreferences("appsWhoseExitedWithTheirTimes",Context.MODE_PRIVATE);
        remindMelaterTimes = getSharedPreferences("remindMeAfter10MinutesClick",Context.MODE_PRIVATE);

    }

    public boolean isappMutedForTheDay(String packageName){
        String today = getTodaysDateFormatted();
        String temp = mutedApps.getString(today,"");
        if(temp.equals("")) return false;
        String[] packages = temp.split(",");// Split the input string into an array of values using ',' as the delimiter
        // Check if the packageNameToFind exists in the array of packages
        System.out.println("muted"+"\n");
        for (String packagename : packages) {
            System.out.println("muted  "+packagename+"\n");
            if (packagename.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String packageName = intent.getStringExtra("packageName");
        appName = getAppNameFromPackageName(packageName);
        button1.setText("\uD83D\uDE0A  Exit the " + appName + " Now");
        String temp = mutedApps.getAll().toString();
        System.out.println(temp+"\ninside on start of overlay MutedApp");
        String temp1=mutedAppsWithTime.getAll().toString();
        System.out.println(temp1+"\ninside on start of overlay MutedAppwith time");
        System.out.println(exitedApps.getAll().toString()+"  \nExited apps list with time");
        System.out.println(remindMelaterTimes.getAll().toString()+"  \nRemind me later");

        if(!isappMutedForTheDay(packageName)){
            System.out.println(temp+"app is not muted");
            if (isforeground(packageName)) {
                showTheOverlay(packageName);
            } else {
                closeButton1ClickedPrompt();
                closeButton2ClickedPrompt();
                hideTheOverlay();
            }
        }
        else{
            closeButton1ClickedPrompt();
            closeButton2ClickedPrompt();
            hideTheOverlay();
        }
        return START_STICKY;
    }


    private boolean isforeground(String packageName) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();

        // Query for usage statistics
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = usageStatsManager.queryEvents(currentTime - 2000, currentTime); // Query for the last 10 seconds

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND
                || event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED
                || event.getEventType() == UsageEvents.Event.USER_INTERACTION
                || event.getEventType() == UsageEvents.Event.SCREEN_INTERACTIVE) {
                String foregroundAppPackageName = event.getPackageName();

                // Check if the foreground app's package name matches the one you're interested in
                if (foregroundAppPackageName.equals(packageName)) {
                    // The target app is in the foreground and visible to the user
                    return true;
                }
            }
        }
        return false;
    }

    public void showTheOverlay(String packageName) {
        overlayView.setVisibility(View.VISIBLE);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 1
                System.out.println("Button1 clicked");
                addActionToSharedprefOfButton1(packageName);
                hideTheOverlay();
                // TODO: seems like we can't forcefully close other apps we can prompt users (after clicking this button) to exit the app by either clicking the home button
//                 or by clicking the back button
                openButton1ClickedPrompt();
                closeCurrentOpenedApp(packageName);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 2
                addActionToSharedprefOfButton2(packageName);
                hideTheOverlay();
                openButton2ClickedPrompt();
                executeCountdownTimer();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//              Action for Button 3
                addActionToSharedprefOfButton3(packageName);

                String temp = mutedApps.getString(getTodaysDateFormatted(),"");
//                temp=mutedApps.getAll().toString();
                System.out.println(temp+"  after adding to shared preferences");
                hideTheOverlay();
            }
        });
    }
    //########################################################## START OF For 1st button exit the app respect inteventions ################
    public  void closeCurrentOpenedApp(String packageName){
        // Get the ActivityManager
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // Iterate through running processes and close the target app
        for (ActivityManager.RunningAppProcessInfo processInfo : activityManager.getRunningAppProcesses()) {
            if (processInfo.processName.equals(packageName)) {
                // Force stop the app
                System.out.println("app is getting killed");
                activityManager.killBackgroundProcesses(packageName);
                break; // No need to continue searching
            }
        }
    }
    public void openButton1ClickedPrompt(){
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Overlay type
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,   // Make it non-focusable
                PixelFormat.TRANSLUCENT   // Allow transparency
        );
        windowManager.addView(closeAppMessageView,layoutParams);
        closeAppMessageView.setVisibility(View.VISIBLE);
    }
    public void closeButton1ClickedPrompt(){
        if(closeAppMessageView.getVisibility()==View.VISIBLE) {
            closeAppMessageView.setVisibility(View.GONE);
        }
    }
    public void addActionToSharedprefOfButton1(String packageName){
        String dayTime=getTodaysDateFormattedWithTime();
        SharedPreferences.Editor editor = exitedApps.edit();
        editor.putString(dayTime, packageName);
        editor.apply();
    }

    //########################################################## END of For 1st button exit the app respect inteventions ################

    //########################################################## START OF For 2nd button wait for some time  inteventions ################

    public void addActionToSharedprefOfButton2(String packageName){
        String dayTime=getTodaysDateFormattedWithTime();
        SharedPreferences.Editor editor = remindMelaterTimes.edit();
        editor.putString(dayTime, packageName);
        editor.apply();
    }
    public void executeCountdownTimer() {
        // Create a CountDownTimer for 10 minutes (10 * 60 * 1000 milliseconds)
        if(countDownTimer!=null){
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(10* 60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                long minutes = millisUntilFinished / 1000 / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                countdownTextView.setText(String.format("%02d:%02d", minutes, seconds));

            }
            @Override
            public void onFinish() {
                // Countdown timer has finished
                countdownTextView.setText("00:00");
                // You can perform actions here when the countdown complete

            }
        };

        // Start the countdown timer
        countDownTimer.start();
    }
    public void openButton2ClickedPrompt(){
        // Define layout parameters for the overlay view
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Overlay type
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,   // Make it non-focusable
                PixelFormat.TRANSLUCENT   // Allow transparency
        );
        // Add the view to the window manager
        windowManager.addView(timerView, layoutParams);
        timerView.setVisibility(View.VISIBLE);
    }
    public void closeButton2ClickedPrompt(){
        if(timerView.getVisibility()==View.VISIBLE) {
            timerView.setVisibility(View.GONE);
        }
//        if (timerView != null) {
//            windowManager.removeView(timerView);
//        }
    }
    public boolean isButton2PromptOpen(){
        return timerView.getVisibility() == timerView.VISIBLE;
    }

    //########################################################## END OF For 2nd button wait for some time  inteventions ################
    //########################################################## For 3rd button mute the inteventions for the rest of the day ################
    public void addActionToSharedprefOfButton3(String packageName){
        String formattedDate = getTodaysDateFormatted();
        if(isDatePresent(formattedDate)){
//            System.out.println("package added");
            appendpackageToTodaysDate(formattedDate,packageName);
        }
        else{
//            System.out.println("date added");
            saveDate(formattedDate,packageName);
        }
        addAccurateMuteAppToPackagename(packageName);
    }

    public boolean isDatePresent(String date) {
        String storedDateStr = mutedApps.getString(date, null);
        // Check if the storedDateStr is not null
        return storedDateStr != null;
    }
    public void saveDate(String date,String packageName) {
        SharedPreferences.Editor editor = mutedApps.edit();
        editor.putString(date, packageName);
        editor.apply();
    }
    public void addAccurateMuteAppToPackagename(String packageName){
        String dateWithTime=getTodaysDateFormattedWithTime();
        SharedPreferences.Editor editor = mutedAppsWithTime.edit();
        editor.putString(dateWithTime, packageName);
        editor.apply();
    }
    public void appendpackageToTodaysDate(String date, String packageName){
        // Retrieve the concatenated string from SharedPreferences
        String str = mutedApps.getString(date, "");
        str=str+","+packageName; //append with comma separated
        SharedPreferences.Editor editor = mutedApps.edit();
        editor.putString(date, str);
        editor.apply();
    }
//    ############################ end of For 3rd button Mute the inteventions  for the rest of the day#####################################################
    public String getTodaysDateFormatted(){
        Date currentDate = new Date();
        // Define a date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        // Format the current date as a string
        String formattedDate = dateFormat.format(currentDate);
        return  formattedDate;
    }
    public String getTodaysDateFormattedWithTime(){
        Date currentDate = new Date();
        // Define a date format
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-ss");
        // Format the current date as a string
        String formattedDate = dateFormat.format(currentDate);
        return  formattedDate;
    }
    public void hideTheOverlay(){
        if(overlayView.getVisibility()==View.VISIBLE) {
            overlayView.setVisibility(View.GONE);
        }
//        if (overlayView != null) {
//            windowManager.removeView(overlayView);
//        }
    }
    public void showTheOverlay(){
        // Define layout parameters for the overlay view
        WindowManager.LayoutParams layoutParams= new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, // Overlay type
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,   // Make it non-focusable
                PixelFormat.TRANSLUCENT   // Allow transparency
        );
        // Add the view to the window manager
        windowManager.addView(overlayView, layoutParams);
        overlayView.setVisibility(View.VISIBLE);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the overlay view when the service is destroyed
        if (overlayView != null && windowManager != null) {
            windowManager.removeView(overlayView);
        }
    }

    private String getTime() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long startTime = calendar.getTimeInMillis(); // Start of the day
        long endTime = System.currentTimeMillis(); // Current time

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
        long totalUsageTime = 0;
        for (UsageStats stat : stats) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                totalUsageTime += stat.getTotalTimeVisible();
            }
        }
        return formatDuration(totalUsageTime);
    }

    private String getUnlockCount() {
        return "";
    }

    public static String formatDuration(long milliseconds) {
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60));
        // Format the duration as "hh:mm:ss"
        String formattedTime = String.format("%02dH:%02dM:%02dS", hours, minutes, seconds);
        return formattedTime;
    }

    private String getAppNameFromPackageName(String packageName) {
        // Implemented a method to retrieve the app name based on the package name.
        // You can use PackageManager or other methods to do this.
        Map<String, String> userAppPackageMap = new HashMap<>();
        userAppPackageMap.put("com.facebook.katana", "Facebook"); // Facebook
        userAppPackageMap.put("com.instagram.android", "Instagram"); // Instagram
        userAppPackageMap.put("com.twitter.android", "Twitter"); // Twitter
        userAppPackageMap.put("com.snapchat.android", "Snapchat"); // Snapchat
        userAppPackageMap.put("com.linkedin.android", "LinkedIn"); // LinkedIn
        userAppPackageMap.put("com.whatsapp", "WhatsApp"); // WhatsApp
        userAppPackageMap.put("com.google.android.youtube", "YouTube");//Youtube

        if (userAppPackageMap.containsKey(packageName)) {
            return userAppPackageMap.get(packageName);
        }
        return "";
    }
    public void setUpButtons(){

        button1 = overlayView.findViewById(R.id.button1);
        button2 = overlayView.findViewById(R.id.button2);
        button3 = overlayView.findViewById(R.id.button3);

        button1.setText("\uD83D\uDE0A  Exit the " + appName + " Now");
        button2.setText("\uD83D\uDE10 Remind me again in 10 minutes");
        button3.setText("\uD83D\uDE22 Mute alert for the rest of the day");


        button1.setBackground(getResources().getDrawable(R.drawable.greenbutton));
        button2.setBackground(getResources().getDrawable(R.drawable.yellowbutton));
        button3.setBackground(getResources().getDrawable(R.drawable.redbutton));
    }
}
