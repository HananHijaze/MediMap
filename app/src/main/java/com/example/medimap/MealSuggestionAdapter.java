package com.example.medimap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medimap.server.Meal;

import java.util.List;

public class MealSuggestionAdapter extends RecyclerView.Adapter<MealSuggestionAdapter.ViewHolder> {
    private final Context context;
    private final List<Meal> mealSuggestions;
    private final OnMealSelectedListener listener;

    // Interface for handling the meal selection
    public interface OnMealSelectedListener {
        void onMealSelected(Meal meal);
    }

    public MealSuggestionAdapter(Context context, List<Meal> mealSuggestions, OnMealSelectedListener listener) {
        this.context = context;
        this.mealSuggestions = mealSuggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.meal_card_2, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Meal meal = mealSuggestions.get(position);

        // Bind meal data to views
        holder.mealNameTextView.setText(meal.getName());
        holder.mealTypeTextView.setText(meal.getType()); // Assuming meal.getType() returns a string like "Breakfast"
        holder.mealCaloriesTextView.setText(meal.getCalories()+"");
        holder.mealCarbsTextView.setText(meal.getCarbs()+"" );
        holder.mealFatsTextView.setText(meal.getFats()+"");
        holder.mealProteinTextView.setText(meal.getProtein() +"");

        // Update meal image based on the meal type (e.g., breakfast, lunch, etc.)
        switch (meal.getType().toLowerCase()) {
            case "breakfast":
                holder.mealImageView.setImageResource(R.drawable.breakfast); // Make sure you have a drawable for each type
                break;
            case "lunch":
                holder.mealImageView.setImageResource(R.drawable.lunch);
                break;
            case "dinner":
                holder.mealImageView.setImageResource(R.drawable.dinner);
                break;
            case "snack":
                holder.mealImageView.setImageResource(R.drawable.snack);
                break;

        }

        // Set click listener for selecting a meal
        holder.itemView.setOnClickListener(v -> listener.onMealSelected(meal));
    }

    @Override
    public int getItemCount() {
        return mealSuggestions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView mealNameTextView, mealTypeTextView, mealCaloriesTextView, mealCarbsTextView, mealFatsTextView, mealProteinTextView;
        ImageView mealImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mealNameTextView = itemView.findViewById(R.id.mealname);
            mealTypeTextView = itemView.findViewById(R.id.mealtype);
            mealCaloriesTextView = itemView.findViewById(R.id.mealcalories);
            mealCarbsTextView = itemView.findViewById(R.id.mealcarbs);
            mealFatsTextView = itemView.findViewById(R.id.mealfats);
            mealProteinTextView = itemView.findViewById(R.id.mealprotein);
            mealImageView = itemView.findViewById(R.id.meal_image);
        }
    }
}
