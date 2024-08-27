package com.example.medimap;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LogIn extends AppCompatActivity {
TextView signup,forgetpass;
Button login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        signup= findViewById(R.id.textView2);
        signup.isClickable();
        signup.setOnClickListener(view -> {
            Intent in =new Intent(this, Signup.class);
            startActivity(in);
        });
        login=findViewById(R.id.login);
        login.setOnClickListener(view -> {
            Intent in =new Intent(this,Home.class);
            startActivity(in);
        });
        forgetpass=findViewById(R.id.textView);
        forgetpass.setOnClickListener(view -> {
            Intent in =new Intent(this,forgotPassword.class);
            startActivity(in);
        });
    }
}