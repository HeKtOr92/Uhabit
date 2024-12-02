package com.example.uhabit;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitRecordDao {
    @Insert
    void insert(HabitRecord habitRecord);

    @Update
    void update(HabitRecord habitRecord);

    @Query("SELECT * FROM habit_record_table WHERE habitId = :habitId")
    LiveData<List<HabitRecord>> getHabitRecords(int habitId);

    @Query("SELECT * FROM habit_record_table WHERE habitId = :habitId AND date = :date")
    LiveData<HabitRecord> getHabitRecordByDateLiveData(int habitId, String date);

    @Query("SELECT * FROM habit_record_table WHERE habitId = :habitId AND date = :date")
    HabitRecord getTodayHabitRecord(int habitId, String date);

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN 1 ELSE 0 END FROM habit_record_table WHERE habitId = :habitId AND date = :date AND completed = 1")
    LiveData<Boolean> isHabitCompletedOnDate(int habitId, String date);

    @Query("SELECT COUNT(*) FROM habit_record_table WHERE habitId = :habitId AND completed = 1")
    int getCompletedDaysCount(int habitId);

    @Query("SELECT COUNT(*) FROM habit_record_table WHERE habitId = :habitId AND completed = 0")
    int getSkippedDaysCount(int habitId);


}
