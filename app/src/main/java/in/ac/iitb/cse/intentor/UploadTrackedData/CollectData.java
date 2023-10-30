package in.ac.iitb.cse.intentor.UploadTrackedData;
import android.content.Context;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import in.ac.iitb.cse.intentor.ApiService;
import in.ac.iitb.cse.intentor.dashboard.AppUsageInfo;
import in.ac.iitb.cse.intentor.dashboard.AppUsageStatistics;

public class CollectData{

    private static final String apiURL = "http://10.129.131.206:8000/login/receive_data";

//    public class AppUsageInfoJSON {
//        private String packageName;
//        private String appName;
//        private long totalUsageTimeOfToday;
//        private long totalVisitCountsOfToday;
//        private long totalPhoneUnlockCountOfToday;
//        private long totalPhoneUsageTimeOfToday;
//
//        public void setTotalPhoneUsageTimeOfToday(long totalPhoneUsageTimeOfToday) {
//            this.totalPhoneUsageTimeOfToday=totalPhoneUsageTimeOfToday;
//        }
//
//        public void setTotalPhoneUnlockCountOfToday(long totalPhoneUnlockCountOfToday) {
//            this.totalPhoneUnlockCountOfToday=totalPhoneUnlockCountOfToday;
//        }
//
//        public void setTotalVisitCountsOfToday(long totalVisitCountsOfToday) {
//            this.totalVisitCountsOfToday=totalVisitCountsOfToday;
//        }
//
//        public void setAppName(String appName) {
//            this.appName=appName;
//        }
//
//        public void setPackageName(String packageName) {
//            this.packageName=packageName;
//        }
//
//        public void setTotalUsageTimeOfToday(long totalUsageTimeOfToday) {
//        }
//    }

    private final Context context;

    public CollectData(Context context) {
        this.context = context;
    }
    public String getTodaysUsageFromAppUsageStatistics(){

        AppUsageStatistics appUsageStatistics = new AppUsageStatistics(context.getApplicationContext());
        List<AppUsageInfo> appUsageInfoList = appUsageStatistics.getUsageStatistics();

//        List<AppUsageInfoJSON> appUsageInfoJSONList = new ArrayList<>();
//        for (AppUsageInfo appUsageInfo : appUsageInfoList) {
//            AppUsageInfoJSON appUsageInfoJSON = new AppUsageInfoJSON();
//            appUsageInfoJSON.setPackageName(appUsageInfo.getPackageName());
//            appUsageInfoJSON.setAppName(appUsageInfo.getAppName());
//            appUsageInfoJSON.setTotalUsageTimeOfToday(appUsageInfo.getTotalUsageTimeOfToday());
//            appUsageInfoJSON.setTotalVisitCountsOfToday(appUsageInfo.getTotalVisitCountsOfToday());
//            appUsageInfoJSON.setTotalPhoneUnlockCountOfToday(appUsageInfo.getTotalPhoneUnlockCountOfToday());
//            appUsageInfoJSON.setTotalPhoneUsageTimeOfToday(appUsageInfo.getTotalPhoneUsageTimeOfToday());
//            appUsageInfoJSONList.add(appUsageInfoJSON);
//        }

        // Use Gson to convert the list to a JSON string
        Gson gson = new Gson();
        String json = gson.toJson(appUsageInfoList);

        return json;
    }
    public void uploadDataToServer() throws IOException {
        String json = getTodaysUsageFromAppUsageStatistics();

        ApiService apiService = new ApiService();
        apiService.postJsonToServer(apiURL,json);
    }

}
