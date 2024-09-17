package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Initialize the input fields
        fullNameEditText = findViewById(R.id.full_name);
        emailEditText = findViewById(R.id.email_edit);
        phoneEditText = findViewById(R.id.phone_edit);
        addressEditText = findViewById(R.id.address_edit);
        passwordEditText = findViewById(R.id.password_edit);

        // Initialize the Sign-Up button
        signup = findViewById(R.id.signup);

        // Set a click listener for the Sign-Up button
        signup.setOnClickListener(view -> {
            if (validateInput()) {
                checkNetworkAndProceed();
            }
        });

        // Initialize the Login button
        Button loginButton = findViewById(R.id.Login);
        loginButton.setOnClickListener(view -> {
            Intent in = new Intent(this, LogIn.class);
            startActivity(in);
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
        } else if (passwordEditText.getText().toString().length() < 4 || passwordEditText.getText().toString().length() > 8) {
            passwordEditText.setError("Password must be 4 to 8 characters long");
            isValid = false;
        }

        if (!isValid) {
            Toast.makeText(this, "Please correct the errors above", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }

    private void checkNetworkAndProceed() {
        if (NetworkUtils.isNetworkAvailable(this)) {
            // Network is available, check the server for user existence
            checkUserExists();
        } else {
            // Show dialog if no network is available
            showNoInternetDialog();
        }
    }

    private void checkUserExists() {
        String email = emailEditText.getText().toString().trim();
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

        // Call the API to check if a user with the email exists
        Call<User> call = userApi.findByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // User exists, show "User already signed up" dialog and redirect to login page
                    showUserAlreadyExistsDialog();
                } else {
                    // If the user doesn’t exist, proceed with sign-up
                    proceedWithSignup();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(Signup.this, "Error checking user: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void proceedWithSignup() {
        // Save user data and proceed to the next step
        saveUserData();
        retrieveAndShowUserData();

        // Proceed to the Birthdate activity
        Intent in = new Intent(Signup.this, Birthdate.class);
        startActivity(in);
    }

    // Show "User already signed up" dialog
    private void showUserAlreadyExistsDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_user_already_exists, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        Button btnOk = dialogView.findViewById(R.id.okButton);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            // Redirect to login page
            Intent loginIntent = new Intent(Signup.this, LogIn.class);
            startActivity(loginIntent);
        });
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

        // Retrieve the data (for verification or use later)
        /*
        String fullName = sharedPreferences.getString("fullName", "");
        String email = sharedPreferences.getString("email", "");
        String phone = sharedPreferences.getString("phone", "");
        String address = sharedPreferences.getString("address", "");
        String password = sharedPreferences.getString("password", "");

        // For testing
        Toast.makeText(this, "Full Name: " + fullName, Toast.LENGTH_SHORT).show();
        */
    }

    // Show no internet dialog (reuse this method)
    private void showNoInternetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button btnOk = dialogView.findViewById(R.id.Save);
        btnOk.setOnClickListener(v -> dialog.dismiss());
    }
}
