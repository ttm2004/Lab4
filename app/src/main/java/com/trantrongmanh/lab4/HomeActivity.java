package com.trantrongmanh.lab4;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.trantrongmanh.lab4.adapter.CategoryHomeAdapter;
import com.trantrongmanh.lab4.adapter.FoodAdapter;
import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.Category;
import com.trantrongmanh.lab4.model.FoodItem;
import com.trantrongmanh.lab4.model.User;

import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FrameLayout container;
    private FoodAdapter foodAdapter;
    private EditText etSearch;
    private int currentTab = R.id.nav_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Food App 🍔");

        container = findViewById(R.id.fragment_container);
        bottomNav = findViewById(R.id.bottom_nav);

        inflateHomeContent();
        setupBottomNav();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentTab = R.id.nav_home;
        if (bottomNav != null) bottomNav.setSelectedItemId(R.id.nav_home);
        inflateHomeContent();
    }

    private void inflateHomeContent() {
        container.removeAllViews();
        View homeView = getLayoutInflater().inflate(R.layout.fragment_home, container, false);
        container.addView(homeView);

        // Fade + translateY entrance for the whole content
        homeView.setAlpha(0f);
        homeView.setTranslationY(30f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(homeView, "alpha", 0f, 1f);
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(homeView, "translationY", 30f, 0f);
        fadeIn.setDuration(400);
        slideUp.setDuration(400);
        new AnimatorSet() {{ playTogether(fadeIn, slideUp); start(); }};

        // User greeting
        User user = DataManager.getInstance().getCurrentUser();
        TextView tvName = homeView.findViewById(R.id.tv_user_name);
        if (user != null) tvName.setText(user.getFullName());

        // Avatar click → profile
        homeView.findViewById(R.id.tv_avatar).setOnClickListener(v -> {
            animateScaleBounce(v);
            openActivity(UserProfileActivity.class);
        });

        // Promo card ripple
        animatePromoCards(homeView);

        // Categories
        RecyclerView rvCat = homeView.findViewById(R.id.rv_categories);
        List<Category> categories = DataManager.getInstance().getCategories();
        CategoryHomeAdapter catAdapter = new CategoryHomeAdapter(this, categories, cat -> {
            Intent intent = new Intent(this, CategoryActivity.class);
            intent.putExtra("selected_category_id", cat.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        rvCat.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvCat.setAdapter(catAdapter);

        // Food grid
        RecyclerView rvFoods = homeView.findViewById(R.id.rv_foods);
        List<FoodItem> foods = DataManager.getInstance().getFoodItems();
        foodAdapter = new FoodAdapter(this, foods, food -> {
            Intent intent = new Intent(this, FoodDetailActivity.class);
            intent.putExtra("food_id", food.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        rvFoods.setLayoutManager(new GridLayoutManager(this, 2));
        rvFoods.setAdapter(foodAdapter);

        // Search
        etSearch = homeView.findViewById(R.id.et_search);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                foodAdapter.updateData(DataManager.getInstance().searchFoodItems(s.toString()));
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // See all
        homeView.findViewById(R.id.tv_see_all).setOnClickListener(v ->
                openActivity(CategoryActivity.class));

        // Re-setup bottom nav
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) { if (currentTab != id) { currentTab = id; inflateHomeContent(); } }
            else if (id == R.id.nav_categories) { currentTab = id; openActivity(CategoryActivity.class); }
            else if (id == R.id.nav_cart)        { currentTab = id; openActivity(CartActivity.class); }
            else if (id == R.id.nav_profile)     { currentTab = id; openActivity(UserProfileActivity.class); }
            return true;
        });
    }

    /** Staggered slide-in animation for promo banners */
    private void animatePromoCards(View homeView) {
        View promo1 = homeView.findViewById(R.id.card_promo1);
        View promo2 = homeView.findViewById(R.id.card_promo2);
        if (promo1 == null || promo2 == null) return;

        for (View v : new View[]{promo1, promo2}) {
            v.setAlpha(0f);
            v.setTranslationX(80f);
        }
        ObjectAnimator p1Fade  = ObjectAnimator.ofFloat(promo1, "alpha", 0f, 1f);
        ObjectAnimator p1Slide = ObjectAnimator.ofFloat(promo1, "translationX", 80f, 0f);
        ObjectAnimator p2Fade  = ObjectAnimator.ofFloat(promo2, "alpha", 0f, 1f);
        ObjectAnimator p2Slide = ObjectAnimator.ofFloat(promo2, "translationX", 80f, 0f);

        for (ObjectAnimator a : new ObjectAnimator[]{p1Fade, p1Slide}) {
            a.setDuration(500);
            a.setStartDelay(200);
            a.setInterpolator(new OvershootInterpolator(0.8f));
        }
        for (ObjectAnimator a : new ObjectAnimator[]{p2Fade, p2Slide}) {
            a.setDuration(500);
            a.setStartDelay(350);
            a.setInterpolator(new OvershootInterpolator(0.8f));
        }
        new AnimatorSet() {{ playTogether(p1Fade, p1Slide, p2Fade, p2Slide); start(); }};
    }

    private void animateScaleBounce(View v) {
        ObjectAnimator sx = ObjectAnimator.ofFloat(v, "scaleX", 1f, 0.85f, 1f);
        ObjectAnimator sy = ObjectAnimator.ofFloat(v, "scaleY", 1f, 0.85f, 1f);
        sx.setDuration(300);
        sy.setDuration(300);
        sx.setInterpolator(new OvershootInterpolator(2f));
        sy.setInterpolator(new OvershootInterpolator(2f));
        new AnimatorSet() {{ playTogether(sx, sy); start(); }};
    }

    private void setupBottomNav() {
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) { if (currentTab != id) { currentTab = id; inflateHomeContent(); } }
            else if (id == R.id.nav_categories) { currentTab = id; openActivity(CategoryActivity.class); }
            else if (id == R.id.nav_cart)        { currentTab = id; openActivity(CartActivity.class); }
            else if (id == R.id.nav_profile)     { currentTab = id; openActivity(UserProfileActivity.class); }
            return true;
        });
    }

    private void openActivity(Class<?> cls) {
        startActivity(new Intent(this, cls));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    // ── Options Menu ─────────────────────────────────────────────────────────

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_search) {
            if (etSearch != null) etSearch.requestFocus();
        } else if (id == R.id.sort_name) {
            sortFoods("name");
        } else if (id == R.id.sort_price) {
            sortFoods("price");
        } else if (id == R.id.sort_rating) {
            sortFoods("rating");
        } else if (id == R.id.menu_refresh) {
            inflateHomeContent();
            Toast.makeText(this, "✅ Đã làm mới!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_about) {
            showAboutDialog();
        }
        return true;
    }

    private void sortFoods(String by) {
        if (foodAdapter == null) return;
        List<FoodItem> foods = DataManager.getInstance().getFoodItems();
        switch (by) {
            case "name":   foods.sort((a, b) -> a.getName().compareTo(b.getName())); break;
            case "price":  foods.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice())); break;
            case "rating": foods.sort((a, b) -> Float.compare(b.getRating(), a.getRating())); break;
        }
        foodAdapter.updateData(foods);
        Toast.makeText(this, "Đã sắp xếp", Toast.LENGTH_SHORT).show();
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Giới thiệu 🍔")
                .setMessage("Food App v1.0\n\nXây dựng bởi: Trần Trọng Mạnh\nMôn: Lập trình ứng dụng di động\nLab 4 — 2026")
                .setPositiveButton("Đóng", null)
                .show();
    }
}
