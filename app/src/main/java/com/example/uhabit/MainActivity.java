package com.example.uhabit;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "UhabitPrefs";
    private static final String FIRST_LAUNCH_KEY = "firstLaunch";
    private static final String ALARM_PERMISSION_PROMPTED_KEY = "alarmPermissionPrompted";
    private static final String KEY_USERNAME = "username";
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 101;
    private static final int ALARM_PERMISSION_REQUEST_CODE = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permissions on first launch
        checkPermissionsOnFirstLaunch();

        // UI Components
        EditText usernameEditText = findViewById(R.id.usernameEditText);
        Button startButton = findViewById(R.id.StartButton);

        // Check if a username is already stored
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedUsername = sharedPreferences.getString(KEY_USERNAME, null);

        if (savedUsername != null) {
            // If a username is saved, skip asking for the name and go to the next activity
            goToSecondActivity(savedUsername);
            return; // End this activity
        }

        startButton.setText(getString(R.string.enter_name));
        startButton.setEnabled(false);

        usernameEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String name = usernameEditText.getText().toString();
                boolean isEnabled = name.length() >= 3; // Minimum 3 characters for name
                startButton.setEnabled(isEnabled);

                if (isEnabled) {
                    startButton.setText(getString(R.string.start_new_habit));
                    startButton.setAlpha(1f);
                } else {
                    startButton.setText(getString(R.string.enter_name));
                    startButton.setAlpha(0.5f);
                }
            }
        });

        startButton.setOnClickListener(v -> {
            String name = usernameEditText.getText().toString();

            // Save the username in SharedPreferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(KEY_USERNAME, name);
            editor.apply();

            // Go to the next activity
            goToSecondActivity(name);
        });
    }

    private void goToSecondActivity(String name) {
        Intent intent = new Intent(this, SecondActivity2.class);
        intent.putExtra("hello", name);
        startActivity(intent);
        finish(); // Close this activity so it won't reappear when the user presses Back
    }

    private void checkPermissionsOnFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isFirstLaunch = sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, true);

        if (isFirstLaunch) {
            checkNotificationPermission();
        }
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            } else {
                // Proceed to alarm permission if already granted
                checkAlarmPermission();
            }
        } else {
            checkAlarmPermission();
        }
    }

    private void checkAlarmPermission() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean alarmPermissionPrompted = sharedPreferences.getBoolean(ALARM_PERMISSION_PROMPTED_KEY, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmPermissionPrompted) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivityForResult(intent, ALARM_PERMISSION_REQUEST_CODE);
                Toast.makeText(this, "Please allow exact alarm permission for reminders.", Toast.LENGTH_SHORT).show();

                // Mark that the alarm permission has been prompted
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(ALARM_PERMISSION_PROMPTED_KEY, true);
                editor.apply();
            }
        }

        // Mark the first launch as complete
        markFirstLaunchComplete();
    }

    private void markFirstLaunchComplete() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(FIRST_LAUNCH_KEY, false);
        editor.apply();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                checkAlarmPermission(); // Proceed to alarm permission
            } else {
                Toast.makeText(this, "Notification permission is required for this app to function", Toast.LENGTH_LONG).show();
                checkNotificationPermission(); // Re-request permission
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Ensure permission is checked on app close
        checkPermissionsOnFirstLaunch();
    }
}
