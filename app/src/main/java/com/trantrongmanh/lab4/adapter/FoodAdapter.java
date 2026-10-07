package com.trantrongmanh.lab4.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trantrongmanh.lab4.R;
import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.FoodItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder> {

    public interface OnFoodClickListener { void onClick(FoodItem food); }

    private final Context context;
    private List<FoodItem> foods;
    private final OnFoodClickListener listener;

    public FoodAdapter(Context context, List<FoodItem> foods, OnFoodClickListener listener) {
        this.context  = context;
        this.foods    = foods;
        this.listener = listener;
    }

    public void updateData(List<FoodItem> newFoods) {
        this.foods = newFoods;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_food_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        FoodItem food = foods.get(position);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        h.ivImage.setImageResource(food.getImageResId());
        h.tvName.setText(food.getName());
        h.tvRating.setText(String.valueOf(food.getRating()));
        h.tvPrice.setText(fmt.format(food.getPrice()) + "đ");
        h.tvFavorite.setText(food.isFavorite() ? "❤️" : "🤍");

        // Staggered entrance
        h.itemView.setAlpha(0f);
        h.itemView.setTranslationY(50f);
        ObjectAnimator fade  = ObjectAnimator.ofFloat(h.itemView, "alpha", 0f, 1f);
        ObjectAnimator slide = ObjectAnimator.ofFloat(h.itemView, "translationY", 50f, 0f);
        fade.setDuration(350);
        slide.setDuration(350);
        fade.setStartDelay(position * 60L);
        slide.setStartDelay(position * 60L);
        slide.setInterpolator(new OvershootInterpolator(0.8f));
        new AnimatorSet() {{ playTogether(fade, slide); start(); }};

        // Click → detail
        h.itemView.setOnClickListener(v -> {
            animateTap(v);
            listener.onClick(food);
        });

        // Long-click → context menu
        h.itemView.setOnLongClickListener(v -> {
            if (context instanceof com.trantrongmanh.lab4.FoodListActivity) {
                ((com.trantrongmanh.lab4.FoodListActivity) context).setContextMenuPosition(position);
            }
            return false;
        });

        // Favorite toggle
        h.tvFavorite.setOnClickListener(v -> {
            food.setFavorite(!food.isFavorite());
            h.tvFavorite.setText(food.isFavorite() ? "❤️" : "🤍");
            animateScaleBounce(v);
        });

        // Add to cart
        h.tvAddToCart.setOnClickListener(v -> {
            DataManager.getInstance().addToCart(food);
            animateScaleBounce(v);
            Toast.makeText(context, "✅ " + food.getName(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override public int getItemCount() { return foods.size(); }

    private void animateTap(View v) {
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.96f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.96f, 1f);
        sx.setDuration(200); sy.setDuration(200);
        new AnimatorSet() {{ playTogether(sx, sy); start(); }};
    }

    private void animateScaleBounce(View v) {
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.4f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.4f, 1f);
        sx.setDuration(300); sy.setDuration(300);
        sx.setInterpolator(new OvershootInterpolator(2f));
        sy.setInterpolator(new OvershootInterpolator(2f));
        new AnimatorSet() {{ playTogether(sx, sy); start(); }};
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvRating, tvPrice, tvFavorite, tvAddToCart;

        ViewHolder(View v) {
            super(v);
            ivImage     = v.findViewById(R.id.iv_food_image);
            tvName      = v.findViewById(R.id.tv_food_name);
            tvRating    = v.findViewById(R.id.tv_rating);
            tvPrice     = v.findViewById(R.id.tv_price);
            tvFavorite  = v.findViewById(R.id.tv_favorite);
            tvAddToCart = v.findViewById(R.id.btn_add_to_cart);
        }
    }
}
