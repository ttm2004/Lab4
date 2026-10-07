package com.trantrongmanh.lab4.model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private FoodItem foodItem;
    private int quantity;

    public CartItem(FoodItem foodItem, int quantity) {
        this.foodItem = foodItem;
        this.quantity = quantity;
    }

    public FoodItem getFoodItem() { return foodItem; }
    public void setFoodItem(FoodItem foodItem) { this.foodItem = foodItem; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        return foodItem.getPrice() * quantity;
    }

    public void increaseQuantity() { this.quantity++; }
    public void decreaseQuantity() {
        if (this.quantity > 1) this.quantity--;
    }
}
