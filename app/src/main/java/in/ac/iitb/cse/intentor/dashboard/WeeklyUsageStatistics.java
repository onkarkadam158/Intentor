package in.ac.iitb.cse.intentor.dashboard;

import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WeeklyUsageStatistics {

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
    public WeeklyUsageStatistics(Context context) {
        this.context = context;
        usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
    }

    public List<WeeklyUsageInfo> getWeeklyUsageInfoList(){
        List<WeeklyUsageInfo> weeklyUsageInfoList = new ArrayList<>();
        List<Long[]> dateRanges=getTheStartAndEndTimesOfLastSevenDays();
        // Sort the dateRanges list by start time
        Collections.sort(dateRanges, new Comparator<Long[]>() {
            @Override
            public int compare(Long[] range1, Long[] range2) {
                return Long.compare(range1[0], range2[0]);
            }
        });

        for (Long[] dateRange : dateRanges) {
            long startTimeMillis = dateRange[0];
            long endTimeMillis = dateRange[1];
            WeeklyUsageInfo weeklyUsageInfo = new WeeklyUsageInfo();
            weeklyUsageInfo.setDay(getDayFromStartTime(startTimeMillis));
            weeklyUsageInfo.setDate(getDateFromStartTime(startTimeMillis));
            weeklyUsageInfo.setPhoneUsageTimeinMilli(getphoneUsageTimeInMillis(startTimeMillis,endTimeMillis));
            weeklyUsageInfo.setPhoneUnlockCount(getPhoneUnlockCounts(startTimeMillis,endTimeMillis));

            System.out.println("Day: "+ weeklyUsageInfo.getDay()+ " Date: "+ weeklyUsageInfo.getDate()+" Unlock: "+ weeklyUsageInfo.getPhoneUnlockCount() + " Usage: "+ weeklyUsageInfo.getPhoneUsageTimeinMilli()+"\n");
//            This is how the output looks like
//            Day: Tuesday Date: 2023-10-10 Unlock: 109 Usage: 11653751
//            Day: Wednesday Date: 2023-10-11 Unlock: 74 Usage: 19448589
//            Day: Thursday Date: 2023-10-12 Unlock: 69 Usage: 10813632
//            Day: Friday Date: 2023-10-13 Unlock: 58 Usage: 19646484
//            Day: Saturday Date: 2023-10-14 Unlock: 75 Usage: 13084177
//            Day: Sunday Date: 2023-10-15 Unlock: 109 Usage: 14583227
//            Day: Monday Date: 2023-10-16 Unlock: 115 Usage: 5999850
            weeklyUsageInfoList.add(weeklyUsageInfo);
        }
        return weeklyUsageInfoList;
    }
    public String getDayFromStartTime(long startTimeMillis){
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        Date startDate = new Date(startTimeMillis);
        String dayOfWeek = dayFormat.format(startDate);
//        System.out.println("Day of the Week: " + dayOfWeek);
        return dayOfWeek;
    }
    public String getDateFromStartTime(long startTimeMillis){
        SimpleDateFormat dateFormatOnly = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Date startDate = new Date(startTimeMillis);
        String dateOnly = dateFormatOnly.format(startDate);
//        System.out.println("Date: " +dateOnly);
        return dateOnly;
    }

    public long getPhoneUnlockCounts(long startTime,long endTime){
        long phoneUnlockCount=0;
        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.KEYGUARD_SHOWN) {
                phoneUnlockCount++;
            }
        }
//        System.out.println("Phone Unlock Count"+phoneUnlockCount);
        return phoneUnlockCount;
    }
    public long getphoneUsageTimeInMillis(long startTime,long endTime){
        long phoneUsageTimeOfToday=0;
        long prev=-1;
        UsageEvents usageEvents = usageStatsManager.queryEvents(startTime, endTime);
        usageStatsManager.queryAndAggregateUsageStats(startTime, endTime);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            String currPackageName=event.getPackageName();
            if (!currPackageName.equals("com.android.launcher")) {
                if (event.getEventType() == 1) {
                    prev = event.getTimeStamp();
                }
                else if (event.getEventType() == 2 && prev != -1) {
                    phoneUsageTimeOfToday = phoneUsageTimeOfToday + (event.getTimeStamp() - prev);
                }
            }
        }
        return phoneUsageTimeOfToday;
    }
    public List<Long[]>  getTheStartAndEndTimesOfLastSevenDays(){

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Long[]> dateRanges = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            // Calculate the start time (beginning of the day)
            calendar.setTime(new Date()); // Reset to the current date
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            Date startTime = calendar.getTime();
            long startTimeMillis = startTime.getTime();

            // Calculate the end time (end of the day)
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            Date endTime = calendar.getTime();
            long endTimeMillis = endTime.getTime();

            // Add the date range to the list
            dateRanges.add(new Long[]{startTimeMillis, endTimeMillis});
        }

        // Print the date ranges in milliseconds
//        for (Long[] dateRange : dateRanges) {
//            System.out.println("Start Time (Millis): " + dateRange[0] + ", End Time (Millis): " + dateRange[1]);
//            System.out.println("Start " + dateFormat.format(dateRange[0]) + "End time "+ dateFormat.format(dateRange[1]));
//        }
        return dateRanges;
    }

}




























