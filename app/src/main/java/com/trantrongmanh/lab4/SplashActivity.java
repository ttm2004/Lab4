package com.trantrongmanh.lab4;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Animated splash screen — shows for ~2s with a zoom+fade entrance,
 * then navigates to LoginActivity.
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        View content = findViewById(R.id.layout_splash_content);
        View version = findViewById(R.id.tv_version);

        // Logo: scale from 0 + fade in with overshoot
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(content, "scaleX", 0f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(content, "scaleY", 0f, 1f);
        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(content, "alpha", 0f, 1f);
        scaleX.setDuration(700);
        scaleY.setDuration(700);
        fadeIn.setDuration(500);
        scaleX.setInterpolator(new OvershootInterpolator(1.4f));
        scaleY.setInterpolator(new OvershootInterpolator(1.4f));

        // Version fade in
        ObjectAnimator versionFade = ObjectAnimator.ofFloat(version, "alpha", 0f, 1f);
        versionFade.setDuration(600);
        versionFade.setStartDelay(400);

        AnimatorSet set = new AnimatorSet();
        set.playTogether(scaleX, scaleY, fadeIn, versionFade);
        set.start();

        // Navigate after 2.2s
        content.postDelayed(() -> {
            // Fade out whole screen
            ObjectAnimator exitFade = ObjectAnimator.ofFloat(
                    findViewById(android.R.id.content), "alpha", 1f, 0f);
            exitFade.setDuration(350);
            exitFade.start();
            exitFade.addListener(new android.animation.AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(android.animation.Animator animation) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    finish();
                }
            });
        }, 2200);
    }
}
