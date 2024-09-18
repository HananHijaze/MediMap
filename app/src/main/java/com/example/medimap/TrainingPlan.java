package com.example.medimap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.medimap.server.Workout;
import com.example.medimap.server.WorkoutApi;
import com.example.medimap.server.WorkoutPlan;
import com.example.medimap.server.WorkoutPlanApi;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrainingPlan extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private WorkoutAdapter workoutAdapter;
    private List<Workout> workoutList = new ArrayList<>();
    private Date selectedDate = null;
    private static final String PREFS_NAME = "UserSignUpData";
    private CardView lastSelectedMonth = null;
    private int selectedMonth = -1;
    private int selectedYear = -1;

    // Date format for hardcoding and displaying
    private static final String DATE_FORMAT = "MMM d, yyyy hh:mm:ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_training_plan);

        // Set up window insets for full-screen experience
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupViews();

        // Setup RecyclerView and Adapter
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        workoutAdapter = new WorkoutAdapter(workoutList);
        recyclerView.setAdapter(workoutAdapter);

    }

    private void setupViews() {
        // Setting up UI components
        calendarView = findViewById(R.id.calendarView3);
        calendarView.setOnDateChangeListener(this::onDateChange);

        Button goToButton = findViewById(R.id.go_to);
        goToButton.setOnClickListener(this::showGoToDateDialog);
    }

    private void onDateChange(CalendarView view, int year, int month, int dayOfMonth) {
        // Handles date changes on the calendar view
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        try {
            selectedDate = parseDate(getFormattedDate(calendar.getTimeInMillis()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        calendarView.setDate(selectedDate.getTime(), true, true);
        // Load user data and fetch training plans
        loadUserAndFetchTrainingPlans();
    }

    private void showGoToDateDialog(View v) {
        // Shows a custom dialog to select the year and month
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_calendar_date, null);
        builder.setView(dialogView);

        Spinner yearSpinner = dialogView.findViewById(R.id.yearSpinner);
        ArrayAdapter<Integer> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getYearList());
        yearSpinner.setAdapter(yearAdapter);
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = (int) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedYear = -1;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        setupMonthListeners(dialogView);

        Button cancelButton = dialogView.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> dialog.dismiss());

        Button saveButton = dialogView.findViewById(R.id.Save);
        saveButton.setOnClickListener(view -> {
            if (selectedMonth != -1 && selectedYear != -1) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(selectedYear, selectedMonth, 1);
                try {
                    selectedDate = parseDate(getFormattedDate(calendar.getTimeInMillis()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                calendarView.setDate(selectedDate.getTime(), true, true);
                dialog.dismiss();
            } else {
                Toast.makeText(TrainingPlan.this, "Please select a month and year", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMonthListeners(View dialogView) {
        // Adds click listeners to each month card, allowing selection
        int[] monthCardIds = {
                R.id.Jan, R.id.Feb, R.id.Mar, R.id.Apr, R.id.May, R.id.Jun,
                R.id.Jul, R.id.Aug, R.id.Sep, R.id.Oct, R.id.Nov, R.id.Dec
        };

        for (int i = 0; i < monthCardIds.length; i++) {
            final int month = i;
            CardView monthCard = dialogView.findViewById(monthCardIds[i]);
            monthCard.setOnClickListener(view -> {
                if (lastSelectedMonth != null) {
                    lastSelectedMonth.setBackgroundResource(R.drawable.border_unselected);
                }
                lastSelectedMonth = monthCard;
                lastSelectedMonth.setBackgroundResource(R.drawable.border_selected);
                selectedMonth = month;
            });
        }
    }

    private Integer[] getYearList() {
        // Generates a list of the last 100 years for the year spinner
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        Integer[] years = new Integer[100];
        for (int i = 0; i < 100; i++) {
            years[i] = currentYear - i;
        }
        return years;
    }

    private String getFormattedDate(Long timeInMillis) {
        // Convert the time in milliseconds to a Date object
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        // Format the date using the specified DATE_FORMAT
        return sdf.format(calendar.getTime());
    }

    private void onDateSelected(CalendarView view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        selectedDate = calendar.getTime();
    }

    public Date parseDate(String dateString) throws ParseException {
        return sdf.parse(dateString);
    }

    private void loadUserAndFetchTrainingPlans() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "user6@example.com"); // Use the actual stored email
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

        // Make the API call to get the user by email
        Call<User> call = userApi.findByEmail(email);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (selectedDate != null) {
                        try {
                            fetchTrainingPlans(user.getId(), selectedDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(TrainingPlan.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(TrainingPlan.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchTrainingPlans(long userId, Date selectedDate) throws ParseException {
        WorkoutPlanApi trainingPlanApi = RetrofitClient.getRetrofitInstance().create(WorkoutPlanApi.class);

        // Use formatted date for API call
        Call<List<WorkoutPlan>> callTrainingPlan = trainingPlanApi.getDatedTrainingPlans(userId, formatDate(selectedDate));
        callTrainingPlan.enqueue(new Callback<List<WorkoutPlan>>() {
            @Override
            public void onResponse(Call<List<WorkoutPlan>> call, Response<List<WorkoutPlan>> response) {
                if (response.isSuccessful()) {
                    List<WorkoutPlan> trainingPlans = response.body();

                    if (trainingPlans != null && !trainingPlans.isEmpty()) {
                        int dayOfWeek = getDayOfWeek(selectedDate);
                        workoutList.clear();
                        boolean hasWorkoutsForDay = false;

                        for (WorkoutPlan trainingPlan : trainingPlans) {
                            if (trainingPlan.getWorkoutDay() == dayOfWeek) {
                                hasWorkoutsForDay = true; // Mark that we found a plan for the selected day
                                    fetchWorkoutDetails(trainingPlan.getWorkoutID());
                            }
                        }

                        // If no workout plans were found for the selected day, show a toast
                        if (!hasWorkoutsForDay) {
                            workoutList.clear();
                            workoutAdapter.notifyDataSetChanged();
                            Toast.makeText(TrainingPlan.this, "No workouts found for the selected date.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Handle case where no training plans are returned at all
                        workoutList.clear();
                        workoutAdapter.notifyDataSetChanged();
                        Toast.makeText(TrainingPlan.this, "No training plans found for the selected date.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Handle API error
                    workoutList.clear();
                    workoutAdapter.notifyDataSetChanged();
                    Toast.makeText(TrainingPlan.this, "No workouts found for the selected date.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<WorkoutPlan>> call, Throwable t) {
                Toast.makeText(TrainingPlan.this, "Failed to fetch training plans", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchWorkoutDetails(Long workoutId) {
        WorkoutApi workoutApi = RetrofitClient.getRetrofitInstance().create(WorkoutApi.class);

        Call<Workout> call = workoutApi.getWorkoutById(workoutId);
        call.enqueue(new Callback<Workout>() {
            @Override
            public void onResponse(Call<Workout> call, Response<Workout> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Workout workout = response.body();
                    workoutList.add(workout);
                    workoutAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Workout> call, Throwable t) {
                Toast.makeText(TrainingPlan.this, "Error fetching workout details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        return sdf.format(date);
    }
}
