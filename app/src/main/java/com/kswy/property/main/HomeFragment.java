package com.kswy.property.main;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
    private LayoutInflater mInflator;

    private static HomeFragment instance;

    private boolean isListViewIdle = true;

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
        mInflator = inflater;

        mRefreshLayout.setOnRefreshListener(onRefreshListener);
        mAdapter = new HomeAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(onScrollListener);
        return contentView;
    }

    private OnRefreshListener onRefreshListener = new OnRefreshListener() {
        @Override
        public void onRefresh(RefreshLayout refreshLayout) {
            getBuild();
        }
    };

    private AbsListView.OnScrollListener onScrollListener =
            new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                    switch (i) {
                        case SCROLL_STATE_IDLE:
                            isListViewIdle = true;
                            break;
                        default:
                            isListViewIdle = false;
                            break;
                    }
                }

                @Override
                public void onScroll(AbsListView absListView, int i, int i1, int i2) {

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

    private void onItemTitleClickListener(View view, HomeAdapter.ViewHolder holder) {
        holder.isCollapse = !holder.isCollapse;
        if (!holder.isCollapse) {
            holder.controller.refresh();
        }
        mAdapter.notifyDataSetChanged();
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
        public View getView(final int i, View view, ViewGroup viewGroup) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = mInflator.inflate(R.layout.item_home_build, null);
                holder.title = view.findViewById(R.id.home_build_name);
                FeeDetailController controller = new FeeDetailController(mActivity, view, mInfos.get(i));
                controller.fill();
                holder.controller = controller;
                holder.isCollapse = true;
                view.setTag(holder);
            }else {
                holder = (ViewHolder) view.getTag();
            }

            holder.controller.setData(mInfos.get(i));
            if (holder.isCollapse) {
                holder.controller.getContentView().setVisibility(View.GONE);
            }else {
                holder.controller.getContentView().setVisibility(View.VISIBLE);
            }

            holder.title.setText(String.format(Locale.CHINA, "%s %s %s",
                    mInfos.get(i).getBuildName(), mInfos.get(i).getInNo(), mInfos.get(i).getInName()));
            holder.title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemTitleClickListener(view, holder);
                }
            });

            return view;
        }

        private class ViewHolder {
            TextView title;
            FeeDetailController controller;
            boolean isCollapse = true;
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
