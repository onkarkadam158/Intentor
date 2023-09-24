package in.ac.iitb.cse.intentor.dashboard;

import static java.nio.file.Paths.get;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            String packagename= event.getPackageName();
            if(userAppPackageMap1.containsKey(packagename) ){
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) { //increment the counter for that package
                    if(accessCountOfApps.containsKey(packagename)){
                        long temp = accessCountOfApps.get(packagename);
                        accessCountOfApps.put(packagename, temp+1);
                    }
                    else{
                        accessCountOfApps.put(packagename, Long.valueOf(1));
                    }
                }
            }
        }
        return accessCountOfApps;
    }
    private Map<String , Long> calculateTodaysUsageTime( ) {
        Map<String , Long> usageTimeOfApps = new HashMap<>();// return variable containing the usage time of our interested apps
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        long endTime = System.currentTimeMillis();
//        System.out.println("Current Time is "+ calendar.getTime());
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
//        System.out.println("Start of the day is "+ calendar.getTime());
        long startTime = calendar.getTimeInMillis();
        long duration=endTime-startTime;
//        System.out.println("Time in mili in  a day"+ duration);
//        startTime = endTime - (24 * 60 * 60 * 1000); // 24 hours ago
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);

        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
            if (userAppPackageMap1.containsKey(packageName) ) {
//                System.out.println(usageStats.getPackageName()+"  Time  "+usageStats.getTotalTimeInForeground());
                long todaysUsageTimeMillis = 0;
                todaysUsageTimeMillis+=usageStats.getTotalTimeInForeground();
                if(usageTimeOfApps.containsKey(packageName)){
                    todaysUsageTimeMillis+=usageTimeOfApps.get(packageName);
                    usageTimeOfApps.put(packageName,todaysUsageTimeMillis);
                }
                else {
                    usageTimeOfApps.put(packageName,todaysUsageTimeMillis);
                }
            }
        }
        return usageTimeOfApps;
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

}
