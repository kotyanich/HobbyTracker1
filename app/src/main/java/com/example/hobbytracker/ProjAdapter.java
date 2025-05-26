package com.example.hobbytracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProjAdapter extends RecyclerView.Adapter<ProjAdapter.ProjViewHolder> {
    private final List<ProjectList> projectLists;
    private final Context context;
    private final LayoutInflater inflater;
    private final onGoalsChangedListener listener;
    private ProjectAdapter.OnDeleteListener listenerDelete;
    private ProjectAdapter projectAdapter;

    public ProjAdapter(Context context, List<ProjectList> projects, onGoalsChangedListener listener, ProjectAdapter.OnDeleteListener listenerDelete){
        this.projectLists = projects;
        this.listener = listener;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.listenerDelete = listenerDelete;
    }

    @NonNull
    @Override
    public ProjAdapter.ProjViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.proj_item,parent,false);
        return new ProjViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjAdapter.ProjViewHolder holder, int position) {
        ProjectList projectList = projectLists.get(position);
        holder.projectName.setText(projectList.getName());
        projectAdapter = new ProjectAdapter(projectList.getProjects(), context, (goals,adapter) ->{
            if (listener != null){
                listener.onGoalsChanged(projectList.getProjects(), adapter);
            }
        }, listenerDelete);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        holder.goalRecyclerView.setLayoutManager(layoutManager);
        holder.goalRecyclerView.setAdapter(projectAdapter);
        holder.addButton.setOnClickListener(v ->
                showAddProjectDialog(projectList, projectAdapter));
    }
    private void showAddProjectDialog(ProjectList projectList, ProjectAdapter projectAdapter){
        AlertDialog.Builder builder= new AlertDialog.Builder(context);
        View dialogView = inflater.inflate(R.layout.add_goal_dialog,null);
        EditText projectInput = dialogView.findViewById(R.id.goalName);
        ImageView ok = dialogView.findViewById(R.id.okGoal);
        ImageView cancel = dialogView.findViewById(R.id.cancelGoal);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        ok.setOnClickListener(v -> {
            String projectText = projectInput.getText().toString().trim();
            if(!projectText.isEmpty()){
                Project newProject = new Project(projectText);
                projectList.getProjects().add(newProject);
                projectAdapter.notifyDataSetChanged();
                if (listener != null){
                    listener.onGoalsChanged(projectList.getProjects(), projectAdapter);
                }
            }
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public int getItemCount() {
        return projectLists.size();
    }


    public static class ProjViewHolder extends RecyclerView.ViewHolder{
        TextView projectName;
        RecyclerView goalRecyclerView;
        ImageView addButton;
        ImageView list;

        public ProjViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectName);
            goalRecyclerView = itemView.findViewById(R.id.projectRecyclerView);
            addButton = itemView.findViewById(R.id.addGoal);
            list = itemView.findViewById(R.id.projectsList);
        }
    }
    public interface onGoalsChangedListener{
        void onGoalsChanged(ArrayList<Project> goals, ProjectAdapter adapter);
    }

}
