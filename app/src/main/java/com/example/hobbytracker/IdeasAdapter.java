package com.example.hobbytracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeasViewHolder> {
    Context context;
    ArrayList<Links> links;

    public IdeasAdapter(Context context, ArrayList<Links> links, IdeaDeleteInterface listener) {
        this.context = context;
        this.links = links;
        this.listener = listener;
    }

    IdeaDeleteInterface listener;
    @NonNull
    @Override
    public IdeasAdapter.IdeasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.idea_item, parent, false);
        return new IdeasViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeasAdapter.IdeasViewHolder holder, int position) {
        Links link = links.get(position);
        holder.link.setText(link.getUrl());
        holder.description.setText(link.getDescription());
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public static class IdeasViewHolder extends RecyclerView.ViewHolder{

        ImageView delete;
        TextView link;
        TextView description;
        public IdeasViewHolder(@NonNull View itemView, IdeaDeleteInterface listener) {
            super(itemView);
            delete = itemView.findViewById(R.id.deleteIdea);
            link = itemView.findViewById(R.id.ideaName);
            description = itemView.findViewById(R.id.ideaDesc);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int pos = getAdapterPosition();
                        if (pos!= RecyclerView.NO_POSITION)
                            listener.onClickDeleteIdea(pos);
                    }
                }
            });
            link.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = link.getText().toString();
                    openLink(url);
                }
            });
        }

        public void openLink(String url){
            if (!url.startsWith("http://") && !url.startsWith("https://")){
                Toast.makeText(itemView.getContext(), "Невозможно открыть ссылку, используйте протокол https://", Toast.LENGTH_SHORT).show();
            }
            try{
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                itemView.getContext().startActivity(intent);
            }
            catch (Exception e){
                Toast.makeText(itemView.getContext(), "Невозможно открыть ссылку", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
