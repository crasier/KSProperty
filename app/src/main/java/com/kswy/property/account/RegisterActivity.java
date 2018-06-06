package com.kswy.property.account;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.kswy.property.BaseActivity;
import com.kswy.property.R;
import com.kswy.property.server.WebRequest;
import com.kswy.property.utils.MyToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class RegisterActivity extends BaseActivity {

    private static final String TAG = "RegisterActivity";

    private Unbinder unbinder;

    @BindView(R.id.register_acc)
    protected EditText mAccEt;
    @BindView(R.id.register_nick)
    protected EditText mNickEt;
    @BindView(R.id.register_pwd)
    protected EditText mPwdEt;
    @BindView(R.id.register_confirm)
    protected EditText mConfirmEt;

    @Override
    protected void initUI(Bundle savedInstanceState) {
        addContentView(R.layout.layout_activity_register);
        unbinder = ButterKnife.bind(this);
        setTopBar(R.drawable.icon_back, R.string.register, 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.base_top_left:
                onBackPressed();
                break;
        }
    }

    @OnClick(R.id.register_register)
    protected void register() {
        final String acc = mAccEt.getText().toString().trim();
        String pwd = mPwdEt.getText().toString().trim();
        String conf = mConfirmEt.getText().toString().trim();
        String nick = mNickEt.getText().toString().trim();
        if (TextUtils.isEmpty(acc)) {
            MyToast.show(this, R.string.acc_empty);
            return;
        }

        if (TextUtils.isEmpty(pwd)) {
            MyToast.show(this, R.string.pwd_empty);
            return;
        }

        if (!conf.equals(pwd)) {
            MyToast.show(this, R.string.confirm_fail);
            return;
        }

        createDialog(R.string.committing, false);
        WebRequest.getInstance().addUser(
                acc,
                pwd,
                TextUtils.isEmpty(nick) ? acc : nick,
                new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> response) {
                        Log.e(TAG, "onNext: "+response);
                        dismissDialog();
                        MyToast.show(RegisterActivity.this, R.string.register_success);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("account", acc);
                        setResult(RESULT_OK, resultIntent);
                        mHandler.postDelayed(exitRunnable, 2000);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e);
                        dismissDialog();
                        MyToast.show(RegisterActivity.this, R.string.operation_failed);
                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });

        //TODO register
    }

    private Runnable exitRunnable = new Runnable() {
        @Override
        public void run() {
            finish();
        }
    };

    @Override
    public void onBackPressed() {
        mHandler.removeCallbacks(exitRunnable);
        finish();
    }

    @Override
    protected void freeMe() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
