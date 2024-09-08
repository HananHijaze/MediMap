package com.example.medimap;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.medimap.server.CustomerTakenPaths;
import com.example.medimap.server.CustomerTakenPathsApi;
import com.example.medimap.server.Hydration;
import com.example.medimap.server.HydrationApi;
import com.example.medimap.server.MealPlan;
import com.example.medimap.server.MealPlanApi;
import com.example.medimap.server.Pathloc;
import com.example.medimap.server.PathlocApi;
import com.example.medimap.server.RetrofitClient;
import com.example.medimap.server.StepCount;
import com.example.medimap.server.StepCountApi;
import com.example.medimap.server.User;
import com.example.medimap.server.UserApi;
import com.example.medimap.server.UserWeekday;
import com.example.medimap.server.UserWeekdayApi;
import com.example.medimap.server.UsersAllergies;
import com.example.medimap.server.UsersAllergiesApi;
import com.example.medimap.server.WorkoutPlan;
import com.example.medimap.server.WorkoutPlanApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Service {
    public static Service service;

    public Service getInstance() {
        if (service == null) {
            service = new Service();
            return service;
        } else {
            return service;
        }
    }

    public Service() {
    }

    // User API methods
    public void addUser(User user) {
        if (user != null) {
            Log.d(TAG, "Adding user: " + user.toString());

            UserApi api = RetrofitClient.getRetrofitInstance().create(UserApi.class);
            Call<User> call = api.createUser(user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "User successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "User object is null. Cannot create user.");
        }
    }

    public void deleteUser(Long id) {
        UserApi api = RetrofitClient.getRetrofitInstance().create(UserApi.class);
        Call<Void> call = api.deleteUser(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "User successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }
    // User API methods
    public void updateUser(Long id, User user) {
        if (user != null) {
            Log.d(TAG, "Updating user with ID " + id + ": " + user.toString());

            UserApi api = RetrofitClient.getRetrofitInstance().create(UserApi.class);
            Call<User> call = api.updateUser(id, user);

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "User successfully updated: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "User object is null. Cannot update user.");
        }
    }

    // Customer Taken Paths API methods
    public void addCustomerTakenPaths(CustomerTakenPaths customerTakenPaths) {
        if (customerTakenPaths != null) {
            Log.d(TAG, "Adding customer taken paths: " + customerTakenPaths.toString());

            CustomerTakenPathsApi api = RetrofitClient.getRetrofitInstance().create(CustomerTakenPathsApi.class);
            Call<CustomerTakenPaths> call = api.createCustomerTakenPath(customerTakenPaths);

            call.enqueue(new Callback<CustomerTakenPaths>() {
                @Override
                public void onResponse(Call<CustomerTakenPaths> call, Response<CustomerTakenPaths> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Customer Taken Paths successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<CustomerTakenPaths> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "CustomerTakenPaths object is null. Cannot create customer taken paths.");
        }
    }

    public void deleteCustomerTakenPath(Long id) {
        CustomerTakenPathsApi api = RetrofitClient.getRetrofitInstance().create(CustomerTakenPathsApi.class);
        Call<Void> call = api.deleteCustomerTakenPath(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Customer Taken Path successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }

    // Hydration API methods
    public void addHydration(Hydration hydration) {
        if (hydration != null) {
            Log.d(TAG, "Adding hydration: " + hydration.toString());

            HydrationApi api = RetrofitClient.getRetrofitInstance().create(HydrationApi.class);
            Call<Hydration> call = api.createHydration(hydration);

            call.enqueue(new Callback<Hydration>() {
                @Override
                public void onResponse(Call<Hydration> call, Response<Hydration> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Hydration successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Hydration> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "Hydration object is null. Cannot create hydration.");
        }
    }

    public void deleteHydration(Long id) {
        HydrationApi api = RetrofitClient.getRetrofitInstance().create(HydrationApi.class);
        Call<Void> call = api.deleteHydration(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Hydration successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }

    // Meal Plan API methods
    public void addMealPlan(MealPlan mealPlan) {
        if (mealPlan != null) {
            Log.d(TAG, "Adding meal plan: " + mealPlan.toString());

            MealPlanApi api = RetrofitClient.getRetrofitInstance().create(MealPlanApi.class);
            Call<MealPlan> call = api.createMealPlan(mealPlan);

            call.enqueue(new Callback<MealPlan>() {
                @Override
                public void onResponse(Call<MealPlan> call, Response<MealPlan> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Meal Plan successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<MealPlan> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "MealPlan object is null. Cannot create meal plan.");
        }
    }

    public void deleteMealPlan(Long id) {
        MealPlanApi api = RetrofitClient.getRetrofitInstance().create(MealPlanApi.class);
        Call<Void> call = api.deleteMealPlan(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Meal Plan successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }

    // Pathloc API methods
    public void addPath(Pathloc pathloc) {
        if (pathloc != null) {
            Log.d(TAG, "Adding path: " + pathloc.toString());

            PathlocApi api = RetrofitClient.getRetrofitInstance().create(PathlocApi.class);
            Call<Pathloc> call = api.createPath(pathloc);

            call.enqueue(new Callback<Pathloc>() {
                @Override
                public void onResponse(Call<Pathloc> call, Response<Pathloc> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Path successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<Pathloc> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "Pathloc object is null. Cannot create path.");
        }
    }

    public void deletePath(Long id) {
        PathlocApi api = RetrofitClient.getRetrofitInstance().create(PathlocApi.class);
        Call<Void> call = api.deletePath(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Path successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }

    // Step Count API methods
    public void addStepCount(StepCount stepCount) {
        if (stepCount != null) {
            Log.d(TAG, "Adding step count: " + stepCount.toString());

            StepCountApi api = RetrofitClient.getRetrofitInstance().create(StepCountApi.class);
            Call<StepCount> call = api.createStepCount(stepCount);

            call.enqueue(new Callback<StepCount>() {
                @Override
                public void onResponse(Call<StepCount> call, Response<StepCount> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Step Count successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<StepCount> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "StepCount object is null. Cannot create step count.");
        }
    }

    public void deleteStepCount(Long id) {
        StepCountApi api = RetrofitClient.getRetrofitInstance().create(StepCountApi.class);
        Call<Void> call = api.deleteStepCount(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Step Count successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }

    // User Weekday API methods
    public void addUserWeekday(UserWeekday userWeekday) {
        if (userWeekday != null) {
            Log.d(TAG, "Adding user weekday: " + userWeekday.toString());

            UserWeekdayApi api = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);
            Call<UserWeekday> call = api.createUserWeekday(userWeekday);

            call.enqueue(new Callback<UserWeekday>() {
                @Override
                public void onResponse(Call<UserWeekday> call, Response<UserWeekday> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "User Weekday successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<UserWeekday> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "UserWeekday object is null. Cannot create user weekday.");
        }
    }

    public void deleteUserWeekday(Long id) {
        UserWeekdayApi api = RetrofitClient.getRetrofitInstance().create(UserWeekdayApi.class);
        Call<Void> call = api.deleteUserWeekday(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "User Weekday successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }

    // Users Allergies API methods
    public void addUsersAllergies(UsersAllergies usersAllergies) {
        if (usersAllergies != null) {
            Log.d(TAG, "Adding users allergies: " + usersAllergies.toString());

            UsersAllergiesApi api = RetrofitClient.getRetrofitInstance().create(UsersAllergiesApi.class);
            Call<UsersAllergies> call = api.createUsersAllergies(usersAllergies);

            call.enqueue(new Callback<UsersAllergies>() {
                @Override
                public void onResponse(Call<UsersAllergies> call, Response<UsersAllergies> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Users Allergies successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<UsersAllergies> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "UsersAllergies object is null. Cannot create users allergies.");
        }
    }

    public void deleteUsersAllergies(Long id) {
        UsersAllergiesApi api = RetrofitClient.getRetrofitInstance().create(UsersAllergiesApi.class);
        Call<Void> call = api.deleteUsersAllergies(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Users Allergies successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }

    // Workout Plan API methods
    public void addWorkoutPlan(WorkoutPlan workoutPlan) {
        if (workoutPlan != null) {
            Log.d(TAG, "Adding workout plan: " + workoutPlan.toString());

            WorkoutPlanApi api = RetrofitClient.getRetrofitInstance().create(WorkoutPlanApi.class);
            Call<WorkoutPlan> call = api.createWorkoutPlan(workoutPlan);

            call.enqueue(new Callback<WorkoutPlan>() {
                @Override
                public void onResponse(Call<WorkoutPlan> call, Response<WorkoutPlan> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Log.d(TAG, "Workout Plan successfully created: " + response.body().toString());
                    } else {
                        Log.e(TAG, "Error: Response Code " + response.code());
                        Log.e(TAG, "Error body: " + response.errorBody());
                    }
                }

                @Override
                public void onFailure(Call<WorkoutPlan> call, Throwable t) {
                    Log.e(TAG, "Network failure: ", t);
                    t.printStackTrace();
                }
            });
        } else {
            Log.e(TAG, "WorkoutPlan object is null. Cannot create workout plan.");
        }
    }

    public void deleteWorkoutPlan(Long id) {
        WorkoutPlanApi api = RetrofitClient.getRetrofitInstance().create(WorkoutPlanApi.class);
        Call<Void> call = api.deleteWorkoutPlan(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Workout Plan successfully deleted.");
                } else {
                    Log.e(TAG, "Error: Response Code " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Network failure: ", t);
                t.printStackTrace();
            }
        });
    }
}
