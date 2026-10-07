package com.trantrongmanh.lab4.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.trantrongmanh.lab4.R;
import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    public interface OnCartChangedListener {
        void onChanged();
    }

    private final Context context;
    private final List<CartItem> cartItems;
    private final OnCartChangedListener listener;

    public CartAdapter(Context context, List<CartItem> cartItems, OnCartChangedListener listener) {
        this.context   = context;
        this.cartItems = cartItems;
        this.listener  = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        holder.ivImage.setImageResource(item.getFoodItem().getImageResId());
        holder.tvName.setText(item.getFoodItem().getName());
        holder.tvUnitPrice.setText(fmt.format(item.getFoodItem().getPrice()) + " đ / cái");
        holder.tvSubtotal.setText(fmt.format(item.getTotalPrice()) + " đ");
        holder.tvQuantity.setText(String.valueOf(item.getQuantity()));

        // Entrance animation
        holder.itemView.startAnimation(
                AnimationUtils.loadAnimation(context, R.anim.item_animation));

        holder.btnIncrease.setOnClickListener(v -> {
            item.increaseQuantity();
            holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
            holder.tvSubtotal.setText(fmt.format(item.getTotalPrice()) + " đ");
            holder.tvQuantity.startAnimation(
                    AnimationUtils.loadAnimation(context, R.anim.scale_up));
            listener.onChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.decreaseQuantity();
                holder.tvQuantity.setText(String.valueOf(item.getQuantity()));
                holder.tvSubtotal.setText(fmt.format(item.getTotalPrice()) + " đ");
                holder.tvQuantity.startAnimation(
                        AnimationUtils.loadAnimation(context, R.anim.scale_up));
                listener.onChanged();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            DataManager.getInstance().removeFromCart(item.getFoodItem().getId());
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, cartItems.size());
            listener.onChanged();
        });
    }

    @Override
    public int getItemCount() { return cartItems.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvUnitPrice, tvSubtotal, tvQuantity, btnRemove;
        Button btnIncrease, btnDecrease;

        ViewHolder(View itemView) {
            super(itemView);
            ivImage      = itemView.findViewById(R.id.iv_food_image);
            tvName       = itemView.findViewById(R.id.tv_food_name);
            tvUnitPrice  = itemView.findViewById(R.id.tv_unit_price);
            tvSubtotal   = itemView.findViewById(R.id.tv_subtotal);
            tvQuantity   = itemView.findViewById(R.id.tv_quantity);
            btnIncrease  = itemView.findViewById(R.id.btn_increase);
            btnDecrease  = itemView.findViewById(R.id.btn_decrease);
            btnRemove    = itemView.findViewById(R.id.btn_remove);
        }
    }
}
