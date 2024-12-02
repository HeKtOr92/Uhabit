package com.example.uhabit;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.time.LocalDate;
import java.util.List;

public class HabitViewModel extends AndroidViewModel {
    private final HabitRepository repository;
    private final LiveData<List<Habit>> allHabits;
    private final LiveData<List<Habit>> completedHabits;

    public HabitViewModel(@NonNull Application application) {
        super(application);
        repository = new HabitRepository(application);
        allHabits = repository.getAllHabits();
        completedHabits = repository.getCompletedHabits();
    }

    // Habit Operations
    public LiveData<List<Habit>> getAllHabits() {
        return allHabits;
    }

    public LiveData<Boolean> isHabitCompletedOnDate(int habitId, LocalDate date) {
        return repository.isHabitCompletedOnDate(habitId, date);
    }

    public HabitRecord getTodayHabitRecordSync(int habitId, LocalDate date) {
        String dateString = Converters.dateToTimestamp(date);
        return repository.getTodayHabitRecordSync(habitId, dateString);
    }

    public LiveData<List<Habit>> getCompletedHabits() {
        return completedHabits;
    }

    public void insert(Habit habit) {
        repository.insert(habit);
    }

    public void update(Habit habit) {
        repository.update(habit);
    }

    public void delete(Habit habit) {
        repository.delete(habit);
    }

    public Habit getHabitById(int id) {
        return repository.getHabitByIdSync(id);
    }

    // HabitRecord Operations
    public void insertHabitRecord(HabitRecord habitRecord) {
        repository.insertHabitRecord(habitRecord);
    }

    public void updateHabitRecord(HabitRecord habitRecord) {
        repository.updateHabitRecord(habitRecord);
    }

    public LiveData<HabitRecord> getTodayHabitRecord(int habitId, LocalDate date) {
        return repository.getTodayHabitRecord(habitId, date);
    }

    public LiveData<List<HabitRecord>> getHabitRecords(int habitId) {
        return repository.getHabitRecords(habitId);
    }

    // Reset daily completion
    public void resetDailyCompletion() {
        repository.resetDailyCompletion();
    }
}
