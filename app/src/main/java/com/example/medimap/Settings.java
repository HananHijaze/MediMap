package com.example.medimap;

import static com.example.medimap.server.RetrofitClient.getRetrofitInstance;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AllergyDao;
import com.example.medimap.roomdb.AllergyRoom;
import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.roomdb.UserWeekdayDao;
import com.example.medimap.roomdb.UserWeekdayRoom;
import com.example.medimap.roomdb.UsersAllergiesDao;
import com.example.medimap.roomdb.UsersAllergiesRoom;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Settings extends AppCompatActivity {

    private TextInputEditText heightput, weightput,
            stepcountgoalput, waterDefaultPut;
    private TextInputEditText emailput, nameput, genderput, birthdateput, hydrationgoalput;
    private Spinner bodytypeput, goalput, diettypeput, allergiesput, mealsperdayput, snacksperdayput,workoutlocationput;
    private UserDao userDao;
    private UserRoom userRoom;
    private UserApi userApi;

    // Selecting training days
    private TextView selectTrainingDays;
    private UserWeekdayDao userWeekdayDao;
    private UsersAllergiesDao usersAllergiesDao;
    private AllergyDao allergyDao;
    private final String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
    private boolean[] selectedDays = new boolean[daysOfWeek.length];
    private List<UserWeekdayRoom> selectedWeekdays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        // Set window insets to adjust the layout according to system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AppDatabaseRoom database = AppDatabaseRoom.getInstance(this);

        usersAllergiesDao = database.usersAllergiesRoomDao();
        allergyDao = database.allergyDao();


        // Load user's allergies and populate the spinner
       // loadUserAllergiesFromDatabase(userRoom.getId());
        Retrofit retrofit = RetrofitClient.getRetrofitInstance();
        userApi = retrofit.create(UserApi.class);

        selectTrainingDays = findViewById(R.id.select_training_days);
        selectTrainingDays.setOnClickListener(v -> showDaysSelectionDialog());

        // Initialize Room database and UserDao
        userDao = database.userDao();

        // Initialize UI components and set up spinners
        initializeUIComponents();
        setUpSpinners();

        // Load user data from the database
        loadUserData();
    }

    // Initialize UI components
    private void initializeUIComponents() {
        emailput = findViewById(R.id.email_edit_text);
        nameput = findViewById(R.id.name_edit_text);
        genderput = findViewById(R.id.gender_edit_text);
        heightput = findViewById(R.id.height_edit_text);
        weightput = findViewById(R.id.weight_edit_text);
        birthdateput = findViewById(R.id.birthDate_edit_text);
        stepcountgoalput = findViewById(R.id.stepcountgoal_edit_text);
        hydrationgoalput = findViewById(R.id.hydrationgoal_edit_text);
        workoutlocationput = findViewById(R.id.workoutlocation);
        waterDefaultPut = findViewById(R.id.waterDefault_edit_text);
        bodytypeput = findViewById(R.id.bodytype_spinner);
        goalput = findViewById(R.id.goal_spinner);
        diettypeput = findViewById(R.id.diet_spinner);
        allergiesput = findViewById(R.id.allergies_spinner);
        mealsperdayput = findViewById(R.id.mealsperday_spinner);
        snacksperdayput = findViewById(R.id.snacksperday_spinner);

        // Initialize Update Button and set OnClickListener
        View updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> saveUserData());
    }

    // Method to set up Spinners
    private void setUpSpinners() {
        // Workout Location Spinner
        ArrayAdapter<CharSequence> workoutLocationAdapter = ArrayAdapter.createFromResource(this,
                R.array.workout_location_array, android.R.layout.simple_spinner_item);
        workoutLocationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        workoutlocationput.setAdapter(workoutLocationAdapter);
        workoutlocationput.setOnItemSelectedListener(createOnItemSelectedListener());

        // Body Type Spinner
        ArrayAdapter<CharSequence> bodyTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.body_type_array, android.R.layout.simple_spinner_item);
        bodyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bodytypeput.setAdapter(bodyTypeAdapter);
        bodytypeput.setOnItemSelectedListener(createOnItemSelectedListener());

        // Goal Spinner
        ArrayAdapter<CharSequence> goalAdapter = ArrayAdapter.createFromResource(this,
                R.array.goal_array, android.R.layout.simple_spinner_item);
        goalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        goalput.setAdapter(goalAdapter);
        goalput.setOnItemSelectedListener(createOnItemSelectedListener());

        // Diet Type Spinner
        ArrayAdapter<CharSequence> dietTypeAdapter = ArrayAdapter.createFromResource(this,
                R.array.diet_type_array, android.R.layout.simple_spinner_item);
        dietTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diettypeput.setAdapter(dietTypeAdapter);
        diettypeput.setOnItemSelectedListener(createOnItemSelectedListener());

        // Allergies Spinner
        ArrayAdapter<CharSequence> allergiesAdapter = ArrayAdapter.createFromResource(this,
                R.array.allergies_array, android.R.layout.simple_spinner_item);
        allergiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        allergiesput.setAdapter(allergiesAdapter);
        allergiesput.setOnItemSelectedListener(createOnItemSelectedListener());

        // Meals per day Spinner (2, 3, 4)
        ArrayAdapter<CharSequence> mealsAdapter = ArrayAdapter.createFromResource(this,
                R.array.meals_per_day_array, android.R.layout.simple_spinner_item);
        mealsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mealsperdayput.setAdapter(mealsAdapter);

        // Snacks per day Spinner (0, 1, 2)
        ArrayAdapter<CharSequence> snacksAdapter = ArrayAdapter.createFromResource(this,
                R.array.snacks_per_day_array, android.R.layout.simple_spinner_item);
        snacksAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snacksperdayput.setAdapter(snacksAdapter);
    }

    // Helper method to create OnItemSelectedListener for spinners
    private AdapterView.OnItemSelectedListener createOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position == 0) {
//                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optional: Handle case where no selection is made
            }
        };
    }

    // Load user data from Room database
    private void loadUserData() {
        AsyncTask.execute(() -> {
            userRoom = userDao.getFirstUser(); // Fetch the first user
            if (userRoom != null) {
                runOnUiThread(() -> {
                    updateUIWithUserDetails();
                    // Load allergies after userRoom has been initialized
                    loadUserAllergiesFromDatabase(userRoom.getId());  // Move this here
                });
            }
        });
    }

    // Method to update the UI with user details
    private void updateUIWithUserDetails() {
        emailput.setText(userRoom.getEmail());
        nameput.setText(userRoom.getName());
        genderput.setText(userRoom.getGender());
        heightput.setText(String.valueOf(userRoom.getHeight()));
        weightput.setText(String.valueOf(userRoom.getWeight()));
        birthdateput.setText(userRoom.getBirthDate());
        stepcountgoalput.setText(String.valueOf(userRoom.getStepCountGoal()));
        hydrationgoalput.setText(String.valueOf(userRoom.getHydrationGoal()));
// Set the selected item in Spinner using its index in the adapter
        workoutlocationput.setSelection(getIndex(workoutlocationput, userRoom.getWhereToWorkout()));
        mealsperdayput.setSelection(getIndex(mealsperdayput, String.valueOf(userRoom.getMealsPerDay())));
        snacksperdayput.setSelection(getIndex(snacksperdayput, String.valueOf(userRoom.getSnacksPerDay())));
        waterDefaultPut.setText(String.valueOf(userRoom.getWaterDefault()));
        setSpinnerSelection(bodytypeput, R.array.body_type_array, userRoom.getBodyType());
        setSpinnerSelection(goalput, R.array.goal_array, userRoom.getGoal());
        setSpinnerSelection(diettypeput, R.array.diet_type_array, userRoom.getDietType());
        setSpinnerSelection(allergiesput, R.array.allergies_array, userRoom.getDietType());
    }

    // Helper method to set spinner selection based on string value
    private void setSpinnerSelection(Spinner spinner, int arrayResId, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        int position = adapter.getPosition(value);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }

    private int getIndex(Spinner spinner, String value) {
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        return adapter.getPosition(value);
    }

    // Method to save user data
    private void saveUserData() {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            showNoInternetDialog(); // Show the custom dialog
        } else {
            if (!validateInputs() && !isServerReachable()) {
                return; // Stop if inputs are invalid
            }
            String email = emailput.getText().toString();
            String name = nameput.getText().toString();
            String gender = genderput.getText().toString();
            int height = parseIntSafely(heightput.getText().toString());
            int weight = parseIntSafely(weightput.getText().toString());
            String birthDate = birthdateput.getText().toString();
            String bodyType = bodytypeput.getSelectedItem().toString();  // Get selected item from spinner
            String goal = goalput.getSelectedItem().toString();  // Get selected item from spinner
            int stepCountGoal = parseIntSafely(stepcountgoalput.getText().toString());
            int hydrationGoal = parseIntSafely(hydrationgoalput.getText().toString());
            String whereToWorkout = workoutlocationput.getSelectedItem().toString();  // Get selected item from spinner
            int mealsPerDay = Integer.parseInt(mealsperdayput.getSelectedItem().toString());
            String dietType = diettypeput.getSelectedItem().toString();
            int snacksPerDay = Integer.parseInt(snacksperdayput.getSelectedItem().toString());
            int waterDefault = parseIntSafely(waterDefaultPut.getText().toString());

            UserRoom newUser = new UserRoom(email, name, "", gender, height, weight, birthDate,
                    bodyType, goal, stepCountGoal, hydrationGoal, whereToWorkout,
                    dietType, mealsPerDay, snacksPerDay, waterDefault);

            AsyncTask.execute(() -> {
                try {
                    if (userRoom == null) {
                        userDao.insertUser(newUser);
                    } else {
                        newUser.setId(userRoom.getId());
                        userDao.updateUser(newUser);
                    }
                    runOnUiThread(() -> showMessage("User data saved successfully."));
                } catch (Exception e) {
                    runOnUiThread(() -> showMessage("Failed to save user data. Please try again."));
                }
            });
        }
    }
    private boolean validateInputs() {
        // Validate Body Type Spinner
       // if (isSpinnerPromptSelected(bodytypeput)) {
         //   showMessage("Please select a valid body type.");
          //  return false;
      //  }

        // Validate Goal Spinner
      //  if (isSpinnerPromptSelected(goalput)) {
       //     showMessage("Please select a valid goal.");
       //     return false;
      //  }

        // Validate Diet Type Spinner
       // if (isSpinnerPromptSelected(diettypeput)) {
       //     showMessage("Please select a valid diet type.");
      //      return false;
     //   }

        // Validate Allergies Spinner
       // if (isSpinnerPromptSelected(allergiesput)) {
       //     showMessage("Please select a valid allergy option.");
      //      return false;
     //    }

        // Validate Height
        String heightInput = heightput.getText().toString();
        if (heightInput.isEmpty()) {
            showMessage("Please enter your height.");
            return false;
        }

        try {
            int height = Integer.parseInt(heightInput);  // Parse the string to an integer

            // Check if height is within a reasonable range (e.g., 50cm to 300cm)
            if (height < 50 || height > 300) {
                showMessage("Please enter a valid height between 50 and 300 cm.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for height.");
            return false;
        }

        // Validate Weight
        String weightInput = weightput.getText().toString();
        if (weightInput.isEmpty()) {
            showMessage("Please enter your weight.");
            return false;
        }

        try {
            int weight = Integer.parseInt(weightInput);

            // Check if weight is within a reasonable range (e.g., 30kg to 300kg)
            if (weight < 30 || weight > 300) {
                showMessage("Please enter a valid weight between 30 and 300 kg.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for weight.");
            return false;
        }

        // Validate Step Count Goal
        String stepCountGoalInput = stepcountgoalput.getText().toString();
        if (stepCountGoalInput.isEmpty()) {
            showMessage("Please enter your step count goal.");
            return false;
        }

        try {
            int stepCountGoal = Integer.parseInt(stepCountGoalInput);

            // Check if step count goal is within a reasonable range (e.g., 1000 to 100000 steps)
            if (stepCountGoal < 1000 || stepCountGoal > 100000) {
                showMessage("Please enter a valid step count goal between 1000 and 100000.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for the step count goal.");
            return false;
        }

        // Validate Water Default
        String waterDefaultInput = waterDefaultPut.getText().toString();
        if (waterDefaultInput.isEmpty()) {
            showMessage("Please enter your default water intake.");
            return false;
        }

        try {
            int waterDefault = Integer.parseInt(waterDefaultInput);

            // Check if water default is within a reasonable range (e.g., 500ml to 5000ml)
            if (waterDefault < 100 || waterDefault >1500) {
                showMessage("Please enter a valid water intake between 500 and 5000 ml.");
                return false;
            }
        } catch (NumberFormatException e) {
            showMessage("Please enter a valid number for the water intake.");
            return false;
        }

        return true; // All validations passed
    }


    private void showNoInternetDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_no_internet, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();
        Button btnOk = dialogView.findViewById(R.id.Save);
        btnOk.setOnClickListener(v -> dialog.dismiss());
    }

    private boolean isServerReachable() {
        try {
            Call<Void> call = userApi.pingServer();
            Response<Void> response = call.execute();
            return response.isSuccessful();
        } catch (Exception e) {
            Log.e("Server Check", "Failed to reach server: " + e.getMessage(), e);
            return false;
        }
    }

    private int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // Handle error case
        }
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void showDaysSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Training Days");

        // MultiChoice dialog with actual selectable days, skipping the first placeholder
        builder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
            // Toggle the selected state
            selectedDays[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            saveSelectedDays();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Show the dialog first, so you can access the buttons
        dialog.show();

        // Change the button colors
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue)); // OK button color
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));  // Cancel button color
    }



    // Save selected days to the database
    private void saveSelectedDays() {
        selectedWeekdays.clear();

        for (int i = 0; i < selectedDays.length; i++) {
            if (selectedDays[i]) {
                // Map the selected day to its index as the weekday ID
                long userId = 0;
                selectedWeekdays.add(new UserWeekdayRoom(userId, (long) i));
            }
        }


    }
    //private boolean isSpinnerPromptSelected(Spinner spinner) {
        // Check if the first item (position 0) is selected
       // return spinner.getSelectedItemPosition() == 0;
    //}
    private void loadUserAllergiesFromDatabase(Long userId) {
        AsyncTask.execute(() -> {
            // Fetch the user's allergies from UsersAllergiesRoom table
            List<UsersAllergiesRoom> usersAllergiesList = usersAllergiesDao.getAllUsersAllergiesByUserId(userId);

            // Fetch the allergy names based on the allergyId from AllergyRoom table
            List<String> allergyNames = new ArrayList<>();
            for (UsersAllergiesRoom userAllergy : usersAllergiesList) {
                AllergyRoom allergyRoom = allergyDao.getAllergyById(userAllergy.getAllergyId());
                if (allergyRoom != null) {
                    // Use the correct getter method for the allergy name
                    allergyNames.add(allergyRoom.getName());
                }
            }

            // Run the UI update on the main thread
            runOnUiThread(() -> populateAllergiesSpinner(allergyNames));
        });
    }


    // Method to populate allergies spinner
    private void populateAllergiesSpinner(List<String> allergyNames) {
        // Convert List<String> to ArrayAdapter
        ArrayAdapter<String> allergiesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, allergyNames);

        // Specify the layout to use when the list of choices appears
        allergiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        Spinner allergiesSpinner = findViewById(R.id.allergies_spinner);
        allergiesSpinner.setAdapter(allergiesAdapter);
    }

}