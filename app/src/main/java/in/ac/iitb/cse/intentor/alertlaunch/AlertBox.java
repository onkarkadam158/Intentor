package in.ac.iitb.cse.intentor.alertlaunch;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import in.ac.iitb.cse.intentor.R;

public class AlertBox extends AppCompatActivity {

    public void onCreate(Bundle savedinstanceState) {

        super.onCreate(savedinstanceState);
        String receivedPackageName = getIntent().getStringExtra("packageName");
        showCustomAlertDialog(receivedPackageName);

    }
    private void showCustomAlertDialog(String packageName) {
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
        String appName=getAppNameFromPackageName(packageName);
        button1.setText("\uD83D\uDE0A  Exit the " + appName + " Now"  );
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
