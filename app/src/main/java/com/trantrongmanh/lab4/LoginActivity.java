package com.trantrongmanh.lab4;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.trantrongmanh.lab4.data.DataManager;
import com.trantrongmanh.lab4.model.User;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private TextView tvError;
    private View cardLogin;
    private View logoIcon;
    private View brandLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername  = findViewById(R.id.et_username);
        etPassword  = findViewById(R.id.et_password);
        btnLogin    = findViewById(R.id.btn_login);
        tvError     = findViewById(R.id.tv_error);
        cardLogin   = findViewById(R.id.card_login);
        logoIcon    = findViewById(R.id.tv_logo_icon);
        brandLayout = findViewById(R.id.layout_brand);

        playEntranceAnimations();
        setupListeners();
        startLogoPulse();
    }

    // ── Entrance ────────────────────────────────────────────────────────────

    private void playEntranceAnimations() {
        // Brand: fade + slide down
        brandLayout.setAlpha(0f);
        brandLayout.setTranslationY(-60f);
        ObjectAnimator brandFade  = ObjectAnimator.ofFloat(brandLayout, "alpha", 0f, 1f);
        ObjectAnimator brandSlide = ObjectAnimator.ofFloat(brandLayout, "translationY", -60f, 0f);
        brandFade.setDuration(600);
        brandSlide.setDuration(600);
        brandSlide.setInterpolator(new OvershootInterpolator(1.2f));

        // Card: slide up from bottom
        cardLogin.setTranslationY(300f);
        cardLogin.setAlpha(0f);
        ObjectAnimator cardSlide = ObjectAnimator.ofFloat(cardLogin, "translationY", 300f, 0f);
        ObjectAnimator cardFade  = ObjectAnimator.ofFloat(cardLogin, "alpha", 0f, 1f);
        cardSlide.setDuration(700);
        cardFade.setDuration(600);
        cardSlide.setInterpolator(new OvershootInterpolator(0.8f));
        cardSlide.setStartDelay(200);
        cardFade.setStartDelay(200);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(brandFade, brandSlide, cardSlide, cardFade);
        set.start();
    }

    private void startLogoPulse() {
        // Gentle continuous scale pulse on the logo icon
        ObjectAnimator pulseX = ObjectAnimator.ofFloat(logoIcon, "scaleX", 1f, 1.06f, 1f);
        ObjectAnimator pulseY = ObjectAnimator.ofFloat(logoIcon, "scaleY", 1f, 1.06f, 1f);
        pulseX.setDuration(2000);
        pulseY.setDuration(2000);
        pulseX.setRepeatCount(ValueAnimator.INFINITE);
        pulseY.setRepeatCount(ValueAnimator.INFINITE);
        pulseX.setInterpolator(new AccelerateDecelerateInterpolator());
        pulseY.setInterpolator(new AccelerateDecelerateInterpolator());
        AnimatorSet pulse = new AnimatorSet();
        pulse.playTogether(pulseX, pulseY);
        pulse.setStartDelay(800);
        pulse.start();
    }

    // ── Listeners ────────────────────────────────────────────────────────────

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            animateButtonPress(btnLogin);
            attemptLogin();
        });

        etPassword.setOnEditorActionListener((tv, actionId, event) -> {
            attemptLogin();
            return true;
        });

        // Focus animations on input containers
        setupInputFocusAnim(R.id.layout_username, etUsername);
        setupInputFocusAnim(R.id.layout_password, etPassword);
    }

    private void setupInputFocusAnim(int containerId, EditText field) {
        LinearLayout container = findViewById(containerId);
        field.setOnFocusChangeListener((v, hasFocus) -> {
            float targetY = hasFocus ? -4f : 0f;
            float targetElev = hasFocus ? 8f : 0f;
            ObjectAnimator translateY = ObjectAnimator.ofFloat(container, "translationY", targetY);
            ObjectAnimator elevation  = ObjectAnimator.ofFloat(container, "elevation", targetElev);
            translateY.setDuration(200);
            elevation.setDuration(200);
            new AnimatorSet().playTogether(translateY, elevation);
            translateY.start();
            elevation.start();
        });
    }

    private void animateButtonPress(View view) {
        ObjectAnimator scaleDown = ObjectAnimator.ofFloat(view, "scaleX", 1f, 0.95f);
        ObjectAnimator scaleUp   = ObjectAnimator.ofFloat(view, "scaleX", 0.95f, 1f);
        scaleDown.setDuration(80);
        scaleUp.setDuration(120);
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(scaleDown, scaleUp);
        set.start();
    }

    // ── Login logic ──────────────────────────────────────────────────────────

    private void attemptLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin");
            shakeView(cardLogin);
            return;
        }

        User user = DataManager.getInstance().login(username, password);
        if (user != null) {
            tvError.setVisibility(View.GONE);
            // Success: scale out + fade
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(cardLogin, "scaleX", 1f, 0.9f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(cardLogin, "scaleY", 1f, 0.9f);
            ObjectAnimator fade   = ObjectAnimator.ofFloat(cardLogin, "alpha", 1f, 0f);
            scaleX.setDuration(300);
            scaleY.setDuration(300);
            fade.setDuration(300);
            AnimatorSet exit = new AnimatorSet();
            exit.playTogether(scaleX, scaleY, fade);
            exit.start();
            exit.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            });
        } else {
            showError("Tên đăng nhập hoặc mật khẩu không đúng ❌");
            shakeView(cardLogin);
        }
    }

    private void showError(String msg) {
        tvError.setText(msg);
        tvError.setVisibility(View.VISIBLE);
        // Bounce in error
        tvError.setAlpha(0f);
        ObjectAnimator errorFade = ObjectAnimator.ofFloat(tvError, "alpha", 0f, 1f);
        errorFade.setDuration(300);
        errorFade.start();
    }

    /** Horizontal shake animation to indicate wrong input */
    private void shakeView(View view) {
        ObjectAnimator shake = ObjectAnimator.ofFloat(
                view, "translationX",
                0f, 18f, -18f, 14f, -14f, 8f, -8f, 0f);
        shake.setDuration(500);
        shake.setInterpolator(new BounceInterpolator());
        shake.start();
    }
}
