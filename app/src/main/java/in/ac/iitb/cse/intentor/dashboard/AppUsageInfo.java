package in.ac.iitb.cse.intentor.dashboard;
public class AppUsageInfo {
    private String packageName;
    private String appName;
    private long totalUsageTime;
    private long totalUsageTimeOfToday;
//    private long totalVisitCounts;
    private long totalVisitCountsOfToday;
    public void setPackageName(String packageName) {
        this.packageName=packageName;
    }

    public void setAppName(String appNameFromPackageName) {
        this.appName=appNameFromPackageName;
    }

    public void setTotalUsageTime(long totalTimeInForeground) {
        this.totalUsageTime=totalTimeInForeground;
    }

    public long getTotalUsageTime() {
        return this.totalUsageTime;
    }

    public String getAppName() {
        return this.appName;
    }

    public String getPackageName(){
        return this.packageName;
    }

    public long getTotalUsageTimeOfToday() {
        return totalUsageTimeOfToday;
    }
    public void setTotalUsageTimeOfToday(long totalUsageTimeOfToday) {
        this.totalUsageTimeOfToday = totalUsageTimeOfToday;
    }

//    public long getTotalVisitCounts() {
//        return totalVisitCounts;
//    }

//    public void setTotalVisitCounts(long totalVisitCounts) {
//        this.totalVisitCounts = totalVisitCounts;
//    }

    public long getTotalVisitCountsOfToday() {
        return totalVisitCountsOfToday;
    }

    public void setTotalVisitCountsOfToday(long totalVisitCountsOfToday) {
        this.totalVisitCountsOfToday = totalVisitCountsOfToday;
    }
    
}

