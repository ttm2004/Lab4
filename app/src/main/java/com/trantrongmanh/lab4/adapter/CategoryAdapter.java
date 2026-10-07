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

import com.trantrongmanh.lab4.CategoryActivity;
import com.trantrongmanh.lab4.R;
import com.trantrongmanh.lab4.model.Category;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    public interface OnCategoryClickListener {
        void onClick(Category category);
    }

    public interface OnCategoryLongClickListener {
        boolean onLongClick(Category category, int position);
    }

    private final Context context;
    private final List<Category> categories;
    private final OnCategoryClickListener clickListener;
    private final OnCategoryLongClickListener longClickListener;

    public CategoryAdapter(Context context, List<Category> categories,
                           OnCategoryClickListener clickListener,
                           OnCategoryLongClickListener longClickListener) {
        this.context           = context;
        this.categories        = categories;
        this.clickListener     = clickListener;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category cat = categories.get(position);

        holder.ivIcon.setImageResource(cat.getIconResId());
        holder.tvName.setText(cat.getName());
        holder.tvDescription.setText(cat.getDescription());
        holder.tvItemCount.setText(cat.getItemCount() + " món");

        // Staggered entrance animation
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.item_animation));

        holder.itemView.setOnClickListener(v -> clickListener.onClick(cat));

        holder.itemView.setOnLongClickListener(v -> {
            // Notify CategoryActivity of the pressed position for context menu
            if (context instanceof CategoryActivity) {
                ((CategoryActivity) context).setContextMenuPosition(position);
            }
            return longClickListener.onLongClick(cat, position);
        });
    }

    @Override
    public int getItemCount() { return categories.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName, tvDescription, tvItemCount;

        ViewHolder(View itemView) {
            super(itemView);
            ivIcon        = itemView.findViewById(R.id.iv_icon);
            tvName        = itemView.findViewById(R.id.tv_name);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvItemCount   = itemView.findViewById(R.id.tv_item_count);
        }
    }
}
