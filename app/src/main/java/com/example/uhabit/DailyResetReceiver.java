package com.example.uhabit;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DailyResetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        HabitRepository repository = new HabitRepository((Application) context.getApplicationContext());
        repository.resetDailyCompletion();
    }
}
