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

}

