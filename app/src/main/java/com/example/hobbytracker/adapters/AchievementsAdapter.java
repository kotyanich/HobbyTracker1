package com.example.hobbytracker.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbytracker.models.AchievementsData;
import com.example.hobbytracker.managers.AchievementsManager;
import com.example.hobbytracker.R;

import java.util.ArrayList;


public class AchievementsAdapter extends RecyclerView.Adapter<AchievementsAdapter.AchievementsViewHolder> {
    private final Context context;
    private final ArrayList<AchievementsData> achievements;
    public int balance;
    AchievementsManager manager;

    public AchievementsAdapter(Context context, AchievementsManager manager) {
        this.context = context;
        this.manager = manager;
        this.achievements = manager.getAchievements();
        loadBalance();
    }

    @NonNull
    @Override
    public AchievementsAdapter.AchievementsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.achievement_item, parent, false);
        return new AchievementsAdapter.AchievementsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementsAdapter.AchievementsViewHolder holder, int position) {
        AchievementsData achievement = achievements.get(position);
        holder.logroName.setText(achievement.getTitle());
        String descr;
        int threshold;
        holder.progress.setProgress(achievement.getProgressPercentage());
        if (achievement.getCurrLevel() < 1) {
            descr = achievement.getBronzeDesc();
            threshold = achievement.getBronzeThreshold();
            holder.medal.setImageResource(R.drawable.no_medal);
        } else if (achievement.getCurrLevel() == 1) {
            descr = achievement.getSilverDesc();
            threshold = achievement.getSilverThreshold();
            holder.medal.setImageResource(R.drawable.bronze);
        } else if (achievement.getCurrLevel() == 2) {
            descr = achievement.getGoldDesc();
            threshold = achievement.getGoldThreshold();
            holder.medal.setImageResource(R.drawable.silver);
        } else {
            descr = achievement.getGoldDesc();
            threshold = achievement.getGoldThreshold();
            holder.medal.setImageResource(R.drawable.gold);
        }
        String prog = descr + " (" + achievement.getCurrProgress() + "/" + threshold + ")";
        holder.description.setText(prog);
        if (!achievement.isHasGotBronze() && achievement.getCurrLevel() >= 1)
            holder.bronzeReward.setVisibility(View.VISIBLE);

        else if (!achievement.isHasGotSilver() && achievement.getCurrLevel() >= 2)
            holder.silverReward.setVisibility(View.VISIBLE);

        else if (!achievement.isHasGotGold() && achievement.getCurrLevel() >= 3)
            holder.goldReward.setVisibility(View.VISIBLE);
        else {
            holder.bronzeReward.setVisibility(View.GONE);
            holder.silverReward.setVisibility(View.GONE);
            holder.goldReward.setVisibility(View.GONE);
        }
        holder.bronzeReward.setOnClickListener(v -> {
            achievement.setHasGotBronze(true);
            holder.bronzeReward.setVisibility(View.GONE);
            if (!achievement.isHasGotSilver() && achievement.getCurrLevel() >= 2)
                holder.silverReward.setVisibility(View.VISIBLE);
            else if (!achievement.isHasGotGold() && achievement.getCurrLevel() >= 3)
                holder.goldReward.setVisibility(View.VISIBLE);
            balance += 10;
            saveBalance();
            manager.saveAchievements();
        });
        holder.silverReward.setOnClickListener(v -> {
            achievement.setHasGotSilver(true);
            holder.silverReward.setVisibility(View.GONE);
            if (!achievement.isHasGotGold() && achievement.getCurrLevel() >= 3)
                holder.goldReward.setVisibility(View.VISIBLE);
            balance += 20;
            saveBalance();
            manager.saveAchievements();
        });
        holder.goldReward.setOnClickListener(v -> {
            achievement.setHasGotGold(true);
            holder.goldReward.setVisibility(View.GONE);
            balance += 30;
            saveBalance();
            manager.saveAchievements();
        });
    }

    @Override
    public int getItemCount() {
        return achievements.size();
    }

    public static class AchievementsViewHolder extends RecyclerView.ViewHolder {
        TextView logroName;
        ProgressBar progress;
        ImageView medal;
        TextView description;
        FrameLayout bronzeReward;
        FrameLayout silverReward;
        FrameLayout goldReward;

        public AchievementsViewHolder(@NonNull View itemView) {
            super(itemView);
            logroName = itemView.findViewById(R.id.logroTitle);
            progress = itemView.findViewById(R.id.progressBar);
            medal = itemView.findViewById(R.id.medal);
            description = itemView.findViewById(R.id.description);
            bronzeReward = itemView.findViewById(R.id.bronze_reward);
            silverReward = itemView.findViewById(R.id.silver_reward);
            goldReward = itemView.findViewById(R.id.gold_reward);
        }
    }

    public void saveBalance() {
        SharedPreferences prefs = context.getSharedPreferences("AchData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Balance", balance);
        editor.apply();
    }

    public void loadBalance() {
        SharedPreferences prefs = context.getSharedPreferences("AchData", Context.MODE_PRIVATE);
        balance = prefs.getInt("Balance", 0);
    }
}