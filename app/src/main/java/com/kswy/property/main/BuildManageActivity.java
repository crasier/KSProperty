package com.kswy.property.main;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.support.jaxrs.FastJsonAutoDiscoverable;
import com.kswy.property.BaseActivity;
import com.kswy.property.R;
import com.kswy.property.bean.Build;
import com.kswy.property.bean.House;
import com.kswy.property.bean.Inhabitant;
import com.kswy.property.bean.User;
import com.kswy.property.server.Entity.NoBodyEntity;
import com.kswy.property.server.WebRequest;
import com.kswy.property.utils.MyToast;
import com.kswy.property.utils.ViewHolder;
import com.kswy.property.widgets.CustomDialog;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.Response;

public class BuildManageActivity extends BaseActivity {

    public static final String TAG = "BuildManageActivity";

    private Unbinder unbinder;
    private MyAdapter mAdapter;
    private LayoutInflater inflater;

    private ArrayList<House> mHousesBound;
    private CustomDialog houseSelectDialog;
    private ArrayList<Build> builds;
    private ArrayList<Inhabitant> inhabitants;
    private AlertDialog delInhabitantDialog;
    private AlertDialog addInhabitantDialog;

    private BuildAdapter buildAdapter;
    private HouseAdapter houseAdapter;

    @BindView(R.id.build_manage_list)
    protected ListView mListView;
    @BindView(R.id.build_refresh)
    protected RefreshLayout mRefresh;

    private Spinner mBuildSpinner;//楼宇列表
    private Spinner mHouseSpinner;//住户列表

    private Build tempBuild;
    private House tempHouse;

    @Override
    protected void initUI(Bundle savedInstanceState) {
        addContentView(R.layout.layout_build_manage_activity);
        inflater = LayoutInflater.from(this);

        unbinder = ButterKnife.bind(this);
        setTopBar(R.drawable.icon_back, R.string.mine_build_manage, 0);

        View addFooter = inflater.inflate(R.layout.view_build_manage_add, null);
        addFooter.setOnClickListener(this);

        mHousesBound = HomeFragment.getInstance().mInfos;
        mRefresh.setOnRefreshListener(refreshListener);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);
        mListView.addFooterView(addFooter);
    }

    private OnRefreshListener refreshListener =
            new OnRefreshListener() {
                @Override
                public void onRefresh(RefreshLayout refreshLayout) {
                    getInhabitants();
                }
            };

    private void initHouseChooser() {
        if (houseSelectDialog == null) {
            houseSelectDialog = new CustomDialog(this, R.style.MyDialogStyle);
            houseSelectDialog.setCanceledOnTouchOutside(false);

            View contentView = inflater.inflate(R.layout.layout_dialog_build_add, null);
            mBuildSpinner = contentView.findViewById(R.id.choose_build);
            mHouseSpinner = contentView.findViewById(R.id.choose_house);
            mBuildSpinner.setPromptId(R.string.mine_build_choose);
            mHouseSpinner.setPromptId(R.string.mine_house_choose);
            contentView.findViewById(R.id.sure).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addHouse(true);
                }
            });
            contentView.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    houseSelectDialog.dismiss();
                }
            });
            houseSelectDialog.setContentView(contentView);
            houseSelectDialog.setTitle(R.string.mine_build_choose);

            buildAdapter = new BuildAdapter();
            houseAdapter = new HouseAdapter();
            mBuildSpinner.setAdapter(buildAdapter);
            mHouseSpinner.setAdapter(houseAdapter);

            mBuildSpinner.setOnItemSelectedListener(itemSelectedListener);
            mHouseSpinner.setOnItemSelectedListener(itemSelectedListener);
        }

        houseSelectDialog.show();
    }

    private void dismissHouseSelectDialog() {
        if (houseSelectDialog != null && houseSelectDialog.isShowing()) {
            houseSelectDialog.dismiss();
        }
    }

    private void getInhabitants() {
        WebRequest.getInstance().getUserInhabitant(User.getUser().getAccount(), new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "getInhabitants onSubscribe: ");
            }

            @Override
            public void onNext(JSONArray infos) {
                Log.e(TAG, "getInhabitants onNext: "+infos);
                mHousesBound = (ArrayList<House>) JSON.parseArray(infos.toString(), House.class);
                HomeFragment.getInstance().setInfos(mHousesBound);
                mAdapter.notifyDataSetChanged();
                mRefresh.finishRefresh(true);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "getInhabitants onError: "+e);
                MyToast.show(BuildManageActivity.this, R.string.home_refresh_fail);
                mRefresh.finishRefresh(false);
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "getInhabitants onComplete: ");
            }
        });
    }

    private void getBuild() {

        createDialog(R.string.loading, false);
        initHouseChooser();
        WebRequest.getInstance().getBuild(new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONArray arr) {
                Log.e(TAG, "onNext: "+arr);
                dismissDialog();
                builds = (ArrayList<Build>) JSON.parseArray(arr.toString(), Build.class);
                Log.e(TAG, "onNext: builds = "+builds);
                buildAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: "+e);
                dismissDialog();
                dismissHouseSelectDialog();
                MyToast.show(BuildManageActivity.this, R.string.build_get_failed);
            }

            @Override
            public void onComplete() {
                dismissDialog();
            }
        });
    }

    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Log.e(TAG, "onItemSelected: adapterView.getID "+adapterView.getId()+"; position = "+i);
            Log.e(TAG, "onItemSelected: chooseBuild.getID "+R.id.choose_build);
            Log.e(TAG, "onItemSelected: chooseHouse.getID "+R.id.choose_house);
            switch (adapterView.getId()) {
                case R.id.choose_build:
                    tempBuild = builds.get(i);
                    getHouse(tempBuild.getId());
                    Log.e(TAG, "onItemSelected: tempBuild = "+tempBuild);
                    break;
                case R.id.choose_house:
                    tempHouse = inhabitants.get(0).getInhats().get(i);
                    Log.e(TAG, "onItemSelected: tempHouse = "+tempHouse);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    /**
     * 选择要添加的房屋
     * */
    private void getHouse(int id) {
        createDialog(R.string.loading, false);
        WebRequest.getInstance().getInhabitantByBuild(id, new Observer<JSONArray>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(JSONArray arr) {
                Log.e(TAG, "onNext: arr = "+arr);
                dismissDialog();
                inhabitants = (ArrayList<Inhabitant>) JSON.parseArray(arr.toString(), Inhabitant.class);
                houseAdapter.notifyDataSetChanged();
                if (inhabitants.get(0).getInhats() != null && inhabitants.get(0).getInhats().size() > 0) {
                    mHouseSpinner.setSelection(0);
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: "+e);
                dismissDialog();
                dismissHouseSelectDialog();
                MyToast.show(BuildManageActivity.this, R.string.house_get_failed);
            }

            @Override
            public void onComplete() {
                dismissDialog();
            }
        });
    }

    /**
     * 增加绑定一个房屋
     */
    private void addHouse(boolean showTip) {
        Log.e(TAG, "addHouse: tempHouse == null ? "+(tempHouse == null)+"; tempHouse = "+tempHouse);
        if (isBound(tempHouse)) {
            MyToast.show(this, getString(R.string.house_has_bound, getString(R.string.info_inhabitant,
                    tempHouse.getBuildName(), tempHouse.getUnit(), tempHouse.getFloor(), tempHouse.getInName())));
            return;
        }

        if (showTip) {
            if (addInhabitantDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.tips_warmly)
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addHouse(false);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                addInhabitantDialog = builder.create();
                addInhabitantDialog.setCancelable(true);
                addInhabitantDialog.setCanceledOnTouchOutside(false);
            }

            addInhabitantDialog.setMessage(getString(R.string.house_add_tip,
                    getString(R.string.info_inhabitant,
                            tempHouse.getBuildName(), tempHouse.getUnit(), tempHouse.getFloor(), tempHouse.getInName())));
            addInhabitantDialog.show();
            return;
        }

        String inNos[];
        if (mHousesBound == null) {
            inNos = new String[]{tempHouse.getInNo()};
        }else {
            inNos = new String[mHousesBound.size() + 1];
            for (int i = 0; i < inNos.length; i++) {
                if (i < mHousesBound.size()) {
                    inNos[i] = mHousesBound.get(i).getInNo();
                    continue;
                }
                inNos[i] = tempHouse.getInNo();
            }
        }
        saveInhabitant(inNos);
    }

    private boolean isBound(House house) {
        Log.e(TAG, "isBound: null ? "+(mHousesBound == null));
        if (mHousesBound == null) {
            return false;
        }
        for (House h : mHousesBound) {
            Log.e(TAG, "isBound: h.inNo = "+h.getInNo()+"; house.inNo = "+house.getInNo());
            if (h.getInNo() != null && h.getInNo().equals(house.getInNo())) {
                return true;
            }
        }
        return false;
    }

    private volatile int deleteIndex = 0;
    /**
     * 点击删除房屋按钮
     * */
    private void onDeleteClick(final View view, final int position, boolean showTip) {

        deleteIndex = position;

        ArrayList<House> arr = new ArrayList<>(mHousesBound);
        Log.e(TAG, "onDeleteClick: position = "+position);
        if (position < 0 || position >= arr.size()) return;

        House house = mHousesBound.get(position);
        if (showTip) {
            if (delInhabitantDialog == null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.tips_warmly)
                        .setMessage(getString(R.string.house_delete_tip,
                                getString(R.string.info_inhabitant,
                                        house.getBuildName(), house.getUnit(), house.getFloor(), house.getInName())))
                        .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onDeleteClick(view, deleteIndex, false);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                delInhabitantDialog = builder.create();
                delInhabitantDialog.setCancelable(true);
                delInhabitantDialog.setCanceledOnTouchOutside(false);
            }

            delInhabitantDialog.show();
            return;
        }

        arr.remove(position);
        String inNos[] = new String[arr.size()];
        for (int i = 0; i < arr.size(); i++) {
            inNos[i] = arr.get(i).getInNo();
        }

        saveInhabitant(inNos);
    }

    private void saveInhabitant(String inNos[]) {
        createDialog(R.string.loading, false);
        WebRequest.getInstance().saveUserInhabitant(
                User.getUser().getAccount(), inNos, new Observer<Response<Void>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<Void> entity) {
                        Log.e(TAG, "onNext: "+entity);
                        dismissDialog();
                        dismissHouseSelectDialog();
                        MyToast.show(BuildManageActivity.this, R.string.operation_success);
                        //重新获取绑定的房屋信息
                        getInhabitants();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e);
                        dismissDialog();
                        MyToast.show(BuildManageActivity.this, R.string.operation_failed);
                    }

                    @Override
                    public void onComplete() {
                        dismissDialog();
                    }
                });
    }

    @Override
    protected void freeMe() {
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.base_top_left:
                finish();
                break;
            case R.id.build_manage_add:
                getBuild();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private class MyAdapter extends BaseAdapter {

        public MyAdapter() {
        }

        @Override
        public int getCount() {
            return mHousesBound == null ? 0 : mHousesBound.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.item_home_build, null);
            }

            ViewHolder.<TextView> get(view, R.id.home_build_name)
                    .setText(String.format(Locale.CHINA, "%s %s %s",
                            mHousesBound.get(i).getBuildName(), mHousesBound.get(i).getInNo(), mHousesBound.get(i).getInName()));
            setImg(ViewHolder. <ImageView> get(view, R.id.home_build_more), i);

            return view;
        }

        private void setImg(final ImageView view, final int position) {
            view.setImageResource(R.drawable.icon_delete);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDeleteClick(view, position, true);
                }
            });
        }
    }

    private class BuildAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return builds == null ? 0 : builds.size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.item_home_build, null);
            }

            ViewHolder.<TextView> get(view, R.id.home_build_name)
                    .setText(builds.get(i).getBuildName()+"-"+builds.get(i).getBuildId());
            ViewHolder.<ImageView> get(view, R.id.home_build_more)
                    .setVisibility(View.GONE);
            return view;
        }
    }

    private class HouseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (inhabitants == null
                    || inhabitants.isEmpty()
                    || inhabitants.get(0) == null
                    || inhabitants.get(0).getInhats() == null
                    || inhabitants.get(0).getInhats().isEmpty()) {
                return 0;
            }
            return inhabitants.get(0).getInhats().size();
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.item_home_build, null);
            }

            House house = inhabitants.get(0).getInhats().get(i);
            ViewHolder.<TextView> get(view, R.id.home_build_name)
                    .setText(getString(R.string.info_inhabitant,
                            house.getBuildName(), house.getUnit(), house.getFloor(), house.getInName()));
            ViewHolder.<ImageView> get(view, R.id.home_build_more)
                    .setVisibility(View.GONE);
            return view;
        }
    }
}
