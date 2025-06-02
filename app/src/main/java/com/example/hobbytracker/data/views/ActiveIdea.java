package com.example.hobbytracker.data.views;

import androidx.room.DatabaseView;

import com.example.hobbytracker.data.model.Idea;

@DatabaseView("SELECT * FROM ideas WHERE status = 'active'")
public class ActiveIdea extends Idea {
}
