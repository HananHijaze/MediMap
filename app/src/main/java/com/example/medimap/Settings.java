package com.example.medimap;

import static com.example.medimap.server.RetrofitClient.getRetrofitInstance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.medimap.roomdb.WeekDaysDao;
import com.example.medimap.roomdb.WeekDaysRoom;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.example.medimap.server.UserWeekday;
import com.example.medimap.server.UserWeekdayApi;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
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
    private UserWeekdayDao userWeekdayDao;
    private int selectedRating;

    // Selecting training days
    private TextView selectTrainingDays;
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
        userWeekdayDao = database.userWeekdayRoomDao();
        // Initialize UI components and set up spinners
        initializeUIComponents();
        setUpSpinners();

        // Load user data from the database
        loadUserData();

        // Load selected days for the user
        loadSelectedDays();

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
            SharedPreferences sharedPreferences = getSharedPreferences("UserSignUpData", MODE_PRIVATE);

            Long id  = sharedPreferences.getLong("id", 1L);


            UserRoom newUser = new UserRoom(id,email, name, "", gender, height, weight, birthDate,
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


                    // Convert UserRoom to User for the server update
                    User updatedUser = new User(newUser);

                    // Update the user data on the server
                    updateUserOnServer(updatedUser);
                    showRatePlanDialog(Settings.this);
                    if(selectedRating<8)//we only create a new plan if the firts one got a rating less than 8
                        CreatingPlan.getInstance().createPlan(Settings.this, updatedUser);

                    runOnUiThread(() -> showMessage("User data saved successfully."));
                } catch (Exception e) {
                    runOnUiThread(() -> showMessage("Failed to save user data. Please try again."));
                }
            });
        }
    }
    private boolean validateInputs() {

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

    // Show the multi-choice dialog with the user's selected days pre-checked
    private void showDaysSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Training Days");

        // MultiChoice dialog with selectable days
        builder.setMultiChoiceItems(daysOfWeek, selectedDays, (dialog, which, isChecked) -> {
            // Toggle the selected state
            selectedDays[which] = isChecked;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if(NetworkUtils.isNetworkAvailable(this)==true)
            {
                saveSelectedDays(); // Save the selected days when user confirms
            }
            else{
                showNoInternetDialog(); // Show the custom dialog
            }

        });


        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Change the button colors (Optional)
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.blue));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
    }


    // Save selected days to the database
    private void saveSelectedDays() {
        selectedWeekdays.clear();

        // Assuming userId is obtained from userRoom (for example)
        Long userId = userRoom.getId();

        for (int i = 0; i < selectedDays.length; i++) {
            if (selectedDays[i]) {
                // Map the selected day to its index as the weekday ID, adjusting by adding 1
                selectedWeekdays.add(new UserWeekdayRoom(userId, (long) (i + 1))); // i+1 to match the weekday ID in the database
            }
        }

        // Save the selected weekdays to the database
        AsyncTask.execute(() -> {
            // Clear existing selections for the user
            userWeekdayDao.deleteAllUserWeekdays();

            // Insert the new selections
            for (UserWeekdayRoom userWeekday : selectedWeekdays) {
                userWeekdayDao.insertUserWeekday(userWeekday);
            }
            // Clear and re-create records on the server
            deleteUserWeekdaysOnServer(userId, selectedWeekdays);


            // Log or show a message if necessary
            Log.d("Settings", "Selected days saved.");
        });
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
    // Load selected days from the database and update selectedDays[] array
    private void loadSelectedDays() {
        AsyncTask.execute(() -> {
            Long userId = userRoom.getId();  // Assuming you have the user ID

            // Get the user's selected weekdays
            List<UserWeekdayRoom> userSelectedDays = userWeekdayDao.getAllUserWeekdaysForUser(userId);

            // Reset selectedDays array (make sure all positions are reset, including 0)
            Arrays.fill(selectedDays, false);

            // Mark the days that the user has previously selected, including day 0 (Sunday)
            for (UserWeekdayRoom userWeekday : userSelectedDays) {
                Long weekdayId = userWeekday.getWeekdayId();
                if (weekdayId != null && weekdayId >= 1 && weekdayId <= 7) {
                    // Subtract 1 from the weekdayId to get the correct index in the selectedDays array
                    selectedDays[weekdayId.intValue() - 1] = true; // Set the day as selected
                }
            }

            // Now, you can show the dialog with the selected days marked
          //  runOnUiThread(() -> showDaysSelectionDialog());
        });
    }

    private void updateUserOnServer(User updatedUser) {
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

        Call<User> call = userApi.updateUser(updatedUser.getId(), updatedUser);
        call.enqueue(new retrofit2.Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    runOnUiThread(() -> showMessage("User updated on server successfully."));
                } else {
                    runOnUiThread(() -> showMessage("Failed to update user on server."));
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                runOnUiThread(() -> showMessage("Error updating user on server: " + t.getMessage()));
            }
        });
    }

//deleting all the saved days for the user from the server
    private void deleteUserWeekdaysOnServer(Long userId, List<UserWeekdayRoom> selectedWeekdays) {
        UserWeekdayApi userWeekdayApi = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);
        Call<Void> deleteCall = userWeekdayApi.deleteUserWeekday(userId);

        deleteCall.enqueue(new retrofit2.Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    createUserWeekdaysOnServer(userId, selectedWeekdays);
                } else {
                    Log.e("Server Sync", "Failed to delete old weekdays on server");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("Server Sync", "Error communicating with server: " + t.getMessage());
            }
        });
    }
//inserting the new days that the user picked in the server
    private void createUserWeekdaysOnServer(Long userId, List<UserWeekdayRoom> selectedWeekdays) {
        UserWeekdayApi userWeekdayApi = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);

        for (UserWeekdayRoom weekday : selectedWeekdays) {
            UserWeekday userWeekday = new UserWeekday(userId, weekday.getWeekdayId());
            Call<UserWeekday> createCall = userWeekdayApi.createUserWeekday(userWeekday);

            createCall.enqueue(new retrofit2.Callback<UserWeekday>() {
                @Override
                public void onResponse(Call<UserWeekday> call, Response<UserWeekday> response) {
                    if (!response.isSuccessful()) {
                        Log.e("Server Sync", "Failed to create weekday on server");
                    }
                }

                @Override
                public void onFailure(Call<UserWeekday> call, Throwable t) {
                    Log.e("Server Sync", "Error creating weekday on server: " + t.getMessage());
                }
            });
        }
    }
    public void showRatePlanDialog(Context context) {
        // Inflate the dialog layout
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.rate_plan_dialog, null);

        // Create the dialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);  // Prevent user from closing the dialog without interaction
        AlertDialog dialog = dialogBuilder.create();

        // Find the Spinner and populate it with numbers 1-10
        Spinner spinner = dialogView.findViewById(R.id.planratingspinner);
        List<Integer> ratings = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ratings.add(i);
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, ratings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Handle submit button click
        Button submitButton = dialogView.findViewById(R.id.submitbutton);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedRating = (int) spinner.getSelectedItem();

                // Handle the selected rating, e.g., save it to the database or send it to a server
                Toast.makeText(context, "You rated the plan: " + selectedRating, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        // Show the dialog
        dialog.show();
    }


}