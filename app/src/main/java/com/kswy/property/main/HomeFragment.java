package com.kswy.property.main;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.kswy.property.BaseFragment;
import com.kswy.property.R;
import com.kswy.property.bean.House;
import com.kswy.property.bean.Fee;
import com.kswy.property.bean.User;
import com.kswy.property.server.WebRequest;
import com.kswy.property.utils.MyToast;
import com.kswy.property.utils.ViewHolder;
import com.scwang.smartrefresh.header.MaterialHeader;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class HomeFragment extends BaseFragment {

    private final String TAG = "HomeFragment";

    @BindView(R.id.home_refresh)
    protected SmartRefreshLayout mRefreshLayout;
    @BindView(R.id.home_header)
    protected MaterialHeader mHeader;
    @BindView(R.id.home_list)
    protected ListView mListView;
    @BindView(R.id.home_empty)
    protected TextView mEmpty;

    private Unbinder unbinder;
    public ArrayList<House> mInfos;
    private HashMap<String, ArrayList<Fee>> feeMap;//费用清单key:inNo,value:Fee
    private HomeAdapter mAdapter;
    private HashMap<String, FeeDetailController> mChildController;//
    private LayoutInflater mInflator;

    private static HomeFragment instance;

    private volatile int feeRequestCount;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        instance = this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.layout_fragment_home, container, false);

        unbinder = ButterKnife.bind(this, contentView);
        getBuild();
        mChildController = new HashMap<>();
        mInflator = inflater;

        mRefreshLayout.setOnRefreshListener(onRefreshListener);
        mAdapter = new HomeAdapter();
        mListView.setAdapter(mAdapter);
//        mListView.setOnGroupClickListener(groupClickListener);
        return contentView;
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshLayout) {
            getBuild();
        }
    };

    private ExpandableListView.OnGroupClickListener groupClickListener =
            new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                    Log.e(TAG, "onGroupClick: "+expandableListView.isGroupExpanded(i));

                    return false;
                }
            };

    private ExpandableListView.OnGroupExpandListener expandListener =
            new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(final int i) {
                    Log.e(TAG, "onGroupExpand: "+i);
                    mActivity.createDialog(R.string.loading, true);
                    mChildController.get(mInfos.get(i).getInNo()).refresh();
                    mChildController.get(mInfos.get(i).getInNo()).setOnFinishListener(new FeeDetailController.FinishedFill() {
                        @Override
                        public void onFinished(View view) {
                            mActivity.dismissDialog();
                        }
                    });
                }
            };

    private ExpandableListView.OnChildClickListener onChildClickListener =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                    Log.e(TAG, "onChildClick: group = "+i);
                    return true;
                }
            };
    /**
     * 根据登录的账号获取绑定的楼房
     * */
    private void getBuild() {

        if (getFeeMap() != null) {
            getFeeMap().clear();
        }

        WebRequest.getInstance().getUserInhabitant(User.getUser().getAccount(), new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "getBuild onSubscribe: ");
            }

            @Override
            public void onNext(JSONArray infos) {
                Log.e(TAG, "getBuild onNext: "+infos);
                mRefreshLayout.finishRefresh(true);
                mInfos = (ArrayList<House>) JSON.parseArray(infos.toString(), House.class);
                if (mInfos == null || mInfos.isEmpty()) {
                    mEmpty.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        mEmpty.setText(Html.fromHtml(getString(R.string.home_empty), Html.FROM_HTML_MODE_COMPACT));
                    }else {
                        mEmpty.setText(Html.fromHtml(getString(R.string.home_empty)));
                    }
                    mEmpty.setOnClickListener(mActivity);
                    return;
                }else {
                    mEmpty.setVisibility(View.GONE);
                }

                feeRequestCount = mInfos.size();

                mChildController.clear();
                for (final House info : mInfos) {
                    FeeDetailController controller = new FeeDetailController(mActivity, mInflator.inflate(R.layout.item_home_charge, null), info);
                    controller.fill();
                    mChildController.put(info.getInNo(), controller);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getBuild onError: "+e);
                MyToast.show(mActivity, R.string.home_refresh_fail);
                mRefreshLayout.finishRefresh(false);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "getBuild onComplete: ");
            }
        });
    }

    public synchronized HashMap<String, ArrayList<Fee>> getFeeMap() {
        return feeMap;
    }

    public synchronized void setFeeMap(HashMap<String, ArrayList<Fee>> map) {
        feeMap = map;
    }

    public static HomeFragment getInstance() {
        return instance;
    }

    public void setInfos(ArrayList<House> infos) {
        this.mInfos = infos;
        mEmpty.setVisibility(mInfos == null || mInfos.isEmpty() ? View.VISIBLE : View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    private class HomeAdapter extends BaseAdapter {

        public HomeAdapter() {
            mInflator = LayoutInflater.from(mActivity);
        }

        @Override
        public int getCount() {
            return mInfos == null ? 0 : mInfos.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = mInflator.inflate(R.layout.item_home_build, null);
                FeeDetailController controller = new FeeDetailController(mActivity, view, mInfos.get(i));
                controller.fill();
            }

            ViewHolder.<LinearLayout> get(view, R.id.item_home_charge);

            return view;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
