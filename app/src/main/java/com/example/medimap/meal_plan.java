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

import com.example.medimap.server.Meal;
import com.example.medimap.server.MealApi;
import com.example.medimap.server.MealPlan;
import com.example.medimap.server.MealPlanApi;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class meal_plan extends AppCompatActivity {

    private CalendarView calendarView;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList = new ArrayList<>();
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
        setContentView(R.layout.activity_meal_plan);

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
        mealAdapter = new MealAdapter(mealList);
        recyclerView.setAdapter(mealAdapter);

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
            selectedDate =parseDate(getFormattedDate(calendar.getTimeInMillis()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        calendarView.setDate(selectedDate.getTime(), true, true);
        // Load user data and fetch meal plans
        loadUserAndFetchMealPlans();
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
                    selectedDate =parseDate(getFormattedDate(calendar.getTimeInMillis()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                calendarView.setDate(selectedDate.getTime(), true, true);
                dialog.dismiss();
            } else {
                Toast.makeText( meal_plan.this , "Please select a month and year", Toast.LENGTH_SHORT).show();
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

//    private void hardCodeDateForTesting() {
//        try {
//            selectedDate = sdf.parse("Sep 12, 2023 11:30:00"); // Hardcoded date for testing
//        } catch (ParseException e) {
//            throw new RuntimeException("Error parsing hardcoded date", e);
//        }
//    }

    private String formatDate(Date date) throws ParseException {
        return sdf.format(date);
    }

    // Method to parse String to Date
    public Date parseDate(String dateString) throws ParseException {
        return sdf.parse(dateString);
    }

    private void loadUserAndFetchMealPlans() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSignUpData", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "user6@example.com"); // Use the actual stored email
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

        // Show progress bar while fetching user data

        // Make the API call to get the user by email
        Call<User> call = userApi.findByEmail("user4@example.com"); // Dynamic email based on stored value
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (selectedDate != null) {
                        // Use formatted date for API call
                        try {
                            fetchMealPlans(user.getId(), selectedDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Toast.makeText(meal_plan.this, "Please select a date.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(meal_plan.this, "Error fetching user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchMealPlans(long userId, Date selectedDate) throws ParseException {
        MealPlanApi mealPlanApi = RetrofitClient.getRetrofitInstance().create(MealPlanApi.class);


        // Use formatted date for API call
        Call<List<MealPlan>> callMealPlan = mealPlanApi.getDatedMealPlans(userId, formatDate(selectedDate));
        callMealPlan.enqueue(new Callback<List<MealPlan>>() {
            @Override
            public void onResponse(Call<List<MealPlan>> call, Response<List<MealPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MealPlan> mealPlans = response.body();
                    int dayOfWeek = getDayOfWeekFromSelectedDate(selectedDate);
                    List<MealPlan> filteredPlans = filterByDayOfWeek(mealPlans, dayOfWeek);

                    if (filteredPlans.isEmpty()) {
                        mealList.clear();
                        mealAdapter.notifyDataSetChanged(); // Notify adapter of data change
                        Toast.makeText(meal_plan.this, "No meal plans found for this day.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    fetchMealDetails(filteredPlans);
                }
            }

            @Override
            public void onFailure(Call<List<MealPlan>> call, Throwable t) {
                Toast.makeText(meal_plan.this, "Error fetching meal plans.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int getDayOfWeekFromSelectedDate(Date selectedDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        return calendar.get(Calendar.DAY_OF_WEEK); // Sunday = 1, Monday = 2, etc.
    }

    private List<MealPlan> filterByDayOfWeek(List<MealPlan> mealPlans, int dayOfWeek) {
        List<MealPlan> filteredPlans = new ArrayList<>();
        for (MealPlan plan : mealPlans) {
            if (plan.getMealDay() == dayOfWeek) {
                filteredPlans.add(plan);
            }
        }
        return filteredPlans;
    }

    private void fetchMealDetails(List<MealPlan> mealPlans) {
        MealApi mealApi = RetrofitClient.getRetrofitInstance().create(MealApi.class);

        // Clear meal list before adding new meals
        mealList.clear();

        for (MealPlan plan : mealPlans) {
            Call<Meal> callMeal = mealApi.getMealById(plan.getMealID());
            callMeal.enqueue(new Callback<Meal>() {
                @Override
                public void onResponse(Call<Meal> call, Response<Meal> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Meal meal = response.body();
                        mealList.add(meal);
                        mealAdapter.notifyDataSetChanged(); // Notify adapter of data change
                    }
                }

                @Override
                public void onFailure(Call<Meal> call, Throwable t) {
                    Toast.makeText(meal_plan.this, "Error fetching meal details.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
