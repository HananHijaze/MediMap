package com.example.medimap;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;  // Import the Button class
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.UserDao;
import com.example.medimap.roomdb.WeeklyMealPlanRoom;
import com.example.medimap.server.Meal;
import com.example.medimap.server.MealApi;
import com.example.medimap.server.MealPlan;
import com.example.medimap.server.MealPlanApi;
import com.example.medimap.server.RetrofitClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> meals;
    private OnMealClickListener onMealClickListener;

    // Interface for handling Edit button clicks
    public interface OnMealClickListener {
        void onEditButtonClick(Meal meal); // Changed to handle only Edit button click
    }

    // Constructor
    public MealAdapter(List<Meal> meals, OnMealClickListener listener) {
        this.meals = meals;
        this.onMealClickListener = listener;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.meal_card, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        Meal meal = meals.get(position);

        // Set meal details
        holder.nameTextView.setText(meal.getName());
        holder.typeTextView.setText(meal.getType());
        holder.caloriesTextView.setText("" + meal.getCalories());
        holder.carbsTextView.setText("" + meal.getCarbs());
        holder.fatsTextView.setText("" + meal.getFats());
        holder.proteinTextView.setText("" + meal.getProtein());

        // Set the image based on the meal type
        switch (meal.getType().toLowerCase()) {
            case "breakfast":
                holder.mealImageView.setImageResource(R.drawable.breakfast); // Use breakfast.jpeg
                break;
            case "lunch":
                holder.mealImageView.setImageResource(R.drawable.lunch); // Use lunch.jpeg
                break;
            case "dinner":
                holder.mealImageView.setImageResource(R.drawable.dinner); // Use dinner.jpeg
                break;
            case "snack":
                holder.mealImageView.setImageResource(R.drawable.snack); // Use snack.jpeg
                break;
        }

        // Set click listener for the Editb button to handle meal ID storage or retrieval
        holder.editButton.setOnClickListener(v -> {
            if (onMealClickListener != null) {
                MealApi mealsApi = RetrofitClient.getRetrofitInstance().create(MealApi.class);
                Call<List<Meal>> call = mealsApi.getMealsByTypeAndCluster(meal.getType(),meal.getCluster());

                call.enqueue(new Callback<List<Meal>>() {
                    @Override
                    public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                        if (response.isSuccessful()) {
                            List<Meal> mealsuggest = response.body();
                            Collections.shuffle(mealsuggest);
                            List<Meal> randomMeals = mealsuggest.subList(0, Math.min(5, mealsuggest.size()));
                            showEditMealDialog(holder.itemView.getContext(), meal, randomMeals);
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Meal>> call, Throwable t) {
                        // Handle failure
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    // ViewHolder class for managing meal views
    public static class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, typeTextView, caloriesTextView, carbsTextView, fatsTextView, proteinTextView;
        public ImageView mealImageView;
        public ImageButton editButton;  // Add Edit button

        public MealViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.mealname);
            typeTextView = itemView.findViewById(R.id.mealtype);
            caloriesTextView = itemView.findViewById(R.id.mealcalories);
            carbsTextView = itemView.findViewById(R.id.mealcarbs);
            fatsTextView = itemView.findViewById(R.id.mealfats);
            proteinTextView = itemView.findViewById(R.id.mealprotein);
            mealImageView = itemView.findViewById(R.id.meal_image);
            editButton = itemView.findViewById(R.id.Editb); // Reference to Edit button (Editb)
        }
    }
    private void showEditMealDialog(Context context, Meal mealToChange, List<Meal> suggestions) {
        // Create a dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_change_meal); // Ensure this matches the layout name
        dialog.setCancelable(true);

        List<Meal> m = new ArrayList<>();
        m.add(mealToChange);

        // Get references to the views
        RecyclerView mealToChangeview= dialog.findViewById(R.id.mealtochange);
        mealToChangeview.setLayoutManager(new LinearLayoutManager(context));
        MealSuggestionAdapter adapter1 = new MealSuggestionAdapter(context, m, meal -> {
        });
        mealToChangeview.setAdapter(adapter1);

        // Set up RecyclerView and Adapter
        RecyclerView mealRecyclerView = dialog.findViewById(R.id.mealRecyclerView);
        mealRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        MealSuggestionAdapter adapter = new MealSuggestionAdapter(context, suggestions, meal -> {
            UserDao userDao = AppDatabaseRoom.getInstance(context).userDao();
            new Thread(() -> {
                MealPlanApi mealPlanApi = RetrofitClient.getRetrofitInstance().create(MealPlanApi.class);
                Long userId = userDao.getAllUsers().get(0).getId();
                Call<List<MealPlan>>calls = mealPlanApi.getMealPlansByCustomerAndMeal(userId,mealToChange.getMealID());
                calls.enqueue(new Callback<List<MealPlan>>() {
                                  @Override
                                  public void onResponse(Call<List<MealPlan>> call, Response<List<MealPlan>> response) {
                                      if (response.isSuccessful()) {
                                          List<MealPlan> mealPlans = response.body();
                                            for(MealPlan mp : mealPlans){
//                                                Date creationDate = mp.getCreationdate();
//                                                Calendar calendar = Calendar.getInstance();
//                                                calendar.setTime(creationDate);
//                                                calendar.add(Calendar.HOUR, 12); // Add 12 hours
//                                                Date newDate = calendar.getTime();
                                                MealPlan m =new MealPlan(userId,meal.getMealID(),mp.getCreationdate()                                                                       ,mp.getMealDay(),mp.getMealTime());
                                                Call<MealPlan> call1 = mealPlanApi.updateMealPlan(mp.getId(),m);
                                                call1.enqueue(new Callback<MealPlan>() {
                                                    @Override
                                                    public void onResponse(Call<MealPlan> call, Response<MealPlan> response) {
                                                        //Toast.makeText(context, "Meal changed successfully", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onFailure(Call<MealPlan> call, Throwable t) {
                                                        Log.e("MealPlanApi", "Error updating meal plan: " + t.getMessage());
                                                        Toast.makeText(context, "Failed to change meal", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                      }

                                  }

                                  @Override
                                  public void onFailure(Call<List<MealPlan>> call, Throwable t) {

                                  }
                              }
                );




            }).start();


            dialog.dismiss();
        });
        mealRecyclerView.setAdapter(adapter);

        // Handle cancel and apply buttons
        Button cancelButton = dialog.findViewById(R.id.buttonCancel);
        cancelButton.setOnClickListener(v -> dialog.dismiss());

        Button applyButton = dialog.findViewById(R.id.buttonApply);
        applyButton.setOnClickListener(v -> {
            // Handle apply action
            dialog.dismiss();
        });

        // Show the dialog
        dialog.show();
    }

}
