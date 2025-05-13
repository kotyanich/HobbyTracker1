package com.example.hobbytracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    ArrayList<ShopItem> shopItems;
    public ShopAdapter(ArrayList<ShopItem> shopItems) {
        this.shopItems = shopItems;
    }

    @NonNull
    @Override
    public ShopAdapter.ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopAdapter.ShopViewHolder holder, int position) {
        ShopItem shopItem = shopItems.get(position);
        holder.mascot.setImageResource(shopItem.getImage());
        if (!shopItem.isBought && !shopItem.isSelected){
            holder.price.setVisibility(View.VISIBLE);
            holder.check.setVisibility(View.GONE);
            holder.choose.setVisibility(View.GONE);
        }
        if (shopItem.isBought && !shopItem.isSelected){
            holder.price.setVisibility(View.GONE);
            holder.check.setVisibility(View.GONE);
            holder.choose.setVisibility(View.VISIBLE);
        }
        if (shopItem.isBought && shopItem.isSelected){
            holder.choose.setVisibility(View.GONE);
            holder.check.setVisibility(View.VISIBLE);
            holder.price.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return shopItems.size();
    }

    static class ShopViewHolder extends RecyclerView.ViewHolder{
        ImageView mascot;
        LinearLayout price;
        TextView choose;
        ImageView check;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            mascot = itemView.findViewById(R.id.cat);
            price = itemView.findViewById(R.id.price);
            choose = itemView.findViewById(R.id.choose);
            check = itemView.findViewById(R.id.tick);
        }
    }
}
