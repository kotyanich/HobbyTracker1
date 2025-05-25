package com.example.hobbytracker;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ShopViewHolder> {
    ArrayList<ShopItem> shopItems;
    int  balance;
    Context context;
    Activity activity;
    TextView balanceText;
    public ShopAdapter(ArrayList<ShopItem> shopItems, Context context, Activity activity, TextView balanceText) {
        this.shopItems = shopItems;
        this.context = context;
        this.activity = activity;
        this.balanceText = balanceText;
        updateBalance();
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
        holder.price.setOnClickListener(v ->{
            if (balance >= 50){
                balance -= 50;
                shopItem.setBought(true);
                saveBalance();
                holder.price.setVisibility(View.GONE);
                holder.choose.setVisibility(View.VISIBLE);
                balanceText.setText(context.getString(R.string.money, balance));
            }
            else Toast.makeText(context, "Недостаточно средств!", Toast.LENGTH_SHORT).show();
        });
        holder.choose.setOnClickListener(v ->{
            for (ShopItem item : shopItems)
                item.setSelected(false);
            holder.choose.setVisibility(View.GONE);
            holder.check.setVisibility(View.VISIBLE);
            shopItem.setSelected(true);
            saveBalance();
            SharedPreferences prefs = context.getSharedPreferences("Prefs", MODE_PRIVATE);
            prefs.edit().putInt("Mascot", shopItem.getImage()).apply();
            notifyDataSetChanged();
            int mascot = shopItem.getImage();
            Intent intent = new Intent();
            intent.putExtra("Mascot", mascot);
            activity.setResult(AppCompatActivity.RESULT_OK, intent);
            activity.finish();
        });
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
    private void saveBalance() {
        SharedPreferences prefs = context.getSharedPreferences("AchData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("Balance", balance);
        for (int i = 0; i < shopItems.size(); i++) {
            editor.putBoolean("ItemBought_" + i, shopItems.get(i).isBought());
            editor.putBoolean("ItemSelected_" + i, shopItems.get(i).isSelected());
        }
        editor.apply();
    }
    private void updateBalance() {
        SharedPreferences prefs = context.getSharedPreferences("AchData", MODE_PRIVATE);
        balance = prefs.getInt("Balance", 0);
        boolean hasSavedState = prefs.contains("ItemBought_0");
        if (!hasSavedState && shopItems.size() > 1) {
            shopItems.get(1).setBought(true);
            shopItems.get(1).setSelected(true);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("ItemBought_1", true);
            editor.putBoolean("ItemSelected_1", true);
            editor.apply();
            SharedPreferences prefsMascot = context.getSharedPreferences("Prefs", MODE_PRIVATE);
            prefsMascot.edit().putInt("Mascot", shopItems.get(1).getImage()).apply();
        } else {
            for (int i = 0; i < shopItems.size(); i++) {
                shopItems.get(i).setBought(prefs.getBoolean("ItemBought_" + i, false));
                shopItems.get(i).setSelected(prefs.getBoolean("ItemSelected_" + i, false));
            }
        }
    }
}
