package com.kswy.property.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.kswy.property.BaseActivity;
import com.kswy.property.R;
import com.kswy.property.account.LoginActivity;
import com.kswy.property.utils.Prefer;

public class SplashActivity extends BaseActivity {

    @Override
    protected void initUI(Bundle savedInstanceState) {
        setContentView(R.layout.layout_activity_splash);
        mHandler.postDelayed(jumpToMain, 3000);
    }

    private Runnable jumpToMain = new Runnable() {
        @Override
        public void run() {
            finish();
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }
    };

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void freeMe() {

    }
}
