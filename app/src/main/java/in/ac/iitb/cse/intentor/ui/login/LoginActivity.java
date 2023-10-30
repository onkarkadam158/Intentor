package in.ac.iitb.cse.intentor.ui.login;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;

import in.ac.iitb.cse.intentor.R;
import in.ac.iitb.cse.intentor.UploadTrackedData.CollectData;
import in.ac.iitb.cse.intentor.alertlaunch.AppLaunchMonitorService;
import in.ac.iitb.cse.intentor.alertlaunch.OverlayService;
import in.ac.iitb.cse.intentor.dashboard.DashboardScrollingActivity;
import in.ac.iitb.cse.intentor.databinding.ActivityLoginBinding;
import in.ac.iitb.cse.intentor.ApiService;


public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_USAGE_STATS_PERMISSION = 101;
    private static final int REQUEST_OVERLAY_PERMISSION = 101;
    public static final int PERMISSION_REQUEST_CODE = 1;
    private ActivityLoginBinding binding;
    private static final long LONG_PRESS_DURATION = 4000; // 4 seconds
    private SharedPreferences loginDetails;
    private static final String SERVER_URL = "http://10.129.131.206:8000/login/check_registration_code";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loginDetails = getSharedPreferences("LoginDetails", Context.MODE_PRIVATE);
        // Issue the notification
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
        }
        if(loginDetails!=null && loginDetails.contains("RegistrationID")){
            updateUiWithUser();
        }

        final EditText usernameEditText = binding.username;
        final Button loginButton = binding.login;

        View longPressArea = binding.container;
        longPressArea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startServerLinkActivityDelayed();
                return true;
            }
        });
        // Start monitoring app launch in a background thread ######################################################

//      AppLaunchMonitorService appLaunchMonitorService = new AppLaunchMonitorService();
//        Intent serviceIntent = new Intent(getApplicationContext(), AppLaunchMonitorService.class);
//        startService(serviceIntent);
        Intent serviceIntent1 = new Intent(getApplicationContext(), NotificationForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent1);
        }
        else {
            startService(serviceIntent1);
        }
        //##################################################################################################################################


        CollectData collectData = new CollectData(this);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if (!isValidID(usernameEditText.getText().toString())) {
//                    Toast.makeText(getApplicationContext(), "Enter Valid Participation ID", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                if (!hasUsageStatsPermission()) {
                    requestUsageStatsPermission();
                    return;
                }
                if (!isOverlayPermissionGranted()) {
                    requestOverlayPermission();
                    return;
                }
//                updateUiWithUser();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Perform  network operation here
                        try {
                            System.out.println("First");
                            ApiService apiService = new ApiService();
                            System.out.println("second");
                            String entered_response_code = usernameEditText.getText().toString();
                            System.out.println(entered_response_code);
//                            ServerLinkActivity temp = new ServerLinkActivity();
//                            if(temp.getServerURL()!=null){
//                                SERVER_URL = temp.getServerURL();
//                            }
                            String response = apiService.getApiResponse(SERVER_URL, entered_response_code);
                            try {
                                collectData.uploadDataToServer();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            if (response != null) {
                                if (response.contains("status") && response.contains("success")) {
                                    // Perform operations for successful status
                                    SharedPreferences.Editor editor = loginDetails.edit();
                                    editor.putString("RegistrationID", entered_response_code);
                                    editor.apply();
//                                    Toast.makeText(getApplicationContext(), "Registration confirmed\nWelcome to Intentor", Toast.LENGTH_SHORT).show();
                                    updateUiWithUser();
                                } else {
//                                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                                }
                            }
                            System.out.println(response);
                        } catch (IOException e) {
                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),"Error! Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
        });
    }

    private void updateUiWithUser() {
        //  initiate successful logged in experience
        Intent intent = new Intent(LoginActivity.this, DashboardScrollingActivity.class);
        startActivity(intent);
    }
    public static boolean isValidID(String participationId) {
        return participationId.length()==6;
    }

    private boolean hasUsageStatsPermission() {
        AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageStatsPermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_USAGE_STATS_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_USAGE_STATS_PERMISSION) {
            if (!hasUsageStatsPermission()) {
                Toast.makeText(getApplicationContext(),"Please provide usage stat permission", Toast.LENGTH_LONG).show();
            }
        }
        else if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (!isOverlayPermissionGranted()) {
                Toast.makeText(this, "Overlay permission not granted. App functionality may be limited.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private boolean isOverlayPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(this);
        }
        return false;
    }

    private void requestOverlayPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
    }
    private void startServerLinkActivityDelayed() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginActivity.this, ServerLinkActivity.class);
                startActivity(intent);
            }
        }, LONG_PRESS_DURATION);
    }
}