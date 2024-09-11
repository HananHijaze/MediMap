package com.example.medimap;

import android.content.Context;

import com.example.medimap.server.User;

public class CreatingPlan {
    private static CreatingPlan creatingPlan;

    private CreatingPlan() {
    }

    public static CreatingPlan getInstance() {
        if (creatingPlan == null) {
            creatingPlan = new CreatingPlan();
        }
        return creatingPlan;
    }

    public void createPlan(Context context, User user) {
        // Step 1: Encode the User into an encodedUser
        encodedUser encodedUser = UserDataEncoder.encodeValues(user);

        // Step 2: Get the singleton instance of the model manager
        ModelManager modelManager = ModelManager.getInstance(context);

        // Step 3: Use the encodedUser to make a prediction
        float[][] predictions = modelManager.createPlan(encodedUser);

        // Step 4: Retrieve predictions for each output (workout, meals, etc.)
        float[] workoutPlanPredictions = (float[]) predictions[0];
        float[] breakfastPredictions = (float[]) predictions[1];
        float[] lunchPredictions = (float[]) predictions[2];
        float[] dinnerPredictions = (float[]) predictions[3];
        float[] snackPredictions = (float[]) predictions[4];

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

    public int getWorkout() {
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

}
