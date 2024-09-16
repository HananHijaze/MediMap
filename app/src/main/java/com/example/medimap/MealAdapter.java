package com.example.medimap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;  // Import the Button class

import androidx.recyclerview.widget.RecyclerView;

import com.example.medimap.server.Meal;

import java.util.List;

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
                onMealClickListener.onEditButtonClick(meal); // Triggered only when Edit button is clicked
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
}
