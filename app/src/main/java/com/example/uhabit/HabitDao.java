package com.example.uhabit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitDao {
    @Insert
    void insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("SELECT * FROM habit_table WHERE completed = 0 ORDER BY id ASC")
    LiveData<List<Habit>> getAllHabits();

    @Query("SELECT * FROM habit_table WHERE completed = 1 ORDER BY id ASC")
    LiveData<List<Habit>> getCompletedHabits();

    @Query("SELECT * FROM habit_table WHERE completed = 0")
    List<Habit> getAllHabitsSync();

    @Query("SELECT * FROM habit_table WHERE id = :id")
    Habit getHabitById(int id);
}
