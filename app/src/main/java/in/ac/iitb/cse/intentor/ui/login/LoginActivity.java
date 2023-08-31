package in.ac.iitb.cse.intentor.ui.login;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import in.ac.iitb.cse.intentor.alertlaunch.AppLaunchMonitorService;
import in.ac.iitb.cse.intentor.dashboard.DashboardScrollingActivity;
import in.ac.iitb.cse.intentor.databinding.ActivityLoginBinding;
import in.ac.iitb.cse.intentor.ApiService;


public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_USAGE_STATS_PERMISSION = 101;
    private static final int REQUEST_OVERLAY_PERMISSION = 101;
    private ActivityLoginBinding binding;
    private static final long LONG_PRESS_DURATION = 4000; // 3 seconds

    private static String SERVER_URL = "http://10.129.131.206:8000/login/check_registration_code";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        final EditText usernameEditText = binding.username;
        final Button loginButton = binding.login;
        final Button pressbutton = binding.login2;

        View longPressArea = binding.container;
        longPressArea.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startServerLinkActivityDelayed();
                return true;
            }
        });
        // Start monitoring app launch in a background thread########################################33333##############
        System.out.println("applaunch");

        AppLaunchMonitorService appLaunchMonitorService = new AppLaunchMonitorService();
        Intent serviceIntent = new Intent(getApplicationContext(), AppLaunchMonitorService.class);
//        startService(serviceIntent);
//        appLaunchMonitorService.onStartCommand(serviceIntent,0,0);
        System.out.println("started Intent");

        //##################################################################################################################################
        pressbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomAlertDialog();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUiWithUser();
                if(!isValidID(usernameEditText.getText().toString())){
                    Toast.makeText(getApplicationContext(),"Enter Valid Participation ID", Toast.LENGTH_LONG).show();
                    return ;
                }
                if (!hasUsageStatsPermission()) {
                    requestUsageStatsPermission();
                    return ;
                }
                if (!isOverlayPermissionGranted()) {
                    requestOverlayPermission();
                    return ;
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // Perform your network operation here
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
                            String response = apiService.getApiResponse(SERVER_URL,entered_response_code);
                            if (response != null) {
                                if (response.contains("status") && response.contains("success")) {
                                    // Perform operations for successful status
                                    updateUiWithUser();
                                }
                                else {
                                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                                }
                            }
                            System.out.println(response);
                        }catch (IOException e) {
                            e.printStackTrace();
//                            Toast.makeText(getApplicationContext(),"Error! Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                }).start();
            }
        });
    }

    private void showCustomAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialogue_layout, null);

        // Set up the text view
        TextView statisticsTextView = dialogView.findViewById(R.id.statisticsTextView);
        statisticsTextView.setText("Displaying statistics here\n\n");

        // Set up buttons
        Button button1 = dialogView.findViewById(R.id.button1);
        Button button2 = dialogView.findViewById(R.id.button2);
        Button button3 = dialogView.findViewById(R.id.button3);

        button1.setText("\uD83D\uDE0A  Exit the Instagram Now");
        button2.setText("\uD83D\uDE10 Remind me again in 10 minutes");
        button3.setText("\uD83D\uDE22 Mute alert for the rest of the day");

        button1.setBackgroundColor(getResources().getColor(R.color.green));
        button2.setBackgroundColor(getResources().getColor(R.color.yellow));
        button3.setBackgroundColor(getResources().getColor(R.color.red));


        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 1
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 2
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Action for Button 3
            }
        });

        builder.setView(dialogView)
                .setTitle("Alert");

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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