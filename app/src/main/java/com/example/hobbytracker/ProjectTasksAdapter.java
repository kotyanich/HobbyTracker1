package com.example.hobbytracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbytracker.data.model.Task;
import com.example.hobbytracker.OnTaskChangedListener;

import java.util.List;

// Shows all tasks for a single project.
public class ProjectTasksAdapter extends RecyclerView.Adapter<ProjectTasksAdapter.TaskViewHolder> {
    private List<Task> tasks;
    private final OnTaskChangedListener taskChangedListener;

    public ProjectTasksAdapter(List<Task> tasks, OnTaskChangedListener taskChangedListener) {
        this.tasks = tasks;
        this.taskChangedListener = taskChangedListener;
    }

    public void setTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.project_task_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.checkBox.setText(task.text);
        holder.checkBox.setChecked(task.isDone);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            task.isDone = isChecked;
            if (taskChangedListener != null) taskChangedListener.onTaskCheckedChanged(task);
        });

        ImageView deleteButton = holder.itemView.findViewById(R.id.deleteProjectTask);
        deleteButton.setOnClickListener(v -> {
            if (taskChangedListener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                taskChangedListener.deleteProjectTask(tasks.get(holder.getAdapterPosition()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        TaskViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.projectTaskCheckBox);
        }
    }
}