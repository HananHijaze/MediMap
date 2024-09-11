package com.example.medimap;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.UserRoom;
import com.example.medimap.roomdb.UserWeekdayDao;
import com.example.medimap.roomdb.UserWeekdayRoom;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.UserApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Retrofit;

public class Settings extends AppCompatActivity {

    private TextInputEditText emailput, nameput, genderput, heightput, weightput, birthdateput,
            stepcountgoalput, hydrationgoalput, workoutlocationput, mealsperdayput, snacksperdayput, waterDefaultPut;
    private Spinner bodytypeput, goalput, diettypeput, allergiesput;
    private UserDao userDao;
    private UserRoom userRoom;

    //Selecting training days
    private TextView selectTrainingDays;
    private UserWeekdayDao userWeekdayDao;
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

//        // Initialize Room database and DAO
//        AppDatabaseRoom database = AppDatabaseRoom.getInstance(this);
//        userWeekdayDao = database.userWeekdayDao();

        selectTrainingDays = findViewById(R.id.select_training_days);
        selectTrainingDays.setOnClickListener(v -> showDaysSelectionDialog());

        // Initialize Room database and UserDao
        AppDatabaseRoom database = AppDatabaseRoom.getInstance(this);
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
        workoutlocationput = findViewById(R.id.workoutlocation_edit_text);
        mealsperdayput = findViewById(R.id.mealsperday_edit_text);
        snacksperdayput = findViewById(R.id.snacksperday_edit_text);
        waterDefaultPut = findViewById(R.id.waterDefault_edit_text);
        bodytypeput = findViewById(R.id.bodytype_spinner);
        goalput = findViewById(R.id.goal_spinner);
        diettypeput = findViewById(R.id.diet_spinner);
        allergiesput = findViewById(R.id.allergies_spinner);

        // Initialize Update Button and set OnClickListener
        View updateButton = findViewById(R.id.updateButton);
        updateButton.setOnClickListener(v -> saveUserData());
    }

    // Method to set up Spinners and prevent the first item from being selected
    private void setUpSpinners() {
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
    }

    // Helper method to create OnItemSelectedListener for spinners
    private AdapterView.OnItemSelectedListener createOnItemSelectedListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Prevent selection of the first item and indicate that it’s a prompt
                if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY); // Optional: visually indicate it’s a prompt
                }
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
                runOnUiThread(this::updateUIWithUserDetails);
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
        workoutlocationput.setText(userRoom.getWhereToWorkout());
        mealsperdayput.setText(String.valueOf(userRoom.getMealsPerDay()));
        snacksperdayput.setText(String.valueOf(userRoom.getSnacksPerDay()));
        waterDefaultPut.setText(String.valueOf(userRoom.getWaterDefault()));

        // Set spinner values based on user data
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

    // Method to save user data
    private void saveUserData() {
        if (!validateInputs()) {
            return; // Stop if inputs are invalid
        }

        // Retrieve input data from UI elements after validation
        String email = emailput.getText().toString();
        String name = nameput.getText().toString();
        String gender = genderput.getText().toString();
        int height = parseIntSafely(heightput.getText().toString());
        int weight = parseIntSafely(weightput.getText().toString());
        String birthDate = birthdateput.getText().toString();
        String bodyType = bodytypeput.getSelectedItem().toString();
        String goal = goalput.getSelectedItem().toString();
        int stepCountGoal = parseIntSafely(stepcountgoalput.getText().toString());
        int hydrationGoal = parseIntSafely(hydrationgoalput.getText().toString());
        String whereToWorkout = workoutlocationput.getText().toString();
        String dietType = diettypeput.getSelectedItem().toString();
        int mealsPerDay = parseIntSafely(mealsperdayput.getText().toString());
        int snacksPerDay = parseIntSafely(snacksperdayput.getText().toString());
        int waterDefault = parseIntSafely(waterDefaultPut.getText().toString());

        // Create a new UserRoom object with the collected data
        UserRoom newUser = new UserRoom(email, name, "", gender, height, weight, birthDate,
                bodyType, goal, stepCountGoal, hydrationGoal, whereToWorkout, dietType,
                mealsPerDay, snacksPerDay, waterDefault);

        // Use AsyncTask to perform database operations on a background thread
        AsyncTask.execute(() -> {
            try {
                if (userRoom == null) {
                    // Insert new user if userRoom is null (indicating it's a new entry)
                    userDao.insertUser(newUser);
                } else {
                    // Update existing user
                    newUser.setId(userRoom.getId());
                    userDao.updateUser(newUser);
                }
                runOnUiThread(() -> showMessage("User data saved successfully."));
            } catch (Exception e) {
                runOnUiThread(() -> showMessage("Failed to save user data. Please try again."));
            }
        });
    }

    // Show dialog with checkboxes for selecting training days
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

        builder.create().show();
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

        // Save selected days to Room database
        AsyncTask.execute(() -> {
            try {
                // Clear previous selections before saving new ones
                userWeekdayDao.deleteAllUserWeekdays();

                for (UserWeekdayRoom day : selectedWeekdays) {
                    userWeekdayDao.insertUserWeekday(day);
                }

                runOnUiThread(() -> showMessage("Training days saved successfully."));
            } catch (Exception e) {
                runOnUiThread(() -> showMessage("Failed to save training days."));
            }
        });
    }



    // Validate user inputs before saving
    private boolean validateInputs() {
        if (emailput.getText().toString().isEmpty()) {
            showMessage("Email is required.");
            return false;
        }
        if (nameput.getText().toString().isEmpty()) {
            showMessage("Name is required.");
            return false;
        }
        if (genderput.getText().toString().isEmpty()) {
            showMessage("Gender is required.");
            return false;
        }
        if (isSpinnerPromptSelected(bodytypeput)) {
            showMessage("Please select a valid body type.");
            return false;
        }
        if (isSpinnerPromptSelected(goalput)) {
            showMessage("Please select a valid goal.");
            return false;
        }
        if (isSpinnerPromptSelected(diettypeput)) {
            showMessage("Please select a valid diet type.");
            return false;
        }
        if (isSpinnerPromptSelected(allergiesput)) {
            showMessage("Please select a valid allergy option.");
            return false;
        }
        return true; // All validations passed
    }

    // Helper method to safely parse integers, returning 0 if parsing fails
    private int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // Return 0 or handle the error as needed
        }
    }

    // Helper method to check if the first spinner item (prompt) is selected
    private boolean isSpinnerPromptSelected(Spinner spinner) {
        return spinner.getSelectedItemPosition() == 0;
    }

    // Display a message to the user
    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
