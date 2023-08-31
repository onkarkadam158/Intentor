package in.ac.iitb.cse.intentor.dashboard;
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
import java.util.Random;

public class AppUsageStatistics {

    private Context context;
    private UsageStatsManager usageStatsManager;



    public AppUsageStatistics(Context context) {
        this.context = context;
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

    }

    public List<AppUsageInfo> getUsageStatistics() {
        List<AppUsageInfo> appUsageInfoList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();

        // Set the start time to a very early date (e.g., Jan 1, 2010) to get the all time data
        calendar.set(Calendar.YEAR, 2010);
        calendar.set(Calendar.MONTH, Calendar.JANUARY);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        long startTime = calendar.getTimeInMillis();

        Map<String, Boolean> userAppPackageMap = new HashMap<>();
        userAppPackageMap.put("com.facebook.katana",true); // Facebook
        userAppPackageMap.put("com.instagram.android",true); // Instagram
        userAppPackageMap.put("com.twitter.android",true); // Twitter
        userAppPackageMap.put("com.snapchat.android",true); // Snapchat
        userAppPackageMap.put("com.linkedin.android",true); // LinkedIn
        userAppPackageMap.put("com.whatsapp",true); // WhatsApp

        // Query app usage statistics
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, startTime, endTime);

        if (usageStatsList != null) {
            for (UsageStats usageStats : usageStatsList) {
                AppUsageInfo appUsageInfo = new AppUsageInfo();
                //getting and setting attribute of class AppUsageInfo
                boolean isUserApp = false ;
                if(userAppPackageMap.containsKey(usageStats.getPackageName().toString())){
                    isUserApp = Boolean.TRUE.equals(userAppPackageMap.get(usageStats.getPackageName().toString()));
                }
                if(isUserApp){
                    appUsageInfo.setPackageName(usageStats.getPackageName().toString());
                    appUsageInfo.setAppName(getAppNameFromPackageName(usageStats.getPackageName().toString()));
                    //setting usage time for today as well as for total since the beginning
                    appUsageInfo.setTotalUsageTime(usageStats.getTotalTimeInForeground());
                    appUsageInfo.setTotalUsageTimeOfToday(calculateTodaysUsageTime( usageStats.getPackageName().toString())); ;
                    //setting the access or visit counts of the app
                    appUsageInfo.setTotalVisitCountsOfToday(CountVisitCountsOfToday(usageStats.getPackageName().toString()));
                    appUsageInfoList.add(appUsageInfo);
                }
            }
        }
        return appUsageInfoList;
    }
    private long CountVisitCountsOfToday(String PackageName){
        long count=0;
//        Random random = new Random();
//        count = random.nextInt(100);
        Calendar calendar = Calendar.getInstance();
        long endTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        long startTime = calendar.getTimeInMillis();
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
        Map<String, Integer> appAccessCounts = new HashMap<>();

        for (UsageStats usageStats : usageStatsList) {
            String packageName = usageStats.getPackageName();
            if(packageName.equals(PackageName)){// Increment access count for the app
                if (appAccessCounts.containsKey(packageName)) {
                    int accessCount = appAccessCounts.get(packageName);
                    appAccessCounts.put(packageName, accessCount + 1);
                } else {
                    appAccessCounts.put(packageName, 1);
                }
            }
        }

        // Now you have a map of app package names and their corresponding access counts
        for (Map.Entry<String, Integer> entry : appAccessCounts.entrySet()) {
            String packageName = entry.getKey();
            int accessCount = entry.getValue();

            // Process and store the access count for each app
            // Print for demonstration
            System.out.println("App Package Name: " + packageName + ", Access Count: " + accessCount);
        }
        if(appAccessCounts.containsKey(PackageName)) {
            return appAccessCounts.get(PackageName);
        }
        return -1;
    }
    private long calculateTodaysUsageTime( String packageName) {
        Calendar calendar = Calendar.getInstance();
        long endTime = System.currentTimeMillis();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND,0);
        long startTime = calendar.getTimeInMillis();
//        long startTime = currentTime - (24 * 60 * 60 * 1000); // 24 hours ago
        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, startTime, endTime);
        long todaysUsageTimeMillis = 0;
        for (UsageStats usageStats : usageStatsList) {
            if (usageStats.getPackageName().equals(packageName)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    todaysUsageTimeMillis += usageStats.getTotalTimeVisible();
                }
                else {
                    todaysUsageTimeMillis+=usageStats.getTotalTimeInForeground();
                }
            }
        }
        return todaysUsageTimeMillis;
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

        if(userAppPackageMap.containsKey(packageName)) {
            return userAppPackageMap.get(packageName);
        }
        return "";
    }
}
