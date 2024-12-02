package com.example.uhabit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int habitId = intent.getIntExtra("habitId", -1);

        Log.d("ReminderReceiver", "Reminder triggered for habitId: " + habitId);

        // Show a notification or perform your desired action here
        Toast.makeText(context, "Time to work on your habit!", Toast.LENGTH_SHORT).show();
    }
}
