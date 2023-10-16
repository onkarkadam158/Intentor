package in.ac.iitb.cse.intentor.dashboard;

public class WeeklyUsageInfo {
    private String date;
    private String day;
    private long phoneUnlockCount=0;
    private long phoneUsageTimeinMilli=0;

    public WeeklyUsageInfo(String date, String day, long phoneUnlockCount, long phoneUsageTimeinMilli) {
        this.date = date;
        this.day = day;
        this.phoneUnlockCount = phoneUnlockCount;
        this.phoneUsageTimeinMilli = phoneUsageTimeinMilli;
    }

    public WeeklyUsageInfo() {

    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getPhoneUnlockCount() {
        return phoneUnlockCount;
    }

    public void setPhoneUnlockCount(long phoneUnlockCount) {
        this.phoneUnlockCount = phoneUnlockCount;
    }

    public long getPhoneUsageTimeinMilli() {
        return phoneUsageTimeinMilli;
    }

    public void setPhoneUsageTimeinMilli(long phoneUsageTimeinMilli) {
        this.phoneUsageTimeinMilli = phoneUsageTimeinMilli;
    }
}
