package com.example.hobbytracker;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbytracker.data.model.DetailedHobby;
import com.example.hobbytracker.data.model.NotificationSettings;
import com.example.hobbytracker.data.model.Task;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HobbyAdapter extends RecyclerView.Adapter<HobbyAdapter.MyViewHolder> {
    Context context;
    ArrayList<DetailedHobby> hobbiesList;
    private final RecycleViewInterface listener;
    private final NotificationsInterface notifListener;

    public HobbyAdapter(Context context, ArrayList<DetailedHobby> hobbiesList, RecycleViewInterface listener, NotificationsInterface notifListener) {
        this.context = context;
        this.hobbiesList = hobbiesList;
        this.listener = listener;
        this.notifListener = notifListener;
    }

    @NonNull
    @Override
    public HobbyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new HobbyAdapter.MyViewHolder(view, listener, notifListener);
    }

    @Override
    public void onBindViewHolder(@NonNull HobbyAdapter.MyViewHolder holder, int position) {
        DetailedHobby hobby = hobbiesList.get(position);
        holder.textView.setText(hobby.base.name);
        holder.imageView.setImageResource(hobby.base.iconId);

        NotificationSettings settings = hobby.notificationSettings;

        if (settings != null && settings.isEnabled && !settings.days.isEmpty()) {
            holder.notifications.setImageResource(R.drawable.notif);
        } else {
            holder.notifications.setImageResource(R.drawable.notif);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HobbyDetailsActivity.class);
            intent.putExtra("hobbyId", hobby.base.id);
            intent.putExtra("hobbyName", hobby.base.name);
            intent.putExtra("hobbyLogo", hobby.base.iconId);
            Gson gson = new Gson();
            List<Task> tasks = hobby.hobbyTasks;
            String tasksJson = gson.toJson(tasks);
            intent.putExtra("hobbyTasks", tasksJson);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hobbiesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        ImageView notifications;

        public MyViewHolder(@NonNull View itemView, RecycleViewInterface listener, NotificationsInterface notifListener) {
            super(itemView);
            imageView = itemView.findViewById(R.id.logo);
            textView = itemView.findViewById(R.id.hobbyname);
            notifications = itemView.findViewById(R.id.notifications);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.OnItemLongClick(pos);
                            return true;
                        }
                    }
                    return false;
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.OnItemClick(pos);
                        }
                    }
                }
            });
            notifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notifListener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) notifListener.onNotifClick(pos);
                    }
                }
            });
        }
    }
}
