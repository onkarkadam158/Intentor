package in.ac.iitb.cse.intentor.dashboard;

import static java.nio.file.Paths.get;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

public class AppUsageStatistics {

    private final Context context;
    private final UsageStatsManager usageStatsManager;
    public static Map<String, Boolean> userAppPackageMap1 = new HashMap<>();

    static {
        // Initialize the map with your app-package-to-boolean associations for apps you are interested in
        userAppPackageMap1.put("com.facebook.katana", true);      // Facebook
        userAppPackageMap1.put("com.instagram.android", true);    // Instagram
        userAppPackageMap1.put("com.twitter.android", true);      // Twitter
        userAppPackageMap1.put("com.snapchat.android", true);     // Snapchat
        userAppPackageMap1.put("com.linkedin.android", true);     // LinkedIn
        userAppPackageMap1.put("com.whatsapp", true);             // WhatsApp
        userAppPackageMap1.put("com.google.android.youtube", true); // Youtube
    }


    public AppUsageStatistics(Context context) {
        this.context = context;
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

    }

    public List<AppUsageInfo> getUsageStatistics() {
        List<AppUsageInfo> appUsageInfoList = new ArrayList<>(); //return variable containing app and their usage, visit counts appname packagename
//        Calendar calendar = Calendar.getInstance();
//        long endTime = calendar.getTimeInMillis();
//
//        // Set the start time to a very early date (e.g., Jan 1, 2010) to get the all time data
//        calendar.set(Calendar.YEAR, 2010);
//        calendar.set(Calendar.MONTH, Calendar.JANUARY);
//        calendar.set(Calendar.DAY_OF_MONTH, 1);
//        long startTime = calendar.getTimeInMillis();

//        Map<String, Boolean> userAppPackageMap = new HashMap<>();
//        userAppPackageMap.put("com.facebook.katana",true); // Facebook
//        userAppPackageMap.put("com.instagram.android",true); // Instagram
//        userAppPackageMap.put("com.twitter.android",true); // Twitter
//        userAppPackageMap.put("com.snapchat.android",true); // Snapchat
//        userAppPackageMap.put("com.linkedin.android",true); // LinkedIn
//        userAppPackageMap.put("com.whatsapp",true); // WhatsApp
//        userAppPackageMap.put("com.google.android.youtube",true);//Youtube

        Map<String , Long> accessCountOfApps = new HashMap<>();
        accessCountOfApps = CountVisitCountsOfToday();

        Map<String , Long> usageTimeOfApps = new HashMap<>();
        usageTimeOfApps = calculateTodaysUsageTime();

        long phoneUnlockCounts=0,phoneUsageTime=0;
        phoneUnlockCounts = calculatePhoneUnlockCountsOfToday();
        phoneUsageTime = calculatePhoneUsageOfToday();
        for (Map.Entry<String, Boolean> entry : userAppPackageMap1.entrySet()) {
            String packageName = entry.getKey();

            AppUsageInfo appUsageInfo = new AppUsageInfo();

            appUsageInfo.setPackageName(packageName);
            appUsageInfo.setAppName(getAppNameFromPackageName(packageName));
            if(usageTimeOfApps.containsKey(packageName)){
                long time = usageTimeOfApps.get(packageName);
                appUsageInfo.setTotalUsageTimeOfToday(time);
            }
            if(accessCountOfApps.containsKey(packageName)){
                long count = accessCountOfApps.get(packageName);
                appUsageInfo.setTotalVisitCountsOfToday(count);
            }
            appUsageInfo.setTotalPhoneUnlockCountOfToday(phoneUnlockCounts);
            appUsageInfo.setTotalPhoneUsageTimeOfToday(phoneUsageTime);
            appUsageInfoList.add(appUsageInfo);
        }

//        if (usageStatsList != null) {
//            for (UsageStats usageStats : usageStatsList) {
//                AppUsageInfo appUsageInfo = new AppUsageInfo();
//                //getting and setting attribute of class AppUsageInfo
//                boolean isUserApp = false ;
//                if(userAppPackageMap.containsKey(usageStats.getPackageName().toString())){
//                    isUserApp = Boolean.TRUE.equals(userAppPackageMap.get(usageStats.getPackageName().toString()));
//                }
//                if(isUserApp){
//                    appUsageInfo.setPackageName(usageStats.getPackageName().toString());
//                    appUsageInfo.setAppName(getAppNameFromPackageName(usageStats.getPackageName().toString()));
//                    //setting usage time for today as well as for total since the beginning
//                    appUsageInfo.setTotalUsageTime(usageStats.getTotalTimeInForeground());
//                    appUsageInfo.setTotalUsageTimeOfToday(calculateTodaysUsageTime( usageStats.getPackageName().toString())); ;
//                    //setting the access or visit counts of the app
//                    appUsageInfo.setTotalVisitCountsOfToday(CountVisitCountsOfToday(usageStats.getPackageName().toString()));
//                    userAppPackageMap.put(usageStats.getPackageName().toString(),false);
//                    appUsageInfoList.add(appUsageInfo);
//                }
//            }
//        }
        return appUsageInfoList;
    }
    public static String formatMillisecondsToTime(long milliseconds) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        Date date = new Date(milliseconds);
        return sdf.format(date);
    }
    private Map<String , Long> CountVisitCountsOfToday(){
        Map<String , Long> accessCountOfApps = new HashMap<>(); //return map of access counts of our interested apps
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        long currentTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        long startTime = calendar.getTimeInMillis();
//        long currentTime = System.currentTimeMillis();
//        long startTime = currentTime - (24 * 60 * 60 * 1000); // 24 hours ago

        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, currentTime);
        usageStatsManager.queryAndAggregateUsageStats(startTime,currentTime);
        String lastForegroundApp = null;
//        System.out.println("Printing all the usage stats events\n\n");
        long whatsappCount=0,instaCount=0,linkedinCount=0,snapchatCount=0,facebookCount=0,youtubeCount=0,twitterCount=0;
        while(usageEvents.hasNextEvent()){
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            if(event.getEventType()==UsageEvents.Event.MOVE_TO_FOREGROUND ){
                if (event.getPackageName().equals("com.instagram.android") && event.getClassName().toString().equals("com.instagram.mainactivity.LauncherActivity")) {
                    instaCount++;
                }
                if(event.getPackageName().equals("com.whatsapp") && event.getClassName().toString().equals("com.whatsapp.Main")){
//                    System.out.println("Package: " + event.getPackageName() + " Event type: " + event.getEventType() + " -- " + event.getClassName() );
                    whatsappCount++;
                }
                if(event.getPackageName().equals("com.facebook.katana") && event.getClassName().toString().equals("com.facebook.katana.activity.FbMainTabActivity") ){
                    //System.out.println("Package: " + event.getPackageName() + " Event type: " + event.getEventType() + " -- " + event.getClassName() );
                    facebookCount++;
                }
                if(event.getPackageName().equals("com.twitter.android") && event.getClassName().toString().equals("com.twitter.app.main.MainActivity")){
                    twitterCount++;
                }

                if(event.getPackageName().equals("com.snapchat.android") && event.getClassName().toString().equals("com.snap.mushroom.MainActivity")){
                    snapchatCount++;
                }

                if(event.getPackageName().equals("com.linkedin.android") && event.getClassName().toString().equals("com.linkedin.android.authenticator.LaunchActivity")){
//                    System.out.println("Package: " + event.getPackageName() + " Event type: " + event.getEventType() + " -- " + event.getClassName()+" time "+formatMillisecondsToTime(event.getTimeStamp()) );
                    linkedinCount++;
                }

                if(event.getPackageName().equals("com.google.android.youtube") && event.getClassName().toString().equals("com.google.android.apps.youtube.app.watchwhile.WatchWhileActivity")){
//                    System.out.println("Package: " + event.getPackageName() + " Event type: " + event.getEventType() + " -- " + event.getClassName()+" time "+formatMillisecondsToTime(event.getTimeStamp())  );
                    youtubeCount++;
                }
            }
        }
//        System.out.println("\n\nwhatsapp: "+whatsappCount);
        accessCountOfApps.put("com.whatsapp",whatsappCount);
        accessCountOfApps.put("com.instagram.android",instaCount);
        accessCountOfApps.put("com.facebook.katana",facebookCount);
        accessCountOfApps.put("com.twitter.android",twitterCount);
        accessCountOfApps.put("com.snapchat.android",snapchatCount);
        accessCountOfApps.put("com.linkedin.android",linkedinCount);
        accessCountOfApps.put("com.google.android.youtube",youtubeCount);

// Commenting out this code just in case i need similar in future
//        while (usageEvents.hasNextEvent()) {
//            UsageEvents.Event event = new UsageEvents.Event();
//            usageEvents.getNextEvent(event);
//            String packagename= event.getPackageName();
//            if(userAppPackageMap1.containsKey(packagename) ){
//                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) { //increment the counter for that package
//                    String currentForegroundApp = event.getPackageName();
//                    if (!TextUtils.isEmpty(currentForegroundApp) && currentForegroundApp.equals(packagename)) {
//                        // Check if the current foreground app is the target app
//                        if (lastForegroundApp == null || !lastForegroundApp.equals(currentForegroundApp)) {
//                            // This is the first event for the target app in the current session
//                            if (accessCountOfApps.containsKey(packagename)) {
//                                long temp = accessCountOfApps.get(packagename);
//                                accessCountOfApps.put(packagename, temp + 1);
////                              System.out.println(packagename+temp+1+" time== "+formatDuration(event.getTimeStamp()));
//                            } else {
//                                accessCountOfApps.put(packagename, Long.valueOf(1));
//                            }
//                            lastForegroundApp = currentForegroundApp;
//                        }
//                    }
//                }
//            }
//        }
        return accessCountOfApps;
    }
    private Map<String , Long> calculateTodaysUsageTime( ) {
        Map<String , Long> usageTimeOfApps = new HashMap<>();// return variable containing the usage time of our interested apps
       Map<String, Map<String, String>> usageSessionsWithTimestampOfApps = new HashMap<>(); //{app:{time:duration}}-->>{"Instagram":{"8am": "5M2S", "10am":"3M2S"}, "Whatsapp":{"3am": "3M", "11am":"4M40S"}... }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        long endTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        long startTime = calendar.getTimeInMillis();
        usageTimeOfApps.put("com.whatsapp",0L);
        usageTimeOfApps.put("com.instagram.android",0L);
        usageTimeOfApps.put("com.facebook.katana",0L);
        usageTimeOfApps.put("com.twitter.android",0L);
        usageTimeOfApps.put("com.snapchat.android",0L);
        usageTimeOfApps.put("com.linkedin.android",0L);
        usageTimeOfApps.put("com.google.android.youtube",0L);

        Map<String , Long> prev = new HashMap<>();
        prev.put("com.whatsapp", -1L);
        prev.put("com.instagram.android",  -1L);
        prev.put("com.facebook.katana", -1L);
        prev.put("com.twitter.android",  -1L);
        prev.put("com.snapchat.android",  -1L);
        prev.put("com.linkedin.android",  -1L);
        prev.put("com.google.android.youtube",  -1L);

        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
        usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            String currPackageName=event.getPackageName();
            if (usageTimeOfApps.containsKey(currPackageName)) {
                if (event.getEventType() == 1) {
                    prev.put(currPackageName, event.getTimeStamp());
                } else if (event.getEventType() == 2 && prev.containsKey(currPackageName) && prev.get(currPackageName) != -1) {
                    long sessionStartTime = prev.get(currPackageName);
                    long sessionEndTime = event.getTimeStamp();
                    Long time = usageTimeOfApps.get(currPackageName) + sessionEndTime-sessionStartTime;
                    usageTimeOfApps.put(currPackageName, time);
                    //this checks is there is an app package name already in usageSessionsWithTimestampOfApps if not adds a hashmap (if not done so, throws object error)
                    if(!usageSessionsWithTimestampOfApps.containsKey(currPackageName)){
                        usageSessionsWithTimestampOfApps.put(currPackageName,new HashMap<>());
                    }
                    // Retrieve the inner map for the app
                    Map<String,String>innerMap=usageSessionsWithTimestampOfApps.get(currPackageName);
                    // Check if the inner map contains the timestamp key
                    if(!innerMap.containsKey(currPackageName)){
                        // Add the new timestamp to the inner map
                        innerMap.put(formatMillisecondsToTime(sessionStartTime),formatDuration(sessionEndTime-sessionStartTime));
                    }
//                    usageSessionsWithTimestampOfApps.get(currPackageName).put(formatMillisecondsToTime(sessionStartTime), formatDuration(sessionEndTime-sessionStartTime));
                }
            }
        }
//        for (Map.Entry<String, Long> entry : usageTimeOfApps.entrySet()) {
//            UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
//            usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);
//            long calcTime = 0, prev = -1;
//            String packageName1 = entry.getKey() ;
//            while (usageEvents.hasNextEvent()) {
//                UsageEvents.Event event = new UsageEvents.Event();
//                usageEvents.getNextEvent(event);
//                if (event.getPackageName().equals(packageName1)) {
//                    if (event.getEventType() == 1) {
////                System.out.println(packageName);
////                System.out.println(event.getClassName());
////                System.out.println("Etype: "+ event.getEventType()+" Time "+ formatMillisecondsToTime(event.getTimeStamp()));
////                usageTimeOfApps.put(packageName,usageStats.getTotalTimeInForeground());
//                        prev = event.getTimeStamp();
//                    } else if (event.getEventType() == 2 && prev != -1) {
//                        calcTime += (event.getTimeStamp() - prev);
//                    }
//                }
//            }
//            System.out.println(packageName1+"  Calc time  "+ formatDuration(calcTime) +"   "+ calcTime);
//            usageTimeOfApps.put(packageName1,calcTime);
//        }

        // This code gives wrong results. aActually it calculates all the time that the package has been to foreground background. Calculates extra time because it overlaps.
//        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime);
//        for (UsageStats usageStats : usageStatsList) {
//            String packageName = usageStats.getPackageName();
//            long timefr=usageStats.getTotalTimeInForeground();
//            long timevis=usageStats.getTotalTimeVisible();
//            long timeforeserv=usageStats.getTotalTimeForegroundServiceUsed();
//            if(packageName.equals("com.google.android.youtube")){
//                System.out.println("Last "+formatMillisecondsToTime(usageStats.getLastTimeUsed())+" timeFR "+ formatDuration(timefr) + "--"+timefr);
//            }
//            if(packageName.equals("com.whatsapp")){
//                System.out.println("Last "+formatMillisecondsToTime(usageStats.getLastTimeUsed())+" timeFR "+ formatDuration(timefr) + "--"+timefr);
//            }
//            if (userAppPackageMap1.containsKey(packageName) ) {
//                System.out.println(usageStats.getPackageName()+" Time: "+ formatDuration(timefr));
//                System.out.println(usageStats.getPackageName()+" TimeVisible: "+ formatDuration(timevis));
//                System.out.println(usageStats.getPackageName()+" TimeForeService: "+ formatDuration(timeforeserv));
////                usageTimeOfApps.put(packageName,timefr);
//            }
//        }
        // Iterate over the nested Map usageSessionsWithTimestampOfApps map
//        System.out.println("Printing the usage sessions");
//        for (Map.Entry<String, Map<String, String>> entry : usageSessionsWithTimestampOfApps.entrySet()) {
//            String appName = entry.getKey();
//            Map<String, String> timestamps = entry.getValue();
//            System.out.println("App Name: " + appName+" \n");
//            // Iterate over the inner Map
//            for (Map.Entry<String, String> timestampEntry : timestamps.entrySet()) {
//                String timestampKey = timestampEntry.getKey();
//                String timestampValue = timestampEntry.getValue();
//                System.out.println("Timestamp Key: " + timestampKey + ", Timestamp Value: " + timestampValue+" \n");
//            }
//        }
//        OUTPUT: Printing the usage sessions
//        App Name: com.google.android.youtube
//        Timestamp Key: 10:33:38, Timestamp Value: 00H:00M:12S
//        Timestamp Key: 10:48:18, Timestamp Value: 00H:00M:20S
//        App Name: com.whatsapp
//        Timestamp Key: 09:43:39, Timestamp Value: 00H:00M:02S
//        Timestamp Key: 09:43:41, Timestamp Value: 00H:00M:12S

        return usageTimeOfApps;
    }

    public long calculatePhoneUnlockCountsOfToday(){
        long phoneUnlockCount=0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        long endTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        long startTime = calendar.getTimeInMillis();

        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.KEYGUARD_SHOWN) {
                    phoneUnlockCount++;
            }
        }
        System.out.println("Phone Unlock Count"+phoneUnlockCount);
        return phoneUnlockCount;
    }
    public long calculatePhoneUsageOfToday(){
        long phoneUsageTimeOfToday=0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        long endTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        long startTime = calendar.getTimeInMillis();
        long prev=-1;
        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
        usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);
//        Map<String, Boolean> userAppsPackages = getUserAppsPackages(context.getApplicationContext());
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            String currPackageName=event.getPackageName();
//            currPackageName.equals("com.android.launcher") userAppsPackages.containsKey(currPackageName)
            if (!currPackageName.equals("com.android.launcher")) {
//                System.out.println("Package: "+currPackageName+" Event; "+ event.getEventType()+ " Class: "+ event.getClassName() + " time "+ formatMillisecondsToTime(event.getTimeStamp()));
                if (event.getEventType() == 1) {
                    prev = event.getTimeStamp();
                }
                else if (event.getEventType() == 2 && prev != -1) {
                    phoneUsageTimeOfToday = phoneUsageTimeOfToday + (event.getTimeStamp() - prev);
                }
            }
        }
        System.out.println("Phone usage time"+formatDuration(phoneUsageTimeOfToday) + "  "+formatMillisecondsToTime(phoneUsageTimeOfToday));
        return phoneUsageTimeOfToday;
    }
    public static Map<String, Boolean> getUserAppsPackages(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Map<String, Boolean> userAppsMap = new HashMap<>();

        List<ApplicationInfo> installedPackages = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : installedPackages) {
            // Check if the package is not a system app
            if (((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 && (packageInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) ==0 )) {
                userAppsMap.put(packageInfo.packageName, true);
            }
        }
        return userAppsMap;
    }
    private String getAppNameFromPackageName(String packageName) {
        // Implemented a method to retrieve the app name based on the package name.
        // You can use PackageManager or other methods to do this.
        Map<String, String> userAppPackageMap = new HashMap<>();
        userAppPackageMap.put("com.facebook.katana","Facebook"); // Facebook
        userAppPackageMap.put("com.instagram.android","Instagram"); // Instagram
        userAppPackageMap.put("com.twitter.android","Twitter"); // Twitter
        userAppPackageMap.put("com.snapchat.android","Snapchat"); // Snapchat
        userAppPackageMap.put("com.linkedin.android","LinkedIn"); // LinkedIn
        userAppPackageMap.put("com.whatsapp","WhatsApp"); // WhatsApp
        userAppPackageMap.put("com.google.android.youtube","YouTube");//Youtube

        if(userAppPackageMap.containsKey(packageName)) {
            return userAppPackageMap.get(packageName);
        }
        return "";
    }
    public static String formatDuration(long milliseconds) {
//        milliseconds = System.currentTimeMillis()-milliseconds;
        long seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / (1000 * 60)) % 60;
        long hours = (milliseconds / (1000 * 60 * 60));
        // Format the duration as "hh:mm:ss"
        String formattedTime = String.format("%02dH:%02dM:%02dS", hours, minutes, seconds);
        return formattedTime;
    }

}
