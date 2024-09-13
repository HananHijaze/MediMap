package com.example.medimap;

import android.content.Context;
import android.util.Log;

import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.example.medimap.server.UserWeekday;
import com.example.medimap.server.UserWeekdayApi;
import com.example.medimap.server.Workout;
import com.example.medimap.server.WorkoutApi;
import com.example.medimap.server.WorkoutPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatingPlan {
    private static CreatingPlan creatingPlan;
    private List<UserWeekday> days;
    private List<Workout> workoutList;

    private CreatingPlan() {
    }

    public static CreatingPlan getInstance() {
        if (creatingPlan == null) {
            creatingPlan = new CreatingPlan();
        }
        return creatingPlan;
    }

    public void createPlan(Context context, User user) {
        // Encode the User into an encodedUser
        encodedUser encodedUser = UserDataEncoder.encodeValues(user);

        // Get the singleton instance of the model manager
        ModelManager modelManager = ModelManager.getInstance(context);

        // Use the encodedUser to make a prediction
        float[][] predictions = modelManager.createPlan(encodedUser);

        // Retrieve predictions for each output (workout, meals, etc.)
        float[] workoutPlanPredictions = predictions[0];
        float[] breakfastPredictions = predictions[1];
        float[] lunchPredictions = predictions[2];
        float[] dinnerPredictions = predictions[3];
        float[] snackPredictions = predictions[4];

        // Process the predictions (e.g., find the index with the highest probability)
        int workoutPlanIndex = argMax(workoutPlanPredictions);
        int breakfastIndex = argMax(breakfastPredictions);
        int lunchIndex = argMax(lunchPredictions);
        int dinnerIndex = argMax(dinnerPredictions);
        int snackIndex = argMax(snackPredictions);

        System.out.println("Predicted Workout Plan: " + workoutPlanIndex);
        System.out.println("Predicted Breakfast: " + breakfastIndex);
        System.out.println("Predicted Lunch: " + lunchIndex);
        System.out.println("Predicted Dinner: " + dinnerIndex);
        System.out.println("Predicted Snack: " + snackIndex);

        // Fetch workouts and user weekdays based on predictions
        getworkoutplan(user, workoutPlanIndex);
        getmealplan(user, breakfastIndex, lunchIndex, dinnerIndex, snackIndex);
    }

    public int getBreakfast() {
        return 0;
    }

    public int getLunch() {
        return 0;
    }

    public int getDinner() {
        return 0;
    }

    public int getSnack() {
        return 0;
    }


    public static int argMax(float[] probabilities) {
        int maxIndex = 0;
        for (int i = 1; i < probabilities.length; i++) {
            if (probabilities[i] > probabilities[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    /*********************************WORKOUT PLAN************************************************/

    private void getworkoutplan(User user, int workoutPlanIndex) {
        String workoutPlan = getWorkoutPlanType(workoutPlanIndex);
        int duration = getDurationBasedOnGoal(user.getGoal());

        // Fetch workouts by type
        WorkoutApi workoutApi = RetrofitClient.getRetrofitInstance().create(WorkoutApi.class);
        workoutApi.getWorkoutsByType(workoutPlan).enqueue(new Callback<List<Workout>>() {
            @Override
            public void onResponse(Call<List<Workout>> call, Response<List<Workout>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    workoutList = response.body();
                    // Now fetch user weekdays
                    fetchUserWeekdays(user, duration);
                } else {
                    Log.e("API_ERROR", "Failed to fetch workouts: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Workout>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch workouts: " + t.getMessage());
            }
        });
    }

    private String getWorkoutPlanType(int workoutPlanIndex) {
        switch (workoutPlanIndex) {
            case 0:
                return "Bodyweight Workouts";
            case 1:
                return "Calisthenics";
            case 2:
                return "Cardio Workouts";
            case 3:
                return "Circuit Training";
            case 4:
                return "Core Workouts";
            case 5:
                return "Endurance & Cardiovascular Health";
            case 6:
                return "HIIT";
            case 7:
                return "Strength Training";
            default:
                return "General Workouts";
        }
    }

    private int getDurationBasedOnGoal(String goal) {
        if (goal.equals("healthy life")) {
            return 30;
        } else if (goal.equals("lose weight")) {
            return 40;
        } else {
            return 50;
        }
    }

    private void fetchUserWeekdays(User user, int duration) {
        UserWeekdayApi userWeekdayApi = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);
        userWeekdayApi.getUserWeekdaysByUserId(user.getId()).enqueue(new Callback<List<UserWeekday>>() {
            @Override
            public void onResponse(Call<List<UserWeekday>> call, Response<List<UserWeekday>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    days = response.body();
                    // Now both workouts and user weekdays are fetched, proceed with plan creation
                    createWorkoutPlan(user, duration);
                } else {
                    Log.e("API_ERROR", "Failed to fetch user weekdays: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<List<UserWeekday>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch user weekdays: " + t.getMessage());
            }
        });
    }

    private void createWorkoutPlan(User user, int duration) {
        for (UserWeekday userWeekday : days) {
            // Select random workouts with target duration
            List<Workout> selectedWorkouts = getRandomWorkoutsWithTargetDuration(workoutList, duration);
            for (Workout workout : selectedWorkouts) {
                // Create and add WorkoutPlan
                WorkoutPlan wp = new WorkoutPlan(user.getId(), workout.getWorkoutID(), new Date(), Integer.parseInt(userWeekday.getWeekdayId().toString()));
                Service.getInstance().addWorkoutPlan(wp);
            }
        }
    }

    public List<Workout> getRandomWorkoutsWithTargetDuration(List<Workout> workoutList, int targetDuration) {
        List<Workout> selectedWorkouts = new ArrayList<>();
        int accumulatedDuration = 0;

        // Shuffle the list to randomly select workouts
        Collections.shuffle(workoutList, new Random());

        int index = 0;
        while (accumulatedDuration < targetDuration && index < workoutList.size()) {
            Workout workout = workoutList.get(index);
            int workoutDuration = workout.getDuration(); // Assuming getDuration() returns the workout duration in minutes

            // Check if adding this workout will exceed the target duration
            if (accumulatedDuration + workoutDuration <= targetDuration) {
                selectedWorkouts.add(workout);
                accumulatedDuration += workoutDuration;
            }

            // Move to the next workout in the shuffled list
            index++;
        }

        return selectedWorkouts;
    }

    public void fetchUserWeekdays(User user) {
        UserWeekdayApi userWeekdayApi = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);
        Call<List<UserWeekday>> call = userWeekdayApi.getUserWeekdaysByUserId(user.getId());

        call.enqueue(new Callback<List<UserWeekday>>() {
            @Override
            public void onResponse(Call<List<UserWeekday>> call, Response<List<UserWeekday>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    days = response.body();
                }
            }

            @Override
            public void onFailure(Call<List<UserWeekday>> call, Throwable t) {
                Log.e("API_ERROR", "Failed to fetch user weekdays: " + t.getMessage());
            }
        });
    }
    /*****************************************MEAL PLAN*************************************************/

    private void getmealplan(User user, int breakfastIndex, int lunchIndex, int dinnerIndex, int snackIndex) {
        // Add the code for fetching and processing meals here
    }
}
