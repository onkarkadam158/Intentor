package in.ac.iitb.cse.intentor.register;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import in.ac.iitb.cse.intentor.R;
import in.ac.iitb.cse.intentor.ui.login.LoginActivity;

public class RegistrationActivity extends AppCompatActivity {


    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    // Pattern object to compile the regex pattern
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private RadioGroup radioGroupGender;
    private Spinner spinnerEducation;
    private Button buttonRegister;
    private TextView textViewSelectedDOB;
    private Calendar calendar;
    private int year, month, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        Button buttonSelectDOB = findViewById(R.id.buttonSelectDOB);
        textViewSelectedDOB = findViewById(R.id.textViewSelectedDOB);
        radioGroupGender = findViewById(R.id.radioGroupGender);
        spinnerEducation = findViewById(R.id.spinnerEducation);

        buttonSelectDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        ArrayAdapter<CharSequence> educationAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.education_levels,
                android.R.layout.simple_spinner_item
        );
        educationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEducation.setAdapter(educationAdapter);

        buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setEnabled(true);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }
    private void showDatePickerDialog() {
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // Update the TextView with the selected date
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        textViewSelectedDOB.setText("Selected Date: " + selectedDate);
                    }
                },
                year,
                month,
                day
        );
        // Set the maximum date to the current date to prevent selecting future dates
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }


private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        String dob = textViewSelectedDOB.getText().toString().trim();
//        dob = dob.substring(15); //removing the text "Selected date: "
        String educationLevel = spinnerEducation.getSelectedItem().toString();
        String gender = "";
        // Check if any radio button is selected
        if (radioGroupGender.getCheckedRadioButtonId() != -1) {
            // Get the selected radio button's text (gender)
            gender = ((RadioButton) findViewById(radioGroupGender.getCheckedRadioButtonId())).getText().toString();
        }

        // Basic validation
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || dob.isEmpty() || gender.isEmpty() || gender=="Selected Date: " || educationLevel.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }
        //valid username
        if (!isValidEmail(email)){
            Toast.makeText(this,"Invalid Email Address", Toast.LENGTH_SHORT).show();
            return ;
        }
        //valid password
        if (!isPasswordValid(password) || !isPasswordValid(confirmPassword)){
            Toast.makeText(this,"Password should be of at least 5 characters", Toast.LENGTH_SHORT).show();
            return ;
        }
        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        // You can perform further validation or save the data to a database here.
        // Handle the button click and navigate to the Login page
        Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
        Toast.makeText(this, "Registration Successful!", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        //  we'll just display a toast indicating successful registration.
    }

    // A placeholder Email validation check

    public static boolean isValidEmail(String email) {
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() >= 5;
    }
}
