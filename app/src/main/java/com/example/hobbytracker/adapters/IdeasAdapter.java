package com.example.hobbytracker.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hobbytracker.listeners.OnIdeaDeleteListener;
import com.example.hobbytracker.R;
import com.example.hobbytracker.data.model.Idea;

import java.util.List;

public class IdeasAdapter extends RecyclerView.Adapter<IdeasAdapter.IdeasViewHolder> {
    Context context;
    List<Idea> ideas;

    public IdeasAdapter(Context context, List<Idea> ideas, OnIdeaDeleteListener listener) {
        this.context = context;
        this.ideas = ideas;
        this.listener = listener;
    }

    OnIdeaDeleteListener listener;

    @NonNull
    @Override
    public IdeasAdapter.IdeasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.idea_item, parent, false);
        return new IdeasViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull IdeasAdapter.IdeasViewHolder holder, int position) {
        Idea idea = ideas.get(position);
        holder.idea.setText(idea.url);
        holder.description.setText(idea.title);
    }

    @Override
    public int getItemCount() {
        return ideas.size();
    }

    public static class IdeasViewHolder extends RecyclerView.ViewHolder {

        ImageView delete;
        TextView idea;
        TextView description;

        public IdeasViewHolder(@NonNull View itemView, OnIdeaDeleteListener listener) {
            super(itemView);
            delete = itemView.findViewById(R.id.deleteIdea);
            idea = itemView.findViewById(R.id.ideaName);
            description = itemView.findViewById(R.id.ideaDesc);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listener.onClickDeleteIdea(pos);
                        }
                    }
                }
            });

            idea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = idea.getText().toString();
                    openLink(url);
                }
            });
        }

        public void openLink(String url) {
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                Toast.makeText(
                                itemView.getContext(),
                                "Невозможно открыть ссылку, используйте протокол https://",
                                Toast.LENGTH_SHORT)
                        .show();
            }
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                itemView.getContext().startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(
                                itemView.getContext(),
                                "Невозможно открыть ссылку",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
