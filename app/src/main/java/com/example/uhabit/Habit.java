package com.example.uhabit;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.time.LocalDate;

@Entity(tableName = "habit_table")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String explanation;

    @TypeConverters(Converters.class)
    private LocalDate startDate;

    private boolean completed = false;

    private String reminderTime; // e.g., "08:00"
    private String category;
    private String frequency; // e.g., "Daily", "Weekly"

    private int progress = 0; // Default to 0
    private int goal;

    private int duration; // Number of days to remind the user
    private boolean dailyCompleted = false; // Default to false

    private int completionCount = 0; // Default to 0

    public Habit() {}

    @Ignore
    public Habit(String name, String explanation, LocalDate startDate, String reminderTime, String category, String frequency, int goal, int duration) {
        this.name = name;
        this.explanation = explanation;
        this.startDate = startDate;
        this.reminderTime = reminderTime;
        this.category = category;
        this.frequency = frequency;
        this.goal = goal;
        this.progress = 0;
        this.duration = duration;
        this.dailyCompleted = false;
        this.completionCount = 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isDailyCompleted() {
        return dailyCompleted;
    }

    public void setDailyCompleted(boolean dailyCompleted) {
        this.dailyCompleted = dailyCompleted;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    public void setCompletionCount(int completionCount) {
        this.completionCount = completionCount;
    }

    public int getCompletionPercentage() {
        return goal == 0 ? 0 : (progress * 100) / goal; // Avoid division by zero
    }
}
