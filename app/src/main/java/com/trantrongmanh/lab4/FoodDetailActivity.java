package com.trantrongmanh.lab4;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.Category;
import com.trantrongmanh.lab4.model.FoodItem;

import java.text.NumberFormat;
import java.util.Locale;

public class FoodDetailActivity extends AppCompatActivity {

    private FoodItem food;
    private int quantity = 1;
    private TextView tvQuantity, tvFavorite, tvTotalPrice;
    private NumberFormat fmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        int foodId = getIntent().getIntExtra("food_id", -1);
        food = DataManager.getInstance().getFoodItemById(foodId);
        if (food == null) { finish(); return; }

        fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bindViews();
        playEntranceAnimation();
    }

    private void bindViews() {
        // Image
        ImageView ivImage = findViewById(R.id.iv_food_image);
        ivImage.setImageResource(food.getImageResId());

        // Name + price
        ((TextView) findViewById(R.id.tv_food_name)).setText(food.getName());
        ((TextView) findViewById(R.id.tv_price)).setText(fmt.format(food.getPrice()) + " đ");

        // Stats
        ((TextView) findViewById(R.id.tv_rating)).setText(food.getRating() + " / 5.0");
        ((TextView) findViewById(R.id.tv_prep_time)).setText(food.getPrepTimeMinutes() + " phút");

        // Category name
        Category cat = DataManager.getInstance().getCategoryById(food.getCategoryId());
        TextView tvCat = findViewById(R.id.tv_category);
        if (cat != null) tvCat.setText(cat.getName());

        // Description
        ((TextView) findViewById(R.id.tv_description)).setText(food.getDescription());

        // Total price
        tvTotalPrice = findViewById(R.id.tv_total_price);
        updateTotalPrice();

        // Favorite
        tvFavorite = findViewById(R.id.tv_favorite);
        updateFavoriteIcon();
        tvFavorite.setOnClickListener(v -> {
            food.setFavorite(!food.isFavorite());
            updateFavoriteIcon();
            animateScaleBounce(tvFavorite);
            Toast.makeText(this,
                    food.isFavorite() ? "Đã thêm yêu thích ❤️" : "Đã bỏ yêu thích",
                    Toast.LENGTH_SHORT).show();
        });

        // Quantity
        tvQuantity = findViewById(R.id.tv_quantity);
        TextView btnDec = findViewById(R.id.btn_decrease);
        TextView btnInc = findViewById(R.id.btn_increase);

        btnDec.setOnClickListener(v -> {
            if (quantity > 1) { quantity--; refreshQuantity(tvQuantity); }
        });
        btnInc.setOnClickListener(v -> {
            quantity++;
            refreshQuantity(tvQuantity);
        });

        // Add to cart
        Button btnCart = findViewById(R.id.btn_add_to_cart);
        btnCart.setOnClickListener(v -> {
            for (int i = 0; i < quantity; i++) DataManager.getInstance().addToCart(food);
            animateAddToCart(btnCart);
            Toast.makeText(this,
                    "✅ Đã thêm " + quantity + "x " + food.getName(),
                    Toast.LENGTH_SHORT).show();
        });
    }

    private void refreshQuantity(TextView tvQty) {
        tvQty.setText(String.valueOf(quantity));
        animateScaleBounce(tvQty);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        tvTotalPrice.setText(fmt.format(food.getPrice() * quantity) + " đ");
    }

    private void updateFavoriteIcon() {
        tvFavorite.setText(food.isFavorite() ? "❤️" : "🤍");
    }

    private void playEntranceAnimation() {
        // Slide up cards
        int delay = 0;
        for (int id : new int[]{R.id.tv_food_name, R.id.tv_description}) {
            View v = findViewById(id);
            if (v != null) {
                v.setAlpha(0f);
                v.setTranslationY(40f);
                ObjectAnimator fade  = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
                ObjectAnimator slide = ObjectAnimator.ofFloat(v, "translationY", 40f, 0f);
                fade.setDuration(400);
                slide.setDuration(400);
                fade.setStartDelay(delay);
                slide.setStartDelay(delay);
                slide.setInterpolator(new OvershootInterpolator(1f));
                new AnimatorSet() {{ playTogether(fade, slide); start(); }};
                delay += 100;
            }
        }
    }

    private void animateScaleBounce(View v) {
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.3f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.3f, 1f);
        sx.setDuration(350);
        sy.setDuration(350);
        sx.setInterpolator(new OvershootInterpolator(2f));
        sy.setInterpolator(new OvershootInterpolator(2f));
        new AnimatorSet() {{ playTogether(sx, sy); start(); }};
    }

    private void animateAddToCart(View v) {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.92f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.92f, 1f);
        scaleX.setDuration(250);
        scaleY.setDuration(250);
        new AnimatorSet() {{ playTogether(scaleX, scaleY); start(); }};
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
