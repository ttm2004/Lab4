package com.trantrongmanh.lab4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trantrongmanh.lab4.R;
import com.trantrongmanh.lab4.model.Category;

import java.util.List;

/**
 * Compact horizontal adapter used on the Home screen category strip.
 */
public class CategoryHomeAdapter extends RecyclerView.Adapter<CategoryHomeAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onClick(Category category);
    }

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener listener;

    public CategoryHomeAdapter(Context context, List<Category> categories,
                               OnCategoryClickListener listener) {
        this.context    = context;
        this.categories = categories;
        this.listener   = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_category_home, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category cat = categories.get(position);
        holder.ivIcon.setImageResource(cat.getIconResId());
        holder.tvName.setText(cat.getName());
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.item_animation));
        holder.itemView.setOnClickListener(v -> listener.onClick(cat));
    }

    @Override
    public int getItemCount() { return categories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_icon);
            tvName = itemView.findViewById(R.id.tv_name);
        }
    }
}
