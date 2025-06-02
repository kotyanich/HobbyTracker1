package com.example.hobbytracker.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbytracker.R;
import com.example.hobbytracker.listeners.OnTaskDeleteListener;
import com.example.hobbytracker.data.db.AppDatabase;
import com.example.hobbytracker.data.model.Task;

import java.util.List;


public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder> {
    Context context;
    List<Task> tasks;
    private final OnTaskDeleteListener listener;
    private final AppDatabase db;

    public TasksAdapter(Context context, List<Task> tasks, OnTaskDeleteListener listener, AppDatabase db) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
        this.db = db;
    }

    @NonNull
    @Override
    public TasksAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TasksAdapter.TaskViewHolder holder, int position) {
        Task task = tasks.get(position);

        holder.taskCheckBox.setText(task.text);
        holder.taskCheckBox.setOnCheckedChangeListener(null);
        holder.taskCheckBox.setChecked(task.isDone);

        if (task.isDone) {
            holder.taskCheckBox.setPaintFlags(holder.taskCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.taskCheckBox.setPaintFlags(holder.taskCheckBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            new Handler().post(() -> {
                task.isDone = isChecked;
                notifyItemChanged(position);
                db.taskDao().update(task);
            });
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox taskCheckBox;
        ImageView delete;

        public TaskViewHolder(@NonNull View itemView, OnTaskDeleteListener listener) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            delete = itemView.findViewById(R.id.deleteTask);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onClickHobbyTaskDelete(pos);
                        }
                    }
                }
            });
        }
    }
}
