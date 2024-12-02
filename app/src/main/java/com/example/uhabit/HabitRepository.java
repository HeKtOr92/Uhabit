package com.example.uhabit;

import android.content.Context;

import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HabitRepository {
    private final HabitDao habitDao;
    private final HabitRecordDao habitRecordDao;
    private final LiveData<List<Habit>> allHabits;
    private final LiveData<List<Habit>> completedHabits;
    private final ExecutorService executorService;

    public HabitRepository(Context context) {
        HabitDatabase db = HabitDatabase.getInstance(context.getApplicationContext());
        habitDao = db.habitDao();
        habitRecordDao = db.habitRecordDao();
        allHabits = habitDao.getAllHabits();
        completedHabits = habitDao.getCompletedHabits();
        executorService = Executors.newFixedThreadPool(4);
    }

    // Habit Operations
    public LiveData<List<Habit>> getAllHabits() {
        return allHabits;
    }

    public LiveData<List<Habit>> getCompletedHabits() {
        return completedHabits;
    }

    public void insert(Habit habit) {
        executorService.execute(() -> habitDao.insert(habit));
    }

    public void update(Habit habit) {
        executorService.execute(() -> habitDao.update(habit));
    }

    public void delete(Habit habit) {
        executorService.execute(() -> habitDao.delete(habit));
    }

    public Habit getHabitByIdSync(int id) {
        return habitDao.getHabitById(id);
    }

    // HabitRecord Operations
    public void insertHabitRecord(HabitRecord habitRecord) {
        executorService.execute(() -> habitRecordDao.insert(habitRecord));
    }

    public void updateHabitRecord(HabitRecord habitRecord) {
        executorService.execute(() -> habitRecordDao.update(habitRecord));
    }

    public HabitRecord getTodayHabitRecordSync(int habitId, String date) {
        return habitRecordDao.getTodayHabitRecord(habitId, date);
    }

    public LiveData<HabitRecord> getTodayHabitRecord(int habitId, LocalDate date) {
        String dateString = Converters.dateToTimestamp(date);
        return habitRecordDao.getHabitRecordByDateLiveData(habitId, dateString);
    }

    public LiveData<List<HabitRecord>> getHabitRecords(int habitId) {
        return habitRecordDao.getHabitRecords(habitId);
    }

    public LiveData<Boolean> isHabitCompletedOnDate(int habitId, LocalDate date) {
        String dateString = Converters.dateToTimestamp(date);
        return habitRecordDao.isHabitCompletedOnDate(habitId, dateString);
    }

    // Reset daily completion
    public void resetDailyCompletion() {
        executorService.execute(() -> {
            List<Habit> habits = habitDao.getAllHabitsSync();
            for (Habit habit : habits) {
                habit.setDailyCompleted(false);
                habitDao.update(habit);
            }
        });
    }
}
