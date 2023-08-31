package in.ac.iitb.cse.intentor.ui.login;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import in.ac.iitb.cse.intentor.R;

public class ServerLinkActivity extends AppCompatActivity {
    EditText serverLinkEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_link);
        serverLinkEditText = findViewById(R.id.serverLinkEditText1);
        serverLinkEditText.setVisibility(View.VISIBLE); // Show the EditText

    }
    public String getServerURL(){
        // Set the server link
        String serverLink = serverLinkEditText.getText().toString(); // Replace with your actual server link
        serverLinkEditText.setText(serverLink);
        return serverLink;
    }
}