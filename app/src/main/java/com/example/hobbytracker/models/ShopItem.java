package com.example.hobbytracker.models;

public class ShopItem {

    public int getImage() {
        return image;
    }

    boolean isBought;
    boolean isSelected;
    int image;

    public ShopItem(int image) {
        this.isBought = false;
        this.isSelected = false;
        this.image = image;
    }

    public boolean isBought() {
        return isBought;
    }

    public void setBought(boolean bought) {
        isBought = bought;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
