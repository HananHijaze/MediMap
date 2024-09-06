    package com.example.medimap;

    import android.content.SharedPreferences;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Toast;

    import androidx.activity.EdgeToEdge;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;

    import com.example.medimap.server.User;
    import com.example.medimap.server.RetrofitClient;
    import com.example.medimap.server.UserApi;
    import com.google.android.material.textfield.TextInputEditText;


    import retrofit2.Call;
    import retrofit2.Callback;
    import retrofit2.Response;

    public class Settings extends AppCompatActivity {

        private UserApi userApi;
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

            // Initialize Retrofit
            userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

            // Retrieve email from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            String email = sharedPreferences.getString("email", "user1@example.com");  // Default to this if no email

            // Make the API call to get the user by email
            Call<User> call = userApi.getUserById(1L);  // Use the retrieved email


            call.enqueue(new Callback<User>() {

                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        user = response.body();
                        Toast.makeText(Settings.this, "User found", Toast.LENGTH_SHORT).show();
                        TextInputEditText emailput = findViewById(R.id.email);
                        emailput.setText("hello1");
                        // Now set the data in the UI
                        populateUserFields(user);
                    } else {
                        TextInputEditText emailput = findViewById(R.id.email);
                        emailput.setText("hello2");
                        Toast.makeText(Settings.this, "User not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    TextInputEditText emailput = findViewById(R.id.email);
                    emailput.setText("hello3");
                    Toast.makeText(Settings.this, "API call failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Separate method to populate fields
        private void populateUserFields(User user) {

            TextInputEditText emailput = findViewById(R.id.email);
            emailput.setText("hello");
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
            TextInputEditText mealsperdayput = findViewById(R.id.mealsperday);
            TextInputEditText snacksperdayput = findViewById(R.id.snacksperday);
            TextInputEditText waterdefultput = findViewById(R.id.waterdefault);

            emailput.setText(user.getEmail());
            nameput.setText(user.getName());
            genderput.setText(user.getGender());
            heightput.setText(String.valueOf(user.getHeight()));
            weightput.setText(String.valueOf(user.getWeight()));
            birthdateput.setText(user.getBirthDate().toString());
            bodytypeput.setText(user.getBodyType());
            goalput.setText(user.getGoal());
            stepcountgoalput.setText(String.valueOf(user.getStepcountgoal()));
            hydrationgoalput.setText(String.valueOf(user.getHydrationgoal()));
            workoutlocationput.setText(user.getWheretoworkout());
            diettypeput.setText(user.getDietType());
            mealsperdayput.setText(String.valueOf(user.getMealsperday()));
            snacksperdayput.setText(String.valueOf(user.getSnackesperday()));
            waterdefultput.setText(String.valueOf(user.getWaterDefault()));
        }

    }

