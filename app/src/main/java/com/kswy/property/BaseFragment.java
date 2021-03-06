package com.kswy.property;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;

import com.kswy.property.main.MainActivity;

public class BaseFragment extends Fragment {

    protected MainActivity mActivity;
    protected Handler mHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
        mHandler = new Handler(Looper.getMainLooper());
    }
}
