package com.example.hobbytracker;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.kizitonwose.calendar.core.CalendarDay;
import com.kizitonwose.calendar.core.CalendarMonth;
import com.kizitonwose.calendar.core.DayPosition;
import com.kizitonwose.calendar.view.CalendarView;
import com.kizitonwose.calendar.view.MonthDayBinder;
import com.kizitonwose.calendar.view.ViewContainer;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HeatMap {
    private CalendarView map;
    private Context context;

    private Map<String, String> mapData;

    public HeatMap(CalendarView map, Context context, Map<String, String> mapData) {
        this.map = map;
        this.context = context;
        this.mapData = mapData;
    }

    public void setupMap(String hobbyName) {
        java.time.YearMonth currMonth = java.time.YearMonth.now();
        java.time.YearMonth stMonth = currMonth.minusMonths(30);
        java.time.YearMonth endMonth = currMonth.plusMonths(30);
        java.time.DayOfWeek[] daysOfWeek = java.time.DayOfWeek.values();

        if (stMonth.isBefore(endMonth)) {
            map.setup(stMonth, endMonth, daysOfWeek[0]);
            map.scrollToMonth(currMonth);
            map.setDayBinder(new MonthDayBinder<DayViewContainer>() {
                @NonNull
                @Override
                public DayViewContainer create(@NonNull View view) {
                    return new DayViewContainer(view);
                }

                @Override
                public void bind(@NonNull DayViewContainer container, CalendarDay data) {

                    container.day = data;
                    View view = container.getView();
                    TextView title = view.findViewById(R.id.mapText);
                    if (data.getPosition() == DayPosition.MonthDate) {
                        title.setText(String.valueOf(data.getDate().getDayOfMonth()));

                        java.time.LocalDate date = data.getDate();
                        Log.d("HEATMAP", "Дата: " + date.toString());
                        Log.d("HEATMAP", "mapData contains: " + mapData.containsKey(date.toString()));
                        Log.d("HEATMAP", "mapData value: " + mapData.get(date.toString()));
                        if (mapData!= null && mapData.containsKey(date.toString())) {
                            int activityLevel = Integer.parseInt(mapData.get(date.toString()));
                            if (activityLevel > 120) {
                                title.setBackgroundColor(Color.RED);
                            } else if (activityLevel > 45) {
                                title.setBackgroundColor(Color.YELLOW);
                            } else {
                                title.setBackgroundColor(Color.GREEN);
                            }
                        } else {
                            title.setBackgroundColor(Color.GRAY);
                            Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        title.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

    }

    private static class DayViewContainer extends ViewContainer {
        CalendarDay day;

        public DayViewContainer(View view) {
            super(view);
        }
    }
}

