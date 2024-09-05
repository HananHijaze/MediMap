package com.example.medimap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LogIn extends AppCompatActivity {
    TextView signUp, forgetPass;
    TextInputEditText email, password;
    Button login;
    UserDao userDao;
    UserApi userApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        // Initialize views
        email = findViewById(R.id.emailin);
        password = findViewById(R.id.passwordin);
        login = findViewById(R.id.login);
        signUp = findViewById(R.id.signUp);
        forgetPass = findViewById(R.id.textView);


        //1.initialize retrofit instance and creating an implementation of the UserApi interface
        Retrofit retrofit = RetrofitClient.getClient();
        userApi = retrofit.create(UserApi.class);

        //2.Initialize Room database and UserDao
        AppDatabaseRoom db = AppDatabaseRoom.getInstance(this);
        userDao = db.userDao();


        // Setup login button click listener
        login.setOnClickListener(view -> {
            if (NetworkUtils.isNetworkAvailable(LogIn.this)) {
                performLogin(); // Call the server login function
            } else {
                performLocalLogin(); // Call the local login function
            }
        });

        // Forget password button listener
        forgetPass.setOnClickListener(view -> {
            Intent in = new Intent(LogIn.this, forgotPassword.class);
            startActivity(in);
        });

        // Sign up button listener
        signUp.setOnClickListener(view -> {
            Intent in = new Intent(LogIn.this, Signup.class);
            startActivity(in);
        });
    }

    //////////////////////////////////////////////////////////////////

    // Function to check login through server
    private void performLogin() {
        String emailIn = email.getText().toString().trim();
        String passwordIn = password.getText().toString().trim();

        if (emailIn.isEmpty() || passwordIn.isEmpty())
        {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        if (emailIn.isEmpty() || passwordIn.isEmpty())
        {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Call the API to get the user by email
        Call<User> call = UserApi.getUserByEmail(emailIn); // Only declare this once
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();

                    // Check if the password matches
                    if (user.getPassword().equals(passwordIn)) {
                        Toast.makeText(LogIn.this, "Login successful (server)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LogIn.this, Home.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(LogIn.this, "Incorrect email or password (server)", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LogIn.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LogIn.this, "Failed to connect to server", Toast.LENGTH_SHORT).show();
                Log.e("Login Error", t.getMessage());
            }
        });
    }


    // Function to check login locally using Room database
    private void performLocalLogin() {
        String emailIn = email.getText().toString().trim();
        String passwordIn = password.getText().toString().trim();

        if (emailIn.isEmpty() || passwordIn.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch user from the Room database
        UserRoom user = UserDao.getUserByEmail(emailIn);
        if (user != null && user.getPassword().equals(passwordIn)) {
            Toast.makeText(LogIn.this, "Login successful (local)", Toast.LENGTH_SHORT).show();
            Intent in = new Intent(LogIn.this, Home.class);
            startActivity(in);
        } else {
            Toast.makeText(LogIn.this, "Incorrect email or password (local)", Toast.LENGTH_SHORT).show();
        }
    }
}