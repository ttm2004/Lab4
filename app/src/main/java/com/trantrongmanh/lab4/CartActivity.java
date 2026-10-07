package com.trantrongmanh.lab4;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.trantrongmanh.lab4.adapter.CartAdapter;
import com.trantrongmanh.lab4.data.DataManager;

import java.text.NumberFormat;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private CartAdapter adapter;
    private LinearLayout layoutEmpty, layoutContent;
    private TextView tvTotal, tvItemCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        layoutEmpty   = findViewById(R.id.layout_empty);
        layoutContent = findViewById(R.id.layout_cart_content);
        tvTotal       = findViewById(R.id.tv_total);
        tvItemCount   = findViewById(R.id.tv_item_count);

        RecyclerView rvCart = findViewById(R.id.rv_cart);
        adapter = new CartAdapter(this, DataManager.getInstance().getCartItems(), this::refreshSummary);
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        rvCart.setAdapter(adapter);
        rvCart.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));

        Button btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(v -> checkout());

        refreshSummary();
    }

    void refreshSummary() {
        DataManager dm = DataManager.getInstance();
        boolean isEmpty = dm.getCartItems().isEmpty();

        layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        layoutContent.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        if (!isEmpty) {
            NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            tvTotal.setText(fmt.format(dm.getCartTotal()) + " đ");
            tvItemCount.setText(dm.getCartItemCount() + " sản phẩm");
        }
        adapter.notifyDataSetChanged();
    }

    private void checkout() {
        if (DataManager.getInstance().getCartItems().isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống!", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận đặt hàng")
                .setMessage("Đặt hàng với tổng tiền " +
                        NumberFormat.getNumberInstance(new Locale("vi", "VN"))
                                .format(DataManager.getInstance().getCartTotal()) + " đ?")
                .setPositiveButton("Đặt hàng", (d, w) -> {
                    DataManager.getInstance().clearCart();
                    adapter.notifyDataSetChanged();
                    refreshSummary();
                    Toast.makeText(this, getString(R.string.order_success), Toast.LENGTH_LONG).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.cart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); return true;
        }
        if (item.getItemId() == R.id.menu_clear_cart) {
            new AlertDialog.Builder(this)
                    .setTitle("Xóa giỏ hàng")
                    .setMessage("Bạn có chắc muốn xóa tất cả sản phẩm trong giỏ không?")
                    .setPositiveButton("Xóa", (d, w) -> {
                        DataManager.getInstance().clearCart();
                        refreshSummary();
                        Toast.makeText(this, "Đã xóa giỏ hàng", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
