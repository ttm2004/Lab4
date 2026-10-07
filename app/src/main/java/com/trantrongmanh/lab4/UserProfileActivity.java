package com.trantrongmanh.lab4;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.cardview.widget.CardView;

import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    private User user;
    private TextView tvFullName, tvUsername, tvEmail, tvPhone, tvAddress;
    private CircleImageView ivAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        user = DataManager.getInstance().getCurrentUser();
        if (user == null) {
            // Not logged in, go back to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        bindViews();
        populateUser();
        setupButtons();
    }

    private void bindViews() {
        ivAvatar   = findViewById(R.id.iv_avatar);
        tvFullName = findViewById(R.id.tv_full_name);
        tvUsername = findViewById(R.id.tv_username);
        tvEmail    = findViewById(R.id.tv_email);
        tvPhone    = findViewById(R.id.tv_phone);
        tvAddress  = findViewById(R.id.tv_address);

        // Animate info card
        CardView cardInfo = findViewById(R.id.card_info);
        cardInfo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.slide_in_up));
    }

    private void populateUser() {
        ivAvatar.setImageResource(user.getAvatarResId());
        tvFullName.setText(user.getFullName());
        tvUsername.setText("@" + user.getUsername());
        tvEmail.setText(user.getEmail());
        tvPhone.setText(user.getPhone());
        tvAddress.setText(user.getAddress());
    }

    private void setupButtons() {
        Button btnEdit = findViewById(R.id.btn_edit_profile);
        btnEdit.setOnClickListener(v -> showEditDialog());

        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void showEditDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText etFullName = dialogView.findViewById(R.id.et_full_name);
        EditText etEmail    = dialogView.findViewById(R.id.et_email);
        EditText etPhone    = dialogView.findViewById(R.id.et_phone);
        EditText etAddress  = dialogView.findViewById(R.id.et_address);
        Button btnCancel    = dialogView.findViewById(R.id.btn_cancel);
        Button btnSave      = dialogView.findViewById(R.id.btn_save);

        // Pre-fill
        etFullName.setText(user.getFullName());
        etEmail.setText(user.getEmail());
        etPhone.setText(user.getPhone());
        etAddress.setText(user.getAddress());

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
            String fullName = etFullName.getText().toString().trim();
            if (fullName.isEmpty()) {
                etFullName.setError("Họ tên không được để trống");
                return;
            }
            user.setFullName(fullName);
            user.setEmail(etEmail.getText().toString().trim());
            user.setPhone(etPhone.getText().toString().trim());
            user.setAddress(etAddress.getText().toString().trim());
            DataManager.getInstance().setCurrentUser(user);
            populateUser();
            dialog.dismiss();
            Toast.makeText(this, "✅ Đã cập nhật thông tin", Toast.LENGTH_SHORT).show();
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (d, w) -> {
                    DataManager.getInstance().logout();
                    DataManager.getInstance().clearCart();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
