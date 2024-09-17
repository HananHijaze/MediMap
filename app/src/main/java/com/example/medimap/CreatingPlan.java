package com.example.medimap;

import android.content.Context;
import android.util.Log;

import com.example.medimap.roomdb.AppDatabaseRoom;
import com.example.medimap.roomdb.MealDao;
import com.example.medimap.roomdb.MealRoom;
import com.example.medimap.roomdb.WeeklyMealPlanRoom;
import com.example.medimap.roomdb.WeeklyMealPlanRoomDao;
import com.example.medimap.roomdb.WeeklyTrainingPlanRoom;
import com.example.medimap.roomdb.WeeklyTrainingPlanRoomDao;
import com.example.medimap.roomdb.WorkoutDao;
import com.example.medimap.roomdb.WorkoutRoom;
import com.example.medimap.server.Meal;
import com.example.medimap.server.MealApi;
import com.example.medimap.server.MealPlan;
import com.example.medimap.server.MealPlanApi;
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
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatingPlan {
    private static CreatingPlan creatingPlan;
    private List<UserWeekday> days;
    private List<Workout> workoutList;
    private AppDatabaseRoom roomdb;
    private WeeklyTrainingPlanRoomDao workoutPlanDao;
    private WeeklyMealPlanRoomDao mealPlanDao;
    private MealDao mealDao;
    private WorkoutDao workoutDao;

    private CreatingPlan() {
    }

    public static CreatingPlan getInstance() {
        if (creatingPlan == null) {
            creatingPlan = new CreatingPlan();
        }
        return creatingPlan;
    }

    public void createPlan(Context context, User user) {
        roomdb = AppDatabaseRoom.getInstance(context);
        // Initialize database and DAOs outside the background thread
         workoutPlanDao = roomdb.weeklyTrainingPlanRoomDao();
         mealPlanDao = roomdb.weeklyMealPlanRoomDao();
         mealDao = roomdb.mealDao();
         workoutDao = roomdb.workoutDao();
        deletefromroom();
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
        insertintoplans();

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

    /************WORKOUT PLAN*****************/

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
                WeeklyTrainingPlanRoom workoutPlan = new WeeklyTrainingPlanRoom(user.getId(), workout.getWorkoutID(), Integer.parseInt(userWeekday.getWeekdayId().toString()));
                new Thread(() -> {
                    workoutPlanDao.insertWorkoutPlan(workoutPlan);
                });
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
    /**************MEAL PLAN****************/

    private void getmealplan(User user, int breakfastIndex, int lunchIndex, int dinnerIndex, int snackIndex) {
        int mealsnum=user.getMealsperday();
        int snacksnum=user.getSnackesperday();

        Random random = new Random();

        if (snacksnum>0&&snackIndex==0){
            snackIndex= random.nextInt(3) + 1;
        }
        if(breakfastIndex==0||lunchIndex==0){
            breakfastIndex = random.nextInt(3) + 1;
            lunchIndex= random.nextInt(3) + 1;
        }
        if(mealsnum>2&&dinnerIndex==0){
            dinnerIndex= random.nextInt(3) + 1;
        }
        breakfastIndex--;
        lunchIndex--;
        dinnerIndex--;
        snackIndex--;

        getSnack(user,snackIndex,snacksnum);

        getBreakfast(user,breakfastIndex,1);

        if(mealsnum==4){
            getLunch(user,lunchIndex,2);
        }else {
            getLunch(user, lunchIndex, 1);
        }

        if(mealsnum>=3){
            getDinner(user,dinnerIndex,1);
        }



    }

    public void getBreakfast(User user, int breakfastIndex, int breakfastNumber) {
        MealApi mealsApi = RetrofitClient.getRetrofitInstance().create(MealApi.class);
        Call<List<Meal>> call = mealsApi.getMealsByTypeAndCluster("Breakfast", breakfastIndex);

        call.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                if (response.isSuccessful()) {
                    List<Meal> meals = response.body();
                    for (int j = 1; j <= 7; j++) {
                        for (int i = 0; i < breakfastNumber; i++) {
                            MealPlan mp = new MealPlan(user.getId(), getRandomMealByDietType(meals, user.getDietType()), new Date(), j, "Breakfast");
                            Service.getInstance().addMealPlan(mp);
                            WeeklyMealPlanRoom mealPlan = new WeeklyMealPlanRoom(user.getId(), getRandomMealByDietType(meals, user.getDietType()), j, "Breakfast");
                            new Thread(() -> {
                                mealPlanDao.insertMealPlan(mealPlan);
                            });
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Meal>> call, Throwable t) {
                // Handle failure
            }
        });
    }


    public void getLunch(User user, int lunchIndex, int lunchNumber) {
        MealApi mealsApi = RetrofitClient.getRetrofitInstance().create(MealApi.class);
        Call<List<Meal>> call = mealsApi.getMealsByTypeAndCluster("Lunch", lunchIndex);

        call.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                if (response.isSuccessful()) {
                    List<Meal> meals = response.body();
                    for (int j = 1; j <= 7; j++) {
                        for (int i = 0; i < lunchNumber; i++) {
                            MealPlan mp = new MealPlan(user.getId(), getRandomMealByDietType(meals, user.getDietType()), new Date(), j, "Lunch");
                            Service.getInstance().addMealPlan(mp);
                            WeeklyMealPlanRoom mealPlan = new WeeklyMealPlanRoom(user.getId(), getRandomMealByDietType(meals, user.getDietType()), j, "Lunch");
                            new Thread(() -> {
                                mealPlanDao.insertMealPlan(mealPlan);
                            });

                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Meal>> call, Throwable t) {
                // Handle failure
            }
        });
    }


    public void getDinner(User user, int dinnerIndex, int dinnerNumber) {
        MealApi mealsApi = RetrofitClient.getRetrofitInstance().create(MealApi.class);
        Call<List<Meal>> call = mealsApi.getMealsByTypeAndCluster("Dinner", dinnerIndex);

        call.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                if (response.isSuccessful()) {
                    List<Meal> meals = response.body();
                    for (int j = 1; j <=7; j++) {
                        for (int i = 0; i < dinnerNumber; i++) {
                            MealPlan mp = new MealPlan(user.getId(), getRandomMealByDietType(meals, user.getDietType()), new Date(), j, "Dinner");
                            Service.getInstance().addMealPlan(mp);
                            WeeklyMealPlanRoom mealPlan = new WeeklyMealPlanRoom(user.getId(), getRandomMealByDietType(meals, user.getDietType()), j, "Dinner");
                            new Thread(() -> {
                                mealPlanDao.insertMealPlan(mealPlan);
                            });
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Meal>> call, Throwable t) {
                // Handle failure
            }
        });
    }


    public void getSnack(User user, int snackIndex,int snacknumber) {

        MealApi mealsApi = RetrofitClient.getRetrofitInstance().create(MealApi.class);
        Call<List<Meal>> call = mealsApi.getMealsByTypeAndCluster("Snack", snackIndex);

        call.enqueue(new Callback<List<Meal>>() {
            @Override
            public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
                if (response.isSuccessful()) {
                    List<Meal> meals = response.body();
                    for (int j = 1; j <=7; j++) {
                        for (int i = 0; i < snacknumber; i++) {
                            MealPlan mp =new MealPlan(user.getId(),getRandomMealByDietType(meals,user.getDietType()),new Date(),j,"Snack");
                            Service.getInstance().addMealPlan(mp);
                            WeeklyMealPlanRoom mealPlan = new WeeklyMealPlanRoom(user.getId(),getRandomMealByDietType(meals,user.getDietType()),j,"Snack");
                            new Thread(() -> {
                                mealPlanDao.insertMealPlan(mealPlan);
                            });
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Meal>> call, Throwable t) {
            }
        });
    }

    public Long getRandomMealByDietType(List<Meal> meals, String dietType) {
        List<Meal> filteredMeals = meals.stream()
                .filter(meal -> dietType.equals(meal.getDiet_type()))
                .collect(Collectors.toList());

        if (!filteredMeals.isEmpty()) {
            // Return a random meal from the filtered list
            Random random = new Random();
            return filteredMeals.get(random.nextInt(filteredMeals.size())).getMealID();
        } else {
            // If no meals match the diet type, return a random meal from the entire list
            Random random = new Random();
            return meals.get(random.nextInt(meals.size())).getMealID();
        }
    }
    private void deletefromroom(){
        // Perform database operations inside a background thread
        Executor executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            // Delete all entries in the background thread
            workoutPlanDao.deleteAllWorkoutPlans();
            mealPlanDao.deleteAllMealPlans();
            mealDao.deleteAllMealPlans();
            workoutDao.deletallWorkouts();
        });

    }
    private void insertintoplans(){
        new Thread(() -> {
            List<WeeklyTrainingPlanRoom> traininglist= workoutPlanDao.getAllWorkoutPlans();
            for(WeeklyTrainingPlanRoom training:traininglist){
                WorkoutApi workoutApi = RetrofitClient.getRetrofitInstance().create(WorkoutApi.class);
                Call<Workout> call = workoutApi.getWorkoutById(training.getWorkoutID());
                call.enqueue(new Callback<Workout>() {
                    @Override
                    public void onResponse(Call<Workout> call, Response<Workout> response) {
                        Workout workout=response.body();
                        WorkoutRoom workoutRoom=new WorkoutRoom(workout.getName(),workout.getDescription(),workout.getDuration(),workout.getRepetitions(),workout.getSets(),workout.getLocation(),workout.getWorkoutType());
                        workoutDao.insertWorkout(workoutRoom);
                    }

                    @Override
                    public void onFailure(Call<Workout> call, Throwable t) {

                    }
                });
            }
        }).start();
        new Thread(() -> {
            List<WeeklyMealPlanRoom> meallist= mealPlanDao.getAllMealPlans();
            for(WeeklyMealPlanRoom meal:meallist){
                MealApi mealsApi = RetrofitClient.getRetrofitInstance().create(MealApi.class);
                Call<Meal>call = mealsApi.getMealById(meal.getMealID());
                call.enqueue(new Callback<Meal>(){
                    @Override
                    public void onResponse(Call<Meal> call, Response<Meal> response) {
                        Meal meal=response.body();
                        MealRoom mealRoom=new MealRoom(meal.getDiet_type(),meal.getCalories(),meal.getCarbs(),meal.getFats(),meal.getName(),meal.getProtein(),meal.getType());
                        mealDao.insertMeal(mealRoom);
                }

                    @Override
                    public void onFailure(Call<Meal> call, Throwable t) {

                    }
                });
            }
        }).start();
    }
}
