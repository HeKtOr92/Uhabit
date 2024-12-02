package com.example.uhabit;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.PendingIntent;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class SecondActivity2 extends AppCompatActivity implements HabitAdapter.OnHabitInteractionListener {

    private HabitViewModel habitViewModel;
    private HabitAdapter habitAdapter;

    private static final String PREFS_NAME = "UhabitPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second2);

        // Initialize ViewModel
        habitViewModel = new ViewModelProvider(this).get(HabitViewModel.class);

        // Get references to UI components
        TextView welcomeTextView = findViewById(R.id.thewelcometext);
        RecyclerView habitsRecyclerView = findViewById(R.id.habitsRecyclerView);
        Button addHabitButton = findViewById(R.id.addHabitButton);
        Button changeUsernameButton = findViewById(R.id.changeUsernameButton);
        Button habitHistoryButton = findViewById(R.id.habitHistoryButton);

        // Retrieve the stored username
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "User");
        welcomeTextView.setText(getString(R.string.welcome_message, username));

        // Set up RecyclerView
        habitAdapter = new HabitAdapter(new ArrayList<>(), habit -> {
            // Navigate to HabitProfileActivity
            Intent intent = new Intent(SecondActivity2.this, HabitProfileActivity.class);
            intent.putExtra("habitId", habit.getId());
            startActivity(intent);
        }, this); // Pass 'this' as the interaction listener

        habitsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        habitsRecyclerView.setAdapter(habitAdapter);

        // Observe LiveData
        habitViewModel.getAllHabits().observe(this, habits -> {
            if (habitAdapter != null) {
                habitAdapter.setHabits(habits);
            }
        });

        // Add habit button listener
        addHabitButton.setOnClickListener(v -> showAddHabitDialog());

        // Change Username Button Listener
        changeUsernameButton.setOnClickListener(v -> showChangeUsernameDialog(sharedPreferences, welcomeTextView));

        // Habit History Button Listener
        habitHistoryButton.setOnClickListener(v -> {
            Intent intent = new Intent(SecondActivity2.this, ThirdActivity3.class);
            startActivity(intent);
        });
    }

    private void showAddHabitDialog() {
        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_habit, null);
        builder.setView(dialogView);

        // Create the AlertDialog first to use it later
        AlertDialog alertDialog = builder.create();

        // Get references to the dialog's UI elements
        EditText habitNameEditText = dialogView.findViewById(R.id.habitNameEditText);
        EditText habitExplanationEditText = dialogView.findViewById(R.id.habitExplanationEditText);
        EditText startDateEditText = dialogView.findViewById(R.id.startDateEditText);
        EditText reminderTimeEditText = dialogView.findViewById(R.id.reminderTimeEditText);
        Spinner categorySpinner = dialogView.findViewById(R.id.categorySpinner);
        Spinner frequencySpinner = dialogView.findViewById(R.id.frequencySpinner);
        EditText durationEditText = dialogView.findViewById(R.id.durationEditText);
        Button saveHabitButton = dialogView.findViewById(R.id.saveHabitButton);

        // Show date picker when clicking on Start Date
        startDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> startDateEditText.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth)),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Show time picker when clicking on Reminder Time
        reminderTimeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (view, hourOfDay, minute) -> reminderTimeEditText.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    DateFormat.is24HourFormat(this)
            );
            timePickerDialog.show();
        });

        // Set Save Button Behavior
        saveHabitButton.setOnClickListener(v -> {
            String name = habitNameEditText.getText().toString();
            String explanation = habitExplanationEditText.getText().toString();
            String startDate = startDateEditText.getText().toString();
            String reminderTime = reminderTimeEditText.getText().toString();
            String category = categorySpinner.getSelectedItem().toString();
            String frequency = frequencySpinner.getSelectedItem().toString();
            String durationText = durationEditText.getText().toString();

            if (name.isEmpty() || startDate.isEmpty() || reminderTime.isEmpty() || durationText.isEmpty()) {
                Toast.makeText(this, "Please fill out all required fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration = Integer.parseInt(durationText);

            Habit habit = new Habit(
                    name,
                    explanation,
                    LocalDate.parse(startDate),
                    reminderTime,
                    category,
                    frequency,
                    0, // Goal
                    duration
            );

            // Insert habit into the database
            habitViewModel.insert(habit);
            Toast.makeText(this, "Habit added successfully!", Toast.LENGTH_SHORT).show();

            // Schedule the alarm for the habit reminder
            scheduleHabitReminder(habit);

            // Close the dialog
            alertDialog.dismiss();
        });

        // Show the dialog
        alertDialog.show();
    }

    private void scheduleHabitReminder(Habit habit) {
        if (habit.getReminderTime() == null || habit.getReminderTime().isEmpty()) {
            Toast.makeText(this, "Reminder time is not set for this habit.", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] timeParts = habit.getReminderTime().split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Schedule for the next day if the time has passed
        }

        // Intent for the reminder
        Intent intent = new Intent(this, ReminderBroadcastReceiver.class);
        intent.putExtra("habitId", habit.getId());
        intent.putExtra("habitName", habit.getName());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                habit.getId(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );
            Toast.makeText(this, "Reminder set for " + habit.getReminderTime(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to set reminder.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showChangeUsernameDialog(SharedPreferences sharedPreferences, TextView welcomeTextView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_username, null);
        builder.setView(dialogView);

        EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        Button saveUsernameButton = dialogView.findViewById(R.id.saveUsernameButton);

        // Create the AlertDialog instance
        AlertDialog alertDialog = builder.create();

        saveUsernameButton.setOnClickListener(v -> {
            String newUsername = usernameEditText.getText().toString();
            if (newUsername.isEmpty()) {
                Toast.makeText(this, "Please enter a valid username!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save the new username in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("username", newUsername);
            editor.apply();

            // Update the welcome text
            welcomeTextView.setText(getString(R.string.welcome_message, newUsername));
            Toast.makeText(this, "Username updated successfully!", Toast.LENGTH_SHORT).show();

            // Dismiss the dialog
            alertDialog.dismiss();
        });

        // Show the dialog
        alertDialog.show();
    }

    @Override
    public void onDailyCheckChanged(Habit habit, boolean isChecked) {
        // Check if the habit is already marked as completed for today
        new Thread(() -> {
            LocalDate today = LocalDate.now();
            HabitRecord todayRecord = habitViewModel.getTodayHabitRecordSync(habit.getId(), today);

            if (isChecked) {
                if (todayRecord == null) {
                    // Add a new record only if it doesn't already exist
                    HabitRecord newRecord = new HabitRecord(habit.getId(), today, true);
                    habitViewModel.insertHabitRecord(newRecord);

                    // Update completion count
                    habit.setCompletionCount(habit.getCompletionCount() + 1);
                    habit.setDailyCompleted(true);
                    habitViewModel.update(habit);
                }
            } else {
                if (todayRecord != null && todayRecord.isCompleted()) {
                    // Update the record to uncompleted
                    todayRecord.setCompleted(false);
                    habitViewModel.updateHabitRecord(todayRecord);

                    // Decrease the completion count
                    habit.setCompletionCount(habit.getCompletionCount() - 1);
                    habit.setDailyCompleted(false);
                    habitViewModel.update(habit);
                }
            }
        }).start();
    }

    @Override
    public void onHabitCompleted(Habit habit) {
        new Thread(() -> {
            HabitRecord record = habitViewModel.getTodayHabitRecordSync(habit.getId(), LocalDate.now());
            if (record == null) {
                // Insert a new record
                HabitRecord newRecord = new HabitRecord(habit.getId(), LocalDate.now(), true);
                habitViewModel.insertHabitRecord(newRecord);
            } else {
                record.setCompleted(true);
                habitViewModel.updateHabitRecord(record);
            }

            // Update habit as completed
            habit.setCompleted(true);
            habitViewModel.update(habit);

            runOnUiThread(() -> {
                Toast.makeText(this, "Habit marked as completed!", Toast.LENGTH_SHORT).show();
                habitAdapter.notifyDataSetChanged();
            });
        }).start();
    }
}
