package com.example.hobbytracker.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbytracker.listeners.OnTaskChangedListener;
import com.example.hobbytracker.R;
import com.example.hobbytracker.data.model.ProjectWithTasks;

import java.util.List;

// Shows all projects for a hobby, each with its own tasks list.
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ProjectViewHolder> {
    private List<ProjectWithTasks> projects;
    private final OnTaskChangedListener taskChangedListener;

    public ProjectsAdapter(List<ProjectWithTasks> projects, OnTaskChangedListener listener) {
        this.projects = projects;
        this.taskChangedListener = listener;
    }

    public void setProjects(List<ProjectWithTasks> newProjects) {
        this.projects = newProjects;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.proj_item, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        ProjectWithTasks project = projects.get(position);
        holder.projectName.setText(project.base.name);

        ProjectTasksAdapter tasksAdapter = new ProjectTasksAdapter(project.tasks, taskChangedListener);
        holder.tasksRecycler.setAdapter(tasksAdapter);
        holder.tasksRecycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));

        ImageView addTaskButton = holder.itemView.findViewById(R.id.addProjectTask);
        addTaskButton.setOnClickListener(v -> {
            Context context = v.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View dialogView = inflater.inflate(R.layout.add_project_task_dialog, null);

            EditText taskEditText = dialogView.findViewById(R.id.projectTaskText);
            ImageView okButton = dialogView.findViewById(R.id.okProjectTask);
            ImageView cancelButton = dialogView.findViewById(R.id.cancelProjectTask);

            AlertDialog dialog = new AlertDialog.Builder(context)
                    .setView(dialogView)
                    .create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }

            okButton.setOnClickListener(okView -> {
                String taskText = taskEditText.getText().toString().trim();
                if (!taskText.isEmpty() && taskChangedListener != null) {
                    taskChangedListener.addTaskToProject(project.base.id, taskText);
                    dialog.dismiss();
                }
            });

            cancelButton.setOnClickListener(cancelView -> dialog.dismiss());

            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    static class ProjectViewHolder extends RecyclerView.ViewHolder {
        TextView projectName;
        RecyclerView tasksRecycler;

        ProjectViewHolder(View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectName);
            tasksRecycler = itemView.findViewById(R.id.projectTasksRecyclerView);
        }
    }
}