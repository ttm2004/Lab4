package com.trantrongmanh.lab4.model;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private int id;
    private String name;
    private String description;
    private double price;
    private int imageResId;
    private int categoryId;
    private float rating;
    private int prepTimeMinutes;
    private boolean isFavorite;

    public FoodItem(int id, String name, String description, double price,
                    int imageResId, int categoryId, float rating, int prepTimeMinutes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageResId = imageResId;
        this.categoryId = categoryId;
        this.rating = rating;
        this.prepTimeMinutes = prepTimeMinutes;
        this.isFavorite = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getPrepTimeMinutes() { return prepTimeMinutes; }
    public void setPrepTimeMinutes(int prepTimeMinutes) { this.prepTimeMinutes = prepTimeMinutes; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
