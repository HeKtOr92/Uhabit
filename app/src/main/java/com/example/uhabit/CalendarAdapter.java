package com.example.uhabit;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder> {

    private List<LocalDate> dates;
    private HashSet<LocalDate> completedDates;
    private HashSet<LocalDate> skippedDates;

    public CalendarAdapter() {
        this.dates = new ArrayList<>();
        this.completedDates = new HashSet<>();
        this.skippedDates = new HashSet<>();
    }

    public void setDates(HashSet<LocalDate> completedDates, HashSet<LocalDate> skippedDates) {
        this.completedDates = completedDates;
        this.skippedDates = skippedDates;

        // Generate dates for the current month
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        dates.clear();
        for (LocalDate date = firstDayOfMonth; !date.isAfter(lastDayOfMonth); date = date.plusDays(1)) {
            dates.add(date);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        LocalDate date = dates.get(position);
        holder.dateTextView.setText(String.valueOf(date.getDayOfMonth()));

        // Set background color based on completion status
        if (completedDates.contains(date)) {
            holder.dateTextView.setBackgroundColor(Color.GREEN);
        } else if (skippedDates.contains(date)) {
            holder.dateTextView.setBackgroundColor(Color.RED);
        } else {
            holder.dateTextView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return dates.size();
    }

    public static class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public CalendarViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }
}
