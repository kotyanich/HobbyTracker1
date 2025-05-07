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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class HobbyAdapter extends RecyclerView.Adapter<HobbyAdapter.MyViewHolder> {
    Context context;
    ArrayList<Hobby> hobby;
    private final RecycleViewInterface listener;
    private final NotificationsInterface notifListener;

    public HobbyAdapter(Context context, ArrayList<Hobby> hobby, RecycleViewInterface listener, NotificationsInterface notifListener) {
        this.context = context;
        this.hobby = hobby;
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
        Hobby item = hobby.get(position);
        holder.textView.setText(item.name);
        holder.imageView.setImageResource(item.image);
        NotifSettings settings = item.getNotifSettings();
        if (settings != null && settings.isEnabled() && !settings.getSelectedDays().isEmpty()) {
            holder.notifications.setImageResource(R.drawable.silver);
        } else {
            holder.notifications.setImageResource(R.drawable.bronze);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, hobby_details.class);
            intent.putExtra("hobbyName", item.getName());
            intent.putExtra("hobbyLogo",item.getImage());
            Gson gson = new Gson();
            List<Task> tasks = item.getTasks();
            String tasksJson = gson.toJson(tasks);
            intent.putExtra("hobbyTasks", tasksJson);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hobby.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
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
                    if (listener != null){
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION){
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
                    if (listener!= null){
                        int pos = getAdapterPosition();
                        if (pos!=RecyclerView.NO_POSITION){
                            listener.OnItemClick(pos);
                        }
                    }
                }
            });
            notifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (notifListener!=null){
                        int pos = getAdapterPosition();
                        if (pos!=RecyclerView.NO_POSITION) notifListener.onNotifClick(pos);
                    }
                }
            });
        }
    }
}
