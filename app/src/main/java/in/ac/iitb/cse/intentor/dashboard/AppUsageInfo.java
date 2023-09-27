package in.ac.iitb.cse.intentor.dashboard;
public class AppUsageInfo {
    //list of objects of this class will have app,package as object having its usage time and access count for the day and total phone's unlock count+usagetime of day
    private String packageName; //Package name of an app for ex. "com.google.android.youtube"
    private String appName;
    private long totalUsageTimeOfToday; //usage time of apps for the day
    private long totalVisitCountsOfToday; //app access count of apps for today
    private long totalPhoneUnlockCountOfToday; // no of times phone is unlocked today
    private long totalPhoneUsageTimeOfToday; //total phone usage (including all apps used today in foreground) in time for today
    public void setPackageName(String packageName) {
        this.packageName=packageName;
    }
    public String getPackageName(){
        return this.packageName;
    }
    public void setAppName(String appNameFromPackageName) {
        this.appName=appNameFromPackageName;
    }
    public String getAppName() {
        return this.appName;
    }

    public void setTotalUsageTimeOfToday(long totalUsageTimeOfToday) {
        this.totalUsageTimeOfToday = totalUsageTimeOfToday;
    }

    public long getTotalUsageTimeOfToday() {
        return totalUsageTimeOfToday;
    }
    public void setTotalVisitCountsOfToday(long totalVisitCountsOfToday) {
        this.totalVisitCountsOfToday = totalVisitCountsOfToday;
    }
    public long getTotalVisitCountsOfToday() {
        return totalVisitCountsOfToday;
    }
    public void setTotalPhoneUnlockCountOfToday(long totalPhoneUnlockCountOfToday){
        this.totalPhoneUnlockCountOfToday = totalPhoneUnlockCountOfToday ;
    }
    public long getTotalPhoneUnlockCountOfToday(){
        return this.totalPhoneUnlockCountOfToday;
    }
    public void setTotalPhoneUsageTimeOfToday(long totalPhoneUsageTimeOfToday){
        this.totalPhoneUsageTimeOfToday=totalPhoneUsageTimeOfToday;
    }
    public long getTotalPhoneUsageTimeOfToday(){
        return this.totalPhoneUsageTimeOfToday;
    }
    
}

