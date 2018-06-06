package com.kswy.property.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.kswy.property.BaseActivity;
import com.kswy.property.Constants;
import com.kswy.property.R;
import com.kswy.property.bean.User;
import com.kswy.property.main.MainActivity;
import com.kswy.property.server.WebRequest;
import com.kswy.property.utils.MyToast;
import com.kswy.property.utils.Prefer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class LoginActivity extends BaseActivity {

    private Unbinder unbinder;

    @Nullable
    @BindView(R.id.login_account)
    protected EditText mAccountEt;
    @Nullable
    @BindView(R.id.login_pwd)
    protected EditText mPwdEt;

    @Override
    protected void initUI(Bundle savedInstanceState) {
        addContentView(R.layout.layout_activity_login);
        unbinder = ButterKnife.bind(this);
        if (!TextUtils.isEmpty(Prefer.getInstance().getString(Constants.KEY_PREFER_USER, ""))) {
            mAccountEt.setText(Prefer.getInstance().getString(Constants.KEY_PREFER_USER, ""));
        }
        if (!TextUtils.isEmpty(Prefer.getInstance().getString(Constants.KEY_PREFER_PWD, ""))) {
            mPwdEt.setText(Prefer.getInstance().getString(Constants.KEY_PREFER_PWD, ""));
        }
    }

    @Optional
    @OnClick(R.id.login_login)
    protected void login() {

        final String acc = mAccountEt.getText().toString().trim();
        final String pwd = mPwdEt.getText().toString().trim();
        if (TextUtils.isEmpty(acc)) {
            MyToast.show(this, R.string.acc_empty);
            return;
        }
        if (TextUtils.isEmpty(pwd)) {
            MyToast.show(this, R.string.pwd_empty);
            return;
        }

//        if (Constants.DEBUG) {
//            Prefer.getInstance().putString(Constants.KEY_PREFER_USER, acc);
//            finish();
//            startActivity(new Intent(this, MainActivity.class));
//        }

        createDialog(R.string.logining, false);
        WebRequest.getInstance().login(acc, pwd, new Observer<JSONObject>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONObject body) {
                Log.e("loginActivity", "onNext: "+body);
                dismissDialog();
                Prefer.getInstance().putString(Constants.KEY_PREFER_USER, acc);
                Prefer.getInstance().putString(Constants.KEY_PREFER_PWD, pwd);
                User.getUser().setAccount(acc);
                User.getUser().setPassword(pwd);
                User.getUser().setToken(body.getString("token"));
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }

            @Override
            public void onError(Throwable e) {
                Log.e("loginActivity", "onError: "+e);
                dismissDialog();
                MyToast.show(LoginActivity.this, R.string.login_fail);
            }

            @Override
            public void onComplete() {
                Log.e("loginActivity", "onComplete: ");
            }
        });
        //TODO login
    }
    @Optional
    @OnClick(R.id.login_register)
    protected void register() {
        startActivityForResult(new Intent(this, RegisterActivity.class), Constants.CODE_ACTIVITY_REGISTER);
    }

    @Override
    protected void setTopBar(int leftId, int titleId, int rightId) {
        super.setTopBar(R.drawable.icon_back, titleId, rightId);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case Constants.CODE_ACTIVITY_REGISTER:
                    if (data == null) {
                        break;
                    }
                    String acc = data.getStringExtra("account");
                    if (TextUtils.isEmpty(acc)) {
                        break;
                    }

                    mAccountEt.setText(acc);
                    break;
            }
        }
    }

    @Override
    protected void freeMe() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
