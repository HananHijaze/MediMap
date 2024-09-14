package com.example.medimap;

import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medimap.roomdb.MealDao;
import com.example.medimap.roomdb.WeeklyMealPlanRoom;
import com.example.medimap.roomdb.MealRoom;

import java.util.List;

public class MealPlanAdapter extends RecyclerView.Adapter<MealPlanAdapter.MealPlanViewHolder> {

    private List<WeeklyMealPlanRoom> mealPlans;
    private MealDao mealDao;  // Add MealDao reference

    public MealPlanAdapter(List<WeeklyMealPlanRoom> mealPlans, MealDao mealDao) {
        this.mealPlans = mealPlans;
        this.mealDao = mealDao;
    }

    @NonNull
    @Override
    public MealPlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.meal_card, parent, false);
        return new MealPlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealPlanViewHolder holder, int position) {
        WeeklyMealPlanRoom mealPlan = mealPlans.get(position);

        // Fetch the meal using the meal ID asynchronously
        new GetMealByIdTask(holder).execute(mealPlan.getMealID());
    }

    @Override
    public int getItemCount() {
        return mealPlans.size();
    }

    public static class MealPlanViewHolder extends RecyclerView.ViewHolder {
        TextView mealName, dietType, calories, carbs, fats, protein;

        public MealPlanViewHolder(@NonNull View itemView) {
            super(itemView);
            mealName = itemView.findViewById(R.id.mealname);
            calories = itemView.findViewById(R.id.mealcalories);
            carbs = itemView.findViewById(R.id.mealcarbs);
            fats = itemView.findViewById(R.id.mealfats);
            protein = itemView.findViewById(R.id.mealprotein);
        }

        // This method will be called after meal is retrieved
        public void bindMealData(MealRoom meal) {
            mealName.setText(meal.getMealName());
            calories.setText(String.valueOf(meal.getCalories()));
            carbs.setText(String.valueOf(meal.getCarbs()));
            fats.setText(String.valueOf(meal.getFats()));
            protein.setText(String.valueOf(meal.getProtein()));
        }
    }

    // AsyncTask to retrieve the meal from the database
    private class GetMealByIdTask extends AsyncTask<Long, Void, MealRoom> {
        private MealPlanViewHolder holder;

        public GetMealByIdTask(MealPlanViewHolder holder) {
            this.holder = holder;
        }

        @Override
        protected MealRoom doInBackground(Long... mealIds) {
            return mealDao.getMealById(mealIds[0]);
        }

        @Override
        protected void onPostExecute(MealRoom meal) {
            if (meal != null) {
                holder.bindMealData(meal); // Bind meal data to the ViewHolder
            }
        }
    }
}
