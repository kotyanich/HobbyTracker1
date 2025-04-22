package com.example.hobbytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class LogrosAdapter extends RecyclerView.Adapter<LogrosAdapter.LogrosViewHolder> {
    private final Context context;
    private final ArrayList<AchievementsData> logros;
    public LogrosAdapter(Context context, ArrayList<AchievementsData> logros) {
        this.context = context;
        this.logros = logros;
    }

    @NonNull
    @Override
    public LogrosAdapter.LogrosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.achievement_item, parent, false);
        return new LogrosAdapter.LogrosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogrosAdapter.LogrosViewHolder holder, int position) {
        AchievementsData achievement = logros.get(position);
        holder.logroName.setText(achievement.getTitle());
        String descr;
        int threshold;
        holder.progress.setProgress(achievement.getProgressPercentage());
        int medalResource;
        if (achievement.getCurrLevel() < 1){
                descr = achievement.getBronzeDesc();
                threshold = achievement.getBronzeThreshold();
                holder.medal.setImageResource(R.drawable.nothing);
        }
        else if (achievement.getCurrLevel() == 1){
            descr = achievement.getSilverDesc();
            threshold = achievement.getSilverThreshold();
            holder.medal.setImageResource(R.drawable.bronze);
        }
        else if (achievement.getCurrLevel() == 2){
            descr = achievement.getGoldDesc();
            threshold = achievement.getGoldThreshold();
            holder.medal.setImageResource(R.drawable.silver);
        }
        else{
            descr = achievement.getGoldDesc();
            threshold = achievement.getGoldThreshold();
            holder.medal.setImageResource(R.drawable.gold);
        }
        String prog = descr + " (" + achievement.getCurrProgress() + "/" + threshold + ")";
        holder.description.setText(prog);
    }

    @Override
    public int getItemCount() {
        return logros.size();
    }
    public static class LogrosViewHolder extends RecyclerView.ViewHolder{
        TextView logroName;
        ProgressBar progress;
        ImageView medal;
        TextView description;
        public LogrosViewHolder(@NonNull View itemView) {
            super(itemView);
            logroName = itemView.findViewById(R.id.logroTitle);
            progress = itemView.findViewById(R.id.progressBar);
            medal = itemView.findViewById(R.id.medal);
            description = itemView.findViewById(R.id.description);
        }
    }
}
