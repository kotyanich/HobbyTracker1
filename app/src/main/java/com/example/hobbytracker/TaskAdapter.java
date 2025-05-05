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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder>{
    Context context;
    ArrayList<Task> tasks;
    private final TasksDeleteInterface listener;

    public TaskAdapter(Context context, ArrayList<Task> tasks, TasksDeleteInterface listener) {
        this.context = context;
        this.tasks= tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.taskCheckBox.setText(task.getTaskName());
        holder.taskCheckBox.setOnCheckedChangeListener(null);
        holder.taskCheckBox.setChecked(task.isCompleted());
        if (task.isCompleted()) holder.taskCheckBox.setPaintFlags(holder.taskCheckBox.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        else holder.taskCheckBox.setPaintFlags(holder.taskCheckBox.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) ->{
            new Handler().post(() -> {
                task.setCompleted(isChecked);
                notifyItemChanged(position);
                ((hobby_details) context).saveData("HobbyTasks", tasks);
            });
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder{
        CheckBox taskCheckBox;
        ImageView delete;
        public TaskViewHolder(@NonNull View itemView, TasksDeleteInterface listener) {
            super(itemView);
            taskCheckBox = itemView.findViewById(R.id.taskCheckBox);
            delete = itemView.findViewById(R.id.deleteTask);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int pos = getAdapterPosition();
                        if (pos!=RecyclerView.NO_POSITION){
                            listener.onClickDelete(pos);
                        }
                    }
                }
            });
        }
    }
}
