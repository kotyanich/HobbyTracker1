package com.example.hobbytracker.models;

import android.content.Context;

import com.kizitonwose.calendar.view.CalendarView;


import java.util.Map;

public class HeatMap {
    private final CalendarView map;
    private final Context context;

    private final Map<String, String> mapData;

    public HeatMap(CalendarView map, Context context, Map<String, String> mapData) {
        this.map = map;
        this.context = context;
        this.mapData = mapData;
    }

}

