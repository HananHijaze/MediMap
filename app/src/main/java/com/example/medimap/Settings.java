package com.example.medimap;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.server.User;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.UserApiService;
import com.google.android.material.textfield.TextInputEditText;


import retrofit2.Retrofit;

public class Settings extends AppCompatActivity {


    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        UserApiService userApiService = new UserApiService(retrofit);


        // Retrieve email from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "");

        userApiService.getUserByEmail("user1@example.com", new UserApiService.UserCallback<User>() {
            @Override
            public void onSuccess(User userre) {
                user = userre;
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error
            }
        });
        TextInputEditText emailput = findViewById(R.id.email);
        TextInputEditText nameput = findViewById(R.id.name);
        TextInputEditText genderput = findViewById(R.id.gender);
        TextInputEditText heightput = findViewById(R.id.height);
        TextInputEditText weightput = findViewById(R.id.weight);
        TextInputEditText birthdateput = findViewById(R.id.birthdate);
        TextInputEditText bodytypeput = findViewById(R.id.bodytype);
        TextInputEditText goalput = findViewById(R.id.goal);
        TextInputEditText stepcountgoalput = findViewById(R.id.stepcountgoal);
        TextInputEditText hydrationgoalput = findViewById(R.id.hydrationgoal);
        TextInputEditText workoutlocationput = findViewById(R.id.workoutlocation);
        TextInputEditText diettypeput = findViewById(R.id.diettype);
        TextInputEditText allergiesput = findViewById(R.id.allergies);
        TextInputEditText mealsperdayput = findViewById(R.id.mealsperday);
        TextInputEditText snacksperdayput = findViewById(R.id.snacksperday);
        TextInputEditText waterdefultput = findViewById(R.id.waterdefault);

        emailput.setText(user.getEmail());
        nameput.setText(user.getName());
        genderput.setText(user.getGender());
        heightput.setText(user.getHeight()+"");
        weightput.setText(user.getWeight()+"");
        birthdateput.setText(user.getBirthDate().toString());
        bodytypeput.setText(user.getBodyType());
        goalput.setText(user.getGoal());
        stepcountgoalput.setText(user.getStepcountgoal()+"");
        hydrationgoalput.setText(user.getHydrationgoal()+"");
        workoutlocationput.setText(user.getWheretoworkout());
        diettypeput.setText(user.getDietType());
        mealsperdayput.setText(user.getMealsperday()+"");
        snacksperdayput.setText(user.getSnackesperday()+"");
        waterdefultput.setText(user.getWaterDefault()+"");
        

    }

}
