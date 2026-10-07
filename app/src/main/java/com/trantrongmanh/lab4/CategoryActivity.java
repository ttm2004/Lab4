package com.trantrongmanh.lab4;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.trantrongmanh.lab4.adapter.CategoryAdapter;
import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.Category;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView rvCategories;
    private CategoryAdapter adapter;
    private List<Category> categories;
    // Position of long-pressed item for context menu
    private int contextMenuPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Danh mục món ăn");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        categories = DataManager.getInstance().getCategories();

        rvCategories = findViewById(R.id.rv_categories);
        adapter = new CategoryAdapter(this, categories,
                // Click → open food list for that category
                cat -> {
                    Intent intent = new Intent(this, FoodListActivity.class);
                    intent.putExtra("category_id", cat.getId());
                    intent.putExtra("category_name", cat.getName());
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                },
                // Long-click → store position for context menu
                (cat, position) -> {
                    contextMenuPosition = position;
                    return false; // let the system show the context menu
                });
        rvCategories.setLayoutManager(new LinearLayoutManager(this));
        rvCategories.setAdapter(adapter);

        // Register RecyclerView for context menu
        registerForContextMenu(rvCategories);

        // FAB → add new category
        FloatingActionButton fab = findViewById(R.id.fab_add);
        fab.setOnClickListener(v -> showCategoryDialog(null, -1));
        fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce_in));

        // Animate list
        rvCategories.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    // ── Context Menu ─────────────────────────────────────────────────────────

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (contextMenuPosition >= 0) {
            Category cat = categories.get(contextMenuPosition);
            menu.setHeaderTitle(cat.getName());
            getMenuInflater().inflate(R.menu.context_menu_category, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (contextMenuPosition < 0) return super.onContextItemSelected(item);
        Category cat = categories.get(contextMenuPosition);
        int id = item.getItemId();
        if (id == R.id.ctx_view) {
            Intent intent = new Intent(this, FoodListActivity.class);
            intent.putExtra("category_id", cat.getId());
            intent.putExtra("category_name", cat.getName());
            startActivity(intent);
        } else if (id == R.id.ctx_edit) {
            showCategoryDialog(cat, contextMenuPosition);
        } else if (id == R.id.ctx_delete) {
            confirmDelete(cat, contextMenuPosition);
        }
        return true;
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
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.sort_name) {
            categories.sort((a, b) -> a.getName().compareTo(b.getName()));
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Sắp xếp theo tên", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.menu_refresh) {
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Đã làm mới!", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // ── CRUD Dialogs ─────────────────────────────────────────────────────────

    private void showCategoryDialog(Category existing, int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_category, null);
        TextView tvTitle     = dialogView.findViewById(R.id.tv_dialog_title);
        EditText etName      = dialogView.findViewById(R.id.et_name);
        EditText etDesc      = dialogView.findViewById(R.id.et_description);
        Button   btnCancel   = dialogView.findViewById(R.id.btn_cancel);
        Button   btnSave     = dialogView.findViewById(R.id.btn_save);

        boolean isEdit = existing != null;
        tvTitle.setText(isEdit ? "Sửa danh mục" : "Thêm danh mục");
        if (isEdit) {
            etName.setText(existing.getName());
            etDesc.setText(existing.getDescription());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        dialog.show();

        dialogView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up));

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Tên không được để trống");
                return;
            }
            if (isEdit) {
                existing.setName(name);
                existing.setDescription(desc);
                DataManager.getInstance().updateCategory(existing);
                adapter.notifyItemChanged(position);
                Toast.makeText(this, "Đã cập nhật: " + name, Toast.LENGTH_SHORT).show();
            } else {
                Category newCat = new Category(0, name, desc, R.drawable.ic_burger);
                DataManager.getInstance().addCategory(newCat);
                adapter.notifyItemInserted(categories.size() - 1);
                rvCategories.scrollToPosition(categories.size() - 1);
                Toast.makeText(this, "Đã thêm: " + name, Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });
    }

    private void confirmDelete(Category cat, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa danh mục")
                .setMessage("Bạn có chắc muốn xóa \"" + cat.getName() + "\" không?\nTất cả món ăn trong danh mục này cũng sẽ bị xóa.")
                .setPositiveButton("Xóa", (d, w) -> {
                    DataManager.getInstance().deleteCategory(cat.getId());
                    adapter.notifyItemRemoved(position);
                    Toast.makeText(this, "Đã xóa: " + cat.getName(), Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    // Called by adapter to set context menu position
    public void setContextMenuPosition(int pos) {
        this.contextMenuPosition = pos;
    }
}
