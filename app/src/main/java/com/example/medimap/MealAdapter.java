package com.example.medimap;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.example.medimap.server.Meal;

import java.util.List;


public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {
    private List<Meal> meals;

    public MealAdapter(List<Meal> meals) {
        this.meals = meals;
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
        holder.nameTextView.setText(meal.getName());
        holder.typeTextView.setText(meal.getType());
        holder.dietTypeTextView.setText(meal.getDiet_type());
        holder.caloriesTextView.setText("Calories: " + meal.getCalories());
        holder.carbsTextView.setText("Carbs: " + meal.getCarbs());
        holder.fatsTextView.setText("Fats: " + meal.getFats());
        holder.proteinTextView.setText("Protein: " + meal.getProtein());
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    public static class MealViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView, typeTextView, dietTypeTextView, caloriesTextView, carbsTextView, fatsTextView, proteinTextView;

        public MealViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.meal_name);
            typeTextView = itemView.findViewById(R.id.meal_type);
            dietTypeTextView = itemView.findViewById(R.id.meal_diet_type);
            caloriesTextView = itemView.findViewById(R.id.meal_calories);
            carbsTextView = itemView.findViewById(R.id.meal_carbs);
            fatsTextView = itemView.findViewById(R.id.meal_fats);
            proteinTextView = itemView.findViewById(R.id.meal_protein);
        }
    }
}
