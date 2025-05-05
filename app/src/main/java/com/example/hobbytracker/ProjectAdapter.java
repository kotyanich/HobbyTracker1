package com.example.hobbytracker;

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

import java.util.ArrayList;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {
    Context context;
    ArrayList<Project> projects;
    private final ProjAdapter.onGoalsChangedListener listener;
    private final OnDeleteListener listenerDelete;
    public ProjectAdapter(ArrayList<Project> projects, Context context, ProjAdapter.onGoalsChangedListener listener, OnDeleteListener listenerDelete) {
        this.projects = projects;
        this.context = context;
        this.listener = listener;
        this.listenerDelete = listenerDelete;
    }
    @NonNull
    @Override
    public ProjectAdapter.ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.project_item, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectAdapter.ProjectViewHolder holder, int position) {
        Project project = projects.get(position);
        holder.projectCheckBox.setText(project.getProjectName());
        holder.projectCheckBox.setOnCheckedChangeListener(null);
        holder.projectCheckBox.setChecked(project.isCompleted());
        if (project.isCompleted()) holder.projectCheckBox.setPaintFlags(holder.projectCheckBox.getPaintFlags()|Paint.STRIKE_THRU_TEXT_FLAG);
        else holder.projectCheckBox.setPaintFlags(holder.projectCheckBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.projectCheckBox.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            new Handler().post(() -> {
                project.setCompleted(isChecked);
                notifyItemChanged(position);
                if (listener!= null){
                    ProjectAdapter projectAdapter = new ProjectAdapter(projects, context, listener, listenerDelete);
                    listener.onGoalsChanged(projects, projectAdapter);
                }
            });
        }));
        holder.deleteGoal.setOnClickListener(v->{
            if (listenerDelete != null){
                int pos = holder.getAdapterPosition();
                if (pos!=RecyclerView.NO_POSITION){
                    listenerDelete.onDeleteChanged(projects, pos, this);
                };
            }
        });
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder{

        CheckBox projectCheckBox;
        ImageView deleteGoal;
        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);
            projectCheckBox = itemView.findViewById(R.id.projectCheckBox);
            deleteGoal = itemView.findViewById(R.id.deleteGoal);
        }
    }
    public interface OnDeleteListener{
        void onDeleteChanged(ArrayList<Project> goals, int position, ProjectAdapter adapter);
    }
}
