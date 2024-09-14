package com.example.medimap;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class meal_plan extends AppCompatActivity {

    private CalendarView calendarView;
    private Date selectedDate = null;
    private RecyclerView recyclerView;
    private MealAdapter mealAdapter;
    private List<Meal> mealList = new ArrayList<>();

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

        // Setup RecyclerView and Adapter
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mealAdapter = new MealAdapter(mealList);
        recyclerView.setAdapter(mealAdapter);

        // Setup calendar view for selecting date
        calendarView = findViewById(R.id.calendarView3);
        calendarView.setOnDateChangeListener(this::onDateSelected);

        // Load user data and fetch meal plans
        loadUserAndFetchMealPlans();
    }

    private void onDateSelected(CalendarView view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        selectedDate = calendar.getTime();
    }

    private void loadUserAndFetchMealPlans() {
        SharedPreferences sharedPreferences = getSharedPreferences("UserSignUpData", MODE_PRIVATE);
        String email = sharedPreferences.getString("email", "user6@example.com");
        UserApi userApi = RetrofitClient.getRetrofitInstance().create(UserApi.class);

        // Make the API call to get the user by email
        Call<User> call = userApi.findByEmail("user4@example.com");
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body();
                    if (selectedDate != null) {
                        fetchMealPlans(user.getId(), selectedDate);
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

    private void fetchMealPlans(long userId, Date selectedDate) {
        MealPlanApi mealPlanApi = RetrofitClient.getRetrofitInstance().create(MealPlanApi.class);
        Call<List<MealPlan>> callMealPlan = mealPlanApi.getDatedMealPlans(userId, selectedDate);
        callMealPlan.enqueue(new Callback<List<MealPlan>>() {
            @Override
            public void onResponse(Call<List<MealPlan>> call, Response<List<MealPlan>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<MealPlan> mealPlans = response.body();
                    int dayOfWeek = getDayOfWeekFromSelectedDate(selectedDate);
                    List<MealPlan> filteredPlans = filterByDayOfWeek(mealPlans, dayOfWeek);
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
