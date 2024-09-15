package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;

public class Signup extends AppCompatActivity {
    Button signup;
    TextInputEditText fullNameEditText, emailEditText, phoneEditText, addressEditText, passwordEditText;

    private static final String PREFS_NAME = "UserSignUpData"; // SharedPreferences file name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize the input fields by linking them to their respective views with correct IDs
        fullNameEditText = findViewById(R.id.full_name);
        emailEditText = findViewById(R.id.email_edit);
        phoneEditText = findViewById(R.id.phone_edit);
        addressEditText = findViewById(R.id.address_edit);
        passwordEditText = findViewById(R.id.password_edit);

        // Initialize the Login button and set its click listener
        Button loginButton = findViewById(R.id.Login);
        loginButton.setOnClickListener(view -> {
            Intent in = new Intent(this, LogIn.class);
            startActivity(in);
        });

        // Initialize the Sign-Up button
        signup = findViewById(R.id.signup);

        // Set a click listener for the Sign-Up button
        signup.setOnClickListener(view -> {
            // Validate the input fields before proceeding
            if (validateInput()) {
                // Save the input data to SharedPreferences
                saveUserData();

                // Retrieve and show data in Toast messages for verification
                retrieveAndShowUserData();

                // If validation is successful, start the Birthdate activity
                Intent in = new Intent(this, Birthdate.class);
                startActivity(in);
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;

        // Validate Full Name
        if (TextUtils.isEmpty(fullNameEditText.getText())) {
            fullNameEditText.setError("Full Name is required");
            isValid = false;
        } else if (!fullNameEditText.getText().toString().matches("[a-zA-Z ]+")) {
            fullNameEditText.setError("Full Name must contain only letters");
            isValid = false;
        }

        // Validate Email
        if (TextUtils.isEmpty(emailEditText.getText())) {
            emailEditText.setError("Email is required");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailEditText.getText().toString()).matches()) {
            emailEditText.setError("Enter a valid email");
            isValid = false;
        }

        // Validate Phone Number
        if (TextUtils.isEmpty(phoneEditText.getText())) {
            phoneEditText.setError("Phone number is required");
            isValid = false;
        } else if (phoneEditText.getText().toString().length() != 9) {
            phoneEditText.setError("Enter a valid 9-digit phone number");
            isValid = false;
        }

        // Validate Address
        if (TextUtils.isEmpty(addressEditText.getText())) {
            addressEditText.setError("Address is required");
            isValid = false;
        }

        // Validate Password
        if (TextUtils.isEmpty(passwordEditText.getText())) {
            passwordEditText.setError("Password is required");
            isValid = false;
        } else if (passwordEditText.getText().toString().length() > 8) {
            passwordEditText.setError("Password must be Maximum 8 characters long");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Please correct the errors above", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private void saveUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save user data in SharedPreferences
        editor.putString("fullName", fullNameEditText.getText().toString());
        editor.putString("email", emailEditText.getText().toString());
        editor.putString("phone", phoneEditText.getText().toString());
        editor.putString("address", addressEditText.getText().toString());
        editor.putString("password", passwordEditText.getText().toString());
        editor.apply(); // Apply the changes asynchronously
    }

    private void retrieveAndShowUserData() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Retrieve the data (we can use it in the last page  )
       /* String fullName = sharedPreferences.getString("fullName", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String address = sharedPreferences.getString("address", "");
        String password = sharedPreferences.getString("password", "");*/

        //check the sharedPreferences (its working alhamdulillah)
        // Display the retrieved data using Toast messages
        // Toast.makeText(this, "Full Name: " + fullName, Toast.LENGTH_SHORT).show();
        // Toast.makeText(this, "Email: " + email, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Phone: " + phone, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Address: " + address, Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, "Password: " + password, Toast.LENGTH_SHORT).show();
    }
}