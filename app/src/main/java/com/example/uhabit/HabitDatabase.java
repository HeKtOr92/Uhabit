package com.example.uhabit;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Habit.class, HabitRecord.class}, version = 5, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class HabitDatabase extends RoomDatabase {

    private static HabitDatabase instance;

    public abstract HabitDao habitDao();
    public abstract HabitRecordDao habitRecordDao();

    public static synchronized HabitDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            HabitDatabase.class, "habit_database")
                    .fallbackToDestructiveMigration() // Implement proper migration in production
                    .build();
        }
        return instance;
    }
}
