package com.example.uhabit;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import java.time.LocalDate;

@Entity(
        tableName = "habit_record_table",
        foreignKeys = @ForeignKey(
                entity = Habit.class,
                parentColumns = "id",
                childColumns = "habitId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = "habitId")
)
public class HabitRecord {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int habitId;

    @TypeConverters(Converters.class)
    private LocalDate date;

    private boolean completed;

    public HabitRecord(int habitId, LocalDate date, boolean completed) {
        this.habitId = habitId;
        this.date = date;
        this.completed = completed;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
