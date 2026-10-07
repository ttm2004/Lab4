package com.trantrongmanh.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trantrongmanh.lab4.adapter.FoodAdapter;
import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.FoodItem;

import java.util.List;

public class FoodListActivity extends AppCompatActivity {

    private RecyclerView rvFoods;
    private FoodAdapter adapter;
    private List<FoodItem> foods;
    private int categoryId;
    private int contextMenuPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        categoryId = getIntent().getIntExtra("category_id", -1);
        String catName = getIntent().getStringExtra("category_name");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(catName != null ? catName : "Món ăn");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        foods = (categoryId >= 0)
                ? DataManager.getInstance().getFoodItemsByCategory(categoryId)
                : DataManager.getInstance().getFoodItems();

        rvFoods = findViewById(R.id.rv_foods);
        adapter = new FoodAdapter(this, foods, food -> {
            Intent intent = new Intent(this, FoodDetailActivity.class);
            intent.putExtra("food_id", food.getId());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        rvFoods.setLayoutManager(new GridLayoutManager(this, 2));
        rvFoods.setAdapter(adapter);
        rvFoods.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        registerForContextMenu(rvFoods);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo info) {
        super.onCreateContextMenu(menu, v, info);
        if (contextMenuPosition >= 0) {
            FoodItem food = foods.get(contextMenuPosition);
            menu.setHeaderTitle(food.getName());
            getMenuInflater().inflate(R.menu.context_menu_food, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (contextMenuPosition < 0) return super.onContextItemSelected(item);
        FoodItem food = foods.get(contextMenuPosition);
        int id = item.getItemId();
        if (id == R.id.ctx_view) {
            Intent intent = new Intent(this, FoodDetailActivity.class);
            intent.putExtra("food_id", food.getId());
            startActivity(intent);
        } else if (id == R.id.ctx_add_cart) {
            DataManager.getInstance().addToCart(food);
            Toast.makeText(this, "Đã thêm \"" + food.getName() + "\" vào giỏ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.ctx_favorite) {
            food.setFavorite(!food.isFavorite());
            String msg = food.isFavorite() ? "Đã thêm vào yêu thích ❤️" : "Đã bỏ yêu thích";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            adapter.notifyItemChanged(contextMenuPosition);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) { onBackPressed(); return true; }
        if (id == R.id.sort_name) {
            foods.sort((a, b) -> a.getName().compareTo(b.getName()));
            adapter.notifyDataSetChanged();
        } else if (id == R.id.sort_price) {
            foods.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            adapter.notifyDataSetChanged();
        } else if (id == R.id.sort_rating) {
            foods.sort((a, b) -> Float.compare(b.getRating(), a.getRating()));
            adapter.notifyDataSetChanged();
        }
        return super.onOptionsItemSelected(item);
    }

    public void setContextMenuPosition(int pos) { this.contextMenuPosition = pos; }
}
