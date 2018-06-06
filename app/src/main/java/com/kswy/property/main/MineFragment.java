package com.kswy.property.main;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kswy.property.BaseFragment;
import com.kswy.property.R;
import com.kswy.property.account.LoginActivity;
import com.kswy.property.bean.User;
import com.kswy.property.utils.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MineFragment extends BaseFragment {

    @BindView(R.id.mine_version)
    protected TextView mVersion;
    @BindView(R.id.mine_build_manage)
    protected TextView mManage;
    @BindView(R.id.mine_build_account)
    protected TextView mAccount;
    @BindView(R.id.mine_logout)
    protected Button mLogout;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.layout_fragment_mine, container, false);

        unbinder = ButterKnife.bind(this, contentView);

        mVersion.setText(getString(R.string.version, Tools.getVersionName(mActivity)));
        mAccount.setText(getString(R.string.mine_build_account, User.getUser().getAccount()));

        mManage.setOnClickListener(onClickListener);
        mLogout.setOnClickListener(onClickListener);

        return contentView;
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.mine_build_manage:
                    startActivity(new Intent(mActivity, BuildManageActivity.class));
                    break;
                case R.id.mine_logout:
                    startActivity(new Intent(mActivity, LoginActivity.class));
                    mActivity.finish();
                    break;
            }
        }
    };

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
