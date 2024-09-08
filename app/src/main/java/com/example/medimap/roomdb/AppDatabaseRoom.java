package com.example.medimap.roomdb;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {UserRoom.class, HydrationRoom.class, WeeklyMealPlanRoom.class, WeeklyTrainingPlanRoom.class}, version = 1)
@TypeConverters(Converters.class)  // Registering the Converters class
public abstract class AppDatabaseRoom extends RoomDatabase {

    // Static instance of the AppDatabase class
    private static AppDatabaseRoom INSTANCE;

    // DAOs
    public abstract UserDao userDao();
    public abstract HydrationRoomDao hydrationRoomDao();
    public abstract WeeklyMealPlanRoomDao weeklyMealPlanRoomDao();
    public abstract WeeklyTrainingPlanRoomDao weeklyTrainingPlanRoomDao();
    public abstract StepCountDao stepCountDao();

    // Singleton implementation to get the database
    public static synchronized AppDatabaseRoom getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabaseRoom.class, "zoll_database")
                    .fallbackToDestructiveMigration() // Handles migration (optional)
                    .build();
        }
        return INSTANCE;
    }
}
