package com.example.hobbytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HobbyAdapter extends RecyclerView.Adapter<HobbyAdapter.MyViewHolder> {
    Context context;
    ArrayList<String> hobbies;
    ArrayList<Integer> hobbyImages;

    public HobbyAdapter(Context context, ArrayList<String> hobbies, ArrayList<Integer> hobbyImages) {
        this.context = context;
        this.hobbies = hobbies;
        this.hobbyImages = hobbyImages;
    }

    @NonNull
    @Override
    public HobbyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        return new HobbyAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HobbyAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(hobbies.get(position));
        holder.imageView.setImageResource(hobbyImages.get(position));
    }

    @Override
    public int getItemCount() {
        return hobbies.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.logo);
            textView = itemView.findViewById(R.id.hobbyname);
        }
    }
}
